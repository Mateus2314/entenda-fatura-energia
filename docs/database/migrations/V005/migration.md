# Migration V005: Create Tariffs Table

## 📋 Overview

**Version:** V005  
**Date:** 2025-12-27  
**Author:** Backend Team  
**Status:** ✅ Implemented  
**Script:** `V005__create_tariffs_table.sql`  

---

## 🎯 Objective

Create the `tariffs` table to store energy tariffs and tariff flags (bandeiras tarifárias) from ANEEL Open Data API. This table supports integration with two ANEEL APIs:
1. **Energy Tariffs API** - Resource: `fcf2906c-7c32-4b9b-a637-054e7a5234f4`
2. **Tariff Flags API** - Resource: `0591b8f6-fe54-437b-b72b-1aa2efd46e42`

---

## 📊 Table Structure

### Primary Table: `tariffs`

**Purpose:** Store energy tariff data and tariff flags from ANEEL API snapshots

| Column | Type | Nullable | Description |
|--------|------|----------|-------------|
| `id` | UUID | NO | Primary key (auto-generated) |
| **Energy Tariffs API Fields** |
| `generation_date` | DATE | NO | Dataset generation date (DatGeracaoConjuntoDados) |
| `description_reh` | VARCHAR(500) | YES | REH description (DscREH) |
| `distributor` | VARCHAR(100) | NO | Distributor name - SigAgente (e.g., "CPFL JAGUARI") |
| `cnpj_distributor` | VARCHAR(14) | NO | Distributor CNPJ - 14 digits (NumCNPJDistribuidora) |
| `valid_from` | DATE | NO | Tariff validity start date (DatInicioVigencia) |
| `valid_until` | DATE | YES | Tariff validity end date (DatFimVigencia) |
| `tariff_base_desc` | VARCHAR(100) | YES | Tariff base description (DscBaseTarifaria) |
| `subgroup` | VARCHAR(10) | YES | Subgroup: A2, B1, etc. (DscSubGrupo) |
| `tariff_modality` | VARCHAR(50) | YES | Modality: Azul, Verde, Convencional (DscModalidadeTarifaria) |
| `consumer_class` | VARCHAR(100) | YES | Consumer class (DscClasse) |
| `consumer_subclass` | VARCHAR(100) | YES | Consumer subclass (DscSubClasse) |
| `detail` | VARCHAR(100) | YES | Additional details (DscDetalhe) |
| `tariff_post_name` | VARCHAR(50) | YES | Tariff post: Ponta, Fora ponta (NomPostoTarifario) |
| `tertiary_unit` | VARCHAR(10) | YES | Unit: kW, kWh (DscUnidadeTerciaria) |
| `accessing_agent` | VARCHAR(100) | YES | Accessing agent (SigAgenteAcessante) |
| `tusd_value` | DECIMAL(10,4) | NO | TUSD Value - Distribution tariff (VlrTUSD) |
| `te_value` | DECIMAL(10,4) | NO | TE Value - Energy tariff (VlrTE) |
| **Tariff Flags API Fields** |
| `flag_generation_date` | DATE | YES | Flag dataset generation date (DatGeracaoConjuntoDados) |
| `competence_date` | DATE | YES | Competence/validity month (DatCompetencia) |
| `activated_flag_name` | VARCHAR(50) | YES | Flag name: Verde, Amarela, Vermelha (NomBandeiraAcionada) |
| `flag_additional_value` | DECIMAL(10,4) | YES | Additional cost per 100 kWh (VlrAdicionalBandeira) |
| **Audit Fields** |
| `created_at` | TIMESTAMP | NO | Record creation timestamp (auto) |
| `updated_at` | TIMESTAMP | NO | Last update timestamp (auto) |

---

## 🔒 Constraints

### Primary Key
- **Name:** `tariffs_pkey`
- **Column:** `id`
- **Type:** UUID (auto-generated with `uuid_generate_v4()`)

### Check Constraints

1. **`chk_valid_dates`**
   - Ensures `valid_until >= valid_from` or `valid_until IS NULL`
   - Prevents invalid date ranges

2. **`chk_cnpj_distributor_format`**
   - Pattern: `^\d{14}$`
   - Ensures CNPJ is exactly 14 numeric digits

3. **`tusd_value >= 0`**
   - Prevents negative TUSD values

4. **`te_value >= 0`**
   - Prevents negative TE values

5. **`flag_additional_value >= 0`**
   - Prevents negative flag additional values

---

## 📇 Indexes

Performance-optimized indexes for common query patterns:

### 1. `idx_tariff_search`
**Columns:** `distributor`, `subgroup`, `tariff_modality`, `valid_from`, `valid_until`  
**Purpose:** Search for active tariffs by distributor and characteristics

### 2. `idx_tariff_cnpj`
**Column:** `cnpj_distributor`  
**Purpose:** Search by CNPJ

### 3. `idx_tariff_validity`
**Columns:** `valid_from`, `valid_until`  
**Purpose:** Find tariffs active in a specific date range

### 4. `idx_tariff_distributor`
**Column:** `distributor`  
**Purpose:** Filter by distributor name

### 5. `idx_tariff_competence_date`
**Column:** `competence_date`  
**Purpose:** Match tariff flags by competence date

---

## ⚙️ Triggers & Functions

### `update_tariffs_updated_at` Trigger
Automatically updates `updated_at` field on every UPDATE operation.

**Implementation:**
```sql
CREATE TRIGGER update_tariffs_updated_at
    BEFORE UPDATE ON tariffs
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
```

**Note:** The `update_updated_at_column()` function was created in V001.

---

## 🔗 Relationships

### Foreign Key Dependencies

**Incoming (Future):**
- `electricity_bills.tariff_id` → `tariffs.id` (Will be created in V006)
  - **Relationship:** Many-to-One
  - **Cascade:** NO ACTION (prevent deletion of tariffs in use)

---

## 🔄 Migration Dependencies

### Prerequisites
- ✅ V001 - `users` table with `update_updated_at_column()` function
- ✅ PostgreSQL `uuid-ossp` extension enabled

### Dependent Tables (Future Migrations)
- 📋 V006 - `electricity_bills` table (will reference `tariffs.id`)

---

## 📡 ANEEL API Integration

### API Endpoints

#### 1. Energy Tariffs API
- **Resource ID:** `fcf2906c-7c32-4b9b-a637-054e7a5234f4`
- **Endpoint:** `https://dadosabertos.aneel.gov.br/api/3/action/datastore_search`
- **Parameters:** `resource_id`, `limit`, `offset`

**Example Response:**
```json
{
  "success": true,
  "result": {
    "records": [{
      "DatGeracaoConjuntoDados": "2025-12-24",
      "DscREH": "RESOLUÇÃO HOMOLOGATÓRIA Nº 0.937...",
      "SigAgente": "CPFL JAGUARI",
      "NumCNPJDistribuidora": "53859112000169",
      "DatInicioVigencia": "2010-02-03",
      "DatFimVigencia": "2011-02-02",
      "VlrTUSD": "1,85",
      "VlrTE": "0,00"
    }]
  }
}
```

#### 2. Tariff Flags API
- **Resource ID:** `0591b8f6-fe54-437b-b72b-1aa2efd46e42`
- **Endpoint:** `https://dadosabertos.aneel.gov.br/api/3/action/datastore_search`

**Example Response:**
```json
{
  "success": true,
  "result": {
    "records": [{
      "DatGeracaoConjuntoDados": "2025-12-22",
      "DatCompetencia": "2015-01-01",
      "NomBandeiraAcionada": "Vermelha P1",
      "VlrAdicionalBandeira": "30,00"
    }]
  }
}
```

### Integration Strategy

**Snapshot Pattern:**
- Store complete tariff records as snapshots
- Allow historical queries and comparisons
- Match tariffs by `valid_from`/`valid_until` date ranges
- Match flags by `competence_date`

---

## 🧪 Testing Performed

See [validation.md](./validation.md) for detailed validation report.

### Test Coverage
- ✅ Table creation
- ✅ All constraints validation
- ✅ Index performance
- ✅ Trigger functionality
- ✅ Sample data insertion from ANEEL API
- ✅ Query patterns for active tariff lookup
- ✅ Flag matching by competence date

---

## 📝 Business Rules

1. **Active Tariff Lookup:**
   - Filter by `distributor`, `subgroup`, `tariff_modality`
   - Match date: `valid_from <= search_date AND (valid_until IS NULL OR valid_until >= search_date)`

2. **Flag Application:**
   - Match by `competence_date` (year-month)
   - Apply `flag_additional_value` per 100 kWh consumed

3. **Data Update Strategy:**
   - Periodic sync with ANEEL API (daily/weekly)
   - Never UPDATE existing records (immutable snapshots)
   - Always INSERT new records for changes

---

## 📚 References

- [ANEEL Open Data Portal](https://dadosabertos.aneel.gov.br/)
- [Energy Tariffs Dataset](https://dadosabertos.aneel.gov.br/dataset/tarifas-de-energia)
- [Tariff Flags Dataset](https://dadosabertos.aneel.gov.br/dataset/bandeiras-tarifarias)
- [JPA Entity Modeling](../../jpa-entity-modeling.md)
- [ER Diagram](../../ER_DIAGRAM.md)
- [Tariff Entity Model](../../../../backend/src/main/java/com/understand_your_electricity_bill/model/Tariff.java)

---

## ✅ Status: IMPLEMENTED

Migration V005 is fully implemented and ready for testing.

**Next Steps:**
1. Execute validation tests (see validation.md)
2. Implement TariffRepository with query methods
3. Create ANEEL API integration service
4. Proceed to V006 (electricity_bills table)

