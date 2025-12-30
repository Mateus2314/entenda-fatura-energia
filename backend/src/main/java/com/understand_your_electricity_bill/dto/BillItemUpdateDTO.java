package com.understand_your_electricity_bill.dto;

import com.understand_your_electricity_bill.dto.validation.DtoValidationUtils;
import com.understand_your_electricity_bill.model.enums.BillItemType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for updating an existing BillItem.
 * All fields are optional to allow partial updates.
 * Bill ID is immutable after creation.
 */
public record BillItemUpdateDTO(
        @NotNull(message = "Item ID is required for update")
        UUID id,

        BillItemType itemType,

        @Size(max = 255, message = "Description must not exceed 255 characters")
        String description,

        @DecimalMin(value = "0.0", inclusive = true, message = "Quantity must be non-negative")
        BigDecimal quantity,

        @DecimalMin(value = "0.0", inclusive = true, message = "Unit price must be non-negative")
        BigDecimal unitPrice,

        @DecimalMin(value = "0.0", message = "Amount must be greater than or equal to 0")
        BigDecimal amount
) {
    /**
     * Compact constructor with validation and normalization
     */
    public BillItemUpdateDTO {
        // Trim and normalize strings
        description = DtoValidationUtils.trimIfNotNull(description);

        // Validate business rules (only if all three values are being updated)
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

