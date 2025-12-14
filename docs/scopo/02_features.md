# 02 - Detailed Features

## 1. User Management & Authentication

### 1.1 User Registration
- **Client Registration**
  - Email, password, name, phone (optional)
  - Email verification
  - Terms of service acceptance
  - Privacy policy acceptance
  
- **Consultant/Company Registration**
  - All client fields plus:
  - Company name
  - CNPJ (Brazilian company ID)
  - Company logo upload
  - Business address
  - Professional license number (if applicable)
  
- **Admin Registration**
  - Created only by existing admins
  - Special permission flags
  - Access level configuration

### 1.2 Authentication & Authorization
- JWT-based authentication
- Secure password storage (BCrypt)
- Password reset via email
- Session management
- Role-based access control (RBAC)
- Multi-factor authentication (future)

### 1.3 User Profile Management
- Update personal information
- Change password
- Upload/change profile picture
- Manage notification preferences
- View activity history
- Export personal data (LGPD compliance)

---

## 2. Bill Data Entry & Management

### 2.1 Utility Company Selection
- Searchable dropdown of all Brazilian distributors
- Integration with ANEEL distributor list
- Display distributor logo and information
- Save as default for user

### 2.2 Bill Information Input
**Required Fields:**
- Reference month/year
- Utility company (distributor)
- Consumer unit number
- Installation number
- Total consumption (kWh)
- Consumption on-peak (kWh) - if applicable
- Consumption off-peak (kWh) - if applicable
- Reactive energy (kVArh) - if applicable
- Power demand (kW) - for Group A consumers
- Tariff modality (Conventional, White, Blue, Green)

**Tariff Components:**
- TUSD (Distribution System Usage Tariff)
- TE (Energy Tariff)
- Public lighting contribution
- PIS/PASEP (%)
- COFINS (%)
- ICMS (%) - state tax

**Flags:**
- Current flag (Green, Yellow, Red Level 1, Red Level 2)
- Flag value

**Total Values:**
- Total amount charged
- Previous reading
- Current reading
- Days in billing period

### 2.3 Data Validation
- Format validation (numbers, dates, percentages)
- Range validation (consumption within reasonable limits)
- Consistency checks (reading difference vs consumption)
- Cross-validation with ANEEL tariffs
- Duplicate bill detection
- Warning for unusual values

### 2.4 Automatic Data Population
- Fetch current tariffs from ANEEL API based on distributor
- Auto-calculate flag charges
- Pre-fill tax rates based on state
- Suggest values based on user history

---

## 3. Engineering Data Input

### 3.1 Installation Characteristics
- Voltage level
- Connection type (single-phase, two-phase, three-phase)
- Installed power (kW)
- Circuit breaker rating
- Transformer capacity (if applicable)
- Power factor
- Installation class (residential, commercial, industrial, rural)
- Subclass (if applicable)

### 3.2 Consumption Patterns
- Typical usage hours
- Number of occupants
- Main equipment and appliances
- Air conditioning details (quantity, BTU, usage hours)
- Electric shower (power, usage time)
- Pool pump (if applicable)
- Electric heating systems
- Solar panels (if installed)
- Battery storage (if installed)

### 3.3 Building Information
- Built area (m²)
- Building type
- Insulation level
- Window area and orientation
- Roof type and color
- Year of construction

---

## 4. External API Integrations

### 4.1 ANEEL Tariffs API
**Endpoint:** https://dadosabertos.aneel.gov.br/dataset/tarifas-distribuidoras-energia-eletrica

**Data Retrieved:**
- Current TUSD rates by distributor
- Current TE rates by distributor
- Tariff validity dates
- Resolution numbers
- Historical tariff data
- Flag values and rules

**Caching Strategy:**
- Cache tariffs for 24 hours
- Invalidate cache on new ANEEL resolution
- Store in database for offline access
- Version control for tariff changes

### 4.2 Tax Information API
**Source:** Serpro/RFB (Brazilian Federal Revenue)

**Data Retrieved:**
- Current PIS rates
- Current COFINS rates
- ICMS rates by state
- Tax calculation rules
- Special tax regimes

### 4.3 Future Integrations
- Currency exchange API (for international expansion)
- Weather API (for consumption correlation)
- Energy market price API
- International tariff databases

---

## 5. Analysis & Calculations

### 5.1 Bill Breakdown Analysis
**Calculate and display separately:**
- Energy consumption charge (TE × consumption)
- Distribution charge (TUSD × consumption)
- Flag charge
- Public lighting contribution
- PIS/PASEP amount
- COFINS amount
- ICMS amount
- Total before taxes
- Total taxes
- Final total

**Validation:**
- Compare calculated total vs bill total
- Highlight discrepancies
- Explain differences
- Flag potential billing errors

### 5.2 Consumption Analysis
- Average daily consumption
- Peak consumption periods (if data available)
- Consumption trends (if historical data)
- Comparison with similar profiles
- Efficiency rating (kWh/m²/month)
- Cost per kWh (effective rate)
- Comparison with regional average

### 5.3 Tariff Analysis
- Current tariff structure explanation
- Comparison between available tariff modalities
- Recommend optimal tariff for user profile
- Calculate savings with tariff change
- Show break-even analysis

### 5.4 Power Factor Analysis (Group A)
- Calculate current power factor
- Identify reactive energy penalties
- Recommend capacitor bank sizing
- Calculate ROI for power factor correction

---

## 6. Simulation Engine

### 6.1 Scenario Simulation
**User can simulate:**
- Consumption reduction (by percentage)
- Tariff modality change
- Installation of solar panels (generation)
- Addition/removal of equipment
- Change in usage patterns
- Power factor improvement
- Energy efficiency improvements

### 6.2 Improvement Selection
**Available improvements:**
- LED lighting retrofit
- Efficient air conditioning
- Solar water heating
- Thermal insulation
- Time-of-use optimization
- Power factor correction
- Variable speed drives
- Efficient motors
- Smart thermostats
- Energy management system

**For each improvement:**
- Investment cost estimate
- Monthly savings estimate
- Payback period
- ROI calculation
- Environmental impact (CO2 reduction)

### 6.3 Comparison Engine
- Side-by-side comparison: Current vs Simulated
- Visual charts and graphs
- Detailed breakdown of changes
- Cumulative savings projection (1, 5, 10 years)
- Export comparison to PDF/Excel

---

## 7. Recommendations System

### 7.1 Automatic Recommendations
**Based on analysis, system suggests:**
- Quick wins (low cost, fast payback)
- Medium-term investments
- Long-term strategic improvements
- Behavioral changes
- Tariff optimization

**Recommendation categories:**
- Consumption reduction
- Tariff optimization
- Equipment upgrade
- Usage pattern change
- Energy generation
- Power quality improvement

### 7.2 Prioritization Algorithm
**Recommendations ranked by:**
- Potential savings (%)
- Investment required
- Payback period
- Implementation difficulty
- Environmental impact
- User profile match

### 7.3 Customization
- Filter by budget
- Filter by payback period
- Filter by category
- Sort by different criteria
- Save favorite recommendations

---

## 8. Dashboard & Visualization

### 8.1 Client Dashboard
**Widgets:**
- Current month summary
- Consumption trend chart
- Cost breakdown pie chart
- Savings opportunities (top 3)
- Efficiency score gauge
- Comparison with previous months
- Upcoming bill prediction
- Recommendations carousel

**Features:**
- Customizable widget layout
- Export dashboard to PDF
- Share dashboard link
- Mobile-responsive design

### 8.2 Consultant Dashboard
**Additional widgets:**
- Client list with status
- Portfolio overview (total clients, total savings)
- Pending analyses
- Reports generated this month
- Client consumption aggregation
- Revenue opportunity tracking
- Task management

**Client management:**
- Add new client
- Assign bills to clients
- Bulk operations
- Client groups/tags
- Notes and comments
- Activity timeline

### 8.3 Admin Dashboard
**System widgets:**
- User statistics (by type)
- System health metrics
- API usage statistics
- Error logs summary
- Recent registrations
- Active sessions
- Storage usage
- Database performance

**Management tools:**
- User search and filter
- Bulk user operations
- System configuration
- Audit log viewer
- Backup management
- Integration status

---

## 9. Report Generation

### 9.1 PDF Report Structure
**For Clients:**
- Cover page
- Executive summary
- Bill validation section
- Detailed breakdown
- Consumption analysis
- Recommendations (prioritized)
- Simulation results
- Glossary of terms
- Contact information

**For Consultants:**
- Custom cover page with company branding
- Company logo and information
- Professional layout
- Client information
- Multiple clients in single report (optional)
- Comparison tables
- Technical details
- Consultant recommendations
- Terms and conditions
- Digital signature option

### 9.2 Report Customization
- Choose sections to include
- Select language (PT-BR, EN - future)
- Apply company theme/colors
- Add custom notes
- Include/exclude technical details
- Select chart types

### 9.3 Report Delivery
- Download as PDF
- Email to recipient
- Generate shareable link
- Schedule automatic reports (future)
- Archive in user account

---

## 10. Internationalization (i18n)

### 10.1 Language Support
**Phase 1 (MVP):**
- Portuguese (Brazil) - pt-BR

**Phase 2:**
- English (US) - en-US
- Spanish (Spain/Latin America) - es-ES

**Phase 3:**
- Other languages based on demand

### 10.2 Localization Requirements
- All UI text translatable
- Date/time formats by locale
- Number formats (decimal separators)
- Currency symbols and formatting
- Measurement units (kWh, kW, m²)
- Address formats
- Phone number formats

### 10.3 Country-Specific Adaptations
**For each country:**
- Local utility companies database
- Local tariff structure
- Local tax rules
- Local regulations
- Local API integrations
- Local payment methods (future)

---

## 11. Testing Strategy

### 11.1 Unit Tests (Backend)
- JUnit 5 for all services
- Mockito for dependencies
- Test coverage > 80%
- Test all calculation algorithms
- Test validation rules
- Test error handling

### 11.2 Unit Tests (Frontend)
- Jest for React components
- React Testing Library
- Test user interactions
- Test API integration
- Test form validations
- Test coverage > 75%

### 11.3 Integration Tests
- Testcontainers for database tests
- Test API endpoints
- Test service layer integration
- Test database operations
- Test external API integration (mocked)

### 11.4 API Mocking
- WireMock for ANEEL API
- Mock tax APIs
- Simulate various responses
- Test error scenarios
- Test rate limiting

### 11.5 End-to-End Tests
- Robot Framework for E2E
- Test complete user flows
- Test all user types (client, consultant, admin)
- Test cross-browser compatibility
- Test mobile responsiveness

### 11.6 Performance Tests
- Load testing (future)
- Stress testing (future)
- API response time monitoring
- Database query optimization

---

## 12. Security & Compliance

### 12.1 Authentication Security
- Secure password hashing (BCrypt with salt)
- JWT tokens with expiration
- Refresh token mechanism
- Session timeout
- Brute force protection
- Account lockout after failed attempts

### 12.2 Data Protection
- Encryption at rest (sensitive data)
- Encryption in transit (HTTPS/TLS)
- Environment variables for secrets
- No credentials in code
- Secure file storage
- Input sanitization

### 12.3 LGPD Compliance (Brazilian Data Protection Law)
- User consent management
- Data access request handling
- Data deletion request handling
- Data portability (export)
- Privacy policy
- Terms of service
- Cookie policy

### 12.4 Audit Trail
- Log all user actions
- Log admin operations
- Log data changes
- Log authentication events
- Log API calls
- Retention policy

---

## 13. Performance & Scalability

### 13.1 Caching Strategy
- Redis for session data
- Cache ANEEL tariffs (24h)
- Cache calculation results
- CDN for static assets
- Browser caching headers

### 13.2 Database Optimization
- Proper indexing
- Query optimization
- Connection pooling
- Read replicas (future)
- Archival strategy

### 13.3 API Optimization
- Rate limiting
- Request throttling
- Pagination
- Async operations
- Batch operations

---

## 14. Monitoring & Observability

### 14.1 Application Monitoring
- Error tracking (future: Sentry)
- Performance monitoring
- API response times
- Database query times
- User behavior analytics

### 14.2 Infrastructure Monitoring
- Container health
- Resource usage (CPU, memory, disk)
- Network performance
- Backup verification

### 14.3 Business Metrics
- User registrations
- Reports generated
- API usage
- Feature adoption
- User engagement

