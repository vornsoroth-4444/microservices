#!/usr/bin/env bash
# ============================================================
#  insert-bulk-customers.sh
#  Bulk insert $1 (default 1000) records into Oracle and 
#  track real-time CDC replication into PostgreSQL.
# ============================================================
COUNT=${1:-1000}
BATCH_ID=$(date +%s%N | cut -b1-13)

echo "=========================================================="
echo "  Bulk Data Ingestion Test: ${COUNT} Records (Oracle -> Postgres)"
echo "=========================================================="

# 1. Baseline count in Postgres
INIT_COUNT=$(docker exec ite-postgres psql -U iteuser -d db_customer -t -A -c "SELECT count(*) FROM cdc_customers;")
echo "Initial PostgreSQL row count: ${INIT_COUNT}"

# 2. Bulk Insert in Oracle
echo -e "\nInserting ${COUNT} records into Oracle..."
START_TIME=$(date +%s)

docker exec istad-oracle bash -c "sqlplus -s appuser/apppass@localhost:1521/XEPDB1 << 'EOF'
SET FEEDBACK OFF HEADING OFF
INSERT INTO customers (first_name, last_name, email, phone, status)
SELECT 
    'User_' || LEVEL,
    'Batch_${BATCH_ID}',
    'bulk_' || LEVEL || '_${BATCH_ID}@test.com',
    '012' || LPAD(MOD(LEVEL, 10000000), 7, '0'),
    'ACTIVE'
FROM dual
CONNECT BY LEVEL <= ${COUNT};
COMMIT;
EXIT;
EOF
"
echo "Oracle insert and commit finished."

# 3. Track Replication in PostgreSQL
EXPECTED_COUNT=$((INIT_COUNT + COUNT))
echo -e "\nStreaming CDC changes via Debezium & Kafka to PostgreSQL..."

ELAPSED=0
TIMEOUT=60
PG_COUNT=${INIT_COUNT}

while [ ${ELAPSED} -lt ${TIMEOUT} ] && [ ${PG_COUNT} -lt ${EXPECTED_COUNT} ]; do
    sleep 1
    ELAPSED=$((ELAPSED + 1))
    PG_COUNT=$(docker exec ite-postgres psql -U iteuser -d db_customer -t -A -c "SELECT count(*) FROM cdc_customers;")
    REPLICATED=$((PG_COUNT - INIT_COUNT))
    echo "[${ELAPSED}s] Replicated: ${REPLICATED} / ${COUNT} rows (Total in PG: ${PG_COUNT})"
done

TOTAL_TIME=$(( $(date +%s) - START_TIME ))

echo "=========================================================="
if [ ${PG_COUNT} -ge ${EXPECTED_COUNT} ]; then
    echo "SUCCESS: All ${COUNT} records replicated to PostgreSQL in ${TOTAL_TIME}s!"
else
    echo "TIMEOUT: Replicated ${PG_COUNT}/${EXPECTED_COUNT} rows."
fi
echo "=========================================================="
