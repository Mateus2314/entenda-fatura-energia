-- ============================================================================
-- Migration: V002 - Create Clients Base Table
-- Description: Creates the base Clients table for JOINED inheritance strategy
-- Author: Mateus De La Fuente Cezar
-- Date: 2025-12-15
-- Updated: 2025-12-27 - Added city, state, zip_code columns
-- ============================================================================

CREATE TABLE clients (
    user_id UUID PRIMARY KEY,
    address VARCHAR(500) NOT NULL,
    city VARCHAR(100),
    state VARCHAR(2),
    zip_code VARCHAR(10),
    cpf VARCHAR(11) NOT NULL UNIQUE,
    registration_date DATE NOT NULL DEFAULT CURRENT_DATE,

    CONSTRAINT fk_client_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_cpf_format CHECK (cpf ~ '^\d{11}$'),
    CONSTRAINT chk_state_format CHECK (state IS NULL OR state ~ '^[A-Z]{2}$'),
    CONSTRAINT chk_zipcode_format CHECK (zip_code IS NULL OR zip_code ~ '^\d{5}-?\d{3}$')
);

-- Create indexes for performance
CREATE INDEX idx_clients_cpf ON clients(cpf);
CREATE INDEX idx_clients_registration_date ON clients(registration_date);
CREATE INDEX idx_clients_city ON clients(city);
CREATE INDEX idx_clients_state ON clients(state);
CREATE INDEX idx_clients_zipcode ON clients(zip_code);

-- Add comment for documentation
COMMENT ON TABLE clients IS 'Client-specific data extending the users table';
COMMENT ON COLUMN clients.user_id IS 'Foreign key to users table (same as primary key)';
COMMENT ON COLUMN clients.address IS 'Full address of the client';
COMMENT ON COLUMN clients.city IS 'City where client lives (optional)';
COMMENT ON COLUMN clients.state IS 'Brazilian state code - 2 uppercase letters (optional)';
COMMENT ON COLUMN clients.zip_code IS 'ZIP code in format 00000-000 or 00000000 (optional)';
COMMENT ON COLUMN clients.cpf IS 'Brazilian individual tax ID (CPF) - 11 digits only';
COMMENT ON COLUMN clients.registration_date IS 'Date when the client was registered';


