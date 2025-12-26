# JPA Entity Modeling Best Practices

**Project:** Understand Your Electricity Bill  
**Strategy:** JOINED Inheritance + External API Integration (ANEEL)  
**Date:** 2025-12-26

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

## 5. External API Integration - ANEEL Tariffs ⭐ NEW

### Strategy: Snapshot Pattern

When integrating external APIs (like ANEEL Open Data), we don't query the API in real-time for each request. Instead, we **synchronize and store snapshots** in our database.

#### Why Snapshot Pattern?

| Aspect | Real-Time API | Snapshot Pattern |
|--------|---------------|------------------|
| **Performance** | ❌ Slow (network calls) | ✅ Fast (local DB) |
| **Availability** | ❌ Depends on external service | ✅ Independent |
| **Historical Data** | ❌ Lost if API changes | ✅ Preserved |
| **Query Complexity** | ❌ Limited by API | ✅ Full SQL/JPQL power |
| **Cost** | ❌ Rate limits | ✅ No external calls |

### Entity Design: Tariff (ANEEL API)

**Integrates TWO ANEEL APIs:**
1. **Tariffs API** - Resource: `fcf2906c-7c32-4b9b-a637-054e7a5234f4` (17 fields)
2. **Flags API** - Resource: `0591b8f6-fe54-437b-b72b-1aa2efd46e42` (4 fields) ⭐ NEW

```java
@Entity
@Table(name = "tariffs", indexes = {
    @Index(name = "idx_tariff_search", 
           columnList = "distributor, subgroup, tariff_modality, valid_from, valid_until"),
    @Index(name = "idx_tariff_cnpj", columnList = "cnpj_distributor"),
    @Index(name = "idx_tariff_validity", columnList = "valid_from, valid_until")
})
public class Tariff {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    // API Fields - Direct Mapping from ANEEL
    @Column(name = "generation_date", nullable = false)
    private LocalDate generationDate; // DatGeracaoConjuntoDados
    
    @Column(name = "description_reh", length = 500)
    private String descriptionReh; // DscREH
    
    @Column(name = "distributor", nullable = false, length = 100)
    private String distributor; // SigAgente (e.g., "CPFL JAGUARI")
    
    @Column(name = "cnpj_distributor", nullable = false, length = 14)
    private String cnpjDistributor; // NumCNPJDistribuidora
    
    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom; // DatInicioVigencia
    
    @Column(name = "valid_until")
    private LocalDate validUntil; // DatFimVigencia (nullable for current tariffs)
    
    @Column(name = "tariff_base_desc", length = 100)
    private String tariffBaseDesc; // DscBaseTarifaria
    
    @Column(name = "subgroup", length = 10)
    private String subgroup; // DscSubGrupo (A2, B1, etc.)
    
    @Column(name = "tariff_modality", length = 50)
    private String tariffModality; // DscModalidadeTarifaria (Azul, Verde, Convencional)
    
    @Column(name = "consumer_class", length = 100)
    private String consumerClass; // DscClasse (Residencial, Industrial, Comercial)
    
    @Column(name = "consumer_subclass", length = 100)
    private String consumerSubclass; // DscSubClasse
    
    @Column(name = "detail", length = 100)
    private String detail; // DscDetalhe (APE, etc.)
    
    @Column(name = "tariff_post_name", length = 50)
    private String tariffPostName; // NomPostoTarifario (Ponta, Fora ponta)
    
    @Column(name = "tertiary_unit", length = 10)
    private String tertiaryUnit; // DscUnidadeTerciaria (kW, kWh)
    
    @Column(name = "accessing_agent", length = 100)
    private String accessingAgent; // SigAgenteAcessante
    
    @Column(name = "tusd_value", nullable = false, precision = 10, scale = 4)
    private BigDecimal tusdValue; // VlrTUSD (Tarifa de Uso do Sistema de Distribuição)
    
    @Column(name = "te_value", nullable = false, precision = 10, scale = 4)
    private BigDecimal teValue; // VlrTE (Tarifa de Energia)
    
    // Tariff Flags (Bandeiras Tarifárias) - API 2 ⭐ NEW
    @Column(name = "flag_generation_date")
    private LocalDate flagGenerationDate; // DatGeracaoConjuntoDados (from flags API)
    
    @Column(name = "competence_date")
    private LocalDate competenceDate; // DatCompetencia (month of flag validity)
    
    @Column(name = "activated_flag_name", length = 50)
    private String activatedFlagName; // NomBandeiraAcionada (Verde, Amarela, Vermelha P1/P2)
    
    @Column(name = "flag_additional_value", precision = 10, scale = 4)
    private BigDecimal flagAdditionalValue; // VlrAdicionalBandeira (additional per 100 kWh)
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relationships
    @OneToMany(mappedBy = "tariff", fetch = FetchType.LAZY)
    private List<ElectricityBill> electricityBills = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

### Key Design Decisions

#### 1. Use String Instead of Enum for Dynamic Fields

**Why?**
- API values can change (new distributors, new modalities)
- ENUMs require code changes and redeployment
- Strings are flexible and future-proof

```java
// ❌ DON'T - Rigid, requires code changes
public enum Distributor {
    CPFL_JAGUARI, CEMIG, LIGHT // What if ANEEL adds new ones?
}

// ✅ DO - Flexible, handles any value
@Column(name = "distributor", length = 100)
private String distributor; // Can handle any value from API
```

#### 2. Handle API Value Format Differences

**Problem:** API returns decimal values with comma: `"1,85"`  
**Solution:** Custom parsing in DTO mapper

```java
@Service
public class AneelTariffMapper {
    
    public Tariff toEntity(AneelTariffDTO dto) {
        Tariff tariff = new Tariff();
        
        // Parse comma-separated decimals
        tariff.setTusdValue(parseDecimal(dto.getVlrTUSD()));
        tariff.setTeValue(parseDecimal(dto.getVlrTE()));
        
        return tariff;
    }
    
    private BigDecimal parseDecimal(String value) {
        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        // Replace comma with dot: "1,85" -> "1.85"
        String normalized = value.replace(",", ".");
        return new BigDecimal(normalized);
    }
}
```

#### 3. Nullable vs Not Null Fields

**Required (NOT NULL):**
- Primary identifiers: `distributor`, `cnpj_distributor`
- Validity period start: `valid_from`
- Cost values: `tusd_value`, `te_value`
- Metadata: `generation_date`, `created_at`

**Optional (NULLABLE):**
- End date: `valid_until` (current tariffs have no end date)
- Descriptive fields: `description_reh`, `detail`, etc.
- Classification fields: `consumer_class`, `subgroup` (can be "Não se aplica")

#### 4. Relationship with ElectricityBill

**Design:** Each bill has exactly ONE tariff (snapshot at creation time)

```java
@Entity
public class ElectricityBill {
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tariff_id", nullable = false)
    private Tariff tariff; // Snapshot - immutable reference
    
    // ❌ DON'T store redundant field
    // private String tariffModality; // Already in tariff.tariffModality
    
    // Query tariff data via relationship
    public String getTariffModality() {
        return tariff.getTariffModality();
    }
}
```

#### 5. Tariff Flags (Bandeiras Tarifárias) Integration ⭐ NEW

**Two APIs Combined:**
The Tariff entity integrates data from TWO ANEEL APIs:
1. **Base Tariffs** (17 fields) - Resource ID: `fcf2906c-7c32-4b9b-a637-054e7a5234f4`
2. **Tariff Flags** (4 fields) - Resource ID: `0591b8f6-fe54-437b-b72b-1aa2efd46e42`

**What are Tariff Flags?**
- Monthly additional charges based on energy generation conditions (mainly hydric scarcity)
- **Verde (Green)**: R$ 0,00 - Favorable conditions
- **Amarela (Yellow)**: ~R$ 18,00 per 100 kWh - Less favorable
- **Vermelha P1 (Red P1)**: ~R$ 30,00 per 100 kWh - Unfavorable
- **Vermelha P2 (Red P2)**: ~R$ 40,00 per 100 kWh - Critical

**New Fields in Tariff Entity:**
```java
@Entity
public class Tariff {
    // ...existing 17 fields...
    
    // Tariff Flags (Bandeiras Tarifárias) - NEW
    @Column(name = "flag_generation_date")
    private LocalDate flagGenerationDate; // DatGeracaoConjuntoDados (from flags API)
    
    @Column(name = "competence_date")
    private LocalDate competenceDate; // DatCompetencia (month of flag validity)
    
    @Column(name = "activated_flag_name", length = 50)
    private String activatedFlagName; // NomBandeiraAcionada (Verde, Amarela, Vermelha P1/P2)
    
    @Column(name = "flag_additional_value", precision = 10, scale = 4)
    private BigDecimal flagAdditionalValue; // VlrAdicionalBandeira (additional cost per 100 kWh)
}
```

**Calculation Logic:**
```java
public BigDecimal calculateTotalCost(BigDecimal consumptionKwh) {
    // Base cost: (TUSD + TE) * consumption
    BigDecimal baseCost = tusdValue.add(teValue).multiply(consumptionKwh);
    
    // Flag cost: (flag_value / 100) * consumption
    BigDecimal flagCost = BigDecimal.ZERO;
    if (flagAdditionalValue != null && flagAdditionalValue.compareTo(BigDecimal.ZERO) > 0) {
        flagCost = flagAdditionalValue.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP)
                                       .multiply(consumptionKwh);
    }
    
    return baseCost.add(flagCost);
}
```

**Nullable Flag Fields:**
- All 4 flag fields are **NULLABLE**
- Older tariff records (before flag system) may not have flag data
- Flag data should be populated for recent/current tariffs

**Mapping Strategy:**
```java
@Service
public class AneelTariffFlagMapper {
    
    public void enrichTariffWithFlag(Tariff tariff, AneelTariffFlagDTO flagDto) {
        tariff.setFlagGenerationDate(parseDate(flagDto.getDatGeracaoConjuntoDados()));
        tariff.setCompetenceDate(parseDate(flagDto.getDatCompetencia()));
        tariff.setActivatedFlagName(flagDto.getNomBandeiraAcionada());
        tariff.setFlagAdditionalValue(parseDecimal(flagDto.getVlrAdicionalBandeira()));
    }
}
```

**Synchronization Logic:**
1. Sync base tariffs from API 1
2. Sync flags from API 2
3. Match by `competence_date` (flag) and `reference_month` (bill)
4. Update existing tariff records or create new snapshots

### Synchronization Service Pattern

```java
@Service
@Slf4j
public class AneelTariffSyncService {
    
    @Autowired
    private AneelApiClient aneelApiClient;
    
    @Autowired
    private TariffRepository tariffRepository;
    
    @Autowired
    private AneelTariffMapper tariffMapper;
    
    /**
     * Scheduled job to sync tariffs from ANEEL API
     * Runs daily at 2 AM
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void syncTariffs() {
        log.info("Starting ANEEL tariff synchronization...");
        
        try {
            // Fetch from API (paginated)
            int page = 0;
            int pageSize = 100;
            boolean hasMore = true;
            
            while (hasMore) {
                AneelApiResponse response = aneelApiClient.fetchTariffs(page, pageSize);
                List<AneelTariffDTO> dtos = response.getRecords();
                
                // Transform and save
                List<Tariff> tariffs = dtos.stream()
                    .map(tariffMapper::toEntity)
                    .filter(this::isValid)
                    .collect(Collectors.toList());
                
                tariffRepository.saveAll(tariffs);
                
                hasMore = dtos.size() == pageSize;
                page++;
                
                log.info("Synced page {} - {} tariffs", page, tariffs.size());
            }
            
            log.info("ANEEL tariff synchronization completed successfully");
            
        } catch (Exception e) {
            log.error("Error syncing ANEEL tariffs", e);
            // Consider: Send alert, retry logic, etc.
        }
    }
    
    private boolean isValid(Tariff tariff) {
        // Filter out records with "Não se aplica" or invalid data
        return tariff.getDistributor() != null 
            && !tariff.getDistributor().equalsIgnoreCase("Não se aplica")
            && tariff.getTusdValue().compareTo(BigDecimal.ZERO) >= 0;
    }
}
```

### Query Patterns for Active Tariff

```java
@Repository
public interface TariffRepository extends JpaRepository<Tariff, UUID> {
    
    /**
     * Find active tariff for a specific date
     * @param distributor Distributor name (e.g., "CPFL JAGUARI")
     * @param subgroup Subgroup (e.g., "B1")
     * @param modality Modality (e.g., "Convencional")
     * @param referenceDate Date to check validity
     * @return Active tariff or empty
     */
    @Query("""
        SELECT t FROM Tariff t 
        WHERE t.distributor = :distributor 
        AND t.subgroup = :subgroup 
        AND t.tariffModality = :modality 
        AND t.validFrom <= :referenceDate 
        AND (t.validUntil IS NULL OR t.validUntil >= :referenceDate)
        ORDER BY t.validFrom DESC
        """)
    Optional<Tariff> findActiveTariff(
        @Param("distributor") String distributor,
        @Param("subgroup") String subgroup,
        @Param("modality") String modality,
        @Param("referenceDate") LocalDate referenceDate
    );
    
    /**
     * Find all tariffs for a distributor with valid date range
     */
    @Query("""
        SELECT t FROM Tariff t 
        WHERE t.distributor = :distributor 
        AND t.validFrom <= :date 
        AND (t.validUntil IS NULL OR t.validUntil >= :date)
        """)
    List<Tariff> findValidTariffsForDistributor(
        @Param("distributor") String distributor,
        @Param("date") LocalDate date
    );
}
```

---

## 6. Child Entities - BillItem and Analysis ⭐ NEW

### BillItem Entity (Line Items)

**Purpose:** Detailed breakdown of electricity bill charges (consumption, taxes, fees).

**Design Pattern:** Weak Entity (depends on ElectricityBill)

```java
@Entity
@Table(name = "bill_items", indexes = {
    @Index(name = "idx_bill_items_bill_id", columnList = "bill_id")
})
public class BillItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    // Parent relationship - REQUIRED
    @NotNull(message = "Bill is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bill_id", nullable = false)
    private ElectricityBill bill;
    
    // Item classification
    @NotBlank
    @Size(max = 50)
    @Column(name = "item_type", nullable = false)
    private String itemType; // E.g., "OFF_PEAK_CONSUMPTION", "ICMS_TAX", "FLAG_CHARGE"
    
    @Size(max = 255)
    @Column(name = "description")
    private String description;
    
    // Values
    @DecimalMin("0.0")
    @Column(name = "quantity", precision = 10, scale = 2)
    private BigDecimal quantity; // kWh consumed, units, etc.
    
    @DecimalMin("0.0")
    @Column(name = "unit_price", precision = 10, scale = 4)
    private BigDecimal unitPrice; // Price per unit
    
    @NotNull
    @DecimalMin("0.0")
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount; // Total: quantity * unitPrice
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
```

**Key Characteristics:**
- ✅ **Weak entity**: Cannot exist without ElectricityBill
- ✅ **Cascade DELETE**: When bill is deleted, items are deleted (orphanRemoval = true)
- ✅ **No UPDATE timestamp**: Items are immutable once created
- ✅ **Flexible itemType**: String instead of ENUM for extensibility

**Bidirectional Relationship in ElectricityBill:**
```java
@OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, orphanRemoval = true)
private List<BillItem> items = new ArrayList<>();

public void addItem(BillItem item) {
    items.add(item);
    item.setBill(this); // Maintain bidirectional consistency
}
```

**Common Item Types:**
- Consumption charges: `OFF_PEAK_CONSUMPTION`, `PEAK_CONSUMPTION`
- Tariff components: `TUSD_CHARGE`, `TE_CHARGE`
- Flags: `FLAG_CHARGE`
- Taxes: `ICMS_TAX`, `PIS_TAX`, `COFINS_TAX`
- Others: `PUBLIC_LIGHTING`, `DISCOUNT`, `CREDIT`

---

### Analysis Entity (Bill Analysis)

**Purpose:** Store analysis results and recommendations for an electricity bill.

**Design Pattern:** One-to-One dependent relationship

```java
@Entity
@Table(name = "analyses", indexes = {
    @Index(name = "idx_analyses_bill_id", columnList = "bill_id")
})
public class Analysis {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    // One-to-One with ElectricityBill
    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bill_id", nullable = false, unique = true)
    private ElectricityBill bill;
    
    // Calculated metrics
    @Column(name = "average_consumption", precision = 10, scale = 2)
    private BigDecimal averageConsumption; // Average kWh over period
    
    @Column(name = "cost_per_kwh", precision = 10, scale = 4)
    private BigDecimal costPerKwh; // Calculated from bill
    
    @Column(name = "comparison_prev_month", precision = 5, scale = 2)
    private BigDecimal comparisonPrevMonth; // % change vs previous month
    
    // Recommendations
    @Column(name = "savings_tips", columnDefinition = "TEXT")
    private String savingsTips; // JSON or plain text recommendations
    
    @Column(name = "report_pdf_url", columnDefinition = "TEXT")
    private String reportPdfUrl; // Path to generated PDF report
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
```

**Key Characteristics:**
- ✅ **One-to-One**: Each bill has at most ONE analysis
- ✅ **Optional**: Bill can exist without analysis
- ✅ **Unique constraint**: `bill_id` is unique (enforced by @OneToOne)
- ✅ **Immutable**: No update timestamp (analyses are snapshots)
- ✅ **Lazy loading**: Analysis loaded only when accessed

**Bidirectional Relationship in ElectricityBill:**
```java
@OneToOne(mappedBy = "bill", cascade = CascadeType.ALL, orphanRemoval = true)
private Analysis analysis;
```

**⚠️ Important Note on @OneToOne Lazy Loading:**

JPA specification states that the **non-owning side** of @OneToOne cannot be truly lazy loaded. Even with `FetchType.LAZY`, Hibernate may fetch it eagerly.

**Workaround if performance is critical:**
```java
// Option 1: Use @MapsId to optimize
@OneToOne
@MapsId
@JoinColumn(name = "bill_id")
private ElectricityBill bill;

// Option 2: Use bytecode instrumentation (Hibernate)
// Add to hibernate.properties:
// hibernate.bytecode.use_reflection_optimizer=true
```

**For this project:** Accept eager loading on non-owning side. Analysis is small and rarely accessed without bill context.

---

### Cascade Strategies for Child Entities

| Entity | Cascade Type | Orphan Removal | Rationale |
|--------|--------------|----------------|-----------|
| **BillItem** | `ALL` | `true` | Items cannot exist without bill |
| **Analysis** | `ALL` | `true` | Analysis cannot exist without bill |

**What happens when ElectricityBill is deleted:**
1. All `BillItem` records are deleted (cascade + orphan removal)
2. Associated `Analysis` is deleted (cascade + orphan removal)
3. Foreign key constraints prevent deletion if references exist elsewhere

**Orphan Removal Example:**
```java
ElectricityBill bill = billRepository.findById(id);
bill.getItems().clear(); // Orphan removal triggers DELETE for all items
billRepository.save(bill);
```

---

### Validation Best Practices

**BillItem Validations:**
- ✅ `@NotNull` on `bill`, `itemType`, `amount`
- ✅ `@NotBlank` on `itemType` (not just NotNull)
- ✅ `@DecimalMin("0.0")` for monetary values
- ✅ `@Size` constraints on text fields

**Analysis Validations:**
- ✅ `@NotNull` on `bill` (required relationship)
- ✅ All numeric fields nullable (optional metrics)
- ✅ TEXT columns for large content (`savingsTips`, `reportPdfUrl`)

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

---

**Last Updated:** 2025-12-26  
**Version:** 2.2 (Added BillItem and Analysis entities)  
**Status:** Implementation Ready ✅
