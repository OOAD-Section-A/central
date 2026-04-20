-- Seed data for Packing subsystem demo/testing
-- Creates products, orders, and order_items so packing UI can load real DB-backed orders.
-- Safe to run multiple times.

USE OOAD;

-- ------------------------------------------------------------
-- Reset previously seeded packing demo rows before re-seeding
-- ------------------------------------------------------------
-- Wipe all packing-related data so the script always starts clean.
-- Temporarily disabling FK checks avoids parent/child delete ordering issues.
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE packaging_jobs;
TRUNCATE TABLE order_items;
TRUNCATE TABLE orders;
TRUNCATE TABLE products;
SET FOREIGN_KEY_CHECKS = 1;

-- ------------------------------------------------------------
-- Product master rows used for item descriptions in packing UI
-- ------------------------------------------------------------
INSERT INTO products (
    product_id,
    product_name,
    sku,
    category,
    sub_category,
    supplier_id,
    unit_of_measure
) VALUES
    ('PROD-PACK-001', 'Crystal Glass Set (Fragile)', 'SKU-PACK-001', 'Home', 'Glassware', 'SUP-PACK-01', 'PCS'),
    ('PROD-PACK-002', 'Ceramic Dinner Plate (Fragile)', 'SKU-PACK-002', 'Home', 'Tableware', 'SUP-PACK-01', 'PCS'),
    ('PROD-PACK-003', 'Wireless Mouse', 'SKU-PACK-003', 'Electronics', 'Accessories', 'SUP-PACK-02', 'PCS'),
    ('PROD-PACK-004', '27-inch Monitor (Fragile)', 'SKU-PACK-004', 'Electronics', 'Display', 'SUP-PACK-02', 'PCS'),
    ('PROD-PACK-005', 'Cotton T-Shirt', 'SKU-PACK-005', 'Apparel', 'Clothing', 'SUP-PACK-03', 'PCS')
ON DUPLICATE KEY UPDATE
    product_name = VALUES(product_name),
    category = VALUES(category),
    sub_category = VALUES(sub_category),
    supplier_id = VALUES(supplier_id),
    unit_of_measure = VALUES(unit_of_measure);

-- ------------------------------------------------------------
-- Orders ready for packing
-- Order distribution for strategy demo:
--   2001 => all fragile, 2002 => all non-fragile, 2003+ => mixed.
-- ------------------------------------------------------------
INSERT INTO orders (
    order_id,
    customer_id,
    order_status,
    order_date,
    total_amount,
    payment_status,
    sales_channel
) VALUES
    ('ORD-PACK-2001', 'CUST-PACK-101', 'CONFIRMED', NOW(), 2650.00, 'PAID', 'ONLINE'),
    ('ORD-PACK-2002', 'CUST-PACK-102', 'CONFIRMED', NOW(), 1599.00, 'PAID', 'ONLINE'),
    ('ORD-PACK-2003', 'CUST-PACK-202', 'CONFIRMED', NOW(), 899.00, 'PAID', 'POS'),
    ('ORD-PACK-2004', 'CUST-PACK-303', 'CONFIRMED', NOW(), 2700.00, 'PAID', 'ONLINE'),
    ('ORD-PACK-2005', 'CUST-PACK-303', 'CONFIRMED', NOW(), 999.00, 'PAID', 'ONLINE'),
    ('ORD-PACK-2006', 'CUST-PACK-404', 'CONFIRMED', NOW(), 1000.00, 'PAID', 'POS'),
    ('ORD-PACK-2007', 'CUST-PACK-505', 'CONFIRMED', NOW(), 3448.00, 'PAID', 'ONLINE'),
    ('ORD-PACK-2008', 'CUST-PACK-606', 'CONFIRMED', NOW(), 1598.00, 'PAID', 'ONLINE')
ON DUPLICATE KEY UPDATE
    order_status = VALUES(order_status),
    total_amount = VALUES(total_amount),
    payment_status = VALUES(payment_status),
    sales_channel = VALUES(sales_channel),
    order_date = VALUES(order_date);

-- ------------------------------------------------------------
-- Order line items consumed by SCMDatabaseAdapter.loadOrders()
-- ------------------------------------------------------------
INSERT INTO order_items (
    order_item_id,
    order_id,
    product_id,
    ordered_quantity,
    unit_price,
    line_total
) VALUES
    ('ITEM-PACK-2001-01', 'ORD-PACK-2001', 'PROD-PACK-001', 2, 500.00, 1000.00),
    ('ITEM-PACK-2001-02', 'ORD-PACK-2001', 'PROD-PACK-004', 1, 1200.00, 1200.00),
    ('ITEM-PACK-2001-03', 'ORD-PACK-2001', 'PROD-PACK-002', 1, 450.00, 450.00),

    ('ITEM-PACK-2002-01', 'ORD-PACK-2002', 'PROD-PACK-003', 2, 600.00, 1200.00),
    ('ITEM-PACK-2002-02', 'ORD-PACK-2002', 'PROD-PACK-005', 1, 399.00, 399.00),

    ('ITEM-PACK-2003-01', 'ORD-PACK-2003', 'PROD-PACK-002', 1, 450.00, 450.00),
    ('ITEM-PACK-2003-02', 'ORD-PACK-2003', 'PROD-PACK-005', 1, 449.00, 449.00),

    ('ITEM-PACK-2004-01', 'ORD-PACK-2004', 'PROD-PACK-001', 1, 500.00, 500.00),
    ('ITEM-PACK-2004-02', 'ORD-PACK-2004', 'PROD-PACK-004', 1, 1200.00, 1200.00),
    ('ITEM-PACK-2004-03', 'ORD-PACK-2004', 'PROD-PACK-003', 1, 600.00, 600.00),
    ('ITEM-PACK-2004-04', 'ORD-PACK-2004', 'PROD-PACK-005', 1, 400.00, 400.00),

    ('ITEM-PACK-2005-01', 'ORD-PACK-2005', 'PROD-PACK-002', 1, 450.00, 450.00),
    ('ITEM-PACK-2005-02', 'ORD-PACK-2005', 'PROD-PACK-005', 1, 549.00, 549.00),

    ('ITEM-PACK-2006-01', 'ORD-PACK-2006', 'PROD-PACK-003', 1, 600.00, 600.00),
    ('ITEM-PACK-2006-02', 'ORD-PACK-2006', 'PROD-PACK-001', 1, 400.00, 400.00),

    ('ITEM-PACK-2007-01', 'ORD-PACK-2007', 'PROD-PACK-004', 2, 1200.00, 2400.00),
    ('ITEM-PACK-2007-02', 'ORD-PACK-2007', 'PROD-PACK-003', 1, 600.00, 600.00),
    ('ITEM-PACK-2007-03', 'ORD-PACK-2007', 'PROD-PACK-005', 1, 448.00, 448.00),

    ('ITEM-PACK-2008-01', 'ORD-PACK-2008', 'PROD-PACK-002', 2, 450.00, 900.00),
    ('ITEM-PACK-2008-02', 'ORD-PACK-2008', 'PROD-PACK-005', 2, 349.00, 698.00)
ON DUPLICATE KEY UPDATE
    ordered_quantity = VALUES(ordered_quantity),
    unit_price = VALUES(unit_price),
    line_total = VALUES(line_total),
    product_id = VALUES(product_id),
    order_id = VALUES(order_id);

-- Quick sanity checks
SELECT order_id, customer_id, order_status, total_amount FROM orders WHERE order_id LIKE 'ORD-PACK-%';
SELECT order_item_id, order_id, product_id, ordered_quantity FROM order_items WHERE order_id LIKE 'ORD-PACK-%';
