#!/usr/bin/env bash
# ============================================================
#  verify-cdc.sh
#  Assertive End-to-End CDC validation using Docker containers:
#    1. Insert in Oracle & verify in PostgreSQL
#    2. Update in Oracle & verify in PostgreSQL
#    3. Delete in Oracle & verify removal in PostgreSQL
# ============================================================
set -euo pipefail

TIMEOUT_SECONDS="${TIMEOUT_SECONDS:-15}"

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "  CDC End-to-End Verification (Oracle -> Kafka -> PG)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

wait_for_condition() {
  local query="$1"
  local expected="$2"
  local elapsed=0
  while [ "$elapsed" -lt "$TIMEOUT_SECONDS" ]; do
    local actual
    actual=$(docker exec ite-postgres psql -U iteuser -d db_customer -t -A -c "$query" 2>/dev/null | tr -d '[:space:]')
    if [ "$actual" = "$expected" ]; then
      return 0
    fi
    sleep 1
    elapsed=$((elapsed + 1))
  done
  return 1
}

# ── 1. INSERT into Oracle ──────────────────────────────────
echo ""
echo "▶  [1/3] INSERT – creating customer in Oracle ..."
docker exec istad-oracle bash -c "sqlplus -s appuser/apppass@localhost:1521/XEPDB1 << 'EOF'
SET FEEDBACK OFF HEADING OFF
DELETE FROM customers WHERE email = 'cdc.testuser@example.com';
INSERT INTO customers (first_name, last_name, email, phone, status)
VALUES ('CDC', 'TestUser', 'cdc.testuser@example.com', '0123456789', 'ACTIVE');
COMMIT;
SELECT 'Oracle INSERT done: email=' || email FROM customers WHERE email = 'cdc.testuser@example.com';
EXIT;
EOF
"

echo "⏳  Waiting for CDC replication to PostgreSQL ..."
if wait_for_condition "SELECT status FROM cdc_customers WHERE email = 'cdc.testuser@example.com';" "ACTIVE"; then
  echo "✅  SUCCESS: INSERT replicated to PostgreSQL!"
  docker exec ite-postgres psql -U iteuser -d db_customer -c \
    "SELECT id, first_name, last_name, email, phone, status FROM cdc_customers WHERE email = 'cdc.testuser@example.com';"
else
  echo "❌  FAILED: INSERT not replicated to PostgreSQL within timeout."
  exit 1
fi

# ── 2. UPDATE in Oracle ────────────────────────────────────
echo ""
echo "▶  [2/3] UPDATE – changing customer status in Oracle ..."
docker exec istad-oracle bash -c "sqlplus -s appuser/apppass@localhost:1521/XEPDB1 << 'EOF'
SET FEEDBACK OFF HEADING OFF
UPDATE customers SET status = 'VERIFIED_GOLD', phone = '0987654321', updated_at = SYSTIMESTAMP
WHERE email = 'cdc.testuser@example.com';
COMMIT;
SELECT 'Oracle UPDATE done: status=' || status FROM customers WHERE email = 'cdc.testuser@example.com';
EXIT;
EOF
"

echo "⏳  Waiting for CDC update to propagate ..."
if wait_for_condition "SELECT status FROM cdc_customers WHERE email = 'cdc.testuser@example.com';" "VERIFIED_GOLD"; then
  echo "✅  SUCCESS: UPDATE replicated to PostgreSQL!"
  docker exec ite-postgres psql -U iteuser -d db_customer -c \
    "SELECT id, first_name, last_name, email, phone, status, updated_at FROM cdc_customers WHERE email = 'cdc.testuser@example.com';"
else
  echo "❌  FAILED: UPDATE not replicated to PostgreSQL within timeout."
  exit 1
fi

# ── 3. DELETE from Oracle and verify in PostgreSQL ─────────
echo ""
echo "▶  [3/3] DELETE – removing customer in Oracle ..."
docker exec istad-oracle bash -c "sqlplus -s appuser/apppass@localhost:1521/XEPDB1 << 'EOF'
SET FEEDBACK OFF HEADING OFF
DELETE FROM customers WHERE email = 'cdc.testuser@example.com';
COMMIT;
SELECT 'Oracle DELETE done' FROM DUAL;
EXIT;
EOF
"

echo "⏳  Waiting for CDC delete to propagate ..."
if wait_for_condition "SELECT COUNT(*) FROM cdc_customers WHERE email = 'cdc.testuser@example.com';" "0"; then
  echo "✅  SUCCESS: DELETE replicated to PostgreSQL (row count is 0)!"
  docker exec ite-postgres psql -U iteuser -d db_customer -c \
    "SELECT COUNT(*) AS row_count FROM cdc_customers WHERE email = 'cdc.testuser@example.com';"
else
  echo "❌  FAILED: DELETE not replicated to PostgreSQL within timeout."
  exit 1
fi

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "  ✅  CDC Verification Complete — All operations passed!"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
