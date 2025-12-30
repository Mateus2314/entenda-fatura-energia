package com.understand_your_electricity_bill.dto;

import com.understand_your_electricity_bill.model.enums.BillItemType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for BillItem response.
 * Contains all item information including bill ID.
 */
public record BillItemResponseDTO(
        UUID id,
        UUID billId,
        BillItemType itemType,
        String description,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal amount,
        LocalDateTime createdAt
) {
    /**
     * Builder pattern for flexible object construction
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Get formatted amount as currency (e.g., "R$ 269,50")
     */
    public String getFormattedAmount() {
        if (amount == null) {
            return "R$ 0,00";
        }
        return String.format("R$ %,.2f", amount);
    }

    /**
     * Get formatted unit price (4 decimal places)
     */
    public String getFormattedUnitPrice() {
        if (unitPrice == null) {
            return "R$ 0,0000";
        }
        return String.format("R$ %.4f", unitPrice);
    }

    /**
     * Get item type display name
     */
    public String getItemTypeDisplay() {
        if (itemType == null) {
            return "N/A";
        }
        return switch (itemType) {
            case OFF_PEAK_CONSUMPTION -> "Consumo Fora Ponta";
            case PEAK_CONSUMPTION -> "Consumo Ponta";
            case INTERMEDIATE_CONSUMPTION -> "Consumo Intermediário";
            case TUSD_CHARGE -> "Tarifa TUSD";
            case TE_CHARGE -> "Tarifa TE";
            case FLAG_CHARGE -> "Bandeira Tarifária";
            case ICMS_TAX -> "ICMS";
            case PIS_TAX -> "PIS";
            case COFINS_TAX -> "COFINS";
            case PUBLIC_LIGHTING -> "Iluminação Pública";
            case DISCOUNT -> "Desconto";
            case CREDIT -> "Crédito";
            case OTHER -> "Outros";
        };
    }

    /**
     * Check if item has quantity and unit price
     */
    public boolean hasUnitPricing() {
        return quantity != null && unitPrice != null;
    }

    /**
     * Calculate percentage of total (requires total amount)
     */
    public BigDecimal calculatePercentage(BigDecimal totalAmount) {
        if (amount == null || totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return amount.divide(totalAmount, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    /**
     * Check if item is a tax
     */
    public boolean isTax() {
        return itemType == BillItemType.ICMS_TAX ||
               itemType == BillItemType.PIS_TAX ||
               itemType == BillItemType.COFINS_TAX;
    }

    /**
     * Check if item is consumption-related
     */
    public boolean isConsumption() {
        return itemType == BillItemType.OFF_PEAK_CONSUMPTION ||
               itemType == BillItemType.PEAK_CONSUMPTION ||
               itemType == BillItemType.INTERMEDIATE_CONSUMPTION;
    }

    public static class Builder {
        private UUID id;
        private UUID billId;
        private BillItemType itemType;
        private String description;
        private BigDecimal quantity;
        private BigDecimal unitPrice;
        private BigDecimal amount;
        private LocalDateTime createdAt;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder billId(UUID billId) {
            this.billId = billId;
            return this;
        }

        public Builder itemType(BillItemType itemType) {
            this.itemType = itemType;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder quantity(BigDecimal quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder unitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public BillItemResponseDTO build() {
            return new BillItemResponseDTO(
                    id, billId, itemType, description,
                    quantity, unitPrice, amount, createdAt
            );
        }
    }
}

