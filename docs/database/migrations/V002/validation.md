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
| cpf | character varying | 11 | NO | - |
| registration_date | date | - | NO | CURRENT_DATE |
**Status:** ? PASSED
---
### 2. Constraints
**Primary Key:** clients_pkey on `user_id` - ? PASSED  
**Foreign Key:** fk_client_user -> users(id), ON DELETE CASCADE - ? PASSED  
**Unique:** clients_cpf_key on `cpf` - ? PASSED  
**Check:** chk_cpf_format CHECK (cpf ~ '^\d{11}$') - ? PASSED
---
### 3. Indexes
| Index Name | Type | Column(s) |
|------------|------|-----------|
| clients_pkey | UNIQUE | user_id |
| clients_cpf_key | UNIQUE | cpf |
| idx_clients_cpf | BTREE | cpf |
| idx_clients_registration_date | BTREE | registration_date |
**Status:** ? PASSED
---
### 4. Functional Tests
**Test 1: INSERT Valid Client**
```sql
-- Insert user + client with CPF 12345678901
-- JOIN query returns correct data
```
**Status:** ? PASSED
**Test 2: CASCADE DELETE**
```sql
-- Delete user -> client also deleted automatically
```
**Status:** ? PASSED
**Test 3: CPF Uniqueness**
```sql
-- Attempt duplicate CPF -> ERROR: duplicate key constraint
```
**Status:** ? PASSED
**Test 4: CPF Format Validation**
| CPF | Expected | Result |
|-----|----------|--------|
| `12345678901` | ? SUCCESS | ? |
| `123456789` | ? ERROR | ? |
| `123.456.789-01` | ? ERROR | ? |
**Status:** ? PASSED
---
## Summary
| Category | Tests | Passed |
|----------|-------|--------|
| Structure | 4 | 4 |
| Constraints | 4 | 4 |
| Indexes | 4 | 4 |
| Functional | 4 | 4 |
| **TOTAL** | **16** | **16** |
**Overall Status:** ? **APPROVED**
---
## Sign-off
- [x] All tests passed
- [x] Constraints validated
- [x] JOINED inheritance working
- [x] Ready for next migration (V003)
**Validated by:** Mateus De La Fuente Cezar  
**Date:** 2025-12-15
