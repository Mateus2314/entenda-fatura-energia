# Business Rules & Validations

## 1. User Management

### Registration
- Email unique, valid format (RFC 5322)
- Password: min 8 chars, 1 uppercase, 1 lowercase, 1 number, 1 special
- Consultant must provide valid CNPJ (14 digits)
- Admin accounts created by admins only
- Email verification required before full access

### Authentication
- Max 5 failed login attempts in 15 min → 30 min lockout
- JWT token expires after 24h, refresh token after 30 days
- Email must be verified to login

### Password Management
- Reset token valid for 1 hour
- Cannot reuse last 3 passwords
- Old password required to change

---

## 2. Bill Data

### Required Fields
- Utility company, reference month/year, total consumption (kWh), tariff modality, total amount

### Validations
- Consumption: 0 < kWh < 999,999
- If readings provided: current > previous
- Reading difference should match consumption (±5% tolerance, warn if not)
- Billing days: 1-60
- Monetary values ≥ 0
- Tariff rates > 0
- Tax percentages: 0-100

### Warnings
- Consumption >3x user's average or <50% average
- Readings don't match consumption
- Billing days unusual (<25 or >35)

---

## 3. Tariff & Tax Rules

### Tariff Validation
- Fetch from ANEEL API, use cached if unavailable (with disclaimer)
- Manual override allowed (with notation)
- TUSD and TE rates: 0.05 - 2.00 R$/kWh
- Flag values match current ANEEL rates

### Tax Calculation Order
1. Calculate energy + distribution + flag = subtotal
2. Apply PIS (1.65%) and COFINS (7.6%) on subtotal
3. Calculate ICMS (12-25% by state) on (subtotal + PIS + COFINS)
4. Add public lighting

### Total Calculation Status
- **MATCH**: Difference < 1%
- **MINOR_DISCREPANCY**: 1-5%
- **MAJOR_DISCREPANCY**: > 5%

**Formula:**
```
energyCharge = consumption × teRate
distributionCharge = consumption × tusdRate
flagCharge = consumption × flagValue
subtotal1 = energyCharge + distributionCharge + flagCharge

pisAmount = subtotal1 × 0.0165
cofinsAmount = subtotal1 × 0.076
subtotal2 = subtotal1 + pisAmount + cofinsAmount

icmsBase = subtotal2 / (1 - icmsRate)
icmsAmount = icmsBase - subtotal2

total = icmsBase + publicLighting
```

---

## 4. Analysis Rules

### Efficiency Score (0-100)
**Factors:**
- Cost per kWh (30%)
- Consumption per m² if available (25%)
- Power factor if available (20%)
- Comparison with regional average (25%)

**Interpretation:**
- 90-100: Excellent
- 75-89: Good
- 60-74: Average
- 40-59: Below average
- 0-39: Poor

### Recommendations
- Min 3, max 10 recommendations
- Prioritized by: savings, investment, payback, difficulty
- Categories: EQUIPMENT, BEHAVIOR, TARIFF, GENERATION, POWER_QUALITY
- Conservative estimates preferred

### Savings Calculation
- Based on historical data, benchmarks, engineering calculations
- Savings cannot exceed current bill amount
- Payback period = investment / monthlySavings
- ROI = (annualSavings / investment) × 100

---

## 5. Simulation Rules

### Adjustable Parameters
- Consumption reduction (0-100%)
- Tariff modality change
- Solar generation (kWh/month)
- Specific improvements

### Validations
- Consumption reduction cannot result in negative consumption
- Solar generation cannot exceed consumption
- Tariff change only to available options

### Warnings
- Aggressive assumptions (>50% reduction)
- Unrealistic solar generation

---

## 6. Report Generation

### Rules
- PDF format only (MVP)
- Reports stored for 90 days
- Consultant reports include company branding

### Configurable Sections
- Cover, executive summary, validation, breakdown, consumption analysis, efficiency metrics, recommendations, simulations, technical details, glossary

### Sharing
- Share link expires in 30 days (default)
- Public or private (requires share token)
- Sensitive data can be redacted

---

## 7. Consultant-Client Relationships

### Rules
- Consultant can add clients
- Client must consent (email verification)
- One client can have multiple consultants
- Relationship can be deactivated (data preserved)

### Permissions
- Consultant can view/create bills and reports for client
- Consultant cannot delete client's bills
- Client can remove consultant access anytime

---

## 8. Admin Permissions

### Allowed
- View all users and data
- Activate/deactivate accounts, reset passwords
- View audit logs, manage system settings
- Soft delete only

### Restricted
- Cannot view passwords (hashed)
- All actions logged
- Cannot delete audit logs

### Audit Logging
- All admin actions, user authentication, bill modifications, report generation logged
- Retention: 1-7 years (LGPD compliance)

---

## 9. Data Privacy (LGPD)

### Principles
- Collect only necessary data
- Clear consent required
- Secure storage (encryption)

### User Rights
- Access, correct, delete, portability, revoke consent

### Retention
- Active users: indefinite
- Inactive >2 years: archived
- Deleted accounts: 90 days soft delete, then permanent
- Audit logs: per legal requirements

### Sharing
- No sharing without consent
- No selling user data
- Third-party: ANEEL API only (public data)

---

## 10. Tariff Flag Rules

**Values by Flag Color:**
- Green: R$ 0.00
- Yellow: ~R$ 0.04-0.05/kWh
- Red Level 1: ~R$ 0.07-0.08/kWh
- Red Level 2: ~R$ 0.09-0.10/kWh

Applied to total consumption, included in ICMS tax base.

---

## 11. Tariff Modalities

### Conventional
- Single rate 24/7
- Most common for residential
- Available to all

### White Tariff
- Time-of-use rates (Peak, Intermediate, Off-peak)
- Available to low-voltage consumers
- Requires compatible meter
- Best for consumers who can shift usage

### Blue/Green (Group A)
- Commercial/industrial only
- Not supported in MVP

---

## 12. System Performance

### Rate Limits
- Authenticated: 1000 req/hour
- Anonymous: 100 req/hour
- Report generation: 20/hour per user
- ANEEL API: 100 req/hour (cached 24h)
- Exceeded: 429 response with Retry-After header

### File Size Limits
- Company logo: 2 MB
- Bill upload (future): 10 MB
- Generated PDF: 20 MB

### Data Limits
- Bills per user: unlimited
- Simulations per bill: 20
- Recommendations per analysis: 10
- Clients per consultant: unlimited
- Reports per user: 100 active

---

## 13. Edge Cases

### Partial Month Bills
- Bills <25 days flagged for review
- Proportional calculations applied

### Estimated Readings
- Utility estimated without meter reading
- Cannot validate reading difference
- Note in report

### Negative Consumption (Solar)
- Net generation > consumption
- Bill shows credit
- Special handling (not common in MVP)

### Multiple Utility Companies
- User changed providers
- Separate analysis for each
- Cannot compare across utilities

---

## Summary

These rules ensure:
- ✅ Data Integrity - Accurate calculations and validations
- ✅ User Experience - Clear feedback and warnings
- ✅ Security - Authentication, authorization, privacy
- ✅ Compliance - LGPD and regulatory requirements
- ✅ Scalability - Performance limits and caching
- ✅ Flexibility - Support for various scenarios
- ✅ Quality - Consistent and reliable results

