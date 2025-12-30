package com.understand_your_electricity_bill.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for Analysis response.
 * Contains all analysis information including bill ID.
 */
public record AnalysisResponseDTO(
        UUID id,
        UUID billId,
        BigDecimal averageConsumption,
        BigDecimal costPerKwh,
        BigDecimal comparisonPrevMonth,
        String savingsTips,
        String reportPdfUrl,
        LocalDateTime createdAt
) {
    /**
     * Builder pattern for flexible object construction
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Get formatted average consumption (e.g., "350,00 kWh")
     */
    public String getFormattedAverageConsumption() {
        if (averageConsumption == null) {
            return "0,00 kWh";
        }
        return String.format("%,.2f kWh", averageConsumption);
    }

    /**
     * Get formatted cost per kWh (e.g., "R$ 1,1014")
     */
    public String getFormattedCostPerKwh() {
        if (costPerKwh == null) {
            return "R$ 0,0000";
        }
        return String.format("R$ %.4f", costPerKwh);
    }

    /**
     * Get formatted comparison (e.g., "+5.5%" or "-3.2%")
     */
    public String getFormattedComparison() {
        if (comparisonPrevMonth == null) {
            return "0%";
        }
        String sign = comparisonPrevMonth.compareTo(BigDecimal.ZERO) > 0 ? "+" : "";
        return String.format("%s%.2f%%", sign, comparisonPrevMonth);
    }

    /**
     * Get comparison status (INCREASED, DECREASED, STABLE)
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
     * Check if has report PDF
     */
    public boolean hasReportPdf() {
        return reportPdfUrl != null && !reportPdfUrl.trim().isEmpty();
    }

    /**
     * Get savings tips preview (first 100 chars)
     */
    public String getSavingsTipsPreview() {
        if (!hasSavingsTips()) {
            return "";
        }
        if (savingsTips.length() <= 100) {
            return savingsTips;
        }
        return savingsTips.substring(0, 97) + "...";
    }

    /**
     * Check if consumption increased significantly (>10%)
     */
    public boolean isSignificantIncrease() {
        return comparisonPrevMonth != null &&
               comparisonPrevMonth.compareTo(new BigDecimal("10.0")) > 0;
    }

    /**
     * Check if consumption decreased significantly (<-10%)
     */
    public boolean isSignificantDecrease() {
        return comparisonPrevMonth != null &&
               comparisonPrevMonth.compareTo(new BigDecimal("-10.0")) < 0;
    }

    public enum ComparisonStatus {
        INCREASED,
        DECREASED,
        STABLE
    }

    public static class Builder {
        private UUID id;
        private UUID billId;
        private BigDecimal averageConsumption;
        private BigDecimal costPerKwh;
        private BigDecimal comparisonPrevMonth;
        private String savingsTips;
        private String reportPdfUrl;
        private LocalDateTime createdAt;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder billId(UUID billId) {
            this.billId = billId;
            return this;
        }

        public Builder averageConsumption(BigDecimal averageConsumption) {
            this.averageConsumption = averageConsumption;
            return this;
        }

        public Builder costPerKwh(BigDecimal costPerKwh) {
            this.costPerKwh = costPerKwh;
            return this;
        }

        public Builder comparisonPrevMonth(BigDecimal comparisonPrevMonth) {
            this.comparisonPrevMonth = comparisonPrevMonth;
            return this;
        }

        public Builder savingsTips(String savingsTips) {
            this.savingsTips = savingsTips;
            return this;
        }

        public Builder reportPdfUrl(String reportPdfUrl) {
            this.reportPdfUrl = reportPdfUrl;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public AnalysisResponseDTO build() {
            return new AnalysisResponseDTO(
                    id, billId, averageConsumption, costPerKwh,
                    comparisonPrevMonth, savingsTips, reportPdfUrl, createdAt
            );
        }
    }
}

