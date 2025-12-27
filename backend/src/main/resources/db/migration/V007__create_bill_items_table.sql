-- ============================================================================
-- Migration: V007 - Create Bill Items Table
-- Description: Creates the bill_items table for storing electricity bill line items
--              Many-to-One relationship with electricity_bills
-- Author: Backend Team
-- Date: 2025-12-27
-- Dependencies: V006 (electricity_bills)
-- ============================================================================

-- ============================================================================
-- Table: bill_items
-- ============================================================================
CREATE TABLE bill_items (
    -- Primary Key
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    -- Foreign Key (Many-to-One with electricity_bills)
    bill_id UUID NOT NULL,

    -- Item Data
    item_type VARCHAR(50) NOT NULL,
    description VARCHAR(500) NOT NULL,
    quantity DECIMAL(10,2),
    unit_price DECIMAL(10,4),
    amount DECIMAL(10,2) NOT NULL,

    -- Audit Fields
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

    -- Foreign Key Constraint
    CONSTRAINT fk_bill_items_bill
        FOREIGN KEY (bill_id)
        REFERENCES electricity_bills(id)
        ON DELETE CASCADE,

    -- Check Constraints
    CONSTRAINT chk_quantity_non_negative
        CHECK (quantity IS NULL OR quantity >= 0),
    CONSTRAINT chk_unit_price_non_negative
        CHECK (unit_price IS NULL OR unit_price >= 0),
    CONSTRAINT chk_amount_non_negative
        CHECK (amount >= 0),
    CONSTRAINT chk_item_type_valid
        CHECK (item_type IN (
            'CONSUMPTION_PEAK',
            'CONSUMPTION_OFF_PEAK',
            'CONSUMPTION_STANDARD',
            'DEMAND',
            'TARIFF_FLAG',
            'PUBLIC_LIGHTING',
            'TAXES',
            'OTHER'
        ))
);

-- ============================================================================
-- Indexes for Performance
-- ============================================================================
-- Foreign key index
CREATE INDEX idx_bill_items_bill_id ON bill_items(bill_id);

-- Business logic indexes
CREATE INDEX idx_bill_items_item_type ON bill_items(item_type);
CREATE INDEX idx_bill_items_created_at ON bill_items(created_at);

-- Composite index for common queries
CREATE INDEX idx_bill_items_bill_type ON bill_items(bill_id, item_type);

-- ============================================================================
-- Trigger for automatic updated_at
-- ============================================================================
CREATE TRIGGER update_bill_items_updated_at
    BEFORE UPDATE ON bill_items
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- ============================================================================
-- Comments for Documentation
-- ============================================================================
COMMENT ON TABLE bill_items IS 'Electricity bill line items - Many-to-One with electricity_bills';
COMMENT ON COLUMN bill_items.id IS 'Primary key (UUID)';
COMMENT ON COLUMN bill_items.bill_id IS 'Foreign key to electricity_bills';
COMMENT ON COLUMN bill_items.item_type IS 'Type of charge: CONSUMPTION_PEAK, CONSUMPTION_OFF_PEAK, CONSUMPTION_STANDARD, DEMAND, TARIFF_FLAG, PUBLIC_LIGHTING, TAXES, OTHER';
COMMENT ON COLUMN bill_items.description IS 'Item description (max 500 chars)';
COMMENT ON COLUMN bill_items.quantity IS 'Quantity (e.g., kWh consumed, kW demanded)';
COMMENT ON COLUMN bill_items.unit_price IS 'Price per unit (e.g., R$/kWh)';
COMMENT ON COLUMN bill_items.amount IS 'Total amount for this item';
COMMENT ON COLUMN bill_items.created_at IS 'Timestamp of record creation';
COMMENT ON COLUMN bill_items.updated_at IS 'Timestamp of last update (auto-updated)';

