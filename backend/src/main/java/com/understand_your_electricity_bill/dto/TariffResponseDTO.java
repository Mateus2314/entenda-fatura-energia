package com.understand_your_electricity_bill.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for returning complete Tariff information.
 * Used for detailed views and API responses.
 * Includes calculated fields and formatting methods.
 */
public record TariffResponseDTO(
        UUID id,
        LocalDate generationDate,
        String descriptionReh,
        String distributor,
        String cnpjDistributor,
        LocalDate validFrom,
        LocalDate validUntil,
        String tariffBaseDesc,
        String subgroup,
        String tariffModality,
        String consumerClass,
        String consumerSubclass,
        String detail,
        String tariffPostName,
        String tertiaryUnit,
        String accessingAgent,
        BigDecimal tusdValue,
        BigDecimal teValue,
        LocalDate flagGenerationDate,
        LocalDate competenceDate,
        String activatedFlagName,
        BigDecimal flagAdditionalValue,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    /**
     * Builder pattern for easier construction from Entity
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private LocalDate generationDate;
        private String descriptionReh;
        private String distributor;
        private String cnpjDistributor;
        private LocalDate validFrom;
        private LocalDate validUntil;
        private String tariffBaseDesc;
        private String subgroup;
        private String tariffModality;
        private String consumerClass;
        private String consumerSubclass;
        private String detail;
        private String tariffPostName;
        private String tertiaryUnit;
        private String accessingAgent;
        private BigDecimal tusdValue;
        private BigDecimal teValue;
        private LocalDate flagGenerationDate;
        private LocalDate competenceDate;
        private String activatedFlagName;
        private BigDecimal flagAdditionalValue;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder generationDate(LocalDate generationDate) {
            this.generationDate = generationDate;
            return this;
        }

        public Builder descriptionReh(String descriptionReh) {
            this.descriptionReh = descriptionReh;
            return this;
        }

        public Builder distributor(String distributor) {
            this.distributor = distributor;
            return this;
        }

        public Builder cnpjDistributor(String cnpjDistributor) {
            this.cnpjDistributor = cnpjDistributor;
            return this;
        }

        public Builder validFrom(LocalDate validFrom) {
            this.validFrom = validFrom;
            return this;
        }

        public Builder validUntil(LocalDate validUntil) {
            this.validUntil = validUntil;
            return this;
        }

        public Builder tariffBaseDesc(String tariffBaseDesc) {
            this.tariffBaseDesc = tariffBaseDesc;
            return this;
        }

        public Builder subgroup(String subgroup) {
            this.subgroup = subgroup;
            return this;
        }

        public Builder tariffModality(String tariffModality) {
            this.tariffModality = tariffModality;
            return this;
        }

        public Builder consumerClass(String consumerClass) {
            this.consumerClass = consumerClass;
            return this;
        }

        public Builder consumerSubclass(String consumerSubclass) {
            this.consumerSubclass = consumerSubclass;
            return this;
        }

        public Builder detail(String detail) {
            this.detail = detail;
            return this;
        }

        public Builder tariffPostName(String tariffPostName) {
            this.tariffPostName = tariffPostName;
            return this;
        }

        public Builder tertiaryUnit(String tertiaryUnit) {
            this.tertiaryUnit = tertiaryUnit;
            return this;
        }

        public Builder accessingAgent(String accessingAgent) {
            this.accessingAgent = accessingAgent;
            return this;
        }

        public Builder tusdValue(BigDecimal tusdValue) {
            this.tusdValue = tusdValue;
            return this;
        }

        public Builder teValue(BigDecimal teValue) {
            this.teValue = teValue;
            return this;
        }

        public Builder flagGenerationDate(LocalDate flagGenerationDate) {
            this.flagGenerationDate = flagGenerationDate;
            return this;
        }

        public Builder competenceDate(LocalDate competenceDate) {
            this.competenceDate = competenceDate;
            return this;
        }

        public Builder activatedFlagName(String activatedFlagName) {
            this.activatedFlagName = activatedFlagName;
            return this;
        }

        public Builder flagAdditionalValue(BigDecimal flagAdditionalValue) {
            this.flagAdditionalValue = flagAdditionalValue;
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

        public TariffResponseDTO build() {
            return new TariffResponseDTO(
                    id, generationDate, descriptionReh, distributor, cnpjDistributor,
                    validFrom, validUntil, tariffBaseDesc, subgroup, tariffModality,
                    consumerClass, consumerSubclass, detail, tariffPostName, tertiaryUnit,
                    accessingAgent, tusdValue, teValue, flagGenerationDate, competenceDate,
                    activatedFlagName, flagAdditionalValue, createdAt, updatedAt
            );
        }
    }

    /**
     * Get masked CNPJ for display (XX.XXX.XXX/XXXX-XX)
     */
    public String getMaskedCnpj() {
        if (cnpjDistributor == null || cnpjDistributor.length() != 14) {
            return cnpjDistributor;
        }
        return String.format("%s.%s.%s/%s-%s",
                cnpjDistributor.substring(0, 2),
                cnpjDistributor.substring(2, 5),
                cnpjDistributor.substring(5, 8),
                cnpjDistributor.substring(8, 12),
                cnpjDistributor.substring(12, 14)
        );
    }

    /**
     * Check if tariff is currently valid
     */
    public boolean isCurrentlyValid() {
        LocalDate today = LocalDate.now();
        boolean afterStart = validFrom == null || !today.isBefore(validFrom);
        boolean beforeEnd = validUntil == null || !today.isAfter(validUntil);
        return afterStart && beforeEnd;
    }

    /**
     * Check if tariff has flag (bandeira tarifária)
     */
    public boolean hasFlag() {
        return activatedFlagName != null && !activatedFlagName.isEmpty();
    }

    /**
     * Calculate total tariff value (TUSD + TE) per kWh
     */
    public BigDecimal getTotalTariffPerKwh() {
        if (tusdValue == null || teValue == null) {
            return BigDecimal.ZERO;
        }
        return tusdValue.add(teValue);
    }

    /**
     * Calculate total cost for given consumption
     * @param consumptionKwh Consumption in kWh
     * @return Total cost including flag
     */
    public BigDecimal calculateTotalCost(BigDecimal consumptionKwh) {
        if (consumptionKwh == null || consumptionKwh.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // Base cost: (TUSD + TE) * consumption
        BigDecimal baseCost = getTotalTariffPerKwh().multiply(consumptionKwh);

        // Flag cost: (flag_value / 100) * consumption
        BigDecimal flagCost = BigDecimal.ZERO;
        if (flagAdditionalValue != null && flagAdditionalValue.compareTo(BigDecimal.ZERO) > 0) {
            flagCost = flagAdditionalValue
                    .divide(new BigDecimal("100"), 4, java.math.RoundingMode.HALF_UP)
                    .multiply(consumptionKwh);
        }

        return baseCost.add(flagCost);
    }

    /**
     * Get validity period description
     */
    public String getValidityPeriod() {
        if (validFrom == null) {
            return "N/A";
        }
        if (validUntil == null) {
            return "From " + validFrom + " onwards";
        }
        return validFrom + " to " + validUntil;
    }
}

