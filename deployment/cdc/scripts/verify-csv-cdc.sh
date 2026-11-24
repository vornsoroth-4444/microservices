#!/usr/bin/env bash
# ============================================================
#  verify-csv-cdc.sh
#  End-to-end CDC verification for CSV -> PostgreSQL pipeline
# ============================================================
set -euo pipefail

SRC_DB="db_csv_source"
DST_DB="db_csv_dest"
TIMEOUT=60
POLL_INTERVAL=2
PASS=0
FAIL=0

log_step() { echo -e "\n\033[1;36m==> $1\033[0m"; }
log_ok()   { echo -e "    \033[1;32m[PASS]\033[0m $1"; PASS=$((PASS + 1)); }
log_fail() { echo -e "    \033[1;31m[FAIL]\033[0m $1"; FAIL=$((FAIL + 1)); }

invoke_pg() {
    local db="$1"
    local sql="$2"
    docker exec ite-postgres psql -U iteuser -d "$db" -t -A -c "$sql" 2>/dev/null | tr -d '\r'
}

wait_for_pg() {
    local db="$1"
    local sql="$2"
    local expected="$3"
    local label="$4"
    local deadline=$((SECONDS + TIMEOUT))

    while [ $SECONDS -lt $deadline ]; do
        local val
        val=$(invoke_pg "$db" "$sql")
        if [ "$val" = "$expected" ]; then
            log_ok "$label (matched '$val')"
            return 0
        fi
        sleep "$POLL_INTERVAL"
    done
    local actual
    actual=$(invoke_pg "$db" "$sql")
    log_fail "$label — expected '$expected', got '$actual' after ${TIMEOUT}s"
    return 1
}

echo -e "\033[1;35m============================================================\033[0m"
echo -e "\033[1;35m CSV -> PostgreSQL CDC Verification\033[0m"
echo -e "\033[1;35m Source: $SRC_DB  |  Dest: $DST_DB\033[0m"
echo -e "\033[1;35m============================================================\033[0m"

log_step "0. Baseline counts"
src_c=$(invoke_pg "$SRC_DB" "SELECT COUNT(*) FROM csv_import.customers")
dst_c=$(invoke_pg "$DST_DB" "SELECT COUNT(*) FROM csv_cdc.customers")
src_p=$(invoke_pg "$SRC_DB" "SELECT COUNT(*) FROM csv_import.products")
dst_p=$(invoke_pg "$DST_DB" "SELECT COUNT(*) FROM csv_cdc.products")
src_o=$(invoke_pg "$SRC_DB" "SELECT COUNT(*) FROM csv_import.orders")
dst_o=$(invoke_pg "$DST_DB" "SELECT COUNT(*) FROM csv_cdc.orders")

echo "    Source: customers=$src_c products=$src_p orders=$src_o"
echo "    Dest:   customers=$dst_c products=$dst_p orders=$dst_o"

[ "$src_c" = "$dst_c" ] && log_ok "customers match ($src_c)" || log_fail "customers mismatch src=$src_c dst=$dst_c"
[ "$src_p" = "$dst_p" ] && log_ok "products match ($src_p)"   || log_fail "products mismatch src=$src_p dst=$dst_p"
[ "$src_o" = "$dst_o" ] && log_ok "orders match ($src_o)"     || log_fail "orders mismatch src=$src_o dst=$dst_o"

log_step "1. INSERT - new customer CDC test"
TEST_ID=9901
TEST_EMAIL="cdc.csv.verify@example.com"
invoke_pg "$SRC_DB" "DELETE FROM csv_import.customers WHERE id=$TEST_ID;" >/dev/null 2>&1 || true
invoke_pg "$DST_DB" "DELETE FROM csv_cdc.customers WHERE id=$TEST_ID;" >/dev/null 2>&1 || true
sleep 1

invoke_pg "$SRC_DB" "INSERT INTO csv_import.customers(id,first_name,last_name,email,phone,city,country,status) VALUES ($TEST_ID,'CDC','TestCSV','$TEST_EMAIL','0999888777','Phnom Penh','Cambodia','ACTIVE');" >/dev/null
wait_for_pg "$DST_DB" "SELECT COUNT(*) FROM csv_cdc.customers WHERE email='$TEST_EMAIL'" "1" "INSERT replicated to dest" || true

log_step "2. UPDATE - modify customer city"
invoke_pg "$SRC_DB" "UPDATE csv_import.customers SET city='Siem Reap', updated_at=NOW() WHERE id=$TEST_ID;" >/dev/null
wait_for_pg "$DST_DB" "SELECT city FROM csv_cdc.customers WHERE id=$TEST_ID" "Siem Reap" "UPDATE replicated to dest" || true

log_step "3. DELETE - remove test customer"
invoke_pg "$SRC_DB" "DELETE FROM csv_import.customers WHERE id=$TEST_ID;" >/dev/null
wait_for_pg "$DST_DB" "SELECT COUNT(*) FROM csv_cdc.customers WHERE id=$TEST_ID" "0" "DELETE replicated to dest" || true

log_step "4. Kafka topics"
topics=$(docker exec istad-kafka-1 kafka-topics --bootstrap-server istad-kafka-1:9090 --list 2>/dev/null | grep "pg-csv-cdc" || true)
if [ -n "$topics" ]; then
    log_ok "Kafka topics found"
else
    log_fail "No pg-csv-cdc topics found"
fi

echo -e "\n\033[1;35m============================================================\033[0m"
if [ "$FAIL" -eq 0 ]; then
    echo -e " \033[1;32mALL TESTS PASSED ($PASS/$((PASS + FAIL))) - CSV CDC Pipeline Working!\033[0m"
else
    echo -e " \033[1;31mRESULTS: $PASS passed, $FAIL FAILED\033[0m"
fi
echo -e "\033[1;35m============================================================\033[0m"
