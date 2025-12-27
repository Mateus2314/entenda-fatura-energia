# Migration V007: Create Bill Items Table

## 📋 Overview

**Version:** V007  
**Date:** 2025-12-27  
**Author:** Backend Team  
**Status:** ✅ Implemented  
**Script:** `V007__create_bill_items_table.sql`  

---

## 🎯 Objective

Create the `bill_items` table to store individual line items of electricity bills with a **Many-to-One** relationship to `electricity_bills`.

---

## 📊 Table Structure

### Primary Table: `bill_items`

**Purpose:** Store detailed line items for each electricity bill (consumption charges, taxes, fees, etc.)

| Column | Type | Nullable | Description |
|--------|------|----------|-------------|
| `id` | UUID | NO | Primary key (auto-generated) |
| `bill_id` | UUID | NO | Foreign key to electricity_bills (Many-to-One) |
| `item_type` | VARCHAR(50) | NO | Type of charge (enum) |
| `description` | VARCHAR(500) | NO | Item description |
| `quantity` | DECIMAL(10,2) | YES | Quantity (e.g., kWh, kW) |
| `unit_price` | DECIMAL(10,4) | YES | Price per unit |
| `amount` | DECIMAL(10,2) | NO | Total amount for this item |
| `created_at` | TIMESTAMP | NO | Record creation timestamp (auto) |
| `updated_at` | TIMESTAMP | NO | Last update timestamp (auto) |

---

## 🏷️ Item Types (Enum)

The `item_type` field accepts the following values:

| Type | Description | Example |
|------|-------------|---------|
| `CONSUMPTION_PEAK` | Peak hours consumption | Energy used 6pm-9pm |
| `CONSUMPTION_OFF_PEAK` | Off-peak hours consumption | Energy used 11pm-6am |
| `CONSUMPTION_STANDARD` | Standard hours consumption | Energy used outside peak/off-peak |
| `DEMAND` | Demand charge | Maximum kW demanded |
| `TARIFF_FLAG` | Tariff flag charge | Red/Yellow flag surcharge |
| `PUBLIC_LIGHTING` | Public lighting tax | CIP (Contribuição de Iluminação Pública) |
| `TAXES` | Other taxes | ICMS, PIS, COFINS |
| `OTHER` | Other charges | Administrative fees, adjustments |

---

## 🔒 Constraints

### Primary Key
- **Name:** `bill_items_pkey`
- **Column:** `id`
- **Type:** UUID (auto-generated with `uuid_generate_v4()`)

### Foreign Keys

1. **`fk_bill_items_bill`**
   - **Column:** `bill_id`
   - **References:** `electricity_bills(id)`
   - **Cascade:** ON DELETE CASCADE
   - **Description:** Parent bill (required)

### Check Constraints

1. **`chk_quantity_non_negative`**
   - Ensures `quantity >= 0` or IS NULL
   - Prevents negative quantities

2. **`chk_unit_price_non_negative`**
   - Ensures `unit_price >= 0` or IS NULL
   - Prevents negative prices

3. **`chk_amount_non_negative`**
   - Ensures `amount >= 0`
   - Prevents negative amounts

4. **`chk_item_type_valid`**
   - Ensures `item_type` is one of the 8 valid enum values
   - Prevents invalid item types

---

## 📇 Indexes

### 1. `idx_bill_items_bill_id`
**Column:** `bill_id`  
**Purpose:** Fast lookup of all items for a bill (Most common query)

### 2. `idx_bill_items_item_type`
**Column:** `item_type`  
**Purpose:** Filter items by type (e.g., all TAXES)

### 3. `idx_bill_items_created_at`
**Column:** `created_at`  
**Purpose:** Sort items by creation date

### 4. `idx_bill_items_bill_type` (Composite)
**Columns:** `bill_id`, `item_type`  
**Purpose:** Optimized queries for specific item types within a bill

**Total:** 4 performance-optimized indexes

---

## ⚙️ Triggers & Functions

### `update_bill_items_updated_at` Trigger
Automatically updates `updated_at` field on every UPDATE operation.

**Implementation:**
```sql
CREATE TRIGGER update_bill_items_updated_at
    BEFORE UPDATE ON bill_items
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
```

**Note:** The `update_updated_at_column()` function was created in V001.

---

## 🔗 Relationships

### Foreign Key Dependencies

**Incoming (Required):**
- `bill_items.bill_id` → `electricity_bills.id` (Many-to-One, CASCADE DELETE)

**Pattern:** Many-to-One Relationship
- Each `electricity_bill` can have **multiple** bill_items
- Each `bill_item` belongs to **exactly ONE** electricity_bill
- If bill is deleted, all its items are deleted (CASCADE)

### Relationship Diagram
```
electricity_bills (id) ──┐
                         │ (1:N)
                         │
                         └──► bill_items (bill_id)
                              - CONSUMPTION_PEAK
                              - CONSUMPTION_OFF_PEAK
                              - DEMAND
                              - TARIFF_FLAG
                              - TAXES
                              - etc.
```

---

## 🔄 Migration Dependencies

### Prerequisites
- ✅ V001 - `users` table with `update_updated_at_column()` function
- ✅ V002 - `clients` table
- ✅ V003 - `consultants` table
- ✅ V005 - `tariffs` table
- ✅ V006 - `electricity_bills` table
- ✅ PostgreSQL `uuid-ossp` extension enabled

### Dependent Tables (Future Migrations)
- 📋 V008 - `analyses` table (may use bill_items data for calculations)

---

## 📝 Business Rules

### 1. Many-to-One Relationship
- Each bill can have **multiple items** (typically 5-15 items per bill)
- Each item belongs to **exactly ONE bill**
- Items cannot exist without a bill (CASCADE DELETE)

### 2. Item Types
- `item_type` must be one of 8 predefined values
- Each bill typically has:
  - 1-3 consumption items (peak/off-peak/standard)
  - 0-1 demand items
  - 0-1 tariff flag items
  - 1-3 tax items
  - 0-N other items

### 3. Calculation Fields
- **quantity** × **unit_price** should ≈ **amount**
- Service layer is responsible for calculating `amount`
- Optional fields: `quantity` and `unit_price` (some items may not have these)

### 4. Non-Negative Values
- All monetary values must be >= 0
- Zero amounts are allowed (e.g., zero consumption)
- Enforced by check constraints

### 5. Description Requirements
- Maximum 500 characters
- Should be descriptive (e.g., "Consumo Ponta - 150 kWh")
- Required field (NOT NULL)

### 6. Cascade Delete
- When a bill is deleted, all its items are automatically deleted
- Ensures referential integrity
- No orphaned items in database

---

## 💡 Usage Examples

### Create Bill Items
```sql
-- Item 1: Peak consumption
INSERT INTO bill_items (
    bill_id, item_type, description,
    quantity, unit_price, amount
) VALUES (
    'bill-uuid-here',
    'CONSUMPTION_PEAK',
    'Consumo Ponta - 150 kWh',
    150.00,
    0.9523,
    142.85
);

-- Item 2: Off-peak consumption
INSERT INTO bill_items (
    bill_id, item_type, description,
    quantity, unit_price, amount
) VALUES (
    'bill-uuid-here',
    'CONSUMPTION_OFF_PEAK',
    'Consumo Fora Ponta - 200 kWh',
    200.00,
    0.4521,
    90.42
);

-- Item 3: Tariff flag
INSERT INTO bill_items (
    bill_id, item_type, description,
    quantity, unit_price, amount
) VALUES (
    'bill-uuid-here',
    'TARIFF_FLAG',
    'Bandeira Vermelha - Patamar 1',
    350.00,
    0.04169,
    14.59
);

-- Item 4: Taxes
INSERT INTO bill_items (
    bill_id, item_type, description,
    quantity, unit_price, amount
) VALUES (
    'bill-uuid-here',
    'TAXES',
    'ICMS (27%)',
    NULL,
    NULL,
    66.84
);
```

### Get All Items for a Bill
```sql
SELECT * FROM bill_items
WHERE bill_id = 'bill-uuid-here'
ORDER BY item_type, created_at;
```

### Calculate Bill Total from Items
```sql
SELECT 
    bill_id,
    SUM(amount) AS calculated_total
FROM bill_items
WHERE bill_id = 'bill-uuid-here'
GROUP BY bill_id;
```

### Get Consumption Items Only
```sql
SELECT * FROM bill_items
WHERE 
    bill_id = 'bill-uuid-here'
    AND item_type IN ('CONSUMPTION_PEAK', 'CONSUMPTION_OFF_PEAK', 'CONSUMPTION_STANDARD')
ORDER BY item_type;
```

---

## 🧪 Testing Performed

See [validation.md](./validation.md) for detailed validation report.

### Test Coverage
- ✅ Table creation
- ✅ Foreign key constraint (CASCADE DELETE)
- ✅ Check constraints (non-negative values)
- ✅ Enum validation (item_type)
- ✅ Index performance
- ✅ Trigger functionality
- ✅ Sample data insertion
- ✅ Cascade delete behavior

---

## 📚 References

- [JPA Entity Modeling](../../jpa-entity-modeling.md) - BillItem Entity
- [ER Diagram](../../ER_DIAGRAM.md)
- [BillItem Entity Model](../../../../backend/src/main/java/com/understand_your_electricity_bill/model/BillItem.java)
- [BillItemType Enum](../../../../backend/src/main/java/com/understand_your_electricity_bill/model/enums/BillItemType.java)
- [ElectricityBill Entity Model](../../../../backend/src/main/java/com/understand_your_electricity_bill/model/ElectricityBill.java)

---

## ✅ Status: IMPLEMENTED

Migration V007 is fully implemented and ready for execution.

**Next Steps:**
1. Execute validation tests (see validation.md)
2. Create V008 - analyses table
3. Implement BillItemRepository with query methods
4. Create service layer for bill item calculations

---

Last Updated: 2025-12-27

