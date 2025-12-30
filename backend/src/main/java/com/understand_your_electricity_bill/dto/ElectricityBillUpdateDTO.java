package com.understand_your_electricity_bill.dto;

import com.understand_your_electricity_bill.dto.validation.DtoValidationUtils;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for updating an existing ElectricityBill.
 * All fields are optional to allow partial updates.
 * Client and Tariff IDs are immutable after creation.
 */
public record ElectricityBillUpdateDTO(
        @NotNull(message = "Bill ID is required for update")
        UUID id,

        UUID consultantId, // Can be assigned/unassigned

        LocalDate referenceMonth,

        LocalDate dueDate,

        @DecimalMin(value = "0.0", message = "Total amount must be greater than or equal to 0")
        BigDecimal totalAmount,

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
    public ElectricityBillUpdateDTO {
        // Trim and normalize strings
        pdfUrl = DtoValidationUtils.trimIfNotNull(pdfUrl);
        installationNumber = DtoValidationUtils.trimIfNotNull(installationNumber);
        invoiceNumber = DtoValidationUtils.trimIfNotNull(invoiceNumber);

        // Validate business rules
        if (dueDate != null && referenceMonth != null && !dueDate.isAfter(referenceMonth)) {
            throw new IllegalArgumentException("Due date must be after reference month");
        }
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

