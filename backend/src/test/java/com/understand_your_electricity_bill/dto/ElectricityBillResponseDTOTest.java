package com.understand_your_electricity_bill.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ElectricityBillResponseDTO Tests")
class ElectricityBillResponseDTOTest {

    // ========== BUILDER PATTERN TESTS ==========

    @Test
    @DisplayName("Should build DTO using builder pattern")
    void shouldBuildDtoUsingBuilderPattern() {
        // Given & When
        ElectricityBillResponseDTO dto = ElectricityBillResponseDTO.builder()
                .id(UUID.randomUUID())
                .clientId(UUID.randomUUID())
                .tariffId(UUID.randomUUID())
                .referenceMonth(LocalDate.of(2024, 1, 1))
                .dueDate(LocalDate.of(2024, 1, 20))
                .totalAmount(new BigDecimal("385.50"))
                .consumptionKwh(new BigDecimal("350.00"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isNotNull();
        assertThat(dto.clientId()).isNotNull();
        assertThat(dto.tariffId()).isNotNull();
    }

    // ========== FORMATTED REFERENCE MONTH TESTS ==========

    @Test
    @DisplayName("Should format reference month correctly")
    void shouldFormatReferenceMonthCorrectly() {
        // Given
        ElectricityBillResponseDTO dto = ElectricityBillResponseDTO.builder()
                .referenceMonth(LocalDate.of(2024, 1, 1))
                .build();

        // When
        String formatted = dto.getFormattedReferenceMonth();

        // Then
        // Accepts formats like "jan./2024", "Jan/2024", "jan/2024" (locale-dependent)
        assertThat(formatted).matches("(?i)(jan\\.?)/2024");
    }

    @Test
    @DisplayName("Should return empty string when reference month is null")
    void shouldReturnEmptyStringWhenReferenceMonthIsNull() {
        // Given
        ElectricityBillResponseDTO dto = ElectricityBillResponseDTO.builder()
                .referenceMonth(null)
                .build();

        // When
        String formatted = dto.getFormattedReferenceMonth();

        // Then
        assertThat(formatted).isEmpty();
    }

    // ========== FORMATTED DUE DATE TESTS ==========

    @Test
    @DisplayName("Should format due date correctly")
    void shouldFormatDueDateCorrectly() {
        // Given
        ElectricityBillResponseDTO dto = ElectricityBillResponseDTO.builder()
                .dueDate(LocalDate.of(2024, 1, 20))
                .build();

        // When
        String formatted = dto.getFormattedDueDate();

        // Then
        assertThat(formatted).isEqualTo("20/01/2024");
    }

    // ========== COST PER KWH TESTS ==========

    @Test
    @DisplayName("Should calculate cost per kWh correctly")
    void shouldCalculateCostPerKwhCorrectly() {
        // Given
        ElectricityBillResponseDTO dto = ElectricityBillResponseDTO.builder()
                .totalAmount(new BigDecimal("385.50"))
                .consumptionKwh(new BigDecimal("350.00"))
                .build();

        // When
        BigDecimal costPerKwh = dto.getCostPerKwh();

        // Then
        assertThat(costPerKwh).isEqualByComparingTo(new BigDecimal("1.1014"));
    }

    @Test
    @DisplayName("Should return zero when consumption is zero")
    void shouldReturnZeroWhenConsumptionIsZero() {
        // Given
        ElectricityBillResponseDTO dto = ElectricityBillResponseDTO.builder()
                .totalAmount(new BigDecimal("385.50"))
                .consumptionKwh(BigDecimal.ZERO)
                .build();

        // When
        BigDecimal costPerKwh = dto.getCostPerKwh();

        // Then
        assertThat(costPerKwh).isEqualByComparingTo(BigDecimal.ZERO);
    }

    // ========== FORMATTED TOTAL AMOUNT TESTS ==========

    @Test
    @DisplayName("Should format total amount as currency")
    void shouldFormatTotalAmountAsCurrency() {
        // Given
        ElectricityBillResponseDTO dto = ElectricityBillResponseDTO.builder()
                .totalAmount(new BigDecimal("385.50"))
                .build();

        // When
        String formatted = dto.getFormattedTotalAmount();

        // Then
        assertThat(formatted).isEqualTo("R$ 385,50");
    }

    @Test
    @DisplayName("Should handle null total amount")
    void shouldHandleNullTotalAmount() {
        // Given
        ElectricityBillResponseDTO dto = ElectricityBillResponseDTO.builder()
                .totalAmount(null)
                .build();

        // When
        String formatted = dto.getFormattedTotalAmount();

        // Then
        assertThat(formatted).isEqualTo("R$ 0,00");
    }

    // ========== HAS CONSULTANT TESTS ==========

    @Test
    @DisplayName("Should return true when consultant is assigned")
    void shouldReturnTrueWhenConsultantIsAssigned() {
        // Given
        ElectricityBillResponseDTO dto = ElectricityBillResponseDTO.builder()
                .consultantId(UUID.randomUUID())
                .build();

        // When & Then
        assertThat(dto.hasConsultant()).isTrue();
    }

    @Test
    @DisplayName("Should return false when consultant is not assigned")
    void shouldReturnFalseWhenConsultantIsNotAssigned() {
        // Given
        ElectricityBillResponseDTO dto = ElectricityBillResponseDTO.builder()
                .consultantId(null)
                .build();

        // When & Then
        assertThat(dto.hasConsultant()).isFalse();
    }

    // ========== HAS PDF TESTS ==========

    @Test
    @DisplayName("Should return true when PDF URL exists")
    void shouldReturnTrueWhenPdfUrlExists() {
        // Given
        ElectricityBillResponseDTO dto = ElectricityBillResponseDTO.builder()
                .pdfUrl("/bills/invoice.pdf")
                .build();

        // When & Then
        assertThat(dto.hasPdf()).isTrue();
    }

    @Test
    @DisplayName("Should return false when PDF URL is null")
    void shouldReturnFalseWhenPdfUrlIsNull() {
        // Given
        ElectricityBillResponseDTO dto = ElectricityBillResponseDTO.builder()
                .pdfUrl(null)
                .build();

        // When & Then
        assertThat(dto.hasPdf()).isFalse();
    }

    @Test
    @DisplayName("Should return false when PDF URL is empty")
    void shouldReturnFalseWhenPdfUrlIsEmpty() {
        // Given
        ElectricityBillResponseDTO dto = ElectricityBillResponseDTO.builder()
                .pdfUrl("   ")
                .build();

        // When & Then
        assertThat(dto.hasPdf()).isFalse();
    }

    // ========== IS OVERDUE TESTS ==========

    @Test
    @DisplayName("Should return true when bill is overdue")
    void shouldReturnTrueWhenBillIsOverdue() {
        // Given
        ElectricityBillResponseDTO dto = ElectricityBillResponseDTO.builder()
                .dueDate(LocalDate.now().minusDays(10))
                .build();

        // When & Then
        assertThat(dto.isOverdue()).isTrue();
    }

    @Test
    @DisplayName("Should return false when bill is not overdue")
    void shouldReturnFalseWhenBillIsNotOverdue() {
        // Given
        ElectricityBillResponseDTO dto = ElectricityBillResponseDTO.builder()
                .dueDate(LocalDate.now().plusDays(10))
                .build();

        // When & Then
        assertThat(dto.isOverdue()).isFalse();
    }

    @Test
    @DisplayName("Should return false when due date is null")
    void shouldReturnFalseWhenDueDateIsNull() {
        // Given
        ElectricityBillResponseDTO dto = ElectricityBillResponseDTO.builder()
                .dueDate(null)
                .build();

        // When & Then
        assertThat(dto.isOverdue()).isFalse();
    }

    // ========== DAYS UNTIL DUE TESTS ==========

    @Test
    @DisplayName("Should return positive days when due date is in future")
    void shouldReturnPositiveDaysWhenDueDateIsInFuture() {
        // Given
        ElectricityBillResponseDTO dto = ElectricityBillResponseDTO.builder()
                .dueDate(LocalDate.now().plusDays(5))
                .build();

        // When
        long days = dto.getDaysUntilDue();

        // Then
        assertThat(days).isEqualTo(5);
    }

    @Test
    @DisplayName("Should return negative days when bill is overdue")
    void shouldReturnNegativeDaysWhenBillIsOverdue() {
        // Given
        ElectricityBillResponseDTO dto = ElectricityBillResponseDTO.builder()
                .dueDate(LocalDate.now().minusDays(5))
                .build();

        // When
        long days = dto.getDaysUntilDue();

        // Then
        assertThat(days).isEqualTo(-5);
    }

    @Test
    @DisplayName("Should return zero when due date is null")
    void shouldReturnZeroWhenDueDateIsNull() {
        // Given
        ElectricityBillResponseDTO dto = ElectricityBillResponseDTO.builder()
                .dueDate(null)
                .build();

        // When
        long days = dto.getDaysUntilDue();

        // Then
        assertThat(days).isEqualTo(0);
    }
}

