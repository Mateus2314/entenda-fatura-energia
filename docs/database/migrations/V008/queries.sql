-- ============================================================================
-- ANALYSES TABLE - SQL QUERIES REFERENCE
-- Migration: V008
-- Purpose: Common queries for analyses management and validation
-- ============================================================================

-- ============================================================================
-- SECTION 1: BASIC CRUD OPERATIONS
-- ============================================================================

-- -----------------------------------------------------------------------------
-- 1.1 INSERT - Create new analysis
-- -----------------------------------------------------------------------------
INSERT INTO analyses (
    bill_id,
    average_consumption,
    cost_per_kwh,
    comparison_prev_month,
    savings_tips,
    report_pdf_url
) VALUES (
    'bill-uuid-here',
    275.50,
    0.8234,
    12.5,
    'Seu consumo aumentou 12.5% em relação ao mês anterior. Considere reduzir o uso de ar-condicionado durante o horário de pico.',
    '/reports/analysis-2025-12-abc123.pdf'
);

-- -----------------------------------------------------------------------------
-- 1.2 SELECT - Get all analyses
-- -----------------------------------------------------------------------------
SELECT
    id,
    bill_id,
    average_consumption,
    cost_per_kwh,
    comparison_prev_month,
    savings_tips,
    report_pdf_url,
    created_at
FROM analyses
ORDER BY created_at DESC;

-- -----------------------------------------------------------------------------
-- 1.3 SELECT - Get analysis by ID
-- -----------------------------------------------------------------------------
SELECT * FROM analyses WHERE id = 'uuid-here';

-- -----------------------------------------------------------------------------
-- 1.4 SELECT - Get analysis by bill_id (One-to-One lookup)
-- -----------------------------------------------------------------------------
SELECT * FROM analyses WHERE bill_id = 'bill-uuid-here';

-- -----------------------------------------------------------------------------
-- 1.5 DELETE - Remove analysis
-- -----------------------------------------------------------------------------
DELETE FROM analyses WHERE id = 'uuid-here';

-- -----------------------------------------------------------------------------
-- 1.6 DELETE - Remove analysis by bill_id
-- -----------------------------------------------------------------------------
DELETE FROM analyses WHERE bill_id = 'bill-uuid-here';

-- ============================================================================
-- SECTION 2: RELATIONSHIP QUERIES
-- ============================================================================

-- -----------------------------------------------------------------------------
-- 2.1 Get analysis with bill details (JOIN)
-- -----------------------------------------------------------------------------
SELECT
    a.id AS analysis_id,
    a.average_consumption,
    a.cost_per_kwh,
    a.comparison_prev_month,
    a.savings_tips,
    a.created_at AS analysis_date,
    eb.id AS bill_id,
    eb.reference_month,
    eb.consumption_kwh,
    eb.total_amount
FROM analyses a
INNER JOIN electricity_bills eb ON a.bill_id = eb.id
ORDER BY a.created_at DESC;

-- -----------------------------------------------------------------------------
-- 2.2 Get bills WITHOUT analysis (LEFT JOIN)
-- -----------------------------------------------------------------------------
SELECT
    eb.id,
    eb.client_id,
    eb.reference_month,
    eb.consumption_kwh,
    eb.total_amount
FROM electricity_bills eb
LEFT JOIN analyses a ON eb.id = a.bill_id
WHERE a.id IS NULL
ORDER BY eb.reference_month DESC;

-- -----------------------------------------------------------------------------
-- 2.3 Get bills WITH analysis (EXISTS)
-- -----------------------------------------------------------------------------
SELECT
    eb.id,
    eb.reference_month,
    eb.consumption_kwh
FROM electricity_bills eb
WHERE EXISTS (
    SELECT 1 FROM analyses a WHERE a.bill_id = eb.id
);

-- -----------------------------------------------------------------------------
-- 2.4 Count analyses by client
-- -----------------------------------------------------------------------------
SELECT
    eb.client_id,
    COUNT(a.id) AS analysis_count
FROM electricity_bills eb
LEFT JOIN analyses a ON eb.id = a.bill_id
GROUP BY eb.client_id
ORDER BY analysis_count DESC;

-- -----------------------------------------------------------------------------
-- 2.5 Get complete analysis view (with client and bill data)
-- -----------------------------------------------------------------------------
SELECT
    a.id AS analysis_id,
    a.average_consumption,
    a.cost_per_kwh,
    a.comparison_prev_month,
    a.savings_tips,
    a.created_at,
    eb.id AS bill_id,
    eb.reference_month,
    eb.consumption_kwh,
    eb.total_amount,
    u.name AS client_name,
    u.email AS client_email
FROM analyses a
INNER JOIN electricity_bills eb ON a.bill_id = eb.id
INNER JOIN clients c ON eb.client_id = c.user_id
INNER JOIN users u ON c.user_id = u.id
ORDER BY a.created_at DESC;

-- ============================================================================
-- SECTION 3: ANALYTICAL QUERIES
-- ============================================================================

-- -----------------------------------------------------------------------------
-- 3.1 Get average cost per kWh across all analyses
-- -----------------------------------------------------------------------------
SELECT
    AVG(cost_per_kwh) AS avg_cost_per_kwh,
    MIN(cost_per_kwh) AS min_cost_per_kwh,
    MAX(cost_per_kwh) AS max_cost_per_kwh,
    STDDEV(cost_per_kwh) AS stddev_cost_per_kwh
FROM analyses
WHERE cost_per_kwh IS NOT NULL;

-- -----------------------------------------------------------------------------
-- 3.2 Get distribution of consumption changes
-- -----------------------------------------------------------------------------
SELECT
    CASE
        WHEN comparison_prev_month IS NULL THEN 'No Comparison'
        WHEN comparison_prev_month < -20 THEN 'Significant Decrease (>20%)'
        WHEN comparison_prev_month < 0 THEN 'Moderate Decrease (0-20%)'
        WHEN comparison_prev_month = 0 THEN 'No Change'
        WHEN comparison_prev_month < 20 THEN 'Moderate Increase (0-20%)'
        ELSE 'Significant Increase (>20%)'
    END AS change_category,
    COUNT(*) AS count
FROM analyses
GROUP BY change_category
ORDER BY
    CASE
        WHEN comparison_prev_month IS NULL THEN 0
        WHEN comparison_prev_month < -20 THEN 1
        WHEN comparison_prev_month < 0 THEN 2
        WHEN comparison_prev_month = 0 THEN 3
        WHEN comparison_prev_month < 20 THEN 4
        ELSE 5
    END;

-- -----------------------------------------------------------------------------
-- 3.3 Find analyses with high consumption increases
-- -----------------------------------------------------------------------------
SELECT
    a.id,
    a.bill_id,
    a.average_consumption,
    a.comparison_prev_month,
    a.cost_per_kwh,
    a.created_at
FROM analyses a
WHERE a.comparison_prev_month > 25
ORDER BY a.comparison_prev_month DESC;

-- -----------------------------------------------------------------------------
-- 3.4 Find analyses with high cost per kWh
-- -----------------------------------------------------------------------------
SELECT
    a.id,
    a.bill_id,
    a.cost_per_kwh,
    a.average_consumption,
    a.savings_tips
FROM analyses a
WHERE a.cost_per_kwh > 0.85
ORDER BY a.cost_per_kwh DESC;

-- -----------------------------------------------------------------------------
-- 3.5 Get analyses created in the last N days
-- -----------------------------------------------------------------------------
SELECT
    id,
    bill_id,
    average_consumption,
    cost_per_kwh,
    comparison_prev_month,
    created_at
FROM analyses
WHERE created_at >= CURRENT_TIMESTAMP - INTERVAL '30 days'
ORDER BY created_at DESC;

-- -----------------------------------------------------------------------------
-- 3.6 Monthly analysis creation trend
-- -----------------------------------------------------------------------------
SELECT
    DATE_TRUNC('month', created_at) AS month,
    COUNT(*) AS analyses_created,
    AVG(cost_per_kwh) AS avg_cost,
    AVG(comparison_prev_month) AS avg_consumption_change
FROM analyses
GROUP BY month
ORDER BY month DESC;

-- -----------------------------------------------------------------------------
-- 3.7 Compare average vs actual consumption
-- -----------------------------------------------------------------------------
SELECT
    a.id,
    a.bill_id,
    a.average_consumption,
    eb.consumption_kwh AS actual_consumption,
    (eb.consumption_kwh - a.average_consumption) AS difference,
    ROUND(
        ((eb.consumption_kwh - a.average_consumption) / NULLIF(a.average_consumption, 0)) * 100,
        2
    ) AS pct_difference
FROM analyses a
INNER JOIN electricity_bills eb ON a.bill_id = eb.id
WHERE a.average_consumption IS NOT NULL
ORDER BY ABS(eb.consumption_kwh - a.average_consumption) DESC;

-- ============================================================================
-- SECTION 4: DATA QUALITY & VALIDATION QUERIES
-- ============================================================================

-- -----------------------------------------------------------------------------
-- 4.1 Find analyses with NULL critical fields
-- -----------------------------------------------------------------------------
SELECT
    id,
    bill_id,
    average_consumption,
    cost_per_kwh,
    comparison_prev_month
FROM analyses
WHERE
    average_consumption IS NULL
    OR cost_per_kwh IS NULL
    OR comparison_prev_month IS NULL;

-- -----------------------------------------------------------------------------
-- 4.2 Verify One-to-One relationship (should return 0 rows)
-- -----------------------------------------------------------------------------
SELECT
    bill_id,
    COUNT(*) AS analysis_count
FROM analyses
GROUP BY bill_id
HAVING COUNT(*) > 1;

-- -----------------------------------------------------------------------------
-- 4.3 Find orphaned analyses (bills don't exist)
-- -----------------------------------------------------------------------------
SELECT
    a.id,
    a.bill_id
FROM analyses a
LEFT JOIN electricity_bills eb ON a.bill_id = eb.id
WHERE eb.id IS NULL;

-- -----------------------------------------------------------------------------
-- 4.4 Verify check constraints (should return 0 rows)
-- -----------------------------------------------------------------------------
-- Negative average_consumption
SELECT id, average_consumption
FROM analyses
WHERE average_consumption < 0;

-- Negative cost_per_kwh
SELECT id, cost_per_kwh
FROM analyses
WHERE cost_per_kwh < 0;

-- -----------------------------------------------------------------------------
-- 4.5 Find analyses with missing savings_tips
-- -----------------------------------------------------------------------------
SELECT
    id,
    bill_id,
    comparison_prev_month,
    cost_per_kwh,
    savings_tips
FROM analyses
WHERE
    savings_tips IS NULL
    OR savings_tips = '';

-- -----------------------------------------------------------------------------
-- 4.6 Find analyses with missing report PDF
-- -----------------------------------------------------------------------------
SELECT
    id,
    bill_id,
    created_at
FROM analyses
WHERE report_pdf_url IS NULL
ORDER BY created_at DESC;

-- -----------------------------------------------------------------------------
-- 4.7 Verify cost_per_kwh calculation accuracy
-- -----------------------------------------------------------------------------
SELECT
    a.id,
    a.cost_per_kwh AS analyzed_cost,
    ROUND(eb.total_amount / NULLIF(eb.consumption_kwh, 0), 4) AS actual_cost,
    ABS(a.cost_per_kwh - ROUND(eb.total_amount / NULLIF(eb.consumption_kwh, 0), 4)) AS difference
FROM analyses a
INNER JOIN electricity_bills eb ON a.bill_id = eb.id
WHERE
    a.cost_per_kwh IS NOT NULL
    AND eb.consumption_kwh > 0
    AND ABS(a.cost_per_kwh - ROUND(eb.total_amount / NULLIF(eb.consumption_kwh, 0), 4)) > 0.01
ORDER BY difference DESC;

-- ============================================================================
-- SECTION 5: CLIENT ANALYTICAL QUERIES
-- ============================================================================

-- -----------------------------------------------------------------------------
-- 5.1 Get client's analysis history
-- -----------------------------------------------------------------------------
SELECT
    a.id,
    a.average_consumption,
    a.cost_per_kwh,
    a.comparison_prev_month,
    a.created_at,
    eb.reference_month
FROM analyses a
INNER JOIN electricity_bills eb ON a.bill_id = eb.id
WHERE eb.client_id = 'client-uuid-here'
ORDER BY eb.reference_month DESC;

-- -----------------------------------------------------------------------------
-- 5.2 Calculate client's consumption trend
-- -----------------------------------------------------------------------------
SELECT
    eb.client_id,
    AVG(a.comparison_prev_month) AS avg_monthly_change,
    COUNT(*) AS analysis_count,
    SUM(CASE WHEN a.comparison_prev_month > 0 THEN 1 ELSE 0 END) AS months_increased,
    SUM(CASE WHEN a.comparison_prev_month < 0 THEN 1 ELSE 0 END) AS months_decreased
FROM analyses a
INNER JOIN electricity_bills eb ON a.bill_id = eb.id
WHERE eb.client_id = 'client-uuid-here'
GROUP BY eb.client_id;

-- -----------------------------------------------------------------------------
-- 5.3 Identify clients needing urgent action
-- -----------------------------------------------------------------------------
SELECT
    eb.client_id,
    u.name AS client_name,
    u.email AS client_email,
    a.comparison_prev_month AS pct_increase,
    a.cost_per_kwh,
    a.savings_tips,
    a.created_at
FROM analyses a
INNER JOIN electricity_bills eb ON a.bill_id = eb.id
INNER JOIN clients c ON eb.client_id = c.user_id
INNER JOIN users u ON c.user_id = u.id
WHERE
    a.comparison_prev_month > 30
    OR a.cost_per_kwh > 1.00
ORDER BY a.comparison_prev_month DESC;

-- -----------------------------------------------------------------------------
-- 5.4 Get client's cost trends
-- -----------------------------------------------------------------------------
SELECT
    eb.reference_month,
    a.cost_per_kwh,
    a.average_consumption,
    eb.consumption_kwh,
    LAG(a.cost_per_kwh) OVER (ORDER BY eb.reference_month) AS prev_cost_per_kwh,
    a.cost_per_kwh - LAG(a.cost_per_kwh) OVER (ORDER BY eb.reference_month) AS cost_change
FROM analyses a
INNER JOIN electricity_bills eb ON a.bill_id = eb.id
WHERE eb.client_id = 'client-uuid-here'
ORDER BY eb.reference_month ASC;

-- ============================================================================
-- SECTION 6: BUSINESS LOGIC QUERIES
-- ============================================================================

-- -----------------------------------------------------------------------------
-- 6.1 Calculate potential savings for high-cost analyses
-- -----------------------------------------------------------------------------
SELECT
    a.id,
    a.bill_id,
    a.cost_per_kwh,
    a.average_consumption,
    (a.cost_per_kwh * a.average_consumption) AS estimated_monthly_cost,
    CASE
        WHEN a.cost_per_kwh > 0.90 THEN 'High Cost - Review tariff plan immediately'
        WHEN a.cost_per_kwh > 0.70 THEN 'Medium Cost - Optimize usage patterns'
        ELSE 'Acceptable Cost'
    END AS cost_category,
    CASE
        WHEN a.cost_per_kwh > 0.90 THEN (a.cost_per_kwh - 0.70) * a.average_consumption
        ELSE 0
    END AS potential_monthly_savings
FROM analyses a
WHERE a.cost_per_kwh IS NOT NULL AND a.average_consumption IS NOT NULL
ORDER BY a.cost_per_kwh DESC;

-- -----------------------------------------------------------------------------
-- 6.2 Generate savings report summary
-- -----------------------------------------------------------------------------
SELECT
    COUNT(*) AS total_analyses,
    COUNT(CASE WHEN savings_tips IS NOT NULL THEN 1 END) AS with_recommendations,
    AVG(cost_per_kwh) AS avg_cost_per_kwh,
    AVG(comparison_prev_month) AS avg_consumption_change,
    COUNT(CASE WHEN comparison_prev_month > 0 THEN 1 END) AS increased_consumption,
    COUNT(CASE WHEN comparison_prev_month < 0 THEN 1 END) AS decreased_consumption,
    COUNT(CASE WHEN comparison_prev_month = 0 THEN 1 END) AS stable_consumption,
    COUNT(CASE WHEN report_pdf_url IS NOT NULL THEN 1 END) AS with_reports
FROM analyses;

-- -----------------------------------------------------------------------------
-- 6.3 Identify best performing clients (lowest cost + decrease)
-- -----------------------------------------------------------------------------
SELECT
    eb.client_id,
    u.name AS client_name,
    AVG(a.cost_per_kwh) AS avg_cost,
    AVG(a.comparison_prev_month) AS avg_change,
    COUNT(*) AS analysis_count
FROM analyses a
INNER JOIN electricity_bills eb ON a.bill_id = eb.id
INNER JOIN clients c ON eb.client_id = c.user_id
INNER JOIN users u ON c.user_id = u.id
GROUP BY eb.client_id, u.name
HAVING
    AVG(a.cost_per_kwh) < 0.70
    AND AVG(a.comparison_prev_month) < 0
ORDER BY avg_cost ASC, avg_change ASC;

-- ============================================================================
-- SECTION 7: REPOSITORY PATTERN QUERIES (for Spring Data JPA)
-- ============================================================================

-- -----------------------------------------------------------------------------
-- 7.1 findByBillId (One-to-One lookup)
-- -----------------------------------------------------------------------------
SELECT * FROM analyses WHERE bill_id = ?;

-- -----------------------------------------------------------------------------
-- 7.2 existsByBillId (Check if analysis exists for bill)
-- -----------------------------------------------------------------------------
SELECT EXISTS(SELECT 1 FROM analyses WHERE bill_id = ?);

-- -----------------------------------------------------------------------------
-- 7.3 findByCreatedAtAfter (Get recent analyses)
-- -----------------------------------------------------------------------------
SELECT * FROM analyses
WHERE created_at >= ?
ORDER BY created_at DESC;

-- -----------------------------------------------------------------------------
-- 7.4 findByCostPerKwhGreaterThan (Find high-cost analyses)
-- -----------------------------------------------------------------------------
SELECT * FROM analyses
WHERE cost_per_kwh > ?
ORDER BY cost_per_kwh DESC;

-- -----------------------------------------------------------------------------
-- 7.5 findByComparisonPrevMonthGreaterThan (Find high increases)
-- -----------------------------------------------------------------------------
SELECT * FROM analyses
WHERE comparison_prev_month > ?
ORDER BY comparison_prev_month DESC;

-- -----------------------------------------------------------------------------
-- 7.6 findByComparisonPrevMonthLessThan (Find decreases)
-- -----------------------------------------------------------------------------
SELECT * FROM analyses
WHERE comparison_prev_month < ?
ORDER BY comparison_prev_month ASC;

-- -----------------------------------------------------------------------------
-- 7.7 findAllWithBills (JOIN query - DTO projection)
-- -----------------------------------------------------------------------------
SELECT
    a.id AS analysisId,
    a.bill_id AS billId,
    a.average_consumption,
    a.cost_per_kwh,
    a.comparison_prev_month,
    a.savings_tips,
    a.report_pdf_url,
    a.created_at,
    eb.reference_month,
    eb.consumption_kwh,
    eb.total_amount
FROM analyses a
INNER JOIN electricity_bills eb ON a.bill_id = eb.id
ORDER BY a.created_at DESC;

-- ============================================================================
-- SECTION 8: MAINTENANCE QUERIES
-- ============================================================================

-- -----------------------------------------------------------------------------
-- 8.1 Get table statistics
-- -----------------------------------------------------------------------------
SELECT
    COUNT(*) AS total_analyses,
    COUNT(DISTINCT bill_id) AS unique_bills_analyzed,
    MIN(created_at) AS oldest_analysis,
    MAX(created_at) AS newest_analysis,
    AVG(average_consumption) AS avg_consumption,
    AVG(cost_per_kwh) AS avg_cost,
    AVG(comparison_prev_month) AS avg_change_pct,
    COUNT(CASE WHEN savings_tips IS NOT NULL THEN 1 END) AS with_tips,
    COUNT(CASE WHEN report_pdf_url IS NOT NULL THEN 1 END) AS with_reports
FROM analyses;

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
WHERE tablename = 'analyses'
ORDER BY idx_scan DESC;

-- -----------------------------------------------------------------------------
-- 8.3 Find analyses older than N months
-- -----------------------------------------------------------------------------
SELECT
    id,
    bill_id,
    created_at,
    AGE(CURRENT_DATE, created_at::date) AS age
FROM analyses
WHERE created_at < (CURRENT_DATE - INTERVAL '12 months')
ORDER BY created_at ASC;

-- -----------------------------------------------------------------------------
-- 8.4 Cleanup old analyses (example - execute with caution)
-- -----------------------------------------------------------------------------
-- DELETE FROM analyses
-- WHERE created_at < (CURRENT_DATE - INTERVAL '2 years');

-- ============================================================================
-- END OF QUERIES
-- ============================================================================

