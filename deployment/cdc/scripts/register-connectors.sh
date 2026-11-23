#!/usr/bin/env bash
# ============================================================
#  register-connectors.sh
#  Registers (or updates) Debezium Oracle Source connector and
#  JDBC PostgreSQL Sink connector via the Connect REST API.
#
#  Usage:
#    bash register-connectors.sh [connect-host] [connect-port]
#
#  Defaults: localhost 8083
# ============================================================
set -euo pipefail

CONNECT_HOST="${1:-localhost}"
CONNECT_PORT="${2:-8083}"
CONNECT_URL="http://${CONNECT_HOST}:${CONNECT_PORT}"
CONNECTORS_DIR="$(dirname "$0")/../connectors"

# ── Wait for Connect to be ready ──────────────────────────
echo "⏳  Waiting for Kafka Connect at ${CONNECT_URL} ..."
until curl -sf "${CONNECT_URL}/connectors" > /dev/null 2>&1; do
  sleep 3
done
echo "✅  Kafka Connect is ready."

# ── Helper: register or update a connector ─────────────────
register_or_update() {
  local name="$1"
  local file="$2"

  echo ""
  echo "📌  Registering connector: ${name}"

  # Check if connector already exists
  HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" "${CONNECT_URL}/connectors/${name}")

  if [ "${HTTP_CODE}" -eq 200 ]; then
    echo "   ↳ Connector exists – updating config ..."
    curl -sf -X PUT \
      -H "Content-Type: application/json" \
      --data "@${file}" \
      "${CONNECT_URL}/connectors/${name}/config" | jq .
  else
    echo "   ↳ Creating new connector ..."
    curl -sf -X POST \
      -H "Content-Type: application/json" \
      --data "@${file}" \
      "${CONNECT_URL}/connectors" | jq .
  fi
}

# ── Register Source Connector (Oracle → Kafka) ─────────────
register_or_update "oracle-cdc-source" "${CONNECTORS_DIR}/oracle-source-connector.json"

# ── Register Sink Connector (Kafka → PostgreSQL) ───────────
register_or_update "postgres-cdc-sink"  "${CONNECTORS_DIR}/postgres-sink-connector.json"

echo ""
echo "🎉  All connectors registered."
echo ""
echo "── Connector Status ──────────────────────────────────"
curl -sf "${CONNECT_URL}/connectors?expand=status" | jq '.[] | {name: .status.name, state: .status.connector.state}'
