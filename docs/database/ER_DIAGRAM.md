# Entity-Relationship (ER) Model - Visual

## 📊 Complete ER Diagram

```
┌────────────────────────────────────────────────────────────────────┐
│                        USERS (Base Table)                          │
├────────────────────────────────────────────────────────────────────┤
│ PK │ id                  UUID                                      │
│    │ email               VARCHAR(255)    UNIQUE, NOT NULL          │
│    │ password            VARCHAR(255)    NOT NULL                  │
│    │ name                VARCHAR(255)    NOT NULL                  │
│    │ phone               VARCHAR(50)                               │
│    │ created_at          TIMESTAMP       NOT NULL, DEFAULT NOW()   │
│    │ updated_at          TIMESTAMP                                 │
│    │ status              VARCHAR(50)     NOT NULL                  │
└────────────────────────────────────────────────────────────────────┘
                                │
                                │ 1:1 (JOINED Inheritance)
                                │
                ┌───────────────┼───────────────┐
                │               │               │
                │ 1:1           │ 1:1           │ 1:1
                ▼               ▼               ▼
┌────────────────────────────────────┐ ┌───────────────────────────────────┐ ┌────────────────────────────────┐
│          CLIENTS                   │ │         CONSULTANTS               │ │          ADMINS                │
│       (Individual/PF)              │ │        (Company/PJ)               │ │                                │
├────────────────────────────────────┤ ├───────────────────────────────────┤ ├────────────────────────────────┤
│PK,FK│user_id          UUID         │ │PK,FK│user_id          UUID        │ │PK,FK│user_id      UUID         │
│     │address          VARCHAR(500) │ │     │consultant_name  VARCHAR(255)│ │     │role         VARCHAR(50)  │
│     │                 NOT NULL     │ │     │                 NOT NULL    │ │     │             NOT NULL     │
│     │city             VARCHAR(100) │ │     │company          VARCHAR(255)│ │     │permissions  JSONB        │
│     │state            VARCHAR(2)   │ │     │                 NOT NULL    │ └────────────────────────────────┘
│     │zip_code         VARCHAR(10)  │ │     │cnpj             VARCHAR(14) │
│     │cpf              VARCHAR(11)  │ │     │                 UNIQUE,     │
│     │                 UNIQUE,      │ │     │                 NOT NULL    │
│     │                 NOT NULL     │ │     │registration_num VARCHAR(50) │
│     │registration_date DATE         │ │     │address          VARCHAR(500)│
│     │                 NOT NULL     │ │     │                 NOT NULL    │
│     │created_at       TIMESTAMP    │ │     │city             VARCHAR(100)│
│     │                 DEFAULT NOW()│ │     │state            VARCHAR(2)  │
│     │updated_at       TIMESTAMP    │ │     │zip_code         VARCHAR(10) │
└────────────────────────────────────┘ │     │company_logo     TEXT        │
            │                          │     │created_at       TIMESTAMP   │
            │ 1                        │     │                 DEFAULT NOW()│
            │                          │     │updated_at       TIMESTAMP   │
            │                          └───────────────────────────────────┘
            │                                      │
            │ N                                    │ N
            │                        │
            │            ┌───────────┘
            │            │
            │            │
            ▼            ▼
┌───────────────────────────────────────────────────────────────────────────┐
│                          ELECTRICITY_BILLS                                │
├───────────────────────────────────────────────────────────────────────────┤
│ PK  │ id                    UUID                                          │
│ FK  │ client_id             UUID            NOT NULL → clients.user_id    │
│ FK  │ consultant_id         UUID            NULL → consultants.user_id    │
│ FK  │ tariff_id             UUID            NOT NULL → tariffs.id         │
│     │ reference_month       DATE            NOT NULL                      │
│     │ due_date              DATE            NOT NULL                      │
│     │ total_amount          DECIMAL(10,2)   NOT NULL                      │
│     │ consumption_kwh       DECIMAL(10,2)   NOT NULL                      │
│     │ pdf_url               TEXT                                          │
│     │ created_at            TIMESTAMP       NOT NULL, DEFAULT NOW()       │
│     │ updated_at            TIMESTAMP       DEFAULT NOW()                 │
└───────────────────────────────────────────────────────────────────────────┘
                │                                    │
                │ 1                                  │ N
                │                                    │
                │ N                                  │ 1
                ▼                                    ▼
┌────────────────────────────────────┐    ┌────────────────────────────────────────────────┐
│          BILL_ITEMS                │    │           TARIFFS (ANEEL API)              │
├────────────────────────────────────┤    ├────────────────────────────────────────────────┤
│ PK │ id           UUID             │    │ PK │ id                   UUID                │
│ FK │ bill_id      UUID             │    │    │ generation_date      DATE       NOT NULL │
│    │              NOT NULL         │    │    │ description_reh      VARCHAR(500)        │
│    │              → elec_bills.id  │    │    │ distributor          VARCHAR(100)        │
│    │ item_type    VARCHAR(50)      │    │    │                      NOT NULL            │
│    │              NOT NULL         │    │    │ cnpj_distributor     VARCHAR(14)         │
│    │ description  VARCHAR(255)     │    │    │                      NOT NULL            │
│    │ quantity     DECIMAL(10,2)    │    │    │ valid_from           DATE       NOT NULL │
│    │ unit_price   DECIMAL(10,4)    │    │    │ valid_until          DATE                │
│    │ amount       DECIMAL(10,2)    │    │    │ tariff_base_desc     VARCHAR(100)        │
│    │              NOT NULL         │    │    │ subgroup             VARCHAR(10)         │
│    │ created_at   TIMESTAMP        │    │    │ tariff_modality      VARCHAR(50)         │
│    │              NOT NULL,        │    │    │ consumer_class       VARCHAR(100)        │
│    │              DEFAULT NOW()    │    │    │ consumer_subclass    VARCHAR(100)        │
└────────────────────────────────────┘    │    │ detail               VARCHAR(100)        │
                │                          │    │ tariff_post_name     VARCHAR(50)         │
                │                          │    │ tertiary_unit        VARCHAR(10)         │
                │                          │    │ accessing_agent      VARCHAR(100)        │
                │                          │    │ tusd_value           DECIMAL(10,4)       │
                │                          │    │                      NOT NULL            │
                │                          │    │ te_value             DECIMAL(10,4)       │
                │                          │    │                      NOT NULL            │
                │                          │    │ flag_generation_date DATE                │
                │                          │    │ competence_date      DATE                │
                │                          │    │ activated_flag_name  VARCHAR(50)         │
                │                          │    │ flag_additional_value DECIMAL(10,4)      │
                │                          │    │ created_at           TIMESTAMP           │
                │                          │    │                      DEFAULT NOW()       │
                │                          │    │ updated_at           TIMESTAMP           │
                │                          │    │                      DEFAULT NOW()       │
                │                          └────────────────────────────────────────────────┘
                │
                │ 1
                │
                │ 1
                ▼
┌────────────────────────────────────────────────────────────────────┐
│                            ANALYSES                                │
├────────────────────────────────────────────────────────────────────┤
│ PK │ id                    UUID                                    │
│ FK │ bill_id               UUID            NOT NULL, UNIQUE        │
│    │                       → electricity_bills.id                  │
│    │ average_consumption   DECIMAL(10,2)                           │
│    │ cost_per_kwh          DECIMAL(10,4)                           │
│    │ comparison_prev_month DECIMAL(5,2)                            │
│    │ savings_tips          TEXT                                    │
│    │ report_pdf_url        TEXT                                    │
│    │ created_at            TIMESTAMP       NOT NULL, DEFAULT NOW() │
└────────────────────────────────────────────────────────────────────┘
```

---

## 📊 Details: TARIFFS (ANEEL API Integration)

### Description
Stores electricity tariff information synchronized from two ANEEL Open Data APIs:
1. **Energy Tariffs** (base values)
2. **Tariff Flags** (consumption surcharges)

**API Sources:**
- Tariffs: `https://dadosabertos.aneel.gov.br/api/3/action/datastore_search?resource_id=fcf2906c-7c32-4b9b-a637-054e7a5234f4`
- Flags: `https://dadosabertos.aneel.gov.br/api/3/action/datastore_search?resource_id=0591b8f6-fe54-437b-b72b-1aa2efd46e42`

### Field Mapping (API → Database)

#### Energy Tariffs (Resource ID: fcf2906c-7c32-4b9b-a637-054e7a5234f4)

| API Field (ANEEL) | Database Field | Type | Description |
|-------------------|----------------|------|-----------|
| `DatGeracaoConjuntoDados` | `generation_date` | `DATE` | Dataset generation date |
| `DscREH` | `description_reh` | `VARCHAR(500)` | Homologatory Resolution |
| `SigAgente` | `distributor` | `VARCHAR(100)` | Distributor name (e.g., CPFL JAGUARI) |
| `NumCNPJDistribuidora` | `cnpj_distributor` | `VARCHAR(14)` | Distributor CNPJ |
| `DatInicioVigencia` | `valid_from` | `DATE` | Validity start date |
| `DatFimVigencia` | `valid_until` | `DATE` | Validity end date (nullable) |
| `DscBaseTarifaria` | `tariff_base_desc` | `VARCHAR(100)` | Tariff base description |
| `DscSubGrupo` | `subgroup` | `VARCHAR(10)` | Tariff subgroup (A2, B1, etc.) |
| `DscModalidadeTarifaria` | `tariff_modality` | `VARCHAR(50)` | Modality (Blue, Green, Conventional) |
| `DscClasse` | `consumer_class` | `VARCHAR(100)` | Consumer class (Residential, Industrial) |
| `DscSubClasse` | `consumer_subclass` | `VARCHAR(100)` | Consumer subclass |
| `DscDetalhe` | `detail` | `VARCHAR(100)` | Additional details (APE, etc.) |
| `NomPostoTarifario` | `tariff_post_name` | `VARCHAR(50)` | Tariff post (Peak, Off-peak) |
| `DscUnidadeTerciaria` | `tertiary_unit` | `VARCHAR(10)` | Unit (kW, kWh) |
| `SigAgenteAcessante` | `accessing_agent` | `VARCHAR(100)` | Accessing agent |
| `VlrTUSD` | `tusd_value` | `DECIMAL(10,4)` | TUSD Value (Distribution System Usage Tariff) |
| `VlrTE` | `te_value` | `DECIMAL(10,4)` | TE Value (Energy Tariff) |

#### Tariff Flags (Resource ID: 0591b8f6-fe54-437b-b72b-1aa2efd46e42) ⭐ NEW

| API Field (ANEEL) | Database Field | Type | Description |
|-------------------|----------------|------|-----------|
| `DatGeracaoConjuntoDados` | `flag_generation_date` | `DATE` | Flag dataset generation date |
| `DatCompetencia` | `competence_date` | `DATE` | Competence/validity month of flag |
| `NomBandeiraAcionada` | `activated_flag_name` | `VARCHAR(50)` | Flag name (Green, Yellow, Red P1, Red P2) |
| `VlrAdicionalBandeira` | `flag_additional_value` | `DECIMAL(10,4)` | Additional value per 100 kWh |

### Constraints

#### Primary Key
- `id` (UUID)

#### Not Null
- `generation_date`
- `distributor`
- `cnpj_distributor`
- `valid_from`
- `tusd_value`
- `te_value`

#### Check Constraints
- `valid_until >= valid_from` (when not NULL)
- `tusd_value >= 0`
- `te_value >= 0`

#### Indexes
```sql
-- Search for active tariff by distributor and characteristics
CREATE INDEX idx_tariff_search ON tariffs (
    distributor, 
    subgroup, 
    tariff_modality, 
    valid_from, 
    valid_until
);

-- Search by CNPJ
CREATE INDEX idx_tariff_cnpj ON tariffs (cnpj_distributor);

-- Search by validity period
CREATE INDEX idx_tariff_validity ON tariffs (valid_from, valid_until);
```

### Business Rules

1. **Automatic Synchronization - Two APIs**
   - **Energy tariffs** synchronized via scheduled job (API 1)
   - **Tariff flags** synchronized via scheduled job (API 2)
   - No manual tariff creation/editing
   - System maintains complete tariff history

2. **Immutability**
   - Tariffs are immutable after being associated with bills
   - New versions are created as new records
   - `ON DELETE RESTRICT` prevents deletion of tariffs in use

3. **Value Conversion**
   - API returns values with comma: `"1,85"` or `"30,00"`
   - System converts to `BigDecimal`: `1.85` or `30.00`
   - Custom parsing required

4. **Validity Periods**
   - `valid_from`: Tariff validity start date (required)
   - `valid_until`: End date (optional - NULL for current tariffs)
   - `competence_date`: Tariff flag competence month
   - Multiple tariffs can exist for same distributor in different periods
   - No overlap validation (ANEEL controls this)

5. **Data Snapshot**
   - Each bill maintains reference to applied tariff
   - If tariff changes, old bills preserve original values
   - System works with snapshots, not dynamic values

6. **Tariff Flags** ⭐ NEW
   - Flag indicates additional cost due to water scarcity
   - Types: Green (R$ 0), Yellow (~R$ 18.00), Red P1 (~R$ 30.00), Red P2 (~R$ 40.00)
   - Additional value charged per 100 kWh consumed
   - Changes monthly according to generation conditions
   - `activated_flag_name`: Active flag name for the month
   - `flag_additional_value`: Additional value per 100 kWh

### Active Tariff Query Example
```java
// Buscar tarifa aplicável para uma fatura
Tariff findActiveTariff(
    String distributor,      // Ex: "CPFL JAGUARI"
    String subgroup,         // Ex: "B1"
    String modality,         // Ex: "Convencional"
    LocalDate referenceDate  // Data de referência da fatura
) {
    return tariffRepository.findByDistributorAndSubgroupAndModalityAndDate(
        distributor, 
        subgroup, 
        modality, 
        referenceDate // WHERE referenceDate BETWEEN valid_from AND valid_until
    );
}
```

### Important Notes

⚠️ **"Not applicable" Fields**
- API may return "Não se aplica" (Not applicable) in fields like `consumer_class`
- Decision: Store as is or convert to NULL (define in mapper)

⚠️ **Zero Values**
- `VlrTE` can be `",00"` (zero with comma)
- `VlrAdicionalBandeira` can be `"0,00"` for Green flag
- Handle edge cases in parsing

⚠️ **Data Volume**
- ANEEL API has thousands of historical tariffs
- Consider filters in synchronization (recent tariffs only?)
- Implement pagination in API queries

⚠️ **Two APIs Integration** ⭐ NEW
- **Tariffs** and **Flags** come from different endpoints
- Both must be synchronized for complete calculation
- `competence_date` of flag must match `reference_month` of bill
- Final calculation: `(TUSD + TE) * consumption_kwh + (flag_additional_value * consumption_kwh / 100)`

⚠️ **Tariff Flags - Monthly Update**
- Flag changes monthly (DatCompetencia)
- Synchronize monthly or before processing bills
- Green flag can have R$ 0.00 value (no additional cost)

---

## 🔗 Detailed Relationships

### 1. **USERS → CLIENTS/CONSULTANTS/ADMINS** (1:1 - JOINED Inheritance)
- **Type:** JOINED Inheritance
- **Description:** Each user type inherits from `users` and has its own table
- **Primary Key:** `user_id` in child tables is FK to `users.id`
- **Cascade:** ON DELETE CASCADE (deletes user → deletes subentity)

### 2. **CLIENTS → ELECTRICITY_BILLS** (1:N)
- **Type:** One-to-Many
- **Description:** A client can have multiple electricity bills
- **Foreign Key:** `electricity_bills.client_id → clients.user_id`
- **Constraint:** NOT NULL (every bill MUST have a client)
- **Cascade:** ON DELETE RESTRICT (cannot delete client with bills)

### 3. **CONSULTANTS → ELECTRICITY_BILLS** (1:N)
- **Type:** One-to-Many (Optional)
- **Description:** A consultant can manage multiple client bills
- **Foreign Key:** `electricity_bills.consultant_id → consultants.user_id`
- **Constraint:** NULL (bill may not have an associated consultant)
- **Cascade:** ON DELETE SET NULL (deletes consultant → keeps bill)
- **Note:** Consultant is like an "upgraded" client with additional fields

### 4. **TARIFFS → ELECTRICITY_BILLS** (1:N) ⭐ NEW
- **Type:** One-to-Many
- **Description:** A tariff can be in multiple bills, but each bill has only ONE tariff
- **Foreign Key:** `electricity_bills.tariff_id → tariffs.id`
- **Constraint:** NOT NULL (every bill MUST have a tariff)
- **Cascade:** ON DELETE RESTRICT (cannot delete tariff in use)

### 5. **ELECTRICITY_BILLS → BILL_ITEMS** (1:N)
- **Type:** One-to-Many
- **Description:** A bill has multiple detailed charge items
- **Foreign Key:** `bill_items.bill_id → electricity_bills.id`
- **Constraint:** NOT NULL
- **Cascade:** ON DELETE CASCADE (deletes bill → deletes items)

### 6. **ELECTRICITY_BILLS → ANALYSES** (1:1)
- **Type:** One-to-One
- **Description:** Each bill can have ONE generated analysis
- **Foreign Key:** `analyses.bill_id → electricity_bills.id`
- **Constraint:** NOT NULL, UNIQUE
- **Cascade:** ON DELETE CASCADE (deletes bill → deletes analysis)

---

## 📋 Cardinalities

| Relationship | Cardinality | Required? |
|---------|---------------|--------------|
| User → Client/Consultant/Admin | 1:1 | Yes (inheritance) |
| Client → Electricity_Bills | 1:N | No (client may have no bills) |
| Consultant → Electricity_Bills | 1:N | No (consultant may not manage bills) |
| **Tariff → Electricity_Bills** | **1:N** | **Yes (bill needs tariff)** |
| Electricity_Bills → Bill_Items | 1:N | No (bill may not have itemization) |
| Electricity_Bills → Analyses | 1:1 | No (bill may not have analysis) |

---

## 🔑 Keys and Indexes

### Primary Keys (PK)
- `users.id` - UUID
- `clients.user_id` - UUID (também FK)
- `consultants.user_id` - UUID (também FK)
- `admins.user_id` - UUID (também FK)
- `electricity_bills.id` - UUID
- `bill_items.id` - UUID
- `tariffs.id` - UUID
- `analyses.id` - UUID

### Foreign Keys (FK)
- `clients.user_id → users.id`
- `consultants.user_id → users.id`
- `admins.user_id → users.id`
- `electricity_bills.client_id → clients.user_id`
- `electricity_bills.consultant_id → consultants.user_id`
- **`electricity_bills.tariff_id → tariffs.id`** ⭐ NEW
- `bill_items.bill_id → electricity_bills.id`
- `analyses.bill_id → electricity_bills.id`

### Recommended Indexes
```sql
-- Users
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_status ON users(status);

-- Clients
CREATE INDEX idx_clients_cpf ON clients(cpf);
CREATE INDEX idx_clients_user_id ON clients(user_id);

-- Consultants
CREATE INDEX idx_consultants_cnpj ON consultants(cnpj);
CREATE INDEX idx_consultants_user_id ON consultants(user_id);

-- Electricity Bills
CREATE INDEX idx_bills_client_id ON electricity_bills(client_id);
CREATE INDEX idx_bills_consultant_id ON electricity_bills(consultant_id);
CREATE INDEX idx_bills_tariff_id ON electricity_bills(tariff_id);
CREATE INDEX idx_bills_reference_month ON electricity_bills(reference_month);

-- Bill Items
CREATE INDEX idx_bill_items_bill_id ON bill_items(bill_id);

-- Tariffs
CREATE INDEX idx_tariffs_distributor ON tariffs(distributor);
CREATE INDEX idx_tariffs_valid_from ON tariffs(valid_from);
CREATE INDEX idx_tariffs_dist_date ON tariffs(distributor, valid_from);

-- Analyses
CREATE INDEX idx_analyses_bill_id ON analyses(bill_id);
CREATE UNIQUE INDEX idx_analyses_bill_unique ON analyses(bill_id);
```

---

## 📐 Business Rules in Model

### 1. User Inheritance (JOINED)
- ✅ Every `Client`, `Consultant` or `Admin` IS A `User`
- ✅ `User` cannot exist without being one of the specific types (abstract class)
- ✅ `user_id` in child tables is both PK and FK simultaneously
- ✅ Consultant inherits ALL fields from Client + specific fields

### 2. Bill Ownership
- ✅ Every bill MUST belong to a client (`client_id NOT NULL`)
- ✅ Bill MAY have an associated consultant (`consultant_id NULL`)
- ✅ **Every bill MUST have an applied tariff (`tariff_id NOT NULL`)** ⭐ NEW
- ✅ Client cannot be deleted if it has bills

### 3. Uniqueness
- ✅ User email is unique in the system
- ✅ Client CPF is unique
- ✅ Consultant CNPJ is unique
- ✅ Each bill has only ONE analysis (1:1 relationship)
- ✅ **Each bill has only ONE applied tariff** ⭐ NEW

### 4. Temporal Integrity
- ✅ Tariffs have validity period (`valid_from`, `valid_until`)
- ✅ Bills have reference month and due date
- ✅ Analyses are created after the bill

### 5. Required Fields
**Users:**
- email, password, name, status, created_at

**Clients (Individual - CPF):**
- user_id, address, city, state, zip_code, cpf, registration_date

**Consultants (Company - CNPJ, "upgraded" Client):**
- user_id, consultant_name, company, cnpj, address, city, state, zip_code
- registration_number (optional)
- company_logo (optional) ⭐ NEW

**Electricity_Bills:**
- id, client_id, tariff_id, reference_month, due_date, total_amount, consumption_kwh

**Tariffs (ANEEL API Fields):**
- id, generation_date, distributor, cnpj_distributor, valid_from, tusd_value, te_value
- Optional: valid_until, description_reh, tariff_base_desc, subgroup, tariff_modality, consumer_class, consumer_subclass, detail, tariff_post_name, tertiary_unit, accessing_agent

**BillItems:**
- id, bill_id, item_type, amount, created_at
- Optional: description, quantity, unit_price

**Analyses:**
- id, bill_id, created_at
- Optional: average_consumption, cost_per_kwh, comparison_prev_month, savings_tips, report_pdf_url

**Key Differences:**
- ✅ **Client** uses **CPF** (individual tax ID - pessoa física)
- ✅ **Consultant** uses **CNPJ** (company tax ID - pessoa jurídica)
- ✅ **Consultant** has additional company-specific fields (company, company_logo, registration_number)
- ✅ **Both** have complete address fields (address, city, state, zip_code)

---

## 🎨 Simplified Visualization

```
           ┌─────────┐
           │  USERS  │ (Base)
           └────┬────┘
                │
        ┌───────┼───────┐
        │       │       │
     ┌──▼──┐ ┌─▼──┐ ┌─▼───┐
     │CLIENT│ │CONS│ │ADMIN│
     └──┬──┘ └─┬──┘ └─────┘
        │      │
        └──┬───┘
           │
     ┌─────▼──────┐
     │ ELEC_BILLS │◄────┐
     └─┬────┬─────┘     │
       │    │         ┌─┴────┐
       │    │         │TARIFF│ ⭐ NEW
       │    │         └──────┘
       │    │
   ┌───▼──┐ └──────┐
   │ITEMS │        │
   └──────┘    ┌───▼────┐
               │ANALYSES│
               └────────┘
```

**Legend:**
- `─` : Relationship
- `▼` : Inheritance (is-a)
- `◄` : Foreign Key (belongs-to)
- ⭐ : New relationship added

---

## 📊 Model Statistics

| Metric | Value |
|---------|-------|
| **Total Tables** | 8 |
| **Base Tables** | 1 (users) |
| **Inheritance Tables** | 3 (clients, consultants, admins) |
| **Domain Tables** | 4 (electricity_bills, bill_items, tariffs, analyses) |
| **Total FKs** | 9 |
| **1:1 Relationships** | 4 (inheritance + analyses) |
| **1:N Relationships** | 5 (bills, items, tariffs) |
| **Recommended Indexes** | 16 |

---

## 🔄 Main Changes

### ✅ Implemented Changes

1. **Consultant as "upgraded" Client** ⭐
   - Consultant has the same base address fields as Client
   - Added consultant-specific name field: `consultant_name`
   - Added company fields: company, cnpj (instead of cpf), registration_number
   - Added branding field: `company_logo` (URL/path for image)
   - Both have: address, city, state, zip_code
   - Main difference: Client uses CPF (individual), Consultant uses CNPJ (company)

2. **Consultant → Electricity_Bills Relationship** ⭐
   - Added 1:N relationship
   - Consultant can manage multiple bills

3. **Tariffs → Electricity_Bills Relationship** ⭐
   - Added 1:N relationship
   - Each bill has exactly ONE tariff
   - FK: `electricity_bills.tariff_id → tariffs.id`
   - NOT NULL (required)

4. **TARIFFS Table - ANEEL API Integration** ⭐⭐ NEW
   - Expanded from 7 fields to 17 fields
   - All fields mapped from ANEEL Open Data API
   - Added detailed field mapping documentation
   - Changed from simple cost storage to complete tariff snapshot
   - Fields include: generation_date, description_reh, distributor, cnpj_distributor, valid_from, valid_until, tariff_base_desc, subgroup, tariff_modality, consumer_class, consumer_subclass, detail, tariff_post_name, tertiary_unit, accessing_agent, tusd_value, te_value

5. **Removed Redundant Fields from ELECTRICITY_BILLS** ⭐⭐ NEW
   - Removed `tariff_modality` (already available in tariffs.tariff_modality via FK)
   - Removed `status` field (not necessary for current business rules)
   - Cleaner design with tariff data centralized in TARIFFS table

6. **company_logo field in Consultants** ⭐
   - Type: TEXT (URL or path)
   - Stores consultant's company logo

---

## 📝 Implementation Notes

### Table Creation Order (Flyway Migrations)
```
V001 - users (base)
V002 - clients (inheritance)
V003 - consultants (inheritance)
V004 - admins (inheritance)
V005 - tariffs (independent, with 17 ANEEL fields)
V006 - electricity_bills (depends: clients, consultants, tariffs)
V007 - bill_items (depends: electricity_bills)
V008 - analyses (depends: electricity_bills)
```

### Deletion Order (Cascade)
```
analyses → bill_items → electricity_bills → consultants/clients → users
                                          ↘ tariffs (independent, RESTRICT)
```

### ANEEL API Integration
**Base URL:** `https://dadosabertos.aneel.gov.br/api/3/action/datastore_search`

**Integrated APIs:**
1. **Energy Tariffs**
   - Resource ID: `fcf2906c-7c32-4b9b-a637-054e7a5234f4`
   - Synchronization: Scheduled job (daily at 2 AM)
   - Fields: 17 fields (distributor, TUSD, TE, etc.)

2. **Tariff Flags** ⭐ NEW
   - Resource ID: `0591b8f6-fe54-437b-b72b-1aa2efd46e42`
   - Synchronization: Scheduled job (monthly or daily)
   - Fields: 4 fields (flag_generation_date, competence_date, activated_flag_name, flag_additional_value)

**Strategy:** Complete snapshot (not real-time updates)

---

**Last Updated:** 12/26/2025  
**Model Version:** 3.2 (BillItem and Analysis entities implemented)  
**Status:** ✅ All model entities implemented

