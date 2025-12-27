# Migration V006: Create Electricity Bills Table

## 📋 Overview

**Version:** V006  
**Date:** 2025-12-27  
**Author:** Backend Team  
**Status:** ✅ Implemented  
**Script:** `V006__create_electricity_bills_table.sql`  

---

## 🎯 Objective

Create the `electricity_bills` table to store energy consumption invoices with relationships to clients, consultants, and tariffs.

---

## 📊 Table Structure

### Primary Table: `electricity_bills`

**Purpose:** Store electricity bills with consumption data and relationships

| Column | Type | Nullable | Description |
|--------|------|----------|-------------|
| `id` | UUID | NO | Primary key (auto-generated) |
| **Relationships** |
| `client_id` | UUID | NO | Foreign key to clients (bill owner) |
| `consultant_id` | UUID | YES | Foreign key to consultants (optional) |
| `tariff_id` | UUID | NO | Foreign key to tariffs (applied tariff) |
| **Bill Data** |
| `reference_month` | DATE | NO | Billing reference month (YYYY-MM-01) |
| `due_date` | DATE | NO | Payment due date |
| `total_amount` | DECIMAL(10,2) | NO | Total bill amount in currency |
| `consumption_kwh` | DECIMAL(10,2) | NO | Energy consumption in kWh |
| **Additional Info** |
| `pdf_url` | TEXT | YES | Path/URL to bill PDF file |
| `installation_number` | VARCHAR(50) | YES | Utility installation/UC number |
| `invoice_number` | VARCHAR(100) | YES | Invoice/bill number from utility |
| **Audit Fields** |
| `created_at` | TIMESTAMP | NO | Record creation timestamp (auto) |
| `updated_at` | TIMESTAMP | NO | Last update timestamp (auto) |

---

## 🔒 Constraints

### Primary Key
- **Name:** `electricity_bills_pkey`
- **Column:** `id`
- **Type:** UUID (auto-generated with `uuid_generate_v4()`)

### Foreign Keys

1. **`fk_bills_client`**
   - **Column:** `client_id`
   - **References:** `clients(user_id)`
   - **Cascade:** ON DELETE CASCADE
   - **Description:** Bill owner (required)

2. **`fk_bills_consultant`**
   - **Column:** `consultant_id`
   - **References:** `consultants(user_id)`
   - **Cascade:** ON DELETE SET NULL
   - **Description:** Assigned consultant (optional)

3. **`fk_bills_tariff`**
   - **Column:** `tariff_id`
   - **References:** `tariffs(id)`
   - **Cascade:** ON DELETE RESTRICT
   - **Description:** Applied tariff (required, cannot delete if in use)

### Check Constraints

1. **`chk_due_date_after_reference`**
   - Ensures `due_date > reference_month`
   - Prevents invalid date ranges

2. **Inline Check on `total_amount`**
   - Ensures `total_amount >= 0`
   - Prevents negative values

3. **Inline Check on `consumption_kwh`**
   - Ensures `consumption_kwh >= 0`
   - Prevents negative consumption

---

## 📇 Indexes

### Foreign Key Indexes (3)
1. **`idx_bills_client_id`** - Fast lookup by client
2. **`idx_bills_consultant_id`** - Fast lookup by consultant
3. **`idx_bills_tariff_id`** - Fast lookup by tariff

### Business Logic Indexes (3)
4. **`idx_bills_reference_month`** - Filter by billing month
5. **`idx_bills_due_date`** - Find overdue bills
6. **`idx_bills_created_at`** - Sort by creation date

### Composite Indexes (2)
7. **`idx_bills_client_reference`** - Client's bills by month
8. **`idx_bills_consultant_reference`** - Consultant's bills by month

**Total:** 8 performance-optimized indexes

---

## ⚙️ Triggers & Functions

### `update_electricity_bills_updated_at` Trigger
Automatically updates `updated_at` field on every UPDATE operation.

**Implementation:**
```sql
CREATE TRIGGER update_electricity_bills_updated_at
    BEFORE UPDATE ON electricity_bills
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
```

**Note:** The `update_updated_at_column()` function was created in V001.

---

## 🔗 Relationships

### Foreign Key Dependencies

**Incoming (Required):**
- `electricity_bills.client_id` → `clients.user_id` (Many-to-One, CASCADE)
- `electricity_bills.tariff_id` → `tariffs.id` (Many-to-One, RESTRICT)

**Incoming (Optional):**
- `electricity_bills.consultant_id` → `consultants.user_id` (Many-to-One, SET NULL)

**Outgoing (Future):**
- `bill_items.bill_id` → `electricity_bills.id` (One-to-Many) - V007
- `analyses.bill_id` → `electricity_bills.id` (One-to-One) - V008

### Relationship Diagram
```
clients (user_id) ──────────┐
                            │ (required)
                            ├──► electricity_bills
                            │
consultants (user_id) ──────┤ (optional)
                            │
tariffs (id) ───────────────┘ (required)
```

---

## 🔄 Migration Dependencies

### Prerequisites
- ✅ V001 - `users` table with `update_updated_at_column()` function
- ✅ V002 - `clients` table
- ✅ V003 - `consultants` table
- ✅ V005 - `tariffs` table
- ✅ PostgreSQL `uuid-ossp` extension enabled

### Dependent Tables (Future Migrations)
- 📋 V007 - `bill_items` table (will reference `electricity_bills.id`)
- 📋 V008 - `analyses` table (will reference `electricity_bills.id`)

---

## 📝 Business Rules

### 1. Client Relationship (Required)
- Every bill MUST belong to a client
- If client is deleted, all their bills are deleted (CASCADE)
- Cannot create bill without valid client

### 2. Consultant Assignment (Optional)
- Bills can exist without a consultant
- If consultant is deleted, their bills remain but `consultant_id` becomes NULL (SET NULL)
- Consultant can be assigned/changed after bill creation

### 3. Tariff Application (Required)
- Every bill MUST have an associated tariff
- Tariff defines pricing structure for the bill
- Cannot delete tariff if bills are using it (RESTRICT)

### 4. Date Validation
- `due_date` must be AFTER `reference_month`
- `reference_month` typically stored as first day of month (YYYY-MM-01)
- Enforced by check constraint

### 5. Non-Negative Values
- `total_amount` and `consumption_kwh` must be >= 0
- Zero consumption is allowed (e.g., for minimum charges)
- Enforced by check constraints

### 6. Bill Identification
- `installation_number` - Utility's UC (Unidade Consumidora) number
- `invoice_number` - Bill/invoice number from utility company
- Both optional but recommended for tracking

### 7. PDF Storage
- `pdf_url` can store local path or cloud URL
- Optional field (bills can exist without PDF)
- Format: `/uploads/bills/2025/12/bill-uuid.pdf` or `https://...`

---

## 🧪 Testing Performed

See [validation.md](./validation.md) for detailed validation report.

### Test Coverage
- ✅ Table creation
- ✅ All foreign key constraints
- ✅ Check constraints validation
- ✅ Indexes performance
- ✅ Trigger functionality
- ✅ Cascade delete behaviors
- ✅ Sample data insertion

---

## 💡 Usage Examples

### Create Bill for Client
```sql
INSERT INTO electricity_bills (
    client_id,
    consultant_id,
    tariff_id,
    reference_month,
    due_date,
    total_amount,
    consumption_kwh,
    installation_number,
    invoice_number
) VALUES (
    '550e8400-e29b-41d4-a716-446655440000', -- client_id
    NULL, -- no consultant assigned yet
    'c1234567-89ab-cdef-0123-456789abcdef', -- tariff_id
    '2025-12-01',
    '2025-12-20',
    285.50,
    350.00,
    '123456789',
    'FAT-2025-12-001'
);
```

### Assign Consultant to Existing Bill
```sql
UPDATE electricity_bills
SET consultant_id = 'a9876543-21ba-fedc-0123-456789abcdef'
WHERE id = 'bill-uuid-here';
```

### Calculate Cost per kWh
```sql
SELECT 
    id,
    reference_month,
    total_amount,
    consumption_kwh,
    (total_amount / NULLIF(consumption_kwh, 0)) AS cost_per_kwh
FROM electricity_bills
WHERE consumption_kwh > 0;
```

---

## 📚 References

- [JPA Entity Modeling](../../jpa-entity-modeling.md) - ElectricityBill Entity
- [ER Diagram](../../ER_DIAGRAM.md)
- [ElectricityBill Entity Model](../../../../backend/src/main/java/com/understand_your_electricity_bill/model/ElectricityBill.java)
- [ElectricityBill Unit Tests](../../../../backend/src/test/java/com/understand_your_electricity_bill/model/ElectricityBillTest.java)

---

## ✅ Status: IMPLEMENTED

Migration V006 is fully implemented and ready for execution.

**Next Steps:**
1. Execute validation tests (see validation.md)
2. Create V007 - bill_items table
3. Create V008 - analyses table
4. Implement ElectricityBillRepository with query methods

---

Last Updated: 2025-12-27

