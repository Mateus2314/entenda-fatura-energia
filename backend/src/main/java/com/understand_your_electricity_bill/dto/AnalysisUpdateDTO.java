package com.understand_your_electricity_bill.dto;

import com.understand_your_electricity_bill.dto.validation.DtoValidationUtils;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for updating an existing Analysis.
 * All fields are optional to allow partial updates.
 * Bill ID is immutable after creation.
 */
public record AnalysisUpdateDTO(
        @NotNull(message = "Analysis ID is required for update")
        UUID id,

        @DecimalMin(value = "0.0", message = "Average consumption must be greater than or equal to 0")
        BigDecimal averageConsumption,

        @DecimalMin(value = "0.0", message = "Cost per kWh must be greater than or equal to 0")
        BigDecimal costPerKwh,

        @DecimalMin(value = "-100.0", message = "Comparison must be greater than or equal to -100%")
        @DecimalMax(value = "1000.0", message = "Comparison must be less than or equal to 1000%")
        BigDecimal comparisonPrevMonth,

        @Size(max = 5000, message = "Savings tips must not exceed 5000 characters")
        String savingsTips,

        @Size(max = 1000, message = "Report PDF URL must not exceed 1000 characters")
        String reportPdfUrl
) {
    /**
     * Compact constructor with validation and normalization
     */
    public AnalysisUpdateDTO {
        // Trim and normalize strings
        savingsTips = DtoValidationUtils.trimIfNotNull(savingsTips);
        reportPdfUrl = DtoValidationUtils.trimIfNotNull(reportPdfUrl);
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

