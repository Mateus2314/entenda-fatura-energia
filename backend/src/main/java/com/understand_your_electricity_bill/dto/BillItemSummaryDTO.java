package com.understand_your_electricity_bill.dto;

import com.understand_your_electricity_bill.model.enums.BillItemType;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO with essential BillItem information only.
 * Used for lists within ElectricityBill details.
 * Lightweight for performance.
 */
public record BillItemSummaryDTO(
        UUID id,
        BillItemType itemType,
        String description,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal amount
) {
    /**
     * Get formatted amount as currency
     */
    public String getFormattedAmount() {
        if (amount == null) {
            return "R$ 0,00";
        }
        return String.format("R$ %,.2f", amount);
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
     * Check if item has unit pricing
     */
    public boolean hasUnitPricing() {
        return quantity != null && unitPrice != null;
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

    /**
     * Get short description (first 50 chars)
     */
    public String getShortDescription() {
        if (description == null || description.length() <= 50) {
            return description;
        }
        return description.substring(0, 47) + "...";
    }
}

