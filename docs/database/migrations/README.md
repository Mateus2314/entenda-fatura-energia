# Database Migrations Index

**Navigation:** Quick reference for all migration files

---

## Migration Files

| Version | Description | Status | Files |
|---------|-------------|--------|-------|
| **V001** | Users base table | ✅ | [Summary](V001.md) · [Details](V001/migration.md) · [Queries](V001/queries.sql) · [Validation](V001/validation.md) |
| **V002** | Clients table | ✅ | [Summary](V002.md) · [Details](V002/migration.md) · [Queries](V002/queries.sql) · [Validation](V002/validation.md) |
| **V003** | Consultants table | ✅ | [Summary](V003.md) · [Details](V003/migration.md) · [Queries](V003/queries.sql) · [Validation](V003/validation.md) |
| **V004** | Admins table | ✅ | [Summary](V004.md) · [Details](V004/migration.md) · [Queries](V004/queries.sql) · [Validation](V004/validation.md) |
| **V005** | Tariffs table | ✅ | [Summary](V005.md) · [Details](V005/migration.md) · [Queries](V005/queries.sql) · [Validation](V005/validation.md) |
| **V006** | Electricity bills table | ✅ | [Summary](V006.md) · [Details](V006/migration.md) · [Queries](V006/queries.sql) · [Validation](V006/validation.md) |
| **V007** | Bill items table | ✅ | [Summary](V007.md) · [Details](V007/migration.md) · [Queries](V007/queries.sql) · [Validation](V007/validation.md) |
| **V008** | Analyses table | ✅ | [Summary](V008.md) · [Details](V008/migration.md) · [Queries](V008/queries.sql) · [Validation](V008/validation.md) |
| **V009** | Consultant-clients join table | ✅ | [Summary](V009.md) · [Details](V009/migration.md) · [Queries](V009/queries.sql) · [Validation](V009/validation.md) |

---

## File Structure Purpose

### Summary Files (`V00X.md`)
- **Purpose:** Quick overview for developers
- **Content:** Objective, key fields, validation status
- **Read time:** < 2 minutes

### Detail Files (`V00X/migration.md`)
- **Purpose:** Complete migration documentation
- **Content:** Full schema, constraints, indexes, dependencies
- **Read time:** 3-5 minutes

### Query Files (`V00X/queries.sql`)
- **Purpose:** Validation and testing queries
- **Content:** 300+ lines of SQL tests per migration
- **Usage:** Copy-paste for manual validation

### Validation Files (`V00X/validation.md`)
- **Purpose:** Test results and status report
- **Content:** Table structure, constraints verification, test outcomes
- **Read time:** 2-3 minutes

---

## Quick Reference: User Hierarchy

```
users (V001)
├── clients (V002) - CPF, address
├── consultants (V003) - CNPJ, company info
└── admins (V004) - role, permissions
```

---

## Dependencies Graph

```
V001 (users)
  │
  ├─► V002 (clients) ─────────┐
  ├─► V003 (consultants) ─────┤
  └─► V004 (admins)           │
                              ├──► V009 (consultant_clients) ⭐ Many-to-Many
                              │
                              ├─► V006 (electricity_bills) ──┬─► V007 (bill_items)
V005 (tariffs) ───────────────┘                              │
                                                              └─► V008 (analyses)
```

- V002-V004 depend on `users` via FK `user_id`
- V005 is independent (ANEEL API data)
- **V009 creates Many-to-Many between consultants and clients** ⭐
- V006 depends on `clients`, `consultants`, and `tariffs`
- V007 depends on `electricity_bills` (Many-to-One)
- V008 depends on `electricity_bills` (One-to-One)

---

## Execution Order

**Must be applied in sequence:**
1. V001 - Creates base users table
2. V002 - Creates clients (depends on users)
3. V003 - Creates consultants (depends on users)
4. V004 - Creates admins (depends on users)
5. V005 - Creates tariffs table (independent - ANEEL API integration)
6. V006 - Creates electricity_bills table (depends on clients, consultants, tariffs)
7. V007 - Creates bill_items table (depends on electricity_bills)
8. V008 - Creates analyses table (depends on electricity_bills)
9. **V009 - Creates consultant_clients join table (Many-to-Many)** ⭐

Flyway ensures correct order automatically.

---

## Testing Workflow

For each migration:

1. **Read summary** (`V00X.md`)
2. **Apply migration** (Flyway or manual)
3. **Run validation queries** (`V00X/queries.sql`)
4. **Verify results** (`V00X/validation.md`)

---

**Back to:** [Database README](../README.md)

