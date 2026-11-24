param (
    [int]$Count = 1000,
    [string]$Table = "customers" # "customers", "products", or "orders"
)

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "  Bulk Data Ingestion Test: $Count Records (CSV Source -> Dest)" -ForegroundColor Cyan
Write-Host "  Source: db_csv_source.csv_import.$Table" -ForegroundColor Cyan
Write-Host "  Dest:   db_csv_dest.csv_cdc.$Table" -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

# 1. Baseline count in Destination
$initialCount = [int](docker exec ite-postgres psql -U iteuser -d db_csv_dest -t -A -c "SELECT COUNT(*) FROM csv_cdc.$Table;").Trim()
$srcInitialCount = [int](docker exec ite-postgres psql -U iteuser -d db_csv_source -t -A -c "SELECT COUNT(*) FROM csv_import.$Table;").Trim()
$maxId = [int](docker exec ite-postgres psql -U iteuser -d db_csv_source -t -A -c "SELECT COALESCE(MAX(id), 0) FROM csv_import.$Table;").Trim()

Write-Host "Initial destination row count: $initialCount (Source count: $srcInitialCount, Max ID: $maxId)" -ForegroundColor Gray

# 2. Bulk Insert in Source PostgreSQL using generate_series()
$batchId = [DateTimeOffset]::UtcNow.ToUnixTimeMilliseconds()
Write-Host "`nInserting $Count records into db_csv_source (csv_import.$Table)..." -ForegroundColor Yellow

$sw = [System.Diagnostics.Stopwatch]::StartNew()

if ($Table -eq "customers") {
    docker exec ite-postgres psql -U iteuser -d db_csv_source -c "
        INSERT INTO csv_import.customers (id, first_name, last_name, email, phone, city, country, status, created_at, updated_at)
        SELECT 
            $maxId + s,
            'User_' || s,
            'Bulk_$batchId',
            'bulk_' || s || '_$batchId@example.com',
            '012' || LPAD((s % 10000000)::text, 7, '0'),
            'Phnom Penh',
            'Cambodia',
            'ACTIVE',
            NOW(),
            NOW()
        FROM generate_series(1, $Count) AS s;
    " | Out-Null
} elseif ($Table -eq "products") {
    docker exec ite-postgres psql -U iteuser -d db_csv_source -c "
        INSERT INTO csv_import.products (id, product_name, category, price, stock_qty, supplier, sku, is_active, created_at, updated_at)
        SELECT 
            $maxId + s,
            'Product_' || s,
            'Electronics',
            (s % 500) + 9.99,
            100,
            'Supplier_$batchId',
            'SKU-BULK-$batchId-' || s,
            TRUE,
            NOW(),
            NOW()
        FROM generate_series(1, $Count) AS s;
    " | Out-Null
} elseif ($Table -eq "orders") {
    docker exec ite-postgres psql -U iteuser -d db_csv_source -c "
        INSERT INTO csv_import.orders (id, customer_id, product_id, quantity, unit_price, total_amount, order_status, payment_method, created_at, updated_at)
        SELECT 
            $maxId + s,
            (s % 20) + 1,
            (s % 25) + 1,
            (s % 5) + 1,
            25.00,
            ((s % 5) + 1) * 25.00,
            'COMPLETED',
            'ABA Pay',
            NOW(),
            NOW()
        FROM generate_series(1, $Count) AS s;
    " | Out-Null
}

$sourceDuration = $sw.ElapsedMilliseconds
Write-Host "Source insert completed in $($sourceDuration) ms." -ForegroundColor Green

# 3. Monitor Real-Time Ingestion into Destination PostgreSQL
Write-Host "`nStreaming CDC changes via Debezium & Kafka to Destination DB..." -ForegroundColor Yellow
$expectedCount = $initialCount + $Count

$timeout = 90 # seconds
$elapsed = 0
$dstCount = $initialCount

while ($elapsed -lt $timeout -and $dstCount -lt $expectedCount) {
    Start-Sleep -Seconds 1
    $elapsed++
    $dstCount = [int](docker exec ite-postgres psql -U iteuser -d db_csv_dest -t -A -c "SELECT COUNT(*) FROM csv_cdc.$Table;").Trim()
    $replicated = $dstCount - $initialCount
    Write-Host "[$($elapsed)s] Replicated: $replicated / $Count rows (Total in Dest: $dstCount)" -ForegroundColor Cyan
}

$sw.Stop()

Write-Host "`n==========================================================" -ForegroundColor Green
if ($dstCount -ge $expectedCount) {
    Write-Host "SUCCESS: All $Count records replicated to Destination DB!" -ForegroundColor Green
    Write-Host "Total End-to-End Pipeline Duration: $($sw.Elapsed.TotalSeconds.ToString('F2')) seconds" -ForegroundColor Green
    $throughput = [math]::Round($Count / $sw.Elapsed.TotalSeconds, 2)
    Write-Host "Effective Ingestion Throughput: $throughput rows/sec" -ForegroundColor Green
} else {
    Write-Host "TIMEOUT: Ingestion still in progress ($dstCount / $expectedCount rows)." -ForegroundColor Red
}
Write-Host "==========================================================" -ForegroundColor Green
