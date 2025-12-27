-- ============================================================================
-- TARIFFS TABLE - SQL QUERIES REFERENCE
-- Migration: V005
-- Purpose: Common queries for tariffs management and ANEEL API integration
-- ============================================================================

-- ============================================================================
-- SECTION 1: BASIC CRUD OPERATIONS
-- ============================================================================

-- -----------------------------------------------------------------------------
-- 1.1 INSERT - Create new tariff record (from ANEEL API)
-- -----------------------------------------------------------------------------
INSERT INTO tariffs (
    generation_date,
    description_reh,
    distributor,
    cnpj_distributor,
    valid_from,
    valid_until,
    tariff_base_desc,
    subgroup,
    tariff_modality,
    consumer_class,
    consumer_subclass,
    detail,
    tariff_post_name,
    tertiary_unit,
    accessing_agent,
    tusd_value,
    te_value,
    flag_generation_date,
    competence_date,
    activated_flag_name,
    flag_additional_value
) VALUES (
    '2025-12-24',
    'RESOLUÇÃO HOMOLOGATÓRIA Nº 0.937, DE 2 DE FEVEREIRO DE 2010',
    'CPFL JAGUARI',
    '53859112000169',
    '2010-02-03',
    '2011-02-02',
    'Tarifa de Aplicação',
    'A2',
    'Azul',
    'Não se aplica',
    'Não se aplica',
    'APE',
    'Fora ponta',
    'kW',
    'Não se aplica',
    1.85,
    0.00,
    '2025-12-22',
    '2015-01-01',
    'Vermelha P1',
    30.00
);

-- -----------------------------------------------------------------------------
-- 1.2 SELECT - Get all tariffs
-- -----------------------------------------------------------------------------
SELECT
    id,
    distributor,
    cnpj_distributor,
    subgroup,
    tariff_modality,
    valid_from,
    valid_until,
    tusd_value,
    te_value,
    activated_flag_name,
    flag_additional_value,
    created_at
FROM tariffs
ORDER BY created_at DESC;

-- -----------------------------------------------------------------------------
-- 1.3 SELECT - Get tariff by ID
-- -----------------------------------------------------------------------------
SELECT * FROM tariffs WHERE id = 'uuid-here';

-- -----------------------------------------------------------------------------
-- 1.4 UPDATE - Update tariff record
-- Note: In production, prefer INSERT for new snapshots instead of UPDATE
-- -----------------------------------------------------------------------------
UPDATE tariffs
SET
    description_reh = 'Updated description',
    updated_at = NOW()
WHERE id = 'uuid-here';

-- -----------------------------------------------------------------------------
-- 1.5 DELETE - Remove tariff (use with caution)
-- -----------------------------------------------------------------------------
DELETE FROM tariffs WHERE id = 'uuid-here';

-- ============================================================================
-- SECTION 2: BUSINESS QUERIES - ACTIVE TARIFF LOOKUP
-- ============================================================================

-- -----------------------------------------------------------------------------
-- 2.1 Find active tariff for a specific distributor and date
-- -----------------------------------------------------------------------------
SELECT
    id,
    distributor,
    subgroup,
    tariff_modality,
    consumer_class,
    tariff_post_name,
    tusd_value,
    te_value,
    valid_from,
    valid_until
FROM tariffs
WHERE
    distributor = 'CPFL JAGUARI'
    AND subgroup = 'B1'
    AND tariff_modality = 'Convencional'
    AND valid_from <= '2025-12-27'
    AND (valid_until IS NULL OR valid_until >= '2025-12-27')
ORDER BY valid_from DESC
LIMIT 1;

-- -----------------------------------------------------------------------------
-- 2.2 Find all active tariffs for a distributor (current date)
-- -----------------------------------------------------------------------------
SELECT
    id,
    distributor,
    subgroup,
    tariff_modality,
    consumer_class,
    tariff_post_name,
    tusd_value,
    te_value,
    tusd_value + te_value AS total_tariff
FROM tariffs
WHERE
    distributor = 'CPFL JAGUARI'
    AND valid_from <= CURRENT_DATE
    AND (valid_until IS NULL OR valid_until >= CURRENT_DATE)
ORDER BY subgroup, tariff_modality;

-- -----------------------------------------------------------------------------
-- 2.3 Find tariff with matching characteristics
-- -----------------------------------------------------------------------------
SELECT
    t.*,
    (t.tusd_value + t.te_value) AS total_cost_per_unit
FROM tariffs t
WHERE
    t.distributor = 'CEMIG'
    AND t.subgroup = 'B3'
    AND t.tariff_modality = 'Convencional'
    AND t.consumer_class ILIKE '%residencial%'
    AND t.valid_from <= CURRENT_DATE
    AND (t.valid_until IS NULL OR t.valid_until >= CURRENT_DATE);

-- ============================================================================
-- SECTION 3: TARIFF FLAGS QUERIES
-- ============================================================================

-- -----------------------------------------------------------------------------
-- 3.1 Get current tariff flag for a specific month
-- -----------------------------------------------------------------------------
SELECT
    activated_flag_name,
    flag_additional_value,
    competence_date,
    flag_generation_date
FROM tariffs
WHERE
    competence_date = DATE_TRUNC('month', '2025-12-01'::DATE)
    AND activated_flag_name IS NOT NULL
ORDER BY flag_generation_date DESC
LIMIT 1;

-- -----------------------------------------------------------------------------
-- 3.2 Get all tariff flags history
-- -----------------------------------------------------------------------------
SELECT
    EXTRACT(YEAR FROM competence_date) AS year,
    EXTRACT(MONTH FROM competence_date) AS month,
    activated_flag_name,
    flag_additional_value
FROM tariffs
WHERE activated_flag_name IS NOT NULL
ORDER BY competence_date DESC;

-- -----------------------------------------------------------------------------
-- 3.3 Calculate total cost with flag (example: 150 kWh consumption)
-- -----------------------------------------------------------------------------
SELECT
    distributor,
    tariff_modality,
    tusd_value,
    te_value,
    (tusd_value + te_value) AS base_tariff,
    activated_flag_name,
    flag_additional_value,
    -- Calculate total for 150 kWh
    (150 * (tusd_value + te_value)) AS base_cost,
    ((150 / 100.0) * flag_additional_value) AS flag_cost,
    (150 * (tusd_value + te_value)) + ((150 / 100.0) * flag_additional_value) AS total_cost
FROM tariffs
WHERE
    distributor = 'CPFL JAGUARI'
    AND valid_from <= CURRENT_DATE
    AND (valid_until IS NULL OR valid_until >= CURRENT_DATE)
    AND activated_flag_name IS NOT NULL
LIMIT 1;

-- ============================================================================
-- SECTION 4: DISTRIBUTOR QUERIES
-- ============================================================================

-- -----------------------------------------------------------------------------
-- 4.1 List all distributors
-- -----------------------------------------------------------------------------
SELECT DISTINCT
    distributor,
    cnpj_distributor
FROM tariffs
ORDER BY distributor;

-- -----------------------------------------------------------------------------
-- 4.2 Count tariffs by distributor
-- -----------------------------------------------------------------------------
SELECT
    distributor,
    COUNT(*) AS total_tariffs,
    COUNT(CASE WHEN valid_until IS NULL OR valid_until >= CURRENT_DATE THEN 1 END) AS active_tariffs
FROM tariffs
GROUP BY distributor
ORDER BY total_tariffs DESC;

-- -----------------------------------------------------------------------------
-- 4.3 Get distributor tariff options (subgroups and modalities)
-- -----------------------------------------------------------------------------
SELECT
    distributor,
    subgroup,
    tariff_modality,
    consumer_class,
    COUNT(*) AS tariff_count
FROM tariffs
WHERE
    distributor = 'CPFL JAGUARI'
    AND valid_from <= CURRENT_DATE
    AND (valid_until IS NULL OR valid_until >= CURRENT_DATE)
GROUP BY distributor, subgroup, tariff_modality, consumer_class
ORDER BY subgroup, tariff_modality;

-- ============================================================================
-- SECTION 5: ANALYTICAL QUERIES
-- ============================================================================

-- -----------------------------------------------------------------------------
-- 5.1 Compare tariffs across distributors
-- -----------------------------------------------------------------------------
SELECT
    distributor,
    subgroup,
    tariff_modality,
    AVG(tusd_value + te_value) AS avg_total_tariff,
    MIN(tusd_value + te_value) AS min_total_tariff,
    MAX(tusd_value + te_value) AS max_total_tariff
FROM tariffs
WHERE
    valid_from <= CURRENT_DATE
    AND (valid_until IS NULL OR valid_until >= CURRENT_DATE)
    AND subgroup = 'B1'
GROUP BY distributor, subgroup, tariff_modality
ORDER BY avg_total_tariff ASC;

-- -----------------------------------------------------------------------------
-- 5.2 Tariff history for a distributor (price changes over time)
-- -----------------------------------------------------------------------------
SELECT
    valid_from,
    valid_until,
    subgroup,
    tariff_modality,
    tusd_value,
    te_value,
    (tusd_value + te_value) AS total_tariff,
    description_reh
FROM tariffs
WHERE
    distributor = 'CPFL JAGUARI'
    AND subgroup = 'B1'
    AND tariff_modality = 'Convencional'
ORDER BY valid_from DESC;

-- -----------------------------------------------------------------------------
-- 5.3 Find tariffs expiring soon (next 30 days)
-- -----------------------------------------------------------------------------
SELECT
    distributor,
    subgroup,
    tariff_modality,
    valid_from,
    valid_until,
    CURRENT_DATE AS today,
    (valid_until - CURRENT_DATE) AS days_until_expiry
FROM tariffs
WHERE
    valid_until IS NOT NULL
    AND valid_until BETWEEN CURRENT_DATE AND (CURRENT_DATE + INTERVAL '30 days')
ORDER BY valid_until ASC;

-- -----------------------------------------------------------------------------
-- 5.4 Get most recent tariff update for each distributor
-- -----------------------------------------------------------------------------
SELECT DISTINCT ON (distributor)
    distributor,
    generation_date,
    valid_from,
    created_at
FROM tariffs
ORDER BY distributor, generation_date DESC, created_at DESC;

-- ============================================================================
-- SECTION 6: DATA VALIDATION QUERIES
-- ============================================================================

-- -----------------------------------------------------------------------------
-- 6.1 Find tariffs with overlapping validity periods (data quality check)
-- -----------------------------------------------------------------------------
SELECT
    t1.id AS tariff_1_id,
    t2.id AS tariff_2_id,
    t1.distributor,
    t1.subgroup,
    t1.tariff_modality,
    t1.valid_from AS t1_from,
    t1.valid_until AS t1_until,
    t2.valid_from AS t2_from,
    t2.valid_until AS t2_until
FROM tariffs t1
INNER JOIN tariffs t2 ON
    t1.distributor = t2.distributor
    AND t1.subgroup = t2.subgroup
    AND t1.tariff_modality = t2.tariff_modality
    AND t1.id != t2.id
WHERE
    t1.valid_from <= COALESCE(t2.valid_until, '9999-12-31')
    AND COALESCE(t1.valid_until, '9999-12-31') >= t2.valid_from;

-- -----------------------------------------------------------------------------
-- 6.2 Find tariffs with invalid CNPJ format
-- -----------------------------------------------------------------------------
SELECT
    id,
    distributor,
    cnpj_distributor
FROM tariffs
WHERE cnpj_distributor !~ '^\d{14}$';

-- -----------------------------------------------------------------------------
-- 6.3 Find tariffs with negative values (should not exist)
-- -----------------------------------------------------------------------------
SELECT
    id,
    distributor,
    tusd_value,
    te_value,
    flag_additional_value
FROM tariffs
WHERE
    tusd_value < 0
    OR te_value < 0
    OR flag_additional_value < 0;

-- -----------------------------------------------------------------------------
-- 6.4 Count tariffs by validity status
-- -----------------------------------------------------------------------------
SELECT
    CASE
        WHEN valid_until IS NULL THEN 'No expiry (current)'
        WHEN valid_until >= CURRENT_DATE THEN 'Active'
        ELSE 'Expired'
    END AS status,
    COUNT(*) AS count
FROM tariffs
GROUP BY status;

-- ============================================================================
-- SECTION 7: REPOSITORY PATTERN QUERIES (for Spring Data JPA)
-- ============================================================================

-- -----------------------------------------------------------------------------
-- 7.1 findActiveByDistributorAndCharacteristics
-- Usage: Find active tariff matching specific characteristics
-- -----------------------------------------------------------------------------
SELECT * FROM tariffs
WHERE
    distributor = ?
    AND subgroup = ?
    AND tariff_modality = ?
    AND valid_from <= ?
    AND (valid_until IS NULL OR valid_until >= ?)
ORDER BY valid_from DESC
LIMIT 1;

-- -----------------------------------------------------------------------------
-- 7.2 findByDistributorAndValidityPeriod
-- Usage: Get all tariffs for a distributor active in a date range
-- -----------------------------------------------------------------------------
SELECT * FROM tariffs
WHERE
    distributor = ?
    AND valid_from <= ?
    AND (valid_until IS NULL OR valid_until >= ?)
ORDER BY subgroup, tariff_modality;

-- -----------------------------------------------------------------------------
-- 7.3 findByCompetenceDate
-- Usage: Get tariff flag for specific month
-- -----------------------------------------------------------------------------
SELECT * FROM tariffs
WHERE
    competence_date = DATE_TRUNC('month', ?::DATE)
    AND activated_flag_name IS NOT NULL
ORDER BY flag_generation_date DESC
LIMIT 1;

-- -----------------------------------------------------------------------------
-- 7.4 findDistinctDistributors
-- Usage: Get list of all distributors
-- -----------------------------------------------------------------------------
SELECT DISTINCT distributor, cnpj_distributor
FROM tariffs
ORDER BY distributor;

-- ============================================================================
-- SECTION 8: MAINTENANCE QUERIES
-- ============================================================================

-- -----------------------------------------------------------------------------
-- 8.1 Archive old tariffs (soft delete approach)
-- Note: Instead of DELETE, consider adding an 'archived' boolean column
-- -----------------------------------------------------------------------------
-- DELETE FROM tariffs
-- WHERE valid_until < (CURRENT_DATE - INTERVAL '5 years');

-- -----------------------------------------------------------------------------
-- 8.2 Get table statistics
-- -----------------------------------------------------------------------------
SELECT
    COUNT(*) AS total_records,
    COUNT(DISTINCT distributor) AS total_distributors,
    MIN(valid_from) AS oldest_tariff_date,
    MAX(valid_from) AS newest_tariff_date,
    COUNT(CASE WHEN valid_until IS NULL OR valid_until >= CURRENT_DATE THEN 1 END) AS active_tariffs,
    COUNT(CASE WHEN activated_flag_name IS NOT NULL THEN 1 END) AS records_with_flags
FROM tariffs;

-- -----------------------------------------------------------------------------
-- 8.3 Check index usage
-- -----------------------------------------------------------------------------
SELECT
    schemaname,
    tablename,
    indexname,
    idx_scan AS index_scans,
    idx_tup_read AS tuples_read,
    idx_tup_fetch AS tuples_fetched
FROM pg_stat_user_indexes
WHERE tablename = 'tariffs'
ORDER BY idx_scan DESC;

-- ============================================================================
-- END OF QUERIES
-- ============================================================================

