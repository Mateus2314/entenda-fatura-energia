-- Migration V004: Create admins table
-- Description: Creates the admins table with JOINED inheritance from users
-- Author: System
-- Date: 2024-12-15

-- Create admins table (JOINED inheritance from users)
CREATE TABLE admins (
                        user_id UUID NOT NULL,
                        role VARCHAR(50) NOT NULL,
                        permissions JSONB,
                        created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                        updated_at TIMESTAMP DEFAULT NOW(),

    -- Primary Key (also Foreign Key to users)
                        CONSTRAINT pk_admins PRIMARY KEY (user_id),

    -- Foreign Key to users table
                        CONSTRAINT fk_admins_user FOREIGN KEY (user_id)
                            REFERENCES users(id)
                            ON DELETE CASCADE
);

-- Create indexes for better query performance
CREATE INDEX idx_admins_role ON admins(role);
CREATE INDEX idx_admins_user_id ON admins(user_id);

-- Add comments for documentation
COMMENT ON TABLE admins IS 'Administrators table - JOINED inheritance from users';
COMMENT ON COLUMN admins.user_id IS 'Primary key and foreign key to users table';
COMMENT ON COLUMN admins.role IS 'Admin role type (e.g., SUPER_ADMIN, ADMIN, MODERATOR)';
COMMENT ON COLUMN admins.permissions IS 'JSON object with specific permissions and access controls';
COMMENT ON COLUMN admins.created_at IS 'Record creation timestamp';
COMMENT ON COLUMN admins.updated_at IS 'Record last update timestamp';
