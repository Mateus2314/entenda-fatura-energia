# JPA Inheritance Strategy for User Hierarchy

## 1. Overview

This document outlines the research, analysis, and decision regarding the JPA inheritance strategy for implementing the user hierarchy in the **Understand Your Electricity Bill** project.

### User Hierarchy Structure

```
User (Abstract Base Entity)
├── Client (End consumer)
├── Consultant (Energy professional)
└── Admin (System administrator)
```

### Common Attributes (User)
- `id` (UUID) - Primary key
- `email` (String) - Unique email
- `password` (String) - Hashed password
- `name` (String) - Full name
- `phone` (String) - Phone number
- `createdAt` (Timestamp) - Creation date
- `updatedAt` (Timestamp) - Last update
- `status` (Enum) - Account status

### Specific Attributes

**Client:**
- `address` (String)
- `cpf` (String) - Brazilian individual tax ID
- `registrationDate` (Date)

**Consultant:**
- `company` (String)
- `cnpj` (String) - Brazilian company tax ID
- `registrationNumber` (String) - Professional license
- `address` (String) - Business address

**Admin:**
- `role` (Enum) - SUPER_ADMIN, ADMIN, MODERATOR
- `permissions` (JSON) - Detailed permissions

---

## 2. JPA Inheritance Strategies

### 2.1 SINGLE_TABLE Strategy

#### How it Works
All classes in the hierarchy (User, Client, Consultant, Admin) are stored in **one single table** with a discriminator column to identify the type.

#### Database Schema
```sql
CREATE TABLE users (
    id UUID PRIMARY KEY,
    dtype VARCHAR(31) NOT NULL,  -- Discriminator column
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    status VARCHAR(50),
    -- Client specific fields
    client_address VARCHAR(500),
    cpf VARCHAR(11),
    registration_date DATE,
    -- Consultant specific fields
    company VARCHAR(255),
    cnpj VARCHAR(14),
    registration_number VARCHAR(50),
    consultant_address VARCHAR(500),
    -- Admin specific fields
    role VARCHAR(50),
    permissions JSONB
);
```

#### Code Example
```java
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype", discriminatorType = DiscriminatorType.STRING)
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String name;
    
    private String phone;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    
    // Getters and setters
}

@Entity
@DiscriminatorValue("CLIENT")
public class Client extends User {
    @Column(name = "client_address", length = 500)
    private String address;
    
    @Column(length = 11, unique = true)
    private String cpf;
    
    @Column(name = "registration_date")
    private LocalDate registrationDate;
    
    // Getters and setters
}

@Entity
@DiscriminatorValue("CONSULTANT")
public class Consultant extends User {
    @Column(nullable = false)
    private String company;
    
    @Column(length = 14, unique = true)
    private String cnpj;
    
    @Column(name = "registration_number", length = 50)
    private String registrationNumber;
    
    @Column(name = "consultant_address", length = 500)
    private String address;
    
    // Getters and setters
}

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends User {
    @Enumerated(EnumType.STRING)
    private AdminRole role;
    
    @Column(columnDefinition = "JSONB")
    private String permissions;
    
    // Getters and setters
}
```

#### Pros
- ✅ **Best Performance**: No JOINs required for queries
- ✅ **Simple Queries**: Single table queries are straightforward
- ✅ **Easy to Implement**: Simplest inheritance strategy
- ✅ **Polymorphic Queries Fast**: Finding all users is a single table scan
- ✅ **No Foreign Keys**: Simpler database structure

#### Cons
- ❌ **Many NULL Columns**: Each row has NULL values for non-applicable fields
- ❌ **Not Normalized**: Violates database normalization principles
- ❌ **Difficult Constraints**: Cannot enforce NOT NULL on specific subclass fields at DB level
- ❌ **Table Grows Wide**: Adding new subclasses adds more columns
- ❌ **Data Integrity Issues**: Harder to ensure data consistency
- ❌ **Wasted Space**: NULLs consume storage

---

### 2.2 JOINED Strategy

#### How it Works
One table for the base entity (`users`) containing common fields, and separate tables for each subclass (`clients`, `consultants`, `admins`) containing specific fields. Foreign key relationships join the tables.

#### Database Schema
```sql
-- Base table with common attributes
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    status VARCHAR(50) NOT NULL
);

-- Client specific table
CREATE TABLE clients (
    user_id UUID PRIMARY KEY,
    address VARCHAR(500) NOT NULL,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    registration_date DATE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Consultant specific table
CREATE TABLE consultants (
    user_id UUID PRIMARY KEY,
    company VARCHAR(255) NOT NULL,
    cnpj VARCHAR(14) NOT NULL UNIQUE,
    registration_number VARCHAR(50),
    address VARCHAR(500) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Admin specific table
CREATE TABLE admins (
    user_id UUID PRIMARY KEY,
    role VARCHAR(50) NOT NULL,
    permissions JSONB,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Indexes for performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_clients_cpf ON clients(cpf);
CREATE INDEX idx_consultants_cnpj ON consultants(cnpj);
```

#### Code Example
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
        if (status == null) {
            status = UserStatus.PENDING_VERIFICATION;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and setters
}

@Entity
@Table(name = "clients")
@PrimaryKeyJoinColumn(name = "user_id")
public class Client extends User {
    @Column(nullable = false, length = 500)
    private String address;
    
    @Column(nullable = false, length = 11, unique = true)
    private String cpf;
    
    @Column(name = "registration_date", nullable = false)
    private LocalDate registrationDate;
    
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<BillData> bills = new ArrayList<>();
    
    @PrePersist
    protected void onClientCreate() {
        if (registrationDate == null) {
            registrationDate = LocalDate.now();
        }
    }
    
    // Getters and setters
}

@Entity
@Table(name = "consultants")
@PrimaryKeyJoinColumn(name = "user_id")
public class Consultant extends User {
    @Column(nullable = false, length = 255)
    private String company;
    
    @Column(nullable = false, length = 14, unique = true)
    private String cnpj;
    
    @Column(name = "registration_number", length = 50)
    private String registrationNumber;
    
    @Column(nullable = false, length = 500)
    private String address;
    
    @OneToMany(mappedBy = "consultant", cascade = CascadeType.ALL)
    private List<BillData> managedBills = new ArrayList<>();
    
    @ManyToMany
    @JoinTable(
        name = "client_consultant_relation",
        joinColumns = @JoinColumn(name = "consultant_id"),
        inverseJoinColumns = @JoinColumn(name = "client_id")
    )
    private Set<Client> clients = new HashSet<>();
    
    // Getters and setters
}

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

#### Pros
- ✅ **Fully Normalized**: No NULL columns, proper database design
- ✅ **Strong Constraints**: Can enforce NOT NULL, UNIQUE on subclass fields
- ✅ **Clean Separation**: Each entity type has its own table
- ✅ **Scalable**: Easy to add new subclasses without affecting existing tables
- ✅ **Data Integrity**: Database constraints work as expected
- ✅ **Storage Efficient**: No wasted space on NULL values
- ✅ **Clear Schema**: Easy to understand table structure

#### Cons
- ❌ **Performance Overhead**: Requires JOIN operations for queries
- ❌ **Complex Queries**: Fetching a specific subclass requires joining tables
- ❌ **More Tables**: More database objects to manage
- ❌ **Migration Complexity**: Schema changes require multiple table updates

---

### 2.3 TABLE_PER_CLASS Strategy

#### How it Works
Each concrete class has its **own complete table** with all attributes (both inherited and specific). No shared base table.

#### Database Schema
```sql
-- Client table (includes all User fields + Client specific)
CREATE TABLE clients (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    status VARCHAR(50),
    address VARCHAR(500) NOT NULL,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    registration_date DATE NOT NULL
);

-- Consultant table (includes all User fields + Consultant specific)
CREATE TABLE consultants (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    status VARCHAR(50),
    company VARCHAR(255) NOT NULL,
    cnpj VARCHAR(14) NOT NULL UNIQUE,
    registration_number VARCHAR(50),
    address VARCHAR(500) NOT NULL
);

-- Admin table (includes all User fields + Admin specific)
CREATE TABLE admins (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    status VARCHAR(50),
    role VARCHAR(50) NOT NULL,
    permissions JSONB
);
```

#### Code Example
```java
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String name;
    
    private String phone;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    
    // Getters and setters
}

@Entity
@Table(name = "clients")
public class Client extends User {
    @Column(nullable = false)
    private String address;
    
    @Column(nullable = false, unique = true)
    private String cpf;
    
    @Column(name = "registration_date")
    private LocalDate registrationDate;
    
    // Getters and setters
}

// Similar for Consultant and Admin
```

#### Pros
- ✅ **No NULL Columns**: Each table contains only relevant fields
- ✅ **Independent Tables**: Each entity type is completely separate
- ✅ **No Joins for Concrete Types**: Querying a specific type is simple
- ✅ **Strong Constraints**: Can enforce constraints at table level

#### Cons
- ❌ **Poor Polymorphic Query Performance**: Finding all users requires UNION of all tables
- ❌ **Column Duplication**: Common fields duplicated across all tables
- ❌ **Difficult Maintenance**: Changes to base class require updating all tables
- ❌ **Complex Schema Changes**: Hard to add/modify common attributes
- ❌ **No Shared Constraints**: Cannot enforce email uniqueness across all user types easily
- ❌ **More Code**: More complex repository implementations

---

## 3. Decision Matrix

### Evaluation Criteria

| Criteria | Weight | SINGLE_TABLE | JOINED | TABLE_PER_CLASS |
|----------|--------|--------------|--------|-----------------|
| **Performance** | 15% | ⭐⭐⭐⭐⭐ (5) | ⭐⭐⭐⭐ (4) | ⭐⭐ (2) |
| **Data Normalization** | 20% | ⭐⭐ (2) | ⭐⭐⭐⭐⭐ (5) | ⭐⭐⭐ (3) |
| **Data Integrity** | 20% | ⭐⭐ (2) | ⭐⭐⭐⭐⭐ (5) | ⭐⭐⭐⭐ (4) |
| **Maintainability** | 15% | ⭐⭐⭐ (3) | ⭐⭐⭐⭐ (4) | ⭐⭐ (2) |
| **Scalability** | 15% | ⭐⭐ (2) | ⭐⭐⭐⭐⭐ (5) | ⭐⭐ (2) |
| **Implementation Ease** | 10% | ⭐⭐⭐⭐⭐ (5) | ⭐⭐⭐ (3) | ⭐⭐⭐ (3) |
| **Query Complexity** | 5% | ⭐⭐⭐⭐⭐ (5) | ⭐⭐⭐ (3) | ⭐⭐ (2) |

### Weighted Scores

**SINGLE_TABLE:**
- (5 × 0.15) + (2 × 0.20) + (2 × 0.20) + (3 × 0.15) + (2 × 0.15) + (5 × 0.10) + (5 × 0.05)
- = 0.75 + 0.40 + 0.40 + 0.45 + 0.30 + 0.50 + 0.25
- = **3.05 / 5.0**

**JOINED:**
- (4 × 0.15) + (5 × 0.20) + (5 × 0.20) + (4 × 0.15) + (5 × 0.15) + (3 × 0.10) + (3 × 0.05)
- = 0.60 + 1.00 + 1.00 + 0.60 + 0.75 + 0.30 + 0.15
- = **4.40 / 5.0** ✅

**TABLE_PER_CLASS:**
- (2 × 0.15) + (3 × 0.20) + (4 × 0.20) + (2 × 0.15) + (2 × 0.15) + (3 × 0.10) + (2 × 0.05)
- = 0.30 + 0.60 + 0.80 + 0.30 + 0.30 + 0.30 + 0.10
- = **2.70 / 5.0**

---

## 4. Chosen Strategy: JOINED

### 4.1 Justification

After thorough analysis, **JOINED** is the best strategy for this project for the following reasons:

#### 1. **Data Normalization is Critical**
- Our domain has well-defined specific attributes per user type
- Client has `cpf`, Consultant has `cnpj`, Admin has `permissions`
- These should not be nullable in a denormalized table
- **Weight: 20%** - High priority for data quality

#### 2. **Data Integrity Requirements**
- Need to enforce NOT NULL constraints on subclass-specific fields
- Need UNIQUE constraints on `cpf` (Client) and `cnpj` (Consultant)
- Database-level constraints ensure data quality
- **Weight: 20%** - Critical for business rules

#### 3. **Future Scalability**
- Project may add new user types in the future (e.g., Enterprise, Partner)
- JOINED strategy allows adding new subclass tables without affecting existing ones
- No need to add columns to a bloated single table
- **Weight: 15%** - Important for long-term maintenance

#### 4. **Performance is Acceptable**
- While JOINED requires JOIN operations, the performance impact is minimal for our use case:
  - Most queries will target specific user types (e.g., find Client by CPF)
  - Polymorphic queries (all users) are infrequent in our application
  - Modern databases optimize JOINs efficiently
  - We can add indexes on foreign keys
- **Weight: 15%** - Performance difference is acceptable

#### 5. **Clean Architecture**
- Each entity has a clear, separate table
- Easy to understand database schema
- Aligns with object-oriented design principles
- **Weight: 15%** - Improves code maintainability

#### 6. **Storage Efficiency**
- No wasted space on NULL values
- Each table only stores relevant data
- Smaller table sizes improve cache efficiency

### 4.2 Trade-offs Accepted

**Performance Impact:**
- Queries requiring user polymorphism need JOINs
- Acceptable because:
  - Most queries are type-specific
  - JOIN overhead is minimal with proper indexing
  - Database optimization handles this well

**Schema Complexity:**
- More tables to manage
- Acceptable because:
  - Clear separation of concerns
  - Easier to understand than SINGLE_TABLE with many NULLs
  - Flyway migrations handle schema changes

---

## 5. Implementation Guidelines

### 5.1 Repository Layer

```java
// Base repository
public interface UserRepository<T extends User> extends JpaRepository<T, UUID> {
    Optional<T> findByEmail(String email);
    boolean existsByEmail(String email);
}

// Specific repositories
@Repository
public interface ClientRepository extends UserRepository<Client> {
    Optional<Client> findByCpf(String cpf);
    boolean existsByCpf(String cpf);
}

@Repository
public interface ConsultantRepository extends UserRepository<Consultant> {
    Optional<Consultant> findByCnpj(String cnpj);
    boolean existsByCnpj(String cnpj);
}

@Repository
public interface AdminRepository extends UserRepository<Admin> {
    List<Admin> findByRole(AdminRole role);
}
```

### 5.2 Service Layer Pattern

```java
@Service
public class ClientService {
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    
    public Client createClient(ClientRegistrationDTO dto) {
        // Validate CPF uniqueness
        if (clientRepository.existsByCpf(dto.getCpf())) {
            throw new DuplicateCpfException("CPF already registered");
        }
        
        // Validate email uniqueness
        if (clientRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateEmailException("Email already registered");
        }
        
        Client client = new Client();
        client.setEmail(dto.getEmail());
        client.setPassword(passwordEncoder.encode(dto.getPassword()));
        client.setName(dto.getName());
        client.setPhone(dto.getPhone());
        client.setAddress(dto.getAddress());
        client.setCpf(dto.getCpf());
        client.setStatus(UserStatus.PENDING_VERIFICATION);
        
        return clientRepository.save(client);
    }
}
```

### 5.3 Query Optimization

```java
// Efficient query for specific type
@Query("SELECT c FROM Client c WHERE c.status = :status")
List<Client> findActiveClients(@Param("status") UserStatus status);

// Polymorphic query (use sparingly)
@Query("SELECT u FROM User u WHERE u.email = :email")
Optional<User> findUserByEmail(@Param("email") String email);

// Fetch with JOIN FETCH for relationships
@Query("SELECT c FROM Client c LEFT JOIN FETCH c.bills WHERE c.id = :id")
Optional<Client> findClientWithBills(@Param("id") UUID id);
```

### 5.4 Migration Strategy (Flyway)

```sql
-- V1__create_user_tables.sql

-- Create base users table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING_VERIFICATION',
    CONSTRAINT chk_email_format CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}$')
);

-- Create clients table
CREATE TABLE clients (
    user_id UUID PRIMARY KEY,
    address VARCHAR(500) NOT NULL,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    registration_date DATE NOT NULL DEFAULT CURRENT_DATE,
    CONSTRAINT fk_client_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_cpf_format CHECK (cpf ~ '^\d{11}$')
);

-- Create consultants table
CREATE TABLE consultants (
    user_id UUID PRIMARY KEY,
    company VARCHAR(255) NOT NULL,
    cnpj VARCHAR(14) NOT NULL UNIQUE,
    registration_number VARCHAR(50),
    address VARCHAR(500) NOT NULL,
    CONSTRAINT fk_consultant_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_cnpj_format CHECK (cnpj ~ '^\d{14}$')
);

-- Create admins table
CREATE TABLE admins (
    user_id UUID PRIMARY KEY,
    role VARCHAR(50) NOT NULL,
    permissions JSONB,
    CONSTRAINT fk_admin_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_admin_role CHECK (role IN ('SUPER_ADMIN', 'ADMIN', 'MODERATOR'))
);

-- Create indexes for performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_created_at ON users(created_at);
CREATE INDEX idx_clients_cpf ON clients(cpf);
CREATE INDEX idx_clients_registration_date ON clients(registration_date);
CREATE INDEX idx_consultants_cnpj ON consultants(cnpj);
CREATE INDEX idx_consultants_company ON consultants(company);
CREATE INDEX idx_admins_role ON admins(role);

-- Create updated_at trigger
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
```

### 5.5 Testing Considerations

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class ClientRepositoryTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");
    
    @Autowired
    private ClientRepository clientRepository;
    
    @Test
    @DisplayName("Should save and retrieve client with all attributes")
    void testSaveAndRetrieveClient() {
        // Given
        Client client = new Client();
        client.setEmail("test@example.com");
        client.setPassword("hashedPassword123");
        client.setName("João Silva");
        client.setPhone("+5511999999999");
        client.setAddress("Rua Exemplo, 123, São Paulo - SP");
        client.setCpf("12345678901");
        client.setStatus(UserStatus.ACTIVE);
        
        // When
        Client saved = clientRepository.save(client);
        Optional<Client> retrieved = clientRepository.findById(saved.getId());
        
        // Then
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getEmail()).isEqualTo("test@example.com");
        assertThat(retrieved.get().getCpf()).isEqualTo("12345678901");
        assertThat(retrieved.get().getRegistrationDate()).isNotNull();
    }
    
    @Test
    @DisplayName("Should enforce CPF uniqueness constraint")
    void testCpfUniquenessConstraint() {
        // Given
        Client client1 = createClient("user1@example.com", "12345678901");
        clientRepository.save(client1);
        
        Client client2 = createClient("user2@example.com", "12345678901");
        
        // When & Then
        assertThrows(DataIntegrityViolationException.class, () -> {
            clientRepository.save(client2);
            clientRepository.flush();
        });
    }
    
    private Client createClient(String email, String cpf) {
        Client client = new Client();
        client.setEmail(email);
        client.setPassword("hashedPassword");
        client.setName("Test User");
        client.setPhone("+5511999999999");
        client.setAddress("Test Address");
        client.setCpf(cpf);
        return client;
    }
}
```

---

## 6. Performance Considerations

### 6.1 Expected Query Patterns

**High Frequency (90% of queries):**
- Find specific user type by ID
- Find Client by CPF
- Find Consultant by CNPJ
- Find User by email (authentication)
- List clients for a consultant

**Low Frequency (10% of queries):**
- Find any user by email (polymorphic)
- Count all users
- List all users (admin dashboard)

### 6.2 Optimization Strategies

1. **Indexes on Foreign Keys:**
   - Automatically created on `user_id` in subclass tables
   - Additional indexes on frequently queried fields (email, cpf, cnpj)

2. **Fetch Strategies:**
   - Use `@ManyToOne(fetch = FetchType.LAZY)` by default
   - Explicit `JOIN FETCH` only when needed
   - DTO projections for list views

3. **Caching:**
   - Second-level cache for User entities (Spring Cache + Caffeine)
   - Cache user lookups by email (authentication)

4. **Connection Pooling:**
   - HikariCP (default in Spring Boot)
   - Proper pool sizing based on load

---

## 7. References

### Official Documentation
- [Spring Data JPA - Inheritance](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.entity-persistence.inheritance)
- [Jakarta Persistence 3.0 Specification](https://jakarta.ee/specifications/persistence/3.0/jakarta-persistence-spec-3.0.html#a121)
- [Hibernate ORM - Inheritance Mapping](https://docs.jboss.org/hibernate/orm/6.2/userguide/html_single/Hibernate_User_Guide.html#entity-inheritance)

### Articles and Tutorials
- [Baeldung - JPA Inheritance Strategies](https://www.baeldung.com/hibernate-inheritance)
- [Vlad Mihalcea - Best Way to Use Hibernate Inheritance](https://vladmihalcea.com/the-best-way-to-use-hibernate-inheritance/)
- [Thorben Janssen - JPA Inheritance Strategies Explained](https://thorben-janssen.com/complete-guide-inheritance-strategies-jpa-hibernate/)

### Performance Analysis
- [JPA Inheritance Performance Comparison](https://vladmihalcea.com/jpa-inheritance-performance/)
- [Hibernate Performance Tuning](https://vladmihalcea.com/tutorials/hibernate/)

---

## 8. Conclusion

The **JOINED inheritance strategy** is the optimal choice for our user hierarchy implementation. It provides:

✅ **Data Integrity** through proper database constraints  
✅ **Normalization** with no wasted NULL columns  
✅ **Scalability** for adding new user types  
✅ **Maintainability** with clear table separation  
✅ **Acceptable Performance** with proper indexing  

The minor performance overhead of JOIN operations is far outweighed by the benefits of data quality, integrity, and long-term maintainability.

---

**Decision Approved:** JOINED Strategy  
**Date:** December 2024  
**Reviewed By:** Development Team  
**Status:** ✅ Approved for Implementation

