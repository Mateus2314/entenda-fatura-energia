# Test Data Documentation

## Overview

This directory contains test data scripts and validation queries for all database migrations (V001-V009).

## Files Structure

### Main Files

1. **`test_data.sql`** - Complete test data for all tables
   - Organized by migration (V001-V009)
   - All UUIDs use hexadecimal characters only (0-9, a-f)
   - Respects foreign key dependencies
   - Includes verification queries

2. **`validate_test_data.sql`** - Comprehensive validation queries
   - 12 validation sections
   - Data integrity checks
   - Business logic validations
   - Relationship verifications

### Migration-Specific Files

Each migration (V001-V009) has its own folder with:
- `migration.md` - Migration documentation
- `queries.sql` - Validation and example queries
- `validation.md` - Specific validation tests

## Data Summary

**Total Records: 93 across 9 tables**

- **15 Users** (5 Clients + 5 Consultants + 5 Admins)
- **5 Clients** with complete address information
- **5 Consultants** with company details
- **5 Admins** with different roles and permissions
- **5 Tariffs** from different distributors (ANEEL-based)
- **7 Electricity Bills** across multiple months
- **37 Bill Items** with detailed breakdowns
- **5 Analyses** with calculations and recommendations
- **10 Consultant-Client Relationships** (many-to-many)

## How to Use

### Option 1: Complete Data Insertion (Recommended)

Execute the main test data file:

```sql
-- In DBeaver or pgAdmin:
-- 1. Open: test_data.sql
-- 2. Select all (Ctrl+A)
-- 3. Execute (Ctrl+Enter or F5)
```

### Option 2: Clean and Restart

```sql
-- WARNING: Deletes all data!
TRUNCATE TABLE analyses CASCADE;
TRUNCATE TABLE bill_items CASCADE;
TRUNCATE TABLE electricity_bills CASCADE;
TRUNCATE TABLE consultant_clients CASCADE;
TRUNCATE TABLE tariffs CASCADE;
TRUNCATE TABLE admins CASCADE;
TRUNCATE TABLE consultants CASCADE;
TRUNCATE TABLE clients CASCADE;
TRUNCATE TABLE users CASCADE;

-- Then execute test_data.sql
```

### Option 3: Validation Only

```sql
-- Execute validation_tests.sql to check existing data
-- This runs 12 comprehensive test suites
```

## Expected Results

```
table_name            | count
----------------------|-------
admins                | 5
analyses              | 5
bill_items            | 37
clients               | 5
consultant_clients    | 10
consultants           | 5
electricity_bills     | 7
tariffs               | 5
users                 | 15
```

## Data Characteristics

### User Types
- **Clients** (5): Mixed statuses (ACTIVE, INACTIVE, PENDING_VERIFICATION)
- **Consultants** (5): Including one SUSPENDED
- **Admins** (5): Different roles (SUPER_ADMIN, SUPPORT_ADMIN, etc.)

### Tariffs
Based on real ANEEL data structure from different distributors:
1. CPFL PAULISTA - Residential B1
2. LIGHT - Residential B1
3. CEMIG - Commercial B3
4. COPEL - Industrial A4
5. RGE SUL - Low Income B1

### Bills
- 7 bills across Oct-Dec 2024
- Multiple bills per client (tracking consumption over time)
- One bill without consultant (Client 3)
- Varying consumption patterns

### Bill Items
- 37 items total with types:
  - CONSUMPTION_STANDARD, CONSUMPTION_PEAK, CONSUMPTION_OFF_PEAK
  - TARIFF_FLAG (bandeiras tarifárias)
  - DEMAND
  - PUBLIC_LIGHTING
  - TAXES (ICMS, PIS/COFINS)
  - OTHER

### Analyses
- 5 analyses with metrics and recommendations
- Some with month-over-month comparisons
- Tailored savings tips

### Relationships
- Many-to-many consultant-client relationships
- Mix of ACTIVE, INACTIVE, and PENDING statuses

## Troubleshooting

### Error: "duplicate key value violates unique constraint"
**Solution:** Data already exists. Check counts with validation query or clean database first.

### Error: "violates foreign key constraint"
**Solution:** Execute the entire test_data.sql file at once, not section by section.

### Error: "invalid input syntax for type uuid"
**Solution:** All UUIDs in test_data.sql are corrected. Make sure you're using the latest version.

## UUID Patterns

All UUIDs follow hexadecimal patterns (0-9, a-f):

- **Clients**: `11111111...`, `22222222...`, etc.
- **Consultants**: `aaaaaaaa...`, `bbbbbbbb...`, etc.
- **Admins**: `f0000000-0000-0000-0000-00000000000X`
- **Tariffs**: `X0000000-0000-0000-0000-000000000001` (X=1-5)
- **Bills**: `b000000X-0000-0000-0000-00000000000Y`
- **Items**: `1000000X-000Y-0000-0000-00000000000Z`
- **Analyses**: `a000000X-0000-0000-0000-00000000000Y`

## Integration with Application

This test data is designed to work with:
- ✅ JPA entities (User, Client, Consultant, Admin, Tariff, etc.)
- ✅ Repository tests
- ✅ Service layer tests
- ✅ Controller integration tests
- ✅ Frontend development

## Notes

- All timestamps use `NOW()` for current insertion time
- UUIDs are hardcoded for consistent relationships
- CPF/CNPJ follow Brazilian format standards
- Phone numbers follow Brazilian format
- Monetary values use DECIMAL(10,2) precision
- Consumption values use DECIMAL(10,2) for kWh

---

**Last Updated:** 2025-12-29  
**Database Version:** PostgreSQL 17.7  
**Migrations:** V001 through V009  
**Total Records:** 93

