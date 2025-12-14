# 07 - Data Models & Entities

## 1. User Domain

### 1.1 User (Base Entity)
**Description:** Abstract base entity for all user types

**Attributes:**
- `id` (UUID) - Primary key, unique identifier
- `email` (String) - Unique email address, indexed
- `passwordHash` (String) - BCrypt hashed password
- `userType` (Enum) - CLIENT, CONSULTANT, ADMIN
- `createdAt` (Timestamp) - Account creation date
- `updatedAt` (Timestamp) - Last update date
- `lastLogin` (Timestamp) - Last successful login
- `isActive` (Boolean) - Account active status
- `isEmailVerified` (Boolean) - Email verification status
- `emailVerificationToken` (String) - Verification token, nullable
- `passwordResetToken` (String) - Password reset token, nullable
- `passwordResetExpires` (Timestamp) - Reset token expiration

**Validations:**
- Email: Valid format, unique, max 255 chars
- Password: Min 8 chars, 1 uppercase, 1 number, 1 special character
- UserType: Must be one of defined enum values

**Business Rules:**
- Email must be verified before full access
- Account can be deactivated but not deleted (soft delete)
- Password must be changed every 180 days (future)

---

### 1.2 Client (extends User)
**Description:** End consumer who analyzes their own bills

**Attributes:**
- `userId` (UUID) - Foreign key to User, also primary key
- `fullName` (String) - Complete name
- `phone` (String) - Phone number, optional
- `countryCode` (String) - ISO country code, default "BR"
- `languagePreference` (String) - UI language, default "pt-BR"
- `timezone` (String) - User timezone, default "America/Sao_Paulo"

**Relationships:**
- One-to-Many with BillsData
- Many-to-Many with Consultant (through ClientConsultantRelation)

**Validations:**
- FullName: Required, min 3 chars, max 255 chars
- Phone: Valid format for country (if provided)
- CountryCode: Valid ISO 3166-1 alpha-2

---

### 1.3 Consultant (extends User)
**Description:** Professional who analyzes bills for clients

**Attributes:**
- `userId` (UUID) - Foreign key to User, also primary key
- `fullName` (String) - Complete name
- `phone` (String) - Phone number, required
- `companyName` (String) - Business name
- `cnpj` (String) - Brazilian company ID, unique
- `companyLogoUrl` (String) - S3/storage URL for logo
- `businessAddress` (String) - Complete address
- `professionalLicense` (String) - Engineering license number, optional

**Relationships:**
- Many-to-Many with Client (through ClientConsultantRelation)
- One-to-Many with BillsData (for client bills they manage)

**Validations:**
- CNPJ: Valid format, unique, 14 digits
- CompanyName: Required, min 3 chars, max 255 chars
- Phone: Required, valid format

**Business Rules:**
- Can manage multiple clients
- Can generate branded reports
- Cannot see other consultants' clients

---

### 1.4 Admin (extends User)
**Description:** Platform administrator with elevated permissions

**Attributes:**
- `userId` (UUID) - Foreign key to User, also primary key
- `fullName` (String) - Complete name
- `adminLevel` (Integer) - Permission level (1-5)

**Relationships:**
- Can access all entities for management

**Validations:**
- AdminLevel: Between 1 and 5

**Business Rules:**
- Can view all users and data
- Can perform administrative actions
- All actions logged in audit trail
- Cannot be created via public registration

---

## 2. Electricity Bill Domain

### 2.1 UtilityCompany
**Description:** Electricity distributor/utility company

**Attributes:**
- `id` (Long) - Primary key
- `name` (String) - Company name
- `cnpj` (String) - Company ID, unique
- `aneelCode` (String) - ANEEL identification code, unique
- `state` (String) - Brazilian state (2-letter code)
- `logoUrl` (String) - Company logo URL
- `website` (String) - Company website URL
- `phoneNumber` (String) - Customer service phone
- `isActive` (Boolean) - Active distributor

**Relationships:**
- One-to-Many with BillsData
- One-to-Many with TariffCache

**Validations:**
- Name: Required, unique
- AneelCode: Required, unique
- State: Valid Brazilian state code
- CNPJ: Valid format, unique

**Business Rules:**
- Cannot delete if bills reference it
- Logo displayed in UI when selected
- Used to fetch ANEEL tariffs

---

### 2.2 BillsData
**Description:** Core entity representing an electricity bill

**Attributes:**

**Identification:**
- `id` (UUID) - Primary key
- `userId` (UUID) - Foreign key to User (bill owner)
- `clientId` (UUID) - Foreign key to Client, nullable (for consultant's client)
- `utilityCompanyId` (Long) - Foreign key to UtilityCompany

**Reference Information:**
- `referenceMonth` (Integer) - Billing month (1-12)
- `referenceYear` (Integer) - Billing year
- `consumerUnitNumber` (String) - Utility's consumer unit ID
- `installationNumber` (String) - Utility's installation number
- `billingDays` (Integer) - Number of days in billing period

**Consumption Data:**
- `totalConsumptionKwh` (BigDecimal) - Total energy consumed
- `onPeakConsumptionKwh` (BigDecimal) - Peak hours consumption, nullable
- `offPeakConsumptionKwh` (BigDecimal) - Off-peak consumption, nullable
- `reactiveEnergyKvarh` (BigDecimal) - Reactive energy, nullable
- `powerDemandKw` (BigDecimal) - Power demand, nullable (Group A)

**Meter Readings:**
- `previousReading` (BigDecimal) - Previous meter reading, nullable
- `currentReading` (BigDecimal) - Current meter reading, nullable

**Tariff Information:**
- `tariffModality` (Enum) - CONVENTIONAL, WHITE, BLUE, GREEN
- `tusdRate` (BigDecimal) - Distribution tariff (R$/kWh)
- `teRate` (BigDecimal) - Energy tariff (R$/kWh)
- `flagType` (Enum) - GREEN, YELLOW, RED_LEVEL_1, RED_LEVEL_2
- `flagValue` (BigDecimal) - Flag charge (R$/kWh)

**Taxes and Charges:**
- `pisRate` (BigDecimal) - PIS tax percentage
- `cofinsRate` (BigDecimal) - COFINS tax percentage
- `icmsRate` (BigDecimal) - ICMS tax percentage
- `publicLightingValue` (BigDecimal) - Public lighting contribution (R$)

**Total:**
- `totalAmount` (BigDecimal) - Total bill amount (R$)

**Metadata:**
- `createdAt` (Timestamp) - Record creation date
- `updatedAt` (Timestamp) - Last update date
- `notes` (String) - User notes, optional

**Relationships:**
- Many-to-One with User
- Many-to-One with Client (if consultant-managed)
- Many-to-One with UtilityCompany
- One-to-One with EngineeringData
- One-to-One with AnalysisResults
- One-to-Many with Simulations

**Validations:**
- ReferenceMonth: Between 1 and 12
- ReferenceYear: Between 2020 and current year + 1
- TotalConsumptionKwh: Positive, max 999999
- All monetary values: Positive, 2 decimal places
- All rate percentages: Between 0 and 100
- BillingDays: Between 1 and 60

**Business Rules:**
- If consultant creates, must link to one of their clients
- Consumption should match reading difference (warning if not)
- Total should be calculable from components (warning if discrepancy)
- Cannot modify after analysis generated (create new version instead)

---

### 2.3 EngineeringData
**Description:** Technical details about the electrical installation

**Attributes:**
- `id` (UUID) - Primary key
- `billId` (UUID) - Foreign key to BillsData, unique

**Electrical System:**
- `voltageLevel` (String) - e.g., "127/220V", "127V", "220V"
- `connectionType` (Enum) - SINGLE_PHASE, TWO_PHASE, THREE_PHASE
- `installedPowerKw` (BigDecimal) - Total installed power
- `circuitBreakerRating` (Integer) - Main breaker amperage
- `transformerCapacity` (BigDecimal) - Transformer capacity (kVA), nullable
- `powerFactor` (BigDecimal) - Power factor, nullable (0.0-1.0)
- `installationClass` (Enum) - RESIDENTIAL, COMMERCIAL, INDUSTRIAL, RURAL
- `installationSubclass` (String) - Detailed classification, nullable

**Usage Patterns:**
- `typicalUsageHours` (String) - e.g., "08:00-22:00", nullable
- `occupantsCount` (Integer) - Number of people, nullable

**Building Information:**
- `builtAreaSqm` (BigDecimal) - Built area in square meters, nullable
- `buildingType` (String) - e.g., "House", "Apartment", nullable
- `yearOfConstruction` (Integer) - Construction year, nullable

**Special Equipment:**
- `hasSolarPanels` (Boolean) - Solar generation installed
- `solarCapacityKwp` (BigDecimal) - Solar system capacity, nullable
- `hasBatteryStorage` (Boolean) - Battery system installed
- `batteryCapacityKwh` (BigDecimal) - Battery capacity, nullable

**Metadata:**
- `createdAt` (Timestamp)
- `updatedAt` (Timestamp)

**Relationships:**
- One-to-One with BillsData

**Validations:**
- PowerFactor: Between 0.0 and 1.0
- OccupantsCount: Positive, max 999
- BuiltAreaSqm: Positive, max 999999
- YearOfConstruction: Between 1900 and current year

**Business Rules:**
- Optional but recommended for better analysis
- Solar/battery fields required if respective boolean is true
- Used for recommendation engine

---

## 3. Analysis Domain

### 3.1 AnalysisResults
**Description:** Calculated results from bill analysis

**Attributes:**
- `id` (UUID) - Primary key
- `billId` (UUID) - Foreign key to BillsData, unique

**Validation Results:**
- `calculatedTotal` (BigDecimal) - System-calculated bill total (R$)
- `discrepancyAmount` (BigDecimal) - Difference from bill total (R$)
- `discrepancyPercentage` (BigDecimal) - Percentage difference
- `validationStatus` (Enum) - MATCH, MINOR_DISCREPANCY, MAJOR_DISCREPANCY, ERROR

**Breakdown Calculations:**
- `energyCharge` (BigDecimal) - TE × consumption (R$)
- `distributionCharge` (BigDecimal) - TUSD × consumption (R$)
- `flagCharge` (BigDecimal) - Flag value × consumption (R$)
- `pisAmount` (BigDecimal) - PIS tax amount (R$)
- `cofinsAmount` (BigDecimal) - COFINS tax amount (R$)
- `icmsAmount` (BigDecimal) - ICMS tax amount (R$)
- `publicLightingAmount` (BigDecimal) - Public lighting (R$)
- `totalTaxes` (BigDecimal) - Sum of all taxes (R$)
- `totalBeforeTaxes` (BigDecimal) - Amount before taxes (R$)

**Efficiency Metrics:**
- `efficiencyScore` (Integer) - 0-100 rating
- `averageDailyConsumptionKwh` (BigDecimal) - Daily average
- `costPerKwh` (BigDecimal) - Effective cost (R$/kWh)
- `consumptionPerSqm` (BigDecimal) - kWh/m²/month, nullable

**Comparisons:**
- `regionalAverageConsumption` (BigDecimal) - Regional average, nullable
- `comparisonVsAverage` (BigDecimal) - Percentage vs average, nullable

**Metadata:**
- `createdAt` (Timestamp)
- `calculationVersion` (String) - Algorithm version used

**Relationships:**
- One-to-One with BillsData
- One-to-Many with Recommendations

**Validations:**
- EfficiencyScore: Between 0 and 100
- All monetary values: 2 decimal places
- DiscrepancyPercentage: Can be negative

**Business Rules:**
- Automatically created when bill is analyzed
- Immutable after creation (create new version for recalculation)
- Efficiency score based on multiple factors
- Validation status determines UI display

---

### 3.2 Recommendations
**Description:** Actionable recommendations for energy savings

**Attributes:**
- `id` (UUID) - Primary key
- `analysisId` (UUID) - Foreign key to AnalysisResults

**Recommendation Details:**
- `category` (Enum) - EQUIPMENT, BEHAVIOR, TARIFF, GENERATION, POWER_QUALITY
- `title` (String) - Short title (e.g., "Install LED Lighting")
- `description` (Text) - Detailed explanation
- `implementationSteps` (Text) - How to implement, nullable

**Financial Impact:**
- `potentialSavingsMonthly` (BigDecimal) - Estimated savings/month (R$)
- `potentialSavingsAnnual` (BigDecimal) - Estimated savings/year (R$)
- `investmentRequired` (BigDecimal) - Upfront cost (R$)
- `paybackMonths` (BigDecimal) - Time to recover investment
- `roiPercentage` (BigDecimal) - Return on investment

**Prioritization:**
- `priorityScore` (BigDecimal) - Algorithm-calculated priority
- `implementationDifficulty` (Enum) - EASY, MODERATE, DIFFICULT, PROFESSIONAL_REQUIRED

**Environmental Impact:**
- `co2ReductionKgPerYear` (BigDecimal) - CO2 savings, nullable

**Metadata:**
- `createdAt` (Timestamp)
- `recommendationVersion` (String) - Algorithm version

**Relationships:**
- Many-to-One with AnalysisResults

**Validations:**
- PotentialSavingsMonthly: Positive
- InvestmentRequired: Non-negative
- PaybackMonths: Positive
- PriorityScore: Between 0 and 100

**Business Rules:**
- Multiple recommendations per analysis
- Ordered by priority score
- Quick wins (low investment, fast payback) prioritized
- Some recommendations may conflict (alternative options)

---

### 3.3 Simulations
**Description:** Scenario simulations with improvements applied

**Attributes:**
- `id` (UUID) - Primary key
- `billId` (UUID) - Foreign key to BillsData
- `userId` (UUID) - Foreign key to User (creator)

**Simulation Details:**
- `simulationName` (String) - User-defined name
- `description` (Text) - Simulation description, nullable

**Adjustments:**
- `consumptionReductionPercent` (BigDecimal) - % reduction, 0-100
- `tariffModalitySimulated` (Enum) - Simulated tariff, nullable
- `solarGenerationKwh` (BigDecimal) - Simulated solar generation, nullable
- `improvementsApplied` (JSON) - Array of improvement IDs/details

**Results:**
- `simulatedConsumptionKwh` (BigDecimal) - After adjustments
- `simulatedTotalAmount` (BigDecimal) - Calculated bill total (R$)
- `savingsAmount` (BigDecimal) - Difference from original (R$)
- `savingsPercentage` (BigDecimal) - Percentage reduction

**Metadata:**
- `createdAt` (Timestamp)

**Relationships:**
- Many-to-One with BillsData
- Many-to-One with User

**Validations:**
- ConsumptionReductionPercent: Between 0 and 100
- SimulatedConsumptionKwh: Positive
- SavingsAmount: Can be negative (if simulation worse)

**Business Rules:**
- Multiple simulations per bill allowed
- User can compare different scenarios
- Simulations don't affect original bill data
- Can share simulation results

---

## 4. Reporting Domain

### 4.1 Reports
**Description:** Generated PDF reports

**Attributes:**
- `id` (UUID) - Primary key
- `userId` (UUID) - Foreign key to User (generator)
- `billId` (UUID) - Foreign key to BillsData, nullable (multi-bill reports)

**Report Details:**
- `reportType` (Enum) - BILL_ANALYSIS, COMPARISON, SIMULATION, PORTFOLIO
- `reportName` (String) - Generated or user-defined name
- `includeRecommendations` (Boolean) - Include recommendations section
- `includeTechnicalDetails` (Boolean) - Include engineering data
- `language` (String) - Report language

**File Information:**
- `fileUrl` (String) - S3/storage URL
- `fileSizeBytes` (Long) - File size
- `fileName` (String) - Original filename

**Access Control:**
- `isPublic` (Boolean) - Publicly accessible
- `shareToken` (String) - Token for sharing, nullable, unique

**Metrics:**
- `generationDate` (Timestamp) - When created
- `downloadCount` (Integer) - Times downloaded
- `lastDownloadedAt` (Timestamp) - Last download date, nullable

**Metadata:**
- `createdAt` (Timestamp)
- `expiresAt` (Timestamp) - Expiration date, nullable

**Relationships:**
- Many-to-One with User
- Many-to-One with BillsData (optional)

**Validations:**
- ReportType: Required, valid enum
- FileUrl: Required, valid URL
- ShareToken: Unique if present

**Business Rules:**
- Reports can expire (e.g., 30 days)
- Expired reports may be deleted from storage
- Share token allows anonymous access if public
- Download count used for analytics

---

## 5. Relationship Domain

### 5.1 ClientConsultantRelation
**Description:** Many-to-many relationship between consultants and clients

**Attributes:**
- `id` (Long) - Primary key
- `consultantId` (UUID) - Foreign key to Consultant
- `clientId` (UUID) - Foreign key to Client
- `relationshipStart` (Timestamp) - When relationship started
- `relationshipEnd` (Timestamp) - When ended, nullable
- `isActive` (Boolean) - Currently active
- `notes` (Text) - Consultant notes about client, nullable

**Metadata:**
- `createdAt` (Timestamp)
- `updatedAt` (Timestamp)

**Relationships:**
- Many-to-One with Consultant
- Many-to-One with Client

**Validations:**
- Unique combination of consultantId + clientId
- RelationshipEnd must be after RelationshipStart

**Business Rules:**
- Consultant can only manage bills for related clients
- Relationship can be deactivated but not deleted
- Client can have multiple consultants
- Consultant can have multiple clients

---

## 6. System Domain

### 6.1 TariffCache
**Description:** Cached tariff data from ANEEL API

**Attributes:**
- `id` (Long) - Primary key
- `utilityCompanyId` (Long) - Foreign key to UtilityCompany
- `aneelResolution` (String) - Resolution number

**Conventional Tariff:**
- `tusdConventional` (BigDecimal) - TUSD rate (R$/kWh)
- `teConventional` (BigDecimal) - TE rate (R$/kWh)

**White Tariff (if applicable):**
- `tusdWhitePeak` (BigDecimal) - TUSD peak rate
- `tusdWhiteOffPeak` (BigDecimal) - TUSD off-peak rate
- `tusdWhiteIntermediate` (BigDecimal) - TUSD intermediate rate
- `teWhitePeak` (BigDecimal) - TE peak rate
- `teWhiteOffPeak` (BigDecimal) - TE off-peak rate
- `teWhiteIntermediate` (BigDecimal) - TE intermediate rate

**Validity:**
- `validFrom` (Date) - Start date of tariff validity
- `validUntil` (Date) - End date, nullable
- `fetchedAt` (Timestamp) - When data was fetched
- `dataSource` (String) - "ANEEL_API" or "MANUAL"

**Metadata:**
- `createdAt` (Timestamp)

**Relationships:**
- Many-to-One with UtilityCompany

**Validations:**
- All rates: Positive, 5 decimal places
- ValidFrom: Required
- ValidUntil: Must be after ValidFrom

**Business Rules:**
- Cache expires after 24 hours
- If API unavailable, use most recent cached data
- New tariffs automatically invalidate old cache
- Manual entries allowed for admin

---

### 6.2 AuditLogs
**Description:** Audit trail for all significant actions

**Attributes:**
- `id` (UUID) - Primary key
- `userId` (UUID) - Foreign key to User, nullable (system actions)
- `actionType` (Enum) - CREATE, UPDATE, DELETE, LOGIN, LOGOUT, DOWNLOAD, etc.
- `entityType` (String) - Entity affected (e.g., "Bill", "User")
- `entityId` (String) - ID of affected entity
- `oldValues` (JSON) - Previous state, nullable
- `newValues` (JSON) - New state, nullable
- `ipAddress` (String) - Client IP address
- `userAgent` (String) - Browser/client info
- `actionResult` (Enum) - SUCCESS, FAILURE
- `errorMessage` (String) - Error details if failed, nullable
- `createdAt` (Timestamp)

**Relationships:**
- Many-to-One with User (optional)

**Validations:**
- ActionType: Required, valid enum
- EntityType: Required
- CreatedAt: Automatically set

**Business Rules:**
- Cannot be modified or deleted
- Retention period: 1 year minimum
- Admin actions always logged
- Sensitive data (passwords) never logged
- Used for compliance (LGPD)

---

### 6.3 Countries (Future)
**Description:** Country-specific configurations for internationalization

**Attributes:**
- `id` (Integer) - Primary key
- `code` (String) - ISO 3166-1 alpha-2 code (e.g., "BR")
- `name` (String) - Country name
- `currencyCode` (String) - ISO 4217 currency code
- `currencySymbol` (String) - Currency symbol (e.g., "R$")
- `defaultLanguage` (String) - Language code
- `dateFormat` (String) - Date format pattern
- `decimalSeparator` (String) - "." or ","
- `thousandsSeparator` (String) - "," or "."
- `isActive` (Boolean) - Currently supported

**Energy System Configuration:**
- `regulatoryAgencyName` (String) - e.g., "ANEEL"
- `regulatoryAgencyApiUrl` (String) - API base URL, nullable
- `energyUnit` (String) - "kWh" or other
- `powerUnit` (String) - "kW" or other

**Metadata:**
- `createdAt` (Timestamp)

**Validations:**
- Code: 2 chars, uppercase, unique
- CurrencyCode: 3 chars, uppercase

**Business Rules:**
- MVP: Only "BR" active
- Future: Expand to other countries
- Each country may have different tariff structures

---

## 7. Enumerations

### UserType
- CLIENT
- CONSULTANT
- ADMIN

### TariffModality
- CONVENTIONAL
- WHITE
- BLUE (Group A)
- GREEN (Group A)

### FlagType
- GREEN
- YELLOW
- RED_LEVEL_1
- RED_LEVEL_2

### ConnectionType
- SINGLE_PHASE
- TWO_PHASE
- THREE_PHASE

### InstallationClass
- RESIDENTIAL
- COMMERCIAL
- INDUSTRIAL
- RURAL
- PUBLIC_SERVICE

### ValidationStatus
- MATCH
- MINOR_DISCREPANCY (< 5%)
- MAJOR_DISCREPANCY (>= 5%)
- ERROR

### RecommendationCategory
- EQUIPMENT
- BEHAVIOR
- TARIFF
- GENERATION
- POWER_QUALITY
- INSULATION

### ImplementationDifficulty
- EASY
- MODERATE
- DIFFICULT
- PROFESSIONAL_REQUIRED

### ReportType
- BILL_ANALYSIS
- COMPARISON
- SIMULATION
- PORTFOLIO

### ActionType (Audit)
- CREATE
- UPDATE
- DELETE
- LOGIN
- LOGOUT
- DOWNLOAD
- SHARE
- EXPORT
- ADMIN_ACTION

### ActionResult
- SUCCESS
- FAILURE

---

## Summary

This comprehensive data model supports:
- ✅ **User management** with inheritance (Client, Consultant, Admin)
- ✅ **Bill data** with complete tariff and consumption information
- ✅ **Engineering data** for detailed analysis
- ✅ **Analysis and recommendations** with financial calculations
- ✅ **Simulations** for scenario planning
- ✅ **Report generation** and sharing
- ✅ **Relationship management** between consultants and clients
- ✅ **Caching** for external API data
- ✅ **Audit trail** for compliance
- ✅ **Internationalization** readiness

The model is designed for:
- **Scalability** - Handles growing user base and data volume
- **Flexibility** - Accommodates different use cases and user types
- **Integrity** - Strong relationships and validations
- **Compliance** - LGPD-ready with audit logs
- **Performance** - Proper indexing and caching
- **Extensibility** - Easy to add new features

