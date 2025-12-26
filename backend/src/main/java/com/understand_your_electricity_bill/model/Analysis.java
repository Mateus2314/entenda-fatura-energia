package com.understand_your_electricity_bill.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "analyses", indexes = {
    @Index(name = "idx_analyses_bill_id", columnList = "bill_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Analysis {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "Bill is required")
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bill_id", nullable = false, unique = true)
    private ElectricityBill bill;

    @Column(name = "average_consumption", precision = 10, scale = 2)
    private java.math.BigDecimal averageConsumption;

    @Column(name = "cost_per_kwh", precision = 10, scale = 4)
    private java.math.BigDecimal costPerKwh;

    @Column(name = "comparison_prev_month", precision = 5, scale = 2)
    private java.math.BigDecimal comparisonPrevMonth;

    @Column(name = "savings_tips", columnDefinition = "TEXT")
    private String savingsTips;

    @Column(name = "report_pdf_url", columnDefinition = "TEXT")
    private String reportPdfUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}

