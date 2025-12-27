-- ============================================================================
-- Migration: V006 - Create Electricity Bills Table
-- Description: Creates the electricity_bills table for managing energy bills
--              Relates to clients, consultants, and tariffs
-- Author: Backend Team
-- Date: 2025-12-27
-- Dependencies: V002 (clients), V003 (consultants), V005 (tariffs)
-- ============================================================================

-- ============================================================================
-- Table: electricity_bills
-- ============================================================================
CREATE TABLE electricity_bills (
    -- Primary Key
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    -- Foreign Keys (Relationships)
    client_id UUID NOT NULL,
    consultant_id UUID,
    tariff_id UUID NOT NULL,

    -- Bill Data
    reference_month DATE NOT NULL,
    due_date DATE NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL CHECK (total_amount >= 0),
    consumption_kwh DECIMAL(10,2) NOT NULL CHECK (consumption_kwh >= 0),

    -- Additional Information
    pdf_url TEXT,
    installation_number VARCHAR(50),
    invoice_number VARCHAR(100),

    -- Audit Fields
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

    -- Foreign Key Constraints
    CONSTRAINT fk_bills_client
        FOREIGN KEY (client_id)
        REFERENCES clients(user_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_bills_consultant
        FOREIGN KEY (consultant_id)
        REFERENCES consultants(user_id)
        ON DELETE SET NULL,

    CONSTRAINT fk_bills_tariff
        FOREIGN KEY (tariff_id)
        REFERENCES tariffs(id)
        ON DELETE RESTRICT,

    -- Check Constraints
    CONSTRAINT chk_due_date_after_reference
        CHECK (due_date > reference_month)
);

-- ============================================================================
-- Indexes for Performance
-- ============================================================================
-- Foreign key indexes
CREATE INDEX idx_bills_client_id ON electricity_bills(client_id);
CREATE INDEX idx_bills_consultant_id ON electricity_bills(consultant_id);
CREATE INDEX idx_bills_tariff_id ON electricity_bills(tariff_id);

-- Business logic indexes
CREATE INDEX idx_bills_reference_month ON electricity_bills(reference_month);
CREATE INDEX idx_bills_due_date ON electricity_bills(due_date);
CREATE INDEX idx_bills_created_at ON electricity_bills(created_at);

-- Composite indexes for common queries
CREATE INDEX idx_bills_client_reference ON electricity_bills(client_id, reference_month);
CREATE INDEX idx_bills_consultant_reference ON electricity_bills(consultant_id, reference_month);

-- ============================================================================
-- Trigger for automatic updated_at
-- ============================================================================
CREATE TRIGGER update_electricity_bills_updated_at
    BEFORE UPDATE ON electricity_bills
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- ============================================================================
-- Comments for Documentation
-- ============================================================================
COMMENT ON TABLE electricity_bills IS 'Electricity bills - stores energy consumption invoices';
COMMENT ON COLUMN electricity_bills.id IS 'Primary key (UUID)';
COMMENT ON COLUMN electricity_bills.client_id IS 'Foreign key to clients (required)';
COMMENT ON COLUMN electricity_bills.consultant_id IS 'Foreign key to consultants (optional)';
COMMENT ON COLUMN electricity_bills.tariff_id IS 'Foreign key to tariffs (required)';
COMMENT ON COLUMN electricity_bills.reference_month IS 'Billing reference month (YYYY-MM-01)';
COMMENT ON COLUMN electricity_bills.due_date IS 'Bill payment due date';
COMMENT ON COLUMN electricity_bills.total_amount IS 'Total bill amount in currency';
COMMENT ON COLUMN electricity_bills.consumption_kwh IS 'Energy consumption in kilowatt-hours';
COMMENT ON COLUMN electricity_bills.pdf_url IS 'Path/URL to bill PDF file';
COMMENT ON COLUMN electricity_bills.installation_number IS 'Utility installation/UC number';
COMMENT ON COLUMN electricity_bills.invoice_number IS 'Invoice/bill number from utility';
COMMENT ON COLUMN electricity_bills.created_at IS 'Timestamp of record creation';
COMMENT ON COLUMN electricity_bills.updated_at IS 'Timestamp of last update (auto-updated)';
