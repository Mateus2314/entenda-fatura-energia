# 03 - User Flows

## Flow 1: Client Registration and First Bill Analysis

### Step 1: Landing Page
- User arrives at homepage
- Views value proposition
- Clicks "Sign Up" or "Get Started"

### Step 2: Registration Form
- Selects user type: "Client"
- Enters email address
- Creates password (min 8 chars, 1 uppercase, 1 number, 1 special)
- Confirms password
- Enters full name
- Enters phone number (optional)
- Accepts terms of service
- Accepts privacy policy
- Clicks "Create Account"

### Step 3: Email Verification
- System sends verification email
- User receives email with verification link
- User clicks verification link
- System confirms email
- User redirected to login page

### Step 4: First Login
- User enters email and password
- System authenticates
- User redirected to onboarding wizard

### Step 5: Welcome Wizard
- Screen 1: Welcome message and quick tutorial
- Screen 2: "Let's analyze your first bill"
- Clicks "Get Started"

### Step 6: Utility Company Selection
- User searches for their utility company
- Selects from dropdown (e.g., "ENEL SP")
- System fetches current ANEEL tariffs for selected company
- System displays company logo and basic info
- User clicks "Next"

### Step 7: Bill Data Entry
- Form with sections:
  - **Reference Period**
    - Month/Year selector
    - Billing period days (auto-calculated or manual)
  
  - **Consumption Data**
    - Total consumption (kWh) - required
    - Previous reading
    - Current reading
    - On-peak consumption (if applicable)
    - Off-peak consumption (if applicable)
  
  - **Tariff Information** (auto-filled from ANEEL API)
    - Tariff modality (dropdown: Conventional, White)
    - TUSD value (auto-filled, editable)
    - TE value (auto-filled, editable)
    - Current flag (dropdown: Green, Yellow, Red 1, Red 2)
    - Flag value (auto-filled based on selection)
  
  - **Taxes** (auto-filled by state, editable)
    - PIS (%)
    - COFINS (%)
    - ICMS (%)
    - Public lighting contribution (R$)
  
  - **Total Amount**
    - Total amount from bill (R$)

- Real-time validation as user types
- Helpful tooltips on each field
- "Where to find this?" links showing bill images
- Save as draft option
- User clicks "Analyze Bill"

### Step 8: Analysis Loading
- Loading screen with progress indicator
- "Validating data..."
- "Calculating charges..."
- "Comparing with ANEEL tariffs..."
- "Generating recommendations..."

### Step 9: Analysis Results Dashboard
- User sees dashboard with:
  - **Validation Result** (Match/Discrepancy badge)
  - **Bill Breakdown** (pie chart and detailed table)
  - **Consumption Analysis** (bar chart, trends)
  - **Your Efficiency Score** (gauge 0-100)
  - **Top 3 Recommendations** (cards with potential savings)
  - **Savings Opportunity** (highlight total potential savings)

- Actions available:
  - "Simulate Improvements"
  - "View Detailed Report"
  - "Download PDF"
  - "Add Another Bill"

### Step 10: Simulation (Optional)
- User clicks "Simulate Improvements"
- Sees list of available improvements with checkboxes
- Selects desired improvements
- Adjusts parameters (e.g., % consumption reduction)
- Clicks "Calculate"
- Sees side-by-side comparison: Current vs Simulated
- Reviews savings and payback period

### Step 11: Report Generation
- User clicks "Download PDF"
- System generates personalized report
- Download starts automatically
- Option to email report

### Step 12: Dashboard
- User clicks "Go to Dashboard"
- Sees overview of all analyzed bills
- Can add more bills
- Can access previous analyses

---

## Flow 2: Consultant Registration and Client Management

### Step 1: Registration
- Selects user type: "Consultant/Company"
- Fills extended form:
  - Personal info (email, password, name, phone)
  - Company name
  - CNPJ
  - Business address
  - Professional license (optional)
  - Company logo upload
- Accepts terms
- Creates account

### Step 2: Email Verification
- Same as client flow

### Step 3: First Login
- Redirected to consultant dashboard

### Step 4: Profile Setup
- Upload company logo (if not done in registration)
- Configure company information
- Set report branding preferences
- Configure notification settings

### Step 5: Add First Client
- Clicks "Add Client" button
- Fills client information:
  - Client name
  - Email (optional)
  - Phone
  - Address
  - Notes
- Saves client

### Step 6: Add Bill for Client
- Selects client from dropdown
- Follows same bill entry flow as client
- All fields same as client flow
- Associates bill with selected client

### Step 7: Analyze and Generate Report
- Reviews analysis results
- Customizes report:
  - Selects sections to include
  - Adds consultant notes
  - Applies company branding
  - Previews report
- Generates and downloads PDF
- Option to email directly to client

### Step 8: Client Portfolio Management
- Views list of all clients
- Sees summary for each client:
  - Number of bills analyzed
  - Total potential savings identified
  - Last analysis date
  - Status
- Can filter, sort, search clients
- Can add tags/categories
- Can view individual client details

### Step 9: Bulk Operations
- Selects multiple clients
- Performs actions:
  - Generate consolidated report
  - Export data to Excel
  - Send email notifications
  - Archive clients

---

## Flow 3: Admin User Management

### Step 1: Admin Login
- Admin credentials required
- 2FA verification (future)
- Access to admin dashboard

### Step 2: Admin Dashboard Overview
- Views system metrics:
  - Total users (by type)
  - Active sessions
  - Bills analyzed today/week/month
  - Reports generated
  - API usage statistics
  - System health
  - Recent errors

### Step 3: User Management
- Clicks "Manage Users"
- Sees paginated list of all users
- Can search/filter:
  - By user type
  - By registration date
  - By status (active, inactive, suspended)
  - By email/name

### Step 4: User Details
- Clicks on a user
- Views complete user information
- Views activity history
- Views associated data (bills, reports)

### Step 5: User Actions
- Can perform:
  - Edit user information
  - Reset password
  - Suspend account
  - Reactivate account
  - Delete account (with confirmation)
  - View audit log for user
  - Impersonate user (for support)

### Step 6: Audit Log Review
- Clicks "Audit Logs"
- Views chronological log of all actions
- Can filter by:
  - User
  - Action type
  - Date range
  - Entity affected
- Can export logs

### Step 7: System Configuration
- Clicks "Settings"
- Can configure:
  - ANEEL API settings
  - Tax API settings
  - Email settings
  - Storage settings
  - Cache settings
  - Feature flags
  - Maintenance mode

### Step 8: Data Management
- Clicks "Data Management"
- Can perform:
  - Database backup
  - Database restore
  - Cache invalidation
  - Data cleanup
  - Export system data

---

## Flow 4: Bill Comparison (Client/Consultant)

### Step 1: Access Comparison Tool
- From dashboard, clicks "Compare Bills"
- Or from bill detail page, clicks "Compare with Another Bill"

### Step 2: Select Bills to Compare
- Sees list of available bills
- Selects 2+ bills (up to 5)
- Bills can be from same or different utility companies
- Clicks "Compare"

### Step 3: Comparison View
- **Summary Table**
  - Side-by-side comparison of key metrics
  - Consumption, cost, tariff, flags
  - % change between bills
  - Visual indicators (up/down arrows)

- **Detailed Breakdown**
  - Line-by-line charge comparison
  - Highlights differences
  - Explains causes of variations

- **Trend Charts**
  - Consumption trend over time
  - Cost trend over time
  - Efficiency trend

- **Insights**
  - Automated insights on changes
  - Seasonal patterns identified
  - Anomalies detected
  - Recommendations based on trends

### Step 4: Export Comparison
- Options:
  - Download as PDF
  - Export to Excel
  - Share link
  - Add to report

---

## Flow 5: Tariff Modality Recommendation

### Step 1: Access Tariff Optimizer
- From dashboard or bill analysis
- Clicks "Optimize My Tariff"

### Step 2: Current Tariff Overview
- System displays current tariff modality
- Shows current costs breakdown
- Explains current tariff structure

### Step 3: Available Options
- System lists all available tariffs for user's utility company:
  - Conventional
  - White (if available)
  - Low Income (if eligible)

### Step 4: Comparison Matrix
- **For each tariff option, shows:**
  - Monthly cost (based on current consumption)
  - Annual cost
  - Potential savings vs current
  - Best suited for (usage pattern description)
  - Pros and cons
  - Eligibility requirements
  - How to switch (process steps)

### Step 5: Recommendation
- System highlights best option based on:
  - User's consumption pattern
  - Time-of-use distribution
  - Historical data
  - Projected savings

### Step 6: Simulation with Different Patterns
- User can adjust:
  - % of consumption on-peak
  - % of consumption off-peak
  - Average daily consumption
- System recalculates recommendations in real-time

### Step 7: Take Action
- User sees recommended tariff
- Options:
  - "Save Recommendation"
  - "Download Comparison Report"
  - "Learn How to Switch" (guide)
  - "Contact My Utility Company" (link/phone)

---

## Flow 6: Recommendation Implementation Tracking

### Step 1: Select Recommendations
- From analysis results
- User sees list of recommendations
- Each shows: description, savings, investment, payback
- User selects recommendations to implement

### Step 2: Create Action Plan
- For each selected recommendation:
  - Set target implementation date
  - Add notes
  - Add estimated cost
  - Add responsible person (for consultants)
  - Set reminders

### Step 3: Track Progress
- Recommendations move to "In Progress"
- User can update status:
  - Not Started
  - In Progress
  - Completed
  - On Hold
  - Cancelled

### Step 4: Mark as Complete
- User marks recommendation as implemented
- Enters actual:
  - Implementation date
  - Actual cost
  - Notes

### Step 5: Measure Impact
- User adds new bills after implementation
- System compares:
  - Before implementation (historical bills)
  - After implementation (new bills)
- Calculates actual savings
- Compares actual vs projected savings
- Shows ROI achieved

### Step 6: Success Report
- System generates success report:
  - List of implemented improvements
  - Total investment
  - Total savings achieved
  - Payback period realized
  - Environmental impact (CO2 reduction)
  - Next recommended actions

---

## Flow 7: Multi-Country Future Flow (Future Feature)

### Step 1: Registration with Country Selection
- During registration
- User selects country
- Form adapts to country:
  - Address format
  - Phone format
  - Language preference
  - Currency

### Step 2: Country-Specific Onboarding
- Tutorial adapted to country's electricity system
- Examples from local utility companies
- Local regulations explained

### Step 3: Utility Company Selection
- List filtered to selected country
- Can search by country-specific identifiers

### Step 4: Bill Entry
- Form fields adapted to country:
  - Local tariff structure
  - Local tax rules
  - Local terminology
  - Local measurements units

### Step 5: API Integration
- System calls country-specific APIs:
  - Local regulatory agency (equivalent to ANEEL)
  - Local tax authorities
  - Local weather data
  - Local energy market

### Step 6: Analysis and Recommendations
- Recommendations adapted to:
  - Local regulations
  - Local available technologies
  - Local energy prices
  - Local climate
  - Local incentives/subsidies

### Step 7: Report Generation
- Report in selected language
- Currency in local format
- Regulations referenced are local
- Contact information for local resources

---

## Flow 8: Error Handling Flows

### Flow 8.1: API Failure (ANEEL Unavailable)
1. User enters bill data
2. System attempts to fetch ANEEL tariffs
3. API call fails (timeout, 500 error, etc.)
4. System shows user-friendly message:
   - "Unable to fetch current tariffs from ANEEL"
   - "Using cached tariffs from [date]"
   - Option to enter tariffs manually
   - Option to retry
5. User continues with cached data or manual entry
6. System adds disclaimer to report about data source

### Flow 8.2: Validation Discrepancy
1. User enters all bill data
2. System calculates expected total
3. Calculated total differs significantly from bill total (>5%)
4. System shows warning:
   - "We detected a discrepancy"
   - Shows: Bill Total vs Calculated Total
   - Highlights potential issues
   - Suggests: "Double-check these fields..."
5. User options:
   - Review and correct data
   - Override and continue anyway
   - Contact support
6. If override: system adds note to analysis

### Flow 8.3: Payment Failure (Future)
1. User attempts to upgrade to premium
2. Payment processing fails
3. System shows clear error message
4. Offers alternatives:
   - Try different payment method
   - Contact support
   - Continue with free tier
5. Transaction rolled back
6. User notified by email

### Flow 8.4: Data Export Failure
1. User requests PDF report or Excel export
2. Generation fails (memory, timeout, etc.)
3. System shows error message
4. Offers alternatives:
   - Reduce data scope
   - Export to different format
   - Contact support for manual generation
5. Error logged for admin review
6. Retry with optimizations

---

## Flow 9: Notification Flows (Future Feature)

### Flow 9.1: New Tariff Alert
1. ANEEL updates tariff for user's utility company
2. System detects change via scheduled check
3. System identifies affected users
4. Notification sent via:
   - Email
   - In-app notification
   - SMS (if enabled)
5. Notification contains:
   - What changed
   - How it affects user
   - Recommended actions
   - Link to re-analyze bills

### Flow 9.2: Unusual Consumption Alert
1. User adds new bill
2. System compares with historical average
3. Consumption is >30% higher than usual
4. System sends alert:
   - "Your consumption increased significantly"
   - Comparison chart
   - Possible causes
   - Recommendations
5. User can:
   - Review bill data
   - Investigate causes
   - Dismiss alert
   - Set up consumption limits

### Flow 9.3: Recommendation Reminder
1. User created action plan for recommendations
2. Implementation deadline approaching
3. System sends reminder:
   - 7 days before
   - 1 day before
   - On deadline
4. Reminder includes:
   - Recommendation details
   - Projected savings
   - Steps to implement
   - Option to reschedule

