# ============================================================
#  start-cdc.ps1
#  Orchestrates the full CDC pipeline startup in PowerShell:
#    1. Postgres DB
#    2. Oracle DB
#    3. Kafka brokers
#    4. Kafka Connect + Schema Registry
#    5. Register connectors
# ============================================================

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$CdcDir = Split-Path -Parent $ScriptDir
$DeploymentDir = Split-Path -Parent $CdcDir

Write-Host "==================================================" -ForegroundColor Cyan
Write-Host "   ISTAD CDC Pipeline - Startup                   " -ForegroundColor Cyan
Write-Host "==================================================" -ForegroundColor Cyan

# ── 1. PostgreSQL ─────────────────────────────────────────
Write-Host "`n[1/5] Checking / Starting PostgreSQL..." -ForegroundColor Yellow
docker compose -f (Join-Path $DeploymentDir "postgres\docker-compose.yaml") up -d

# ── 2. Oracle ─────────────────────────────────────────────
Write-Host "`n[2/5] Checking / Starting Oracle XE..." -ForegroundColor Yellow
docker compose -f (Join-Path $DeploymentDir "oracle\docker-compose.yaml") up -d

Write-Host "Waiting for Oracle XE to be healthy..." -ForegroundColor Gray
$oracleHealthy = $false
for ($i = 1; $i -le 30; $i++) {
    $status = (docker inspect --format='{{.State.Health.Status}}' istad-oracle 2>$null)
    if ($status -eq "healthy") {
        $oracleHealthy = $true
        break
    }
    Write-Host "   Oracle status: $status (attempt $i/30)" -ForegroundColor Gray
    Start-Sleep -Seconds 5
}
if (-not $oracleHealthy) {
    Write-Host "Warning: Oracle healthcheck timed out. Continuing..." -ForegroundColor Yellow
} else {
    Write-Host "Oracle XE is healthy." -ForegroundColor Green
}

# ── 3. Kafka Brokers ──────────────────────────────────────
Write-Host "`n[3/5] Verifying Kafka brokers..." -ForegroundColor Yellow
$kafkaReady = $false
for ($i = 1; $i -le 10; $i++) {
    docker exec istad-kafka-1 kafka-topics --bootstrap-server istad-kafka-1:9090 --list 2>$null | Out-Null
    if ($LASTEXITCODE -eq 0) {
        $kafkaReady = $true
        break
    }
    Write-Host "   Waiting for Kafka (attempt $i/10)..." -ForegroundColor Gray
    Start-Sleep -Seconds 3
}
if (-not $kafkaReady) {
    Write-Host "Kafka is not running. Starting Kafka cluster..." -ForegroundColor Yellow
    docker compose -f (Join-Path $DeploymentDir "kafka\docker-compose.yaml") up -d
}
Write-Host "Kafka brokers are ready." -ForegroundColor Green

# ── 4. Kafka Connect + Schema Registry ────────────────────
Write-Host "`n[4/5] Starting Kafka Connect + Schema Registry..." -ForegroundColor Yellow
docker compose -f (Join-Path $CdcDir "docker-compose.yaml") up -d

# ── 5. Register Connectors ────────────────────────────────
Write-Host "`n[5/5] Registering connectors..." -ForegroundColor Yellow
& "$ScriptDir\register-connectors.ps1"

Write-Host "`n==================================================" -ForegroundColor Green
Write-Host "  CDC Pipeline Started Successfully               " -ForegroundColor Green
Write-Host "==================================================" -ForegroundColor Green
Write-Host "  Kafka Connect:     http://localhost:8083" -ForegroundColor Cyan
Write-Host "  Schema Registry:   http://localhost:8081" -ForegroundColor Cyan
Write-Host "  Kafka UI:          http://localhost:18080" -ForegroundColor Cyan
Write-Host "  PostgreSQL:        localhost:9543 (db_customer)" -ForegroundColor Cyan
Write-Host "  Oracle XE:         localhost:1521/XEPDB1" -ForegroundColor Cyan
Write-Host "==================================================" -ForegroundColor Green
Write-Host "  To verify CDC: powershell -File deployment\cdc\scripts\verify-cdc.ps1" -ForegroundColor Yellow
