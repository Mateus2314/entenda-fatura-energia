# V002 - Create Clients Table Migration

## Overview

| Field | Value |
|-------|-------|
| **Migration Version** | V002 |
| **File Name** | `V2__create_clients_table.sql` |
| **Description** | Create clients table following JOINED inheritance strategy |
| **Depends On** | V001 (users table) |
| **Date Created** | 2025-12-15 |
| **Author** | Mateus De La Fuente Cezar |

---

## Purpose

Create the `clients` table to store client-specific data, implementing the JOINED inheritance strategy where client data is stored in a separate table linked to the `users` base table via foreign key.

---

## Schema Design

### Table: `clients`

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| user_id | UUID | PRIMARY KEY, FK -> users(id) | Reference to base user record |
| address | VARCHAR(500) | NOT NULL | Client full address |
| city | VARCHAR(100) | NULL | Client city (optional) |
| state | VARCHAR(2) | NULL | Client state code - 2 uppercase letters (optional) |
| zip_code | VARCHAR(10) | NULL | Client ZIP code (format: 00000-000 or 00000000) (optional) |
| cpf | VARCHAR(11) | NOT NULL, UNIQUE | Brazilian individual tax ID (11 digits) |
| registration_date | DATE | NOT NULL, DEFAULT CURRENT_DATE | Client registration date |

**Note:** `created_at` and `updated_at` are inherited from the `users` table, not duplicated here.

---

## Inheritance Strategy: JOINED

**Why JOINED?**
- ✅ Clean separation of concerns (base vs specific data)
- ✅ No NULL columns in base table
- ✅ Easy to query specific user types
- ✅ Scalable for multiple user types (consultants, admins)
- ✅ Referential integrity via foreign keys

**How it works:**
1. Common user data stored in `users` table
2. Client-specific data stored in `clients` table
3. `user_id` acts as both PRIMARY KEY and FOREIGN KEY
4. JOIN queries retrieve complete user profile

---

## Relationships

```
users (1) ----< (1) clients
  |                   |
  id  ------------  user_id
```

**ON DELETE CASCADE:** When a user is deleted, corresponding client record is automatically deleted.

---

## Constraints

### Primary Key
- **Name:** `clients_pkey`
- **Column:** `user_id`
- **Purpose:** Ensure one client record per user

### Foreign Key
- **Name:** `fk_client_user`
- **Column:** `user_id`
- **References:** `users(id)`
- **On Delete:** CASCADE
- **On Update:** NO ACTION

### Unique Constraint
- **Name:** `clients_cpf_key`
- **Column:** `cpf`
- **Purpose:** Prevent duplicate CPF registration

### Check Constraints

#### chk_cpf_format
- **Definition:** `cpf ~ '^\d{11}$'`
- **Purpose:** Ensure CPF contains exactly 11 digits (no formatting)

#### chk_state_format
- **Definition:** `state IS NULL OR state ~ '^[A-Z]{2}$'`
- **Purpose:** Validate state code format (2 uppercase letters) when provided

#### chk_zipcode_format
- **Definition:** `zip_code IS NULL OR zip_code ~ '^\d{5}-?\d{3}$'`
- **Purpose:** Validate ZIP code format (00000-000 or 00000000) when provided

---

## Indexes

| Index Name | Type | Column(s) | Purpose |
|------------|------|-----------|---------|
| clients_pkey | UNIQUE BTREE | user_id | Primary key enforcement |
| clients_cpf_key | UNIQUE BTREE | cpf | Uniqueness + fast CPF lookups |
| idx_clients_cpf | BTREE | cpf | Fast CPF search (redundant with unique) |
| idx_clients_registration_date | BTREE | registration_date | Sort/filter by registration date |
| idx_clients_city | BTREE | city | Filter by city |
| idx_clients_state | BTREE | state | Filter by state |
| idx_clients_zipcode | BTREE | zip_code | Filter by ZIP code |
| idx_clients_registration_date | BTREE | registration_date | Query clients by registration date |

---

## Migration SQL

```sql
-- V2__create_clients_table.sql
CREATE TABLE clients (
    user_id UUID PRIMARY KEY,
    address VARCHAR(500) NOT NULL,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    registration_date DATE NOT NULL DEFAULT CURRENT_DATE,
    CONSTRAINT fk_client_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_cpf_format CHECK (cpf ~ '^\d{11}$')
);

CREATE INDEX idx_clients_cpf ON clients(cpf);
CREATE INDEX idx_clients_registration_date ON clients(registration_date);

COMMENT ON TABLE clients IS 'Client-specific data extending the users table';
COMMENT ON COLUMN clients.user_id IS 'Foreign key to users table (same as primary key)';
COMMENT ON COLUMN clients.cpf IS 'Brazilian individual tax ID (CPF) - 11 digits only';
COMMENT ON COLUMN clients.registration_date IS 'Date when the client was registered';
```

---

## Usage Examples

### Insert Client
```sql
-- Step 1: Create user
INSERT INTO users (email, password, name, phone, status)
VALUES ('client@example.com', 'hashed_password', 'João Silva', '+5511999999999', 'ACTIVE')
RETURNING id;

-- Step 2: Create client (using returned UUID)
INSERT INTO clients (user_id, address, cpf, registration_date)
VALUES ('<user_id_from_step1>', 'Rua Teste, 123, São Paulo - SP', '12345678901', CURRENT_DATE);
```

### Query Client (JOINED)
```sql
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
WHERE u.email = 'client@example.com';
```

### Delete Client
```sql
-- Deleting user automatically deletes client (CASCADE)
DELETE FROM users WHERE email = 'client@example.com';
```

---

## Rollback Strategy

**⚠️ Important:** Flyway does NOT support automatic rollback. Always test migrations in development first.

**Manual Rollback (if needed):**
```sql
DROP TABLE IF EXISTS clients CASCADE;
DELETE FROM flyway_schema_history WHERE version = '2';
```

---

## Testing Requirements

### Unit Tests
- [ ] Table structure matches specification
- [ ] All constraints created correctly
- [ ] All indexes created
- [ ] Foreign key references users table
- [ ] CASCADE delete works

### Integration Tests
- [ ] Insert valid client succeeds
- [ ] JOIN query retrieves complete profile
- [ ] Duplicate CPF rejected
- [ ] Invalid CPF format rejected
- [ ] Deleting user cascades to client

---

## Performance Considerations

### Expected Load
- **Initial:** ~1000 clients
- **Growth:** ~500 new clients/month
- **Queries:** Frequent JOINs between users and clients

### Index Strategy
- `cpf` index: Fast lookups by CPF (unique constraint already creates index)
- `registration_date` index: Support date range queries

### Query Optimization
```sql
-- Use INNER JOIN for active clients only
SELECT u.*, c.*
FROM users u
INNER JOIN clients c ON c.user_id = u.id
WHERE u.status = 'ACTIVE'
  AND c.registration_date >= '2025-01-01';
```

---

## Dependencies

### Prerequisites
- ✅ V001 migration applied (users table exists)
- ✅ PostgreSQL 12+ (for UUID support)
- ✅ Flyway configured

### Next Migrations
- V003: Create `consultants` table
- V004: Create `admins` table

---

## Validation

See [validation.md](./validation.md) for detailed test results.

**Quick Validation:**
```sql
-- Check table exists
SELECT * FROM information_schema.tables WHERE table_name = 'clients';

-- Check constraints
SELECT constraint_name, constraint_type 
FROM information_schema.table_constraints 
WHERE table_name = 'clients';

-- Test INSERT
BEGIN;
INSERT INTO users (email, password, name, status) 
VALUES ('test@test.com', 'pass', 'Test', 'ACTIVE') RETURNING id;
INSERT INTO clients (user_id, address, cpf) 
VALUES ('<returned_id>', 'Address', '12345678901');
ROLLBACK;
```

---

## References

- [JPA Inheritance Strategy Documentation](../../jpa-inheritance-strategy.md)
- [Users Table Migration (V001)](../V001/migration.md)
- [Scope Documentation](../../../scopo/01_scope.md)

---

**Status:** ✅ Completed  
**Applied:** 2025-12-15  
**Validated:** 2025-12-15

