# JPA Entity Modeling Best Practices

**Project:** Understand Your Electricity Bill  
**Strategy:** JOINED Inheritance  
**Date:** 2025-12-16

---

## Research Summary

### 1. @MappedSuperclass vs @Entity for Superclass

#### @MappedSuperclass
- **Not** a JPA entity
- No database table
- Fields copied to subclass tables
- Cannot be used in JPQL queries
- Cannot have relationships pointing to it

**Use Case:** Abstract base classes with common fields (id, timestamps) shared across unrelated entities.

```java
@MappedSuperclass
public abstract class BaseEntity {
    @Id
    @GeneratedValue
    private UUID id;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

#### @Entity on Superclass
- Full JPA entity
- Requires `@Inheritance` strategy
- Can be queried directly
- Supports polymorphic queries
- Allows relationships to base type

**Use Case:** True inheritance hierarchies with polymorphic behavior.

```java
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {
    @Id
    private UUID id;
    // ... fields
}
```

**Decision for this project:** `@Entity` on User superclass because:
- Need polymorphic queries (`findAll()` returns all user types)
- Authentication works against User base
- Relationships point to User (e.g., bills owned by User)

---

### 2. @DiscriminatorColumn and @DiscriminatorValue

#### When to Use
Required for **SINGLE_TABLE** and **TABLE_PER_CLASS** strategies.  
Optional for **JOINED** (implicit via table structure).

#### Our Implementation (JOINED)

**Database:** Already has `user_type` ENUM in users table.

**JPA Approach:** Not needed for JOINED but can add for clarity:

```java
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
public abstract class User {
    // ...
}

@Entity
@Table(name = "clients")
@PrimaryKeyJoinColumn(name = "user_id")
@DiscriminatorValue("CLIENT")
public class Client extends User {
    // ...
}
```

**Note:** With JOINED strategy, Hibernate can infer type from joined table. Discriminator is optional but adds:
- Explicit type indication
- Faster polymorphic queries (no JOIN needed to determine type)
- Consistency with database design

**Decision:** Include discriminator for performance and clarity.

---

### 3. Relationships: @OneToMany and @ManyToMany

#### @OneToMany Best Practices

**1. Use `mappedBy` on the "one" side (owner):**
```java
@Entity
public class Client extends User {
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ElectricityBill> bills = new ArrayList<>();
}
```

**2. Foreign key on the "many" side:**
```java
@Entity
public class ElectricityBill {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
}
```

**Guidelines:**
- Always use `FetchType.LAZY` (default for @OneToMany)
- Consider `orphanRemoval = true` if child cannot exist without parent
- Use `CascadeType.ALL` carefully (usually only for aggregates)
- Initialize collections to avoid NPE: `= new ArrayList<>()`

#### @ManyToMany Best Practices

**Example:** Consultants ↔ Clients

```java
@Entity
public class Consultant extends User {
    @ManyToMany
    @JoinTable(
        name = "consultant_clients",
        joinColumns = @JoinColumn(name = "consultant_id"),
        inverseJoinColumns = @JoinColumn(name = "client_id")
    )
    private Set<Client> managedClients = new HashSet<>();
}

@Entity
public class Client extends User {
    @ManyToMany(mappedBy = "managedClients")
    private Set<Consultant> consultants = new HashSet<>();
}
```

**Guidelines:**
- Use `Set` instead of `List` (avoids duplicates, better performance)
- Define `@JoinTable` on the owning side
- Use `mappedBy` on inverse side
- Avoid `CascadeType.REMOVE` (would delete both sides!)
- Consider adding helper methods for bidirectional consistency

---

## Applied Design Decisions

### User Hierarchy (JOINED Strategy)

```
┌─────────────┐
│    User     │ (Abstract @Entity)
│   (base)    │
└──────┬──────┘
       │
   ┌───┴───┬────────┬─────────┐
   │       │        │         │
┌──▼───┐ ┌─▼──────┐ ┌─▼─────┐
│Client│ │Consult│ │ Admin │
└──────┘ └────────┘ └───────┘
```

**Tables:**
- `users` - Base fields (email, password, name, phone, status, timestamps)
- `clients` - CPF, address, registration_date
- `consultants` - CNPJ, company, consultant_name, address, company_logo
- `admins` - role, permissions (JSONB)

**Join Column:** `user_id` (PK in child tables, FK to users.id)

---

## Entity Implementation Guidelines

### 1. User Base Entity

```java
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
public abstract class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(nullable = false, unique = true, length = 255)
    private String email;
    
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;
    
    @Column(nullable = false, length = 255)
    private String name;
    
    @Column(length = 20)
    private String phone;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private UserStatus status;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters/Setters
}
```

**Key Points:**
- Abstract class (cannot instantiate directly)
- Lifecycle callbacks for timestamps
- ENUMs for type safety
- Column names match database (snake_case)

---

### 2. Client Entity

```java
@Entity
@Table(name = "clients")
@PrimaryKeyJoinColumn(name = "user_id")
@DiscriminatorValue("CLIENT")
public class Client extends User {
    
    @Column(nullable = false, length = 500)
    private String address;
    
    @Column(length = 100)
    private String city;
    
    @Column(length = 2)
    private String state;
    
    @Column(name = "zip_code", length = 10)
    private String zipCode;
    
    @Column(nullable = false, unique = true, length = 11)
    private String cpf;
    
    @Column(name = "registration_date", nullable = false)
    private LocalDate registrationDate;
    
    // Relationships
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ElectricityBill> bills = new ArrayList<>();
    
    @ManyToMany(mappedBy = "managedClients")
    private Set<Consultant> consultants = new HashSet<>();
    
    // Lifecycle
    @PrePersist
    protected void onClientCreate() {
        if (registrationDate == null) {
            registrationDate = LocalDate.now();
        }
    }
    
    // Getters/Setters
}
```

---

### 3. Consultant Entity

```java
@Entity
@Table(name = "consultants")
@PrimaryKeyJoinColumn(name = "user_id")
@DiscriminatorValue("CONSULTANT")
public class Consultant extends User {
    
    @Column(name = "consultant_name", nullable = false, length = 255)
    private String consultantName;
    
    @Column(nullable = false, length = 255)
    private String company;
    
    @Column(nullable = false, unique = true, length = 14)
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
    
    @Column(name = "company_logo")
    @Lob
    private String companyLogo; // Base64 or URL
    
    @Column(name = "registration_date", nullable = false)
    private LocalDate registrationDate;
    
    // Relationships
    @ManyToMany
    @JoinTable(
        name = "consultant_clients",
        joinColumns = @JoinColumn(name = "consultant_id"),
        inverseJoinColumns = @JoinColumn(name = "client_id")
    )
    private Set<Client> managedClients = new HashSet<>();
    
    // Helper methods for bidirectional consistency
    public void addClient(Client client) {
        managedClients.add(client);
        client.getConsultants().add(this);
    }
    
    public void removeClient(Client client) {
        managedClients.remove(client);
        client.getConsultants().remove(this);
    }
    
    // Getters/Setters
}
```

---

### 4. Admin Entity

```java
@Entity
@Table(name = "admins")
@PrimaryKeyJoinColumn(name = "user_id")
@DiscriminatorValue("ADMIN")
public class Admin extends User {
    
    @Column(nullable = false, length = 50)
    private String role;
    
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> permissions = new HashMap<>();
    
    // Getters/Setters
}
```

**Note:** For JSONB in PostgreSQL, use Hibernate Types library:
```xml
<dependency>
    <groupId>com.vladmihalcea</groupId>
    <artifactId>hibernate-types-52</artifactId>
    <version>2.21.1</version>
</dependency>
```

---

## Repository Pattern

### Base Repository
```java
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    List<User> findByStatus(UserStatus status);
}
```

### Type-Specific Repositories
```java
@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {
    Optional<Client> findByCpf(String cpf);
    List<Client> findByState(String state);
}

@Repository
public interface ConsultantRepository extends JpaRepository<Consultant, UUID> {
    Optional<Consultant> findByCnpj(String cnpj);
}

@Repository
public interface AdminRepository extends JpaRepository<Admin, UUID> {
    List<Admin> findByRole(String role);
}
```

---

## Query Examples

### Polymorphic Query (all user types)
```java
List<User> allUsers = userRepository.findAll();
// Returns: Client + Consultant + Admin instances
```

### Type-Specific Query
```java
List<Client> clients = clientRepository.findAll();
// Returns: Only Client instances
```

### JPQL with Inheritance
```java
@Query("SELECT u FROM User u WHERE u.status = :status")
List<User> findByStatus(@Param("status") UserStatus status);
// Works across all subclasses
```

### Join Fetch for Performance
```java
@Query("SELECT c FROM Client c LEFT JOIN FETCH c.bills WHERE c.id = :id")
Optional<Client> findByIdWithBills(@Param("id") UUID id);
```

---

## Testing Strategy

### 1. Entity Tests
```java
@DataJpaTest
class ClientEntityTest {
    
    @Test
    void shouldInheritUserFields() {
        Client client = new Client();
        client.setEmail("test@example.com");
        client.setCpf("12345678901");
        // Assert inheritance works
    }
    
    @Test
    void shouldCascadeToElectricityBills() {
        // Test cascade operations
    }
}
```

### 2. Repository Tests
```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Testcontainers
class ClientRepositoryTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");
    
    @Test
    void shouldFindByCpf() {
        // Integration test with real PostgreSQL
    }
}
```

---

## Common Pitfalls & Solutions

### 1. N+1 Query Problem
**Problem:** Loading users + bills causes N queries
```java
List<Client> clients = clientRepository.findAll();
clients.forEach(c -> c.getBills().size()); // N+1!
```

**Solution:** Use JOIN FETCH
```java
@Query("SELECT c FROM Client c LEFT JOIN FETCH c.bills")
List<Client> findAllWithBills();
```

### 2. LazyInitializationException
**Problem:** Accessing lazy relationship outside transaction
**Solution:** 
- Use `@Transactional` on service methods
- Use JOIN FETCH in query
- Use DTOs with only needed data

### 3. Cascading Deletes
**Problem:** Deleting user should delete child data
**Solution:** Database handles it with `ON DELETE CASCADE`
JPA cascade is optional but adds safety:
```java
@OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
```

---

## References

### Official Documentation
- [Jakarta Persistence Specification](https://jakarta.ee/specifications/persistence/3.1/)
- [Hibernate ORM Documentation](https://hibernate.org/orm/documentation/)
- [Spring Data JPA Reference](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)

### Best Practices
- [Baeldung - JPA Entity Inheritance](https://www.baeldung.com/hibernate-inheritance)
- [Vlad Mihalcea - Hibernate Best Practices](https://vladmihalcea.com/tutorials/hibernate/)
- [Thorben Janssen - JPA Tips](https://thorben-janssen.com/tips/)

### Books
- *Java Persistence with Hibernate* (2nd Edition) - Bauer, King, Gregory
- *Pro JPA 2* - Keith, Schincariol

---

**Last Updated:** 2025-12-16  
**Status:** Implementation Ready ✅

