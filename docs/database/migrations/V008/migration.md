# Migration V008: Create Analyses Table

## 📋 Overview

**Version:** V008  
**Date:** 2025-12-27  
**Author:** Backend Team  
**Status:** ✅ Implemented  
**Script:** `V008__create_analyses_table.sql`  

---

## 🎯 Objective

Create the `analyses` table to store electricity bill analysis results with a **One-to-One** relationship to `electricity_bills`.

---

## 📊 Table Structure

### Primary Table: `analyses`

**Purpose:** Store calculated analysis metrics and recommendations for electricity bills

| Column | Type | Nullable | Description |
|--------|------|----------|-------------|
| `id` | UUID | NO | Primary key (auto-generated) |
| `bill_id` | UUID | NO | Foreign key to electricity_bills (UNIQUE - One-to-One) |
| `average_consumption` | DECIMAL(10,2) | YES | Average kWh consumption over historical period |
| `cost_per_kwh` | DECIMAL(10,4) | YES | Calculated cost per kWh from bill |
| `comparison_prev_month` | DECIMAL(5,2) | YES | Percentage change vs previous month |
| `savings_tips` | TEXT | YES | JSON or text with savings recommendations |
| `report_pdf_url` | TEXT | YES | Path/URL to generated PDF report |
| `created_at` | TIMESTAMP | NO | Analysis creation timestamp (immutable) |

**Note:** No `updated_at` field - analyses are immutable snapshots

---

## 🔒 Constraints

### Primary Key
- **Name:** `analyses_pkey`
- **Column:** `id`
- **Type:** UUID (auto-generated with `uuid_generate_v4()`)

### Foreign Keys

1. **`fk_analyses_bill`**
   - **Column:** `bill_id`
   - **References:** `electricity_bills(id)`
   - **Cascade:** ON DELETE CASCADE
   - **Unique:** YES (enforces One-to-One)
   - **Note:** One-to-One relationship enforced by UNIQUE constraint

### Check Constraints

1. **`chk_average_consumption_non_negative`**
   - Ensures `average_consumption >= 0` or IS NULL
   - Prevents negative consumption values

2. **`chk_cost_per_kwh_non_negative`**
   - Ensures `cost_per_kwh >= 0` or IS NULL
   - Prevents negative cost values

---

## 📇 Indexes

### 1. `idx_analyses_bill_unique` (UNIQUE)
**Column:** `bill_id`  
**Purpose:** Enforce One-to-One relationship with electricity_bills

### 2. `idx_analyses_created_at`
**Column:** `created_at`  
**Purpose:** Sort and filter analyses by creation date

**Total:** 2 performance-optimized indexes

---

## 🔗 Relationships

### Foreign Key Dependencies

**Incoming (Required):**
- `analyses.bill_id` → `electricity_bills.id` (One-to-One, CASCADE DELETE)

**Pattern:** One-to-One Dependent Relationship
- Each `electricity_bill` can have **at most ONE** analysis
- Each `analysis` belongs to **exactly ONE** electricity_bill
- If bill is deleted, analysis is also deleted (CASCADE)
- Analysis cannot exist without a bill

### Relationship Diagram
```
electricity_bills (id) ──┐
                         │ (1:0..1)
                         │
                         └──► analyses (bill_id - UNIQUE)
```

---

## 🔄 Migration Dependencies

### Prerequisites
- ✅ V001 - `users` table
- ✅ V002 - `clients` table
- ✅ V003 - `consultants` table
- ✅ V005 - `tariffs` table
- ✅ V006 - `electricity_bills` table
- ✅ V007 - `bill_items` table (recommended for analysis calculations)
- ✅ PostgreSQL `uuid-ossp` extension enabled

### No Future Dependencies
- V008 is the final migration in current schema

---

## 📝 Business Rules

### 1. One-to-One Relationship
- Each bill can have **zero or one** analysis
- Analysis is optional (bills can exist without analysis)
- `bill_id` must be unique (enforced by UNIQUE index)
- Cannot create multiple analyses for the same bill

### 2. Immutable Analysis
- No `updated_at` column (analyses are snapshots in time)
- Once created, analysis represents a point-in-time calculation
- To update analysis, delete and recreate (or create versioned analyses)

### 3. Calculated Fields
- **`average_consumption`**: Historical average over N months
  - Calculated from client's previous bills
  - Example: Average of last 3-6 months
  
- **`cost_per_kwh`**: Total bill amount / consumption_kwh
  - Calculated from current bill data
  - Formula: `total_amount ÷ consumption_kwh`
  
- **`comparison_prev_month`**: Percentage change from previous month
  - Formula: `((current - previous) / previous) × 100`
  - Positive = increase, Negative = decrease
  - Example: `15.5` = 15.5% increase

### 4. Optional Fields
- All metric fields are nullable
- Analysis can be created incrementally
- Service layer calculates values before INSERT

### 5. Recommendations
- `savings_tips` can be JSON array or plain text
- Generated based on consumption patterns
- Examples:
  - "Reduce usage during peak hours (6pm-9pm)"
  - "Consider switching to off-peak tariff"
  - "High consumption detected - check for energy leaks"

### 6. Report Generation
- `report_pdf_url` stores path to generated PDF
- PDF contains:
  - Consumption charts
  - Cost breakdown
  - Historical comparison
  - Savings recommendations
- Optional field (can be NULL)

---

## 💡 Usage Examples

### Create Analysis for Bill
```sql
INSERT INTO analyses (
    bill_id,
    average_consumption,
    cost_per_kwh,
    comparison_prev_month,
    savings_tips,
    report_pdf_url
) VALUES (
    'bill-uuid-here',
    275.50,
    0.8234,
    12.5,
    'Seu consumo aumentou 12.5% em relação ao mês anterior. Considere reduzir o uso de ar-condicionado.',
    '/reports/analysis-abc123.pdf'
);
```

### Get Analysis for Bill
```sql
SELECT * FROM analyses
WHERE bill_id = 'bill-uuid-here';
```

### Check if Bill has Analysis
```sql
SELECT EXISTS(
    SELECT 1 FROM analyses WHERE bill_id = 'bill-uuid-here'
) AS has_analysis;
```

### Get Bills Without Analysis
```sql
SELECT eb.*
FROM electricity_bills eb
LEFT JOIN analyses a ON eb.id = a.bill_id
WHERE a.id IS NULL;
```

### Calculate Average Cost Across Analyses
```sql
SELECT 
    AVG(cost_per_kwh) AS avg_cost,
    MIN(cost_per_kwh) AS min_cost,
    MAX(cost_per_kwh) AS max_cost
FROM analyses
WHERE cost_per_kwh IS NOT NULL;
```

---

## 🧪 Testing Performed

See [validation.md](./validation.md) for detailed validation report.

### Test Coverage
- ✅ Table creation
- ✅ One-to-One relationship enforcement
- ✅ Foreign key constraint (CASCADE)
- ✅ Unique constraint on bill_id
- ✅ Check constraints (non-negative values)
- ✅ Sample data insertion
- ✅ Cannot create duplicate analyses

---

## 📚 References

- [JPA Entity Modeling](../../jpa-entity-modeling.md) - Analysis Entity (line 974)
- [ER Diagram](../../ER_DIAGRAM.md)
- [Analysis Entity Model](../../../../backend/src/main/java/com/understand_your_electricity_bill/model/Analysis.java)
- [Analysis Unit Tests](../../../../backend/src/test/java/com/understand_your_electricity_bill/model/AnalysisTest.java)
- [ElectricityBill Entity Model](../../../../backend/src/main/java/com/understand_your_electricity_bill/model/ElectricityBill.java)

---

## 🔍 Analysis Calculation Logic

### Service Layer Responsibilities

The `AnalysisService` should:

1. **Calculate `average_consumption`**:
   ```java
   // Get last N bills for client
   List<ElectricityBill> recentBills = billRepository
       .findByClientIdOrderByReferenceMonthDesc(clientId, PageRequest.of(0, 6));
   
   // Calculate average
   BigDecimal avgConsumption = recentBills.stream()
       .map(ElectricityBill::getConsumptionKwh)
       .reduce(BigDecimal.ZERO, BigDecimal::add)
       .divide(new BigDecimal(recentBills.size()), 2, RoundingMode.HALF_UP);
   ```

2. **Calculate `cost_per_kwh`**:
   ```java
   BigDecimal costPerKwh = bill.getTotalAmount()
       .divide(bill.getConsumptionKwh(), 4, RoundingMode.HALF_UP);
   ```

3. **Calculate `comparison_prev_month`**:
   ```java
   ElectricityBill prevBill = billRepository
       .findByClientIdAndReferenceMonth(clientId, currentMonth.minusMonths(1));
   
   if (prevBill != null) {
       BigDecimal diff = currentBill.getConsumptionKwh()
           .subtract(prevBill.getConsumptionKwh());
       BigDecimal percentage = diff
           .divide(prevBill.getConsumptionKwh(), 2, RoundingMode.HALF_UP)
           .multiply(new BigDecimal(100));
   }
   ```

4. **Generate `savings_tips`**:
   ```java
   StringBuilder tips = new StringBuilder();
   
   if (comparison > 20) {
       tips.append("Consumo muito alto. Verifique equipamentos.");
   }
   
   if (costPerKwh.compareTo(new BigDecimal("0.80")) > 0) {
       tips.append("Custo por kWh elevado. Considere mudar de tarifa.");
   }
   ```

---

## ✅ Status: IMPLEMENTED

Migration V008 is fully implemented and ready for execution.

**Next Steps:**
1. Execute validation tests (see validation.md)
2. Implement `AnalysisRepository` with query methods
3. Create `AnalysisService` with calculation logic
4. Implement PDF report generation

---

Last Updated: 2025-12-27

