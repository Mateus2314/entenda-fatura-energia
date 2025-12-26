package com.understand_your_electricity_bill.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bill_items", indexes = {
    @Index(name = "idx_bill_items_bill_id", columnList = "bill_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "Bill is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bill_id", nullable = false)
    private ElectricityBill bill;

    @NotBlank(message = "Item type is required")
    @Size(max = 50, message = "Item type must not exceed 50 characters")
    @Column(name = "item_type", length = 50, nullable = false)
    private String itemType;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    @Column(name = "description", length = 255)
    private String description;

    @DecimalMin(value = "0.0", inclusive = true, message = "Quantity must be non-negative")
    @Column(name = "quantity", precision = 10, scale = 2)
    private BigDecimal quantity;

    @DecimalMin(value = "0.0", inclusive = true, message = "Unit price must be non-negative")
    @Column(name = "unit_price", precision = 10, scale = 4)
    private BigDecimal unitPrice;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", message = "Amount must be greater than or equal to 0")
    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

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

