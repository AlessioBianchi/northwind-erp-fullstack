-- ============================================
-- Fix discontinued column in products
-- ============================================

ALTER TABLE products ALTER COLUMN discontinued TYPE boolean USING discontinued::boolean;


-- ============================================
-- Restructure customers table
-- Replace varchar PK with integer PK,
-- rename old code column to customer_code
-- ============================================

-- Restructure customers table
ALTER TABLE customers DROP CONSTRAINT pk_customers CASCADE;
ALTER TABLE customers RENAME COLUMN customer_id TO customer_code;
ALTER TABLE customers ADD COLUMN customer_id SERIAL PRIMARY KEY;

-- Fix orders foreign key
ALTER TABLE orders ADD COLUMN customer_fk integer;
UPDATE orders o
SET customer_fk = c.customer_id
    FROM customers c
WHERE c.customer_code = o.customer_id;
ALTER TABLE orders DROP COLUMN customer_id;
ALTER TABLE orders RENAME COLUMN customer_fk TO customer_id;
ALTER TABLE orders ADD CONSTRAINT fk_orders_customers
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id);


-- ============================================
-- Add IDENTITY to primary key columns
-- ============================================

ALTER TABLE categories ALTER COLUMN category_id ADD GENERATED ALWAYS AS IDENTITY;
ALTER TABLE employees ALTER COLUMN employee_id ADD GENERATED ALWAYS AS IDENTITY;
ALTER TABLE orders ALTER COLUMN order_id ADD GENERATED ALWAYS AS IDENTITY;
ALTER TABLE products ALTER COLUMN product_id ADD GENERATED ALWAYS AS IDENTITY;
ALTER TABLE shippers ALTER COLUMN shipper_id ADD GENERATED ALWAYS AS IDENTITY;
ALTER TABLE suppliers ALTER COLUMN supplier_id ADD GENERATED ALWAYS AS IDENTITY;


-- ============================================
-- Add authentication columns to employees
-- ============================================

ALTER TABLE employees
    ADD COLUMN username VARCHAR(50) UNIQUE,
    ADD COLUMN password VARCHAR(255);


-- ============================================
-- Seed test credentials
-- BCrypt hash below = 'password123'
-- ============================================

-- Regular employees (report to someone)
UPDATE employees
SET username = 'employee' || employee_id,
    password = '$2a$10$1RaXDpiFhgu9rokETVbJe.J93J11W3A89b91BZ8qIEjug4/C86KM6'
WHERE reports_to IS NOT NULL;

-- Manager account (reports to nobody)
UPDATE employees
SET username = 'manager' || employee_id,
    password = '$2a$10$1RaXDpiFhgu9rokETVbJe.J93J11W3A89b91BZ8qIEjug4/C86KM6'
WHERE reports_to IS NULL;