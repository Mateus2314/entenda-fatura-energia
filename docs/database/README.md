# Database Documentation

Database design documentation for the **Understand Your Electricity Bill** project.

---

## 📁 Key Documents

- **[ER_DIAGRAM.md](ER_DIAGRAM.md)** - Visual ER model (⭐ PRIMARY REFERENCE)
- **[jpa-inheritance-strategy.md](jpa-inheritance-strategy.md)** - JPA JOINED strategy
- **[ALTERACOES_MODELO_V2.md](ALTERACOES_MODELO_V2.md)** - Recent changes log
- **[migrations/](migrations/)** - Flyway migration files

---

## 🗄️ Schema Overview (JOINED Inheritance)

```
users (base)
├── clients (CPF - individual)
├── consultants (CNPJ - company)
└── admins (role, permissions)
```

**Key Differences:**
- **Clients:** CPF (pessoa física), own bills, address info
- **Consultants:** CNPJ (pessoa jurídica), manage bills, company_logo, address info
- **Admins:** Role-based access, JSONB permissions, no address/documents

**Shared:** created_at, updated_at (all types)

---

## 📊 Main Relationships

```
Clients ──1:N──► ElectricityBills
Consultants ──1:N──► ElectricityBills
Tariffs ──1:N──► ElectricityBills (⭐ required)
ElectricityBills ──1:N──► BillItems
ElectricityBills ──1:1──► Analyses
```

---

## 🔧 Tech Stack

- **DB:** PostgreSQL 15+
- **ORM:** Hibernate (Spring Data JPA)
- **Migrations:** Flyway
- **Testing:** Testcontainers

---

## 📝 Conventions

- **Tables:** snake_case (e.g., `electricity_bills`)
- **PKs:** UUID
- **Timestamps:** TIMESTAMP UTC
- **Money:** NUMERIC(10,2)
- **FKs:** `{entity}_id`

---

## 🚀 Quick Start

```bash
# Start PostgreSQL
docker-compose up -d postgres

# Run migrations
mvn flyway:migrate
```

---

**See [ER_DIAGRAM.md](ER_DIAGRAM.md) for complete visual reference.**
}
```

---

## 📈 Performance

### Indexing Strategy
- Primary keys: Automatic B-tree index
- Foreign keys: Automatic index
- Email lookups: Index on `users.email`
- CPF/CNPJ: Indexes on `clients.cpf`, `consultants.cnpj`
- Status filtering: Index on `users.status`

### Query Optimization
- Use specific repositories (ClientRepository) for type-specific queries
- Avoid polymorphic queries when possible
- Use `JOIN FETCH` for eager loading relationships
- DTO projections for list views
- Second-level cache for frequently accessed entities

---

## 🔄 Migration Workflow

1. Create Flyway migration file: `V{version}__{description}.sql`
2. Test locally with `flyway:migrate`
3. Add rollback script (if needed): `U{version}__{description}.sql`
4. Commit migration files to Git
5. CI/CD runs migrations on deployment
6. Never modify existing migration files

---

**Last Updated:** December 2024  
**Maintainer:** Development Team

