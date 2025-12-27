-- ============================================================================
-- Migration: V003 - Create Consultants Base Table
-- Description: Creates the base Consultants table for JOINED inheritance strategy
-- Author: Mateus De La Fuente Cezar
-- Date: 2025-12-15
-- Updated: 2025-12-27 - Fixed column types and constraints
-- ============================================================================

CREATE TABLE consultants (
    user_id UUID PRIMARY KEY,
    consultant_name VARCHAR(255) NOT NULL,
    company VARCHAR(255) NOT NULL,
    cnpj VARCHAR(14) NOT NULL UNIQUE,
    registration_number VARCHAR(50),
    address VARCHAR(500) NOT NULL,
    city VARCHAR(100),
    state VARCHAR(2),
    zip_code VARCHAR(10),
    company_logo TEXT,
    registration_date DATE NOT NULL DEFAULT CURRENT_DATE,

    CONSTRAINT fk_consultant_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_cnpj_format CHECK (cnpj ~ '^\d{14}$'),
    CONSTRAINT chk_state_format CHECK (state IS NULL OR state ~ '^[A-Z]{2}$'),
    CONSTRAINT chk_zipcode_format CHECK (zip_code IS NULL OR zip_code ~ '^\d{5}-?\d{3}$')
);

-- Create indexes for performance
CREATE INDEX idx_consultants_cnpj ON consultants(cnpj);
CREATE INDEX idx_consultants_company ON consultants(company);
CREATE INDEX idx_consultants_registration_date ON consultants(registration_date);
CREATE INDEX idx_consultants_city ON consultants(city);
CREATE INDEX idx_consultants_state ON consultants(state);

-- Add comments for documentation
COMMENT ON TABLE consultants IS 'Consultant-specific data extending the users table';
COMMENT ON COLUMN consultants.user_id IS 'Foreign key to users table (same as primary key)';
COMMENT ON COLUMN consultants.consultant_name IS 'Full name of the consultant';
COMMENT ON COLUMN consultants.company IS 'Company name';
COMMENT ON COLUMN consultants.cnpj IS 'Brazilian company tax ID (CNPJ) - 14 digits only';
COMMENT ON COLUMN consultants.registration_number IS 'Professional registration number (optional)';
COMMENT ON COLUMN consultants.address IS 'Full address of the consultant/company';
COMMENT ON COLUMN consultants.city IS 'City (optional)';
COMMENT ON COLUMN consultants.state IS 'Brazilian state code - 2 uppercase letters (optional)';
COMMENT ON COLUMN consultants.zip_code IS 'ZIP code in format 00000-000 or 00000000 (optional)';
COMMENT ON COLUMN consultants.company_logo IS 'Base64 encoded logo or URL (optional)';
COMMENT ON COLUMN consultants.registration_date IS 'Date when the consultant was registered';


