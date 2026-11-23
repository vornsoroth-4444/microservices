#!/usr/bin/env bash
# ============================================================
#  start-cdc.sh
#  Orchestrates the full CDC pipeline startup:
#    1. Oracle DB
#    2. Kafka brokers  (expected already running)
#    3. Kafka Connect + Schema Registry
#    4. Connector registration
# ============================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
DEPLOYMENT_DIR="${SCRIPT_DIR}/.."

echo "╔══════════════════════════════════════════════════╗"
echo "║   ISTAD CDC Pipeline – Startup                  ║"
echo "╚══════════════════════════════════════════════════╝"

# ── Step 1: Start Oracle ──────────────────────────────────
echo ""
echo "▶  [1/4] Starting Oracle XE ..."
docker compose -f "${DEPLOYMENT_DIR}/oracle/docker-compose.yaml" up -d

echo "⏳  Waiting for Oracle to be healthy (this can take 2-3 min on first boot) ..."
ORACLE_HEALTHY=false
for i in $(seq 1 30); do
  STATUS=$(docker inspect --format='{{.State.Health.Status}}' istad-oracle 2>/dev/null || echo "starting")
  if [ "${STATUS}" = "healthy" ]; then
    ORACLE_HEALTHY=true
    break
  fi
  echo "   Oracle status: ${STATUS} (attempt ${i}/30)"
  sleep 10
done

if [ "${ORACLE_HEALTHY}" = false ]; then
  echo "❌  Oracle did not become healthy within 5 minutes. Check logs:"
  echo "    docker logs istad-oracle"
  exit 1
fi
echo "✅  Oracle is healthy."

# ── Step 2: Kafka (assumed running) ──────────────────────
echo ""
echo "▶  [2/4] Verifying Kafka brokers ..."
KAFKA_READY=false
for i in $(seq 1 10); do
  if docker exec istad-kafka-1 kafka-topics --bootstrap-server istad-kafka-1:9090 --list > /dev/null 2>&1; then
    KAFKA_READY=true
    break
  fi
  echo "   Kafka not ready yet (attempt ${i}/10)"
  sleep 5
done

if [ "${KAFKA_READY}" = false ]; then
  echo "❌  Kafka is not running. Start it first:"
  echo "    docker compose -f deployment/kafka/docker-compose.yaml up -d"
  exit 1
fi
echo "✅  Kafka brokers are ready."

# ── Step 3: Start Kafka Connect + Schema Registry ─────────
echo ""
echo "▶  [3/4] Starting Kafka Connect + Schema Registry ..."
docker compose -f "${DEPLOYMENT_DIR}/cdc/docker-compose.yaml" up -d --build

echo "⏳  Waiting for Kafka Connect REST API ..."
for i in $(seq 1 20); do
  if curl -sf http://localhost:8083/connectors > /dev/null 2>&1; then
    break
  fi
  echo "   Connect not ready (attempt ${i}/20)"
  sleep 10
done
echo "✅  Kafka Connect is ready."

# ── Step 4: Register connectors ───────────────────────────
echo ""
echo "▶  [4/4] Registering connectors ..."
bash "${SCRIPT_DIR}/register-connectors.sh" localhost 8083

echo ""
echo "╔══════════════════════════════════════════════════╗"
echo "║  🎉  CDC Pipeline Started Successfully           ║"
echo "╠══════════════════════════════════════════════════╣"
echo "║  Kafka Connect:     http://localhost:8083        ║"
echo "║  Schema Registry:   http://localhost:8081        ║"
echo "║  Kafka UI:          http://localhost:18080       ║"
echo "║  Oracle EM Express: https://localhost:5500/em    ║"
echo "╠══════════════════════════════════════════════════╣"
echo "║  Verify CDC pipeline:                            ║"
echo "║    bash deployment/cdc/scripts/verify-cdc.sh    ║"
echo "╚══════════════════════════════════════════════════╝"
