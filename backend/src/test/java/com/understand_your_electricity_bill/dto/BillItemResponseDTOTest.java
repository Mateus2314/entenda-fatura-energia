package com.understand_your_electricity_bill.dto;

import com.understand_your_electricity_bill.model.enums.BillItemType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BillItemResponseDTO Tests")
class BillItemResponseDTOTest {

    // ========== BUILDER PATTERN TESTS ==========

    @Test
    @DisplayName("Should build DTO using builder pattern")
    void shouldBuildDtoUsingBuilderPattern() {
        // Given & When
        BillItemResponseDTO dto = BillItemResponseDTO.builder()
                .id(UUID.randomUUID())
                .billId(UUID.randomUUID())
                .itemType(BillItemType.OFF_PEAK_CONSUMPTION)
                .description("Energy consumption")
                .quantity(new BigDecimal("350.00"))
                .unitPrice(new BigDecimal("0.7700"))
                .amount(new BigDecimal("269.50"))
                .createdAt(LocalDateTime.now())
                .build();

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isNotNull();
        assertThat(dto.billId()).isNotNull();
        assertThat(dto.itemType()).isEqualTo(BillItemType.OFF_PEAK_CONSUMPTION);
    }

    // ========== FORMATTED AMOUNT TESTS ==========

    @Test
    @DisplayName("Should format amount as currency")
    void shouldFormatAmountAsCurrency() {
        // Given
        BillItemResponseDTO dto = BillItemResponseDTO.builder()
                .amount(new BigDecimal("269.50"))
                .build();

        // When
        String formatted = dto.getFormattedAmount();

        // Then
        assertThat(formatted).isEqualTo("R$ 269,50");
    }

    @Test
    @DisplayName("Should handle null amount")
    void shouldHandleNullAmount() {
        // Given
        BillItemResponseDTO dto = BillItemResponseDTO.builder()
                .amount(null)
                .build();

        // When
        String formatted = dto.getFormattedAmount();

        // Then
        assertThat(formatted).isEqualTo("R$ 0,00");
    }

    // ========== FORMATTED UNIT PRICE TESTS ==========

    @Test
    @DisplayName("Should format unit price with 4 decimals")
    void shouldFormatUnitPriceWith4Decimals() {
        // Given
        BillItemResponseDTO dto = BillItemResponseDTO.builder()
                .unitPrice(new BigDecimal("0.7700"))
                .build();

        // When
        String formatted = dto.getFormattedUnitPrice();

        // Then
        assertThat(formatted).isEqualTo("R$ 0,7700");
    }

    // ========== ITEM TYPE DISPLAY TESTS ==========

    @Test
    @DisplayName("Should return display name for OFF_PEAK_CONSUMPTION")
    void shouldReturnDisplayNameForOffPeakConsumption() {
        // Given
        BillItemResponseDTO dto = BillItemResponseDTO.builder()
                .itemType(BillItemType.OFF_PEAK_CONSUMPTION)
                .build();

        // When
        String display = dto.getItemTypeDisplay();

        // Then
        assertThat(display).isEqualTo("Consumo Fora Ponta");
    }

    @Test
    @DisplayName("Should return display name for ICMS_TAX")
    void shouldReturnDisplayNameForIcmsTax() {
        // Given
        BillItemResponseDTO dto = BillItemResponseDTO.builder()
                .itemType(BillItemType.ICMS_TAX)
                .build();

        // When
        String display = dto.getItemTypeDisplay();

        // Then
        assertThat(display).isEqualTo("ICMS");
    }

    @Test
    @DisplayName("Should return N/A when item type is null")
    void shouldReturnNAWhenItemTypeIsNull() {
        // Given
        BillItemResponseDTO dto = BillItemResponseDTO.builder()
                .itemType(null)
                .build();

        // When
        String display = dto.getItemTypeDisplay();

        // Then
        assertThat(display).isEqualTo("N/A");
    }

    // ========== HAS UNIT PRICING TESTS ==========

    @Test
    @DisplayName("Should return true when has quantity and unit price")
    void shouldReturnTrueWhenHasQuantityAndUnitPrice() {
        // Given
        BillItemResponseDTO dto = BillItemResponseDTO.builder()
                .quantity(new BigDecimal("350.00"))
                .unitPrice(new BigDecimal("0.7700"))
                .build();

        // When & Then
        assertThat(dto.hasUnitPricing()).isTrue();
    }

    @Test
    @DisplayName("Should return false when quantity is null")
    void shouldReturnFalseWhenQuantityIsNull() {
        // Given
        BillItemResponseDTO dto = BillItemResponseDTO.builder()
                .quantity(null)
                .unitPrice(new BigDecimal("0.7700"))
                .build();

        // When & Then
        assertThat(dto.hasUnitPricing()).isFalse();
    }

    @Test
    @DisplayName("Should return false when unit price is null")
    void shouldReturnFalseWhenUnitPriceIsNull() {
        // Given
        BillItemResponseDTO dto = BillItemResponseDTO.builder()
                .quantity(new BigDecimal("350.00"))
                .unitPrice(null)
                .build();

        // When & Then
        assertThat(dto.hasUnitPricing()).isFalse();
    }

    // ========== CALCULATE PERCENTAGE TESTS ==========

    @Test
    @DisplayName("Should calculate percentage of total correctly")
    void shouldCalculatePercentageOfTotalCorrectly() {
        // Given
        BillItemResponseDTO dto = BillItemResponseDTO.builder()
                .amount(new BigDecimal("269.50"))
                .build();
        BigDecimal totalAmount = new BigDecimal("385.50");

        // When
        BigDecimal percentage = dto.calculatePercentage(totalAmount);

        // Then
        // 269.50 / 385.50 * 100 = 69.91% (rounded to 4 decimals: 69.9100)
        assertThat(percentage).isEqualByComparingTo(new BigDecimal("69.9100"));
    }

    @Test
    @DisplayName("Should return zero when total is zero")
    void shouldReturnZeroWhenTotalIsZero() {
        // Given
        BillItemResponseDTO dto = BillItemResponseDTO.builder()
                .amount(new BigDecimal("269.50"))
                .build();

        // When
        BigDecimal percentage = dto.calculatePercentage(BigDecimal.ZERO);

        // Then
        assertThat(percentage).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should return zero when amount is null")
    void shouldReturnZeroWhenAmountIsNull() {
        // Given
        BillItemResponseDTO dto = BillItemResponseDTO.builder()
                .amount(null)
                .build();

        // When
        BigDecimal percentage = dto.calculatePercentage(new BigDecimal("385.50"));

        // Then
        assertThat(percentage).isEqualByComparingTo(BigDecimal.ZERO);
    }

    // ========== IS TAX TESTS ==========

    @Test
    @DisplayName("Should return true for ICMS tax")
    void shouldReturnTrueForIcmsTax() {
        // Given
        BillItemResponseDTO dto = BillItemResponseDTO.builder()
                .itemType(BillItemType.ICMS_TAX)
                .build();

        // When & Then
        assertThat(dto.isTax()).isTrue();
    }

    @Test
    @DisplayName("Should return true for PIS tax")
    void shouldReturnTrueForPisTax() {
        // Given
        BillItemResponseDTO dto = BillItemResponseDTO.builder()
                .itemType(BillItemType.PIS_TAX)
                .build();

        // When & Then
        assertThat(dto.isTax()).isTrue();
    }

    @Test
    @DisplayName("Should return false for consumption")
    void shouldReturnFalseForConsumption() {
        // Given
        BillItemResponseDTO dto = BillItemResponseDTO.builder()
                .itemType(BillItemType.OFF_PEAK_CONSUMPTION)
                .build();

        // When & Then
        assertThat(dto.isTax()).isFalse();
    }

    // ========== IS CONSUMPTION TESTS ==========

    @Test
    @DisplayName("Should return true for off-peak consumption")
    void shouldReturnTrueForOffPeakConsumption() {
        // Given
        BillItemResponseDTO dto = BillItemResponseDTO.builder()
                .itemType(BillItemType.OFF_PEAK_CONSUMPTION)
                .build();

        // When & Then
        assertThat(dto.isConsumption()).isTrue();
    }

    @Test
    @DisplayName("Should return true for peak consumption")
    void shouldReturnTrueForPeakConsumption() {
        // Given
        BillItemResponseDTO dto = BillItemResponseDTO.builder()
                .itemType(BillItemType.PEAK_CONSUMPTION)
                .build();

        // When & Then
        assertThat(dto.isConsumption()).isTrue();
    }

    @Test
    @DisplayName("Should return false for tax")
    void shouldReturnFalseForTax() {
        // Given
        BillItemResponseDTO dto = BillItemResponseDTO.builder()
                .itemType(BillItemType.ICMS_TAX)
                .build();

        // When & Then
        assertThat(dto.isConsumption()).isFalse();
    }
}

