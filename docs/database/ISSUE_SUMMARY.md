# JPA Inheritance Strategy - Decision

**Decision:** JOINED strategy  
**Date:** 2024-12-14  
**Status:** ✅ Implemented

---

## Decision

Evaluated 3 strategies:

| Strategy | Score | Notes |
|----------|-------|-------|
| SINGLE_TABLE | 3.05/5 | Fast but NULL columns |
| **JOINED** | **4.40/5** | ⭐ Normalized, strong constraints |
| TABLE_PER_CLASS | 2.70/5 | Poor polymorphic queries |

**Chosen:** JOINED - Normalized, strong constraints, acceptable JOIN overhead

---

## Implementation Checklist

- [x] Decision documented
- [x] Schema defined
- [ ] Migrations (V003, V004, V005)
- [ ] JPA Entities (User, Client, Consultant, Admin)
- [ ] Repositories
- [ ] Unit & Integration Tests

---

**See:** [jpa-inheritance-strategy.md](./jpa-inheritance-strategy.md) and [ER_DIAGRAM.md](./ER_DIAGRAM.md)

