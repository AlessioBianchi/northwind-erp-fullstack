-- ============================================
-- Type fixes — smallint -> integer
-- ============================================

ALTER TABLE categories ALTER COLUMN category_id TYPE integer;
ALTER TABLE products ALTER COLUMN product_id TYPE integer;
ALTER TABLE products ALTER COLUMN category_id TYPE integer;
ALTER TABLE products ALTER COLUMN supplier_id TYPE integer;
ALTER TABLE products ALTER COLUMN reorder_level TYPE integer;
ALTER TABLE products ALTER COLUMN units_in_stock TYPE integer;
ALTER TABLE products ALTER COLUMN units_on_order TYPE integer;
ALTER TABLE orders ALTER COLUMN order_id TYPE integer;
ALTER TABLE orders ALTER COLUMN employee_id TYPE integer;
ALTER TABLE orders ALTER COLUMN ship_via TYPE integer;
ALTER TABLE order_details ALTER COLUMN order_id TYPE integer;
ALTER TABLE order_details ALTER COLUMN product_id TYPE integer;
ALTER TABLE order_details ALTER COLUMN quantity TYPE integer;
ALTER TABLE employees ALTER COLUMN employee_id TYPE integer;
ALTER TABLE employees ALTER COLUMN reports_to TYPE integer;
ALTER TABLE shippers ALTER COLUMN shipper_id TYPE integer;
ALTER TABLE suppliers ALTER COLUMN supplier_id TYPE integer;
ALTER TABLE employee_territories ALTER COLUMN employee_id TYPE integer;
ALTER TABLE region ALTER COLUMN region_id TYPE integer;
ALTER TABLE territories ALTER COLUMN region_id TYPE integer;
ALTER TABLE us_states ALTER COLUMN state_id TYPE integer;