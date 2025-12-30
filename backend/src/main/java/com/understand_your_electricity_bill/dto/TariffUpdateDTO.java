package com.understand_your_electricity_bill.dto;

import com.understand_your_electricity_bill.dto.validation.DtoValidationUtils;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for updating an existing Tariff.
 * Includes ID for identification and specific validations.
 * Fields are optional (nullable) to allow partial updates.
 * CNPJ and distributor cannot be updated (immutable business rule).
 */
public record TariffUpdateDTO(
        @NotNull(message = "Tariff ID is required for update")
        UUID id,

        LocalDate generationDate,

        @Size(max = 500, message = "Description REH must not exceed 500 characters")
        String descriptionReh,

        LocalDate validFrom,

        LocalDate validUntil,

        @Size(max = 100, message = "Tariff base description must not exceed 100 characters")
        String tariffBaseDesc,

        @Size(max = 10, message = "Subgroup must not exceed 10 characters")
        String subgroup,

        @Size(max = 50, message = "Tariff modality must not exceed 50 characters")
        String tariffModality,

        @Size(max = 100, message = "Consumer class must not exceed 100 characters")
        String consumerClass,

        @Size(max = 100, message = "Consumer subclass must not exceed 100 characters")
        String consumerSubclass,

        @Size(max = 100, message = "Detail must not exceed 100 characters")
        String detail,

        @Size(max = 50, message = "Tariff post name must not exceed 50 characters")
        String tariffPostName,

        @Size(max = 10, message = "Tertiary unit must not exceed 10 characters")
        String tertiaryUnit,

        @Size(max = 100, message = "Accessing agent must not exceed 100 characters")
        String accessingAgent,

        @DecimalMin(value = "0.00001", message = "TUSD value must be positive")
        BigDecimal tusdValue,

        @DecimalMin(value = "0.00001", message = "TE value must be positive")
        BigDecimal teValue,

        LocalDate flagGenerationDate,

        LocalDate competenceDate,

        @Size(max = 50, message = "Activated flag name must not exceed 50 characters")
        String activatedFlagName,

        @DecimalMin(value = "0.0", inclusive = true, message = "Flag additional value must be non-negative")
        BigDecimal flagAdditionalValue
) {
    /**
     * Compact constructor with validation and normalization
     */
    public TariffUpdateDTO {
        // Trim and normalize strings
        descriptionReh = DtoValidationUtils.trimIfNotNull(descriptionReh);
        tariffBaseDesc = DtoValidationUtils.trimIfNotNull(tariffBaseDesc);
        subgroup = DtoValidationUtils.trimIfNotNull(subgroup);
        tariffModality = DtoValidationUtils.trimIfNotNull(tariffModality);
        consumerClass = DtoValidationUtils.trimIfNotNull(consumerClass);
        consumerSubclass = DtoValidationUtils.trimIfNotNull(consumerSubclass);
        detail = DtoValidationUtils.trimIfNotNull(detail);
        tariffPostName = DtoValidationUtils.trimIfNotNull(tariffPostName);
        tertiaryUnit = DtoValidationUtils.trimIfNotNull(tertiaryUnit);
        accessingAgent = DtoValidationUtils.trimIfNotNull(accessingAgent);
        activatedFlagName = DtoValidationUtils.trimIfNotNull(activatedFlagName);

        // Validate date logic
        DtoValidationUtils.validateDateRange(validFrom, validUntil);
    }

    /**
     * Check if at least one field is being updated.
     * Uses reflection to check all fields except ID.
     *
     * @return true if at least one field (excluding ID) is not null
     */
    public boolean hasUpdates() {
        return DtoValidationUtils.hasUpdates(this);
    }
}

