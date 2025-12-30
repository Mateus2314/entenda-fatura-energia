package com.understand_your_electricity_bill.dto;

import com.understand_your_electricity_bill.dto.validation.DtoValidationUtils;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for creating a new Analysis.
 * Contains all required fields for analysis creation.
 */
public record AnalysisCreateDTO(
        @NotNull(message = "Bill ID is required")
        UUID billId,

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
    public AnalysisCreateDTO {
        // Trim and normalize strings
        savingsTips = DtoValidationUtils.trimIfNotNull(savingsTips);
        reportPdfUrl = DtoValidationUtils.trimIfNotNull(reportPdfUrl);
    }
}

