# User Flows

## Flow 1: Client Registration and First Bill Analysis

### 1. Landing Page
- User arrives at homepage, views value proposition
- Clicks "Sign Up" or "Get Started"

### 2. Registration
- Selects user type: "Client"
- Enters email, password (min 8 chars, 1 uppercase, 1 number, 1 special)
- Enters full name, phone (optional)
- Accepts terms and privacy policy

### 3. Email Verification
- System sends verification email
- User clicks link, account confirmed

### 4. First Login & Onboarding
- User logs in, sees welcome wizard
- Quick tutorial on bill analysis

### 5. Utility Company Selection
- Search and select utility company (e.g., "ENEL SP")
- System fetches current ANEEL tariffs

### 6. Bill Data Entry
- **Reference Period**: Month/Year, billing days
- **Consumption**: kWh, readings, on-peak/off-peak
- **Tariff Info**: Modality, TUSD, TE, flag (auto-filled from ANEEL)
- **Taxes**: PIS, COFINS, ICMS, public lighting (auto-filled by state)
- **Total Amount**: From bill
- Real-time validation with tooltips

### 7. Analysis Loading
- Progress indicator: validating, calculating, comparing, generating recommendations

### 8. Results Dashboard
- Validation result badge (Match/Discrepancy)
- Bill breakdown (pie chart, table)
- Consumption analysis (bar chart, trends)
- Efficiency score (0-100)
- Top 3 recommendations with savings
- Actions: Simulate, View Report, Download PDF, Add Another Bill

### 9. Simulation (Optional)
- Select improvements, adjust parameters
- Side-by-side comparison: Current vs Simulated
- Review savings and payback period

### 10. Report Generation
- Download PDF with personalized analysis
- Option to email report

---

## Flow 2: Consultant Registration and Client Management

### 1. Registration
- Selects "Consultant/Company"
- Extended form: personal info + company name, CNPJ, address, logo

### 2. Profile Setup
- Configure company information and branding
- Set report preferences

### 3. Add Clients
- Enter client information (name, email, phone, address, notes)
- Associate bills with clients

### 4. Analyze and Generate Reports
- Review analysis, customize report
- Apply company branding, add consultant notes
- Generate and download/email to client

### 5. Client Portfolio Management
- View all clients with summary (bills analyzed, savings, last analysis)
- Filter, sort, search, tag clients

### 6. Bulk Operations
- Select multiple clients
- Generate consolidated reports, export to Excel

---

## Flow 3: Admin User Management

### 1. Admin Dashboard
- System metrics: users, sessions, bills analyzed, reports generated, API usage
- System health and recent errors

### 2. User Management
- List all users with search/filter
- View user details and activity history

### 3. User Actions
- Edit info, reset password, suspend/reactivate/delete account
- View audit log, impersonate for support

### 4. System Configuration
- Configure APIs (ANEEL, taxes), email, storage, cache
- Feature flags, maintenance mode

### 5. Data Management
- Database backup/restore, cache invalidation, data cleanup

---

## Flow 4: Bill Comparison

### 1. Select Bills
- From dashboard, select 2-5 bills to compare

### 2. Comparison View
- Summary table with side-by-side metrics
- Detailed breakdown with highlighted differences
- Trend charts (consumption, cost, efficiency)
- Automated insights on patterns and anomalies

### 3. Export
- Download PDF, export to Excel, share link

---

## Flow 5: Tariff Modality Recommendation

### 1. Access Optimizer
- From dashboard, click "Optimize My Tariff"

### 2. Current Tariff Overview
- Display current costs and structure

### 3. Comparison Matrix
- List available tariffs (Conventional, White, Low Income)
- Show monthly/annual costs, savings, pros/cons, eligibility

### 4. Recommendation
- Highlight best option based on consumption pattern
- Show projected savings

### 5. Simulation
- Adjust consumption patterns, recalculate recommendations

### 6. Take Action
- Save recommendation, download report, learn how to switch

---

## Flow 6: Recommendation Tracking

### 1. Create Action Plan
- Select recommendations to implement
- Set target dates, add notes, costs, responsible person

### 2. Track Progress
- Update status (Not Started, In Progress, Completed, On Hold)

### 3. Measure Impact
- Add new bills after implementation
- Compare before/after, calculate actual savings vs projected
- Show ROI achieved

### 4. Success Report
- List implemented improvements, investment, savings, payback period
- Environmental impact (CO2 reduction)

---

## Flow 7: Error Handling

### API Failure
- Show user-friendly message if ANEEL unavailable
- Use cached tariffs with disclaimer
- Option to enter manually or retry

### Validation Discrepancy
- Warn if calculated total differs >5% from bill total
- Highlight potential issues, suggest corrections
- Allow override with disclaimer

### Export Failure
- Show error, offer alternatives (reduce scope, different format)
- Log for admin review

---

## Flow 8: Notifications (Future)

### New Tariff Alert
- Notify users when ANEEL updates tariffs
- Email/in-app/SMS with change details and recommended actions

### Unusual Consumption Alert
- Alert if consumption >30% higher than usual
- Show comparison, possible causes, recommendations

### Recommendation Reminders
- Remind users of implementation deadlines (7 days, 1 day, on deadline)

