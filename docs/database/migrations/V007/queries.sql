-- ============================================================================
-- BILL_ITEMS TABLE - SQL QUERIES REFERENCE
-- Migration: V007
-- Purpose: Common queries for bill items management
-- ============================================================================

-- ============================================================================
-- SECTION 1: BASIC CRUD OPERATIONS
-- ============================================================================

-- -----------------------------------------------------------------------------
-- 1.1 INSERT - Create new bill item
-- -----------------------------------------------------------------------------
INSERT INTO bill_items (
    bill_id,
    item_type,
    description,
    quantity,
    unit_price,
    amount
) VALUES (
    'bill-uuid-here',
    'CONSUMPTION_PEAK',
    'Consumo Ponta - 150 kWh',
    150.00,
    0.9523,
    142.85
);

-- -----------------------------------------------------------------------------
-- 1.2 INSERT - Multiple items for a bill
-- -----------------------------------------------------------------------------
INSERT INTO bill_items (bill_id, item_type, description, quantity, unit_price, amount) VALUES
('bill-uuid', 'CONSUMPTION_PEAK', 'Consumo Ponta - 150 kWh', 150.00, 0.9523, 142.85),
('bill-uuid', 'CONSUMPTION_OFF_PEAK', 'Consumo Fora Ponta - 200 kWh', 200.00, 0.4521, 90.42),
('bill-uuid', 'DEMAND', 'Demanda Contratada - 50 kW', 50.00, 12.50, 625.00),
('bill-uuid', 'TARIFF_FLAG', 'Bandeira Vermelha P1', 350.00, 0.04169, 14.59),
('bill-uuid', 'PUBLIC_LIGHTING', 'CIP - Contrib. Ilum. Pública', NULL, NULL, 35.00),
('bill-uuid', 'TAXES', 'ICMS (27%)', NULL, NULL, 66.84);

-- -----------------------------------------------------------------------------
-- 1.3 SELECT - Get all items
-- -----------------------------------------------------------------------------
SELECT
    id,
    bill_id,
    item_type,
    description,
    quantity,
    unit_price,
    amount,
    created_at,
    updated_at
FROM bill_items
ORDER BY created_at DESC;

-- -----------------------------------------------------------------------------
-- 1.4 SELECT - Get item by ID
-- -----------------------------------------------------------------------------
SELECT * FROM bill_items WHERE id = 'uuid-here';

-- -----------------------------------------------------------------------------
-- 1.5 UPDATE - Update item
-- -----------------------------------------------------------------------------
UPDATE bill_items
SET
    quantity = 160.00,
    amount = 152.37,
    updated_at = NOW()
WHERE id = 'uuid-here';

-- -----------------------------------------------------------------------------
-- 1.6 DELETE - Remove item
-- -----------------------------------------------------------------------------
DELETE FROM bill_items WHERE id = 'uuid-here';

-- -----------------------------------------------------------------------------
-- 1.7 DELETE - Remove all items for a bill
-- -----------------------------------------------------------------------------
DELETE FROM bill_items WHERE bill_id = 'bill-uuid-here';

-- ============================================================================
-- SECTION 2: BILL-RELATED QUERIES
-- ============================================================================

-- -----------------------------------------------------------------------------
-- 2.1 Get all items for a specific bill
-- -----------------------------------------------------------------------------
SELECT
    bi.*
FROM bill_items bi
WHERE bi.bill_id = 'bill-uuid-here'
ORDER BY bi.item_type, bi.created_at;

-- -----------------------------------------------------------------------------
-- 2.2 Get items grouped by type
-- -----------------------------------------------------------------------------
SELECT
    bi.item_type,
    COUNT(*) AS item_count,
    SUM(bi.amount) AS total_amount
FROM bill_items bi
WHERE bi.bill_id = 'bill-uuid-here'
GROUP BY bi.item_type
ORDER BY bi.item_type;

-- -----------------------------------------------------------------------------
-- 2.3 Calculate bill total from items
-- -----------------------------------------------------------------------------
SELECT
    bi.bill_id,
    SUM(bi.amount) AS calculated_total,
    COUNT(*) AS item_count
FROM bill_items bi
WHERE bi.bill_id = 'bill-uuid-here'
GROUP BY bi.bill_id;

-- -----------------------------------------------------------------------------
-- 2.4 Verify bill total matches sum of items
-- -----------------------------------------------------------------------------
SELECT
    eb.id AS bill_id,
    eb.total_amount AS bill_total,
    COALESCE(SUM(bi.amount), 0) AS items_total,
    eb.total_amount - COALESCE(SUM(bi.amount), 0) AS difference
FROM electricity_bills eb
LEFT JOIN bill_items bi ON eb.id = bi.bill_id
WHERE eb.id = 'bill-uuid-here'
GROUP BY eb.id, eb.total_amount;

-- -----------------------------------------------------------------------------
-- 2.5 Get bills with their item count
-- -----------------------------------------------------------------------------
SELECT
    eb.id,
    eb.reference_month,
    eb.total_amount,
    COUNT(bi.id) AS item_count
FROM electricity_bills eb
LEFT JOIN bill_items bi ON eb.id = bi.bill_id
GROUP BY eb.id, eb.reference_month, eb.total_amount
ORDER BY eb.reference_month DESC;

-- ============================================================================
-- SECTION 3: ITEM TYPE QUERIES
-- ============================================================================

-- -----------------------------------------------------------------------------
-- 3.1 Get consumption items only
-- -----------------------------------------------------------------------------
SELECT
    bi.*
FROM bill_items bi
WHERE
    bi.bill_id = 'bill-uuid-here'
    AND bi.item_type IN ('CONSUMPTION_PEAK', 'CONSUMPTION_OFF_PEAK', 'CONSUMPTION_STANDARD')
ORDER BY bi.item_type;

-- -----------------------------------------------------------------------------
-- 3.2 Calculate total consumption (kWh)
-- -----------------------------------------------------------------------------
SELECT
    bi.bill_id,
    SUM(bi.quantity) AS total_kwh,
    SUM(bi.amount) AS total_consumption_cost
FROM bill_items bi
WHERE
    bi.bill_id = 'bill-uuid-here'
    AND bi.item_type IN ('CONSUMPTION_PEAK', 'CONSUMPTION_OFF_PEAK', 'CONSUMPTION_STANDARD')
GROUP BY bi.bill_id;

-- -----------------------------------------------------------------------------
-- 3.3 Get tax items
-- -----------------------------------------------------------------------------
SELECT
    bi.*
FROM bill_items bi
WHERE
    bi.bill_id = 'bill-uuid-here'
    AND bi.item_type = 'TAXES'
ORDER BY bi.amount DESC;

-- -----------------------------------------------------------------------------
-- 3.4 Get demand charges
-- -----------------------------------------------------------------------------
SELECT
    bi.*
FROM bill_items bi
WHERE
    bi.bill_id = 'bill-uuid-here'
    AND bi.item_type = 'DEMAND';

-- -----------------------------------------------------------------------------
-- 3.5 Get tariff flag charges
-- -----------------------------------------------------------------------------
SELECT
    bi.*
FROM bill_items bi
WHERE
    bi.bill_id = 'bill-uuid-here'
    AND bi.item_type = 'TARIFF_FLAG';

-- -----------------------------------------------------------------------------
-- 3.6 Count items by type across all bills
-- -----------------------------------------------------------------------------
SELECT
    item_type,
    COUNT(*) AS count,
    SUM(amount) AS total_amount,
    AVG(amount) AS avg_amount
FROM bill_items
GROUP BY item_type
ORDER BY count DESC;

-- ============================================================================
-- SECTION 4: ANALYTICAL QUERIES
-- ============================================================================

-- -----------------------------------------------------------------------------
-- 4.1 Calculate average unit price by item type
-- -----------------------------------------------------------------------------
SELECT
    item_type,
    COUNT(*) AS count,
    AVG(unit_price) AS avg_unit_price,
    MIN(unit_price) AS min_unit_price,
    MAX(unit_price) AS max_unit_price
FROM bill_items
WHERE unit_price IS NOT NULL
GROUP BY item_type
ORDER BY avg_unit_price DESC;

-- -----------------------------------------------------------------------------
-- 4.2 Find items with highest amounts
-- -----------------------------------------------------------------------------
SELECT
    bi.id,
    bi.bill_id,
    bi.item_type,
    bi.description,
    bi.amount
FROM bill_items bi
ORDER BY bi.amount DESC
LIMIT 10;

-- -----------------------------------------------------------------------------
-- 4.3 Compare peak vs off-peak consumption
-- -----------------------------------------------------------------------------
SELECT
    bill_id,
    SUM(CASE WHEN item_type = 'CONSUMPTION_PEAK' THEN quantity ELSE 0 END) AS peak_kwh,
    SUM(CASE WHEN item_type = 'CONSUMPTION_OFF_PEAK' THEN quantity ELSE 0 END) AS offpeak_kwh,
    SUM(CASE WHEN item_type = 'CONSUMPTION_PEAK' THEN amount ELSE 0 END) AS peak_cost,
    SUM(CASE WHEN item_type = 'CONSUMPTION_OFF_PEAK' THEN amount ELSE 0 END) AS offpeak_cost
FROM bill_items
WHERE item_type IN ('CONSUMPTION_PEAK', 'CONSUMPTION_OFF_PEAK')
GROUP BY bill_id;

-- -----------------------------------------------------------------------------
-- 4.4 Calculate tax percentage of total bill
-- -----------------------------------------------------------------------------
SELECT
    bi.bill_id,
    eb.total_amount AS bill_total,
    SUM(CASE WHEN bi.item_type = 'TAXES' THEN bi.amount ELSE 0 END) AS tax_amount,
    ROUND(
        (SUM(CASE WHEN bi.item_type = 'TAXES' THEN bi.amount ELSE 0 END) /
         NULLIF(eb.total_amount, 0)) * 100,
        2
    ) AS tax_percentage
FROM bill_items bi
INNER JOIN electricity_bills eb ON bi.bill_id = eb.id
GROUP BY bi.bill_id, eb.total_amount
ORDER BY tax_percentage DESC;

-- -----------------------------------------------------------------------------
-- 4.5 Find bills with missing item types
-- -----------------------------------------------------------------------------
SELECT
    eb.id AS bill_id,
    eb.reference_month,
    CASE WHEN SUM(CASE WHEN bi.item_type LIKE 'CONSUMPTION_%' THEN 1 ELSE 0 END) = 0
         THEN 'Missing consumption items'
         ELSE NULL END AS issue
FROM electricity_bills eb
LEFT JOIN bill_items bi ON eb.id = bi.bill_id
GROUP BY eb.id, eb.reference_month
HAVING SUM(CASE WHEN bi.item_type LIKE 'CONSUMPTION_%' THEN 1 ELSE 0 END) = 0;

-- ============================================================================
-- SECTION 5: VALIDATION QUERIES
-- ============================================================================

-- -----------------------------------------------------------------------------
-- 5.1 Find items with invalid calculations (quantity * unit_price ≠ amount)
-- -----------------------------------------------------------------------------
SELECT
    id,
    bill_id,
    description,
    quantity,
    unit_price,
    amount,
    (quantity * unit_price) AS calculated_amount,
    ABS(amount - (quantity * unit_price)) AS difference
FROM bill_items
WHERE
    quantity IS NOT NULL
    AND unit_price IS NOT NULL
    AND ABS(amount - (quantity * unit_price)) > 0.01
ORDER BY difference DESC;

-- -----------------------------------------------------------------------------
-- 5.2 Find orphaned items (bills don't exist)
-- -----------------------------------------------------------------------------
SELECT
    bi.id,
    bi.bill_id,
    bi.item_type
FROM bill_items bi
LEFT JOIN electricity_bills eb ON bi.bill_id = eb.id
WHERE eb.id IS NULL;

-- -----------------------------------------------------------------------------
-- 5.3 Find items with negative values (should return 0 rows)
-- -----------------------------------------------------------------------------
SELECT
    id,
    bill_id,
    item_type,
    quantity,
    unit_price,
    amount
FROM bill_items
WHERE
    (quantity IS NOT NULL AND quantity < 0)
    OR (unit_price IS NOT NULL AND unit_price < 0)
    OR amount < 0;

-- -----------------------------------------------------------------------------
-- 5.4 Find items with invalid item_type (should return 0 rows)
-- -----------------------------------------------------------------------------
SELECT
    id,
    item_type
FROM bill_items
WHERE item_type NOT IN (
    'CONSUMPTION_PEAK',
    'CONSUMPTION_OFF_PEAK',
    'CONSUMPTION_STANDARD',
    'DEMAND',
    'TARIFF_FLAG',
    'PUBLIC_LIGHTING',
    'TAXES',
    'OTHER'
);

-- -----------------------------------------------------------------------------
-- 5.5 Find bills where items total doesn't match bill total
-- -----------------------------------------------------------------------------
SELECT
    eb.id AS bill_id,
    eb.reference_month,
    eb.total_amount AS bill_total,
    COALESCE(SUM(bi.amount), 0) AS items_total,
    ABS(eb.total_amount - COALESCE(SUM(bi.amount), 0)) AS difference
FROM electricity_bills eb
LEFT JOIN bill_items bi ON eb.id = bi.bill_id
GROUP BY eb.id, eb.reference_month, eb.total_amount
HAVING ABS(eb.total_amount - COALESCE(SUM(bi.amount), 0)) > 0.01
ORDER BY difference DESC;

-- ============================================================================
-- SECTION 6: CLIENT ANALYTICAL QUERIES
-- ============================================================================

-- -----------------------------------------------------------------------------
-- 6.1 Get client's consumption breakdown by type
-- -----------------------------------------------------------------------------
SELECT
    eb.client_id,
    bi.item_type,
    COUNT(*) AS item_count,
    SUM(bi.quantity) AS total_quantity,
    SUM(bi.amount) AS total_amount,
    AVG(bi.unit_price) AS avg_unit_price
FROM bill_items bi
INNER JOIN electricity_bills eb ON bi.bill_id = eb.id
WHERE
    eb.client_id = 'client-uuid-here'
    AND bi.item_type LIKE 'CONSUMPTION_%'
GROUP BY eb.client_id, bi.item_type
ORDER BY bi.item_type;

-- -----------------------------------------------------------------------------
-- 6.2 Client's total taxes paid
-- -----------------------------------------------------------------------------
SELECT
    eb.client_id,
    SUM(bi.amount) AS total_taxes_paid,
    COUNT(DISTINCT eb.id) AS bills_count
FROM bill_items bi
INNER JOIN electricity_bills eb ON bi.bill_id = eb.id
WHERE
    eb.client_id = 'client-uuid-here'
    AND bi.item_type = 'TAXES'
GROUP BY eb.client_id;

-- -----------------------------------------------------------------------------
-- 6.3 Monthly consumption trend by type
-- -----------------------------------------------------------------------------
SELECT
    eb.reference_month,
    bi.item_type,
    SUM(bi.quantity) AS total_quantity,
    SUM(bi.amount) AS total_amount
FROM bill_items bi
INNER JOIN electricity_bills eb ON bi.bill_id = eb.id
WHERE
    eb.client_id = 'client-uuid-here'
    AND bi.item_type LIKE 'CONSUMPTION_%'
GROUP BY eb.reference_month, bi.item_type
ORDER BY eb.reference_month ASC, bi.item_type;

-- ============================================================================
-- SECTION 7: REPOSITORY PATTERN QUERIES (for Spring Data JPA)
-- ============================================================================

-- -----------------------------------------------------------------------------
-- 7.1 findByBillId
-- -----------------------------------------------------------------------------
SELECT * FROM bill_items
WHERE bill_id = ?
ORDER BY item_type, created_at;

-- -----------------------------------------------------------------------------
-- 7.2 findByBillIdAndItemType
-- -----------------------------------------------------------------------------
SELECT * FROM bill_items
WHERE bill_id = ? AND item_type = ?
ORDER BY created_at;

-- -----------------------------------------------------------------------------
-- 7.3 findByItemType
-- -----------------------------------------------------------------------------
SELECT * FROM bill_items
WHERE item_type = ?
ORDER BY created_at DESC;

-- -----------------------------------------------------------------------------
-- 7.4 countByBillId
-- -----------------------------------------------------------------------------
SELECT COUNT(*) FROM bill_items WHERE bill_id = ?;

-- -----------------------------------------------------------------------------
-- 7.5 sumAmountByBillId
-- -----------------------------------------------------------------------------
SELECT COALESCE(SUM(amount), 0) FROM bill_items WHERE bill_id = ?;

-- -----------------------------------------------------------------------------
-- 7.6 findConsumptionItemsByBillId
-- -----------------------------------------------------------------------------
SELECT * FROM bill_items
WHERE
    bill_id = ?
    AND item_type IN ('CONSUMPTION_PEAK', 'CONSUMPTION_OFF_PEAK', 'CONSUMPTION_STANDARD')
ORDER BY item_type;

-- -----------------------------------------------------------------------------
-- 7.7 deleteByBillId
-- -----------------------------------------------------------------------------
DELETE FROM bill_items WHERE bill_id = ?;

-- ============================================================================
-- SECTION 8: MAINTENANCE QUERIES
-- ============================================================================

-- -----------------------------------------------------------------------------
-- 8.1 Get table statistics
-- -----------------------------------------------------------------------------
SELECT
    COUNT(*) AS total_items,
    COUNT(DISTINCT bill_id) AS unique_bills,
    SUM(amount) AS total_amount,
    AVG(amount) AS avg_amount_per_item,
    MIN(created_at) AS oldest_item,
    MAX(created_at) AS newest_item
FROM bill_items;

-- -----------------------------------------------------------------------------
-- 8.2 Check index usage
-- -----------------------------------------------------------------------------
SELECT
    schemaname,
    tablename,
    indexname,
    idx_scan AS index_scans,
    idx_tup_read AS tuples_read,
    idx_tup_fetch AS tuples_fetched
FROM pg_stat_user_indexes
WHERE tablename = 'bill_items'
ORDER BY idx_scan DESC;

-- -----------------------------------------------------------------------------
-- 8.3 Find items without quantity or unit_price
-- -----------------------------------------------------------------------------
SELECT
    id,
    bill_id,
    item_type,
    description,
    quantity,
    unit_price,
    amount
FROM bill_items
WHERE quantity IS NULL OR unit_price IS NULL
ORDER BY item_type;

-- -----------------------------------------------------------------------------
-- 8.4 Items created in last N days
-- -----------------------------------------------------------------------------
SELECT
    id,
    bill_id,
    item_type,
    amount,
    created_at
FROM bill_items
WHERE created_at >= (CURRENT_DATE - INTERVAL '30 days')
ORDER BY created_at DESC;

-- ============================================================================
-- END OF QUERIES
-- ============================================================================

