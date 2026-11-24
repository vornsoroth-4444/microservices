# ============================================================
#  verify-csv-cdc.ps1
#  End-to-end CDC verification for CSV → PostgreSQL pipeline
#  Tests INSERT, UPDATE, DELETE propagation from db_csv_source
#  to db_csv_dest via Debezium + Kafka
# ============================================================

param(
    [int]$TimeoutSeconds = 60,
    [int]$PollIntervalMs = 1500
)

$ErrorActionPreference = "Stop"

$PG_CMD   = "docker"
$PG_ARGS  = @("exec", "ite-postgres", "psql", "-U", "iteuser")
$SRC_DB   = "db_csv_source"
$DST_DB   = "db_csv_dest"
$PASS     = 0
$FAIL     = 0

function Write-Step([string]$msg) { Write-Host "`n==> $msg" -ForegroundColor Cyan }
function Write-Ok([string]$msg)   { Write-Host "    [PASS] $msg" -ForegroundColor Green;  $global:PASS++ }
function Write-Fail([string]$msg) { Write-Host "    [FAIL] $msg" -ForegroundColor Red;    $global:FAIL++ }

function Invoke-PG([string]$db, [string]$sql) {
    $result = & $PG_CMD @PG_ARGS -d $db -t -A -c $sql 2>&1
    return ($result -join "`n").Trim()
}

function Wait-For-PG([string]$db, [string]$sql, [string]$expected, [string]$label) {
    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    while ((Get-Date) -lt $deadline) {
        $val = Invoke-PG $db $sql
        if ($val -eq $expected) {
            Write-Ok "$label (got '$val' in $(($deadline - (Get-Date)).TotalSeconds.ToString('F0'))s remaining)"
            return $true
        }
        Start-Sleep -Milliseconds $PollIntervalMs
    }
    $actual = Invoke-PG $db $sql
    Write-Fail "$label — expected '$expected', got '$actual' after ${TimeoutSeconds}s"
    return $false
}

Write-Host "============================================================" -ForegroundColor Magenta
Write-Host " CSV → PostgreSQL CDC Verification" -ForegroundColor Magenta
Write-Host " Source: $SRC_DB  |  Dest: $DST_DB" -ForegroundColor Magenta
Write-Host "============================================================" -ForegroundColor Magenta

# ── 0. Baseline counts ───────────────────────────────────────
Write-Step "0. Baseline counts"
$srcCust = Invoke-PG $SRC_DB "SELECT COUNT(*) FROM csv_import.customers"
$dstCust = Invoke-PG $DST_DB "SELECT COUNT(*) FROM csv_cdc.customers"
$srcProd = Invoke-PG $SRC_DB "SELECT COUNT(*) FROM csv_import.products"
$dstProd = Invoke-PG $DST_DB "SELECT COUNT(*) FROM csv_cdc.products"
$srcOrd  = Invoke-PG $SRC_DB "SELECT COUNT(*) FROM csv_import.orders"
$dstOrd  = Invoke-PG $DST_DB "SELECT COUNT(*) FROM csv_cdc.orders"

Write-Host "    Source: customers=$srcCust  products=$srcProd  orders=$srcOrd"
Write-Host "    Dest:   customers=$dstCust  products=$dstProd  orders=$dstOrd"

if ($srcCust -eq $dstCust) { Write-Ok "customers counts match ($srcCust)" } else { Write-Fail "customers mismatch src=$srcCust dst=$dstCust" }
if ($srcProd -eq $dstProd) { Write-Ok "products counts match ($srcProd)" }  else { Write-Fail "products mismatch src=$srcProd dst=$dstProd" }
if ($srcOrd  -eq $dstOrd)  { Write-Ok "orders counts match ($srcOrd)" }     else { Write-Fail "orders mismatch src=$srcOrd dst=$dstOrd" }

# ── 1. INSERT test ───────────────────────────────────────────
Write-Step "1. INSERT — new customer CDC test"
$testEmail = "cdc.csv.verify@example.com"
$testId    = 9901

# Clean up if leftover from previous run
Invoke-PG $SRC_DB "DELETE FROM csv_import.customers WHERE id=$testId" | Out-Null
Invoke-PG $DST_DB "DELETE FROM csv_cdc.customers WHERE id=$testId" | Out-Null
Start-Sleep -Milliseconds 1000

Invoke-PG $SRC_DB @"
INSERT INTO csv_import.customers(id,first_name,last_name,email,phone,city,country,status)
VALUES ($testId,'CDC','TestCSV','$testEmail','0999888777','Phnom Penh','Cambodia','ACTIVE')
"@ | Out-Null

Write-Host "    Inserted id=$testId — waiting for replication..." -ForegroundColor Yellow
$null = Wait-For-PG $DST_DB "SELECT COUNT(*) FROM csv_cdc.customers WHERE email='$testEmail'" "1" "INSERT replicated to dest"

# ── 2. UPDATE test ───────────────────────────────────────────
Write-Step "2. UPDATE — modify customer city"
Invoke-PG $SRC_DB "UPDATE csv_import.customers SET city='Siem Reap', updated_at=NOW() WHERE id=$testId" | Out-Null

Write-Host "    Updated city → 'Siem Reap' — waiting for replication..." -ForegroundColor Yellow
$null = Wait-For-PG $DST_DB "SELECT city FROM csv_cdc.customers WHERE id=$testId" "Siem Reap" "UPDATE replicated to dest"

# ── 3. DELETE test ───────────────────────────────────────────
Write-Step "3. DELETE — remove test customer"
Invoke-PG $SRC_DB "DELETE FROM csv_import.customers WHERE id=$testId" | Out-Null

Write-Host "    Deleted id=$testId — waiting for replication..." -ForegroundColor Yellow
$null = Wait-For-PG $DST_DB "SELECT COUNT(*) FROM csv_cdc.customers WHERE id=$testId" "0" "DELETE replicated to dest"

# ── 4. Audit log check ───────────────────────────────────────
Write-Step "4. Audit log — verify cdc_audit_log entries"
$auditCount = Invoke-PG $DST_DB "SELECT COUNT(*) FROM csv_cdc.cdc_audit_log WHERE record_id=$testId"
if ([int]$auditCount -ge 2) {
    Write-Ok "Audit log captured changes for record id=$testId (count: $auditCount)"
} else {
    Write-Fail "Audit log has only $auditCount entries for id=$testId"
}

$auditSummary = Invoke-PG $DST_DB "SELECT operation, COUNT(*) FROM csv_cdc.cdc_audit_log WHERE record_id=$testId GROUP BY operation ORDER BY operation"
Write-Host "    Audit breakdown:`n$auditSummary" -ForegroundColor Gray

# ── 5. Kafka topics check ────────────────────────────────────
Write-Step "5. Kafka topics — verify pg-csv-cdc topics exist"
$topics = docker exec istad-kafka-1 kafka-topics --bootstrap-server istad-kafka-1:9090 --list 2>&1
$csvTopics = $topics | Select-String "pg-csv-cdc"
if ($csvTopics) {
    Write-Ok "Kafka topics active:`n$(($csvTopics | ForEach-Object { '        ' + $_ }) -join "`n")"
} else {
    Write-Fail "No pg-csv-cdc topics found in Kafka"
}

# ── 6. CDC summary view ──────────────────────────────────────
Write-Step "6. CDC summary view"
$summary = Invoke-PG $DST_DB "SELECT table_name, total_events, inserts, updates, deletes FROM csv_cdc.v_cdc_summary ORDER BY table_name"
Write-Host "    $summary" -ForegroundColor Gray

# ── Final result ─────────────────────────────────────────────
Write-Host ""
Write-Host "============================================================" -ForegroundColor Magenta
if ($FAIL -eq 0) {
    Write-Host " ALL TESTS PASSED ($PASS/$($PASS+$FAIL)) - CSV CDC Pipeline Working!" -ForegroundColor Green
} else {
    Write-Host " RESULTS: $PASS passed, $FAIL FAILED" -ForegroundColor Red
}
Write-Host "============================================================" -ForegroundColor Magenta
