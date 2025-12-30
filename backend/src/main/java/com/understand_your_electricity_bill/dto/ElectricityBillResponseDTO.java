package com.understand_your_electricity_bill.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * DTO for ElectricityBill response.
 * Contains all bill information including related entity IDs.
 */
public record ElectricityBillResponseDTO(
        UUID id,
        UUID clientId,
        UUID consultantId, // Nullable
        UUID tariffId,
        LocalDate referenceMonth,
        LocalDate dueDate,
        BigDecimal totalAmount,
        BigDecimal consumptionKwh,
        String pdfUrl,
        String installationNumber,
        String invoiceNumber,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    /**
     * Builder pattern for flexible object construction
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Formats reference month as "MMM/yyyy" (e.g., "Jan/2024")
     */
    public String getFormattedReferenceMonth() {
        if (referenceMonth == null) {
            return "";
        }
        return referenceMonth.format(DateTimeFormatter.ofPattern("MMM/yyyy"));
    }

    /**
     * Formats due date as "dd/MM/yyyy" (e.g., "15/01/2024")
     */
    public String getFormattedDueDate() {
        if (dueDate == null) {
            return "";
        }
        return dueDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /**
     * Calculates cost per kWh
     */
    public BigDecimal getCostPerKwh() {
        if (consumptionKwh == null || consumptionKwh.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return totalAmount.divide(consumptionKwh, 4, RoundingMode.HALF_UP);
    }

    /**
     * Formats total amount as currency (e.g., "R$ 385,50")
     */
    public String getFormattedTotalAmount() {
        if (totalAmount == null) {
            return "R$ 0,00";
        }
        return String.format("R$ %,.2f", totalAmount);
    }

    /**
     * Checks if bill has a consultant assigned
     */
    public boolean hasConsultant() {
        return consultantId != null;
    }

    /**
     * Checks if bill has PDF uploaded
     */
    public boolean hasPdf() {
        return pdfUrl != null && !pdfUrl.trim().isEmpty();
    }

    /**
     * Checks if bill is overdue
     */
    public boolean isOverdue() {
        if (dueDate == null) {
            return false;
        }
        return LocalDate.now().isAfter(dueDate);
    }

    /**
     * Gets number of days until due date (negative if overdue)
     */
    public long getDaysUntilDue() {
        if (dueDate == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
    }

    public static class Builder {
        private UUID id;
        private UUID clientId;
        private UUID consultantId;
        private UUID tariffId;
        private LocalDate referenceMonth;
        private LocalDate dueDate;
        private BigDecimal totalAmount;
        private BigDecimal consumptionKwh;
        private String pdfUrl;
        private String installationNumber;
        private String invoiceNumber;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder clientId(UUID clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder consultantId(UUID consultantId) {
            this.consultantId = consultantId;
            return this;
        }

        public Builder tariffId(UUID tariffId) {
            this.tariffId = tariffId;
            return this;
        }

        public Builder referenceMonth(LocalDate referenceMonth) {
            this.referenceMonth = referenceMonth;
            return this;
        }

        public Builder dueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public Builder totalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public Builder consumptionKwh(BigDecimal consumptionKwh) {
            this.consumptionKwh = consumptionKwh;
            return this;
        }

        public Builder pdfUrl(String pdfUrl) {
            this.pdfUrl = pdfUrl;
            return this;
        }

        public Builder installationNumber(String installationNumber) {
            this.installationNumber = installationNumber;
            return this;
        }

        public Builder invoiceNumber(String invoiceNumber) {
            this.invoiceNumber = invoiceNumber;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public ElectricityBillResponseDTO build() {
            return new ElectricityBillResponseDTO(
                    id, clientId, consultantId, tariffId,
                    referenceMonth, dueDate, totalAmount, consumptionKwh,
                    pdfUrl, installationNumber, invoiceNumber,
                    createdAt, updatedAt
            );
        }
    }
}

