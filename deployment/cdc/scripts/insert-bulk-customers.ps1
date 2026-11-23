param (
    [int]$Count = 1000
)

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "  Bulk Data Ingestion Test: $Count Records (Oracle -> Postgres)" -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

# 1. Baseline count in Postgres
$initialCount = (docker exec ite-postgres psql -U iteuser -d db_customer -t -A -c "SELECT count(*) FROM cdc_customers;").Trim()
Write-Host "Initial PostgreSQL row count: $initialCount" -ForegroundColor Gray

# 2. Bulk Insert in Oracle
$batchId = [DateTimeOffset]::UtcNow.ToUnixTimeMilliseconds()
Write-Host "`nInserting $Count records into Oracle (XEPDB1.APPUSER.CUSTOMERS)..." -ForegroundColor Yellow

$sw = [System.Diagnostics.Stopwatch]::StartNew()

docker exec istad-oracle bash -c "sqlplus -s appuser/apppass@localhost:1521/XEPDB1 << 'EOF'
SET FEEDBACK OFF HEADING OFF
INSERT INTO customers (first_name, last_name, email, phone, status)
SELECT 
    'User_' || LEVEL,
    'Batch_$batchId',
    'bulk_' || LEVEL || '_$batchId@test.com',
    '012' || LPAD(MOD(LEVEL, 10000000), 7, '0'),
    'ACTIVE'
FROM dual
CONNECT BY LEVEL <= $Count;
COMMIT;
SELECT 'Inserted ' || COUNT(*) || ' records into Oracle' FROM customers WHERE last_name = 'Batch_$batchId';
EXIT;
EOF
"

$oracleDuration = $sw.ElapsedMilliseconds
Write-Host "Oracle insert completed in $($oracleDuration) ms." -ForegroundColor Green

# 3. Monitor Real-Time Ingestion into PostgreSQL
Write-Host "`nStreaming CDC changes via Debezium & Kafka to PostgreSQL..." -ForegroundColor Yellow
$expectedCount = [int]$initialCount + $Count

$timeout = 60 # seconds
$elapsed = 0
$pgCount = [int]$initialCount

while ($elapsed -lt $timeout -and $pgCount -lt $expectedCount) {
    Start-Sleep -Seconds 1
    $elapsed++
    $pgCount = [int](docker exec ite-postgres psql -U iteuser -d db_customer -t -A -c "SELECT count(*) FROM cdc_customers;").Trim()
    $replicated = $pgCount - [int]$initialCount
    Write-Host "[$($elapsed)s] Replicated: $replicated / $Count rows (Total in PG: $pgCount)" -ForegroundColor Cyan
}

$sw.Stop()

Write-Host "`n==========================================================" -ForegroundColor Green
if ($pgCount -ge $expectedCount) {
    Write-Host "SUCCESS: All $Count records replicated to PostgreSQL!" -ForegroundColor Green
    Write-Host "Total End-to-End Pipeline Duration: $($sw.Elapsed.TotalSeconds.ToString('F2')) seconds" -ForegroundColor Green
    $throughput = [math]::Round($Count / $sw.Elapsed.TotalSeconds, 2)
    Write-Host "Effective Ingestion Throughput: $throughput rows/sec" -ForegroundColor Green
} else {
    Write-Host "TIMEOUT: Ingestion still in progress ($pgCount / $expectedCount rows)." -ForegroundColor Red
}
Write-Host "==========================================================" -ForegroundColor Green
