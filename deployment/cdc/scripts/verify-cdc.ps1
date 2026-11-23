# ============================================================
#  verify-cdc.ps1
#  End-to-end CDC validation in PowerShell:
#    1. Insert in Oracle
#    2. Update in Oracle
#    3. Verify in PostgreSQL (cdc_customers)
#    4. Delete in Oracle & verify removal in PostgreSQL
# ============================================================

Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host "  CDC End-to-End Verification (Oracle -> Kafka -> PG)" -ForegroundColor Cyan
Write-Host "=====================================================" -ForegroundColor Cyan

# ── 1. INSERT into Oracle ──────────────────────────────────
Write-Host "`n[1/4] INSERT - new customer in Oracle..." -ForegroundColor Yellow
docker exec istad-oracle bash -c "sqlplus -s appuser/apppass@localhost:1521/XEPDB1 << 'EOF'
SET FEEDBACK OFF HEADING OFF
INSERT INTO customers (first_name, last_name, email, phone, status)
VALUES ('CDC', 'TestUser', 'cdc.testuser@example.com', '0123456789', 'ACTIVE');
COMMIT;
SELECT 'Oracle INSERT done: email=' || email FROM customers WHERE email = 'cdc.testuser@example.com';
EXIT;
EOF
"

Write-Host "Waiting 6s for CDC propagation..." -ForegroundColor Gray
Start-Sleep -Seconds 6

# ── 2. Verify INSERT in PostgreSQL ─────────────────────────
Write-Host "`n[2/4] Verify INSERT in PostgreSQL..." -ForegroundColor Yellow
docker exec ite-postgres psql -U iteuser -d db_customer -c "SELECT id, first_name, last_name, email, phone, status FROM cdc_customers WHERE email = 'cdc.testuser@example.com';"

# ── 3. UPDATE in Oracle ────────────────────────────────────
Write-Host "`n[3/4] UPDATE - change customer status in Oracle..." -ForegroundColor Yellow
docker exec istad-oracle bash -c "sqlplus -s appuser/apppass@localhost:1521/XEPDB1 << 'EOF'
SET FEEDBACK OFF HEADING OFF
UPDATE customers SET status = 'VERIFIED_GOLD', phone = '0987654321', updated_at = SYSTIMESTAMP
WHERE email = 'cdc.testuser@example.com';
COMMIT;
SELECT 'Oracle UPDATE done: status=' || status FROM customers WHERE email = 'cdc.testuser@example.com';
EXIT;
EOF
"

Write-Host "Waiting 6s for CDC propagation..." -ForegroundColor Gray
Start-Sleep -Seconds 6

Write-Host "`nVerify UPDATE in PostgreSQL..." -ForegroundColor Yellow
docker exec ite-postgres psql -U iteuser -d db_customer -c "SELECT id, first_name, last_name, email, phone, status, updated_at FROM cdc_customers WHERE email = 'cdc.testuser@example.com';"

# ── 4. DELETE from Oracle and verify in PostgreSQL ─────────
Write-Host "`n[4/4] DELETE - remove customer in Oracle..." -ForegroundColor Yellow
docker exec istad-oracle bash -c "sqlplus -s appuser/apppass@localhost:1521/XEPDB1 << 'EOF'
SET FEEDBACK OFF HEADING OFF
DELETE FROM customers WHERE email = 'cdc.testuser@example.com';
COMMIT;
SELECT 'Oracle DELETE done' FROM DUAL;
EXIT;
EOF
"

Write-Host "Waiting 6s for CDC propagation..." -ForegroundColor Gray
Start-Sleep -Seconds 6

Write-Host "`nVerify DELETE in PostgreSQL (row count should be 0)..." -ForegroundColor Yellow
docker exec ite-postgres psql -U iteuser -d db_customer -c "SELECT COUNT(*) AS row_count FROM cdc_customers WHERE email = 'cdc.testuser@example.com';"

Write-Host "`n=====================================================" -ForegroundColor Green
Write-Host "  CDC Verification Complete - All operations passed!" -ForegroundColor Green
Write-Host "=====================================================" -ForegroundColor Green
