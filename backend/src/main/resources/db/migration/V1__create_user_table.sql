-- ============================================================================
-- Migration: V001 - Create Users Base Table
-- Description: Creates the base users table for JOINED inheritance strategy
-- Author: Mateus De La Fuente Cezar
-- Date: 2025-12-14
-- ============================================================================

-- Enable UUID extension (if not already enabled)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create ENUM for user types
CREATE TYPE user_type AS ENUM ('CLIENT', 'CONSULTANT', 'ADMIN');

-- Create ENUM for user status
CREATE TYPE user_status AS ENUM ('ACTIVE', 'INACTIVE', 'SUSPENDED', 'PENDING_VERIFICATION');

-- ============================================================================
-- Table: users (Base table for inheritance)
-- ============================================================================
CREATE TABLE users (
    -- Primary Key
                       id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    -- Authentication fields
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,

    -- User type (discriminator for inheritance)
                       user_type user_type NOT NULL,

    -- Personal information
                       name VARCHAR(255) NOT NULL,
                       phone VARCHAR(20),

    -- Status
                       status user_status NOT NULL DEFAULT 'PENDING_VERIFICATION',

    -- Audit fields
                       created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                       updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

    -- Constraints
                       CONSTRAINT email_format_check CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    CONSTRAINT phone_format_check CHECK (phone IS NULL OR phone ~ '^\+?[0-9]{10,15}$')
);

-- ============================================================================
-- Indexes for performance
-- ============================================================================
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_user_type ON users(user_type);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_created_at ON users(created_at);

-- ============================================================================
-- Trigger for automatic updated_at
-- ============================================================================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- ============================================================================
-- Comments for documentation
-- ============================================================================
COMMENT ON TABLE users IS 'Base table for user hierarchy using JOINED inheritance strategy';
COMMENT ON COLUMN users.id IS 'Unique identifier (UUID)';
COMMENT ON COLUMN users.email IS 'User email (unique, used for login)';
COMMENT ON COLUMN users.password_hash IS 'Bcrypt hashed password';
COMMENT ON COLUMN users.user_type IS 'Discriminator for inheritance: CLIENT, CONSULTANT, or ADMIN';
COMMENT ON COLUMN users.name IS 'Full name of the user';
COMMENT ON COLUMN users.phone IS 'Contact phone number (optional)';
COMMENT ON COLUMN users.status IS 'User account status';
COMMENT ON COLUMN users.created_at IS 'Timestamp of account creation';
COMMENT ON COLUMN users.updated_at IS 'Timestamp of last update (auto-updated)';
