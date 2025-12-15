-- ============================================================================
-- Validation Queries for V003 - Consultants Table
-- ============================================================================

-- ============================================================================
-- 1. TABLE STRUCTURE VALIDATION
-- ============================================================================

-- Check if consultants table exists
SELECT EXISTS (
    SELECT FROM information_schema.tables
    WHERE table_schema = 'public'
    AND table_name = 'consultants'
) AS consultants_table_exists;

-- List all columns in consultants table
SELECT
    column_name,
    data_type,
    character_maximum_length,
    is_nullable,
    column_default
FROM information_schema.columns
WHERE table_name = 'consultants'
ORDER BY ordinal_position;

-- ============================================================================
-- 2. CONSTRAINTS VALIDATION
-- ============================================================================

-- Check primary key constraint
SELECT
    tc.constraint_name,
    tc.constraint_type,
    kcu.column_name
FROM information_schema.table_constraints tc
JOIN information_schema.key_column_usage kcu
    ON tc.constraint_name = kcu.constraint_name
WHERE tc.table_name = 'consultants'
AND tc.constraint_type = 'PRIMARY KEY';

-- Check foreign key constraint
SELECT
    tc.constraint_name,
    tc.constraint_type,
    kcu.column_name,
    ccu.table_name AS foreign_table_name,
    ccu.column_name AS foreign_column_name
FROM information_schema.table_constraints tc
JOIN information_schema.key_column_usage kcu
    ON tc.constraint_name = kcu.constraint_name
JOIN information_schema.constraint_column_usage ccu
    ON ccu.constraint_name = tc.constraint_name
WHERE tc.table_name = 'consultants'
AND tc.constraint_type = 'FOREIGN KEY';

-- Check unique constraints
SELECT
    tc.constraint_name,
    tc.constraint_type,
    kcu.column_name
FROM information_schema.table_constraints tc
JOIN information_schema.key_column_usage kcu
    ON tc.constraint_name = kcu.constraint_name
WHERE tc.table_name = 'consultants'
AND tc.constraint_type = 'UNIQUE';

-- Check all check constraints
SELECT
    con.conname AS constraint_name,
    pg_get_constraintdef(con.oid) AS constraint_definition
FROM pg_constraint con
JOIN pg_class rel ON rel.oid = con.conrelid
JOIN pg_namespace nsp ON nsp.oid = rel.relnamespace
WHERE rel.relname = 'consultants'
AND con.contype = 'c';

-- ============================================================================
-- 3. INDEXES VALIDATION
-- ============================================================================

-- List all indexes on consultants table
SELECT
    indexname,
    indexdef
FROM pg_indexes
WHERE tablename = 'consultants'
ORDER BY indexname;

-- ============================================================================
-- 4. COMMENTS VALIDATION
-- ============================================================================

-- Check table comment
SELECT
    obj_description('consultants'::regclass) AS table_comment;

-- Check column comments
SELECT
    cols.column_name,
    pg_catalog.col_description(c.oid, cols.ordinal_position::int) AS column_comment
FROM information_schema.columns cols
JOIN pg_catalog.pg_class c ON c.relname = cols.table_name
WHERE cols.table_name = 'consultants'
AND pg_catalog.col_description(c.oid, cols.ordinal_position::int) IS NOT NULL
ORDER BY cols.ordinal_position;

-- ============================================================================
-- 5. DATA INTEGRITY TESTS
-- ============================================================================

-- Test 1: Try to insert consultant without user (should fail)
-- Expected: Foreign key violation
-- INSERT INTO consultants (user_id, consultant_name, company, cnpj)
-- VALUES (gen_random_uuid(), 'Test Consultant', 'Test Company', '12345678901234');

-- Test 2: Valid CNPJ format (14 digits) - should succeed
-- First create a user, then create consultant
-- INSERT INTO users (id, email, password, name, status)
-- VALUES (gen_random_uuid(), 'consultant@test.com', 'hashed_password', 'Test Consultant', 'ACTIVE');
--
-- INSERT INTO consultants (user_id, consultant_name, company, cnpj)
-- SELECT id, 'Test Consultant', 'Test Company', '12345678901234'
-- FROM users WHERE email = 'consultant@test.com';

-- Test 3: Invalid CNPJ format (should fail check constraint)
-- Expected: Check constraint violation
-- INSERT INTO consultants (user_id, consultant_name, company, cnpj)
-- VALUES ((SELECT id FROM users WHERE email = 'consultant@test.com'), 'Test', 'Test Co', '123');

-- Test 4: Duplicate CNPJ (should fail unique constraint)
-- Expected: Unique constraint violation
-- INSERT INTO consultants (user_id, consultant_name, company, cnpj)
-- VALUES (gen_random_uuid(), 'Another Consultant', 'Another Company', '12345678901234');

-- ============================================================================
-- 6. RELATIONSHIP VALIDATION
-- ============================================================================

-- Test JOIN with users table
SELECT
    u.id,
    u.email,
    u.name AS user_name,
    c.consultant_name,
    c.company,
    c.cnpj,
    c.registration_date
FROM users u
INNER JOIN consultants c ON u.id = c.user_id
LIMIT 5;

-- Count consultants by status
SELECT
    u.status,
    COUNT(*) AS consultant_count
FROM users u
INNER JOIN consultants c ON u.id = c.user_id
GROUP BY u.status;

-- ============================================================================
-- 7. PERFORMANCE VALIDATION
-- ============================================================================

-- Check index usage for CNPJ lookup
EXPLAIN ANALYZE
SELECT * FROM consultants WHERE cnpj = '12345678901234';

-- Check index usage for company search
EXPLAIN ANALYZE
SELECT * FROM consultants WHERE company ILIKE '%Test%';

-- Check JOIN performance
EXPLAIN ANALYZE
SELECT u.*, c.*
FROM users u
INNER JOIN consultants c ON u.id = c.user_id
WHERE c.cnpj = '12345678901234';

-- ============================================================================
-- 8. SUMMARY STATISTICS
-- ============================================================================

-- Table information summary
SELECT
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS total_size,
    pg_size_pretty(pg_relation_size(schemaname||'.'||tablename)) AS table_size,
    pg_size_pretty(pg_indexes_size(schemaname||'.'||tablename)) AS indexes_size
FROM pg_tables
WHERE tablename = 'consultants';

-- Row count
SELECT COUNT(*) AS total_consultants FROM consultants;

-- Consultants with complete address information
SELECT COUNT(*) AS consultants_with_complete_address
FROM consultants
WHERE address IS NOT NULL
AND city IS NOT NULL
AND state IS NOT NULL
AND zip_code IS NOT NULL;

