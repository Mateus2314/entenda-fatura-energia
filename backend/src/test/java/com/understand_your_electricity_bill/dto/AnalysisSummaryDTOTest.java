package com.understand_your_electricity_bill.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AnalysisSummaryDTO Tests")
class AnalysisSummaryDTOTest {

    // ========== FORMATTED AVERAGE CONSUMPTION TESTS ==========

    @Test
    @DisplayName("Should format average consumption")
    void shouldFormatAverageConsumption() {
        // Given
        AnalysisSummaryDTO dto = new AnalysisSummaryDTO(
                UUID.randomUUID(),
                new BigDecimal("350.00"),
                new BigDecimal("1.1014"),
                new BigDecimal("5.5"),
                "Reduce peak consumption"
        );

        // When
        String formatted = dto.getFormattedAverageConsumption();

        // Then
        assertThat(formatted).isEqualTo("350,00 kWh");
    }

    @Test
    @DisplayName("Should handle null average consumption")
    void shouldHandleNullAverageConsumption() {
        // Given
        AnalysisSummaryDTO dto = new AnalysisSummaryDTO(
                UUID.randomUUID(), null, null, null, null
        );

        // When
        String formatted = dto.getFormattedAverageConsumption();

        // Then
        assertThat(formatted).isEqualTo("0,00 kWh");
    }

    // ========== FORMATTED COST PER KWH TESTS ==========

    @Test
    @DisplayName("Should format cost per kWh")
    void shouldFormatCostPerKwh() {
        // Given
        AnalysisSummaryDTO dto = new AnalysisSummaryDTO(
                UUID.randomUUID(),
                new BigDecimal("350.00"),
                new BigDecimal("1.1014"),
                null, null
        );

        // When
        String formatted = dto.getFormattedCostPerKwh();

        // Then
        assertThat(formatted).isEqualTo("R$ 1,1014");
    }

    // ========== FORMATTED COMPARISON TESTS ==========

    @Test
    @DisplayName("Should format positive comparison")
    void shouldFormatPositiveComparison() {
        // Given
        AnalysisSummaryDTO dto = new AnalysisSummaryDTO(
                UUID.randomUUID(), null, null,
                new BigDecimal("5.5"),
                null
        );

        // When
        String formatted = dto.getFormattedComparison();

        // Then
        assertThat(formatted).isEqualTo("+5,50%");
    }

    @Test
    @DisplayName("Should format negative comparison")
    void shouldFormatNegativeComparison() {
        // Given
        AnalysisSummaryDTO dto = new AnalysisSummaryDTO(
                UUID.randomUUID(), null, null,
                new BigDecimal("-10.5"),
                null
        );

        // When
        String formatted = dto.getFormattedComparison();

        // Then
        assertThat(formatted).isEqualTo("-10,50%");
    }

    // ========== COMPARISON STATUS TESTS ==========

    @Test
    @DisplayName("Should return INCREASED for positive comparison")
    void shouldReturnIncreasedForPositiveComparison() {
        // Given
        AnalysisSummaryDTO dto = new AnalysisSummaryDTO(
                UUID.randomUUID(), null, null,
                new BigDecimal("5.5"),
                null
        );

        // When
        AnalysisSummaryDTO.ComparisonStatus status = dto.getComparisonStatus();

        // Then
        assertThat(status).isEqualTo(AnalysisSummaryDTO.ComparisonStatus.INCREASED);
    }

    @Test
    @DisplayName("Should return DECREASED for negative comparison")
    void shouldReturnDecreasedForNegativeComparison() {
        // Given
        AnalysisSummaryDTO dto = new AnalysisSummaryDTO(
                UUID.randomUUID(), null, null,
                new BigDecimal("-10.5"),
                null
        );

        // When
        AnalysisSummaryDTO.ComparisonStatus status = dto.getComparisonStatus();

        // Then
        assertThat(status).isEqualTo(AnalysisSummaryDTO.ComparisonStatus.DECREASED);
    }

    @Test
    @DisplayName("Should return STABLE for zero comparison")
    void shouldReturnStableForZeroComparison() {
        // Given
        AnalysisSummaryDTO dto = new AnalysisSummaryDTO(
                UUID.randomUUID(), null, null,
                BigDecimal.ZERO,
                null
        );

        // When
        AnalysisSummaryDTO.ComparisonStatus status = dto.getComparisonStatus();

        // Then
        assertThat(status).isEqualTo(AnalysisSummaryDTO.ComparisonStatus.STABLE);
    }

    // ========== HAS SAVINGS TIPS TESTS ==========

    @Test
    @DisplayName("Should return true when has savings tips")
    void shouldReturnTrueWhenHasSavingsTips() {
        // Given
        AnalysisSummaryDTO dto = new AnalysisSummaryDTO(
                UUID.randomUUID(), null, null, null,
                "Reduce peak consumption"
        );

        // When & Then
        assertThat(dto.hasSavingsTips()).isTrue();
    }

    @Test
    @DisplayName("Should return false when savings tips is null")
    void shouldReturnFalseWhenSavingsTipsIsNull() {
        // Given
        AnalysisSummaryDTO dto = new AnalysisSummaryDTO(
                UUID.randomUUID(), null, null, null, null
        );

        // When & Then
        assertThat(dto.hasSavingsTips()).isFalse();
    }

    @Test
    @DisplayName("Should return false when savings tips is blank")
    void shouldReturnFalseWhenSavingsTipsIsBlank() {
        // Given
        AnalysisSummaryDTO dto = new AnalysisSummaryDTO(
                UUID.randomUUID(), null, null, null, "   "
        );

        // When & Then
        assertThat(dto.hasSavingsTips()).isFalse();
    }

    // ========== SHORT SAVINGS TIPS TESTS ==========

    @Test
    @DisplayName("Should return full tips when short")
    void shouldReturnFullTipsWhenShort() {
        // Given
        AnalysisSummaryDTO dto = new AnalysisSummaryDTO(
                UUID.randomUUID(), null, null, null,
                "Reduce peak consumption"
        );

        // When
        String shortTips = dto.getShortSavingsTips();

        // Then
        assertThat(shortTips).isEqualTo("Reduce peak consumption");
    }

    @Test
    @DisplayName("Should truncate long tips")
    void shouldTruncateLongTips() {
        // Given
        String longTips = "a".repeat(150);
        AnalysisSummaryDTO dto = new AnalysisSummaryDTO(
                UUID.randomUUID(), null, null, null,
                longTips
        );

        // When
        String shortTips = dto.getShortSavingsTips();

        // Then
        assertThat(shortTips).hasSize(100);
        assertThat(shortTips).endsWith("...");
    }

    @Test
    @DisplayName("Should return empty when no tips")
    void shouldReturnEmptyWhenNoTips() {
        // Given
        AnalysisSummaryDTO dto = new AnalysisSummaryDTO(
                UUID.randomUUID(), null, null, null, null
        );

        // When
        String shortTips = dto.getShortSavingsTips();

        // Then
        assertThat(shortTips).isEmpty();
    }

    @Test
    @DisplayName("Should handle tips exactly 100 chars")
    void shouldHandleTipsExactly100Chars() {
        // Given
        String tips = "a".repeat(100);
        AnalysisSummaryDTO dto = new AnalysisSummaryDTO(
                UUID.randomUUID(), null, null, null, tips
        );

        // When
        String shortTips = dto.getShortSavingsTips();

        // Then
        assertThat(shortTips).hasSize(100);
        assertThat(shortTips).doesNotContain("...");
    }

    // ========== SIGNIFICANT CHANGE TESTS ==========

    @Test
    @DisplayName("Should return true for significant increase")
    void shouldReturnTrueForSignificantIncrease() {
        // Given
        AnalysisSummaryDTO dto = new AnalysisSummaryDTO(
                UUID.randomUUID(), null, null,
                new BigDecimal("15.5"),
                null
        );

        // When & Then
        assertThat(dto.isSignificantChange()).isTrue();
    }

    @Test
    @DisplayName("Should return true for significant decrease")
    void shouldReturnTrueForSignificantDecrease() {
        // Given
        AnalysisSummaryDTO dto = new AnalysisSummaryDTO(
                UUID.randomUUID(), null, null,
                new BigDecimal("-15.5"),
                null
        );

        // When & Then
        assertThat(dto.isSignificantChange()).isTrue();
    }

    @Test
    @DisplayName("Should return false for small change")
    void shouldReturnFalseForSmallChange() {
        // Given
        AnalysisSummaryDTO dto = new AnalysisSummaryDTO(
                UUID.randomUUID(), null, null,
                new BigDecimal("5.5"),
                null
        );

        // When & Then
        assertThat(dto.isSignificantChange()).isFalse();
    }

    @Test
    @DisplayName("Should return false when comparison is null")
    void shouldReturnFalseWhenComparisonIsNull() {
        // Given
        AnalysisSummaryDTO dto = new AnalysisSummaryDTO(
                UUID.randomUUID(), null, null, null, null
        );

        // When & Then
        assertThat(dto.isSignificantChange()).isFalse();
    }

    @Test
    @DisplayName("Should return false for exactly 10% change")
    void shouldReturnFalseForExactly10PercentChange() {
        // Given
        AnalysisSummaryDTO dto = new AnalysisSummaryDTO(
                UUID.randomUUID(), null, null,
                new BigDecimal("10.0"),
                null
        );

        // When & Then
        assertThat(dto.isSignificantChange()).isFalse();
    }
}

