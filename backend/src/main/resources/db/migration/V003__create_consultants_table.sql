-- ============================================================================
-- Migration: V003 - Create Consultants Base Table
-- Description: Creates the base Consultants table for JOINED inheritance strategy
-- Author: Mateus De La Fuente Cezar
-- Date: 2025-12-15
-- ============================================================================

CREATE TABLE consultants (
                             user_id UUID PRIMARY KEY,
                             consultant_name VARCHAR(255) NOT NULL,
                             company VARCHAR(255) NOT NULL,
                             cnpj VARCHAR(14) NOT NULL UNIQUE,
                             address VARCHAR(500),
                             city VARCHAR(100),
                             state VARCHAR(2),
                             zip_code VARCHAR(8),
                             registration_number VARCHAR(50),
                             company_logo VARCHAR(255),
                             registration_date DATE NOT NULL DEFAULT CURRENT_DATE,
                             CONSTRAINT fk_consultant_user FOREIGN KEY (user_id)
                                 REFERENCES users(id) ON DELETE CASCADE,
                             CONSTRAINT chk_cnpj_format CHECK (cnpj ~ '^\d{14}$'),
    CONSTRAINT chk_state_format CHECK (state ~ '^[A-Z]{2}$'),
    CONSTRAINT chk_zip_code_format CHECK (zip_code ~ '^\d{8}$')
);

-- Create indexes for performance
CREATE INDEX idx_consultants_cnpj ON consultants(cnpj);
CREATE INDEX idx_consultants_company ON consultants(company);
CREATE INDEX idx_consultants_registration_date ON consultants(registration_date);

-- Add comments for documentation
COMMENT ON TABLE consultants IS 'Consultant-specific data extending the users table';
COMMENT ON COLUMN consultants.user_id IS 'Foreign key to users table (same as primary key)';
COMMENT ON COLUMN consultants.cnpj IS 'Brazilian company tax ID (CNPJ) - 14 digits only';
COMMENT ON COLUMN consultants.consultant_name IS 'Full name of the consultant';
COMMENT ON COLUMN consultants.company IS 'Company name';
COMMENT ON COLUMN consultants.registration_date IS 'Date when the consultant was registered';
