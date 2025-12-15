-- ============================================================================
-- Migration: V002 - Create Clients Base Table
-- Description: Creates the base Clients table for JOINED inheritance strategy
-- Author: Mateus De La Fuente Cezar
-- Date: 2025-12-15
-- ============================================================================

CREATE TABLE clients (
                         user_id UUID PRIMARY KEY,
                         address VARCHAR(500) NOT NULL,
                         cpf VARCHAR(11) NOT NULL UNIQUE,
                         registration_date DATE NOT NULL DEFAULT CURRENT_DATE,
                         CONSTRAINT fk_client_user FOREIGN KEY (user_id)
                             REFERENCES users(id) ON DELETE CASCADE,
                         CONSTRAINT chk_cpf_format CHECK (cpf ~ '^\d{11}$')
    );

-- Create indexes for performance
CREATE INDEX idx_clients_cpf ON clients(cpf);
CREATE INDEX idx_clients_registration_date ON clients(registration_date);

-- Add comment for documentation
COMMENT ON TABLE clients IS 'Client-specific data extending the users table';
COMMENT ON COLUMN clients.user_id IS 'Foreign key to users table (same as primary key)';
COMMENT ON COLUMN clients.cpf IS 'Brazilian individual tax ID (CPF) - 11 digits only';
COMMENT ON COLUMN clients.registration_date IS 'Date when the client was registered';
