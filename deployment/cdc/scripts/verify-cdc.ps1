# ============================================================
#  verify-cdc.ps1
#  Assertive End-to-End CDC validation in PowerShell:
#    1. Insert in Oracle & verify in PostgreSQL
#    2. Update in Oracle & verify in PostgreSQL
#    3. Delete in Oracle & verify in PostgreSQL
# ============================================================

Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host "  CDC End-to-End Verification (Oracle -> Kafka -> PG)" -ForegroundColor Cyan
Write-Host "=====================================================" -ForegroundColor Cyan

function Wait-For-Pg-Condition {
    param (
        [string]$Query,
        [scriptblock]$Condition,
        [int]$TimeoutSeconds = 15
    )
    $sw = [System.Diagnostics.Stopwatch]::StartNew()
    while ($sw.Elapsed.TotalSeconds -lt $TimeoutSeconds) {
        $out = (docker exec ite-postgres psql -U iteuser -d db_customer -t -A -c $Query 2>$null)
        if ($out) {
            $trimmed = $out.Trim()
            if (& $Condition $trimmed) {
                return $trimmed
            }
        }
        Start-Sleep -Milliseconds 1000
    }
    return $null
}

# ── 1. INSERT into Oracle ──────────────────────────────────
Write-Host "`n[1/3] INSERT - creating customer in Oracle..." -ForegroundColor Yellow
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

Write-Host "Waiting for CDC replication to PostgreSQL..." -ForegroundColor Gray
$insertVerified = Wait-For-Pg-Condition -Query "SELECT status FROM cdc_customers WHERE email = 'cdc.testuser@example.com';" -Condition { param($v) $v -eq 'ACTIVE' } -TimeoutSeconds 15

if ($insertVerified) {
    Write-Host "SUCCESS: INSERT replicated to PostgreSQL!" -ForegroundColor Green
    docker exec ite-postgres psql -U iteuser -d db_customer -c "SELECT id, first_name, last_name, email, phone, status FROM cdc_customers WHERE email = 'cdc.testuser@example.com';"
} else {
    Write-Host "FAILED: INSERT not replicated to PostgreSQL within timeout." -ForegroundColor Red
    exit 1
}

# ── 2. UPDATE in Oracle ────────────────────────────────────
Write-Host "`n[2/3] UPDATE - changing customer status to VERIFIED_GOLD..." -ForegroundColor Yellow
docker exec istad-oracle bash -c "sqlplus -s appuser/apppass@localhost:1521/XEPDB1 << 'EOF'
SET FEEDBACK OFF HEADING OFF
UPDATE customers SET status = 'VERIFIED_GOLD', phone = '0987654321', updated_at = SYSTIMESTAMP
WHERE email = 'cdc.testuser@example.com';
COMMIT;
SELECT 'Oracle UPDATE done: status=' || status FROM customers WHERE email = 'cdc.testuser@example.com';
EXIT;
EOF
"

Write-Host "Waiting for CDC update to propagate..." -ForegroundColor Gray
$updateVerified = Wait-For-Pg-Condition -Query "SELECT status FROM cdc_customers WHERE email = 'cdc.testuser@example.com';" -Condition { param($v) $v -eq 'VERIFIED_GOLD' } -TimeoutSeconds 15

if ($updateVerified) {
    Write-Host "SUCCESS: UPDATE replicated to PostgreSQL!" -ForegroundColor Green
    docker exec ite-postgres psql -U iteuser -d db_customer -c "SELECT id, first_name, last_name, email, phone, status, updated_at FROM cdc_customers WHERE email = 'cdc.testuser@example.com';"
} else {
    Write-Host "FAILED: UPDATE not replicated to PostgreSQL within timeout." -ForegroundColor Red
    exit 1
}

# ── 3. DELETE from Oracle ──────────────────────────────────
Write-Host "`n[3/3] DELETE - removing customer from Oracle..." -ForegroundColor Yellow
docker exec istad-oracle bash -c "sqlplus -s appuser/apppass@localhost:1521/XEPDB1 << 'EOF'
SET FEEDBACK OFF HEADING OFF
DELETE FROM customers WHERE email = 'cdc.testuser@example.com';
COMMIT;
SELECT 'Oracle DELETE done' FROM DUAL;
EXIT;
EOF
"

Write-Host "Waiting for CDC delete to propagate..." -ForegroundColor Gray
$deleteVerified = Wait-For-Pg-Condition -Query "SELECT COUNT(*) FROM cdc_customers WHERE email = 'cdc.testuser@example.com';" -Condition { param($v) $v -eq '0' } -TimeoutSeconds 15

if ($deleteVerified -eq '0') {
    Write-Host "SUCCESS: DELETE replicated to PostgreSQL (row count is 0)!" -ForegroundColor Green
    docker exec ite-postgres psql -U iteuser -d db_customer -c "SELECT COUNT(*) AS row_count FROM cdc_customers WHERE email = 'cdc.testuser@example.com';"
} else {
    Write-Host "FAILED: DELETE not replicated to PostgreSQL within timeout." -ForegroundColor Red
    exit 1
}

Write-Host "`n=====================================================" -ForegroundColor Green
Write-Host "  CDC Verification Complete - All operations passed!" -ForegroundColor Green
Write-Host "=====================================================" -ForegroundColor Green
