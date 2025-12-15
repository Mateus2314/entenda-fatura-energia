# Technical Specification

## Tech Stack

### Backend
- **Framework**: Spring Boot 3.x (Java 17+)
- **Database**: PostgreSQL 15+
- **Security**: Spring Security + JWT
- **API Docs**: SpringDoc OpenAPI (Swagger)
- **Validation**: Jakarta Bean Validation

### Frontend
- **Framework**: React 18+
- **Styling**: TailwindCSS
- **HTTP Client**: Axios
- **Charts**: Recharts

### Infrastructure
- **Containerization**: Docker + Docker Compose
- **Database Migrations**: Flyway

## Data Models

### Core Entities
- **User**: Authentication, profile, permissions
- **Bill**: Consumption data, tariffs, calculations
- **Tariff**: ANEEL rates (TUSD, TE)
- **Recommendation**: Suggestions for savings
- **Report**: Generated analysis documents

### Enums
- `UserType`: BASIC, PROFESSIONAL, ADMIN
- `UserStatus`: ACTIVE, INACTIVE, SUSPENDED
- `TariffGroup`: A, B (B1, B2, B3)

## API Endpoints

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - Login with JWT
- `POST /api/auth/refresh` - Token refresh

### Bills
- `POST /api/bills` - Create bill
- `GET /api/bills` - List user bills
- `GET /api/bills/{id}` - Get bill details
- `GET /api/bills/{id}/analysis` - Detailed analysis

### Tariffs
- `GET /api/tariffs` - List current tariffs
- `GET /api/tariffs/{distributor}` - Get by distributor

### Reports
- `POST /api/reports/generate` - Generate report
- `GET /api/reports/{id}/download` - Download PDF/Excel

### Admin
- `GET /api/admin/users` - List all users
- `PUT /api/admin/users/{id}/status` - Update user status
- `POST /api/admin/tariffs/sync` - Sync ANEEL data
