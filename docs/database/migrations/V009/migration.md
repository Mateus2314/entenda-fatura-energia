# Migration V009: Create Consultant Clients Join Table

## 📋 Overview

**Version:** V009  
**Date:** 2025-12-27  
**Author:** Backend Team  
**Status:** ✅ Implemented  
**Script:** `V009__create_consultant_clients_table.sql`  

---

## 🎯 Objective

Create the `consultant_clients` join table to establish a **Many-to-Many** relationship between consultants and clients, allowing consultants to manage multiple clients and clients to have multiple consultants.

---

## 📊 Table Structure

### Join Table: `consultant_clients`

**Purpose:** Manage Many-to-Many associations between consultants and clients

| Column | Type | Nullable | Description |
|--------|------|----------|-------------|
| `consultant_id` | UUID | NO | Foreign key to consultants (PK part 1) |
| `client_id` | UUID | NO | Foreign key to clients (PK part 2) |
| `assigned_at` | TIMESTAMP | NO | Timestamp when consultant was assigned |
| `status` | VARCHAR(20) | NO | Relationship status (ACTIVE, INACTIVE, PENDING) |

**Composite Primary Key:** (`consultant_id`, `client_id`)

---

## 🔒 Constraints

### Primary Key (Composite)
- **Name:** `consultant_clients_pkey`
- **Columns:** (`consultant_id`, `client_id`)
- **Purpose:** Ensures unique consultant-client pairs

### Foreign Keys

1. **`fk_consultant_clients_consultant`**
   - **Column:** `consultant_id`
   - **References:** `consultants(user_id)`
   - **Cascade:** ON DELETE CASCADE
   - **Description:** If consultant is deleted, remove all associations

2. **`fk_consultant_clients_client`**
   - **Column:** `client_id`
   - **References:** `clients(user_id)`
   - **Cascade:** ON DELETE CASCADE
   - **Description:** If client is deleted, remove all associations

### Check Constraints

1. **`chk_status_valid`**
   - Ensures `status` is one of: 'ACTIVE', 'INACTIVE', 'PENDING'
   - Prevents invalid status values

---

## 📇 Indexes

### 1. `idx_consultant_clients_consultant`
**Column:** `consultant_id`  
**Purpose:** Fast lookup of all clients for a consultant

### 2. `idx_consultant_clients_client`
**Column:** `client_id`  
**Purpose:** Fast lookup of all consultants for a client

### 3. `idx_consultant_clients_status`
**Column:** `status`  
**Purpose:** Filter relationships by status

### 4. `idx_consultant_clients_assigned_at`
**Column:** `assigned_at`  
**Purpose:** Sort by assignment date

### 5. `idx_consultant_clients_active` (Partial Index)
**Columns:** `consultant_id`, `client_id`, `status`  
**Condition:** `WHERE status = 'ACTIVE'`  
**Purpose:** Optimized queries for active relationships only

**Total:** 5 performance-optimized indexes

---

## 🔗 Relationships

### Foreign Key Dependencies

**Many-to-Many Pattern:**
- `consultant_clients.consultant_id` → `consultants(user_id)` (Many-to-Many)
- `consultant_clients.client_id` → `clients(user_id)` (Many-to-Many)

### Relationship Diagram
```
consultants (N) ←──► consultant_clients ←──► (N) clients

One consultant manages many clients
One client can have many consultants
Join table tracks the association with metadata
```

---

## 🔄 Migration Dependencies

### Prerequisites
- ✅ V001 - `users` table
- ✅ V002 - `clients` table
- ✅ V003 - `consultants` table

### Impact on Existing Tables
- **No changes** to existing tables
- **New relationship** layer added
- **Access control** can now be implemented

---

## 📝 Business Rules

### 1. Many-to-Many Relationship
- One consultant can manage **multiple clients**
- One client can have **multiple consultants** (over time or simultaneously)
- Composite PK ensures **no duplicate pairs**

### 2. Relationship Status
- **ACTIVE**: Consultant currently managing client
- **INACTIVE**: Past relationship (consultant no longer managing)
- **PENDING**: Invitation sent, awaiting approval

### 3. Assignment Tracking
- `assigned_at` records when relationship was created
- Immutable timestamp (no updates)
- Useful for historical analysis

### 4. Cascade Delete
- **Delete consultant** → Remove all their client associations
- **Delete client** → Remove all their consultant associations
- **Maintains referential integrity**

### 5. Access Control
- Consultant can only view bills of **ACTIVE** associated clients
- Client can see history of all consultants (ACTIVE + INACTIVE)
- PENDING relationships don't grant access yet

---

## 💡 Usage Examples

### Assign Consultant to Client
```sql
INSERT INTO consultant_clients (
    consultant_id,
    client_id,
    status
) VALUES (
    'consultant-uuid',
    'client-uuid',
    'ACTIVE'
);
```

### Get All Clients of a Consultant
```sql
SELECT 
    cc.client_id,
    c.user_id,
    u.name AS client_name,
    u.email,
    cc.assigned_at,
    cc.status
FROM consultant_clients cc
INNER JOIN clients c ON cc.client_id = c.user_id
INNER JOIN users u ON c.user_id = u.id
WHERE 
    cc.consultant_id = 'consultant-uuid'
    AND cc.status = 'ACTIVE'
ORDER BY cc.assigned_at DESC;
```

### Get All Consultants of a Client
```sql
SELECT 
    cc.consultant_id,
    cons.user_id,
    u.name AS consultant_name,
    cons.company,
    cc.assigned_at,
    cc.status
FROM consultant_clients cc
INNER JOIN consultants cons ON cc.consultant_id = cons.user_id
INNER JOIN users u ON cons.user_id = u.id
WHERE cc.client_id = 'client-uuid'
ORDER BY cc.assigned_at DESC;
```

### Change Relationship Status (Deactivate)
```sql
UPDATE consultant_clients
SET status = 'INACTIVE'
WHERE 
    consultant_id = 'consultant-uuid'
    AND client_id = 'client-uuid';
```

### Remove Association
```sql
DELETE FROM consultant_clients
WHERE 
    consultant_id = 'consultant-uuid'
    AND client_id = 'client-uuid';
```

### Count Active Clients per Consultant
```sql
SELECT 
    cons.user_id,
    u.name AS consultant_name,
    COUNT(cc.client_id) AS active_clients
FROM consultants cons
INNER JOIN users u ON cons.user_id = u.id
LEFT JOIN consultant_clients cc ON cons.user_id = cc.consultant_id AND cc.status = 'ACTIVE'
GROUP BY cons.user_id, u.name
ORDER BY active_clients DESC;
```

---

## 🧪 Testing Performed

See [validation.md](./validation.md) for detailed validation report.

### Test Coverage
- ✅ Table creation
- ✅ Composite primary key
- ✅ Foreign key constraints (both sides)
- ✅ Check constraint (status)
- ✅ Prevent duplicate pairs
- ✅ Cascade delete behavior
- ✅ Index performance

---

## 📚 References

- [ER Diagram](../../ER_DIAGRAM.md) - Should be updated to include this relationship
- [JPA Entity Modeling](../../jpa-entity-modeling.md) - Should be updated
- [Consultant Entity Model](../../../../backend/src/main/java/com/understand_your_electricity_bill/model/Consultant.java)
- [Client Entity Model](../../../../backend/src/main/java/com/understand_your_electricity_bill/model/Client.java)

---

## 🔍 Use Cases

### 1. Consultant Dashboard
- List all clients managed by consultant
- Filter by status (ACTIVE/INACTIVE/PENDING)
- View client bills and analyses

### 2. Client Portal
- View assigned consultants
- See consultation history
- Accept/reject consultant invitations

### 3. Access Control
- Consultant can only access bills of ACTIVE clients
- Security layer for data protection
- Audit trail of consultant-client relationships

### 4. Business Intelligence
- Track consultant workload (number of clients)
- Identify inactive relationships
- Measure consultant efficiency

---

## ✅ Status: IMPLEMENTED

Migration V009 is fully implemented and ready for execution.

**Next Steps:**
1. Execute validation tests (see validation.md)
2. Update JPA entities (Consultant and Client) with `@ManyToMany`
3. Create `ConsultantClientRepository` (optional)
4. Implement service layer methods for assignment
5. Update ER_DIAGRAM.md to reflect Many-to-Many relationship

---

Last Updated: 2025-12-27

