-- ============================================================================
-- Migration: V005 - Create Tariffs Table
-- Description: Creates the tariffs table for ANEEL API integration
--              Stores energy tariffs and tariff flags (bandeiras tarifárias)
-- Author: Backend Team
-- Date: 2025-12-27
-- ============================================================================

-- ============================================================================
-- Table: tariffs (ANEEL Open Data API Integration)
-- ============================================================================
CREATE TABLE tariffs (
    -- Primary Key
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    -- API Fields - Energy Tariffs (Resource: fcf2906c-7c32-4b9b-a637-054e7a5234f4)
    generation_date DATE NOT NULL,
    description_reh VARCHAR(500),
    distributor VARCHAR(100) NOT NULL,
    cnpj_distributor VARCHAR(14) NOT NULL,
    valid_from DATE NOT NULL,
    valid_until DATE,
    tariff_base_desc VARCHAR(100),
    subgroup VARCHAR(10),
    tariff_modality VARCHAR(50),
    consumer_class VARCHAR(100),
    consumer_subclass VARCHAR(100),
    detail VARCHAR(100),
    tariff_post_name VARCHAR(50),
    tertiary_unit VARCHAR(10),
    accessing_agent VARCHAR(100),
    tusd_value DECIMAL(10,4) NOT NULL CHECK (tusd_value >= 0),
    te_value DECIMAL(10,4) NOT NULL CHECK (te_value >= 0),

    -- API Fields - Tariff Flags (Resource: 0591b8f6-fe54-437b-b72b-1aa2efd46e42)
    flag_generation_date DATE,
    competence_date DATE,
    activated_flag_name VARCHAR(50),
    flag_additional_value DECIMAL(10,4) CHECK (flag_additional_value >= 0),

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

    -- Constraints
    CONSTRAINT chk_valid_dates CHECK (valid_until IS NULL OR valid_until >= valid_from),
    CONSTRAINT chk_cnpj_distributor_format CHECK (cnpj_distributor ~ '^\d{14}$')
);

-- ============================================================================
-- Indexes for performance
-- ============================================================================
-- Search for active tariff by distributor and characteristics
CREATE INDEX idx_tariff_search ON tariffs (
    distributor,
    subgroup,
    tariff_modality,
    valid_from,
    valid_until
);

-- Search by CNPJ
CREATE INDEX idx_tariff_cnpj ON tariffs (cnpj_distributor);

-- Search by validity period
CREATE INDEX idx_tariff_validity ON tariffs (valid_from, valid_until);

-- Search by distributor
CREATE INDEX idx_tariff_distributor ON tariffs (distributor);

-- Search by competence date (for flag matching)
CREATE INDEX idx_tariff_competence_date ON tariffs (competence_date);

-- ============================================================================
-- Trigger for automatic updated_at
-- ============================================================================
CREATE TRIGGER update_tariffs_updated_at
    BEFORE UPDATE ON tariffs
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- ============================================================================
-- Comments for documentation
-- ============================================================================
COMMENT ON TABLE tariffs IS 'Energy tariffs and flags from ANEEL Open Data API (snapshot pattern)';
COMMENT ON COLUMN tariffs.id IS 'Unique identifier (UUID)';

-- Energy Tariffs API Fields
COMMENT ON COLUMN tariffs.generation_date IS 'ANEEL API: DatGeracaoConjuntoDados - Dataset generation date';
COMMENT ON COLUMN tariffs.description_reh IS 'ANEEL API: DscREH - REH description';
COMMENT ON COLUMN tariffs.distributor IS 'ANEEL API: SigAgente - Distributor name (e.g., "CPFL JAGUARI")';
COMMENT ON COLUMN tariffs.cnpj_distributor IS 'ANEEL API: NumCNPJDistribuidora - Distributor CNPJ (14 digits)';
COMMENT ON COLUMN tariffs.valid_from IS 'ANEEL API: DatInicioVigencia - Tariff validity start date';
COMMENT ON COLUMN tariffs.valid_until IS 'ANEEL API: DatFimVigencia - Tariff validity end date (NULL for current tariffs)';
COMMENT ON COLUMN tariffs.tariff_base_desc IS 'ANEEL API: DscBaseTarifaria - Tariff base description';
COMMENT ON COLUMN tariffs.subgroup IS 'ANEEL API: DscSubGrupo - Subgroup (A2, B1, etc.)';
COMMENT ON COLUMN tariffs.tariff_modality IS 'ANEEL API: DscModalidadeTarifaria - Modality (Azul, Verde, Convencional)';
COMMENT ON COLUMN tariffs.consumer_class IS 'ANEEL API: DscClasse - Consumer class (Residencial, Industrial, Comercial)';
COMMENT ON COLUMN tariffs.consumer_subclass IS 'ANEEL API: DscSubClasse - Consumer subclass';
COMMENT ON COLUMN tariffs.detail IS 'ANEEL API: DscDetalhe - Additional details (APE, etc.)';
COMMENT ON COLUMN tariffs.tariff_post_name IS 'ANEEL API: NomPostoTarifario - Tariff post (Ponta, Fora ponta)';
COMMENT ON COLUMN tariffs.tertiary_unit IS 'ANEEL API: DscUnidadeTerciaria - Unit (kW, kWh)';
COMMENT ON COLUMN tariffs.accessing_agent IS 'ANEEL API: SigAgenteAcessante - Accessing agent';
COMMENT ON COLUMN tariffs.tusd_value IS 'ANEEL API: VlrTUSD - TUSD Value (Distribution System Usage Tariff)';
COMMENT ON COLUMN tariffs.te_value IS 'ANEEL API: VlrTE - TE Value (Energy Tariff)';

-- Tariff Flags API Fields
COMMENT ON COLUMN tariffs.flag_generation_date IS 'ANEEL API: DatGeracaoConjuntoDados - Flag dataset generation date';
COMMENT ON COLUMN tariffs.competence_date IS 'ANEEL API: DatCompetencia - Competence/validity month of flag';
COMMENT ON COLUMN tariffs.activated_flag_name IS 'ANEEL API: NomBandeiraAcionada - Flag name (Verde, Amarela, Vermelha P1, Vermelha P2)';
COMMENT ON COLUMN tariffs.flag_additional_value IS 'ANEEL API: VlrAdicionalBandeira - Additional value per 100 kWh';

-- Audit Fields
COMMENT ON COLUMN tariffs.created_at IS 'Timestamp of record creation';
COMMENT ON COLUMN tariffs.updated_at IS 'Timestamp of last update (auto-updated)';

