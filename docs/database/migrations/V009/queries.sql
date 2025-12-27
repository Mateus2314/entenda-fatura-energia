-- ============================================================================
-- CONSULTANT_CLIENTS TABLE - SQL QUERIES REFERENCE
-- Migration: V009
-- Purpose: Common queries for consultant-client relationship management
-- ============================================================================

-- ============================================================================
-- SECTION 1: BASIC CRUD OPERATIONS
-- ============================================================================

-- -----------------------------------------------------------------------------
-- 1.1 INSERT - Assign consultant to client
-- -----------------------------------------------------------------------------
INSERT INTO consultant_clients (
    consultant_id,
    client_id,
    status
) VALUES (
    'consultant-uuid-here',
    'client-uuid-here',
    'ACTIVE'
);

-- -----------------------------------------------------------------------------
-- 1.2 INSERT - Multiple assignments
-- -----------------------------------------------------------------------------
INSERT INTO consultant_clients (consultant_id, client_id, status) VALUES
('consultant-uuid', 'client1-uuid', 'ACTIVE'),
('consultant-uuid', 'client2-uuid', 'ACTIVE'),
('consultant-uuid', 'client3-uuid', 'PENDING');

-- -----------------------------------------------------------------------------
-- 1.3 SELECT - Get all relationships
-- -----------------------------------------------------------------------------
SELECT
    consultant_id,
    client_id,
    assigned_at,
    status
FROM consultant_clients
ORDER BY assigned_at DESC;

-- -----------------------------------------------------------------------------
-- 1.4 SELECT - Get specific relationship
-- -----------------------------------------------------------------------------
SELECT * FROM consultant_clients
WHERE
    consultant_id = 'consultant-uuid'
    AND client_id = 'client-uuid';

-- -----------------------------------------------------------------------------
-- 1.5 UPDATE - Change relationship status
-- -----------------------------------------------------------------------------
UPDATE consultant_clients
SET status = 'INACTIVE'
WHERE
    consultant_id = 'consultant-uuid'
    AND client_id = 'client-uuid';

-- -----------------------------------------------------------------------------
-- 1.6 DELETE - Remove relationship
-- -----------------------------------------------------------------------------
DELETE FROM consultant_clients
WHERE
    consultant_id = 'consultant-uuid'
    AND client_id = 'client-uuid';

-- ============================================================================
-- SECTION 2: CONSULTANT QUERIES
-- ============================================================================

-- -----------------------------------------------------------------------------
-- 2.1 Get all clients of a consultant
-- -----------------------------------------------------------------------------
SELECT
    cc.client_id,
    u.name AS client_name,
    u.email AS client_email,
    c.cpf,
    cc.assigned_at,
    cc.status
FROM consultant_clients cc
INNER JOIN clients c ON cc.client_id = c.user_id
INNER JOIN users u ON c.user_id = u.id
WHERE cc.consultant_id = 'consultant-uuid'
ORDER BY cc.assigned_at DESC;

-- -----------------------------------------------------------------------------
-- 2.2 Get active clients only
-- -----------------------------------------------------------------------------
SELECT
    cc.client_id,
    u.name AS client_name,
    u.email,
    cc.assigned_at
FROM consultant_clients cc
INNER JOIN clients c ON cc.client_id = c.user_id
INNER JOIN users u ON c.user_id = u.id
WHERE
    cc.consultant_id = 'consultant-uuid'
    AND cc.status = 'ACTIVE'
ORDER BY u.name;

-- -----------------------------------------------------------------------------
-- 2.3 Count clients by status for consultant
-- -----------------------------------------------------------------------------
SELECT
    cc.status,
    COUNT(*) AS client_count
FROM consultant_clients cc
WHERE cc.consultant_id = 'consultant-uuid'
GROUP BY cc.status
ORDER BY cc.status;

-- -----------------------------------------------------------------------------
-- 2.4 Get consultant's clients with bill count
-- -----------------------------------------------------------------------------
SELECT
    cc.client_id,
    u.name AS client_name,
    COUNT(eb.id) AS bill_count,
    cc.status
FROM consultant_clients cc
INNER JOIN clients c ON cc.client_id = c.user_id
INNER JOIN users u ON c.user_id = u.id
LEFT JOIN electricity_bills eb ON c.user_id = eb.client_id
WHERE cc.consultant_id = 'consultant-uuid'
GROUP BY cc.client_id, u.name, cc.status
ORDER BY bill_count DESC;

-- -----------------------------------------------------------------------------
-- 2.5 Get recently assigned clients
-- -----------------------------------------------------------------------------
SELECT
    cc.client_id,
    u.name AS client_name,
    cc.assigned_at,
    cc.status
FROM consultant_clients cc
INNER JOIN clients c ON cc.client_id = c.user_id
INNER JOIN users u ON c.user_id = u.id
WHERE
    cc.consultant_id = 'consultant-uuid'
    AND cc.assigned_at >= (CURRENT_DATE - INTERVAL '30 days')
ORDER BY cc.assigned_at DESC;

-- ============================================================================
-- SECTION 3: CLIENT QUERIES
-- ============================================================================

-- -----------------------------------------------------------------------------
-- 3.1 Get all consultants of a client
-- -----------------------------------------------------------------------------
SELECT
    cc.consultant_id,
    u.name AS consultant_name,
    cons.company,
    u.email AS consultant_email,
    cc.assigned_at,
    cc.status
FROM consultant_clients cc
INNER JOIN consultants cons ON cc.consultant_id = cons.user_id
INNER JOIN users u ON cons.user_id = u.id
WHERE cc.client_id = 'client-uuid'
ORDER BY cc.assigned_at DESC;

-- -----------------------------------------------------------------------------
-- 3.2 Get current active consultant
-- -----------------------------------------------------------------------------
SELECT
    cc.consultant_id,
    u.name AS consultant_name,
    cons.company,
    cons.consultant_name AS consultant_person,
    u.email,
    u.phone
FROM consultant_clients cc
INNER JOIN consultants cons ON cc.consultant_id = cons.user_id
INNER JOIN users u ON cons.user_id = u.id
WHERE
    cc.client_id = 'client-uuid'
    AND cc.status = 'ACTIVE'
ORDER BY cc.assigned_at DESC
LIMIT 1;

-- -----------------------------------------------------------------------------
-- 3.3 Get pending consultant invitations
-- -----------------------------------------------------------------------------
SELECT
    cc.consultant_id,
    u.name AS consultant_name,
    cons.company,
    cc.assigned_at
FROM consultant_clients cc
INNER JOIN consultants cons ON cc.consultant_id = cons.user_id
INNER JOIN users u ON cons.user_id = u.id
WHERE
    cc.client_id = 'client-uuid'
    AND cc.status = 'PENDING'
ORDER BY cc.assigned_at DESC;

-- ============================================================================
-- SECTION 4: ANALYTICAL QUERIES
-- ============================================================================

-- -----------------------------------------------------------------------------
-- 4.1 Count active clients per consultant
-- -----------------------------------------------------------------------------
SELECT
    cons.user_id,
    u.name AS consultant_name,
    cons.company,
    COUNT(CASE WHEN cc.status = 'ACTIVE' THEN 1 END) AS active_clients,
    COUNT(cc.client_id) AS total_clients
FROM consultants cons
INNER JOIN users u ON cons.user_id = u.id
LEFT JOIN consultant_clients cc ON cons.user_id = cc.consultant_id
GROUP BY cons.user_id, u.name, cons.company
ORDER BY active_clients DESC;

-- -----------------------------------------------------------------------------
-- 4.2 Find clients with multiple consultants
-- -----------------------------------------------------------------------------
SELECT
    c.user_id AS client_id,
    u.name AS client_name,
    COUNT(cc.consultant_id) AS consultant_count
FROM clients c
INNER JOIN users u ON c.user_id = u.id
INNER JOIN consultant_clients cc ON c.user_id = cc.client_id
WHERE cc.status = 'ACTIVE'
GROUP BY c.user_id, u.name
HAVING COUNT(cc.consultant_id) > 1
ORDER BY consultant_count DESC;

-- -----------------------------------------------------------------------------
-- 4.3 Find consultants without clients
-- -----------------------------------------------------------------------------
SELECT
    cons.user_id,
    u.name AS consultant_name,
    cons.company
FROM consultants cons
INNER JOIN users u ON cons.user_id = u.id
LEFT JOIN consultant_clients cc ON cons.user_id = cc.consultant_id AND cc.status = 'ACTIVE'
WHERE cc.consultant_id IS NULL
ORDER BY cons.registration_date DESC;

-- -----------------------------------------------------------------------------
-- 4.4 Find clients without consultants
-- -----------------------------------------------------------------------------
SELECT
    c.user_id,
    u.name AS client_name,
    u.email,
    c.registration_date
FROM clients c
INNER JOIN users u ON c.user_id = u.id
LEFT JOIN consultant_clients cc ON c.user_id = cc.client_id AND cc.status = 'ACTIVE'
WHERE cc.client_id IS NULL
ORDER BY c.registration_date DESC;

-- -----------------------------------------------------------------------------
-- 4.5 Relationship status distribution
-- -----------------------------------------------------------------------------
SELECT
    status,
    COUNT(*) AS count,
    ROUND(COUNT(*) * 100.0 / SUM(COUNT(*)) OVER (), 2) AS percentage
FROM consultant_clients
GROUP BY status
ORDER BY count DESC;

-- -----------------------------------------------------------------------------
-- 4.6 Monthly assignment trend
-- -----------------------------------------------------------------------------
SELECT
    DATE_TRUNC('month', assigned_at) AS month,
    COUNT(*) AS new_assignments,
    COUNT(CASE WHEN status = 'ACTIVE' THEN 1 END) AS active_count
FROM consultant_clients
GROUP BY month
ORDER BY month DESC;

-- ============================================================================
-- SECTION 5: VALIDATION QUERIES
-- ============================================================================

-- -----------------------------------------------------------------------------
-- 5.1 Find duplicate relationships (should return 0 rows)
-- -----------------------------------------------------------------------------
SELECT
    consultant_id,
    client_id,
    COUNT(*) AS duplicate_count
FROM consultant_clients
GROUP BY consultant_id, client_id
HAVING COUNT(*) > 1;

-- -----------------------------------------------------------------------------
-- 5.2 Find orphaned consultants (consultant doesn't exist)
-- -----------------------------------------------------------------------------
SELECT
    cc.consultant_id
FROM consultant_clients cc
LEFT JOIN consultants cons ON cc.consultant_id = cons.user_id
WHERE cons.user_id IS NULL;

-- -----------------------------------------------------------------------------
-- 5.3 Find orphaned clients (client doesn't exist)
-- -----------------------------------------------------------------------------
SELECT
    cc.client_id
FROM consultant_clients cc
LEFT JOIN clients c ON cc.client_id = c.user_id
WHERE c.user_id IS NULL;

-- -----------------------------------------------------------------------------
-- 5.4 Find invalid status values (should return 0 rows)
-- -----------------------------------------------------------------------------
SELECT
    consultant_id,
    client_id,
    status
FROM consultant_clients
WHERE status NOT IN ('ACTIVE', 'INACTIVE', 'PENDING');

-- -----------------------------------------------------------------------------
-- 5.5 Verify cascade delete behavior
-- -----------------------------------------------------------------------------
-- This would be tested by:
-- 1. Creating a relationship
-- 2. Deleting the consultant or client
-- 3. Verifying the relationship is also deleted

-- ============================================================================
-- SECTION 6: BUSINESS LOGIC QUERIES
-- ============================================================================

-- -----------------------------------------------------------------------------
-- 6.1 Check if consultant can access client's bills
-- -----------------------------------------------------------------------------
SELECT EXISTS(
    SELECT 1
    FROM consultant_clients
    WHERE
        consultant_id = 'consultant-uuid'
        AND client_id = 'client-uuid'
        AND status = 'ACTIVE'
) AS has_access;

-- -----------------------------------------------------------------------------
-- 6.2 Get accessible bills for consultant
-- -----------------------------------------------------------------------------
SELECT
    eb.*
FROM electricity_bills eb
INNER JOIN consultant_clients cc ON eb.client_id = cc.client_id
WHERE
    cc.consultant_id = 'consultant-uuid'
    AND cc.status = 'ACTIVE'
ORDER BY eb.reference_month DESC;

-- -----------------------------------------------------------------------------
-- 6.3 Accept pending invitation (client action)
-- -----------------------------------------------------------------------------
UPDATE consultant_clients
SET status = 'ACTIVE'
WHERE
    consultant_id = 'consultant-uuid'
    AND client_id = 'client-uuid'
    AND status = 'PENDING';

-- -----------------------------------------------------------------------------
-- 6.4 Decline pending invitation (client action)
-- -----------------------------------------------------------------------------
DELETE FROM consultant_clients
WHERE
    consultant_id = 'consultant-uuid'
    AND client_id = 'client-uuid'
    AND status = 'PENDING';

-- -----------------------------------------------------------------------------
-- 6.5 Terminate consultant relationship (either party)
-- -----------------------------------------------------------------------------
UPDATE consultant_clients
SET status = 'INACTIVE'
WHERE
    consultant_id = 'consultant-uuid'
    AND client_id = 'client-uuid'
    AND status = 'ACTIVE';

-- ============================================================================
-- SECTION 7: REPOSITORY PATTERN QUERIES (for Spring Data JPA)
-- ============================================================================

-- -----------------------------------------------------------------------------
-- 7.1 findByConsultantId
-- -----------------------------------------------------------------------------
SELECT * FROM consultant_clients
WHERE consultant_id = ?
ORDER BY assigned_at DESC;

-- -----------------------------------------------------------------------------
-- 7.2 findByClientId
-- -----------------------------------------------------------------------------
SELECT * FROM consultant_clients
WHERE client_id = ?
ORDER BY assigned_at DESC;

-- -----------------------------------------------------------------------------
-- 7.3 findByConsultantIdAndStatus
-- -----------------------------------------------------------------------------
SELECT * FROM consultant_clients
WHERE consultant_id = ? AND status = ?
ORDER BY assigned_at DESC;

-- -----------------------------------------------------------------------------
-- 7.4 findByClientIdAndStatus
-- -----------------------------------------------------------------------------
SELECT * FROM consultant_clients
WHERE client_id = ? AND status = ?
ORDER BY assigned_at DESC;

-- -----------------------------------------------------------------------------
-- 7.5 existsByConsultantIdAndClientId
-- -----------------------------------------------------------------------------
SELECT EXISTS(
    SELECT 1 FROM consultant_clients
    WHERE consultant_id = ? AND client_id = ?
);

-- -----------------------------------------------------------------------------
-- 7.6 existsByConsultantIdAndClientIdAndStatus
-- -----------------------------------------------------------------------------
SELECT EXISTS(
    SELECT 1 FROM consultant_clients
    WHERE consultant_id = ? AND client_id = ? AND status = ?
);

-- -----------------------------------------------------------------------------
-- 7.7 countByConsultantIdAndStatus
-- -----------------------------------------------------------------------------
SELECT COUNT(*) FROM consultant_clients
WHERE consultant_id = ? AND status = ?;

-- -----------------------------------------------------------------------------
-- 7.8 deleteByConsultantIdAndClientId
-- -----------------------------------------------------------------------------
DELETE FROM consultant_clients
WHERE consultant_id = ? AND client_id = ?;

-- ============================================================================
-- SECTION 8: MAINTENANCE QUERIES
-- ============================================================================

-- -----------------------------------------------------------------------------
-- 8.1 Get table statistics
-- -----------------------------------------------------------------------------
SELECT
    COUNT(*) AS total_relationships,
    COUNT(DISTINCT consultant_id) AS unique_consultants,
    COUNT(DISTINCT client_id) AS unique_clients,
    COUNT(CASE WHEN status = 'ACTIVE' THEN 1 END) AS active_count,
    COUNT(CASE WHEN status = 'INACTIVE' THEN 1 END) AS inactive_count,
    COUNT(CASE WHEN status = 'PENDING' THEN 1 END) AS pending_count,
    MIN(assigned_at) AS oldest_assignment,
    MAX(assigned_at) AS newest_assignment
FROM consultant_clients;

-- -----------------------------------------------------------------------------
-- 8.2 Check index usage
-- -----------------------------------------------------------------------------
SELECT
    schemaname,
    tablename,
    indexname,
    idx_scan AS index_scans,
    idx_tup_read AS tuples_read,
    idx_tup_fetch AS tuples_fetched
FROM pg_stat_user_indexes
WHERE tablename = 'consultant_clients'
ORDER BY idx_scan DESC;

-- -----------------------------------------------------------------------------
-- 8.3 Find stale PENDING invitations (older than 30 days)
-- -----------------------------------------------------------------------------
SELECT
    cc.consultant_id,
    cc.client_id,
    cc.assigned_at,
    AGE(CURRENT_DATE, cc.assigned_at::date) AS age
FROM consultant_clients cc
WHERE
    cc.status = 'PENDING'
    AND cc.assigned_at < (CURRENT_DATE - INTERVAL '30 days')
ORDER BY cc.assigned_at ASC;

-- -----------------------------------------------------------------------------
-- 8.4 Cleanup old INACTIVE relationships (example)
-- -----------------------------------------------------------------------------
-- DELETE FROM consultant_clients
-- WHERE
--     status = 'INACTIVE'
--     AND assigned_at < (CURRENT_DATE - INTERVAL '2 years');

-- ============================================================================
-- END OF QUERIES
-- ============================================================================

