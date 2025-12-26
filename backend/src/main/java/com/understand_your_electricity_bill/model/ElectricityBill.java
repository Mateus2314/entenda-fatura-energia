package com.understand_your_electricity_bill.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Table(name = "electricity_bills", indexes = {
        @Index(name = "idx_bills_client_id", columnList = "client_id"),
        @Index(name = "idx_bills_consultant_id", columnList = "consultant_id"),
        @Index(name = "idx_bills_tariff_id", columnList = "tariff_id"),
        @Index(name = "idx_bills_reference_month", columnList = "reference_month")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ElectricityBill {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Relationships
    @NotNull(message = "Client is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultant_id")
    private Consultant consultant;

    @NotNull(message = "Tariff is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tariff_id", nullable = false)
    private Tariff tariff;

    // Bill Data
    @NotNull(message = "Reference month is required")
    @Column(name = "reference_month", nullable = false)
    private LocalDate referenceMonth;

    @NotNull(message = "Due date is required")
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", message = "Total amount must be greater than or equal to 0")
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @NotNull(message = "Consumption is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Consumption must be greater than or equal to 0")
    @Column(name = "consumption_kwh", nullable = false, precision = 10, scale = 2)
    private BigDecimal consumptionKwh;


    @Size(max = 1000, message = "PDF URL must not exceed 1000 characters")
    @Column(name = "pdf_url", columnDefinition = "TEXT")
    private String pdfUrl;

    @Size(max = 50, message = "Installation number must not exceed 50 characters")
    @Column(name = "installation_number", length = 50)
    private String installationNumber;

    @Size(max = 100, message = "Invoice number must not exceed 100 characters")
    @Column(name = "invoice_number", length = 100)
    private String invoiceNumber;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Child Relationships
    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BillItem> items = new ArrayList<>();

    @OneToOne(mappedBy = "bill", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Analysis analysis;

    /**
     * Calcula o custo por kWh baseado na tarifa aplicada
     * @return Custo por kWh em Reais
     */
    public BigDecimal calculateCostPerKwh() {
        if (consumptionKwh == null || consumptionKwh.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return totalAmount.divide(consumptionKwh, 4, java.math.RoundingMode.HALF_UP);
    }

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
     * Adiciona um item à fatura e mantém consistência bidirecional
     * @param item Item a ser adicionado
     */
    public void addItem(BillItem item) {
        items.add(item);
        item.setBill(this);
    }

    /**
     * Remove um item da fatura e mantém consistência bidirecional
     * @param item Item a ser removido
     */
    public void removeItem(BillItem item) {
        items.remove(item);
        item.setBill(null);
    }

    /**
     * Valida se a data de vencimento é posterior ao mês de referência
     * @return true se válido
     */
    @Transient
    public boolean isValidDueDate() {
        return dueDate.isAfter(referenceMonth);
    }

}
