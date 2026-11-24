-- ============================================================
--  PostgreSQL CSV Source Database Setup
--  Source DB:  db_csv_source  (where CSV data lives)
--  Purpose:    Debezium watches this DB via logical replication
-- ============================================================

\c db_csv_source;

-- Enable logical replication for Debezium (requires wal_level=logical)
-- Debezium will create its own replication slot automatically.

-- ── Schema for imported CSV data ───────────────────────────
CREATE SCHEMA IF NOT EXISTS csv_import;

-- ── 1. customers ──────────────────────────────────────────
CREATE TABLE IF NOT EXISTS csv_import.customers (
    id          BIGINT PRIMARY KEY,
    first_name  VARCHAR(100) NOT NULL,
    last_name   VARCHAR(100) NOT NULL,
    email       VARCHAR(255) NOT NULL UNIQUE,
    phone       VARCHAR(20),
    city        VARCHAR(100),
    country     VARCHAR(100),
    status      VARCHAR(20)  DEFAULT 'ACTIVE',
    created_at  TIMESTAMPTZ  DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  DEFAULT NOW()
);

COMMENT ON TABLE csv_import.customers IS 'CSV-imported customers — Debezium source table';

-- ── 2. products ───────────────────────────────────────────
CREATE TABLE IF NOT EXISTS csv_import.products (
    id           BIGINT PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    category     VARCHAR(100),
    price        NUMERIC(15,2) NOT NULL,
    stock_qty    INTEGER DEFAULT 0,
    supplier     VARCHAR(255),
    sku          VARCHAR(100) UNIQUE,
    is_active    BOOLEAN DEFAULT TRUE,
    created_at   TIMESTAMPTZ DEFAULT NOW(),
    updated_at   TIMESTAMPTZ DEFAULT NOW()
);

COMMENT ON TABLE csv_import.products IS 'CSV-imported products — Debezium source table';

-- ── 3. orders ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS csv_import.orders (
    id              BIGINT PRIMARY KEY,
    customer_id     BIGINT NOT NULL,
    product_id      BIGINT NOT NULL,
    quantity        INTEGER NOT NULL DEFAULT 1,
    unit_price      NUMERIC(15,2) NOT NULL,
    total_amount    NUMERIC(15,2) NOT NULL,
    order_status    VARCHAR(50)  DEFAULT 'PENDING',
    payment_method  VARCHAR(50),
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    updated_at      TIMESTAMPTZ DEFAULT NOW()
);

COMMENT ON TABLE csv_import.orders IS 'CSV-imported orders — Debezium source table';

-- ── Enable REPLICA IDENTITY for DELETEs to include old row ─
ALTER TABLE csv_import.customers REPLICA IDENTITY FULL;
ALTER TABLE csv_import.products  REPLICA IDENTITY FULL;
ALTER TABLE csv_import.orders    REPLICA IDENTITY FULL;

-- ── Publication for Debezium logical replication ───────────
DROP PUBLICATION IF EXISTS dbz_csv_publication;
CREATE PUBLICATION dbz_csv_publication
    FOR TABLE csv_import.customers, csv_import.products, csv_import.orders;

COMMENT ON PUBLICATION dbz_csv_publication IS 'Debezium PostgreSQL CDC publication for csv_import tables';
