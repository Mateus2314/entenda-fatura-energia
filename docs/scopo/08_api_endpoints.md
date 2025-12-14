# 08 - API Endpoints Specification

## Base URL
- **Development:** `http://localhost:8080/api/v1`
- **Production:** `https://api.entendafaturaenergia.com.br/api/v1`

## Authentication
- **Method:** JWT Bearer Token
- **Header:** `Authorization: Bearer {token}`
- **Token Expiration:** 24 hours
- **Refresh Token:** 30 days

---

## 1. Authentication & User Management

### 1.1 POST /auth/register
**Description:** Register a new user account

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "SecurePass123!",
  "userType": "CLIENT",
  "fullName": "João Silva",
  "phone": "+5511999999999",
  "companyName": "Energy Consulting Ltda",  // Required if CONSULTANT
  "cnpj": "12.345.678/0001-90"              // Required if CONSULTANT
}
```

**Response (201 Created):**
```json
{
  "id": "uuid",
  "email": "user@example.com",
  "userType": "CLIENT",
  "message": "Registration successful. Please verify your email."
}
```

**Errors:**
- 400: Validation error (invalid email, weak password, etc.)
- 409: Email already registered

---

### 1.2 POST /auth/login
**Description:** Authenticate user and get JWT token

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "SecurePass123!"
}
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "user": {
    "id": "uuid",
    "email": "user@example.com",
    "fullName": "João Silva",
    "userType": "CLIENT",
    "isEmailVerified": true
  }
}
```

**Errors:**
- 401: Invalid credentials
- 403: Email not verified

---

### 1.3 POST /auth/verify-email
**Description:** Verify email address with token

**Request Body:**
```json
{
  "token": "verification-token-from-email"
}
```

**Response (200 OK):**
```json
{
  "message": "Email verified successfully"
}
```

**Errors:**
- 400: Invalid or expired token

---

### 1.4 POST /auth/forgot-password
**Description:** Request password reset

**Request Body:**
```json
{
  "email": "user@example.com"
}
```

**Response (200 OK):**
```json
{
  "message": "Password reset email sent"
}
```

---

### 1.5 POST /auth/reset-password
**Description:** Reset password with token

**Request Body:**
```json
{
  "token": "reset-token-from-email",
  "newPassword": "NewSecurePass123!"
}
```

**Response (200 OK):**
```json
{
  "message": "Password reset successful"
}
```

---

### 1.6 POST /auth/refresh-token
**Description:** Refresh access token

**Request Body:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
}
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "expiresIn": 86400
}
```

---

### 1.7 GET /users/me
**Description:** Get current user profile
**Auth:** Required

**Response (200 OK):**
```json
{
  "id": "uuid",
  "email": "user@example.com",
  "fullName": "João Silva",
  "userType": "CLIENT",
  "phone": "+5511999999999",
  "createdAt": "2024-01-15T10:30:00Z",
  "lastLogin": "2024-01-20T14:45:00Z"
}
```

---

### 1.8 PATCH /users/me
**Description:** Update user profile
**Auth:** Required

**Request Body:**
```json
{
  "fullName": "João Pedro Silva",
  "phone": "+5511988888888"
}
```

**Response (200 OK):**
```json
{
  "id": "uuid",
  "email": "user@example.com",
  "fullName": "João Pedro Silva",
  "phone": "+5511988888888",
  "updatedAt": "2024-01-20T15:00:00Z"
}
```

---

### 1.9 POST /users/change-password
**Description:** Change password (authenticated user)
**Auth:** Required

**Request Body:**
```json
{
  "currentPassword": "OldPass123!",
  "newPassword": "NewPass456!"
}
```

**Response (200 OK):**
```json
{
  "message": "Password changed successfully"
}
```

**Errors:**
- 401: Current password incorrect

---

## 2. Utility Companies

### 2.1 GET /utility-companies
**Description:** List all utility companies
**Auth:** Required

**Query Parameters:**
- `state` (optional): Filter by state (e.g., "SP")
- `search` (optional): Search by name
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 20)

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "name": "ENEL SP",
      "aneelCode": "SP-001",
      "state": "SP",
      "logoUrl": "https://storage.../enel-logo.png",
      "phone": "0800-7272-196"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 60,
  "totalPages": 3
}
```

---

### 2.2 GET /utility-companies/{id}
**Description:** Get utility company details
**Auth:** Required

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "ENEL SP",
  "cnpj": "12.345.678/0001-90",
  "aneelCode": "SP-001",
  "state": "SP",
  "logoUrl": "https://storage.../enel-logo.png",
  "website": "https://www.enel.com.br",
  "phone": "0800-7272-196",
  "currentTariffs": {
    "conventional": {
      "tusd": 0.25432,
      "te": 0.31245
    },
    "white": {
      "peak": {
        "tusd": 0.35432,
        "te": 0.41245
      },
      "offPeak": {
        "tusd": 0.15432,
        "te": 0.21245
      }
    },
    "validFrom": "2024-01-01",
    "resolution": "REH 3000/2024"
  }
}
```

---

## 3. Bills Management

### 3.1 POST /bills
**Description:** Create a new bill
**Auth:** Required

**Request Body:**
```json
{
  "utilityCompanyId": 1,
  "clientId": "uuid",  // Optional, only for consultants
  "referenceMonth": 12,
  "referenceYear": 2024,
  "consumerUnitNumber": "123456789",
  "installationNumber": "987654321",
  "totalConsumptionKwh": 350.5,
  "onPeakConsumptionKwh": null,
  "offPeakConsumptionKwh": null,
  "previousReading": 12500,
  "currentReading": 12850.5,
  "billingDays": 30,
  "tariffModality": "CONVENTIONAL",
  "tusdRate": 0.25432,
  "teRate": 0.31245,
  "flagType": "RED_LEVEL_1",
  "flagValue": 0.07790,
  "pisRate": 1.65,
  "cofinsRate": 7.6,
  "icmsRate": 18.0,
  "publicLightingValue": 25.50,
  "totalAmount": 289.45,
  "notes": "Bill for December 2024"
}
```

**Response (201 Created):**
```json
{
  "id": "bill-uuid",
  "userId": "user-uuid",
  "utilityCompanyId": 1,
  "referenceMonth": 12,
  "referenceYear": 2024,
  "totalConsumptionKwh": 350.5,
  "totalAmount": 289.45,
  "createdAt": "2024-01-20T16:00:00Z",
  "message": "Bill created successfully"
}
```

**Errors:**
- 400: Validation error
- 404: Utility company not found
- 403: Cannot create bill for client (not related consultant)

---

### 3.2 GET /bills
**Description:** List user's bills
**Auth:** Required

**Query Parameters:**
- `page` (optional): Page number
- `size` (optional): Page size
- `sort` (optional): Sort field (e.g., "referenceYear,desc")
- `utilityCompanyId` (optional): Filter by company
- `year` (optional): Filter by year
- `clientId` (optional): Filter by client (consultants only)

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": "bill-uuid",
      "utilityCompany": {
        "id": 1,
        "name": "ENEL SP"
      },
      "referenceMonth": 12,
      "referenceYear": 2024,
      "totalConsumptionKwh": 350.5,
      "totalAmount": 289.45,
      "hasAnalysis": true,
      "createdAt": "2024-01-20T16:00:00Z"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 15,
  "totalPages": 1
}
```

---

### 3.3 GET /bills/{id}
**Description:** Get bill details
**Auth:** Required

**Response (200 OK):**
```json
{
  "id": "bill-uuid",
  "userId": "user-uuid",
  "utilityCompany": {
    "id": 1,
    "name": "ENEL SP",
    "logoUrl": "..."
  },
  "referenceMonth": 12,
  "referenceYear": 2024,
  "consumerUnitNumber": "123456789",
  "installationNumber": "987654321",
  "totalConsumptionKwh": 350.5,
  "onPeakConsumptionKwh": null,
  "offPeakConsumptionKwh": null,
  "previousReading": 12500,
  "currentReading": 12850.5,
  "billingDays": 30,
  "tariffModality": "CONVENTIONAL",
  "tusdRate": 0.25432,
  "teRate": 0.31245,
  "flagType": "RED_LEVEL_1",
  "flagValue": 0.07790,
  "pisRate": 1.65,
  "cofinsRate": 7.6,
  "icmsRate": 18.0,
  "publicLightingValue": 25.50,
  "totalAmount": 289.45,
  "notes": "Bill for December 2024",
  "createdAt": "2024-01-20T16:00:00Z",
  "updatedAt": "2024-01-20T16:00:00Z"
}
```

**Errors:**
- 404: Bill not found
- 403: Not authorized to view this bill

---

### 3.4 PUT /bills/{id}
**Description:** Update bill
**Auth:** Required

**Request Body:** Same as POST /bills

**Response (200 OK):**
```json
{
  "id": "bill-uuid",
  "message": "Bill updated successfully",
  "updatedAt": "2024-01-20T17:00:00Z"
}
```

---

### 3.5 DELETE /bills/{id}
**Description:** Delete bill
**Auth:** Required

**Response (204 No Content)**

**Errors:**
- 404: Bill not found
- 403: Not authorized
- 409: Cannot delete bill with existing analysis

---

## 4. Engineering Data

### 4.1 POST /bills/{billId}/engineering-data
**Description:** Add engineering data to a bill
**Auth:** Required

**Request Body:**
```json
{
  "voltageLevel": "127/220V",
  "connectionType": "TWO_PHASE",
  "installedPowerKw": 15.5,
  "circuitBreakerRating": 50,
  "powerFactor": 0.92,
  "installationClass": "RESIDENTIAL",
  "typicalUsageHours": "08:00-22:00",
  "occupantsCount": 4,
  "builtAreaSqm": 120.5,
  "buildingType": "House",
  "hasSolarPanels": false,
  "hasBatteryStorage": false
}
```

**Response (201 Created):**
```json
{
  "id": "uuid",
  "billId": "bill-uuid",
  "message": "Engineering data added successfully"
}
```

---

### 4.2 GET /bills/{billId}/engineering-data
**Description:** Get engineering data for a bill
**Auth:** Required

**Response (200 OK):**
```json
{
  "id": "uuid",
  "billId": "bill-uuid",
  "voltageLevel": "127/220V",
  "connectionType": "TWO_PHASE",
  "installedPowerKw": 15.5,
  "circuitBreakerRating": 50,
  "powerFactor": 0.92,
  "installationClass": "RESIDENTIAL",
  "typicalUsageHours": "08:00-22:00",
  "occupantsCount": 4,
  "builtAreaSqm": 120.5,
  "buildingType": "House",
  "hasSolarPanels": false,
  "hasBatteryStorage": false,
  "createdAt": "2024-01-20T16:30:00Z"
}
```

---

## 5. Analysis

### 5.1 POST /bills/{billId}/analyze
**Description:** Analyze a bill
**Auth:** Required

**Response (201 Created):**
```json
{
  "id": "analysis-uuid",
  "billId": "bill-uuid",
  "validationStatus": "MINOR_DISCREPANCY",
  "calculatedTotal": 287.12,
  "discrepancyAmount": -2.33,
  "discrepancyPercentage": -0.81,
  "breakdown": {
    "energyCharge": 109.64,
    "distributionCharge": 89.12,
    "flagCharge": 27.30,
    "pisAmount": 4.53,
    "cofinsAmount": 20.84,
    "icmsAmount": 49.96,
    "publicLightingAmount": 25.50,
    "totalTaxes": 75.33,
    "totalBeforeTaxes": 226.06
  },
  "efficiencyMetrics": {
    "efficiencyScore": 72,
    "averageDailyConsumptionKwh": 11.68,
    "costPerKwh": 0.82,
    "consumptionPerSqm": 2.91
  },
  "recommendations": [
    {
      "id": "rec-uuid-1",
      "category": "EQUIPMENT",
      "title": "Install LED Lighting",
      "description": "Replace incandescent and fluorescent bulbs with LED",
      "potentialSavingsMonthly": 25.50,
      "investmentRequired": 350.00,
      "paybackMonths": 13.7,
      "priorityScore": 85,
      "implementationDifficulty": "EASY"
    },
    {
      "id": "rec-uuid-2",
      "category": "BEHAVIOR",
      "title": "Shift Usage to Off-Peak Hours",
      "description": "Consider switching to White Tariff and using appliances during off-peak hours",
      "potentialSavingsMonthly": 42.00,
      "investmentRequired": 0.00,
      "paybackMonths": 0,
      "priorityScore": 92,
      "implementationDifficulty": "EASY"
    }
  ],
  "createdAt": "2024-01-20T16:45:00Z"
}
```

**Errors:**
- 404: Bill not found
- 409: Analysis already exists for this bill

---

### 5.2 GET /bills/{billId}/analysis
**Description:** Get analysis for a bill
**Auth:** Required

**Response (200 OK):** Same as POST response

---

### 5.3 GET /analysis/{id}/recommendations
**Description:** Get recommendations for an analysis
**Auth:** Required

**Query Parameters:**
- `category` (optional): Filter by category
- `minPriority` (optional): Minimum priority score

**Response (200 OK):**
```json
{
  "recommendations": [
    {
      "id": "rec-uuid-1",
      "category": "BEHAVIOR",
      "title": "Shift Usage to Off-Peak Hours",
      "description": "...",
      "potentialSavingsMonthly": 42.00,
      "potentialSavingsAnnual": 504.00,
      "investmentRequired": 0.00,
      "paybackMonths": 0,
      "roiPercentage": null,
      "priorityScore": 92,
      "implementationDifficulty": "EASY",
      "co2ReductionKgPerYear": 156.8
    }
  ]
}
```

---

## 6. Simulations

### 6.1 POST /bills/{billId}/simulations
**Description:** Create a simulation
**Auth:** Required

**Request Body:**
```json
{
  "simulationName": "With Solar Panels",
  "description": "Simulation with 5kWp solar installation",
  "consumptionReductionPercent": 0,
  "tariffModalitySimulated": "CONVENTIONAL",
  "solarGenerationKwh": 200,
  "improvementsApplied": [
    {
      "recommendationId": "rec-uuid-1",
      "description": "LED lighting"
    }
  ]
}
```

**Response (201 Created):**
```json
{
  "id": "sim-uuid",
  "billId": "bill-uuid",
  "simulationName": "With Solar Panels",
  "original": {
    "consumptionKwh": 350.5,
    "totalAmount": 289.45
  },
  "simulated": {
    "consumptionKwh": 150.5,
    "totalAmount": 124.32,
    "savingsAmount": 165.13,
    "savingsPercentage": 57.05
  },
  "createdAt": "2024-01-20T17:00:00Z"
}
```

---

### 6.2 GET /bills/{billId}/simulations
**Description:** List simulations for a bill
**Auth:** Required

**Response (200 OK):**
```json
{
  "simulations": [
    {
      "id": "sim-uuid",
      "simulationName": "With Solar Panels",
      "savingsAmount": 165.13,
      "savingsPercentage": 57.05,
      "createdAt": "2024-01-20T17:00:00Z"
    }
  ]
}
```

---

### 6.3 GET /simulations/{id}
**Description:** Get simulation details
**Auth:** Required

**Response (200 OK):** Same as POST response with full details

---

### 6.4 DELETE /simulations/{id}
**Description:** Delete a simulation
**Auth:** Required

**Response (204 No Content)**

---

## 7. Reports

### 7.1 POST /reports/generate
**Description:** Generate PDF report
**Auth:** Required

**Request Body:**
```json
{
  "billId": "bill-uuid",
  "reportType": "BILL_ANALYSIS",
  "includeRecommendations": true,
  "includeTechnicalDetails": false,
  "language": "pt-BR",
  "customNotes": "Report for client presentation"
}
```

**Response (201 Created):**
```json
{
  "id": "report-uuid",
  "reportName": "Bill_Analysis_Dec_2024.pdf",
  "fileUrl": "https://storage.../reports/report-uuid.pdf",
  "fileSizeBytes": 524288,
  "generationDate": "2024-01-20T17:30:00Z",
  "downloadUrl": "https://api.../reports/report-uuid/download"
}
```

---

### 7.2 GET /reports
**Description:** List user's reports
**Auth:** Required

**Query Parameters:**
- `page`, `size`, `sort`
- `reportType` (optional)
- `startDate`, `endDate` (optional)

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": "report-uuid",
      "reportName": "Bill_Analysis_Dec_2024.pdf",
      "reportType": "BILL_ANALYSIS",
      "generationDate": "2024-01-20T17:30:00Z",
      "fileSizeBytes": 524288,
      "downloadCount": 3
    }
  ],
  "page": 0,
  "totalElements": 10
}
```

---

### 7.3 GET /reports/{id}/download
**Description:** Download report PDF
**Auth:** Required (or valid share token)

**Response:** PDF file stream

---

### 7.4 POST /reports/{id}/share
**Description:** Generate shareable link
**Auth:** Required

**Request Body:**
```json
{
  "expiresInDays": 30
}
```

**Response (200 OK):**
```json
{
  "shareUrl": "https://app.../reports/shared/abc123token",
  "expiresAt": "2024-02-19T17:30:00Z"
}
```

---

## 8. Consultant Management

### 8.1 POST /consultants/clients
**Description:** Add a client (consultant only)
**Auth:** Required (CONSULTANT)

**Request Body:**
```json
{
  "clientEmail": "client@example.com",
  "clientName": "Maria Santos",
  "clientPhone": "+5511988888888",
  "notes": "New client from referral"
}
```

**Response (201 Created):**
```json
{
  "relationId": "relation-uuid",
  "clientId": "client-uuid",
  "message": "Client added successfully"
}
```

---

### 8.2 GET /consultants/clients
**Description:** List consultant's clients
**Auth:** Required (CONSULTANT)

**Response (200 OK):**
```json
{
  "clients": [
    {
      "id": "client-uuid",
      "fullName": "Maria Santos",
      "email": "client@example.com",
      "phone": "+5511988888888",
      "relationshipStart": "2024-01-15T10:00:00Z",
      "billsCount": 5,
      "lastBillDate": "2024-01-10",
      "totalPotentialSavings": 150.00
    }
  ]
}
```

---

### 8.3 GET /consultants/dashboard
**Description:** Get consultant dashboard metrics
**Auth:** Required (CONSULTANT)

**Response (200 OK):**
```json
{
  "totalClients": 15,
  "activeClients": 12,
  "billsAnalyzedThisMonth": 38,
  "reportsGeneratedThisMonth": 22,
  "totalPotentialSavings": 12500.00,
  "averageSavingsPerClient": 833.33,
  "recentActivity": [
    {
      "type": "BILL_ANALYZED",
      "clientName": "Maria Santos",
      "timestamp": "2024-01-20T15:00:00Z"
    }
  ]
}
```

---

## 9. Admin Management

### 9.1 GET /admin/users
**Description:** List all users (admin only)
**Auth:** Required (ADMIN)

**Query Parameters:**
- `userType`, `isActive`, `search`, `page`, `size`

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": "user-uuid",
      "email": "user@example.com",
      "fullName": "João Silva",
      "userType": "CLIENT",
      "isActive": true,
      "createdAt": "2024-01-15T10:00:00Z",
      "lastLogin": "2024-01-20T14:00:00Z"
    }
  ],
  "totalElements": 150
}
```

---

### 9.2 PATCH /admin/users/{id}
**Description:** Update user (admin only)
**Auth:** Required (ADMIN)

**Request Body:**
```json
{
  "isActive": false
}
```

**Response (200 OK):**
```json
{
  "message": "User updated successfully"
}
```

---

### 9.3 GET /admin/audit-logs
**Description:** Get audit logs (admin only)
**Auth:** Required (ADMIN)

**Query Parameters:**
- `userId`, `actionType`, `startDate`, `endDate`, `page`, `size`

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": "log-uuid",
      "userId": "user-uuid",
      "actionType": "LOGIN",
      "entityType": "User",
      "entityId": "user-uuid",
      "actionResult": "SUCCESS",
      "ipAddress": "192.168.1.1",
      "createdAt": "2024-01-20T14:00:00Z"
    }
  ]
}
```

---

### 9.4 GET /admin/metrics
**Description:** System metrics (admin only)
**Auth:** Required (ADMIN)

**Response (200 OK):**
```json
{
  "totalUsers": 150,
  "usersByType": {
    "CLIENT": 120,
    "CONSULTANT": 28,
    "ADMIN": 2
  },
  "billsAnalyzedToday": 45,
  "billsAnalyzedThisWeek": 215,
  "billsAnalyzedThisMonth": 890,
  "reportsGeneratedToday": 28,
  "apiCallsToday": 1250,
  "averageResponseTime": 245,
  "errorRate": 0.3
}
```

---

## 10. External API Integration

### 10.1 GET /external/aneel/tariffs
**Description:** Fetch current tariffs from ANEEL
**Auth:** Required

**Query Parameters:**
- `utilityCompanyId` (required)

**Response (200 OK):**
```json
{
  "utilityCompanyId": 1,
  "aneelResolution": "REH 3000/2024",
  "validFrom": "2024-01-01",
  "validUntil": null,
  "conventional": {
    "tusd": 0.25432,
    "te": 0.31245
  },
  "white": {
    "peak": {
      "tusd": 0.35432,
      "te": 0.41245
    },
    "offPeak": {
      "tusd": 0.15432,
      "te": 0.21245
    },
    "intermediate": {
      "tusd": 0.20432,
      "te": 0.26245
    }
  },
  "flags": {
    "green": 0.0,
    "yellow": 0.04458,
    "redLevel1": 0.07790,
    "redLevel2": 0.09795
  },
  "source": "ANEEL_API",
  "fetchedAt": "2024-01-20T10:00:00Z"
}
```

---

### 10.2 GET /external/taxes
**Description:** Get current tax rates
**Auth:** Required

**Query Parameters:**
- `state` (required): Brazilian state code

**Response (200 OK):**
```json
{
  "state": "SP",
  "pis": 1.65,
  "cofins": 7.6,
  "icms": 18.0,
  "validFrom": "2024-01-01",
  "source": "SEFAZ_SP"
}
```

---

## Error Response Format

All endpoints follow consistent error format:

**4xx Client Errors:**
```json
{
  "timestamp": "2024-01-20T17:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/bills",
  "errors": [
    {
      "field": "totalConsumptionKwh",
      "message": "must be greater than 0"
    }
  ]
}
```

**5xx Server Errors:**
```json
{
  "timestamp": "2024-01-20T17:00:00Z",
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred",
  "path": "/api/v1/bills/analyze"
}
```

---

## Rate Limiting

- **Authenticated requests:** 1000 requests per hour
- **Anonymous requests:** 100 requests per hour
- **Report generation:** 20 reports per hour per user

**Rate Limit Headers:**
```
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 987
X-RateLimit-Reset: 1705774800
```

---

## Pagination

Standard pagination format for list endpoints:

**Query Parameters:**
- `page`: Page number (0-indexed)
- `size`: Items per page (default: 20, max: 100)
- `sort`: Sort field and direction (e.g., "createdAt,desc")

**Response:**
```json
{
  "content": [...],
  "page": 0,
  "size": 20,
  "totalElements": 150,
  "totalPages": 8,
  "first": true,
  "last": false
}
```

---

## Versioning

API version in URL: `/api/v1/...`

Future versions will maintain backward compatibility or provide migration path.

