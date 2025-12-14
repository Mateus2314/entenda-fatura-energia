-- ============================================================================
-- Validation Queries for Migration V001
-- Purpose: Verify users table creation and functionality
-- Database: PostgreSQL 17.7
-- Date: 2024-12-14
-- ============================================================================

-- ============================================================================
-- 1. TABLE STRUCTURE VALIDATION
-- ============================================================================

-- 1.1 Check if table exists
SELECT table_name
FROM information_schema.tables
WHERE table_schema = 'public'
  AND table_name = 'users';
-- Expected: 1 row with 'users'

-- 1.2 View complete table structure
SELECT
    column_name,
    data_type,
    character_maximum_length,
    is_nullable,
    column_default
FROM information_schema.columns
WHERE table_schema = 'public'
  AND table_name = 'users'
ORDER BY ordinal_position;
-- Expected: 9 rows (all columns)

-- 1.3 Describe table (PostgreSQL specific)
\d users

-- ============================================================================
-- 2. ENUM VALIDATION
-- ============================================================================

-- 2.1 List all ENUMs with their values
SELECT
    t.typname AS enum_name,
    array_agg(e.enumlabel ORDER BY e.enumsortorder) AS enum_values
FROM pg_type t
JOIN pg_enum e ON t.oid = e.enumtypid
WHERE t.typname IN ('user_type', 'user_status')
GROUP BY t.typname;
-- Expected: 2 rows (user_type, user_status)

-- 2.2 View user_type ENUM details
\dT+ user_type

-- 2.3 View user_status ENUM details
\dT+ user_status

-- ============================================================================
-- 3. CONSTRAINTS VALIDATION
-- ============================================================================

-- 3.1 List all constraints
SELECT
    con.conname AS constraint_name,
    CASE
        WHEN con.contype = 'p' THEN 'PRIMARY KEY'
        WHEN con.contype = 'u' THEN 'UNIQUE'
        WHEN con.contype = 'c' THEN 'CHECK'
        WHEN con.contype = 'f' THEN 'FOREIGN KEY'
        ELSE con.contype::text
    END AS constraint_type,
    pg_get_constraintdef(con.oid) AS definition
FROM pg_constraint con
JOIN pg_class rel ON rel.oid = con.conrelid
WHERE rel.relname = 'users'
ORDER BY con.contype;
-- Expected: 4 constraints (PK, UNIQUE, 2 CHECKs)

-- 3.2 Test email validation constraint (should FAIL)
INSERT INTO users (email, password_hash, user_type, name, status)
VALUES ('invalid-email', 'hash', 'CLIENT', 'Test', 'ACTIVE');
-- Expected: ERROR - violates check constraint "email_format_check"

-- 3.3 Test valid email (should SUCCEED)
INSERT INTO users (email, password_hash, user_type, name, status)
VALUES ('valid@example.com', 'hash123', 'CLIENT', 'Valid User', 'ACTIVE');
-- Expected: 1 row inserted

-- 3.4 Test email uniqueness (should FAIL)
INSERT INTO users (email, password_hash, user_type, name, status)
VALUES ('valid@example.com', 'hash456', 'CLIENT', 'Another User', 'ACTIVE');
-- Expected: ERROR - duplicate key value violates unique constraint

-- Cleanup
DELETE FROM users WHERE email = 'valid@example.com';

-- ============================================================================
-- 4. INDEXES VALIDATION
-- ============================================================================

-- 4.1 List all indexes
SELECT
    indexname,
    indexdef
FROM pg_indexes
WHERE tablename = 'users' AND schemaname = 'public'
ORDER BY indexname;
-- Expected: 6 indexes

-- 4.2 Check index usage (after some queries)
SELECT
    schemaname,
    tablename,
    indexname,
    idx_scan AS index_scans,
    idx_tup_read AS tuples_read,
    idx_tup_fetch AS tuples_fetched
FROM pg_stat_user_indexes
WHERE tablename = 'users'
ORDER BY idx_scan DESC;

-- ============================================================================
-- 5. TRIGGER VALIDATION
-- ============================================================================

-- 5.1 List triggers
SELECT
    trigger_name,
    event_manipulation,
    action_statement,
    action_timing
FROM information_schema.triggers
WHERE event_object_table = 'users';
-- Expected: 1 trigger (update_users_updated_at)

-- 5.2 Check trigger function
SELECT routine_name, routine_type
FROM information_schema.routines
WHERE routine_name = 'update_updated_at_column';
-- Expected: 1 function

-- 5.3 Test trigger functionality
-- Insert a record
INSERT INTO users (email, password_hash, user_type, name, status)
VALUES ('trigger@test.com', 'hash', 'CLIENT', 'Trigger Test', 'ACTIVE')
RETURNING id, created_at, updated_at;

-- Wait a moment, then update
SELECT pg_sleep(2);

UPDATE users
SET name = 'Updated Name'
WHERE email = 'trigger@test.com';

-- Verify updated_at changed
SELECT
    email,
    name,
    created_at,
    updated_at,
    (updated_at - created_at) AS time_difference
FROM users
WHERE email = 'trigger@test.com';
-- Expected: time_difference > 0

-- Cleanup
DELETE FROM users WHERE email = 'trigger@test.com';

-- ============================================================================
-- 6. CRUD OPERATIONS VALIDATION
-- ============================================================================

-- 6.1 INSERT test
INSERT INTO users (email, password_hash, user_type, name, phone, status)
VALUES (
    'crud@test.com',
    '$2a$10$dummyHashForTesting',
    'CONSULTANT',
    'CRUD Test User',
    '+5511999999999',
    'ACTIVE'
)
RETURNING *;
-- Expected: 1 row with all fields populated

-- 6.2 SELECT test
SELECT
    id,
    email,
    user_type,
    name,
    phone,
    status,
    created_at,
    updated_at
FROM users
WHERE email = 'crud@test.com';
-- Expected: 1 row

-- 6.3 UPDATE test
UPDATE users
SET
    name = 'Updated CRUD User',
    status = 'INACTIVE'
WHERE email = 'crud@test.com'
RETURNING *;
-- Expected: 1 row updated, updated_at changed

-- 6.4 DELETE test
DELETE FROM users
WHERE email = 'crud@test.com'
RETURNING email;
-- Expected: 1 row deleted

-- ============================================================================
-- 7. ENUM VALIDATION TESTS
-- ============================================================================

-- 7.1 Test invalid user_type (should FAIL)
INSERT INTO users (email, password_hash, user_type, name, status)
VALUES ('enum@test.com', 'hash', 'INVALID_TYPE', 'Test', 'ACTIVE');
-- Expected: ERROR - invalid input value for enum

-- 7.2 Test valid user_types (should SUCCEED)
INSERT INTO users (email, password_hash, user_type, name, status)
VALUES ('client@test.com', 'hash', 'CLIENT', 'Test Client', 'ACTIVE');

INSERT INTO users (email, password_hash, user_type, name, status)
VALUES ('consultant@test.com', 'hash', 'CONSULTANT', 'Test Consultant', 'ACTIVE');

INSERT INTO users (email, password_hash, user_type, name, status)
VALUES ('admin@test.com', 'hash', 'ADMIN', 'Test Admin', 'ACTIVE');

-- Verify
SELECT email, user_type FROM users WHERE email LIKE '%@test.com';
-- Expected: 3 rows

-- Cleanup
DELETE FROM users WHERE email LIKE '%@test.com';

-- 7.3 Test invalid user_status (should FAIL)
INSERT INTO users (email, password_hash, user_type, name, status)
VALUES ('status@test.com', 'hash', 'CLIENT', 'Test', 'INVALID_STATUS');
-- Expected: ERROR - invalid input value for enum

-- ============================================================================
-- 8. FLYWAY INTEGRATION VALIDATION
-- ============================================================================

-- 8.1 Check Flyway schema history
SELECT
    installed_rank,
    version,
    description,
    type,
    script,
    checksum,
    installed_on,
    execution_time,
    success
FROM flyway_schema_history
ORDER BY installed_rank;
-- Expected: At least 1 row with version = 1

-- 8.2 Count total migrations
SELECT COUNT(*) AS total_migrations
FROM flyway_schema_history
WHERE success = true;
-- Expected: >= 1

-- ============================================================================
-- 9. DATA INTEGRITY TESTS
-- ============================================================================

-- 9.1 Test NOT NULL constraints
-- Missing password_hash (should FAIL)
INSERT INTO users (email, user_type, name, status)
VALUES ('null@test.com', 'CLIENT', 'Test', 'ACTIVE');
-- Expected: ERROR - null value violates not-null constraint

-- Missing email (should FAIL)
INSERT INTO users (password_hash, user_type, name, status)
VALUES ('hash', 'CLIENT', 'Test', 'ACTIVE');
-- Expected: ERROR - null value violates not-null constraint

-- 9.2 Test optional fields (should SUCCEED)
INSERT INTO users (email, password_hash, user_type, name, status)
VALUES ('optional@test.com', 'hash', 'CLIENT', 'No Phone', 'ACTIVE');
-- phone is NULL (optional)
-- Expected: 1 row inserted

-- Verify phone is NULL
SELECT email, phone FROM users WHERE email = 'optional@test.com';
-- Expected: phone = NULL

-- Cleanup
DELETE FROM users WHERE email = 'optional@test.com';

-- ============================================================================
-- 10. PERFORMANCE TESTS
-- ============================================================================

-- 10.1 Test index on email (most common query)
EXPLAIN ANALYZE
SELECT * FROM users WHERE email = 'test@example.com';
-- Expected: Index Scan using idx_users_email or users_email_key

-- 10.2 Test index on user_type
EXPLAIN ANALYZE
SELECT * FROM users WHERE user_type = 'CLIENT';
-- Expected: Index Scan using idx_users_user_type or Seq Scan (if no data)

-- 10.3 Test index on status
EXPLAIN ANALYZE
SELECT * FROM users WHERE status = 'ACTIVE';
-- Expected: Index Scan using idx_users_status or Seq Scan (if no data)

-- ============================================================================
-- 11. SUMMARY QUERIES
-- ============================================================================

-- 11.1 Table statistics
SELECT
    schemaname,
    tablename,
    n_live_tup AS row_count,
    n_dead_tup AS dead_rows,
    last_vacuum,
    last_autovacuum
FROM pg_stat_user_tables
WHERE tablename = 'users';

-- 11.2 Table size
SELECT
    pg_size_pretty(pg_total_relation_size('users')) AS total_size,
    pg_size_pretty(pg_relation_size('users')) AS table_size,
    pg_size_pretty(pg_indexes_size('users')) AS indexes_size;

-- 11.3 Column statistics
SELECT
    attname AS column_name,
    n_distinct AS distinct_values,
    null_frac AS null_fraction
FROM pg_stats
WHERE tablename = 'users';

-- ============================================================================
-- CLEANUP (Run if needed)
-- ============================================================================

-- Remove all test data
DELETE FROM users WHERE email LIKE '%@test.com' OR email LIKE '%@example.com';

-- Verify cleanup
SELECT COUNT(*) FROM users;
-- Expected: 0 (if no production data)

-- ============================================================================
-- END OF VALIDATION QUERIES
-- ============================================================================

