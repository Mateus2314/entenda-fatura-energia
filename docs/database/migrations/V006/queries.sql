-- ============================================================================
-- ELECTRICITY_BILLS TABLE - SQL QUERIES REFERENCE
-- Migration: V006
-- Purpose: Common queries for electricity bills management
-- ============================================================================

-- ============================================================================
-- SECTION 1: BASIC CRUD OPERATIONS
-- ============================================================================

-- -----------------------------------------------------------------------------
-- 1.1 INSERT - Create new electricity bill
-- -----------------------------------------------------------------------------
INSERT INTO electricity_bills (
    client_id,
    consultant_id,
    tariff_id,
    reference_month,
    due_date,
    total_amount,
    consumption_kwh,
    pdf_url,
    installation_number,
    invoice_number
) VALUES (
    '550e8400-e29b-41d4-a716-446655440000',
    NULL,
    'c1234567-89ab-cdef-0123-456789abcdef',
    '2025-12-01',
    '2025-12-20',
    285.50,
    350.00,
    '/uploads/bills/2025/12/bill-uuid.pdf',
    '123456789',
    'FAT-2025-12-001'
);

-- -----------------------------------------------------------------------------
-- 1.2 SELECT - Get all bills
-- -----------------------------------------------------------------------------
SELECT
    id,
    client_id,
    consultant_id,
    tariff_id,
    reference_month,
    due_date,
    total_amount,
    consumption_kwh,
    installation_number,
    invoice_number,
    created_at,
    updated_at
FROM electricity_bills
ORDER BY reference_month DESC, created_at DESC;

-- -----------------------------------------------------------------------------
-- 1.3 SELECT - Get bill by ID
-- -----------------------------------------------------------------------------
SELECT * FROM electricity_bills WHERE id = 'uuid-here';

-- -----------------------------------------------------------------------------
-- 1.4 UPDATE - Update bill information
-- -----------------------------------------------------------------------------
UPDATE electricity_bills
SET
    total_amount = 300.00,
    consumption_kwh = 375.00,
    updated_at = NOW()
WHERE id = 'uuid-here';

-- -----------------------------------------------------------------------------
-- 1.5 UPDATE - Assign consultant to bill
-- -----------------------------------------------------------------------------
UPDATE electricity_bills
SET consultant_id = 'consultant-uuid-here'
WHERE id = 'bill-uuid-here';

-- -----------------------------------------------------------------------------
-- 1.6 DELETE - Remove bill
-- -----------------------------------------------------------------------------
DELETE FROM electricity_bills WHERE id = 'uuid-here';

-- ============================================================================
-- SECTION 2: CLIENT QUERIES
-- ============================================================================

-- -----------------------------------------------------------------------------
-- 2.1 Get all bills for a client
-- -----------------------------------------------------------------------------
SELECT
    eb.*
FROM electricity_bills eb
WHERE eb.client_id = 'client-uuid-here'
ORDER BY eb.reference_month DESC;

-- -----------------------------------------------------------------------------
-- 2.2 Get client's bills for specific year
-- -----------------------------------------------------------------------------
SELECT
    eb.id,
    eb.reference_month,
    eb.total_amount,
    eb.consumption_kwh,
    (eb.total_amount / NULLIF(eb.consumption_kwh, 0)) AS cost_per_kwh
FROM electricity_bills eb
WHERE
    eb.client_id = 'client-uuid-here'
    AND EXTRACT(YEAR FROM eb.reference_month) = 2025
ORDER BY eb.reference_month ASC;

-- -----------------------------------------------------------------------------
-- 2.3 Get client's latest bill
-- -----------------------------------------------------------------------------
SELECT *
FROM electricity_bills
WHERE client_id = 'client-uuid-here'
ORDER BY reference_month DESC
LIMIT 1;

-- -----------------------------------------------------------------------------
-- 2.4 Get client's bills with total consumption and cost
-- -----------------------------------------------------------------------------
SELECT
    client_id,
    COUNT(*) AS total_bills,
    SUM(consumption_kwh) AS total_consumption,
    SUM(total_amount) AS total_cost,
    AVG(consumption_kwh) AS avg_consumption,
    AVG(total_amount) AS avg_cost
FROM electricity_bills
WHERE client_id = 'client-uuid-here'
GROUP BY client_id;

-- -----------------------------------------------------------------------------
-- 2.5 Compare client's consumption over months
-- -----------------------------------------------------------------------------
SELECT
    reference_month,
    consumption_kwh,
    total_amount,
    LAG(consumption_kwh) OVER (ORDER BY reference_month) AS prev_month_consumption,
    consumption_kwh - LAG(consumption_kwh) OVER (ORDER BY reference_month) AS consumption_diff,
    ROUND(
        ((consumption_kwh - LAG(consumption_kwh) OVER (ORDER BY reference_month)) /
         NULLIF(LAG(consumption_kwh) OVER (ORDER BY reference_month), 0)) * 100,
        2
    ) AS pct_change
FROM electricity_bills
WHERE client_id = 'client-uuid-here'
ORDER BY reference_month ASC;

-- ============================================================================
-- SECTION 3: CONSULTANT QUERIES
-- ============================================================================

-- -----------------------------------------------------------------------------
-- 3.1 Get all bills assigned to consultant
-- -----------------------------------------------------------------------------
SELECT
    eb.*
FROM electricity_bills eb
WHERE eb.consultant_id = 'consultant-uuid-here'
ORDER BY eb.reference_month DESC;

-- -----------------------------------------------------------------------------
-- 3.2 Get consultant's workload (bills per month)
-- -----------------------------------------------------------------------------
SELECT
    DATE_TRUNC('month', reference_month) AS month,
    COUNT(*) AS bills_count,
    SUM(total_amount) AS total_revenue
FROM electricity_bills
WHERE consultant_id = 'consultant-uuid-here'
GROUP BY month
ORDER BY month DESC;

-- -----------------------------------------------------------------------------
-- 3.3 Get bills without consultant assigned
-- -----------------------------------------------------------------------------
SELECT
    eb.id,
    eb.client_id,
    eb.reference_month,
    eb.total_amount,
    eb.created_at
FROM electricity_bills eb
WHERE eb.consultant_id IS NULL
ORDER BY eb.created_at ASC;

-- -----------------------------------------------------------------------------
-- 3.4 Count bills by consultant
-- -----------------------------------------------------------------------------
SELECT
    c.user_id,
    u.name AS consultant_name,
    COUNT(eb.id) AS bills_count
FROM consultants c
INNER JOIN users u ON c.user_id = u.id
LEFT JOIN electricity_bills eb ON c.user_id = eb.consultant_id
GROUP BY c.user_id, u.name
ORDER BY bills_count DESC;

-- ============================================================================
-- SECTION 4: TARIFF QUERIES
-- ============================================================================

-- -----------------------------------------------------------------------------
-- 4.1 Get bills using specific tariff
-- -----------------------------------------------------------------------------
SELECT
    eb.*
FROM electricity_bills eb
WHERE eb.tariff_id = 'tariff-uuid-here'
ORDER BY eb.reference_month DESC;

-- -----------------------------------------------------------------------------
-- 4.2 Count bills by tariff
-- -----------------------------------------------------------------------------
SELECT
    tariff_id,
    COUNT(*) AS bills_count,
    SUM(consumption_kwh) AS total_consumption,
    AVG(total_amount / NULLIF(consumption_kwh, 0)) AS avg_cost_per_kwh
FROM electricity_bills
GROUP BY tariff_id
ORDER BY bills_count DESC;

-- -----------------------------------------------------------------------------
-- 4.3 Verify tariff is in use (before deletion)
-- -----------------------------------------------------------------------------
SELECT EXISTS(
    SELECT 1 FROM electricity_bills WHERE tariff_id = 'tariff-uuid-here'
) AS tariff_in_use;

-- ============================================================================
-- SECTION 5: DATE-BASED QUERIES
-- ============================================================================

-- -----------------------------------------------------------------------------
-- 5.1 Get bills for specific month
-- -----------------------------------------------------------------------------
SELECT *
FROM electricity_bills
WHERE reference_month = '2025-12-01'
ORDER BY client_id;

-- -----------------------------------------------------------------------------
-- 5.2 Get overdue bills (past due_date)
-- -----------------------------------------------------------------------------
SELECT
    eb.id,
    eb.client_id,
    eb.reference_month,
    eb.due_date,
    eb.total_amount,
    (CURRENT_DATE - eb.due_date) AS days_overdue
FROM electricity_bills eb
WHERE eb.due_date < CURRENT_DATE
ORDER BY eb.due_date ASC;

-- -----------------------------------------------------------------------------
-- 5.3 Get bills due in next N days
-- -----------------------------------------------------------------------------
SELECT
    eb.id,
    eb.client_id,
    eb.reference_month,
    eb.due_date,
    eb.total_amount,
    (eb.due_date - CURRENT_DATE) AS days_until_due
FROM electricity_bills eb
WHERE
    eb.due_date >= CURRENT_DATE
    AND eb.due_date <= (CURRENT_DATE + INTERVAL '7 days')
ORDER BY eb.due_date ASC;

-- -----------------------------------------------------------------------------
-- 5.4 Get bills created in date range
-- -----------------------------------------------------------------------------
SELECT *
FROM electricity_bills
WHERE created_at BETWEEN '2025-12-01' AND '2025-12-31'
ORDER BY created_at DESC;

-- -----------------------------------------------------------------------------
-- 5.5 Monthly bill statistics
-- -----------------------------------------------------------------------------
SELECT
    DATE_TRUNC('month', reference_month) AS month,
    COUNT(*) AS bills_count,
    SUM(total_amount) AS total_revenue,
    SUM(consumption_kwh) AS total_consumption,
    AVG(total_amount) AS avg_bill_amount,
    AVG(consumption_kwh) AS avg_consumption
FROM electricity_bills
GROUP BY month
ORDER BY month DESC;

-- ============================================================================
-- SECTION 6: ANALYTICAL QUERIES
-- ============================================================================

-- -----------------------------------------------------------------------------
-- 6.1 Calculate cost per kWh for all bills
-- -----------------------------------------------------------------------------
SELECT
    id,
    client_id,
    reference_month,
    total_amount,
    consumption_kwh,
    ROUND(total_amount / NULLIF(consumption_kwh, 0), 4) AS cost_per_kwh
FROM electricity_bills
WHERE consumption_kwh > 0
ORDER BY cost_per_kwh DESC;

-- -----------------------------------------------------------------------------
-- 6.2 Find high consumption bills (top 10)
-- -----------------------------------------------------------------------------
SELECT
    eb.id,
    eb.client_id,
    eb.reference_month,
    eb.consumption_kwh,
    eb.total_amount
FROM electricity_bills eb
ORDER BY eb.consumption_kwh DESC
LIMIT 10;

-- -----------------------------------------------------------------------------
-- 6.3 Find high cost bills (top 10)
-- -----------------------------------------------------------------------------
SELECT
    eb.id,
    eb.client_id,
    eb.reference_month,
    eb.total_amount,
    eb.consumption_kwh
FROM electricity_bills eb
ORDER BY eb.total_amount DESC
LIMIT 10;

-- -----------------------------------------------------------------------------
-- 6.4 Average consumption by client
-- -----------------------------------------------------------------------------
SELECT
    client_id,
    COUNT(*) AS bills_count,
    AVG(consumption_kwh) AS avg_consumption,
    MIN(consumption_kwh) AS min_consumption,
    MAX(consumption_kwh) AS max_consumption,
    STDDEV(consumption_kwh) AS stddev_consumption
FROM electricity_bills
GROUP BY client_id
HAVING COUNT(*) >= 3
ORDER BY avg_consumption DESC;

-- -----------------------------------------------------------------------------
-- 6.5 Year-over-year consumption comparison
-- -----------------------------------------------------------------------------
SELECT
    client_id,
    EXTRACT(YEAR FROM reference_month) AS year,
    SUM(consumption_kwh) AS annual_consumption,
    SUM(total_amount) AS annual_cost
FROM electricity_bills
GROUP BY client_id, year
ORDER BY client_id, year;

-- ============================================================================
-- SECTION 7: JOIN QUERIES (with related tables)
-- ============================================================================

-- -----------------------------------------------------------------------------
-- 7.1 Get bills with client details
-- -----------------------------------------------------------------------------
SELECT
    eb.id,
    eb.reference_month,
    eb.total_amount,
    eb.consumption_kwh,
    u.name AS client_name,
    u.email AS client_email,
    c.cpf AS client_cpf
FROM electricity_bills eb
INNER JOIN clients c ON eb.client_id = c.user_id
INNER JOIN users u ON c.user_id = u.id
ORDER BY eb.reference_month DESC;

-- -----------------------------------------------------------------------------
-- 7.2 Get bills with consultant details
-- -----------------------------------------------------------------------------
SELECT
    eb.id,
    eb.reference_month,
    eb.total_amount,
    u.name AS consultant_name,
    cons.company AS consultant_company
FROM electricity_bills eb
INNER JOIN consultants cons ON eb.consultant_id = cons.user_id
INNER JOIN users u ON cons.user_id = u.id
ORDER BY eb.reference_month DESC;

-- -----------------------------------------------------------------------------
-- 7.3 Get bills with tariff details
-- -----------------------------------------------------------------------------
SELECT
    eb.id,
    eb.reference_month,
    eb.consumption_kwh,
    eb.total_amount,
    t.distributor,
    t.tariff_modality,
    t.subgroup,
    t.tusd_value,
    t.te_value
FROM electricity_bills eb
INNER JOIN tariffs t ON eb.tariff_id = t.id
ORDER BY eb.reference_month DESC;

-- -----------------------------------------------------------------------------
-- 7.4 Complete bill view (all relationships)
-- -----------------------------------------------------------------------------
SELECT
    eb.id AS bill_id,
    eb.reference_month,
    eb.due_date,
    eb.total_amount,
    eb.consumption_kwh,
    uc.name AS client_name,
    uc.email AS client_email,
    ucons.name AS consultant_name,
    t.distributor AS tariff_distributor,
    t.tariff_modality
FROM electricity_bills eb
INNER JOIN clients c ON eb.client_id = c.user_id
INNER JOIN users uc ON c.user_id = uc.id
LEFT JOIN consultants cons ON eb.consultant_id = cons.user_id
LEFT JOIN users ucons ON cons.user_id = ucons.id
INNER JOIN tariffs t ON eb.tariff_id = t.id
ORDER BY eb.reference_month DESC;

-- ============================================================================
-- SECTION 8: DATA VALIDATION QUERIES
-- ============================================================================

-- -----------------------------------------------------------------------------
-- 8.1 Find bills with invalid dates (should return 0 rows)
-- -----------------------------------------------------------------------------
SELECT
    id,
    reference_month,
    due_date
FROM electricity_bills
WHERE due_date <= reference_month;

-- -----------------------------------------------------------------------------
-- 8.2 Find bills with negative values (should return 0 rows)
-- -----------------------------------------------------------------------------
SELECT
    id,
    total_amount,
    consumption_kwh
FROM electricity_bills
WHERE
    total_amount < 0
    OR consumption_kwh < 0;

-- -----------------------------------------------------------------------------
-- 8.3 Find orphaned bills (invalid foreign keys)
-- -----------------------------------------------------------------------------
-- Bills with non-existent clients
SELECT eb.id, eb.client_id
FROM electricity_bills eb
LEFT JOIN clients c ON eb.client_id = c.user_id
WHERE c.user_id IS NULL;

-- Bills with non-existent consultants (should be NULL, not invalid)
SELECT eb.id, eb.consultant_id
FROM electricity_bills eb
LEFT JOIN consultants c ON eb.consultant_id = c.user_id
WHERE eb.consultant_id IS NOT NULL AND c.user_id IS NULL;

-- Bills with non-existent tariffs
SELECT eb.id, eb.tariff_id
FROM electricity_bills eb
LEFT JOIN tariffs t ON eb.tariff_id = t.id
WHERE t.id IS NULL;

-- -----------------------------------------------------------------------------
-- 8.4 Find duplicate bills (same client + reference_month)
-- -----------------------------------------------------------------------------
SELECT
    client_id,
    reference_month,
    COUNT(*) AS duplicate_count
FROM electricity_bills
GROUP BY client_id, reference_month
HAVING COUNT(*) > 1;

-- -----------------------------------------------------------------------------
-- 8.5 Find bills with missing installation or invoice numbers
-- -----------------------------------------------------------------------------
SELECT
    id,
    client_id,
    reference_month,
    installation_number,
    invoice_number
FROM electricity_bills
WHERE
    installation_number IS NULL
    OR invoice_number IS NULL;

-- ============================================================================
-- SECTION 9: REPOSITORY PATTERN QUERIES (for Spring Data JPA)
-- ============================================================================

-- -----------------------------------------------------------------------------
-- 9.1 findByClientId
-- -----------------------------------------------------------------------------
SELECT * FROM electricity_bills
WHERE client_id = ?
ORDER BY reference_month DESC;

-- -----------------------------------------------------------------------------
-- 9.2 findByConsultantId
-- -----------------------------------------------------------------------------
SELECT * FROM electricity_bills
WHERE consultant_id = ?
ORDER BY reference_month DESC;

-- -----------------------------------------------------------------------------
-- 9.3 findByReferenceMonth
-- -----------------------------------------------------------------------------
SELECT * FROM electricity_bills
WHERE reference_month = ?
ORDER BY client_id;

-- -----------------------------------------------------------------------------
-- 9.4 findByClientIdAndReferenceMonth
-- -----------------------------------------------------------------------------
SELECT * FROM electricity_bills
WHERE client_id = ? AND reference_month = ?;

-- -----------------------------------------------------------------------------
-- 9.5 findByDueDateBefore (overdue bills)
-- -----------------------------------------------------------------------------
SELECT * FROM electricity_bills
WHERE due_date < ?
ORDER BY due_date ASC;

-- -----------------------------------------------------------------------------
-- 9.6 findByClientIdAndReferenceMonthBetween
-- -----------------------------------------------------------------------------
SELECT * FROM electricity_bills
WHERE
    client_id = ?
    AND reference_month BETWEEN ? AND ?
ORDER BY reference_month ASC;

-- -----------------------------------------------------------------------------
-- 9.7 findByConsumptionKwhGreaterThan
-- -----------------------------------------------------------------------------
SELECT * FROM electricity_bills
WHERE consumption_kwh > ?
ORDER BY consumption_kwh DESC;

-- ============================================================================
-- SECTION 10: MAINTENANCE QUERIES
-- ============================================================================

-- -----------------------------------------------------------------------------
-- 10.1 Get table statistics
-- -----------------------------------------------------------------------------
SELECT
    COUNT(*) AS total_bills,
    COUNT(DISTINCT client_id) AS unique_clients,
    COUNT(DISTINCT consultant_id) AS unique_consultants,
    COUNT(DISTINCT tariff_id) AS unique_tariffs,
    MIN(reference_month) AS oldest_bill_month,
    MAX(reference_month) AS newest_bill_month,
    SUM(total_amount) AS total_revenue,
    SUM(consumption_kwh) AS total_consumption,
    AVG(total_amount) AS avg_bill_amount,
    AVG(consumption_kwh) AS avg_consumption
FROM electricity_bills;

-- -----------------------------------------------------------------------------
-- 10.2 Check index usage
-- -----------------------------------------------------------------------------
SELECT
    schemaname,
    tablename,
    indexname,
    idx_scan AS index_scans,
    idx_tup_read AS tuples_read,
    idx_tup_fetch AS tuples_fetched
FROM pg_stat_user_indexes
WHERE tablename = 'electricity_bills'
ORDER BY idx_scan DESC;

-- -----------------------------------------------------------------------------
-- 10.3 Find bills without PDF
-- -----------------------------------------------------------------------------
SELECT
    id,
    client_id,
    reference_month,
    total_amount
FROM electricity_bills
WHERE pdf_url IS NULL
ORDER BY reference_month DESC;

-- -----------------------------------------------------------------------------
-- 10.4 Archive old bills (example - execute with caution)
-- -----------------------------------------------------------------------------
-- DELETE FROM electricity_bills
-- WHERE reference_month < (CURRENT_DATE - INTERVAL '5 years');

-- ============================================================================
-- END OF QUERIES
-- ============================================================================

