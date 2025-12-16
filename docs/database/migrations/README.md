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
  ├─► V002 (clients)
  ├─► V003 (consultants)
  └─► V004 (admins)
```

All child tables depend on `users` table via foreign key `user_id`.

---

## Execution Order

**Must be applied in sequence:**
1. V001 - Creates base users table
2. V002 - Creates clients (depends on users)
3. V003 - Creates consultants (depends on users)
4. V004 - Creates admins (depends on users)

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

