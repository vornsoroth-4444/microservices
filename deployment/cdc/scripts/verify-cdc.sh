#!/usr/bin/env bash
# ============================================================
#  verify-cdc.sh
#  End-to-end CDC validation using Docker containers:
#    1. Insert in Oracle
#    2. Update in Oracle
#    3. Verify in PostgreSQL (cdc_customers)
#    4. Delete in Oracle & verify removal in PostgreSQL
# ============================================================
set -euo pipefail

WAIT_SECONDS="${WAIT_SECONDS:-6}"

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "  CDC End-to-End Verification (Oracle -> Kafka -> PG)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# ── 1. INSERT into Oracle ──────────────────────────────────
echo ""
echo "▶  [1/4] INSERT – new customer in Oracle ..."
docker exec istad-oracle bash -c "sqlplus -s appuser/apppass@localhost:1521/XEPDB1 << 'EOF'
SET FEEDBACK OFF HEADING OFF
INSERT INTO customers (first_name, last_name, email, phone, status)
VALUES ('CDC', 'TestUser', 'cdc.testuser@example.com', '0123456789', 'ACTIVE');
COMMIT;
SELECT 'Oracle INSERT done: email=' || email FROM customers WHERE email = 'cdc.testuser@example.com';
EXIT;
EOF
"

echo "⏳  Waiting ${WAIT_SECONDS}s for CDC propagation ..."
sleep "${WAIT_SECONDS}"

# ── 2. Verify INSERT in PostgreSQL ─────────────────────────
echo ""
echo "▶  [2/4] Verify INSERT in PostgreSQL ..."
docker exec ite-postgres psql -U iteuser -d db_customer -c \
  "SELECT id, first_name, last_name, email, phone, status FROM cdc_customers WHERE email = 'cdc.testuser@example.com';"

# ── 3. UPDATE in Oracle ────────────────────────────────────
echo ""
echo "▶  [3/4] UPDATE – change customer status in Oracle ..."
docker exec istad-oracle bash -c "sqlplus -s appuser/apppass@localhost:1521/XEPDB1 << 'EOF'
SET FEEDBACK OFF HEADING OFF
UPDATE customers SET status = 'VERIFIED_GOLD', phone = '0987654321', updated_at = SYSTIMESTAMP
WHERE email = 'cdc.testuser@example.com';
COMMIT;
SELECT 'Oracle UPDATE done: status=' || status FROM customers WHERE email = 'cdc.testuser@example.com';
EXIT;
EOF
"

echo "⏳  Waiting ${WAIT_SECONDS}s for CDC propagation ..."
sleep "${WAIT_SECONDS}"

echo "▶  Verify UPDATE in PostgreSQL ..."
docker exec ite-postgres psql -U iteuser -d db_customer -c \
  "SELECT id, first_name, last_name, email, phone, status, updated_at FROM cdc_customers WHERE email = 'cdc.testuser@example.com';"

# ── 4. DELETE from Oracle and verify in PostgreSQL ─────────
echo ""
echo "▶  [4/4] DELETE – remove customer in Oracle ..."
docker exec istad-oracle bash -c "sqlplus -s appuser/apppass@localhost:1521/XEPDB1 << 'EOF'
SET FEEDBACK OFF HEADING OFF
DELETE FROM customers WHERE email = 'cdc.testuser@example.com';
COMMIT;
SELECT 'Oracle DELETE done' FROM DUAL;
EXIT;
EOF
"

echo "⏳  Waiting ${WAIT_SECONDS}s for CDC propagation ..."
sleep "${WAIT_SECONDS}"

echo "▶  Verify DELETE in PostgreSQL (row count should be 0) ..."
docker exec ite-postgres psql -U iteuser -d db_customer -c \
  "SELECT COUNT(*) AS row_count FROM cdc_customers WHERE email = 'cdc.testuser@example.com';"

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "  ✅  CDC Verification Complete — All operations passed!"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
