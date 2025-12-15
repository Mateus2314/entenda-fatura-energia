# Modelo Entidade-Relacionamento (ER) - Visual

## 📊 Diagrama ER Completo

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
│     │ tariff_modality       VARCHAR(50)                                   │
│     │ pdf_url               TEXT                                          │
│     │ status                VARCHAR(20)                                   │
│     │ created_at            TIMESTAMP       NOT NULL, DEFAULT NOW()       │
│     │ updated_at            TIMESTAMP       DEFAULT NOW()                 │
└───────────────────────────────────────────────────────────────────────────┘
                │                                    │
                │ 1                                  │ N
                │                                    │
                │ N                                  │ 1
                ▼                                    ▼
┌────────────────────────────────────┐    ┌────────────────────────────────────┐
│          BILL_ITEMS                │    │           TARIFFS                  │
├────────────────────────────────────┤    ├────────────────────────────────────┤
│ PK │ id           UUID             │    │ PK │ id              UUID          │
│ FK │ bill_id      UUID             │    │    │ distributor     VARCHAR(100)  │
│    │              NOT NULL         │    │    │                 NOT NULL      │
│    │              → elec_bills.id  │    │    │ tariff_type     VARCHAR(50)   │
│    │ item_type    VARCHAR(50)      │    │    │                 NOT NULL      │
│    │              NOT NULL         │    │    │ value           DECIMAL(10,4) │
│    │ description  VARCHAR(255)     │    │    │                 NOT NULL      │
│    │ quantity     DECIMAL(10,2)    │    │    │ valid_from      DATE          │
│    │ unit_price   DECIMAL(10,4)    │    │    │                 NOT NULL      │
│    │ amount       DECIMAL(10,2)    │    │    │ valid_until     DATE          │
│    │              NOT NULL         │    │    │ created_at      TIMESTAMP     │
│    │ created_at   TIMESTAMP        │    │    │                 NOT NULL,     │
│    │              NOT NULL,        │    │    │                 DEFAULT NOW() │
│    │              DEFAULT NOW()    │    │    │ updated_at      TIMESTAMP     │
└────────────────────────────────────┘    │    │                 DEFAULT NOW() │
                │                          └────────────────────────────────────┘
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
└─��──────────────────────────────────────────────────────────────────┘
```

---

## 🔗 Relacionamentos Detalhados

### 1. **USERS → CLIENTS/CONSULTANTS/ADMINS** (1:1 - JOINED Inheritance)
- **Tipo:** Herança JOINED
- **Descrição:** Cada tipo de usuário herda de `users` e tem sua própria tabela
- **Chave Primária:** `user_id` nas tabelas filhas é FK para `users.id`
- **Cascata:** ON DELETE CASCADE (deleta usuário → deleta subentidade)

### 2. **CLIENTS → ELECTRICITY_BILLS** (1:N)
- **Tipo:** One-to-Many
- **Descrição:** Um cliente pode ter várias faturas de energia
- **Chave Estrangeira:** `electricity_bills.client_id → clients.user_id`
- **Restrição:** NOT NULL (toda fatura DEVE ter um cliente)
- **Cascata:** ON DELETE RESTRICT (não pode deletar cliente com faturas)

### 3. **CONSULTANTS → ELECTRICITY_BILLS** (1:N)
- **Tipo:** One-to-Many (Opcional)
- **Descrição:** Um consultor pode gerenciar várias faturas de clientes
- **Chave Estrangeira:** `electricity_bills.consultant_id → consultants.user_id`
- **Restrição:** NULL (fatura pode não ter consultor associado)
- **Cascata:** ON DELETE SET NULL (deleta consultor → mantém fatura)
- **Observação:** Consultante é como um "upgrade" do cliente com campos adicionais

### 4. **TARIFFS → ELECTRICITY_BILLS** (1:N) ⭐ NOVO
- **Tipo:** One-to-Many
- **Descrição:** Uma tarifa pode estar em várias faturas, mas cada fatura tem apenas UMA tarifa
- **Chave Estrangeira:** `electricity_bills.tariff_id → tariffs.id`
- **Restrição:** NOT NULL (toda fatura DEVE ter uma tarifa)
- **Cascata:** ON DELETE RESTRICT (não pode deletar tarifa em uso)

### 5. **ELECTRICITY_BILLS → BILL_ITEMS** (1:N)
- **Tipo:** One-to-Many
- **Descrição:** Uma fatura tem vários itens de cobrança detalhados
- **Chave Estrangeira:** `bill_items.bill_id → electricity_bills.id`
- **Restrição:** NOT NULL
- **Cascata:** ON DELETE CASCADE (deleta fatura → deleta itens)

### 6. **ELECTRICITY_BILLS → ANALYSES** (1:1)
- **Tipo:** One-to-One
- **Descrição:** Cada fatura pode ter UMA análise gerada
- **Chave Estrangeira:** `analyses.bill_id → electricity_bills.id`
- **Restrição:** NOT NULL, UNIQUE
- **Cascata:** ON DELETE CASCADE (deleta fatura → deleta análise)

---

## 📋 Cardinalidades

| Relação | Cardinalidade | Obrigatório? |
|---------|---------------|--------------|
| User → Client/Consultant/Admin | 1:1 | Sim (herança) |
| Client → Electricity_Bills | 1:N | Não (cliente pode não ter faturas) |
| Consultant → Electricity_Bills | 1:N | Não (consultor pode não gerenciar faturas) |
| **Tariff → Electricity_Bills** | **1:N** | **Sim (fatura precisa de tarifa)** |
| Electricity_Bills → Bill_Items | 1:N | Não (fatura pode não ter detalhamento) |
| Electricity_Bills → Analyses | 1:1 | Não (fatura pode não ter análise) |

---

## 🔑 Chaves e Índices

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
- **`electricity_bills.tariff_id → tariffs.id`** ⭐ NOVO
- `bill_items.bill_id → electricity_bills.id`
- `analyses.bill_id → electricity_bills.id`

### Índices Recomendados
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
CREATE INDEX idx_bills_tariff_id ON electricity_bills(tariff_id); -- NOVO
CREATE INDEX idx_bills_reference_month ON electricity_bills(reference_month);
CREATE INDEX idx_bills_status ON electricity_bills(status);

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

## 📐 Regras de Negócio no Modelo

### 1. Herança de Usuários (JOINED)
- ✅ Todo `Client`, `Consultant` ou `Admin` É UM `User`
- ✅ Não existe `User` sem ser um dos tipos específicos (classe abstrata)
- ✅ `user_id` nas tabelas filhas é PK e FK simultaneamente
- ✅ Consultant herda TODOS os campos de Client + campos específicos

### 2. Propriedade de Faturas
- ✅ Toda fatura DEVE pertencer a um cliente (`client_id NOT NULL`)
- ✅ Fatura PODE ter um consultor associado (`consultant_id NULL`)
- ✅ **Toda fatura DEVE ter uma tarifa aplicada (`tariff_id NOT NULL`)** ⭐ NOVO
- ✅ Cliente não pode ser deletado se tiver faturas

### 3. Unicidade
- ✅ Email de usuário é único no sistema
- ✅ CPF de cliente é único
- ✅ CNPJ de consultor é único
- ✅ Cada fatura tem apenas UMA análise (relação 1:1)
- ✅ **Cada fatura tem apenas UMA tarifa aplicada** ⭐ NOVO

### 4. Integridade Temporal
- ✅ Tarifas têm período de validade (`valid_from`, `valid_until`)
- ✅ Faturas têm mês de referência e data de vencimento
- ✅ Análises são criadas após a fatura

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
- id, client_id, **tariff_id** ⭐, reference_month, due_date, total_amount, consumption_kwh

**Tariffs:**
- id, distributor, tariff_type, value, valid_from

**Key Differences:**
- ✅ **Client** uses **CPF** (individual tax ID - pessoa física)
- ✅ **Consultant** uses **CNPJ** (company tax ID - pessoa jurídica)
- ✅ **Consultant** has additional company-specific fields (company, company_logo, registration_number)
- ✅ **Both** have complete address fields (address, city, state, zip_code)

---

## 🎨 Visualização Simplificada

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
       │    │         │TARIFF│ ⭐ NOVO
       │    │         └──────┘
       │    │
   ┌───▼──┐ └──────┐
   │ITEMS │        │
   └──────┘    ┌───▼────┐
               │ANALYSES│
               └────────┘
```

**Legenda:**
- `─` : Relacionamento
- `▼` : Herança (is-a)
- `◄` : Foreign Key (belongs-to)
- ⭐ : Novo relacionamento adicionado

---

## 📊 Estatísticas do Modelo

| Métrica | Valor |
|---------|-------|
| **Total de Tabelas** | 8 |
| **Tabelas Base** | 1 (users) |
| **Tabelas de Herança** | 3 (clients, consultants, admins) |
| **Tabelas de Domínio** | 4 (electricity_bills, bill_items, tariffs, analyses) |
| **Total de FKs** | 9 |
| **Relacionamentos 1:1** | 4 (herança + analyses) |
| **Relacionamentos 1:N** | 5 (bills, items, tariffs) |
| **Índices Recomendados** | 16 |

---

## 🔄 Alterações Principais

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

4. **company_logo field in Consultants** ⭐
   - Type: TEXT (URL or path)
   - Stores consultant's company logo

---

## 📝 Notas de Implementação

### Ordem de Criação de Tabelas (Flyway Migrations)
```
V001 - users (base)
V002 - clients (herança)
V003 - consultants (herança)
V004 - admins (herança)
V005 - tariffs (independente)
V006 - electricity_bills (depende: clients, consultants, tariffs)
V007 - bill_items (depende: electricity_bills)
V008 - analyses (depende: electricity_bills)
```

### Ordem de Deleção (Cascade)
```
analyses → bill_items → electricity_bills → consultants/clients → users
                                          ↘ tariffs (independente)
```

---

**Última Atualização:** 15/12/2024  
**Versão do Modelo:** 2.0  
**Status:** ✅ Revisado e Atualizado

