# V002 - Clients Table Migration - Validation Report
## Migration Information
| Field | Value |
|-------|-------|
| **Version** | V002 |
| **File** | `V2__create_clients_table.sql` |
| **Description** | Create clients table (JOINED inheritance) |
| **Author** | Mateus De La Fuente Cezar |
| **Date** | 2025-12-15 |
| **Database** | PostgreSQL 17.7 |
## Validation Results
### 1. Table Structure

| column_name | data_type | max_length | nullable | default |
|-------------|-----------|------------|----------|---------|
| user_id | uuid | - | NO | - |
| address | character varying | 500 | NO | - |
| city | character varying | 100 | YES | - |
| state | character varying | 2 | YES | - |
| zip_code | character varying | 10 | YES | - |
| cpf | character varying | 11 | NO | - |
| registration_date | date | - | NO | CURRENT_DATE |

**Status:** ✅ PASSED

**Note:** `created_at` and `updated_at` are inherited from `users` table.

---

### 2. Constraints

**Primary Key:** clients_pkey on `user_id` - ✅ PASSED  
**Foreign Key:** fk_client_user -> users(id), ON DELETE CASCADE - ✅ PASSED  
**Unique:** clients_cpf_key on `cpf` - ✅ PASSED  
**Check Constraints:**
- chk_cpf_format: `cpf ~ '^\d{11}$'` - ✅ PASSED
- chk_state_format: `state IS NULL OR state ~ '^[A-Z]{2}$'` - ✅ PASSED
- chk_zipcode_format: `zip_code IS NULL OR zip_code ~ '^\d{5}-?\d{3}$'` - ✅ PASSED

---

### 3. Indexes

| Index Name | Type | Column(s) |
|------------|------|-----------|
| clients_pkey | UNIQUE | user_id |
| clients_cpf_key | UNIQUE | cpf |
| idx_clients_cpf | BTREE | cpf |
| idx_clients_registration_date | BTREE | registration_date |
| idx_clients_city | BTREE | city |
| idx_clients_state | BTREE | state |
| idx_clients_zipcode | BTREE | zip_code |

**Status:** ✅ PASSED
---

### 4. Functional Tests

**Test 1: INSERT Valid Client**
```sql
-- Insert user + client with CPF 12345678901
-- JOIN query returns correct data
```
**Status:** ✅ PASSED

**Test 2: CASCADE DELETE**
```sql
-- Delete user -> client also deleted automatically
```
**Status:** ✅ PASSED

**Test 3: CPF Uniqueness**
```sql
-- Attempt duplicate CPF -> ERROR: duplicate key constraint
```
**Status:** ✅ PASSED

**Test 4: CPF Format Validation**
| CPF | Expected | Result |
|-----|----------|--------|
| `12345678901` | ✅ SUCCESS | ✅ |
| `123456789` | ❌ ERROR | ✅ |
| `123.456.789-01` | ❌ ERROR | ✅ |

**Status:** ✅ PASSED

**Test 5: State Format Validation**
| State | Expected | Result |
|-------|----------|--------|
| `SP` | ✅ SUCCESS | ✅ |
| `NULL` | ✅ SUCCESS | ✅ |
| `sp` (lowercase) | ❌ ERROR | ✅ |
| `SAO` (3 chars) | ❌ ERROR | ✅ |

**Status:** ✅ PASSED

**Test 6: ZIP Code Format Validation**
| ZIP Code | Expected | Result |
|----------|----------|--------|
| `12345-678` | ✅ SUCCESS | ✅ |
| `12345678` | ✅ SUCCESS | ✅ |
| `NULL` | ✅ SUCCESS | ✅ |
| `12345` (short) | ❌ ERROR | ✅ |

**Status:** ✅ PASSED

---

## Summary

| Category | Tests | Passed |
|----------|-------|--------|
| Structure | 7 | 7 |
| Constraints | 6 | 6 |
| Indexes | 7 | 7 |
| Functional | 6 | 6 |
| **TOTAL** | **26** | **26** |

**Overall Status:** ✅ **APPROVED**

---

## Sign-off

- [x] All tests passed
- [x] Constraints validated
- [x] JOINED inheritance working
- [x] New columns (city, state, zip_code) validated
- [x] Format validations working correctly
- [x] Ready for next migration (V003)

**Validated by:** Mateus De La Fuente Cezar  
**Date:** 2025-12-27  
**Updated:** 2025-12-27 - Added city, state, zip_code validations
