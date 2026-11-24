# ============================================================
#  import-csv.ps1
#  Automates loading CSV datasets into PostgreSQL source DB
#  (db_csv_source.csv_import.*)
# ============================================================

param(
    [string]$ContainerName = "ite-postgres",
    [string]$DbUser        = "iteuser",
    [string]$DbName        = "db_csv_source",
    [string]$CsvDir        = "D:\ISTAD\Java\microservices\deployment\cdc\csv"
)

$ErrorActionPreference = "Stop"

Write-Host "==> Copying CSV files into $ContainerName..." -ForegroundColor Cyan
docker cp "$CsvDir\customers.csv" "${ContainerName}:/tmp/customers.csv"
docker cp "$CsvDir\products.csv"  "${ContainerName}:/tmp/products.csv"
docker cp "$CsvDir\orders.csv"    "${ContainerName}:/tmp/orders.csv"

Write-Host "==> Importing customers.csv..." -ForegroundColor Cyan
docker exec $ContainerName psql -U $DbUser -d $DbName -c "
    COPY csv_import.customers(id, first_name, last_name, email, phone, city, country, status, created_at)
    FROM '/tmp/customers.csv' WITH (FORMAT csv, HEADER true);
"

Write-Host "==> Importing products.csv..." -ForegroundColor Cyan
docker exec $ContainerName psql -U $DbUser -d $DbName -c "
    COPY csv_import.products(id, product_name, category, price, stock_qty, supplier, sku, is_active)
    FROM '/tmp/products.csv' WITH (FORMAT csv, HEADER true);
"

Write-Host "==> Importing orders.csv..." -ForegroundColor Cyan
docker exec $ContainerName psql -U $DbUser -d $DbName -c "
    COPY csv_import.orders(id, customer_id, product_id, quantity, unit_price, total_amount, order_status, payment_method, created_at)
    FROM '/tmp/orders.csv' WITH (FORMAT csv, HEADER true);
"

Write-Host "==> Import completed successfully!" -ForegroundColor Green
docker exec $ContainerName psql -U $DbUser -d $DbName -c "
    SELECT 'customers' AS table_name, COUNT(*) AS count FROM csv_import.customers
    UNION ALL
    SELECT 'products', COUNT(*) FROM csv_import.products
    UNION ALL
    SELECT 'orders', COUNT(*) FROM csv_import.orders;
"
