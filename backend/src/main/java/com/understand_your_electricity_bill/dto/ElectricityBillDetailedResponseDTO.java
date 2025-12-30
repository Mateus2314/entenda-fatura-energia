package com.understand_your_electricity_bill.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DTO for detailed ElectricityBill response.
 * Includes full information about related entities (Client, Consultant, Tariff).
 * Used when complete bill details are needed (e.g., bill detail page).
 */
public record ElectricityBillDetailedResponseDTO(
        UUID id,
        ClientSummaryDTO client,
        ConsultantSummaryDTO consultant, // Nullable
        TariffResponseDTO tariff,
        LocalDate referenceMonth,
        LocalDate dueDate,
        BigDecimal totalAmount,
        BigDecimal consumptionKwh,
        String pdfUrl,
        String installationNumber,
        String invoiceNumber,
        List<BillItemSummaryDTO> items, // Bill items
        AnalysisSummaryDTO analysis, // Bill analysis, if exists
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    /**
     * Compact constructor with defensive copies
     */
    public ElectricityBillDetailedResponseDTO {
        items = items != null ? List.copyOf(items) : List.of();
    }

    /**
     * Builder pattern for flexible object construction
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Gets formatted reference month (MMM/yyyy)
     */
    public String getFormattedReferenceMonth() {
        if (referenceMonth == null) {
            return "";
        }
        return referenceMonth.format(java.time.format.DateTimeFormatter.ofPattern("MMM/yyyy"));
    }

    /**
     * Gets formatted due date (dd/MM/yyyy)
     */
    public String getFormattedDueDate() {
        if (dueDate == null) {
            return "";
        }
        return dueDate.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /**
     * Calculates cost per kWh
     */
    public BigDecimal getCostPerKwh() {
        if (consumptionKwh == null || consumptionKwh.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return totalAmount.divide(consumptionKwh, 4, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Checks if bill has consultant assigned
     */
    public boolean hasConsultant() {
        return consultant != null;
    }

    /**
     * Checks if bill has analysis
     */
    public boolean hasAnalysis() {
        return analysis != null;
    }

    /**
     * Checks if bill has items
     */
    public boolean hasItems() {
        return items != null && !items.isEmpty();
    }

    /**
     * Gets total number of items
     */
    public int getItemsCount() {
        return items != null ? items.size() : 0;
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
     * Placeholder for BillItemSummaryDTO (to be created later)
     */
    public record BillItemSummaryDTO(
            UUID id,
            String itemType,
            String description,
            BigDecimal quantity,
            BigDecimal unitPrice,
            BigDecimal amount
    ) {}

    /**
     * Placeholder for AnalysisSummaryDTO (to be created later)
     */
    public record AnalysisSummaryDTO(
            UUID id,
            BigDecimal averageConsumption,
            BigDecimal costPerKwh,
            BigDecimal comparisonPrevMonth,
            String savingsTips
    ) {}

    public static class Builder {
        private UUID id;
        private ClientSummaryDTO client;
        private ConsultantSummaryDTO consultant;
        private TariffResponseDTO tariff;
        private LocalDate referenceMonth;
        private LocalDate dueDate;
        private BigDecimal totalAmount;
        private BigDecimal consumptionKwh;
        private String pdfUrl;
        private String installationNumber;
        private String invoiceNumber;
        private List<BillItemSummaryDTO> items = new ArrayList<>();
        private AnalysisSummaryDTO analysis;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder client(ClientSummaryDTO client) {
            this.client = client;
            return this;
        }

        public Builder consultant(ConsultantSummaryDTO consultant) {
            this.consultant = consultant;
            return this;
        }

        public Builder tariff(TariffResponseDTO tariff) {
            this.tariff = tariff;
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

        public Builder items(List<BillItemSummaryDTO> items) {
            this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
            return this;
        }

        public Builder addItem(BillItemSummaryDTO item) {
            if (this.items == null) {
                this.items = new ArrayList<>();
            }
            this.items.add(item);
            return this;
        }

        public Builder analysis(AnalysisSummaryDTO analysis) {
            this.analysis = analysis;
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

        public ElectricityBillDetailedResponseDTO build() {
            return new ElectricityBillDetailedResponseDTO(
                    id, client, consultant, tariff,
                    referenceMonth, dueDate, totalAmount, consumptionKwh,
                    pdfUrl, installationNumber, invoiceNumber,
                    items, analysis, createdAt, updatedAt
            );
        }
    }
}

