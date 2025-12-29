-- ============================================================================
-- VALIDATION QUERIES FOR TEST DATA
-- Description: Validates all inserted test data across all tables
-- Execute these queries to verify data integrity
-- Date: 2025-12-29
-- ============================================================================

-- ============================================================================
-- 1. COUNT RECORDS IN EACH TABLE
-- ============================================================================
SELECT 'users' AS table_name, COUNT(*) AS record_count FROM users
UNION ALL
SELECT 'clients', COUNT(*) FROM clients
UNION ALL
SELECT 'consultants', COUNT(*) FROM consultants
UNION ALL
SELECT 'admins', COUNT(*) FROM admins
UNION ALL
SELECT 'tariffs', COUNT(*) FROM tariffs
UNION ALL
SELECT 'electricity_bills', COUNT(*) FROM electricity_bills
UNION ALL
SELECT 'bill_items', COUNT(*) FROM bill_items
UNION ALL
SELECT 'analyses', COUNT(*) FROM analyses
UNION ALL
SELECT 'consultant_clients', COUNT(*) FROM consultant_clients
ORDER BY table_name;

-- Expected results:
-- admins: 5
-- analyses: 5
-- bill_items: 37 (5+5+6+7+5+5+5 items across 7 bills)
-- clients: 5
-- consultant_clients: 10
-- consultants: 5
-- electricity_bills: 7
-- tariffs: 5
-- users: 15 (5 clients + 5 consultants + 5 admins)

-- ============================================================================
-- 2. VERIFY USER TYPES DISTRIBUTION
-- ============================================================================
SELECT
    user_type,
    COUNT(*) AS count,
    COUNT(*) FILTER (WHERE status = 'ACTIVE') AS active,
    COUNT(*) FILTER (WHERE status = 'INACTIVE') AS inactive,
    COUNT(*) FILTER (WHERE status = 'SUSPENDED') AS suspended,
    COUNT(*) FILTER (WHERE status = 'PENDING_VERIFICATION') AS pending
FROM users
GROUP BY user_type
ORDER BY user_type;

-- Expected:
-- ADMIN: 5 users (all ACTIVE)
-- CLIENT: 5 users (3 ACTIVE, 1 INACTIVE, 1 PENDING_VERIFICATION)
-- CONSULTANT: 5 users (4 ACTIVE, 1 SUSPENDED)

-- ============================================================================
-- 3. VERIFY CLIENTS WITH COMPLETE DATA
-- ============================================================================
SELECT
    u.email,
    u.name,
    u.status AS user_status,
    c.cpf,
    c.city,
    c.state,
    c.registration_date,
    COUNT(eb.id) AS bill_count
FROM users u
JOIN clients c ON u.id = c.user_id
LEFT JOIN electricity_bills eb ON c.user_id = eb.client_id
WHERE u.user_type = 'CLIENT'
GROUP BY u.id, u.email, u.name, u.status, c.cpf, c.city, c.state, c.registration_date
ORDER BY u.email;

-- Expected: 5 clients with varying bill counts (0-2 bills each)

-- ============================================================================
-- 4. VERIFY CONSULTANTS WITH COMPLETE DATA
-- ============================================================================
SELECT
    u.email,
    cons.consultant_name,
    cons.company,
    cons.cnpj,
    u.status AS user_status,
    COUNT(DISTINCT cc.client_id) AS clients_managed,
    COUNT(DISTINCT eb.id) AS bills_managed
FROM users u
JOIN consultants cons ON u.id = cons.user_id
LEFT JOIN consultant_clients cc ON cons.user_id = cc.consultant_id AND cc.status = 'ACTIVE'
LEFT JOIN electricity_bills eb ON cons.user_id = eb.consultant_id
WHERE u.user_type = 'CONSULTANT'
GROUP BY u.id, u.email, cons.consultant_name, cons.company, cons.cnpj, u.status
ORDER BY u.email;

-- Expected: 5 consultants with varying client and bill counts

-- ============================================================================
-- 5. VERIFY ELECTRICITY BILLS WITH ALL RELATIONSHIPS
-- ============================================================================
SELECT
    eb.invoice_number,
    eb.reference_month,
    c_user.name AS client_name,
    cons_user.name AS consultant_name,
    t.distributor AS tariff_distributor,
    eb.consumption_kwh,
    eb.total_amount,
    COUNT(bi.id) AS item_count,
    CASE WHEN a.id IS NOT NULL THEN 'YES' ELSE 'NO' END AS has_analysis
FROM electricity_bills eb
JOIN clients c ON eb.client_id = c.user_id
JOIN users c_user ON c.user_id = c_user.id
LEFT JOIN consultants cons ON eb.consultant_id = cons.user_id
LEFT JOIN users cons_user ON cons.user_id = cons_user.id
JOIN tariffs t ON eb.tariff_id = t.id
LEFT JOIN bill_items bi ON eb.id = bi.bill_id
LEFT JOIN analyses a ON eb.id = a.bill_id
GROUP BY
    eb.id, eb.invoice_number, eb.reference_month,
    c_user.name, cons_user.name, t.distributor,
    eb.consumption_kwh, eb.total_amount, a.id
ORDER BY eb.reference_month DESC, eb.invoice_number;

-- Expected: 7 bills with 5-7 items each, 5 with analysis

-- ============================================================================
-- 6. VERIFY BILL ITEMS BY TYPE
-- ============================================================================
SELECT
    item_type,
    COUNT(*) AS count,
    SUM(amount) AS total_amount,
    AVG(amount) AS avg_amount
FROM bill_items
GROUP BY item_type
ORDER BY count DESC;

-- Expected: Distribution across different item types

-- ============================================================================
-- 7. VERIFY ANALYSES WITH BILL DETAILS
-- ============================================================================
SELECT
    a.id AS analysis_id,
    eb.invoice_number,
    c_user.name AS client_name,
    eb.consumption_kwh,
    eb.total_amount,
    a.cost_per_kwh,
    a.average_consumption,
    a.comparison_prev_month,
    CASE WHEN a.savings_tips IS NOT NULL THEN 'YES' ELSE 'NO' END AS has_tips,
    CASE WHEN a.report_pdf_url IS NOT NULL THEN 'YES' ELSE 'NO' END AS has_report
FROM analyses a
JOIN electricity_bills eb ON a.bill_id = eb.id
JOIN clients c ON eb.client_id = c.user_id
JOIN users c_user ON c.user_id = c_user.id
ORDER BY eb.reference_month DESC;

-- Expected: 5 analyses, all with tips and reports

-- ============================================================================
-- 8. VERIFY CONSULTANT-CLIENT RELATIONSHIPS
-- ============================================================================
SELECT
    cons_user.name AS consultant_name,
    cons.company,
    c_user.name AS client_name,
    cc.status AS relationship_status,
    cc.assigned_at
FROM consultant_clients cc
JOIN consultants cons ON cc.consultant_id = cons.user_id
JOIN users cons_user ON cons.user_id = cons_user.id
JOIN clients c ON cc.client_id = c.user_id
JOIN users c_user ON c.user_id = c_user.id
ORDER BY cons_user.name, cc.assigned_at;

-- Expected: 10 relationships (mix of ACTIVE, INACTIVE, PENDING)

-- ============================================================================
-- 9. VERIFY TARIFFS DATA
-- ============================================================================
SELECT
    distributor,
    subgroup,
    tariff_modality,
    consumer_class,
    valid_from,
    valid_until,
    tusd_value + te_value AS total_tariff,
    activated_flag_name,
    flag_additional_value
FROM tariffs
ORDER BY valid_from DESC;

-- Expected: 5 tariffs from different distributors with various modalities

-- ============================================================================
-- 10. VERIFY DATA INTEGRITY - FOREIGN KEYS
-- ============================================================================

-- Check if all clients have a corresponding user
SELECT 'Orphan clients' AS check_name, COUNT(*) AS count
FROM clients c
LEFT JOIN users u ON c.user_id = u.id
WHERE u.id IS NULL;

-- Check if all consultants have a corresponding user
UNION ALL
SELECT 'Orphan consultants', COUNT(*)
FROM consultants cons
LEFT JOIN users u ON cons.user_id = u.id
WHERE u.id IS NULL;

-- Check if all admins have a corresponding user
UNION ALL
SELECT 'Orphan admins', COUNT(*)
FROM admins a
LEFT JOIN users u ON a.user_id = u.id
WHERE u.id IS NULL;

-- Check if all bills reference valid clients
UNION ALL
SELECT 'Bills with invalid client', COUNT(*)
FROM electricity_bills eb
LEFT JOIN clients c ON eb.client_id = c.user_id
WHERE c.user_id IS NULL;

-- Check if all bills reference valid tariffs
UNION ALL
SELECT 'Bills with invalid tariff', COUNT(*)
FROM electricity_bills eb
LEFT JOIN tariffs t ON eb.tariff_id = t.id
WHERE t.id IS NULL;

-- Check if all bill items reference valid bills
UNION ALL
SELECT 'Items with invalid bill', COUNT(*)
FROM bill_items bi
LEFT JOIN electricity_bills eb ON bi.bill_id = eb.id
WHERE eb.id IS NULL;

-- Check if all analyses reference valid bills
UNION ALL
SELECT 'Analyses with invalid bill', COUNT(*)
FROM analyses a
LEFT JOIN electricity_bills eb ON a.bill_id = eb.id
WHERE eb.id IS NULL;

-- Expected: All counts should be 0 (no orphan records)

-- ============================================================================
-- 11. BUSINESS LOGIC VALIDATIONS
-- ============================================================================

-- Verify CPF format (11 digits)
SELECT 'Invalid CPF format' AS validation, COUNT(*) AS violations
FROM clients
WHERE cpf !~ '^\d{11}$';

-- Verify CNPJ format (14 digits)
UNION ALL
SELECT 'Invalid CNPJ format', COUNT(*)
FROM consultants
WHERE cnpj !~ '^\d{14}$';

-- Verify email format
UNION ALL
SELECT 'Invalid email format', COUNT(*)
FROM users
WHERE email !~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$';

-- Verify non-negative amounts in bills
UNION ALL
SELECT 'Negative bill amounts', COUNT(*)
FROM electricity_bills
WHERE total_amount < 0 OR consumption_kwh < 0;

-- Verify non-negative amounts in bill items
UNION ALL
SELECT 'Negative bill item amounts', COUNT(*)
FROM bill_items
WHERE amount < 0;

-- Verify due date is after reference month
UNION ALL
SELECT 'Due date before reference', COUNT(*)
FROM electricity_bills
WHERE due_date <= reference_month;

-- Expected: All counts should be 0 (no violations)

-- ============================================================================
-- 12. SUMMARY STATISTICS
-- ============================================================================

-- Total consumption and billing by month
SELECT
    TO_CHAR(reference_month, 'YYYY-MM') AS month,
    COUNT(*) AS bill_count,
    SUM(consumption_kwh) AS total_consumption,
    SUM(total_amount) AS total_billing,
    AVG(consumption_kwh) AS avg_consumption,
    AVG(total_amount) AS avg_billing
FROM electricity_bills
GROUP BY TO_CHAR(reference_month, 'YYYY-MM')
ORDER BY month DESC;

-- Top 5 clients by consumption
SELECT
    u.name AS client_name,
    COUNT(eb.id) AS bill_count,
    SUM(eb.consumption_kwh) AS total_consumption,
    SUM(eb.total_amount) AS total_amount
FROM users u
JOIN clients c ON u.id = c.user_id
LEFT JOIN electricity_bills eb ON c.user_id = eb.client_id
GROUP BY u.id, u.name
ORDER BY total_consumption DESC NULLS LAST
LIMIT 5;

-- ============================================================================
-- END OF VALIDATION QUERIES
-- ============================================================================

-- Summary: If all queries execute successfully without errors and return
-- expected counts, the test data is properly inserted and all relationships
-- are correctly established.

