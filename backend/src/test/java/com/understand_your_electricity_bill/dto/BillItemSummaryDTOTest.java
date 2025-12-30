package com.understand_your_electricity_bill.dto;

import com.understand_your_electricity_bill.model.enums.BillItemType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BillItemSummaryDTO Tests")
class BillItemSummaryDTOTest {

    // ========== FORMATTED AMOUNT TESTS ==========

    @Test
    @DisplayName("Should format amount as currency")
    void shouldFormatAmountAsCurrency() {
        // Given
        BillItemSummaryDTO dto = new BillItemSummaryDTO(
                UUID.randomUUID(),
                BillItemType.OFF_PEAK_CONSUMPTION,
                "Energy consumption",
                new BigDecimal("350.00"),
                new BigDecimal("0.7700"),
                new BigDecimal("269.50")
        );

        // When
        String formatted = dto.getFormattedAmount();

        // Then
        assertThat(formatted).isEqualTo("R$ 269,50");
    }

    @Test
    @DisplayName("Should handle null amount")
    void shouldHandleNullAmount() {
        // Given
        BillItemSummaryDTO dto = new BillItemSummaryDTO(
                UUID.randomUUID(),
                BillItemType.OFF_PEAK_CONSUMPTION,
                "Energy consumption",
                null, null, null
        );

        // When
        String formatted = dto.getFormattedAmount();

        // Then
        assertThat(formatted).isEqualTo("R$ 0,00");
    }

    // ========== ITEM TYPE DISPLAY TESTS ==========

    @Test
    @DisplayName("Should return display name for consumption")
    void shouldReturnDisplayNameForConsumption() {
        // Given
        BillItemSummaryDTO dto = new BillItemSummaryDTO(
                UUID.randomUUID(),
                BillItemType.PEAK_CONSUMPTION,
                "Peak consumption",
                null, null, null
        );

        // When
        String display = dto.getItemTypeDisplay();

        // Then
        assertThat(display).isEqualTo("Consumo Ponta");
    }

    @Test
    @DisplayName("Should return display name for tax")
    void shouldReturnDisplayNameForTax() {
        // Given
        BillItemSummaryDTO dto = new BillItemSummaryDTO(
                UUID.randomUUID(),
                BillItemType.COFINS_TAX,
                "COFINS",
                null, null, new BigDecimal("32.49")
        );

        // When
        String display = dto.getItemTypeDisplay();

        // Then
        assertThat(display).isEqualTo("COFINS");
    }

    // ========== HAS UNIT PRICING TESTS ==========

    @Test
    @DisplayName("Should return true when has unit pricing")
    void shouldReturnTrueWhenHasUnitPricing() {
        // Given
        BillItemSummaryDTO dto = new BillItemSummaryDTO(
                UUID.randomUUID(),
                BillItemType.OFF_PEAK_CONSUMPTION,
                "Energy",
                new BigDecimal("350.00"),
                new BigDecimal("0.7700"),
                new BigDecimal("269.50")
        );

        // When & Then
        assertThat(dto.hasUnitPricing()).isTrue();
    }

    @Test
    @DisplayName("Should return false when missing quantity")
    void shouldReturnFalseWhenMissingQuantity() {
        // Given
        BillItemSummaryDTO dto = new BillItemSummaryDTO(
                UUID.randomUUID(),
                BillItemType.PUBLIC_LIGHTING,
                "Fixed charge",
                null,
                new BigDecimal("35.00"),
                new BigDecimal("35.00")
        );

        // When & Then
        assertThat(dto.hasUnitPricing()).isFalse();
    }

    // ========== IS TAX TESTS ==========

    @Test
    @DisplayName("Should return true for ICMS")
    void shouldReturnTrueForIcms() {
        // Given
        BillItemSummaryDTO dto = new BillItemSummaryDTO(
                UUID.randomUUID(),
                BillItemType.ICMS_TAX,
                "ICMS 18%",
                null, null, new BigDecimal("48.51")
        );

        // When & Then
        assertThat(dto.isTax()).isTrue();
    }

    @Test
    @DisplayName("Should return false for non-tax items")
    void shouldReturnFalseForNonTaxItems() {
        // Given
        BillItemSummaryDTO dto = new BillItemSummaryDTO(
                UUID.randomUUID(),
                BillItemType.OFF_PEAK_CONSUMPTION,
                "Energy",
                new BigDecimal("350.00"),
                new BigDecimal("0.7700"),
                new BigDecimal("269.50")
        );

        // When & Then
        assertThat(dto.isTax()).isFalse();
    }

    // ========== IS CONSUMPTION TESTS ==========

    @Test
    @DisplayName("Should return true for consumption types")
    void shouldReturnTrueForConsumptionTypes() {
        // Given
        BillItemSummaryDTO dto = new BillItemSummaryDTO(
                UUID.randomUUID(),
                BillItemType.INTERMEDIATE_CONSUMPTION,
                "Intermediate energy",
                new BigDecimal("100.00"),
                new BigDecimal("0.8000"),
                new BigDecimal("80.00")
        );

        // When & Then
        assertThat(dto.isConsumption()).isTrue();
    }

    @Test
    @DisplayName("Should return false for non-consumption types")
    void shouldReturnFalseForNonConsumptionTypes() {
        // Given
        BillItemSummaryDTO dto = new BillItemSummaryDTO(
                UUID.randomUUID(),
                BillItemType.DISCOUNT,
                "Special discount",
                null, null, new BigDecimal("-50.00")
        );

        // When & Then
        assertThat(dto.isConsumption()).isFalse();
    }

    // ========== SHORT DESCRIPTION TESTS ==========

    @Test
    @DisplayName("Should return full description when short")
    void shouldReturnFullDescriptionWhenShort() {
        // Given
        BillItemSummaryDTO dto = new BillItemSummaryDTO(
                UUID.randomUUID(),
                BillItemType.OFF_PEAK_CONSUMPTION,
                "Energy consumption",
                null, null, null
        );

        // When
        String shortDesc = dto.getShortDescription();

        // Then
        assertThat(shortDesc).isEqualTo("Energy consumption");
    }

    @Test
    @DisplayName("Should truncate long description")
    void shouldTruncateLongDescription() {
        // Given
        String longDesc = "This is a very long description that exceeds fifty characters";
        BillItemSummaryDTO dto = new BillItemSummaryDTO(
                UUID.randomUUID(),
                BillItemType.OFF_PEAK_CONSUMPTION,
                longDesc,
                null, null, null
        );

        // When
        String shortDesc = dto.getShortDescription();

        // Then
        assertThat(shortDesc).hasSize(50);
        assertThat(shortDesc).endsWith("...");
        assertThat(shortDesc).startsWith("This is a very long description");
    }

    @Test
    @DisplayName("Should handle null description")
    void shouldHandleNullDescription() {
        // Given
        BillItemSummaryDTO dto = new BillItemSummaryDTO(
                UUID.randomUUID(),
                BillItemType.OFF_PEAK_CONSUMPTION,
                null,
                null, null, null
        );

        // When
        String shortDesc = dto.getShortDescription();

        // Then
        assertThat(shortDesc).isNull();
    }

    @Test
    @DisplayName("Should handle description exactly 50 chars")
    void shouldHandleDescriptionExactly50Chars() {
        // Given
        String desc = "a".repeat(50);
        BillItemSummaryDTO dto = new BillItemSummaryDTO(
                UUID.randomUUID(),
                BillItemType.OFF_PEAK_CONSUMPTION,
                desc,
                null, null, null
        );

        // When
        String shortDesc = dto.getShortDescription();

        // Then
        assertThat(shortDesc).hasSize(50);
        assertThat(shortDesc).doesNotContain("...");
    }
}

