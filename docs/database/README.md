# Database Documentation

This directory contains detailed database design documentation for the **Understand Your Electricity Bill** project.

---

## 📁 Documents

### [JPA Inheritance Strategy](jpa-inheritance-strategy.md)
**Status:** ✅ Approved  
**Date:** December 2024  

**Content:**
- Research on 3 JPA inheritance strategies (SINGLE_TABLE, JOINED, TABLE_PER_CLASS)
- Detailed pros and cons analysis
- Decision matrix with weighted criteria
- **Decision: JOINED strategy** selected
- Complete implementation guidelines
- Code examples (Entity classes, Repositories, Services)
- Database schema (SQL DDL)
- Migration scripts (Flyway)
- Performance considerations
- Testing strategies

**Why JOINED:**
- Data normalization (no NULL columns)
- Strong database constraints
- Scalable for future user types
- Clean separation of concerns
- Acceptable performance trade-off

---

## 🗄️ Database Schema Overview

### User Domain (JOINED Inheritance)

```
users (base table)
├── clients (specific attributes)
├── consultants (specific attributes)
└── admins (specific attributes)
```

**users table:**
- id, email, password, name, phone, createdAt, updatedAt, status

**clients table:**
- user_id (FK), address, cpf, registrationDate

**consultants table:**
- user_id (FK), company, cnpj, registrationNumber, address

**admins table:**
- user_id (FK), role, permissions

---

## 📊 Entity Relationship Diagram

```
User (abstract)
  ↓ (inherits)
  ├─ Client ──────► BillsData (1:N) - owns electricity bills
  │    └─────────► ClientConsultantRelation (M:N)
  │
  ├─ Consultant ──► BillsData (1:N) - manages client bills
  │    └─────────► ClientConsultantRelation (M:N)
  │
  └─ Admin (full access)

BillsData ─────► UtilityCompany (N:1)
         └─────► EngineeringData (1:1)
         └─────► AnalysisResults (1:1)
         └─────► Simulations (1:N)

Key Points:
- Every bill MUST have a client (client_id NOT NULL)
- Bills MAY have an associated consultant (consultant_id NULL)
- Consultants can only see bills where they have a relationship with the client
```

---

## 🔧 Technology Stack

- **Database:** PostgreSQL 15+
- **ORM:** Hibernate (via Spring Data JPA)
- **Migrations:** Flyway
- **Connection Pool:** HikariCP
- **Testing:** Testcontainers + PostgreSQL

---

## 📝 Conventions

### Naming
- Tables: lowercase with underscores (e.g., `users`, `bill_data`)
- Columns: lowercase with underscores (e.g., `created_at`, `user_id`)
- Foreign keys: `{entity}_id` (e.g., `user_id`, `bill_id`)
- Indexes: `idx_{table}_{column}` (e.g., `idx_users_email`)
- Constraints: `fk_{child}_{parent}` (e.g., `fk_client_user`)

### Data Types
- Primary keys: `UUID`
- Timestamps: `TIMESTAMP` (UTC)
- Dates: `DATE`
- Enums: `VARCHAR(50)` stored as strings
- Money: `NUMERIC(10,2)` in BRL
- JSON: `JSONB` for flexible data

---

## 🔐 Security Considerations

- Passwords: BCrypt hashed, never stored in plain text
- Sensitive data: Encrypted at rest (future)
- Connection: TLS/SSL for production
- Credentials: Stored in `.env` files, never in code
- Audit logs: Immutable, retained for 1+ year

---

## 🚀 Getting Started

### Local Development Setup

1. **Start PostgreSQL with Docker:**
   ```bash
   docker-compose up -d postgres
   ```

2. **Database Configuration:**
   ```yaml
   # application.yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/electricity_bill_db
       username: ${DB_USER}
       password: ${DB_PASSWORD}
     jpa:
       hibernate:
         ddl-auto: validate  # Use Flyway for schema
       show-sql: true
   ```

3. **Run Migrations:**
   ```bash
   mvn flyway:migrate
   ```

---

## 📚 Related Documentation

- [Data Models](../scopo/07_data_models.md) - Complete entity specifications
- [Business Rules](../scopo/09_business_rules.md) - Validation rules
- [API Endpoints](../scopo/08_api_endpoints.md) - REST API specs

---

## 🧪 Testing

### Integration Tests with Testcontainers

```java
@DataJpaTest
@Testcontainers
class DatabaseIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = 
        new PostgreSQLContainer<>("postgres:15-alpine");
    
    // Tests...
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

