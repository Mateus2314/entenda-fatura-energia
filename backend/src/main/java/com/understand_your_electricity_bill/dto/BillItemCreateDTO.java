package com.understand_your_electricity_bill.dto;

import com.understand_your_electricity_bill.dto.validation.DtoValidationUtils;
import com.understand_your_electricity_bill.model.enums.BillItemType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for creating a new BillItem.
 * Contains all required fields for item creation.
 */
public record BillItemCreateDTO(
        @NotNull(message = "Bill ID is required")
        UUID billId,

        @NotNull(message = "Item type is required")
        BillItemType itemType,

        @Size(max = 255, message = "Description must not exceed 255 characters")
        String description,

        @DecimalMin(value = "0.0", inclusive = true, message = "Quantity must be non-negative")
        BigDecimal quantity,

        @DecimalMin(value = "0.0", inclusive = true, message = "Unit price must be non-negative")
        BigDecimal unitPrice,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.0", message = "Amount must be greater than or equal to 0")
        BigDecimal amount
) {
    /**
     * Compact constructor with validation and normalization
     */
    public BillItemCreateDTO {
        // Trim and normalize strings
        description = DtoValidationUtils.trimIfNotNull(description);

        // Validate business rules
        if (quantity != null && unitPrice != null && amount != null) {
            BigDecimal calculatedAmount = quantity.multiply(unitPrice);
            // Allow small rounding differences (0.01)
            if (calculatedAmount.subtract(amount).abs().compareTo(new BigDecimal("0.01")) > 0) {
                throw new IllegalArgumentException(
                    "Amount must equal quantity × unit price (allowing 0.01 rounding tolerance)"
                );
            }
        }
    }
}

