# V003 - Create Consultants Table Migration

## Overview

| Field | Value |
|-------|-------|
| **Migration Version** | V003 |
| **File Name** | `V003__create_consultants_table.sql` |
| **Description** | Create consultants table following JOINED inheritance strategy |
| **Depends On** | V001 (users table) |
| **Date Created** | 2025-12-15 |
| **Date Updated** | 2025-12-27 |
| **Author** | Mateus De La Fuente Cezar |

---

## Purpose

Create the `consultants` table to store consultant-specific data, implementing the JOINED inheritance strategy where consultant data is stored in a separate table linked to the `users` base table via foreign key.

---

## Schema Design

### Table: `consultants`

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| user_id | UUID | PRIMARY KEY, FK -> users(id) | Reference to base user record |
| consultant_name | VARCHAR(255) | NOT NULL | Full name of the consultant |
| company | VARCHAR(255) | NOT NULL | Company name |
| cnpj | VARCHAR(14) | NOT NULL, UNIQUE | Brazilian company tax ID (14 digits) |
| registration_number | VARCHAR(50) | NULL | Professional registration number (optional) |
| address | VARCHAR(500) | NOT NULL | Full address of the consultant/company |
| city | VARCHAR(100) | NULL | City (optional) |
| state | VARCHAR(2) | NULL | Brazilian state code - 2 uppercase letters (optional) |
| zip_code | VARCHAR(10) | NULL | ZIP code (format: 00000-000 or 00000000) (optional) |
| company_logo | TEXT | NULL | Base64 encoded logo or URL (optional) |
| registration_date | DATE | NOT NULL, DEFAULT CURRENT_DATE | Consultant registration date |

**Note:** `created_at` and `updated_at` are inherited from the `users` table, not duplicated here.

---

## Constraints

### Primary Key
- **Name:** `consultants_pkey`
- **Column:** `user_id`
- **Purpose:** Ensure one consultant record per user

### Foreign Key
- **Name:** `fk_consultant_user`
- **Column:** `user_id`
- **References:** `users(id)`
- **On Delete:** CASCADE

### Unique Constraint
- **Name:** `consultants_cnpj_key`
- **Column:** `cnpj`
- **Purpose:** Prevent duplicate CNPJ registration

### Check Constraints

#### chk_cnpj_format
- **Definition:** `cnpj ~ '^\d{14}$'`
- **Purpose:** Ensure CNPJ contains exactly 14 digits

#### chk_state_format
- **Definition:** `state IS NULL OR state ~ '^[A-Z]{2}$'`
- **Purpose:** Validate state code format when provided

#### chk_zipcode_format
- **Definition:** `zip_code IS NULL OR zip_code ~ '^\d{5}-?\d{3}$'`
- **Purpose:** Validate ZIP code format when provided

---

## Indexes

| Index Name | Type | Column(s) | Purpose |
|------------|------|-----------|---------|
| consultants_pkey | UNIQUE BTREE | user_id | Primary key enforcement |
| consultants_cnpj_key | UNIQUE BTREE | cnpj | Uniqueness + fast CNPJ lookups |
| idx_consultants_cnpj | BTREE | cnpj | Fast CNPJ search |
| idx_consultants_company | BTREE | company | Search by company name |
| idx_consultants_registration_date | BTREE | registration_date | Sort/filter by date |
| idx_consultants_city | BTREE | city | Filter by city |
| idx_consultants_state | BTREE | state | Filter by state |

---
- registration_date defaults to current date if not provided
- Consultant can have optional company_logo for branding
- Consultants can manage multiple electricity bills (1:N relationship)
- Both share similar address structure
- Consultants are company-based users (CNPJ) vs clients who are individuals (CPF)

## Notes

---

- [V002 Migration](../V002/migration.md) - Clients table
- [V001 Migration](../V001/migration.md) - Users table
- [ER Diagram](../../ER_DIAGRAM.md)
- [JPA Inheritance Strategy](../../jpa-inheritance-strategy.md)

## Related Documentation

---

- Test data retrieval
- Verify JOIN query works
- Create consultant record
- Create user in users table
### 4. Integration Testing

- Test query performance with indexes
- Verify all 3 indexes created
### 3. Index Verification

- Test valid ZIP code format (8 digits)
- Test valid state format (2 uppercase letters)
- Test CASCADE delete (deleting user should delete consultant)
- Test duplicate CNPJ (should fail)
- Test invalid CNPJ format (should fail)
- Test valid CNPJ format (14 digits)
### 2. Constraint Testing

- Verify check constraints
- Verify unique constraint on cnpj
- Verify foreign key to users table
- Verify primary key constraint
- Verify all columns with correct types
- Verify table exists
### 1. Structure Validation

## Testing Strategy

---

```
DROP TABLE IF EXISTS consultants CASCADE;
-- Drop table (CASCADE will handle any dependencies)

DROP INDEX IF EXISTS idx_consultants_cnpj;
DROP INDEX IF EXISTS idx_consultants_company;
DROP INDEX IF EXISTS idx_consultants_registration_date;
-- Drop indexes first
```sql

If this migration needs to be reverted:

## Rollback Strategy

---

```
COMMENT ON COLUMN consultants.registration_date IS 'Date when the consultant was registered';
COMMENT ON COLUMN consultants.company IS 'Company name';
COMMENT ON COLUMN consultants.consultant_name IS 'Full name of the consultant';
COMMENT ON COLUMN consultants.cnpj IS 'Brazilian company tax ID (CNPJ) - 14 digits only';
COMMENT ON COLUMN consultants.user_id IS 'Foreign key to users table (same as primary key)';
COMMENT ON TABLE consultants IS 'Consultant-specific data extending the users table';
-- Add comments for documentation

CREATE INDEX idx_consultants_registration_date ON consultants(registration_date);
CREATE INDEX idx_consultants_company ON consultants(company);
CREATE INDEX idx_consultants_cnpj ON consultants(cnpj);
-- Create indexes for performance

);
    CONSTRAINT chk_zip_code_format CHECK (zip_code ~ '^\d{8}$')
    CONSTRAINT chk_state_format CHECK (state ~ '^[A-Z]{2}$'),
    CONSTRAINT chk_cnpj_format CHECK (cnpj ~ '^\d{14}$'),
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_consultant_user FOREIGN KEY (user_id)
    registration_date DATE NOT NULL DEFAULT CURRENT_DATE,
    company_logo VARCHAR(255),
    registration_number VARCHAR(50),
    zip_code VARCHAR(8),
    state VARCHAR(2),
    city VARCHAR(100),
    address VARCHAR(500),
    cnpj VARCHAR(14) NOT NULL UNIQUE,
    company VARCHAR(255) NOT NULL,
    consultant_name VARCHAR(255) NOT NULL,
    user_id UUID PRIMARY KEY,
CREATE TABLE consultants (

-- ============================================================================
-- Date: 2024-12-15
-- Author: Mateus De La Fuente Cezar
-- Description: Creates the base Consultants table for JOINED inheritance strategy
-- Migration: V003 - Create Consultants Base Table
-- ============================================================================
```sql

## Migration Script

---

3. **idx_consultants_registration_date** - Date-based queries
2. **idx_consultants_company** - Search by company name
1. **idx_consultants_cnpj** - Lookup by CNPJ (most common query)
### Performance Indexes

## Indexes

---

- `chk_zip_code_format` - Validates ZIP code format (8 digits)
- `chk_state_format` - Validates state format (2 uppercase letters)
- `chk_cnpj_format` - Validates CNPJ format (14 digits)
### Check Constraints

- `cnpj` - Prevents duplicate company registrations
### Unique Constraints

- `fk_consultant_user` - Links to users table with CASCADE delete
### Foreign Keys

- `user_id` - Ensures one consultant record per user
### Primary Key

## Constraints

---

| **Address Fields** | ✅ Same structure | ✅ Same structure |
| **Professional License** | ❌ | ✅ registration_number |
| **Company Info** | ❌ | ✅ company, company_logo |
| **Name Field** | Inherited from users.name | consultant_name |
| **Type** | Individual (PF) | Company (PJ) |
| **Tax ID** | CPF (11 digits) | CNPJ (14 digits) |
|--------|--------|------------|
| Aspect | Client | Consultant |

## Key Differences: Consultant vs Client

---

4. JOIN queries retrieve complete user profile
3. `user_id` acts as both PRIMARY KEY and FOREIGN KEY
2. Consultant-specific data stored in `consultants` table
1. Common user data stored in `users` table
**How it works:**

- ✅ Referential integrity via foreign keys
- ✅ Scalable for multiple user types (clients, consultants, admins)
- ✅ Easy to query specific user types
- ✅ No NULL columns in base table
- ✅ Clean separation of concerns (base vs specific data)
**Why JOINED?**

## Inheritance Strategy: JOINED

---

| registration_date | DATE | NOT NULL, DEFAULT CURRENT_DATE | Registration date |
| company_logo | VARCHAR(255) | | URL/path to company logo |
| registration_number | VARCHAR(50) | | Professional license number |
| zip_code | VARCHAR(8) | | ZIP code (8 digits) |
| state | VARCHAR(2) | | State abbreviation (UF) |
| city | VARCHAR(100) | | City |
| address | VARCHAR(500) | | Complete business address |
| cnpj | VARCHAR(14) | NOT NULL, UNIQUE | Brazilian company tax ID (14 digits) |
| company | VARCHAR(255) | NOT NULL | Company name |
| consultant_name | VARCHAR(255) | NOT NULL | Consultant's full name |
| user_id | UUID | PRIMARY KEY, FK -> users(id) | Reference to base user record |
|--------|------|-------------|-------------|
| Column | Type | Constraints | Description |

### Table: `consultants`

## Schema Design

---

Create the `consultants` table to store consultant-specific data, implementing the JOINED inheritance strategy where consultant data is stored in a separate table linked to the `users` base table via foreign key.

## Purpose

---

| **Author** | Mateus De La Fuente Cezar |
| **Date Created** | 2024-12-15 |
| **Depends On** | V001 (users table) |
| **Description** | Create consultants table following JOINED inheritance strategy |
| **File Name** | `V3__create_consultants_table.sql` |
| **Migration Version** | V003 |
|-------|-------|
| Field | Value |

## Overview


