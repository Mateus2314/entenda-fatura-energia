# Sprint Planning & Roadmap

**Project:** Understand Your Electricity Bill  
**Methodology:** TDD (Test-Driven Development) + DDD (Domain-Driven Design)  
**Last Updated:** 2025-12-26  
**Version:** 2.0 (Detailed Implementation Roadmap)

---

## 📋 Development Approach

### Test-Driven Development (TDD)
1. **RED** - Write failing test first
2. **GREEN** - Implement minimum code to pass
3. **REFACTOR** - Improve code quality

### Domain-Driven Design (DDD)
- **Domain Layer** - Entities, Value Objects, Aggregates
- **Repository Layer** - Data access
- **Service Layer** - Business logic
- **Controller Layer** - API endpoints

---

## 🎯 PHASE 1: Domain Layer - Complete Entities

### Issue #1: Implement JPA Entities - Tariff and ElectricityBill

**Title:** [DOMAIN] Implement Tariff and ElectricityBill entities with ANEEL API integration

**Description:**  
Implement remaining domain entities following JOINED inheritance pattern already established. Tariff entity must support complete ANEEL API field mapping (17 fields).

**Priority:** HIGH  
**Estimated Time:** 8-10 hours

**Acceptance Criteria:**

- ✅ **Tariff Entity** with 21 fields (17 from Tariffs API + 4 from Flags API):
  
  **Base Tariff Fields (API 1):**
  - `id` (UUID, PK)
  - `generation_date` (LocalDate, NOT NULL)
  - `description_reh` (String, 500)
  - `distributor` (String, 100, NOT NULL) - maps to `SigAgente`
  - `cnpj_distributor` (String, 14, NOT NULL)
  - `valid_from` (LocalDate, NOT NULL)
  - `valid_until` (LocalDate, nullable)
  - `tariff_base_desc` (String, 100)
  - `subgroup` (String, 10)
  - `tariff_modality` (String, 50)
  - `consumer_class` (String, 100)
  - `consumer_subclass` (String, 100)
  - `detail` (String, 100)
  - `tariff_post_name` (String, 50)
  - `tertiary_unit` (String, 10)
  - `accessing_agent` (String, 100)
  - `tusd_value` (BigDecimal, NOT NULL, precision=10, scale=4)
  - `te_value` (BigDecimal, NOT NULL, precision=10, scale=4)
  
  **Tariff Flag Fields (API 2) ⭐ NEW:**
  - `flag_generation_date` (LocalDate, nullable)
  - `competence_date` (LocalDate, nullable) - month of flag validity
  - `activated_flag_name` (String, 50, nullable) - Verde, Amarela, Vermelha P1/P2
  - `flag_additional_value` (BigDecimal, nullable, precision=10, scale=4) - additional per 100 kWh
  
  **Metadata:**
  - `created_at`, `updated_at` (timestamps)

- ✅ **ElectricityBill Entity**:
  - `id` (UUID, PK)
  - `client_id` (FK → clients.user_id, NOT NULL)
  - `consultant_id` (FK → consultants.user_id, nullable)
  - `tariff_id` (FK → tariffs.id, NOT NULL) ⭐
  - `reference_month` (LocalDate, NOT NULL)
  - `due_date` (LocalDate, NOT NULL)
  - `total_amount` (BigDecimal, NOT NULL)
  - `consumption_kwh` (BigDecimal, NOT NULL)
  - `pdf_url` (String/TEXT, nullable)
  - `created_at`, `updated_at` (timestamps)
  - ❌ NO `tariff_modality` field (redundant - use tariff.tariffModality)
  - ❌ NO `status` field (not required)

- ✅ **Relationships**:
  - `Tariff` @OneToMany → `ElectricityBill` (one tariff, many bills)
  - `ElectricityBill` @ManyToOne → `Tariff` (lazy, non-optional)
  - `ElectricityBill` @ManyToOne → `Client` (lazy, non-optional)
  - `ElectricityBill` @ManyToOne → `Consultant` (lazy, optional)

- ✅ **Indexes** (using @Table annotation):
  - `idx_tariff_search` on (distributor, subgroup, tariff_modality, valid_from, valid_until)
  - `idx_tariff_cnpj` on (cnpj_distributor)
  - `idx_tariff_validity` on (valid_from, valid_until)
  - `idx_bills_client_id` on (client_id)
  - `idx_bills_consultant_id` on (consultant_id)
  - `idx_bills_tariff_id` on (tariff_id)
  - `idx_bills_reference_month` on (reference_month)

- ✅ **Validation Constraints**:
  - `@NotNull` on required fields
  - `@Column` with proper lengths
  - Check: `valid_until >= valid_from` (if not null)
  - Check: `tusd_value >= 0`, `te_value >= 0`

- ✅ **Unit Tests** (TariffTest, ElectricityBillTest):
  - Test field validations (NotNull, lengths)
  - Test relationship mappings
  - Test @PrePersist/@PreUpdate callbacks
  - Test entity equality and hashCode

- ✅ **Integration Tests** with Testcontainers:
  - Test persistence (save/find)
  - Test cascade operations
  - Test relationship queries

**Files to Create:**
```
backend/src/main/java/com/understand_your_electricity_bill/model/
├── Tariff.java                    (NEW - 21 fields: 17 tariffs + 4 flags)
└── ElectricityBill.java           (NEW - without tariff_modality/status)

backend/src/test/java/com/understand_your_electricity_bill/model/
├── TariffTest.java                (NEW)
└── ElectricityBillTest.java       (NEW)
```

**References:**
- `docs/database/ER_DIAGRAM.md` (section: TARIFFS with dual ANEEL API mapping)
- `docs/database/jpa-entity-modeling.md` (section: External API Integration + Tariff Flags)

**ANEEL APIs:**
1. **Tariffs API**: `https://dadosabertos.aneel.gov.br/api/3/action/datastore_search?resource_id=fcf2906c-7c32-4b9b-a637-054e7a5234f4`
2. **Flags API** ⭐: `https://dadosabertos.aneel.gov.br/api/3/action/datastore_search?resource_id=0591b8f6-fe54-437b-b72b-1aa2efd46e42`

**Technical Notes:**
- Use **String** for ANEEL fields (NOT ENUMs) - API values are dynamic
- Tariff fields match ANEEL API response structure exactly
- **Flag fields are nullable** - older records may not have flag data
- ElectricityBill references Tariff for snapshot (immutable)
- No manual tariff editing - synced from API only
- **Dual API sync**: Tariffs + Flags combined in single entity

---

### Issue #2: Implement Repositories - Tariff and ElectricityBill

**Title:** [REPOSITORY] Implement repositories with custom queries for Tariff and ElectricityBill

**Description:**  
Create repository interfaces with custom JPQL queries following pattern established in ClientRepository and ConsultantRepository.

**Priority:** HIGH  
**Estimated Time:** 6-8 hours

**Acceptance Criteria:**

- ✅ **TariffRepository** extends JpaRepository<Tariff, UUID>:
  - `findActiveTariff(distributor, subgroup, modality, referenceDate)` - returns single active tariff
  - `findValidTariffsForDistributor(distributor, date)` - returns all valid tariffs for distributor
  - `findByDistributor(distributor)` - returns all tariffs for distributor
  - `findByCnpjDistributor(cnpj)` - returns tariffs by CNPJ
  - `findByValidFromBetween(startDate, endDate)` - returns tariffs by validity range

- ✅ **ElectricityBillRepository** extends JpaRepository<ElectricityBill, UUID>:
  - `findByClientId(clientId)` - returns all bills for client
  - `findByConsultantId(consultantId)` - returns all bills for consultant
  - `findByClientIdAndReferenceMonth(clientId, referenceMonth)` - specific month bill
  - `findByClientIdOrderByReferenceMonthDesc(clientId)` - bills sorted by date
  - `findByReferenceMonthBetween(startMonth, endMonth)` - bills in date range
  - `countByClientId(clientId)` - count bills for client

- ✅ **Custom Queries** using @Query annotation:
  - Use JOIN FETCH for relationships to avoid N+1 queries
  - Use named parameters (@Param)
  - Add pagination support (@Pageable) where applicable

- ✅ **Integration Tests** with Testcontainers:
  - Test each custom query method
  - Test pagination and sorting
  - Test edge cases (empty results, null parameters)
  - Test JOIN FETCH efficiency (no N+1)

**Files to Create:**
```
backend/src/main/java/com/understand_your_electricity_bill/repository/
├── TariffRepository.java              (NEW)
└── ElectricityBillRepository.java     (NEW)

backend/src/test/java/com/understand_your_electricity_bill/repository/
├── TariffRepositoryTest.java          (NEW)
└── ElectricityBillRepositoryTest.java (NEW)
```

**References:**
- `docs/backend/repository-patterns.md`
- Existing: `ClientRepository.java`, `ConsultantRepository.java`

---

## 🎯 PHASE 2: DTOs and Mappers

### Issue #3: Create DTOs for Tariff Operations

**Title:** [DTO] Implement DTOs for Tariff with validation

**Description:**  
Create DTOs for Tariff following MapStruct pattern. Since tariffs are synced from API (not user-created), we need Response DTOs only.

**Priority:** MEDIUM  
**Estimated Time:** 3-4 hours

**Acceptance Criteria:**

- ✅ **TariffResponseDTO** (record):
  - All 17 fields from Tariff entity
  - Used for API responses and frontend display
  - No validations (read-only)

- ✅ **TariffSummaryDTO** (record):
  - Essential fields only: id, distributor, tariffModality, subgroup, validFrom, validUntil, tusdValue, teValue
  - Used for list views and dropdowns

- ✅ **TariffMapper** with MapStruct:
  - `toResponseDTO(Tariff tariff)`
  - `toSummaryDTO(Tariff tariff)`
  - `toResponseDTOList(List<Tariff> tariffs)`

- ✅ **Unit Tests** (TariffMapperTest):
  - Test entity → DTO conversion
  - Test null handling
  - Test list conversion

**Files to Create:**
```
backend/src/main/java/com/understand_your_electricity_bill/dto/
├── TariffResponseDTO.java    (NEW - record)
└── TariffSummaryDTO.java     (NEW - record)

backend/src/main/java/com/understand_your_electricity_bill/mapper/
└── TariffMapper.java         (NEW - MapStruct interface)

backend/src/test/java/com/understand_your_electricity_bill/mapper/
└── TariffMapperTest.java     (NEW)
```

---

### Issue #4: Create DTOs for ElectricityBill Operations

**Title:** [DTO] Implement DTOs for ElectricityBill CRUD with validation

**Description:**  
Create DTOs for all ElectricityBill operations following pattern established for Client/Consultant.

**Priority:** MEDIUM  
**Estimated Time:** 4-5 hours

**Acceptance Criteria:**

- ✅ **ElectricityBillCreateDTO** (record):
  - Fields: clientId, consultantId (optional), tariffId, referenceMonth, dueDate, totalAmount, consumptionKwh, pdfUrl (optional)
  - Validations: @NotNull, @Positive, @FutureOrPresent (dueDate)

- ✅ **ElectricityBillUpdateDTO** (record):
  - Editable fields: consultantId, dueDate, totalAmount, consumptionKwh, pdfUrl
  - Validations: @NotNull, @Positive

- ✅ **ElectricityBillResponseDTO** (record):
  - All entity fields
  - Nested TariffSummaryDTO
  - Nested ClientDTO (or just clientName)
  - ConsultantDTO if present

- ✅ **ElectricityBillSummaryDTO** (record):
  - Essential fields: id, referenceMonth, totalAmount, consumptionKwh, clientName

- ✅ **ElectricityBillMapper** with MapStruct:
  - `toEntity(CreateDTO dto)`
  - `toResponseDTO(ElectricityBill bill)`
  - `toSummaryDTO(ElectricityBill bill)`
  - Handle nested mappings (tariff, client, consultant)

- ✅ **Validation Tests**:
  - Test Bean Validation constraints
  - Test invalid data rejection

- ✅ **Mapper Tests**:
  - Test all mapping methods
  - Test nested object mappings

**Files to Create:**
```
backend/src/main/java/com/understand_your_electricity_bill/dto/
├── ElectricityBillCreateDTO.java     (NEW)
├── ElectricityBillUpdateDTO.java     (NEW)
├── ElectricityBillResponseDTO.java   (NEW)
└── ElectricityBillSummaryDTO.java    (NEW)

backend/src/main/java/com/understand_your_electricity_bill/mapper/
└── ElectricityBillMapper.java        (NEW)

backend/src/test/java/com/understand_your_electricity_bill/mapper/
└── ElectricityBillMapperTest.java    (NEW)
```

---

## 🎯 PHASE 3: Service Layer - Business Logic

### Issue #5: Implement TariffService with ANEEL Integration

**Title:** [SERVICE] Implement TariffService with dual ANEEL API synchronization (Tariffs + Flags)

**Description:**  
Service to manage tariffs synced from TWO ANEEL APIs: base tariffs and tariff flags (bandeiras tarifárias). Includes scheduled jobs for automatic synchronization.

**Priority:** HIGH  
**Estimated Time:** 10-12 hours (increased due to dual API integration)

**Acceptance Criteria:**

- ✅ **TariffService** methods:
  - `findActiveTariff(distributor, subgroup, modality, date)` - returns active tariff
  - `findActiveTariffWithFlag(distributor, subgroup, modality, date)` - with flag data ⭐ NEW
  - `findById(id)` - returns tariff by ID
  - `findAllByDistributor(distributor)` - returns all tariffs for distributor
  - `searchTariffs(searchCriteria)` - advanced search with filters
  - `calculateTotalCost(tariffId, consumptionKwh)` - includes flag cost ⭐ NEW

- ✅ **AneelApiClient** (RestTemplate/WebClient):
  - `fetchTariffs(page, pageSize)` - calls Tariffs API with pagination
  - `fetchTariffFlags(page, pageSize)` - calls Flags API with pagination ⭐ NEW
  - Error handling and retries
  - Timeout configuration

- ✅ **AneelTariffSyncService**:
  - `@Scheduled` method to sync base tariffs (cron: daily at 2 AM)
  - `@Scheduled` method to sync tariff flags (cron: daily at 3 AM) ⭐ NEW
  - Parse API responses and map to entity
  - Handle value format conversion (comma → dot for decimals)
  - Filter invalid records ("Não se aplica", negative values)
  - **Enrichment logic**: Match flags to tariffs by competence_date ⭐ NEW
  - Upsert logic (update if exists, insert if new)
  - Transaction management

- ✅ **DTOs and Mappers**:
  - `AneelTariffDTO` - Base tariff API response (17 fields)
  - `AneelTariffFlagDTO` - Flag API response (4 fields) ⭐ NEW
  - `AneelTariffMapper` - Maps base tariffs with custom decimal parsing
  - `AneelTariffFlagMapper` - Maps and enriches tariffs with flag data ⭐ NEW

- ✅ **Unit Tests** with Mockito:
  - Mock TariffRepository
  - Test business logic in isolation
  - Test flag enrichment logic ⭐ NEW
  - Test cost calculation with flags ⭐ NEW
  - Test edge cases

- ✅ **Integration Tests**:
  - Test with real database (Testcontainers)
  - Mock both ANEEL APIs with WireMock
  - Test scheduled job execution (both sync jobs)
  - Test dual API synchronization flow ⭐ NEW

**Files to Create:**
```
backend/src/main/java/com/understand_your_electricity_bill/service/
├── TariffService.java                    (NEW)
└── AneelTariffSyncService.java           (NEW - dual API sync)

backend/src/main/java/com/understand_your_electricity_bill/client/
├── AneelApiClient.java                   (NEW - handles both APIs)
└── dto/
    ├── AneelTariffDTO.java               (NEW - base tariff)
    ├── AneelTariffFlagDTO.java           (NEW - flag data) ⭐
    ├── AneelApiResponse.java             (NEW)
    ├── AneelTariffMapper.java            (NEW - base mapper)
    └── AneelTariffFlagMapper.java        (NEW - flag enrichment) ⭐

backend/src/test/java/com/understand_your_electricity_bill/service/
├── TariffServiceTest.java                (NEW)
└── AneelTariffSyncServiceTest.java       (NEW - test dual sync)
```

**Configuration:**
```yaml
# application.yaml
aneel:
  api:
    base-url: https://dadosabertos.aneel.gov.br/api/3/action
    tariffs:
      resource-id: fcf2906c-7c32-4b9b-a637-054e7a5234f4
    flags:
      resource-id: 0591b8f6-fe54-437b-b72b-1aa2efd46e42  # NEW
    page-size: 100
    timeout: 30s
  sync:
    tariffs-cron: "0 0 2 * * ?"  # Daily at 2 AM
    flags-cron: "0 0 3 * * ?"    # Daily at 3 AM (NEW)
    enabled: true
```

---

### Issue #6: Implement ElectricityBillService

**Title:** [SERVICE] Implement ElectricityBillService with bill management and calculations

**Description:**  
Service layer for electricity bill operations including CRUD, tariff association, and cost calculations.

**Priority:** HIGH  
**Estimated Time:** 6-8 hours

**Acceptance Criteria:**

- ✅ **CRUD Operations**:
  - `createBill(CreateDTO dto)` - validates tariff exists and is valid for reference month
  - `updateBill(id, UpdateDTO dto)` - updates mutable fields only
  - `deleteBill(id)` - soft delete or hard delete with cascade
  - `findById(id)` - returns bill with relationships
  - `findByClientId(clientId, pageable)` - paginated bills for client

- ✅ **Business Logic**:
  - `assignConsultant(billId, consultantId)` - associate consultant with bill
  - `calculateCostBreakdown(billId)` - calculate TUSD + TE costs
  - `getBillsByConsultant(consultantId, pageable)` - bills managed by consultant
  - Validation: tariff must be valid for bill's reference_month

- ✅ **Unit Tests** with Mockito:
  - Test all service methods
  - Test validation logic
  - Test exception handling

- ✅ **Integration Tests**:
  - Test with Testcontainers
  - Test cascade operations
  - Test transaction rollback

**Files to Create:**
```
backend/src/main/java/com/understand_your_electricity_bill/service/
└── ElectricityBillService.java           (NEW)

backend/src/test/java/com/understand_your_electricity_bill/service/
├── ElectricityBillServiceTest.java       (NEW - Unit)
└── ElectricityBillServiceIntegrationTest.java (NEW)
```

---

## 🎯 PHASE 4: REST API Controllers

### Issue #7: Implement TariffController

**Title:** [CONTROLLER] Implement TariffController with REST endpoints

**Description:**  
REST API for tariff queries (read-only, no create/update - synced from ANEEL only).

**Priority:** MEDIUM  
**Estimated Time:** 4-5 hours

**Acceptance Criteria:**

- ✅ **Endpoints**:
  - `GET /api/tariffs` - List all tariffs (paginated, filterable)
  - `GET /api/tariffs/{id}` - Get tariff by ID
  - `GET /api/tariffs/search` - Search with filters (distributor, subgroup, modality, date)
  - `GET /api/tariffs/distributors` - List unique distributors
  - `GET /api/tariffs/active` - Get active tariff for criteria (query params)

- ✅ **Features**:
  - Pagination (@PageableDefault)
  - Filtering with request params
  - OpenAPI/Swagger documentation
  - Exception handling (@ControllerAdvice)

- ✅ **Tests** with MockMvc:
  - Test all endpoints
  - Test pagination
  - Test filters
  - Test error responses (404, 400)

**Files to Create:**
```
backend/src/main/java/com/understand_your_electricity_bill/controller/
└── TariffController.java                 (NEW)

backend/src/test/java/com/understand_your_electricity_bill/controller/
└── TariffControllerTest.java             (NEW)
```

---

### Issue #8: Implement ElectricityBillController

**Title:** [CONTROLLER] Implement ElectricityBillController with CRUD endpoints

**Description:**  
REST API for electricity bill management including file upload for PDFs.

**Priority:** HIGH  
**Estimated Time:** 6-8 hours

**Acceptance Criteria:**

- ✅ **Endpoints**:
  - `POST /api/bills` - Create bill
  - `GET /api/bills` - List bills (paginated, by client or consultant)
  - `GET /api/bills/{id}` - Get bill by ID
  - `PUT /api/bills/{id}` - Update bill
  - `DELETE /api/bills/{id}` - Delete bill
  - `PATCH /api/bills/{id}/consultant` - Assign/change consultant
  - `POST /api/bills/{id}/upload` - Upload PDF file
  - `GET /api/bills/{id}/download` - Download PDF

- ✅ **Features**:
  - MultipartFile upload for PDFs
  - File validation (size, type)
  - Authorization (client can only see own bills)
  - Pagination and sorting
  - OpenAPI documentation

- ✅ **Tests** with MockMvc:
  - Test CRUD operations
  - Test file upload
  - Test authorization rules
  - Test validation errors

**Files to Create:**
```
backend/src/main/java/com/understand_your_electricity_bill/controller/
└── ElectricityBillController.java        (NEW)

backend/src/main/java/com/understand_your_electricity_bill/service/
└── FileStorageService.java               (NEW - for PDF handling)

backend/src/test/java/com/understand_your_electricity_bill/controller/
└── ElectricityBillControllerTest.java    (NEW)
```

---

## 🎯 PHASE 5: Database Migrations

### Issue #9: Create Flyway Migrations for Tariffs and Bills

**Title:** [DATABASE] Create Flyway migrations V005 and V006

**Description:**  
SQL migrations for tariffs and electricity_bills tables following established pattern.

**Priority:** HIGH  
**Estimated Time:** 3-4 hours

**Acceptance Criteria:**

- ✅ **V005__create_tariffs_table.sql**:
  - Create tariffs table with all 17 fields
  - Add indexes (distributor, cnpj, validity, search)
  - Add check constraints (valid_until >= valid_from, values >= 0)
  - Add comments documenting ANEEL API mapping

- ✅ **V006__create_electricity_bills_table.sql**:
  - Create electricity_bills table
  - Add foreign keys to clients, consultants, tariffs
  - Add indexes (client_id, consultant_id, tariff_id, reference_month)
  - Add ON DELETE constraints (CASCADE for client, SET NULL for consultant, RESTRICT for tariff)

- ✅ **Validation**:
  - Test migrations run successfully
  - Test rollback (if supported)
  - Verify indexes created
  - Test foreign key constraints

**Files to Create:**
```
backend/src/main/resources/db/migration/
├── V005__create_tariffs_table.sql        (NEW)
└── V006__create_electricity_bills_table.sql (NEW)

docs/database/migrations/
├── V005/
│   ├── migration.md                      (NEW - documentation)
│   ├── queries.sql                       (NEW - test queries)
│   └── validation.md                     (NEW)
└── V006/
    ├── migration.md                      (NEW)
    ├── queries.sql                       (NEW)
    └── validation.md                     (NEW)
```

---

## 🎯 PHASE 6: Security & Authentication

### Issue #10: Implement Authorization for Bills

**Title:** [SECURITY] Implement role-based authorization for electricity bills

**Description:**  
Security rules ensuring clients can only access their own bills, consultants can access managed bills, and admins have full access.

**Priority:** HIGH  
**Estimated Time:** 4-5 hours

**Acceptance Criteria:**

- ✅ **Authorization Rules**:
  - CLIENT role: Can only view/create own bills
  - CONSULTANT role: Can view/manage bills for associated clients
  - ADMIN role: Full access to all bills

- ✅ **Implementation**:
  - `@PreAuthorize` annotations on controller methods
  - Custom SpEL expressions for ownership checks
  - Service-level authorization checks

- ✅ **Tests**:
  - Test CLIENT cannot access other client's bills
  - Test CONSULTANT can access managed bills only
  - Test ADMIN has full access
  - Test 403 Forbidden responses

**Files to Modify:**
```
backend/src/main/java/com/understand_your_electricity_bill/controller/
└── ElectricityBillController.java        (ADD @PreAuthorize)

backend/src/main/java/com/understand_your_electricity_bill/config/
└── SecurityConfig.java                   (UPDATE rules)

backend/src/test/java/com/understand_your_electricity_bill/security/
└── BillAuthorizationTest.java            (NEW)
```

---

## 🎯 PHASE 7: Frontend Implementation

### Issue #11: Setup React Project Structure

**Title:** [FRONTEND] Configure React with TypeScript, Vite, TailwindCSS, and React Router

**Priority:** MEDIUM  
**Estimated Time:** 4-5 hours

**Acceptance Criteria:**

- ✅ Initialize Vite + React + TypeScript project
- ✅ Configure TailwindCSS
- ✅ Setup React Router for navigation
- ✅ Configure Axios for API calls
- ✅ Create folder structure (pages, components, services, hooks, types)
- ✅ Setup environment variables (.env)
- ✅ Configure ESLint + Prettier

---

### Issue #12: Implement Login and Authentication Flow

**Title:** [FRONTEND] Create login page with JWT authentication

**Priority:** HIGH  
**Estimated Time:** 5-6 hours

**Acceptance Criteria:**

- ✅ Login form with email/password
- ✅ Integration with /auth/login endpoint
- ✅ Store JWT token in localStorage
- ✅ Redirect based on user role (CLIENT → dashboard, CONSULTANT → management, ADMIN → admin panel)
- ✅ Protected routes with authentication guard
- ✅ Tests with React Testing Library

---

### Issue #13: Client Dashboard - View Bills

**Title:** [FRONTEND] Create client dashboard to view electricity bills

**Priority:** HIGH  
**Estimated Time:** 8-10 hours

**Acceptance Criteria:**

- ✅ List all client's bills (table/cards)
- ✅ Filter by date range
- ✅ Sort by reference month
- ✅ View bill details (modal or separate page)
- ✅ Display tariff information
- ✅ Download PDF button
- ✅ Chart: consumption over time (Chart.js or Recharts)
- ✅ Responsive design

---

## 📊 Summary Statistics

| Phase | Issues | Estimated Hours | Priority |
|-------|--------|-----------------|----------|
| PHASE 1: Domain Layer | 2 | 14-18 | HIGH |
| PHASE 2: DTOs/Mappers | 2 | 7-9 | MEDIUM |
| PHASE 3: Service Layer | 2 | 16-20 (+2 for flags) | HIGH |
| PHASE 4: Controllers | 2 | 10-13 | MEDIUM-HIGH |
| PHASE 5: Migrations | 1 | 3-4 | HIGH |
| PHASE 6: Security | 1 | 4-5 | HIGH |
| PHASE 7: Frontend | 3 | 17-21 | MEDIUM-HIGH |
| **TOTAL** | **13** | **71-90** | - |

**Notes:** 
- Issue #5 increased from 8-10h to 10-12h due to dual ANEEL API integration (tariffs + flags)
- Tariff entity expanded from 17 to 21 fields

---

## 🎯 Current Focus

**Next Immediate Tasks:**
1. ✅ Complete documentation updates (jpa-entity-modeling.md, ER_DIAGRAM.md, sprints.md) - DONE
2. 🔄 **Issue #1** - Implement Tariff (21 fields) and ElectricityBill entities - IN PROGRESS
3. Issue #2 - Implement repositories
4. Issue #5 - ANEEL dual API integration (Tariffs + Flags)

---

**Last Updated:** 2025-12-26  
**Roadmap Version:** 2.1 (Added Tariff Flags - Bandeiras Tarifárias)  
**Status:** ✅ Ready for Implementation

