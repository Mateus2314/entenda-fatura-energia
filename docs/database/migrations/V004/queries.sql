-- ============================================================================
-- V004: Create Admins Table - Validation Queries
-- ============================================================================
-- Description: SQL queries to validate the admins table migration
-- Author: System
-- Date: 2024-12-16
-- Related Migration: V004__create_admins_table.sql
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. VERIFY TABLE STRUCTURE
-- ----------------------------------------------------------------------------
-- Check if admins table exists and has correct columns
SELECT
    table_name,
    column_name,
    data_type,
    character_maximum_length,
    is_nullable,
    column_default
FROM information_schema.columns
WHERE table_schema = 'public'
  AND table_name = 'admins'
ORDER BY ordinal_position;

-- Expected Output:
-- table_name | column_name  | data_type         | character_maximum_length | is_nullable | column_default
-- admins     | user_id      | uuid              | NULL                     | NO          | NULL
-- admins     | role         | character varying | 50                       | NO          | NULL
-- admins     | permissions  | jsonb             | NULL                     | YES         | NULL
-- admins     | created_at   | timestamp         | NULL                     | NO          | now()
-- admins     | updated_at   | timestamp         | NULL                     | YES         | now()


-- ----------------------------------------------------------------------------
-- 2. VERIFY PRIMARY KEY
-- ----------------------------------------------------------------------------
-- Check primary key constraint
SELECT
    tc.constraint_name,
    tc.constraint_type,
    kcu.column_name
FROM information_schema.table_constraints tc
         JOIN information_schema.key_column_usage kcu
              ON tc.constraint_name = kcu.constraint_name
WHERE tc.table_schema = 'public'
  AND tc.table_name = 'admins'
  AND tc.constraint_type = 'PRIMARY KEY';

-- Expected Output:
-- constraint_name | constraint_type | column_name
-- pk_admins       | PRIMARY KEY     | user_id


-- ----------------------------------------------------------------------------
-- 3. VERIFY FOREIGN KEY CONSTRAINT
-- ----------------------------------------------------------------------------
-- Check foreign key to users table with CASCADE DELETE
SELECT
    tc.constraint_name,
    tc.table_name,
    kcu.column_name,
    ccu.table_name AS foreign_table_name,
    ccu.column_name AS foreign_column_name,
    rc.delete_rule,
    rc.update_rule
FROM information_schema.table_constraints AS tc
         JOIN information_schema.key_column_usage AS kcu
              ON tc.constraint_name = kcu.constraint_name
         JOIN information_schema.constraint_column_usage AS ccu
              ON ccu.constraint_name = tc.constraint_name
         JOIN information_schema.referential_constraints AS rc
              ON tc.constraint_name = rc.constraint_name
WHERE tc.constraint_type = 'FOREIGN KEY'
  AND tc.table_schema = 'public'
  AND tc.table_name = 'admins';

-- Expected Output:
-- constraint_name | table_name | column_name | foreign_table_name | foreign_column_name | delete_rule | update_rule
-- fk_admins_user  | admins     | user_id     | users              | id                  | CASCADE     | NO ACTION


-- ----------------------------------------------------------------------------
-- 4. VERIFY ALL CONSTRAINTS
-- ----------------------------------------------------------------------------
-- Check all constraints on admins table
SELECT
    constraint_name,
    constraint_type
FROM information_schema.table_constraints
WHERE table_schema = 'public'
  AND table_name = 'admins'
ORDER BY constraint_type;

-- Expected Output:
-- constraint_name | constraint_type
-- fk_admins_user  | FOREIGN KEY
-- pk_admins       | PRIMARY KEY


-- ----------------------------------------------------------------------------
-- 5. VERIFY INDEXES
-- ----------------------------------------------------------------------------
-- Check all indexes on admins table
SELECT
    indexname,
    indexdef
FROM pg_indexes
WHERE schemaname = 'public'
  AND tablename = 'admins'
ORDER BY indexname;

-- Expected Output:
-- indexname           | indexdef
-- idx_admins_role     | CREATE INDEX idx_admins_role ON public.admins USING btree (role)
-- idx_admins_user_id  | CREATE INDEX idx_admins_user_id ON public.admins USING btree (user_id)
-- pk_admins           | CREATE UNIQUE INDEX pk_admins ON public.admins USING btree (user_id)


-- ----------------------------------------------------------------------------
-- 6. VERIFY TABLE AND COLUMN COMMENTS
-- ----------------------------------------------------------------------------
-- Check table comment
SELECT
    obj_description('public.admins'::regclass) AS table_comment;

-- Check column comments
SELECT
    column_name,
    col_description('public.admins'::regclass, ordinal_position) AS column_comment
FROM information_schema.columns
WHERE table_schema = 'public'
  AND table_name = 'admins'
ORDER BY ordinal_position;

-- Expected Output:
-- table_comment: 'Administrators table - JOINED inheritance from users'
-- user_id     | Primary key and foreign key to users table
-- role        | Admin role type (e.g., SUPER_ADMIN, ADMIN, MODERATOR)
-- permissions | JSON object with specific permissions and access controls
-- created_at  | Record creation timestamp
-- updated_at  | Record last update timestamp


-- ----------------------------------------------------------------------------
-- 7. TEST DATA INSERTION - COMPLETE FLOW
-- ----------------------------------------------------------------------------

-- Step 1: Create a test user
INSERT INTO users (id, email, password_hash, user_type, name, phone, status)
VALUES (
    'test-admin-001',
    'testadmin@example.com',
    '$2a$10$encrypted_password_hash_here',
    'ADMIN',
    'Test Admin User',
    '+5511987654321',
    'ACTIVE'
)
ON CONFLICT (id) DO NOTHING;

-- Step 2: Create admin record with permissions
INSERT INTO admins (user_id, role, permissions)
VALUES (
    'test-admin-001',
    'SUPER_ADMIN',
    '{
        "users": ["create", "read", "update", "delete"],
        "bills": ["read", "update", "delete"],
        "reports": ["read", "generate"],
        "system": ["manage_settings", "view_logs", "backup"]
    }'::jsonb
)
ON CONFLICT (user_id) DO NOTHING;

-- Step 3: Verify insertion with JOIN
SELECT
    u.id,
    u.email,
    u.name,
    u.user_type,
    u.status,
    a.role,
    a.permissions,
    a.created_at
FROM users u
INNER JOIN admins a ON u.id = a.user_id
WHERE u.id = 'test-admin-001';


-- ----------------------------------------------------------------------------
-- 8. TEST DIFFERENT ADMIN ROLES
-- ----------------------------------------------------------------------------

-- Insert multiple admin roles for testing
INSERT INTO users (id, email, password_hash, user_type, name, status) VALUES
('admin-super-001', 'super@example.com', 'hash1', 'ADMIN', 'Super Admin', 'ACTIVE'),
('admin-regular-001', 'admin@example.com', 'hash2', 'ADMIN', 'Regular Admin', 'ACTIVE'),
('admin-moderator-001', 'moderator@example.com', 'hash3', 'ADMIN', 'Moderator', 'ACTIVE'),
('admin-support-001', 'support@example.com', 'hash4', 'ADMIN', 'Support Admin', 'ACTIVE')
ON CONFLICT (id) DO NOTHING;

INSERT INTO admins (user_id, role, permissions) VALUES
('admin-super-001', 'SUPER_ADMIN', '{"users": ["create", "read", "update", "delete"], "system": ["full_access"]}'::jsonb),
('admin-regular-001', 'ADMIN', '{"users": ["read", "update"], "bills": ["read", "update", "delete"]}'::jsonb),
('admin-moderator-001', 'MODERATOR', '{"users": ["read"], "bills": ["read"], "support": ["manage_tickets"]}'::jsonb),
('admin-support-001', 'SUPPORT', '{"users": ["read"], "support": ["view_tickets", "respond"]}'::jsonb)
ON CONFLICT (user_id) DO NOTHING;


-- ----------------------------------------------------------------------------
-- 9. QUERY ADMINS BY ROLE
-- ----------------------------------------------------------------------------

-- Get all super admins
SELECT u.email, u.name, a.role, a.permissions
FROM users u
JOIN admins a ON u.id = a.user_id
WHERE a.role = 'SUPER_ADMIN';

-- Count admins by role
SELECT role, COUNT(*) as total
FROM admins
GROUP BY role
ORDER BY total DESC;


-- ----------------------------------------------------------------------------
-- 10. JSONB PERMISSIONS QUERIES
-- ----------------------------------------------------------------------------

-- Find admins with specific permission
SELECT u.email, u.name, a.role
FROM users u
JOIN admins a ON u.id = a.user_id
WHERE a.permissions @> '{"users": ["delete"]}'::jsonb;

-- Find admins with system access
SELECT u.email, u.name, a.role, a.permissions->'system' as system_permissions
FROM users u
JOIN admins a ON u.id = a.user_id
WHERE a.permissions ? 'system';

-- Get all permissions for a specific admin
SELECT
    u.email,
    jsonb_object_keys(a.permissions) as resource,
    a.permissions->jsonb_object_keys(a.permissions) as actions
FROM users u
JOIN admins a ON u.id = a.user_id
WHERE u.email = 'super@example.com';


-- ----------------------------------------------------------------------------
-- 11. TEST CASCADE DELETE
-- ----------------------------------------------------------------------------

-- Create test user and admin
INSERT INTO users (id, email, password_hash, user_type, name, status)
VALUES ('cascade-test-001', 'cascade@test.com', 'hash', 'ADMIN', 'Cascade Test', 'ACTIVE')
ON CONFLICT (id) DO NOTHING;

INSERT INTO admins (user_id, role)
VALUES ('cascade-test-001', 'ADMIN')
ON CONFLICT (user_id) DO NOTHING;

-- Verify both records exist
SELECT 'User exists' as check FROM users WHERE id = 'cascade-test-001'
UNION ALL
SELECT 'Admin exists' as check FROM admins WHERE user_id = 'cascade-test-001';

-- Delete user (should cascade to admin)
DELETE FROM users WHERE id = 'cascade-test-001';

-- Verify admin record was also deleted
SELECT COUNT(*) as remaining_records
FROM admins
WHERE user_id = 'cascade-test-001';
-- Expected: 0


-- ----------------------------------------------------------------------------
-- 12. TEST CONSTRAINT VIOLATIONS
-- ----------------------------------------------------------------------------

-- Test 1: Try to insert admin without user (should fail)
-- Uncomment to test:
-- INSERT INTO admins (user_id, role)
-- VALUES ('non-existent-user-id', 'ADMIN');
-- Expected Error: FOREIGN KEY violation

-- Test 2: Try to insert duplicate admin for same user (should fail)
-- Uncomment to test:
-- INSERT INTO admins (user_id, role)
-- VALUES ('test-admin-001', 'MODERATOR');
-- Expected Error: PRIMARY KEY violation

-- Test 3: Try to insert admin with NULL role (should fail)
-- Uncomment to test:
-- INSERT INTO admins (user_id, role)
-- VALUES ('test-admin-001', NULL);
-- Expected Error: NOT NULL violation


-- ----------------------------------------------------------------------------
-- 13. PERFORMANCE TEST - INDEX USAGE
-- ----------------------------------------------------------------------------

-- Check if index is used for role queries
EXPLAIN ANALYZE
SELECT * FROM admins WHERE role = 'SUPER_ADMIN';
-- Should show: Index Scan using idx_admins_role

-- Check if index is used for user_id queries
EXPLAIN ANALYZE
SELECT * FROM admins WHERE user_id = 'test-admin-001';
-- Should show: Index Scan using pk_admins or idx_admins_user_id


-- ----------------------------------------------------------------------------
-- 14. JOIN PERFORMANCE TEST
-- ----------------------------------------------------------------------------

-- Test JOIN query performance
EXPLAIN ANALYZE
SELECT
    u.email,
    u.name,
    u.status,
    a.role,
    a.permissions
FROM users u
INNER JOIN admins a ON u.id = a.user_id
WHERE u.user_type = 'ADMIN'
  AND u.status = 'ACTIVE';


-- ----------------------------------------------------------------------------
-- 15. COMPLETE ADMIN PROFILE QUERY
-- ----------------------------------------------------------------------------

-- Get complete admin profile with all user information
SELECT
    u.id,
    u.email,
    u.name,
    u.phone,
    u.user_type,
    u.status,
    u.created_at as user_created_at,
    u.updated_at as user_updated_at,
    a.role,
    a.permissions,
    a.created_at as admin_created_at,
    a.updated_at as admin_updated_at
FROM users u
INNER JOIN admins a ON u.id = a.user_id
WHERE u.user_type = 'ADMIN'
ORDER BY a.created_at DESC;


-- ----------------------------------------------------------------------------
-- 16. CLEANUP TEST DATA (Optional)
-- ----------------------------------------------------------------------------

-- Uncomment to clean up test data
/*
DELETE FROM users WHERE id IN (
    'test-admin-001',
    'admin-super-001',
    'admin-regular-001',
    'admin-moderator-001',
    'admin-support-001',
    'cascade-test-001'
);
-- Admin records will be automatically deleted due to CASCADE
*/


-- ============================================================================
-- END OF VALIDATION QUERIES
-- ============================================================================

