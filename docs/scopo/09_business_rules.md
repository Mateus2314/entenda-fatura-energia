# 09 - Business Rules & Validations

## 1. User Management Rules

### 1.1 Registration
**Rules:**
- Email must be unique across all users
- Email format must be valid (RFC 5322)
- Password requirements:
  - Minimum 8 characters
  - At least 1 uppercase letter
  - At least 1 lowercase letter
  - At least 1 number
  - At least 1 special character (!@#$%^&*)
- User type must be selected during registration
- Consultant/Company must provide CNPJ
- CNPJ must be valid (14 digits, valid check digits)
- Admin accounts can only be created by existing admins

**Validations:**
- Email verification required before full access
- CNPJ uniqueness check for consultants
- Phone number format validation (optional field)

---

### 1.2 Authentication
**Rules:**
- Maximum 5 failed login attempts in 15 minutes
- After 5 failures, account locked for 30 minutes
- JWT token expires after 24 hours
- Refresh token expires after 30 days
- Email must be verified to login
- Inactive accounts cannot login

**Validations:**
- Credentials must match database records
- Account must be active
- Email must be verified

---

### 1.3 Password Management
**Rules:**
- Password reset token valid for 1 hour
- Old password required to change password
- Cannot reuse last 3 passwords
- Password change recommended every 180 days (notification)
- Admin can force password reset for users

**Validations:**
- Current password verification before change
- New password must meet strength requirements
- Reset token must be valid and not expired

---

## 2. Bill Data Rules

### 2.1 Bill Creation
**Rules:**
- User can create bills for themselves
- Consultant can create bills for their clients only
- Admin can create bills for any user
- Reference month must be between 1-12
- Reference year must be between 2020 and current year + 1
- Duplicate check: Same user + utility company + month + year = warning (allow but warn)

**Required Fields:**
- Utility company
- Reference month and year
- Total consumption (kWh)
- Tariff modality
- Total amount

**Optional Fields:**
- Consumer unit number
- Installation number
- Meter readings
- Engineering data
- Notes

**Validations:**
- Total consumption > 0 and < 999,999 kWh
- If readings provided: current > previous
- Reading difference should approximately match consumption (±5% tolerance, warning if not)
- Billing days between 1 and 60
- All monetary values ≥ 0
- Tariff rates > 0
- Tax percentages between 0 and 100

---

### 2.2 Consumption Validation
**Rules:**
- If on-peak and off-peak provided, sum should equal total consumption
- Consumption should match meter reading difference
- Formula: `currentReading - previousReading ≈ totalConsumption`
- If discrepancy > 5%, show warning (but allow)

**Warnings:**
- Consumption unusually high (>3x user's average)
- Consumption unusually low (<50% user's average)
- Readings don't match consumption
- Billing days unusual (<25 or >35 days)

---

### 2.3 Tariff Validation
**Rules:**
- Tariff rates must be fetched from ANEEL API when possible
- If API unavailable, use cached rates (with disclaimer)
- Manual override allowed (with notation)
- Tariff validity period checked
- Flag type and value must match ANEEL data

**Validations:**
- TUSD and TE rates within expected ranges (0.05 - 2.00 R$/kWh)
- Flag values match current ANEEL rates
- Tariff modality matches utility company offerings

---

### 2.4 Tax Validation
**Rules:**
- PIS and COFINS rates standard nationwide
- ICMS rate varies by state
- Public lighting contribution varies by municipality
- Tax base calculation order matters:
  1. Calculate energy + distribution + flag = subtotal
  2. Apply PIS and COFINS on subtotal
  3. Calculate ICMS on (subtotal + PIS + COFINS)
  4. Add public lighting

**Standard Rates (can be overridden):**
- PIS: 1.65%
- COFINS: 7.6%
- ICMS: Varies by state (12-25%)

---

### 2.5 Total Calculation Validation
**Rules:**
- System calculates expected total
- Compare with user-provided total
- Status determination:
  - MATCH: Difference < 1%
  - MINOR_DISCREPANCY: Difference 1-5%
  - MAJOR_DISCREPANCY: Difference > 5%
- If discrepancy, prompt user to verify data

**Calculation Formula:**
```
energyCharge = totalConsumption × teRate
distributionCharge = totalConsumption × tusdRate
flagCharge = totalConsumption × flagValue
subtotal1 = energyCharge + distributionCharge + flagCharge

pisAmount = subtotal1 × (pisRate / 100)
cofinsAmount = subtotal1 × (cofinsRate / 100)
subtotal2 = subtotal1 + pisAmount + cofinsAmount

icmsBase = subtotal2 / (1 - icmsRate/100)
icmsAmount = icmsBase - subtotal2

total = icmsBase + publicLightingValue
```

---

## 3. Analysis Rules

### 3.1 Analysis Generation
**Rules:**
- Bill must exist and be complete
- Engineering data recommended but optional
- Analysis can only be generated once per bill
- To re-analyze, must create new bill version
- Analysis locked after creation (immutable)

**Calculations:**
- All charges broken down separately
- Tax calculations verified
- Efficiency score calculated (0-100)
- Comparisons with regional averages
- Consumption patterns analyzed

---

### 3.2 Efficiency Score Calculation
**Rules:**
- Score range: 0-100 (higher is better)
- Factors considered:
  - Cost per kWh (30% weight)
  - Consumption per m² if available (25% weight)
  - Power factor if available (20% weight)
  - Comparison with regional average (25% weight)

**Score Interpretation:**
- 90-100: Excellent efficiency
- 75-89: Good efficiency
- 60-74: Average efficiency
- 40-59: Below average
- 0-39: Poor efficiency

---

### 3.3 Recommendations Generation
**Rules:**
- Minimum 3 recommendations always generated
- Maximum 10 recommendations
- Recommendations prioritized by:
  - Potential savings
  - Investment required
  - Payback period
  - Implementation difficulty
- Quick wins (low investment, fast payback) highlighted

**Recommendation Categories:**
- EQUIPMENT: Replace or upgrade equipment
- BEHAVIOR: Change usage patterns
- TARIFF: Switch tariff modality
- GENERATION: Solar/renewable installation
- POWER_QUALITY: Power factor correction

**Eligibility Rules:**
- Solar recommendations: Only if building area known
- Power factor recommendations: Only for low power factor (<0.92)
- Tariff change: Only if alternative tariffs available
- Equipment: Based on typical usage patterns

---

### 3.4 Savings Calculation
**Rules:**
- Savings estimated based on:
  - Historical data if available
  - Industry benchmarks
  - Engineering calculations
- Conservative estimates (prefer underestimate)
- Payback period: `investment / monthlySavings`
- ROI: `(annualSavings / investment) × 100`

**Validation:**
- Savings cannot exceed current bill amount
- Payback period must be positive
- Investment must be realistic for recommendation type

---

## 4. Simulation Rules

### 4.1 Simulation Creation
**Rules:**
- Must be based on existing bill
- Multiple simulations per bill allowed
- Simulations don't modify original bill
- User can compare original vs simulated

**Adjustable Parameters:**
- Consumption reduction percentage (0-100%)
- Tariff modality change
- Solar generation (kWh/month)
- Specific improvements applied
- Combination of multiple factors

**Calculations:**
- Apply adjustments to original bill
- Recalculate all charges
- Compare totals
- Calculate savings

---

### 4.2 Simulation Validation
**Rules:**
- Consumption reduction cannot result in negative consumption
- Solar generation cannot exceed consumption (net zero minimum)
- Tariff change only to available options
- All calculations follow same rules as bill analysis

**Warnings:**
- Aggressive assumptions (>50% reduction)
- Unrealistic solar generation (>installation capacity)
- Combination effects may not be cumulative

---

## 5. Report Generation Rules

### 5.1 Report Creation
**Rules:**
- Can only generate report for analyzed bills
- Consultant reports include company branding
- Client reports are simplified
- PDF format only (MVP)
- Reports stored for 90 days
- After 90 days, archived or deleted

**Report Sections (configurable):**
- Cover page
- Executive summary
- Bill validation
- Detailed breakdown
- Consumption analysis
- Efficiency metrics
- Recommendations
- Simulations (if any)
- Technical details (optional)
- Glossary

---

### 5.2 Report Sharing
**Rules:**
- Reports can be shared via link
- Share link has expiration (default 30 days)
- Can be made public or private
- Public reports accessible without login
- Private reports require share token
- Download count tracked

**Security:**
- Share tokens are unique UUIDs
- Cannot guess other report URLs
- Expired links return 410 Gone
- Sensitive data (emails, phones) can be redacted in shared reports

---

## 6. Consultant-Client Relationship Rules

### 6.1 Relationship Management
**Rules:**
- Consultant can add clients
- Client must consent (email verification)
- One client can have multiple consultants
- One consultant can have unlimited clients
- Relationship can be deactivated
- Deactivation doesn't delete data

**Permissions:**
- Consultant can view client's bills
- Consultant can create bills for client
- Consultant can generate reports for client
- Consultant cannot delete client's bills
- Consultant cannot access other consultants' clients

---

### 6.2 Client Onboarding
**Rules:**
- If client email exists: Link to existing account
- If email new: Create invitation
- Invitation valid for 7 days
- Client must accept invitation
- Client can reject invitation
- Client can remove consultant access anytime

---

## 7. Admin Rules

### 7.1 Admin Permissions
**Rules:**
- Can view all users and data
- Can activate/deactivate accounts
- Can reset passwords
- Can view audit logs
- Can manage system settings
- Cannot delete data (soft delete only)

**Restrictions:**
- Cannot view user passwords (hashed)
- Cannot modify bills without logging
- All actions logged in audit trail
- Cannot delete audit logs

---

### 7.2 Audit Logging
**Rules:**
- All admin actions logged
- User authentication events logged
- Bill modifications logged
- Report generation logged
- Failed actions logged

**Log Retention:**
- Minimum 1 year
- Maximum 7 years
- LGPD compliance required

---

## 8. Data Privacy & Security Rules (LGPD)

### 8.1 Personal Data
**Rules:**
- Collect only necessary data
- Clear consent required
- Purpose explicitly stated
- Data minimization principle
- Secure storage (encryption)

**User Rights:**
- Right to access data
- Right to correct data
- Right to delete data
- Right to data portability
- Right to revoke consent

---

### 8.2 Data Retention
**Rules:**
- Active users: Data retained indefinitely
- Inactive users (>2 years): Data archived
- Deleted accounts: Data retained 90 days (soft delete)
- After 90 days: Permanent deletion
- Audit logs retained per legal requirements

---

### 8.3 Data Sharing
**Rules:**
- No sharing without explicit consent
- Consultant access requires client consent
- Shared reports can be anonymous
- No selling of user data
- Third-party services: Only ANEEL API (public data)

---

## 9. Business Logic Rules

### 9.1 Tariff Flag Rules
**Rules:**
- Flags change monthly (ANEEL decision)
- Values vary by flag color:
  - Green: R$ 0.00
  - Yellow: ~R$ 0.04-0.05/kWh
  - Red Level 1: ~R$ 0.07-0.08/kWh
  - Red Level 2: ~R$ 0.09-0.10/kWh
- Applied to total consumption
- Included in ICMS tax base

---

### 9.2 Tariff Modality Rules
**Conventional Tariff:**
- Single rate 24/7
- Most common for residential
- Available to all consumers

**White Tariff:**
- Time-of-use rates
- Peak (higher), Intermediate, Off-peak (lower)
- Available to low-voltage consumers
- Must have compatible meter
- Best for consumers who can shift usage

**Blue/Green Tariff (Group A - High Voltage):**
- Commercial/industrial only
- Separate demand and consumption charges
- Not supported in MVP

---

### 9.3 Solar Net Metering Rules
**Rules:**
- Excess generation credited to next bill
- Credits valid for 60 months
- Cannot receive monetary compensation
- Minimum charge still applies (availability)
- TUSD still charged on net consumption
- Not fully implemented in MVP (future)

---

## 10. System Performance Rules

### 10.1 API Rate Limiting
**Rules:**
- Authenticated: 1000 requests/hour
- Anonymous: 100 requests/hour
- Report generation: 20/hour per user
- ANEEL API: 100 requests/hour (cached 24h)

**Exceeded Limits:**
- Return 429 Too Many Requests
- Include Retry-After header
- Temporary block, not permanent

---

### 10.2 File Size Limits
**Rules:**
- Company logo: Max 2 MB
- Bill upload (future): Max 10 MB
- Generated PDF: Max 20 MB

---

### 10.3 Data Limits
**Rules:**
- Bills per user: Unlimited
- Simulations per bill: Max 20
- Recommendations per analysis: Max 10
- Clients per consultant: Unlimited
- Reports per user: Max 100 active

---

## 11. Edge Cases & Special Scenarios

### 11.1 Partial Month Bills
**Rules:**
- Bills with <25 days: Flag for review
- Proportional calculations applied
- Average daily consumption calculated

---

### 11.2 Estimated Readings
**Rules:**
- Utility may estimate without meter reading
- Cannot validate reading difference
- Note in analysis report
- Recommend actual reading verification

---

### 11.3 Negative Consumption (Solar)
**Rules:**
- Net generation > consumption
- Bill shows credit
- Special handling in analysis
- Not common in MVP target users

---

### 11.4 Multiple Utility Companies (Moved)
**Rules:**
- User changed providers
- Old and new bills in system
- Separate analysis for each
- Cannot compare across utilities

---

### 11.5 Shared Meters (Condos)
**Rules:**
- Individual unit vs common areas
- Different analysis approach
- Not fully supported in MVP
- Future feature

---

## 12. Notification Rules (Future)

### 12.1 Email Notifications
**Triggers:**
- Account created (verification)
- Password reset requested
- Bill analyzed (optional)
- New tariff available
- Report generated
- Unusual consumption detected

**Rules:**
- User can opt-out of non-critical emails
- Transactional emails always sent
- Maximum 1 marketing email per week

---

### 12.2 In-App Notifications
**Triggers:**
- Analysis completed
- Recommendation updated
- Consultant invitation
- System announcements

**Rules:**
- Notifications expire after 30 days
- Can mark as read
- Can dismiss

---

## Summary

These business rules ensure:
- ✅ **Data Integrity** - Accurate calculations and validations
- ✅ **User Experience** - Clear feedback and warnings
- ✅ **Security** - Authentication, authorization, privacy
- ✅ **Compliance** - LGPD and regulatory requirements
- ✅ **Scalability** - Performance limits and caching
- ✅ **Flexibility** - Support for various scenarios
- ✅ **Quality** - Consistent and reliable results

All rules should be implemented in:
- **Backend validation** (primary enforcement)
- **Frontend validation** (user experience)
- **Database constraints** (data integrity)
- **Unit tests** (verification)
- **Documentation** (reference)

