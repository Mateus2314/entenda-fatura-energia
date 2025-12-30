package com.understand_your_electricity_bill.dto;

import com.understand_your_electricity_bill.dto.validation.DtoValidationUtils;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for creating a new ElectricityBill.
 * Contains all required fields for bill creation.
 */
public record ElectricityBillCreateDTO(
        @NotNull(message = "Client ID is required")
        UUID clientId,

        UUID consultantId, // Optional

        @NotNull(message = "Tariff ID is required")
        UUID tariffId,

        @NotNull(message = "Reference month is required")
        LocalDate referenceMonth,

        @NotNull(message = "Due date is required")
        LocalDate dueDate,

        @NotNull(message = "Total amount is required")
        @DecimalMin(value = "0.0", message = "Total amount must be greater than or equal to 0")
        BigDecimal totalAmount,

        @NotNull(message = "Consumption is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "Consumption must be greater than or equal to 0")
        BigDecimal consumptionKwh,

        @Size(max = 1000, message = "PDF URL must not exceed 1000 characters")
        String pdfUrl,

        @Size(max = 50, message = "Installation number must not exceed 50 characters")
        String installationNumber,

        @Size(max = 100, message = "Invoice number must not exceed 100 characters")
        String invoiceNumber
) {
    /**
     * Compact constructor with validation and normalization
     */
    public ElectricityBillCreateDTO {
        // Trim and normalize strings
        pdfUrl = DtoValidationUtils.trimIfNotNull(pdfUrl);
        installationNumber = DtoValidationUtils.trimIfNotNull(installationNumber);
        invoiceNumber = DtoValidationUtils.trimIfNotNull(invoiceNumber);

        // Validate business rules
        if (dueDate != null && referenceMonth != null && !dueDate.isAfter(referenceMonth)) {
            throw new IllegalArgumentException("Due date must be after reference month");
        }
    }
}

