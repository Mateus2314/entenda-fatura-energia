# 05 - Technology Stack

## 1. Frontend Technologies

### 1.1 Core Framework
**React 18.x**
- Component-based architecture
- Virtual DOM for performance
- Large ecosystem and community
- Excellent documentation
- React Hooks for state management

**Why React:**
- Industry standard for modern web apps
- Rich ecosystem of libraries
- Strong community support
- Easy to find developers
- Excellent testing tools

### 1.2 UI Framework & Styling
**Tailwind CSS 3.x**
- Utility-first CSS framework
- Rapid UI development
- Consistent design system
- Responsive by default
- Small production bundle with purging

**Additional UI Libraries:**
- **Headless UI** - Unstyled, accessible components
- **Heroicons** - SVG icon library
- **React Icons** - Additional icon sets

**Why Tailwind:**
- Fast development iteration
- Consistent styling
- No CSS conflicts
- Easy customization
- Great with React

### 1.3 State Management
**React Context API + Hooks**
- Built-in solution
- No additional dependencies
- Sufficient for MVP complexity

**Future consideration:**
- Redux Toolkit (if state becomes complex)
- Zustand (lightweight alternative)

### 1.4 Routing
**React Router 6.x**
- Declarative routing
- Nested routes support
- Dynamic routing
- Code splitting support

### 1.5 Form Management
**React Hook Form**
- Performance optimized
- Easy validation
- Small bundle size
- TypeScript support
- Great DX (Developer Experience)

**Validation:**
- **Yup** or **Zod** - Schema validation

### 1.6 HTTP Client
**Axios**
- Promise-based
- Request/response interceptors
- Automatic JSON transformation
- Request cancellation
- Error handling

**Alternative:** Fetch API with wrapper

### 1.7 Charts & Data Visualization
**Recharts**
- React-based
- Responsive
- Composable components
- Good documentation

**Chart Types Needed:**
- Pie charts (bill breakdown)
- Bar charts (consumption trends)
- Line charts (historical data)
- Gauge charts (efficiency score)

### 1.8 Date Handling
**date-fns**
- Modular
- Lightweight
- Immutable
- Internationalization support

### 1.9 Internationalization (i18n)
**react-i18next**
- Translation management
- Language detection
- Pluralization
- Formatting (dates, numbers, currency)

### 1.10 Testing
**Unit & Component Testing:**
- **Jest** - Test runner
- **React Testing Library** - Component testing
- **MSW (Mock Service Worker)** - API mocking

**Coverage Target:** >75%

---

## 2. Backend Technologies

### 2.1 Core Framework
**Spring Boot 3.x**
- Opinionated, production-ready
- Auto-configuration
- Embedded server (Tomcat)
- Spring ecosystem integration
- Excellent for microservices

**Java Version:** Java 17 LTS (or Java 21 LTS)
- Long-term support
- Modern language features
- Performance improvements

**Why Spring Boot:**
- Industry standard for Java
- Comprehensive ecosystem
- Easy dependency management
- Production-ready features
- Excellent documentation

### 2.2 Build Tool
**Maven 3.x**
- Dependency management
- Build lifecycle
- Plugin ecosystem
- Multi-module projects support

**Alternative:** Gradle (if preferred)

### 2.3 Web Layer
**Spring Web MVC**
- RESTful APIs
- Request mapping
- Exception handling
- Content negotiation

**Annotations:**
- `@RestController`
- `@RequestMapping`
- `@GetMapping`, `@PostMapping`, etc.
- `@RequestBody`, `@ResponseBody`
- `@PathVariable`, `@RequestParam`

### 2.4 Security
**Spring Security**
- Authentication
- Authorization (role-based)
- JWT token support
- CORS configuration
- CSRF protection

**Password Encoding:**
- BCryptPasswordEncoder

**JWT Library:**
- **jjwt (Java JWT)** - Token generation and validation

### 2.5 Data Access Layer
**Spring Data JPA**
- Repository abstraction
- Query methods
- Custom queries with JPQL
- Pagination and sorting

**JPA Implementation:**
- **Hibernate** - ORM framework

**Why JPA:**
- Database agnostic
- Object-relational mapping
- Automatic schema generation (dev)
- Query optimization

### 2.6 Database
**PostgreSQL 15.x**
- Open source
- ACID compliant
- JSON support
- Full-text search
- Excellent performance
- Strong data integrity

**Why PostgreSQL:**
- Production-ready
- Free and open source
- Rich feature set
- Great community support
- Docker support

**Database Migration:**
- **Flyway** - Version control for database

### 2.7 Validation
**Jakarta Bean Validation**
- Annotations: `@NotNull`, `@Size`, `@Email`, `@Min`, `@Max`
- Custom validators
- Automatic validation

**Library:** Hibernate Validator

### 2.8 HTTP Client (External APIs)
**Spring WebClient** (from Spring WebFlux)
- Non-blocking
- Reactive streams
- Fluent API

**Alternative:** RestTemplate (if reactive not needed)

### 2.9 PDF Generation
**iText 7** or **Apache PDFBox**
- PDF creation
- Text and images
- Tables and layouts
- Custom fonts
- Digital signatures (future)

**Why iText:**
- Comprehensive features
- Good documentation
- Active development

**License Consideration:** AGPL (or commercial license)

### 2.10 Environment Variables
**Dotenv**
- **dotenv-java** library
- Load `.env` files
- Configuration management
- Secrets management

### 2.11 Caching
**Spring Cache Abstraction**
- Annotation-based: `@Cacheable`, `@CacheEvict`
- Multiple cache providers support

**Cache Provider:**
- **Caffeine** - In-memory cache (for single instance)
- **Redis** - Distributed cache (future for scaling)

### 2.12 Logging
**SLF4J + Logback**
- Structured logging
- Log levels (DEBUG, INFO, WARN, ERROR)
- File appenders
- JSON logging (for production)

### 2.13 Testing
**Unit Testing:**
- **JUnit 5** - Test framework
- **Mockito** - Mocking framework
- **AssertJ** - Fluent assertions

**Integration Testing:**
- **Testcontainers** - Docker containers for tests
- **@SpringBootTest** - Integration test support
- **MockMvc** - Controller testing

**API Mocking:**
- **WireMock** - HTTP service mocking

**Coverage Target:** >80%

### 2.14 API Documentation
**SpringDoc OpenAPI 3**
- Automatic Swagger UI generation
- API documentation
- Try-it-out functionality

**Alternative:** Springfox (older)

---

## 3. Database Design

### 3.1 Tables Structure

**users** (base table)
- id (PK, UUID)
- email (unique, indexed)
- password_hash
- user_type (CLIENT, CONSULTANT, ADMIN)
- created_at
- updated_at
- last_login
- is_active
- is_email_verified

**clients** (inherits users)
- user_id (FK to users, PK)
- full_name
- phone
- country_code (default: BR)
- language (default: pt-BR)

**consultants** (inherits users)
- user_id (FK to users, PK)
- full_name
- phone
- company_name
- cnpj
- company_logo_url
- business_address
- professional_license

**admins** (inherits users)
- user_id (FK to users, PK)
- full_name
- admin_level

**utility_companies**
- id (PK)
- name
- cnpj
- aneel_code
- state
- logo_url
- website
- phone
- is_active

**bills_data**
- id (PK, UUID)
- user_id (FK to users)
- client_id (FK to clients, nullable - for consultants)
- utility_company_id (FK)
- reference_month
- reference_year
- consumer_unit_number
- installation_number
- total_consumption_kwh
- on_peak_consumption_kwh
- off_peak_consumption_kwh
- reactive_energy_kvarh
- power_demand_kw
- tariff_modality
- tusd_rate
- te_rate
- flag_type
- flag_value
- pis_rate
- cofins_rate
- icms_rate
- public_lighting_value
- total_amount
- previous_reading
- current_reading
- billing_days
- created_at
- updated_at

**engineering_data**
- id (PK, UUID)
- bill_id (FK to bills_data)
- voltage_level
- connection_type
- installed_power_kw
- circuit_breaker_rating
- transformer_capacity
- power_factor
- installation_class
- installation_subclass
- typical_usage_hours
- occupants_count
- built_area_sqm
- building_type
- has_solar_panels
- has_battery_storage
- created_at
- updated_at

**analysis_results**
- id (PK, UUID)
- bill_id (FK to bills_data)
- calculated_total
- discrepancy_percentage
- validation_status
- efficiency_score
- average_daily_consumption
- cost_per_kwh
- energy_charge
- distribution_charge
- flag_charge
- pis_amount
- cofins_amount
- icms_amount
- public_lighting_amount
- total_taxes
- created_at

**recommendations**
- id (PK, UUID)
- analysis_id (FK to analysis_results)
- category
- title
- description
- potential_savings_monthly
- investment_required
- payback_months
- priority_score
- implementation_difficulty
- environmental_impact_co2_kg

**simulations**
- id (PK, UUID)
- bill_id (FK to bills_data)
- user_id (FK to users)
- simulation_name
- consumption_reduction_percent
- tariff_modality_simulated
- solar_generation_kwh
- improvements_applied (JSON)
- simulated_consumption_kwh
- simulated_total_amount
- savings_amount
- savings_percent
- created_at

**reports**
- id (PK, UUID)
- user_id (FK to users)
- bill_id (FK to bills_data, nullable for multi-bill)
- report_type
- file_url
- file_size_bytes
- generation_date
- download_count
- is_public
- share_token

**client_consultant_relation**
- id (PK)
- consultant_id (FK to consultants)
- client_id (FK to clients)
- created_at
- is_active

**audit_logs**
- id (PK, UUID)
- user_id (FK to users)
- action_type
- entity_type
- entity_id
- old_values (JSON)
- new_values (JSON)
- ip_address
- user_agent
- created_at

**tariff_cache**
- id (PK)
- utility_company_id (FK)
- aneel_resolution
- tusd_conventional
- te_conventional
- tusd_white_peak
- tusd_white_off_peak
- te_white_peak
- te_white_off_peak
- valid_from
- valid_until
- fetched_at
- data_source

**countries** (for future internationalization)
- id (PK)
- code (ISO 3166-1 alpha-2)
- name
- currency_code
- language_default
- is_active

### 3.2 Indexes
- users.email
- bills_data.user_id
- bills_data.reference_month, reference_year
- analysis_results.bill_id
- audit_logs.user_id, created_at
- tariff_cache.utility_company_id, valid_from

### 3.3 Relationships
- One-to-Many: users → bills_data
- One-to-One: bills_data → engineering_data
- One-to-One: bills_data → analysis_results
- One-to-Many: analysis_results → recommendations
- Many-to-Many: consultants ↔ clients (through client_consultant_relation)

---

## 4. DevOps & Infrastructure

### 4.1 Containerization
**Docker**
- Dockerfile for backend
- Dockerfile for frontend
- docker-compose.yml for local development

**Images:**
- Backend: openjdk:17-slim
- Frontend: node:18-alpine (build) + nginx:alpine (serve)
- Database: postgres:15-alpine

### 4.2 Environment Management
**.env files**
- .env.development
- .env.production
- Never commit to version control
- Use .env.example for template

**Environment Variables:**
```
# Database
DB_HOST=
DB_PORT=
DB_NAME=
DB_USER=
DB_PASSWORD=

# JWT
JWT_SECRET=
JWT_EXPIRATION=

# ANEEL API
ANEEL_API_BASE_URL=
ANEEL_API_KEY= (if needed)

# Email (future)
SMTP_HOST=
SMTP_PORT=
SMTP_USER=
SMTP_PASSWORD=

# File Storage
STORAGE_PATH=
MAX_FILE_SIZE=

# URLs
FRONTEND_URL=
BACKEND_URL=
```

### 4.3 Version Control
**Git**
- GitHub or GitLab
- Branch strategy: Gitflow or GitHub Flow
- Protected main branch
- Pull request reviews
- CI/CD integration

**Branching:**
- main/master - production
- develop - integration
- feature/* - new features
- bugfix/* - bug fixes
- hotfix/* - production fixes

### 4.4 CI/CD (Future)
**GitHub Actions** or **GitLab CI**
- Automated testing on PR
- Build and push Docker images
- Deploy to staging
- Deploy to production (manual approval)

**Pipeline stages:**
1. Lint code
2. Run unit tests
3. Run integration tests
4. Build Docker images
5. Push to registry
6. Deploy to environment

### 4.5 Hosting (Future Options)
**Options for MVP:**
- **AWS** - EC2, RDS, S3
- **DigitalOcean** - Droplets, Managed Databases
- **Heroku** - Quick deployment (more expensive)
- **Railway** - Modern platform

**Services needed:**
- Compute (backend + frontend)
- Database (PostgreSQL)
- Storage (PDF files)
- Domain and SSL

---

## 5. Testing Technologies

### 5.1 End-to-End Testing
**Robot Framework**
- Keyword-driven testing
- Test case readability
- SeleniumLibrary for browser automation
- Support for parallel execution

**Why Robot:**
- Non-developer friendly syntax
- Excellent reporting
- Extensible with Python
- Large library ecosystem

**Test Browsers:**
- Chrome (primary)
- Firefox (secondary)
- Edge (if time allows)

### 5.2 Test Structure
```
tests/
  robot/
    resources/
      keywords.robot
      variables.robot
    testcases/
      01_authentication.robot
      02_bill_entry.robot
      03_analysis.robot
      04_simulation.robot
      05_reports.robot
      06_admin.robot
    libraries/
      custom_keywords.py
```

### 5.3 Test Data Management
- Test fixtures in JSON
- Database seeding for tests
- Mock data generators
- Test user accounts

---

## 6. Development Tools

### 6.1 IDE
**Recommended:**
- IntelliJ IDEA (backend) - your current IDE
- VS Code (frontend/full-stack)
- DataGrip or DBeaver (database)

### 6.2 API Testing
- Postman or Insomnia
- Swagger UI (built-in)
- curl commands

### 6.3 Code Quality
**Backend:**
- Checkstyle - Code style
- SpotBugs - Bug detection
- SonarLint - Code quality

**Frontend:**
- ESLint - JavaScript linting
- Prettier - Code formatting

### 6.4 Dependency Management
**Security:**
- Dependabot (GitHub) - Dependency updates
- Snyk - Security vulnerability scanning

---

## 7. External APIs & Services

### 7.1 ANEEL API
**Base URL:** https://dadosabertos.aneel.gov.br

**Endpoints Used:**
- Tariffs by distributor
- Flag values
- Resolution history

**Rate Limiting:** None specified (be respectful)

**Caching:** 24 hours

### 7.2 Tax APIs (Future)
**Serpro - Brazilian Federal Revenue**
- PIS/COFINS rates
- Tax calculation rules

**SEFAZ APIs by State**
- ICMS rates
- State-specific rules

### 7.3 Email Service (Future)
**Options:**
- SendGrid
- Mailgun
- AWS SES
- Postmark

**Use cases:**
- Email verification
- Password reset
- Report delivery
- Notifications

### 7.4 File Storage (Future)
**Options:**
- AWS S3
- DigitalOcean Spaces
- Local filesystem (MVP)

**Use cases:**
- PDF reports
- User uploads (logos, bills in future)

---

## 8. Monitoring & Observability (Future)

### 8.1 Application Monitoring
- **Sentry** - Error tracking
- **New Relic** or **Datadog** - APM
- Spring Boot Actuator - Health checks

### 8.2 Logging
- Structured logging (JSON)
- Log aggregation: ELK Stack or CloudWatch
- Log retention policy

### 8.3 Metrics
- Request rates
- Response times
- Error rates
- Database query performance
- Cache hit rates

---

## 9. Security Tools

### 9.1 Secrets Management
- Environment variables
- AWS Secrets Manager (future)
- HashiCorp Vault (future)

### 9.2 HTTPS/TLS
- Let's Encrypt (free SSL)
- Certificate auto-renewal

### 9.3 Security Headers
- Content-Security-Policy
- X-Frame-Options
- X-Content-Type-Options
- Strict-Transport-Security

---

## 10. Documentation Tools

### 10.1 Code Documentation
**Backend:**
- JavaDoc comments
- OpenAPI/Swagger annotations

**Frontend:**
- JSDoc comments
- Storybook (component library - future)

### 10.2 Project Documentation
- **Markdown** files in `/docs`
- README.md for each module
- Architecture diagrams (Mermaid)
- API documentation (Swagger UI)

---

## Technology Decision Summary

| Layer | Technology | Justification |
|-------|-----------|---------------|
| Frontend | React + Tailwind | Modern, fast development, large ecosystem |
| Backend | Spring Boot + Java 17 | Enterprise-grade, comprehensive features |
| Database | PostgreSQL | Reliable, feature-rich, open source |
| Testing | JUnit + Jest + Robot | Comprehensive coverage at all levels |
| PDF | iText | Professional quality, feature-rich |
| Cache | Caffeine/Redis | Performance optimization |
| Security | Spring Security + JWT | Industry standard, proven |
| Containerization | Docker | Consistency across environments |

This stack balances:
- ✅ **Maturity** - Production-ready technologies
- ✅ **Performance** - Fast and scalable
- ✅ **Developer Experience** - Good tooling and documentation
- ✅ **Community** - Active support and resources
- ✅ **Cost** - Open source where possible
- ✅ **Maintainability** - Standard patterns and practices

