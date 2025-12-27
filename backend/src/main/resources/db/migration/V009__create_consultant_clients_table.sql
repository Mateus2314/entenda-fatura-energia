-- ============================================================================
-- Migration: V009 - Create Consultant Clients Join Table
-- Description: Creates the consultant_clients join table for Many-to-Many relationship
--              between consultants and clients
-- Author: Backend Team
-- Date: 2025-12-27
-- Dependencies: V002 (clients), V003 (consultants)
-- ============================================================================

-- ============================================================================
-- Table: consultant_clients (Join Table)
-- ============================================================================
CREATE TABLE consultant_clients (
    -- Composite Primary Key (consultant_id + client_id)
    consultant_id UUID NOT NULL,
    client_id UUID NOT NULL,

    -- Association Metadata
    assigned_at TIMESTAMP NOT NULL DEFAULT NOW(),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    -- Primary Key (Composite)
    PRIMARY KEY (consultant_id, client_id),

    -- Foreign Key Constraints
    CONSTRAINT fk_consultant_clients_consultant
        FOREIGN KEY (consultant_id)
        REFERENCES consultants(user_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_consultant_clients_client
        FOREIGN KEY (client_id)
        REFERENCES clients(user_id)
        ON DELETE CASCADE,

    -- Check Constraints
    CONSTRAINT chk_status_valid
        CHECK (status IN ('ACTIVE', 'INACTIVE', 'PENDING'))
);

-- ============================================================================
-- Indexes for Performance
-- ============================================================================
-- Index on consultant_id (find all clients of a consultant)
CREATE INDEX idx_consultant_clients_consultant ON consultant_clients(consultant_id);

-- Index on client_id (find all consultants of a client)
CREATE INDEX idx_consultant_clients_client ON consultant_clients(client_id);

-- Index on status (filter by status)
CREATE INDEX idx_consultant_clients_status ON consultant_clients(status);

-- Index on assigned_at (sort by assignment date)
CREATE INDEX idx_consultant_clients_assigned_at ON consultant_clients(assigned_at);

-- Composite index for active relationships
CREATE INDEX idx_consultant_clients_active ON consultant_clients(consultant_id, client_id, status)
WHERE status = 'ACTIVE';

-- ============================================================================
-- Comments for Documentation
-- ============================================================================
COMMENT ON TABLE consultant_clients IS 'Many-to-Many relationship between consultants and clients';
COMMENT ON COLUMN consultant_clients.consultant_id IS 'Foreign key to consultants (part of composite PK)';
COMMENT ON COLUMN consultant_clients.client_id IS 'Foreign key to clients (part of composite PK)';
COMMENT ON COLUMN consultant_clients.assigned_at IS 'Timestamp when consultant was assigned to client';
COMMENT ON COLUMN consultant_clients.status IS 'Relationship status: ACTIVE, INACTIVE, PENDING';

