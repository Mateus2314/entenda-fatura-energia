# Scope Documentation / Documentação do Escopo

This directory contains detailed scope documentation for the **Understand Your Electricity Bill** project.

Este diretório contém a documentação detalhada do escopo do projeto **Entenda Sua Fatura de Energia**.

---

## 📁 Document Structure / Estrutura dos Documentos

### [01 - Objective](01_objective.md)
**What's inside:** Project goals, business value, success metrics, problem statement, solution approach, and long-term vision.

**O que contém:** Objetivos do projeto, valor de negócio, métricas de sucesso, problema a ser resolvido, abordagem da solução e visão de longo prazo.

---

### [02 - Features](02_features.md)
**What's inside:** Comprehensive list of all features including:
- User management & authentication
- Bill data entry & management
- External API integrations (ANEEL, taxes)
- Analysis & calculations engine
- Simulation engine
- Recommendations system
- Dashboard & visualization
- Report generation
- Internationalization
- Testing strategy
- Security & compliance
- Performance & scalability
- Monitoring & observability

**O que contém:** Lista completa de todas as funcionalidades incluindo autenticação, entrada de dados, integrações externas, análise, simulações, recomendações, dashboards, relatórios, testes, segurança e mais.

---

### [03 - User Flows](03_user_flows.md)
**What's inside:** Detailed step-by-step flows for:
- Client registration and first bill analysis
- Consultant registration and client management
- Admin user management
- Bill comparison
- Tariff modality recommendation
- Recommendation implementation tracking
- Multi-country support (future)
- Error handling flows
- Notification flows (future)

**O que contém:** Fluxos detalhados passo-a-passo para cada tipo de usuário e funcionalidade principal do sistema.

---

### [04 - Out of Scope](04_out_of_scope.md)
**What's inside:** Clear definition of what will NOT be included in MVP:
- Advanced features (OCR, ML, smart home integration)
- International features (multi-country initially)
- Advanced analysis (weather correlation, peer benchmarking)
- Extended user features (MFA, SSO, team collaboration)
- Reporting features (scheduled reports, interactive reports)
- Integration features (utility APIs, payment, CRM)
- Mobile applications (native iOS/Android)
- Financial features (subscriptions, commissions)
- Support features (live chat, chatbot)
- And more...

**O que contém:** Definição clara do que NÃO será incluído no MVP para manter o foco e garantir entrega no prazo.

---

### [05 - Technologies](05_technologies.md)
**What's inside:** Complete technology stack specification:
- **Frontend:** React, Tailwind CSS, React Router, Axios, Recharts, Jest
- **Backend:** Spring Boot, Java 17, Maven, Spring Security, JPA/Hibernate
- **Database:** PostgreSQL, Flyway migrations
- **PDF Generation:** iText 7
- **Testing:** JUnit 5, Mockito, Robot Framework, Testcontainers, WireMock
- **DevOps:** Docker, docker-compose, Git
- **External APIs:** ANEEL, tax services
- Database schema design
- Development tools
- Monitoring solutions

**O que contém:** Stack tecnológico completo com justificativas para cada escolha.

---

### [06 - Target Audience](06_target_audience.md)
**What's inside:** Detailed user segmentation:
- **Primary Users:**
  - Clients (end consumers)
  - Consultants (energy professionals)
  - Companies (consulting firms)
  - Administrators (platform managers)
- Demographics and characteristics
- Pain points and goals
- Usage patterns
- User personas (Maria, Carlos, Ana, João)
- Market segmentation
- User acquisition and retention strategies

**O que contém:** Segmentação detalhada do público-alvo com personas, características, dores e estratégias de aquisição.

---

### [07 - Data Models](07_data_models.md)
**What's inside:** Complete data model specification:
- **User Domain:** User, Client, Consultant, Admin entities
- **Electricity Bill Domain:** UtilityCompany, BillsData, EngineeringData
- **Analysis Domain:** AnalysisResults, Recommendations, Simulations
- **Reporting Domain:** Reports
- **Relationship Domain:** ClientConsultantRelation
- **System Domain:** TariffCache, AuditLogs, Countries
- All entity attributes with types
- Relationships and foreign keys
- Validations and constraints
- Business rules per entity
- Enumerations

**O que contém:** Modelo de dados completo com todas as entidades, atributos, relacionamentos, validações e regras de negócio.

---

### [08 - API Endpoints](08_api_endpoints.md)
**What's inside:** Complete REST API specification:
- **Authentication:** register, login, verify email, password reset, refresh token
- **User Management:** profile, update, change password
- **Utility Companies:** list, get details
- **Bills Management:** create, read, update, delete, list
- **Engineering Data:** add, get
- **Analysis:** analyze bill, get results, recommendations
- **Simulations:** create, list, get, delete
- **Reports:** generate, list, download, share
- **Consultant Management:** add clients, list clients, dashboard
- **Admin Management:** users, audit logs, metrics
- **External APIs:** ANEEL tariffs, tax rates
- Request/response examples
- Error handling
- Rate limiting
- Pagination

**O que contém:** Especificação completa da API REST com todos os endpoints, exemplos de request/response, tratamento de erros e limites.

---

### [09 - Business Rules](09_business_rules.md)
**What's inside:** Comprehensive business rules and validations:
- **User Management:** registration, authentication, password policies
- **Bill Data:** creation, consumption validation, tariff validation, total calculation
- **Analysis:** generation, efficiency score, recommendations, savings calculation
- **Simulations:** creation, validation
- **Reports:** generation, sharing, security
- **Consultant-Client Relationships:** permissions, onboarding
- **Admin:** permissions, audit logging
- **Data Privacy:** LGPD compliance, data retention, sharing policies
- **Tariff Rules:** flags, modalities, net metering
- **Performance:** rate limiting, file sizes, data limits
- **Edge Cases:** partial months, estimated readings, special scenarios

**O que contém:** Regras de negócio completas e validações para todas as funcionalidades do sistema.

---

## 🎯 How to Use This Documentation

### For Developers:
1. Start with **01 - Objective** to understand the big picture
2. Read **02 - Features** to know what needs to be built
3. Study **07 - Data Models** before database implementation
4. Reference **08 - API Endpoints** during backend development
5. Follow **09 - Business Rules** for validation logic
6. Use **03 - User Flows** to understand user journeys
7. Check **05 - Technologies** for implementation decisions

### For Product Managers:
1. **01 - Objective** - Vision and success metrics
2. **06 - Target Audience** - User segmentation and personas
3. **02 - Features** - Feature set and priorities
4. **04 - Out of Scope** - What's deferred to later phases
5. **03 - User Flows** - User experience design

### For QA/Testers:
1. **03 - User Flows** - Test scenarios
2. **09 - Business Rules** - Validation test cases
3. **08 - API Endpoints** - API testing specifications
4. **07 - Data Models** - Data integrity tests

### For Designers:
1. **06 - Target Audience** - User personas and needs
2. **03 - User Flows** - User journey mapping
3. **02 - Features** - UI/UX requirements

---

## 📊 Documentation Stats

- **Total Documents:** 9
- **Total Pages:** ~150+ (estimated)
- **Coverage:**
  - ✅ Business objectives and vision
  - ✅ Complete feature specifications
  - ✅ Detailed user flows
  - ✅ Technology stack
  - ✅ Data models
  - ✅ API specifications
  - ✅ Business rules
  - ✅ Target audience analysis
  - ✅ Out of scope items

---

## 🔄 Document Maintenance

**Last Updated:** December 2024

**Update Frequency:**
- Review after each sprint
- Update when requirements change
- Version control via Git

**Responsibility:**
- Product Owner: Business rules, features, audience
- Tech Lead: Technologies, data models, API specs
- All team: User flows, edge cases

---

## 📝 Contributing

When updating these documents:
1. Maintain consistent formatting
2. Update related documents (cross-references)
3. Add examples where helpful
4. Keep language clear and concise
5. Version control all changes
6. Review with team before committing

---

## 🔗 Related Documentation

- [../architecture.md](../architecture.md) - System architecture
- [../diagram.md](../diagram.md) - System flow diagram
- [../requirements.md](../requirements.md) - Original requirements
- [../sprints.md](../sprints.md) - Sprint planning
- [../../README.md](../../README.md) - Project README

---

## 📞 Questions?

If you have questions about this documentation:
1. Check if answered in related docs
2. Review Git history for context
3. Discuss with team in daily standup
4. Update docs with answers for future reference

---

**Happy Building! 🚀 / Bom Desenvolvimento! 🚀**

