-- ============================================================
--  PostgreSQL CDC Sink Schema
--  Mirrors Oracle source tables + CDC metadata columns.
--  Run once against db_customer (or your target DB).
-- ============================================================

\c db_customer;

-- ── Schema to hold CDC-replicated tables ──────────────────
CREATE SCHEMA IF NOT EXISTS cdc_replica;

-- ── 1. customers ──────────────────────────────────────────
CREATE TABLE IF NOT EXISTS cdc_replica.customers (
    id          BIGINT PRIMARY KEY,
    first_name  VARCHAR(100),
    last_name   VARCHAR(100),
    email       VARCHAR(255) UNIQUE,
    phone       VARCHAR(20),
    status      VARCHAR(20)  DEFAULT 'ACTIVE',
    created_at  TIMESTAMPTZ,
    updated_at  TIMESTAMPTZ,
    -- CDC metadata
    _cdc_op     VARCHAR(1),          -- r=read(snapshot), c=create, u=update, d=delete
    _cdc_table  VARCHAR(255),
    _cdc_ts_ms  BIGINT,
    _synced_at  TIMESTAMPTZ DEFAULT NOW()
);

COMMENT ON TABLE  cdc_replica.customers IS 'Oracle APPUSER.CUSTOMERS replicated via Debezium CDC';
COMMENT ON COLUMN cdc_replica.customers._cdc_op IS 'Debezium operation: r/c/u/d';

-- ── 2. orders ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS cdc_replica.orders (
    id           BIGINT PRIMARY KEY,
    customer_id  BIGINT,
    order_status VARCHAR(50),
    total_amount NUMERIC(15, 2),
    currency     VARCHAR(3)  DEFAULT 'USD',
    created_at   TIMESTAMPTZ,
    updated_at   TIMESTAMPTZ,
    -- CDC metadata
    _cdc_op     VARCHAR(1),
    _cdc_table  VARCHAR(255),
    _cdc_ts_ms  BIGINT,
    _synced_at  TIMESTAMPTZ DEFAULT NOW()
);

COMMENT ON TABLE cdc_replica.orders IS 'Oracle APPUSER.ORDERS replicated via Debezium CDC';

-- ── 3. products ───────────────────────────────────────────
CREATE TABLE IF NOT EXISTS cdc_replica.products (
    id           BIGINT PRIMARY KEY,
    product_name VARCHAR(255),
    category     VARCHAR(100),
    price        NUMERIC(15, 2),
    stock_qty    INTEGER DEFAULT 0,
    created_at   TIMESTAMPTZ,
    updated_at   TIMESTAMPTZ,
    -- CDC metadata
    _cdc_op     VARCHAR(1),
    _cdc_table  VARCHAR(255),
    _cdc_ts_ms  BIGINT,
    _synced_at  TIMESTAMPTZ DEFAULT NOW()
);

COMMENT ON TABLE cdc_replica.products IS 'Oracle APPUSER.PRODUCTS replicated via Debezium CDC';

-- ── 4. CDC audit log (optional) ───────────────────────────
CREATE TABLE IF NOT EXISTS cdc_replica.cdc_audit_log (
    id          BIGSERIAL PRIMARY KEY,
    table_name  VARCHAR(255) NOT NULL,
    record_id   BIGINT,
    operation   VARCHAR(10)  NOT NULL,  -- INSERT / UPDATE / DELETE
    payload     JSONB,
    source_ts   TIMESTAMPTZ,
    synced_at   TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_cdc_audit_log_table ON cdc_replica.cdc_audit_log(table_name);
CREATE INDEX IF NOT EXISTS idx_cdc_audit_log_synced ON cdc_replica.cdc_audit_log(synced_at);

-- ── 5. Trigger function: auto-populate cdc_audit_log ──────
CREATE OR REPLACE FUNCTION cdc_replica.log_cdc_change()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
  IF TG_OP = 'DELETE' THEN
    INSERT INTO cdc_replica.cdc_audit_log(table_name, record_id, operation, payload, source_ts)
    VALUES (TG_TABLE_NAME, OLD.id, 'DELETE', to_jsonb(OLD), NOW());
    RETURN OLD;
  ELSIF TG_OP = 'UPDATE' THEN
    INSERT INTO cdc_replica.cdc_audit_log(table_name, record_id, operation, payload, source_ts)
    VALUES (TG_TABLE_NAME, NEW.id, 'UPDATE', to_jsonb(NEW), NOW());
    RETURN NEW;
  ELSIF TG_OP = 'INSERT' THEN
    INSERT INTO cdc_replica.cdc_audit_log(table_name, record_id, operation, payload, source_ts)
    VALUES (TG_TABLE_NAME, NEW.id, 'INSERT', to_jsonb(NEW), NOW());
    RETURN NEW;
  END IF;
END;
$$;

-- Attach triggers
DROP TRIGGER IF EXISTS trg_cdc_customers ON cdc_replica.customers;
CREATE TRIGGER trg_cdc_customers
  AFTER INSERT OR UPDATE OR DELETE ON cdc_replica.customers
  FOR EACH ROW EXECUTE FUNCTION cdc_replica.log_cdc_change();

DROP TRIGGER IF EXISTS trg_cdc_orders ON cdc_replica.orders;
CREATE TRIGGER trg_cdc_orders
  AFTER INSERT OR UPDATE OR DELETE ON cdc_replica.orders
  FOR EACH ROW EXECUTE FUNCTION cdc_replica.log_cdc_change();

DROP TRIGGER IF EXISTS trg_cdc_products ON cdc_replica.products;
CREATE TRIGGER trg_cdc_products
  AFTER INSERT OR UPDATE OR DELETE ON cdc_replica.products
  FOR EACH ROW EXECUTE FUNCTION cdc_replica.log_cdc_change();

-- ── 6. Consistency validation view ────────────────────────
CREATE OR REPLACE VIEW cdc_replica.v_cdc_lag AS
SELECT
    table_name,
    COUNT(*)                                                AS total_records,
    MAX(synced_at)                                          AS last_sync,
    NOW() - MAX(synced_at)                                  AS lag,
    COUNT(*) FILTER (WHERE operation = 'INSERT')            AS inserts,
    COUNT(*) FILTER (WHERE operation = 'UPDATE')            AS updates,
    COUNT(*) FILTER (WHERE operation = 'DELETE')            AS deletes
FROM cdc_replica.cdc_audit_log
GROUP BY table_name;

COMMENT ON VIEW cdc_replica.v_cdc_lag IS 'Real-time CDC lag and operation statistics';
