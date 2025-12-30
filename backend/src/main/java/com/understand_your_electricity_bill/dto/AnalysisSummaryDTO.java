package com.understand_your_electricity_bill.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO with essential Analysis information only.
 * Used for lists within ElectricityBill details.
 * Lightweight for performance.
 */
public record AnalysisSummaryDTO(
        UUID id,
        BigDecimal averageConsumption,
        BigDecimal costPerKwh,
        BigDecimal comparisonPrevMonth,
        String savingsTips
) {
    /**
     * Get formatted average consumption
     */
    public String getFormattedAverageConsumption() {
        if (averageConsumption == null) {
            return "0,00 kWh";
        }
        return String.format("%,.2f kWh", averageConsumption);
    }

    /**
     * Get formatted cost per kWh
     */
    public String getFormattedCostPerKwh() {
        if (costPerKwh == null) {
            return "R$ 0,0000";
        }
        return String.format("R$ %.4f", costPerKwh);
    }

    /**
     * Get formatted comparison
     */
    public String getFormattedComparison() {
        if (comparisonPrevMonth == null) {
            return "0%";
        }
        String sign = comparisonPrevMonth.compareTo(BigDecimal.ZERO) > 0 ? "+" : "";
        return String.format("%s%.2f%%", sign, comparisonPrevMonth);
    }

    /**
     * Get comparison status
     */
    public ComparisonStatus getComparisonStatus() {
        if (comparisonPrevMonth == null) {
            return ComparisonStatus.STABLE;
        }
        if (comparisonPrevMonth.compareTo(BigDecimal.ZERO) > 0) {
            return ComparisonStatus.INCREASED;
        } else if (comparisonPrevMonth.compareTo(BigDecimal.ZERO) < 0) {
            return ComparisonStatus.DECREASED;
        }
        return ComparisonStatus.STABLE;
    }

    /**
     * Check if has savings tips
     */
    public boolean hasSavingsTips() {
        return savingsTips != null && !savingsTips.trim().isEmpty();
    }

    /**
     * Get short savings tips (first 100 chars)
     */
    public String getShortSavingsTips() {
        if (!hasSavingsTips()) {
            return "";
        }
        if (savingsTips.length() <= 100) {
            return savingsTips;
        }
        return savingsTips.substring(0, 97) + "...";
    }

    /**
     * Check if consumption changed significantly (>10% or <-10%)
     */
    public boolean isSignificantChange() {
        if (comparisonPrevMonth == null) {
            return false;
        }
        return comparisonPrevMonth.abs().compareTo(new BigDecimal("10.0")) > 0;
    }

    public enum ComparisonStatus {
        INCREASED,
        DECREASED,
        STABLE
    }
}

