# JPA Inheritance Strategy - JOINED

**Decision:** JOINED strategy for User hierarchy  
**Reason:** Normalized, strong constraints, scalable

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

## JPA Entities

### Base Entity

```java
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(nullable = false, unique = true, length = 255)
    private String email;
    
    @Column(nullable = false, length = 255)
    private String password;
    
    @Column(nullable = false, length = 255)
    private String name;
    
    @Column(length = 50)
    private String phone;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private UserStatus status;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = UserStatus.PENDING_VERIFICATION;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and setters
}
```

### Client Entity

```java
@Entity
@Table(name = "clients")
@PrimaryKeyJoinColumn(name = "user_id")
public class Client extends User {
    @Column(nullable = false, length = 500)
    private String address;
    
    @Column(length = 100)
    private String city;
    
    @Column(length = 2)
    private String state;
    
    @Column(name = "zip_code", length = 10)
    private String zipCode;
    
    @Column(nullable = false, length = 11, unique = true)
    private String cpf;
    
    @Column(name = "registration_date", nullable = false)
    private LocalDate registrationDate;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<ElectricityBill> bills = new ArrayList<>();
    
    @PrePersist
    protected void onClientCreate() {
        if (registrationDate == null) registrationDate = LocalDate.now();
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onClientUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and setters
}
```

### Consultant Entity

```java
@Entity
@Table(name = "consultants")
@PrimaryKeyJoinColumn(name = "user_id")
public class Consultant extends User {
    @Column(name = "consultant_name", nullable = false, length = 255)
    private String consultantName;
    
    @Column(nullable = false, length = 255)
    private String company;
    
    @Column(nullable = false, length = 14, unique = true)
    private String cnpj;
    
    @Column(name = "registration_number", length = 50)
    private String registrationNumber;
    
    @Column(nullable = false, length = 500)
    private String address;
    
    @Column(length = 100)
    private String city;
    
    @Column(length = 2)
    private String state;
    
    @Column(name = "zip_code", length = 10)
    private String zipCode;
    
    @Column(name = "company_logo", columnDefinition = "TEXT")
    private String companyLogo;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "consultant", cascade = CascadeType.ALL)
    private List<ElectricityBill> managedBills = new ArrayList<>();
    
    @PrePersist
    protected void onConsultantCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onConsultantUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and setters
}
```

### Admin Entity

```java
@Entity
@Table(name = "admins")
@PrimaryKeyJoinColumn(name = "user_id")
public class Admin extends User {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AdminRole role;
    
    @Column(columnDefinition = "JSONB")
    private String permissions;
    
    // Getters and setters
}
```

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

