-- ============================================================
--  PostgreSQL CSV Destination Database Setup
--  Dest DB:    db_csv_dest  (where CDC events are written)
--  Purpose:    Receives Debezium CDC events via JDBC Sink
-- ============================================================

\c db_csv_dest;

-- ── Schema for CDC-replicated destination tables ───────────
CREATE SCHEMA IF NOT EXISTS csv_cdc;

-- ── 1. customers (destination) ────────────────────────────
CREATE TABLE IF NOT EXISTS csv_cdc.customers (
    id          BIGINT PRIMARY KEY,
    first_name  VARCHAR(100),
    last_name   VARCHAR(100),
    email       VARCHAR(255) UNIQUE,
    phone       VARCHAR(20),
    city        VARCHAR(100),
    country     VARCHAR(100),
    status      VARCHAR(20)  DEFAULT 'ACTIVE',
    created_at  TIMESTAMPTZ,
    updated_at  TIMESTAMPTZ
);

COMMENT ON TABLE csv_cdc.customers IS 'CDC destination: replicated from db_csv_source.csv_import.customers';

-- ── 2. products (destination) ─────────────────────────────
CREATE TABLE IF NOT EXISTS csv_cdc.products (
    id           BIGINT PRIMARY KEY,
    product_name VARCHAR(255),
    category     VARCHAR(100),
    price        NUMERIC(15,2),
    stock_qty    INTEGER,
    supplier     VARCHAR(255),
    sku          VARCHAR(100) UNIQUE,
    is_active    BOOLEAN,
    created_at   TIMESTAMPTZ,
    updated_at   TIMESTAMPTZ
);

COMMENT ON TABLE csv_cdc.products IS 'CDC destination: replicated from db_csv_source.csv_import.products';

-- ── 3. orders (destination) ───────────────────────────────
CREATE TABLE IF NOT EXISTS csv_cdc.orders (
    id              BIGINT PRIMARY KEY,
    customer_id     BIGINT,
    product_id      BIGINT,
    quantity        INTEGER,
    unit_price      NUMERIC(15,2),
    total_amount    NUMERIC(15,2),
    order_status    VARCHAR(50),
    payment_method  VARCHAR(50),
    created_at      TIMESTAMPTZ,
    updated_at      TIMESTAMPTZ
);

COMMENT ON TABLE csv_cdc.orders IS 'CDC destination: replicated from db_csv_source.csv_import.orders';

-- ── 4. CDC Audit Log ──────────────────────────────────────
CREATE TABLE IF NOT EXISTS csv_cdc.cdc_audit_log (
    id          BIGSERIAL PRIMARY KEY,
    table_name  VARCHAR(255) NOT NULL,
    record_id   BIGINT,
    operation   VARCHAR(10) NOT NULL,
    payload     JSONB,
    synced_at   TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_csv_cdc_audit_table  ON csv_cdc.cdc_audit_log(table_name);
CREATE INDEX IF NOT EXISTS idx_csv_cdc_audit_synced ON csv_cdc.cdc_audit_log(synced_at);

-- ── 5. Trigger function for audit logging ─────────────────
CREATE OR REPLACE FUNCTION csv_cdc.log_cdc_change()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    IF TG_OP = 'DELETE' THEN
        INSERT INTO csv_cdc.cdc_audit_log(table_name, record_id, operation, payload)
        VALUES (TG_TABLE_NAME, OLD.id, 'DELETE', to_jsonb(OLD));
        RETURN OLD;
    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO csv_cdc.cdc_audit_log(table_name, record_id, operation, payload)
        VALUES (TG_TABLE_NAME, NEW.id, 'UPDATE', to_jsonb(NEW));
        RETURN NEW;
    ELSIF TG_OP = 'INSERT' THEN
        INSERT INTO csv_cdc.cdc_audit_log(table_name, record_id, operation, payload)
        VALUES (TG_TABLE_NAME, NEW.id, 'INSERT', to_jsonb(NEW));
        RETURN NEW;
    END IF;
END;
$$;

-- Attach triggers to destination tables
DROP TRIGGER IF EXISTS trg_csv_cdc_customers ON csv_cdc.customers;
CREATE TRIGGER trg_csv_cdc_customers
  AFTER INSERT OR UPDATE OR DELETE ON csv_cdc.customers
  FOR EACH ROW EXECUTE FUNCTION csv_cdc.log_cdc_change();

DROP TRIGGER IF EXISTS trg_csv_cdc_products ON csv_cdc.products;
CREATE TRIGGER trg_csv_cdc_products
  AFTER INSERT OR UPDATE OR DELETE ON csv_cdc.products
  FOR EACH ROW EXECUTE FUNCTION csv_cdc.log_cdc_change();

DROP TRIGGER IF EXISTS trg_csv_cdc_orders ON csv_cdc.orders;
CREATE TRIGGER trg_csv_cdc_orders
  AFTER INSERT OR UPDATE OR DELETE ON csv_cdc.orders
  FOR EACH ROW EXECUTE FUNCTION csv_cdc.log_cdc_change();

-- ── 6. Consistency validation view ────────────────────────
CREATE OR REPLACE VIEW csv_cdc.v_cdc_summary AS
SELECT
    table_name,
    COUNT(*)                                             AS total_events,
    COUNT(*) FILTER (WHERE operation = 'INSERT')         AS inserts,
    COUNT(*) FILTER (WHERE operation = 'UPDATE')         AS updates,
    COUNT(*) FILTER (WHERE operation = 'DELETE')         AS deletes,
    MAX(synced_at)                                       AS last_synced,
    NOW() - MAX(synced_at)                               AS lag
FROM csv_cdc.cdc_audit_log
GROUP BY table_name;

COMMENT ON VIEW csv_cdc.v_cdc_summary IS 'Real-time CDC operation stats for CSV pipeline';
