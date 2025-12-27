-- ============================================================================
-- V002: Create Clients Table - Validation Queries
-- ============================================================================
-- Description: SQL queries to validate the clients table migration
-- Author: Mateus De La Fuente Cezar
-- Date: 2025-12-15
-- Related Migration: V2__create_clients_table.sql
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. VERIFY TABLE STRUCTURE
-- ----------------------------------------------------------------------------
-- Check if clients table exists and has correct columns
SELECT
    table_name,
    column_name,
    data_type,
    character_maximum_length,
    is_nullable,
    column_default
FROM information_schema.columns
WHERE table_schema = 'public'
  AND table_name = 'clients'
ORDER BY ordinal_position;

-- Expected Output:
-- table_name | column_name        | data_type         | character_maximum_length | is_nullable | column_default
-- clients    | user_id           | uuid              | NULL                     | NO          | NULL
-- clients    | address           | character varying | 500                      | NO          | NULL
-- clients    | city              | character varying | 100                      | YES         | NULL
-- clients    | state             | character varying | 2                        | YES         | NULL
-- clients    | zip_code          | character varying | 10                       | YES         | NULL
-- clients    | cpf               | character varying | 11                       | NO          | NULL
-- clients    | registration_date | date              | NULL                     | NO          | CURRENT_DATE


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
  AND tc.table_name = 'clients'
  AND tc.constraint_type = 'PRIMARY KEY';

-- Expected Output:
-- constraint_name | constraint_type | column_name
-- clients_pkey    | PRIMARY KEY     | user_id


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
  AND tc.table_name = 'clients';

-- Expected Output:
-- constraint_name | table_name | column_name | foreign_table_name | foreign_column_name | delete_rule | update_rule
-- fk_client_user  | clients    | user_id     | users              | id                  | CASCADE     | NO ACTION


-- ----------------------------------------------------------------------------
-- 4. VERIFY UNIQUE CONSTRAINT ON CPF
-- ----------------------------------------------------------------------------
-- Check unique constraint
SELECT
    tc.constraint_name,
    tc.constraint_type,
    kcu.column_name
FROM information_schema.table_constraints tc
         JOIN information_schema.key_column_usage kcu
              ON tc.constraint_name = kcu.constraint_name
WHERE tc.table_schema = 'public'
  AND tc.table_name = 'clients'
  AND tc.constraint_type = 'UNIQUE';

-- Expected Output:
-- constraint_name  | constraint_type | column_name
-- clients_cpf_key  | UNIQUE         | cpf


-- ----------------------------------------------------------------------------
-- 5. VERIFY CHECK CONSTRAINT ON CPF FORMAT
-- ----------------------------------------------------------------------------
-- Check CPF format validation
SELECT
    con.conname AS constraint_name,
    pg_get_constraintdef(con.oid) AS constraint_definition
FROM pg_constraint con
         JOIN pg_class rel ON rel.oid = con.conrelid
         JOIN pg_namespace nsp ON nsp.oid = rel.relnamespace
WHERE nsp.nspname = 'public'
  AND rel.relname = 'clients'
  AND con.contype = 'c';

-- Expected Output:
-- constraint_name   | constraint_definition
-- chk_cpf_format    | CHECK ((cpf ~ '^\d{11}$'::text))


-- ----------------------------------------------------------------------------
-- 6. VERIFY INDEXES
-- ----------------------------------------------------------------------------
-- Check created indexes
SELECT
    schemaname,
    tablename,
    indexname,
    indexdef
FROM pg_indexes
WHERE schemaname = 'public'
  AND tablename = 'clients'
ORDER BY indexname;

-- Expected Output:
-- schemaname | tablename | indexname                    | indexdef
-- public     | clients   | clients_cpf_key              | CREATE UNIQUE INDEX clients_cpf_key ON public.clients USING btree (cpf)
-- public     | clients   | clients_pkey                 | CREATE UNIQUE INDEX clients_pkey ON public.clients USING btree (user_id)
-- public     | clients   | idx_clients_cpf              | CREATE INDEX idx_clients_cpf ON public.clients USING btree (cpf)
-- public     | clients   | idx_clients_registration_date| CREATE INDEX idx_clients_registration_date ON public.clients USING btree (registration_date)


-- ----------------------------------------------------------------------------
-- 7. FUNCTIONAL TEST: INSERT VALID CLIENT
-- ----------------------------------------------------------------------------
-- Test 1: Create user and client with valid data
BEGIN;

-- Insert user first
INSERT INTO users (email, password, name, phone, status)
VALUES (
           'client.test@example.com',
           '$2a$10$abcdefghijklmnopqrstuv', -- BCrypt hashed password
           'João Silva',
           '+5511999999999',
           'ACTIVE'
       )
    RETURNING id;

-- Copy the UUID from above and use in the next INSERT
-- Replace <USER_ID> with the actual UUID returned above
INSERT INTO clients (user_id, address, cpf, registration_date)
VALUES (
           '<USER_ID>',
           'Rua Teste, 123, São Paulo - SP, CEP 01234-567',
           '12345678901',
           CURRENT_DATE
       );

-- Verify insertion
SELECT
    u.id,
    u.email,
    u.name,
    u.phone,
    u.status,
    c.address,
    c.cpf,
    c.registration_date,
    u.created_at
FROM users u
         INNER JOIN clients c ON c.user_id = u.id
WHERE u.email = 'client.test@example.com';

ROLLBACK; -- or COMMIT to keep the test data


-- ----------------------------------------------------------------------------
-- 8. FUNCTIONAL TEST: JOIN INHERITANCE (JOINED STRATEGY)
-- ----------------------------------------------------------------------------
-- Test 2: Verify JOINED inheritance strategy works correctly
BEGIN;

-- Insert test data
INSERT INTO users (email, password, name, status)
VALUES
    ('client1@test.com', 'password123', 'Maria Santos', 'ACTIVE'),
    ('client2@test.com', 'password456', 'Pedro Costa', 'PENDING_VERIFICATION');

-- Get the IDs
WITH user_ids AS (
    SELECT id, email FROM users WHERE email IN ('client1@test.com', 'client2@test.com')
)
INSERT INTO clients (user_id, address, cpf)
SELECT
    id,
    CASE
        WHEN email = 'client1@test.com' THEN 'Rua A, 100'
        ELSE 'Rua B, 200'
        END,
    CASE
        WHEN email = 'client1@test.com' THEN '98765432100'
        ELSE '11122233344'
        END
FROM user_ids;

-- Query using JOINED strategy
SELECT
    u.id,
    u.email,
    u.name,
    u.status,
    c.cpf,
    c.address,
    c.registration_date
FROM users u
         INNER JOIN clients c ON u.id = c.user_id
ORDER BY u.email;

ROLLBACK; -- or COMMIT


-- ----------------------------------------------------------------------------
-- 9. FUNCTIONAL TEST: CASCADE DELETE
-- ----------------------------------------------------------------------------
-- Test 3: Verify ON DELETE CASCADE works
BEGIN;

-- Insert test user and client
INSERT INTO users (email, password, name, status)
VALUES ('delete.test@example.com', 'password', 'Test User', 'ACTIVE')
    RETURNING id;

-- Use returned ID
INSERT INTO clients (user_id, address, cpf)
VALUES ('<USER_ID>', 'Rua Delete, 999', '55566677788');

-- Count before delete
SELECT COUNT(*) as clients_before FROM clients WHERE cpf = '55566677788';
-- Expected: 1

-- Delete user
DELETE FROM users WHERE email = 'delete.test@example.com';

-- Count after delete - should be 0 due to CASCADE
SELECT COUNT(*) as clients_after FROM clients WHERE cpf = '55566677788';
-- Expected: 0

ROLLBACK;


-- ----------------------------------------------------------------------------
-- 10. FUNCTIONAL TEST: CPF UNIQUENESS CONSTRAINT
-- ----------------------------------------------------------------------------
-- Test 4: Verify CPF uniqueness is enforced
BEGIN;

-- Insert first client with CPF
INSERT INTO users (email, password, name, status)
VALUES ('unique1@test.com', 'password', 'User One', 'ACTIVE')
    RETURNING id;

INSERT INTO clients (user_id, address, cpf)
VALUES ('<USER_ID_1>', 'Address 1', '99988877766');

-- Try to insert another client with same CPF - SHOULD FAIL
INSERT INTO users (email, password, name, status)
VALUES ('unique2@test.com', 'password', 'User Two', 'ACTIVE')
    RETURNING id;

-- This INSERT should fail with: ERROR: duplicate key value violates unique constraint "clients_cpf_key"
INSERT INTO clients (user_id, address, cpf)
VALUES ('<USER_ID_2>', 'Address 2', '99988877766');

ROLLBACK;


-- ----------------------------------------------------------------------------
-- 11. FUNCTIONAL TEST: CPF FORMAT VALIDATION
-- ----------------------------------------------------------------------------
-- Test 5: Verify CPF format CHECK constraint
BEGIN;

INSERT INTO users (email, password, name, status)
VALUES ('format.test@example.com', 'password', 'Format Test', 'ACTIVE')
    RETURNING id;

-- Valid CPF (11 digits)
INSERT INTO clients (user_id, address, cpf)
VALUES ('<USER_ID>', 'Address', '12345678901'); -- Should succeed

-- Invalid CPF formats - ALL SHOULD FAIL
-- Too short
INSERT INTO clients (user_id, address, cpf)
VALUES ('<USER_ID>', 'Address', '123456789');
-- Expected: ERROR: new row for relation "clients" violates check constraint "chk_cpf_format"

-- Too long
INSERT INTO clients (user_id, address, cpf)
VALUES ('<USER_ID>', 'Address', '123456789012');
-- Expected: ERROR: new row for relation "clients" violates check constraint "chk_cpf_format"

-- With non-numeric characters
INSERT INTO clients (user_id, address, cpf)
VALUES ('<USER_ID>', 'Address', '123.456.789-01');
-- Expected: ERROR: new row for relation "clients" violates check constraint "chk_cpf_format"

ROLLBACK;


-- ----------------------------------------------------------------------------
-- 12. FUNCTIONAL TEST: STATE FORMAT VALIDATION
-- ----------------------------------------------------------------------------
-- Test 6: Verify state format CHECK constraint
BEGIN;

INSERT INTO users (email, password, name, status)
VALUES ('state.test@example.com', 'password', 'State Test', 'ACTIVE')
    RETURNING id;

-- Valid state (2 uppercase letters)
INSERT INTO clients (user_id, address, cpf, state)
VALUES ('<USER_ID>', 'Address', '11111111111', 'SP'); -- Should succeed

-- Valid NULL state
INSERT INTO clients (user_id, address, cpf, state)
VALUES ('<USER_ID_2>', 'Address', '22222222222', NULL); -- Should succeed

-- Invalid state formats - ALL SHOULD FAIL
-- Lowercase
INSERT INTO clients (user_id, address, cpf, state)
VALUES ('<USER_ID_3>', 'Address', '33333333333', 'sp');
-- Expected: ERROR: new row for relation "clients" violates check constraint "chk_state_format"

-- Too short
INSERT INTO clients (user_id, address, cpf, state)
VALUES ('<USER_ID_4>', 'Address', '44444444444', 'S');
-- Expected: ERROR: new row for relation "clients" violates check constraint "chk_state_format"

-- Too long
INSERT INTO clients (user_id, address, cpf, state)
VALUES ('<USER_ID_5>', 'Address', '55555555555', 'SAO');
-- Expected: ERROR: new row for relation "clients" violates check constraint "chk_state_format"

ROLLBACK;


-- ----------------------------------------------------------------------------
-- 13. FUNCTIONAL TEST: ZIP CODE FORMAT VALIDATION
-- ----------------------------------------------------------------------------
-- Test 7: Verify zip_code format CHECK constraint
BEGIN;

INSERT INTO users (email, password, name, status)
VALUES ('zipcode.test@example.com', 'password', 'ZipCode Test', 'ACTIVE')
    RETURNING id;

-- Valid zip_code with hyphen (format: 12345-678)
INSERT INTO clients (user_id, address, cpf, zip_code)
VALUES ('<USER_ID>', 'Address', '66666666666', '12345-678'); -- Should succeed

-- Valid zip_code without hyphen (format: 12345678)
INSERT INTO clients (user_id, address, cpf, zip_code)
VALUES ('<USER_ID_2>', 'Address', '77777777777', '12345678'); -- Should succeed

-- Valid NULL zip_code
INSERT INTO clients (user_id, address, cpf, zip_code)
VALUES ('<USER_ID_3>', 'Address', '88888888888', NULL); -- Should succeed

-- Invalid zip_code formats - ALL SHOULD FAIL
-- Too short
INSERT INTO clients (user_id, address, cpf, zip_code)
VALUES ('<USER_ID_4>', 'Address', '99999999999', '12345');
-- Expected: ERROR: new row for relation "clients" violates check constraint "chk_zipcode_format"

-- Invalid format (missing digits)
INSERT INTO clients (user_id, address, cpf, zip_code)
VALUES ('<USER_ID_5>', 'Address', '10101010101', '12345-67');
-- Expected: ERROR: new row for relation "clients" violates check constraint "chk_zipcode_format"

-- With letters
INSERT INTO clients (user_id, address, cpf, zip_code)
VALUES ('<USER_ID_6>', 'Address', '20202020202', '1234A-678');
-- Expected: ERROR: new row for relation "clients" violates check constraint "chk_zipcode_format"

ROLLBACK;


-- ============================================================================
-- END OF VALIDATION QUERIES
-- ============================================================================
-- Notes:
-- - Replace <USER_ID> placeholders with actual UUIDs from INSERT...RETURNING
-- - All tests use transactions (BEGIN/ROLLBACK) for safety
-- - Execute queries in order for proper validation flow
-- Updated: 2025-12-27 - Added tests for city, state, and zip_code columns
-- ============================================================================
