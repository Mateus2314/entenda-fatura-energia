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


## 📚 References

- [JPA Inheritance Strategy Documentation](../../jpa-inheritance-strategy.md)
- [Data Models](../../scopo/07_data_models.md)
- [System Architecture Diagram](../../diagram.md)

---

## ✅ Status: COMPLETE

Migration V001 is fully implemented, tested, and validated.
Ready for production deployment.

