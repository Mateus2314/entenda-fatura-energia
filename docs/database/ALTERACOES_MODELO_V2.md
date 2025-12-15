# Database Changes - v2.0

**Date:** 2024-12-15  
**Status:** ✅ Applied

---

## Key Changes

### 1. Added Address Fields to CLIENTS and CONSULTANTS
Both tables now have complete address structure:
- `city` VARCHAR(100)
- `state` VARCHAR(2)
- `zip_code` VARCHAR(10)
- `created_at`, `updated_at` TIMESTAMP

### 2. Added Consultant-Specific Fields
- `consultant_name` VARCHAR(255) - Consultant's personal name
- `company_logo` TEXT - Company branding

### 3. New Relationships
- **Consultants → ElectricityBills** (1:N) - Consultants can manage bills
- **Tariffs → ElectricityBills** (N:1) - Each bill MUST have one tariff

### 4. Renamed
- `BillsData` → `ElectricityBills` (better naming)

---

## Migration Scripts

### V003 - Update Clients & Consultants
```sql
ALTER TABLE clients
ADD COLUMN city VARCHAR(100),
ADD COLUMN state VARCHAR(2),
ADD COLUMN zip_code VARCHAR(10),
ADD COLUMN created_at TIMESTAMP DEFAULT NOW(),
ADD COLUMN updated_at TIMESTAMP;

ALTER TABLE consultants
ADD COLUMN consultant_name VARCHAR(255) NOT NULL DEFAULT 'Consultant',
ADD COLUMN city VARCHAR(100),
ADD COLUMN state VARCHAR(2),
ADD COLUMN zip_code VARCHAR(10),
ADD COLUMN company_logo TEXT,
ADD COLUMN created_at TIMESTAMP DEFAULT NOW(),
ADD COLUMN updated_at TIMESTAMP;
```

### V004 - Create Tariffs Table
```sql
CREATE TABLE tariffs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    distributor VARCHAR(100) NOT NULL,
    tariff_type VARCHAR(50) NOT NULL,
    value NUMERIC(10,4) NOT NULL,
    valid_from DATE NOT NULL,
    valid_until DATE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_tariffs_distributor ON tariffs(distributor);
CREATE INDEX idx_tariffs_dist_date ON tariffs(distributor, valid_from);
```

### V005 - Add Tariff to Electricity Bills
```sql
ALTER TABLE electricity_bills
ADD COLUMN tariff_id UUID NOT NULL REFERENCES tariffs(id);

CREATE INDEX idx_bills_tariff_id ON electricity_bills(tariff_id);
```

---

## Quick Reference

**Key Difference:**
- CLIENTS use CPF (individual)
- CONSULTANTS use CNPJ (company)

**Both have:**
- Complete address (address, city, state, zip_code)
- Timestamps (created_at, updated_at)

**See:** [ER_DIAGRAM.md](./ER_DIAGRAM.md) for visual reference

### 1. Migrations Flyway

**V003 - Add address fields to Clients and update Consultants:**
```sql
-- Add missing address fields to Clients
ALTER TABLE clients
ADD COLUMN city VARCHAR(100),
ADD COLUMN state VARCHAR(2),
ADD COLUMN zip_code VARCHAR(10),
ADD COLUMN created_at TIMESTAMP DEFAULT NOW(),
ADD COLUMN updated_at TIMESTAMP;

-- Add fields to Consultants
ALTER TABLE consultants
ADD COLUMN consultant_name VARCHAR(255) NOT NULL DEFAULT 'Consultant',
ADD COLUMN city VARCHAR(100),
ADD COLUMN state VARCHAR(2),
ADD COLUMN zip_code VARCHAR(10),
ADD COLUMN company_logo TEXT,
ADD COLUMN created_at TIMESTAMP DEFAULT NOW(),
ADD COLUMN updated_at TIMESTAMP;
```

**V004 - Criar tabela Tariffs:**
```sql
CREATE TABLE tariffs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    distributor VARCHAR(100) NOT NULL,
    tariff_type VARCHAR(50) NOT NULL,
    value NUMERIC(10,4) NOT NULL,
    valid_from DATE NOT NULL,
    valid_until DATE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_tariffs_distributor ON tariffs(distributor);
CREATE INDEX idx_tariffs_dist_date ON tariffs(distributor, valid_from);
```

**V005 - Adicionar tariff_id em Electricity Bills:**
```sql
ALTER TABLE electricity_bills
ADD COLUMN tariff_id UUID NOT NULL REFERENCES tariffs(id);

CREATE INDEX idx_bills_tariff_id ON electricity_bills(tariff_id);
```

### 2. Atualizar Entidades JPA

- ✅ Atualizar `Consultant.java` com novos campos
- ✅ Criar `Tariff.java`
- ✅ Atualizar `ElectricityBill.java` com relacionamento Tariff
- ✅ Criar `TariffRepository.java`

### 3. Atualizar Services

- ✅ `ConsultantService` - validar novos campos
- ✅ Criar `TariffService` - CRUD de tarifas
- ✅ `ElectricityBillService` - validar tariff_id obrigatório

### 4. Atualizar DTOs

- ✅ `ConsultantDTO` - incluir novos campos
- ✅ Criar `TariffDTO`
- ✅ `ElectricityBillDTO` - incluir tariff_id

### 5. Testes

- ✅ Testar herança Consultant com novos campos
- ✅ Testar relacionamento Consultant → Bills
- ✅ Testar relacionamento Tariff → Bills
- ✅ Testar constraint NOT NULL em tariff_id
- ✅ Testar validações de unicidade

---

## ✅ Checklist de Validação

- [x] Diagrama ER atualizado e documentado
- [x] Tabela Consultants com todos os campos de Client + específicos
- [x] Campo company_logo adicionado
- [x] Relacionamento Consultants → Electricity Bills documentado
- [x] Relacionamento Tariffs → Electricity Bills documentado
- [x] Entidade Tariff criada e documentada
- [x] Foreign key tariff_id adicionada em electricity_bills
- [x] Índices definidos para performance
- [x] Documentação atualizada em todos os arquivos relevantes
- [x] Regras de negócio documentadas
- [x] Plano de migração definido

---

## 📚 Referências

- [ER_DIAGRAM.md](./ER_DIAGRAM.md) - Diagrama visual completo
- [jpa-inheritance-strategy.md](./jpa-inheritance-strategy.md) - Estratégia de herança JPA
- [README.md](./README.md) - Overview do banco de dados
- [ISSUE_SUMMARY.md](./ISSUE_SUMMARY.md) - Resumo da issue original

---

**Documentado por:** GitHub Copilot  
**Data:** 15/12/2024  
**Versão:** 2.0  
**Status:** ✅ Completo e Validado

