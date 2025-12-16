# JPA Inheritance Strategy - JOINED

**Decision:** JOINED strategy for User hierarchy  
**Reason:** Normalized, strong constraints, scalable

> 📖 **For implementation details and code examples:** See [jpa-entity-modeling.md](jpa-entity-modeling.md)

---

## User Hierarchy

```
User (Abstract Base)
├── Client (CPF - individual)
├── Consultant (CNPJ - company)
└── Admin
```

---

## JOINED Strategy

### How It Works
- One table for base entity (`users`)
- Separate tables for each subclass (`clients`, `consultants`, `admins`)
- Foreign key joins for complete data

### Why JOINED?
✅ Fully normalized (no NULL columns)  
✅ Strong constraints at DB level  
✅ Clean separation of concerns  
✅ Easy to extend

---

## Database Schema

```sql
-- Base table
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    status VARCHAR(50) NOT NULL
);

-- Client (individual - CPF)
CREATE TABLE clients (
    user_id UUID PRIMARY KEY,
    address VARCHAR(500) NOT NULL,
    city VARCHAR(100),
    state VARCHAR(2),
    zip_code VARCHAR(10),
    cpf VARCHAR(11) NOT NULL UNIQUE,
    registration_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Consultant (company - CNPJ)
CREATE TABLE consultants (
    user_id UUID PRIMARY KEY,
    consultant_name VARCHAR(255) NOT NULL,
    company VARCHAR(255) NOT NULL,
    cnpj VARCHAR(14) NOT NULL UNIQUE,
    registration_number VARCHAR(50),
    address VARCHAR(500) NOT NULL,
    city VARCHAR(100),
    state VARCHAR(2),
    zip_code VARCHAR(10),
    company_logo TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Admin
CREATE TABLE admins (
    user_id UUID PRIMARY KEY,
    role VARCHAR(50) NOT NULL,
    permissions JSONB,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

---

## JPA Implementation Quick Reference

### Base Entity Pattern

```java
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type")
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    // ... other common fields
}
```

### Subclass Pattern

```java
@Entity
@Table(name = "clients")
@PrimaryKeyJoinColumn(name = "user_id")
@DiscriminatorValue("CLIENT")
public class Client extends User {
    @Column(nullable = false, unique = true, length = 11)
    private String cpf;
    
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<ElectricityBill> bills;
    
    // ... other client-specific fields
}
```

> 📖 **Complete entity implementations, relationships, and best practices:**  
> See [jpa-entity-modeling.md](jpa-entity-modeling.md)

---

## Repositories

```java
@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {
    Optional<Client> findByCpf(String cpf);
    List<Client> findByStatus(UserStatus status);
}

@Repository
public interface ConsultantRepository extends JpaRepository<Consultant, UUID> {
    Optional<Consultant> findByCnpj(String cnpj);
    List<Consultant> findByCompanyContaining(String company);
}

@Repository
public interface AdminRepository extends JpaRepository<Admin, UUID> {
    List<Admin> findByRole(AdminRole role);
}
```

---

## Key Points

**Client vs Consultant:**
- Client: CPF (individual), owns bills
- Consultant: CNPJ (company), manages bills, has company_logo
- Both: Complete address fields (address, city, state, zip_code)

---

## Database Migrations

- **V001:** users table → [Migration Details](migrations/V001.md)
- **V002:** clients table → [Migration Details](migrations/V002.md)
- **V003:** consultants table → [Migration Details](migrations/V003.md)
- **V004:** admins table → [Migration Details](migrations/V004.md)

---

**Last Updated:** 2025-12-16  
**Status:** ✅ Implemented

**Relationships:**
- Client → ElectricityBills (1:N)
- Consultant → ElectricityBills (1:N)
- ElectricityBills → Tariffs (N:1, required)

**Indexes:**
```sql
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_clients_cpf ON clients(cpf);
CREATE INDEX idx_consultants_cnpj ON consultants(cnpj);
```

---

**See:** [ER_DIAGRAM.md](./ER_DIAGRAM.md) for visual reference

