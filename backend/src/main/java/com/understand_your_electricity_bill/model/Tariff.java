package com.understand_your_electricity_bill.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tariffs", indexes = {
        @Index(name = "idx_tariff_search",
                columnList = "distributor, subgroup, tariff_modality, valid_from, valid_until"),
        @Index(name = "idx_tariff_cnpj", columnList = "cnpj_distributor"),
        @Index(name = "idx_tariff_validity", columnList = "valid_from, valid_until")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tariff {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "Generation date is required")
    @Column(name = "generation_date", nullable = false)
    private LocalDate generationDate;

    @Size(max = 500, message = "Description REH must not exceed 500 characters")
    @Column(name = "description_reh", length = 500)
    private String descriptionReh;

    @NotBlank(message = "Distributor is required")
    @Size(max = 100, message = "Distributor must not exceed 100 characters")
    @Column(name = "distributor", nullable = false, length = 100)
    private String distributor;

    @NotBlank(message = "CNPJ distributor is required")
    @Size(min = 14, max = 14, message = "CNPJ must contain exactly 14 digits")
    @Column(name = "cnpj_distributor", nullable = false, length = 14)
    private String cnpjDistributor;

    @NotNull(message = "Valid from date is required")
    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_until")
    private LocalDate validUntil;

    @Size(max = 100, message = "Tariff base description must not exceed 100 characters")
    @Column(name = "tariff_base_desc", length = 100)
    private String tariffBaseDesc;

    @Size(max = 10, message = "Subgroup must not exceed 10 characters")
    @Column(name = "subgroup", length = 10)
    private String subgroup;

    @Size(max = 50, message = "Tariff modality must not exceed 50 characters")
    @Column(name = "tariff_modality", length = 50)
    private String tariffModality;

    @Size(max = 100, message = "Consumer class must not exceed 100 characters")
    @Column(name = "consumer_class", length = 100)
    private String consumerClass;

    @Size(max = 100, message = "Consumer subclass must not exceed 100 characters")
    @Column(name = "consumer_subclass", length = 100)
    private String consumerSubclass;

    @Size(max = 100, message = "Detail must not exceed 100 characters")
    @Column(name = "detail", length = 100)
    private String detail;

    @Size(max = 50, message = "Tariff post name must not exceed 50 characters")
    @Column(name = "tariff_post_name", length = 50)
    private String tariffPostName;

    @Size(max = 10, message = "Tertiary unit must not exceed 10 characters")
    @Column(name = "tertiary_unit", length = 10)
    private String tertiaryUnit;

    @Size(max = 100, message = "Accessing agent must not exceed 100 characters")
    @Column(name = "accessing_agent", length = 100)
    private String accessingAgent;

    @NotNull(message = "TUSD value is required")
    @DecimalMin(value = "0.00001", message = "TUSD value must be positive")
    @Column(name = "tusd_value", nullable = false, precision = 10, scale = 4)
    private BigDecimal tusdValue;

    @NotNull(message = "TE value is required")
    @Positive(message = "TE value must be positive")
    @Column(name = "te_value", nullable = false, precision = 10, scale = 4)
    private BigDecimal teValue;

    // Tariff Flags (Bandeiras Tarifárias)
    @Column(name = "flag_generation_date")
    private LocalDate flagGenerationDate;

    @Column(name = "competence_date")
    private LocalDate competenceDate;

    @Size(max = 50, message = "Activated flag name must not exceed 50 characters")
    @Column(name = "activated_flag_name", length = 50)
    private String activatedFlagName;

    @DecimalMin(value = "0.0", inclusive = true, message = "Flag additional value must be non-negative")
    @Column(name = "flag_additional_value", precision = 10, scale = 4)
    private BigDecimal flagAdditionalValue;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "tariff", fetch = FetchType.LAZY)
    private List<ElectricityBill> electricityBills = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Calcula o custo total considerando tarifas base e bandeiras
     * @param consumptionKwh Consumo em kWh
     * @return Custo total em Reais
     */
    public BigDecimal calculateTotalCost(BigDecimal consumptionKwh) {
        // Custo base: (TUSD + TE) * consumo
        BigDecimal baseCost = tusdValue.add(teValue).multiply(consumptionKwh);

        // Custo adicional da bandeira: (valor_bandeira / 100) * consumo
        BigDecimal flagCost = BigDecimal.ZERO;
        if (flagAdditionalValue != null && flagAdditionalValue.compareTo(BigDecimal.ZERO) > 0) {
            flagCost = flagAdditionalValue
                    .divide(new BigDecimal("100"), 4, java.math.RoundingMode.HALF_UP)
                    .multiply(consumptionKwh);
        }

        return baseCost.add(flagCost);
    }
}
