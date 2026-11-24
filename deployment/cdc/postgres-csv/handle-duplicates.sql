-- ============================================================
--  handle-duplicates.sql
--  Data Consistency & Deduplication Strategies for CDC Pipeline
-- ============================================================

-- ── 1. STRATEGY 1: Kafka / Debezium Ingestion Deduplication (Upsert) ──
-- The Debezium JDBC Sink is configured with:
--   "insert.mode": "upsert"
--   "primary.key.mode": "record_key"
--
-- Why this guarantees idempotency:
-- In distributed systems, Kafka provides "at-least-once" delivery semantics.
-- Network retries or rebalances can cause the same CDC event to be consumed
-- multiple times. Under upsert mode, the JDBC Sink issues:
--   INSERT INTO <table> (...) VALUES (...)
--   ON CONFLICT (<primary_key>) DO UPDATE SET ...;
-- This ensures that duplicate Kafka deliveries never result in duplicate rows!

\c db_csv_dest;

-- ── 2. STRATEGY 2: Audit & Detect Duplicates ──────────────────
-- Query to identify any duplicate email addresses in destination customers:
SELECT email, COUNT(*) AS duplicate_count, array_agg(id) AS customer_ids
FROM csv_cdc.customers
GROUP BY email
HAVING COUNT(*) > 1;

-- Query to identify duplicate SKUs in destination products:
SELECT sku, COUNT(*) AS duplicate_count, array_agg(id) AS product_ids
FROM csv_cdc.products
GROUP BY sku
HAVING COUNT(*) > 1;

-- ── 3. STRATEGY 3: Clean Up Existing Duplicates (if any occur) ─
-- Using standard PostgreSQL ctid technique (keeps lowest ctid / first inserted):
/*
DELETE FROM csv_cdc.customers a USING csv_cdc.customers b
WHERE a.ctid < b.ctid AND a.email = b.email;
*/

-- Using ROW_NUMBER() window function (keeps latest updated record):
/*
WITH ranked_dupes AS (
    SELECT id, ROW_NUMBER() OVER (PARTITION BY email ORDER BY updated_at DESC, id DESC) AS rn
    FROM csv_cdc.customers
)
DELETE FROM csv_cdc.customers
WHERE id IN (
    SELECT id FROM ranked_dupes WHERE rn > 1
);
*/

-- ── 4. STRATEGY 4: Source Import Deduplication ─────────────────
-- When importing dirty/untrusted CSV files into the source PostgreSQL DB,
-- a staging table pattern prevents duplicate insertion into the CDC table:
\c db_csv_source;

CREATE SCHEMA IF NOT EXISTS csv_staging;

CREATE TABLE IF NOT EXISTS csv_staging.customers_staging (
    id          BIGINT,
    first_name  VARCHAR(100),
    last_name   VARCHAR(100),
    email       VARCHAR(255),
    phone       VARCHAR(20),
    city        VARCHAR(100),
    country     VARCHAR(100),
    status      VARCHAR(20),
    created_at  TIMESTAMPTZ
);

-- Deduplicated merge from staging to source CDC table:
-- (Only inserts the latest record per email, updating existing records)
/*
INSERT INTO csv_import.customers (id, first_name, last_name, email, phone, city, country, status, created_at, updated_at)
SELECT DISTINCT ON (email)
    id, first_name, last_name, email, phone, city, country, status, created_at, NOW()
FROM csv_staging.customers_staging
ORDER BY email, created_at DESC
ON CONFLICT (id) DO UPDATE SET
    first_name = EXCLUDED.first_name,
    last_name  = EXCLUDED.last_name,
    email      = EXCLUDED.email,
    phone      = EXCLUDED.phone,
    city       = EXCLUDED.city,
    country    = EXCLUDED.country,
    status     = EXCLUDED.status,
    updated_at = NOW();
*/
