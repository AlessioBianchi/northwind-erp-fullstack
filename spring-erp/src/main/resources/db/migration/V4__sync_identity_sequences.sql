-- ============================================
-- Sync identity sequences with existing data
-- V2 added IDENTITY to populated tables, so the
-- sequences started at 1 and collided with seed ids.
-- ============================================

SELECT setval(pg_get_serial_sequence('categories', 'category_id'), (SELECT MAX(category_id) FROM categories));
SELECT setval(pg_get_serial_sequence('employees', 'employee_id'), (SELECT MAX(employee_id) FROM employees));
SELECT setval(pg_get_serial_sequence('orders', 'order_id'), (SELECT MAX(order_id) FROM orders));
SELECT setval(pg_get_serial_sequence('products', 'product_id'), (SELECT MAX(product_id) FROM products));
SELECT setval(pg_get_serial_sequence('shippers', 'shipper_id'), (SELECT MAX(shipper_id) FROM shippers));
SELECT setval(pg_get_serial_sequence('suppliers', 'supplier_id'), (SELECT MAX(supplier_id) FROM suppliers));
