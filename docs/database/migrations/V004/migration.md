# V004 - Create Admins Table Migration

## Overview

| Field | Value |
|-------|-------|
| **Migration Version** | V004 |
| **File Name** | `V004__create_admins_table.sql` |
| **Description** | Create admins table following JOINED inheritance strategy |
| **Depends On** | V001 (users table) |
| **Date Created** | 2024-12-16 |
| **Author** | System |

---

## Purpose

Create the `admins` table to store administrator-specific data, implementing the JOINED inheritance strategy where admin data is stored in a separate table linked to the `users` base table via foreign key.

---

## Schema Design

### Table: `admins`

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| user_id | UUID | PRIMARY KEY, FK -> users(id) | Reference to base user record |
| role | VARCHAR(50) | NOT NULL | Admin role type (e.g., SUPER_ADMIN, ADMIN, MODERATOR) |
| permissions | JSONB | NULL | JSON object with specific permissions and access controls |

**Note:** `created_at` and `updated_at` are inherited from the `users` table, not duplicated here.

---

## Inheritance Strategy: JOINED

**Why JOINED?**
- ✅ Clean separation of concerns (base vs specific data)
- ✅ No NULL columns in base table
- ✅ Easy to query specific user types
- ✅ Scalable for multiple user types (clients, consultants, admins)
- ✅ Referential integrity via foreign keys

**How it works:**
1. Common user data stored in `users` table
2. Admin-specific data stored in `admins` table
3. `user_id` acts as both PRIMARY KEY and FOREIGN KEY
4. JOIN queries retrieve complete user profile

---

## Relationships

```
users (1) ----< (1) admins
  |                  |
  id  ------------  user_id
```

**ON DELETE CASCADE:** When a user is deleted, corresponding admin record is automatically deleted.

---

## Constraints

### Primary Key
- **Name:** `pk_admins`
- **Column:** `user_id`
- **Purpose:** Ensure one admin record per user

### Foreign Key
- **Name:** `fk_admins_user`
- **Column:** `user_id`
- **References:** `users(id)`
- **On Delete:** CASCADE
- **On Update:** NO ACTION

---

## Indexes

| Index Name | Type | Column(s) | Purpose |
|------------|------|-----------|---------|
| pk_admins | UNIQUE BTREE | user_id | Primary key enforcement |
| idx_admins_role | BTREE | role | Fast role-based queries |

---

## Permissions Structure

The `permissions` column uses JSONB format to store flexible access controls:

### Example Permissions JSON:
```json
{
  "users": ["create", "read", "update", "delete"],
  "bills": ["read", "update", "delete"],
  "reports": ["read", "generate"],
  "system": ["manage_settings", "view_logs"]
}
```

### Suggested Role Types:
- `SUPER_ADMIN` - Full system access
- `ADMIN` - User and bill management
- `MODERATOR` - Content moderation and support
- `SUPPORT` - Customer support access

---

## Testing Strategy

### 1. Structure Validation
- Verify table exists
- Verify all columns with correct types
- Verify primary key constraint
- Verify foreign key to users table

### 2. Constraint Testing
- Test CASCADE delete (deleting user should delete admin)
- Test valid role assignment
- Test JSONB permissions storage
- Test user_id uniqueness

### 3. Index Verification
- Verify all 3 indexes created
- Test query performance with indexes

### 4. Integration Testing
- Create user in users table
- Create admin record
- Verify JOIN query works
- Test data retrieval

---

## Rollback Strategy

If this migration needs to be reverted:

```sql
-- Drop indexes first
DROP INDEX IF EXISTS idx_admins_role;
DROP INDEX IF EXISTS idx_admins_user_id;

-- Drop table (CASCADE will handle any dependencies)
DROP TABLE IF EXISTS admins CASCADE;
```

---

## SQL Implementation

```sql
-- Create admins table (JOINED inheritance from users)
CREATE TABLE admins (
    user_id UUID NOT NULL,
    role VARCHAR(50) NOT NULL,
    permissions JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    
    -- Primary Key (also Foreign Key to users)
    CONSTRAINT pk_admins PRIMARY KEY (user_id),
    
    -- Foreign Key to users table
    CONSTRAINT fk_admins_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for performance
CREATE INDEX idx_admins_role ON admins(role);
CREATE INDEX idx_admins_user_id ON admins(user_id);

-- Add comments for documentation
COMMENT ON TABLE admins IS 'Administrators table - JOINED inheritance from users';
COMMENT ON COLUMN admins.user_id IS 'Primary key and foreign key to users table';
COMMENT ON COLUMN admins.role IS 'Admin role type (e.g., SUPER_ADMIN, ADMIN, MODERATOR)';
COMMENT ON COLUMN admins.permissions IS 'JSON object with specific permissions and access controls';
COMMENT ON COLUMN admins.created_at IS 'Record creation timestamp';
COMMENT ON COLUMN admins.updated_at IS 'Record last update timestamp';
```

---

## Related Documentation

- [V003 Migration](../V003/migration.md) - Consultants table
- [V002 Migration](../V002/migration.md) - Clients table
- [V001 Migration](../V001/migration.md) - Users table
- [ER Diagram](../../ER_DIAGRAM.md)
- [JPA Inheritance Strategy](../../jpa-inheritance-strategy.md)

---

## Notes

### Key Differences from Clients/Consultants:
- Admins use flexible JSONB for permissions vs fixed fields
- Admins have role-based access control
- No personal/company document fields (CPF/CNPJ)
- No address information stored
- Focus on system access and permissions

### Design Decisions:
- JSONB allows flexible permission structures without schema changes
- Role field enables quick role-based filtering
- Minimal fields keep admin table lightweight
- Can extend permissions structure as needed

