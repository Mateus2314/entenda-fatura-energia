# Migration V001: Create Users Base Table

## 📋 Overview

**Version:** V001  
**Date:** 2024-12-14  
**Author:** Backend Team  
**Status:** ✅ Implemented and Validated  
**Script:** `V1__create_user_table.sql`  

---

## 🎯 Objective

Create the base `users` table for implementing JOINED inheritance strategy in the user hierarchy.

---

## 📊 What Was Created

### Tables

#### `users` (Base Table)
Base table for user hierarchy using JOINED inheritance.

**Columns:**

| Column | Type | Nullable | Default | Description |
|--------|------|----------|---------|-------------|
| `id` | UUID | NO | `uuid_generate_v4()` | Primary key, unique identifier |
| `email` | VARCHAR(255) | NO | - | Unique login email with format validation |
| `password_hash` | VARCHAR(255) | NO | - | BCrypt hashed password |
| `user_type` | user_type (ENUM) | NO | - | Discriminator: CLIENT, CONSULTANT, ADMIN |
| `name` | VARCHAR(255) | NO | - | Full name of the user |
| `phone` | VARCHAR(20) | YES | NULL | Contact phone (optional) |
| `status` | user_status (ENUM) | NO | 'PENDING_VERIFICATION' | Account status |
| `created_at` | TIMESTAMP | NO | NOW() | Creation timestamp |
| `updated_at` | TIMESTAMP | NO | NOW() | Last update timestamp (auto-updated) |

### ENUMs

#### `user_type`
```sql
CREATE TYPE user_type AS ENUM ('CLIENT', 'CONSULTANT', 'ADMIN');
```

Possible values:
- `CLIENT` - End consumer who analyzes own bills
- `CONSULTANT` - Professional who manages client bills
- `ADMIN` - System administrator

#### `user_status`
```sql
CREATE TYPE user_status AS ENUM ('ACTIVE', 'INACTIVE', 'SUSPENDED', 'PENDING_VERIFICATION');
```

Possible values:
- `ACTIVE` - Active account, full access
- `INACTIVE` - Temporarily inactive
- `SUSPENDED` - Account suspended by admin
- `PENDING_VERIFICATION` - Awaiting email verification (default)

### Constraints

1. **Primary Key:** `users_pkey` on `id`
2. **Unique Constraint:** `users_email_key` on `email`
3. **Check Constraint:** `email_format_check` - Validates email format using regex
4. **Check Constraint:** `phone_format_check` - Validates phone format (optional field)

### Indexes

Created for query performance optimization:

1. `idx_users_email` - Most common query (login)
2. `idx_users_user_type` - Filter by user type
3. `idx_users_status` - Filter by status
4. `idx_users_created_at` - Sort by registration date

### Triggers & Functions

#### `update_updated_at_column()` Function
```sql
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
```

#### `update_users_updated_at` Trigger
Automatically updates `updated_at` field on every UPDATE operation.

---

## 🔄 Migration Dependencies

### Prerequisites
- PostgreSQL 12+ (we're using 17.7)
- `uuid-ossp` extension

### Child Tables (Next Migrations)
This table will be the parent for:
- `clients` (V002) - via `user_id` FK
- `consultants` (V003) - via `user_id` FK
- `admins` (V004) - via `user_id` FK

---

## 🧪 Testing Performed

See [validation.md](./validation.md) for detailed validation report.

**Summary:**
- ✅ Table structure validated
- ✅ ENUMs created correctly
- ✅ Constraints working
- ✅ Indexes created
- ✅ Trigger functioning
- ✅ Insert/Update/Delete operations working
- ✅ Validation rules enforced

---

## 📝 SQL Script

**Location:** `src/main/resources/db/migration/V1__create_user_table.sql`

**Checksum:** Registered in `flyway_schema_history`

**Execution Time:** ~200ms

---

## 🔙 Rollback

If needed, to rollback this migration:

```sql
-- WARNING: This will delete all user data!
DROP TRIGGER IF EXISTS update_users_updated_at ON users;
DROP FUNCTION IF EXISTS update_updated_at_column();
DROP TABLE IF EXISTS users CASCADE;
DROP TYPE IF EXISTS user_status;
DROP TYPE IF EXISTS user_type;
```

⚠️ **Note:** In production, never rollback. Create a new migration instead.

---

## 📚 References

- [JPA Inheritance Strategy Documentation](../../jpa-inheritance-strategy.md)
- [Data Models](../../scopo/07_data_models.md)
- [System Architecture Diagram](../../diagram.md)

---

## 📅 Timeline

- **2024-12-14:** Migration created
- **2024-12-14:** Migration executed successfully
- **2024-12-14:** Validation completed
- **2024-12-14:** Documentation finalized

---

## ✅ Status: COMPLETE

Migration V001 is fully implemented, tested, and validated.
Ready for production deployment.

