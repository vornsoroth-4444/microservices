-- ============================================================
--  Oracle CDC Bootstrap Script
--  Runs once inside the container on first start-up.
--  Enables LogMiner prerequisites for Debezium Oracle connector.
-- ============================================================

-- 1. Connect as SYSDBA to configure the CDB
ALTER SESSION SET CONTAINER = CDB$ROOT;

-- 2. Enable supplemental logging at the database level
--    (required so LogMiner captures before/after values)
ALTER DATABASE ADD SUPPLEMENTAL LOG DATA;
ALTER DATABASE ADD SUPPLEMENTAL LOG DATA (ALL) COLUMNS;

-- 3. Set log retention: keep redo logs for 1 day minimum
ALTER SYSTEM SET LOG_ARCHIVE_DEST_1='LOCATION=USE_DB_RECOVERY_FILE_DEST' SCOPE=BOTH;
ALTER SYSTEM SET DB_RECOVERY_FILE_DEST_SIZE=10G SCOPE=BOTH;

-- 4. Grant Debezium user the minimum required privileges at CDB level
ALTER SESSION SET CONTAINER = CDB$ROOT;
CREATE USER c##dbzuser IDENTIFIED BY dbzpass CONTAINER=ALL;
GRANT CREATE SESSION          TO c##dbzuser CONTAINER=ALL;
GRANT SET CONTAINER           TO c##dbzuser CONTAINER=ALL;
GRANT SELECT ANY TRANSACTION  TO c##dbzuser CONTAINER=ALL;
GRANT LOGMINING               TO c##dbzuser CONTAINER=ALL;
GRANT EXECUTE_CATALOG_ROLE    TO c##dbzuser CONTAINER=ALL;
GRANT SELECT ANY DICTIONARY   TO c##dbzuser CONTAINER=ALL;
GRANT SELECT_CATALOG_ROLE     TO c##dbzuser CONTAINER=ALL;
GRANT SELECT ON V_$DATABASE   TO c##dbzuser CONTAINER=ALL;
GRANT SELECT ON V_$LOGFILE    TO c##dbzuser CONTAINER=ALL;
GRANT SELECT ON V_$ARCHIVED_LOG TO c##dbzuser CONTAINER=ALL;
GRANT SELECT ON V_$LOG        TO c##dbzuser CONTAINER=ALL;
GRANT SELECT ON V_$LOGMNR_CONTENTS TO c##dbzuser CONTAINER=ALL;
GRANT SELECT ON V_$LOGMNR_LOGS TO c##dbzuser CONTAINER=ALL;
GRANT SELECT ON V_$TRANSACTION TO c##dbzuser CONTAINER=ALL;
GRANT SELECT ON V_$ACTIVE_TRANSACTIONS TO c##dbzuser CONTAINER=ALL;
GRANT SELECT ON V_$PARAMETER  TO c##dbzuser CONTAINER=ALL;

-- 5. Switch to the pluggable database
ALTER SESSION SET CONTAINER = XEPDB1;

-- 6. Create application tables in the pluggable DB
CREATE USER appuser IDENTIFIED BY apppass;
GRANT CONNECT, RESOURCE, CREATE TABLE TO appuser;
ALTER USER appuser QUOTA UNLIMITED ON USERS;

-- 7. Create sample tables to demonstrate CDC
CREATE TABLE appuser.customers (
    id          NUMBER(10)    GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    first_name  VARCHAR2(100) NOT NULL,
    last_name   VARCHAR2(100) NOT NULL,
    email       VARCHAR2(255) NOT NULL UNIQUE,
    phone       VARCHAR2(20),
    status      VARCHAR2(20)  DEFAULT 'ACTIVE',
    created_at  TIMESTAMP     DEFAULT SYSTIMESTAMP,
    updated_at  TIMESTAMP     DEFAULT SYSTIMESTAMP
);

CREATE TABLE appuser.orders (
    id           NUMBER(10)      GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    customer_id  NUMBER(10)      NOT NULL,
    order_status VARCHAR2(50)    DEFAULT 'PENDING',
    total_amount NUMBER(15, 2)   NOT NULL,
    currency     VARCHAR2(3)     DEFAULT 'USD',
    created_at   TIMESTAMP       DEFAULT SYSTIMESTAMP,
    updated_at   TIMESTAMP       DEFAULT SYSTIMESTAMP,
    CONSTRAINT fk_orders_customer FOREIGN KEY (customer_id) REFERENCES appuser.customers(id)
);

CREATE TABLE appuser.products (
    id           NUMBER(10)      GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    product_name VARCHAR2(255)   NOT NULL,
    category     VARCHAR2(100),
    price        NUMBER(15, 2)   NOT NULL,
    stock_qty    NUMBER(10)      DEFAULT 0,
    created_at   TIMESTAMP       DEFAULT SYSTIMESTAMP,
    updated_at   TIMESTAMP       DEFAULT SYSTIMESTAMP
);

-- 8. Enable supplemental logging per table (column-level)
ALTER TABLE appuser.customers ADD SUPPLEMENTAL LOG DATA (ALL) COLUMNS;
ALTER TABLE appuser.orders    ADD SUPPLEMENTAL LOG DATA (ALL) COLUMNS;
ALTER TABLE appuser.products  ADD SUPPLEMENTAL LOG DATA (ALL) COLUMNS;

-- 9. Grant Debezium user read access in PDB
GRANT SELECT ON appuser.customers TO c##dbzuser;
GRANT SELECT ON appuser.orders    TO c##dbzuser;
GRANT SELECT ON appuser.products  TO c##dbzuser;

-- 10. Insert seed data for initial snapshot
INSERT INTO appuser.customers (first_name, last_name, email, phone, status)
VALUES ('Sokha',  'Chan',   'sokha.chan@example.com',   '0123456789', 'ACTIVE');

INSERT INTO appuser.customers (first_name, last_name, email, phone, status)
VALUES ('Dara',   'Pich',   'dara.pich@example.com',   '0987654321', 'ACTIVE');

INSERT INTO appuser.customers (first_name, last_name, email, phone, status)
VALUES ('Malis',  'Heng',   'malis.heng@example.com',  '0112233445', 'ACTIVE');

INSERT INTO appuser.products (product_name, category, price, stock_qty)
VALUES ('Laptop Pro 15', 'Electronics', 1299.99, 50);

INSERT INTO appuser.products (product_name, category, price, stock_qty)
VALUES ('Wireless Mouse', 'Accessories', 29.99, 200);

INSERT INTO appuser.orders (customer_id, order_status, total_amount)
VALUES (1, 'CONFIRMED', 1299.99);

COMMIT;

PROMPT Oracle CDC bootstrap complete.
