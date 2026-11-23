#!/usr/bin/env bash
# ============================================================
#  monitor-cdc.sh
#  Real-time monitoring dashboard for CDC pipeline health.
#  Shows connector status, Kafka topic lag, and DB row counts.
# ============================================================
set -euo pipefail

CONNECT_URL="${CONNECT_URL:-http://localhost:8083}"
PG_HOST="${PG_HOST:-localhost}"
PG_PORT="${PG_PORT:-9543}"
PG_DB="${PG_DB:-db_customer}"
PG_USER="${PG_USER:-iteuser}"
PGPASSWORD="${PGPASSWORD:-qwerqwer}"
export PGPASSWORD

REFRESH_INTERVAL="${REFRESH_INTERVAL:-10}"

print_header() {
  clear
  echo "╔══════════════════════════════════════════════════════════════╗"
  echo "║   ISTAD CDC Pipeline Monitor          $(date '+%Y-%m-%d %H:%M:%S')   ║"
  echo "╚══════════════════════════════════════════════════════════════╝"
}

check_connectors() {
  echo ""
  echo "── Connector Status ──────────────────────────────────────────"
  curl -sf "${CONNECT_URL}/connectors?expand=status" 2>/dev/null \
    | jq -r '.[] | "\(.status.name | @text)\t\(.status.connector.state | @text)\t\(.status.tasks[0].state // "NO_TASKS" | @text)"' \
    | awk 'BEGIN{printf "%-35s %-12s %-12s\n","CONNECTOR","STATUS","TASK"} {printf "%-35s %-12s %-12s\n",$1,$2,$3}' \
    || echo "  ⚠️  Cannot reach Kafka Connect at ${CONNECT_URL}"
}

check_kafka_topics() {
  echo ""
  echo "── Kafka CDC Topics ──────────────────────────────────────────"
  docker exec istad-kafka-1 kafka-topics \
    --bootstrap-server istad-kafka-1:9090 \
    --list 2>/dev/null \
    | grep "oracle-cdc" \
    | while read -r topic; do
        OFFSET=$(docker exec istad-kafka-1 kafka-run-class kafka.tools.GetOffsetShell \
          --bootstrap-server istad-kafka-1:9090 \
          --topic "${topic}" --time -1 2>/dev/null \
          | awk -F: '{sum+=$3} END{print sum}')
        printf "  %-55s  messages: %s\n" "${topic}" "${OFFSET:-0}"
      done || echo "  ⚠️  Cannot reach Kafka"
}

check_postgres() {
  echo ""
  echo "── PostgreSQL Sink Row Counts ────────────────────────────────"
  psql -h "${PG_HOST}" -p "${PG_PORT}" -U "${PG_USER}" -d "${PG_DB}" -t 2>/dev/null <<-'SQL'
    SELECT
      table_name,
      COUNT(*) AS rows
    FROM (
      SELECT 'customers' AS table_name, id FROM cdc_replica.customers
      UNION ALL
      SELECT 'orders',   id FROM cdc_replica.orders
      UNION ALL
      SELECT 'products', id FROM cdc_replica.products
    ) t
    GROUP BY table_name
    ORDER BY table_name;
SQL
  || echo "  ⚠️  Cannot reach PostgreSQL"

  echo ""
  echo "── CDC Lag Stats ─────────────────────────────────────────────"
  psql -h "${PG_HOST}" -p "${PG_PORT}" -U "${PG_USER}" -d "${PG_DB}" -t 2>/dev/null <<-'SQL'
    SELECT
      table_name,
      total_records,
      inserts,
      updates,
      deletes,
      COALESCE(EXTRACT(EPOCH FROM lag)::INT || 's', 'N/A') AS lag
    FROM cdc_replica.v_cdc_lag
    ORDER BY table_name;
SQL
  || true
}

# ── Main loop ─────────────────────────────────────────────
while true; do
  print_header
  check_connectors
  check_kafka_topics
  check_postgres
  echo ""
  echo "  Refreshing every ${REFRESH_INTERVAL}s. Press Ctrl+C to exit."
  sleep "${REFRESH_INTERVAL}"
done
