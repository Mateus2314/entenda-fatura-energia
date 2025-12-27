-- ============================================================================
-- Migration: V008 - Create Analyses Table
-- Description: Creates the analyses table for storing electricity bill analysis results
--              One-to-One relationship with electricity_bills
-- Author: Backend Team
-- Date: 2025-12-27
-- Dependencies: V006 (electricity_bills)
-- ============================================================================

-- ============================================================================
-- Table: analyses
-- ============================================================================
CREATE TABLE analyses (
    -- Primary Key
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    -- Foreign Key (One-to-One with electricity_bills)
    bill_id UUID NOT NULL UNIQUE,

    -- Calculated Metrics
    average_consumption DECIMAL(10,2),
    cost_per_kwh DECIMAL(10,4),
    comparison_prev_month DECIMAL(5,2),

    -- Recommendations and Reports
    savings_tips TEXT,
    report_pdf_url TEXT,

    -- Audit Fields
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    -- Foreign Key Constraint
    CONSTRAINT fk_analyses_bill
        FOREIGN KEY (bill_id)
        REFERENCES electricity_bills(id)
        ON DELETE CASCADE,

    -- Check Constraints
    CONSTRAINT chk_average_consumption_non_negative
        CHECK (average_consumption IS NULL OR average_consumption >= 0),
    CONSTRAINT chk_cost_per_kwh_non_negative
        CHECK (cost_per_kwh IS NULL OR cost_per_kwh >= 0)
);

-- ============================================================================
-- Indexes for Performance
-- ============================================================================
-- Unique index on bill_id (enforces One-to-One relationship)
CREATE UNIQUE INDEX idx_analyses_bill_unique ON analyses(bill_id);

-- Index for date-based queries
CREATE INDEX idx_analyses_created_at ON analyses(created_at);

-- ============================================================================
-- Comments for Documentation
-- ============================================================================
COMMENT ON TABLE analyses IS 'Electricity bill analysis results - One-to-One with electricity_bills';
COMMENT ON COLUMN analyses.id IS 'Primary key (UUID)';
COMMENT ON COLUMN analyses.bill_id IS 'Foreign key to electricity_bills - UNIQUE (One-to-One)';
COMMENT ON COLUMN analyses.average_consumption IS 'Average kWh consumption over historical period';
COMMENT ON COLUMN analyses.cost_per_kwh IS 'Calculated cost per kWh from bill data';
COMMENT ON COLUMN analyses.comparison_prev_month IS 'Percentage change vs previous month (e.g., 15.5 = 15.5% increase)';
COMMENT ON COLUMN analyses.savings_tips IS 'JSON or text with savings recommendations';
COMMENT ON COLUMN analyses.report_pdf_url IS 'Path/URL to generated analysis PDF report';
COMMENT ON COLUMN analyses.created_at IS 'Timestamp of analysis creation (immutable - no updated_at)';

