package com.understand_your_electricity_bill.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AnalysisResponseDTO Tests")
class AnalysisResponseDTOTest {

    // ========== BUILDER PATTERN TESTS ==========

    @Test
    @DisplayName("Should build DTO using builder pattern")
    void shouldBuildDtoUsingBuilderPattern() {
        // Given & When
        AnalysisResponseDTO dto = AnalysisResponseDTO.builder()
                .id(UUID.randomUUID())
                .billId(UUID.randomUUID())
                .averageConsumption(new BigDecimal("350.00"))
                .costPerKwh(new BigDecimal("1.1014"))
                .comparisonPrevMonth(new BigDecimal("5.5"))
                .savingsTips("Reduce peak consumption")
                .reportPdfUrl("/reports/analysis.pdf")
                .createdAt(LocalDateTime.now())
                .build();

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isNotNull();
        assertThat(dto.billId()).isNotNull();
    }

    // ========== FORMATTED AVERAGE CONSUMPTION TESTS ==========

    @Test
    @DisplayName("Should format average consumption")
    void shouldFormatAverageConsumption() {
        // Given
        AnalysisResponseDTO dto = AnalysisResponseDTO.builder()
                .averageConsumption(new BigDecimal("350.00"))
                .build();

        // When
        String formatted = dto.getFormattedAverageConsumption();

        // Then
        assertThat(formatted).isEqualTo("350,00 kWh");
    }

    @Test
    @DisplayName("Should handle null average consumption")
    void shouldHandleNullAverageConsumption() {
        // Given
        AnalysisResponseDTO dto = AnalysisResponseDTO.builder()
                .averageConsumption(null)
                .build();

        // When
        String formatted = dto.getFormattedAverageConsumption();

        // Then
        assertThat(formatted).isEqualTo("0,00 kWh");
    }

    // ========== FORMATTED COST PER KWH TESTS ==========

    @Test
    @DisplayName("Should format cost per kWh with 4 decimals")
    void shouldFormatCostPerKwhWith4Decimals() {
        // Given
        AnalysisResponseDTO dto = AnalysisResponseDTO.builder()
                .costPerKwh(new BigDecimal("1.1014"))
                .build();

        // When
        String formatted = dto.getFormattedCostPerKwh();

        // Then
        assertThat(formatted).isEqualTo("R$ 1,1014");
    }

    // ========== FORMATTED COMPARISON TESTS ==========

    @Test
    @DisplayName("Should format positive comparison with plus sign")
    void shouldFormatPositiveComparisonWithPlusSign() {
        // Given
        AnalysisResponseDTO dto = AnalysisResponseDTO.builder()
                .comparisonPrevMonth(new BigDecimal("5.5"))
                .build();

        // When
        String formatted = dto.getFormattedComparison();

        // Then
        assertThat(formatted).isEqualTo("+5,50%");
    }

    @Test
    @DisplayName("Should format negative comparison with minus sign")
    void shouldFormatNegativeComparisonWithMinusSign() {
        // Given
        AnalysisResponseDTO dto = AnalysisResponseDTO.builder()
                .comparisonPrevMonth(new BigDecimal("-10.5"))
                .build();

        // When
        String formatted = dto.getFormattedComparison();

        // Then
        assertThat(formatted).isEqualTo("-10,50%");
    }

    @Test
    @DisplayName("Should format zero comparison without sign")
    void shouldFormatZeroComparisonWithoutSign() {
        // Given
        AnalysisResponseDTO dto = AnalysisResponseDTO.builder()
                .comparisonPrevMonth(BigDecimal.ZERO)
                .build();

        // When
        String formatted = dto.getFormattedComparison();

        // Then
        assertThat(formatted).isEqualTo("0,00%");
    }

    @Test
    @DisplayName("Should handle null comparison")
    void shouldHandleNullComparison() {
        // Given
        AnalysisResponseDTO dto = AnalysisResponseDTO.builder()
                .comparisonPrevMonth(null)
                .build();

        // When
        String formatted = dto.getFormattedComparison();

        // Then
        assertThat(formatted).isEqualTo("0%");
    }

    // ========== COMPARISON STATUS TESTS ==========

    @Test
    @DisplayName("Should return INCREASED status for positive comparison")
    void shouldReturnIncreasedStatusForPositiveComparison() {
        // Given
        AnalysisResponseDTO dto = AnalysisResponseDTO.builder()
                .comparisonPrevMonth(new BigDecimal("5.5"))
                .build();

        // When
        AnalysisResponseDTO.ComparisonStatus status = dto.getComparisonStatus();

        // Then
        assertThat(status).isEqualTo(AnalysisResponseDTO.ComparisonStatus.INCREASED);
    }

    @Test
    @DisplayName("Should return DECREASED status for negative comparison")
    void shouldReturnDecreasedStatusForNegativeComparison() {
        // Given
        AnalysisResponseDTO dto = AnalysisResponseDTO.builder()
                .comparisonPrevMonth(new BigDecimal("-10.5"))
                .build();

        // When
        AnalysisResponseDTO.ComparisonStatus status = dto.getComparisonStatus();

        // Then
        assertThat(status).isEqualTo(AnalysisResponseDTO.ComparisonStatus.DECREASED);
    }

    @Test
    @DisplayName("Should return STABLE status for zero comparison")
    void shouldReturnStableStatusForZeroComparison() {
        // Given
        AnalysisResponseDTO dto = AnalysisResponseDTO.builder()
                .comparisonPrevMonth(BigDecimal.ZERO)
                .build();

        // When
        AnalysisResponseDTO.ComparisonStatus status = dto.getComparisonStatus();

        // Then
        assertThat(status).isEqualTo(AnalysisResponseDTO.ComparisonStatus.STABLE);
    }

    // ========== HAS SAVINGS TIPS TESTS ==========

    @Test
    @DisplayName("Should return true when has savings tips")
    void shouldReturnTrueWhenHasSavingsTips() {
        // Given
        AnalysisResponseDTO dto = AnalysisResponseDTO.builder()
                .savingsTips("Reduce peak consumption")
                .build();

        // When & Then
        assertThat(dto.hasSavingsTips()).isTrue();
    }

    @Test
    @DisplayName("Should return false when savings tips is null")
    void shouldReturnFalseWhenSavingsTipsIsNull() {
        // Given
        AnalysisResponseDTO dto = AnalysisResponseDTO.builder()
                .savingsTips(null)
                .build();

        // When & Then
        assertThat(dto.hasSavingsTips()).isFalse();
    }

    @Test
    @DisplayName("Should return false when savings tips is empty")
    void shouldReturnFalseWhenSavingsTipsIsEmpty() {
        // Given
        AnalysisResponseDTO dto = AnalysisResponseDTO.builder()
                .savingsTips("   ")
                .build();

        // When & Then
        assertThat(dto.hasSavingsTips()).isFalse();
    }

    // ========== HAS REPORT PDF TESTS ==========

    @Test
    @DisplayName("Should return true when has report PDF")
    void shouldReturnTrueWhenHasReportPdf() {
        // Given
        AnalysisResponseDTO dto = AnalysisResponseDTO.builder()
                .reportPdfUrl("/reports/analysis.pdf")
                .build();

        // When & Then
        assertThat(dto.hasReportPdf()).isTrue();
    }

    @Test
    @DisplayName("Should return false when report PDF is null")
    void shouldReturnFalseWhenReportPdfIsNull() {
        // Given
        AnalysisResponseDTO dto = AnalysisResponseDTO.builder()
                .reportPdfUrl(null)
                .build();

        // When & Then
        assertThat(dto.hasReportPdf()).isFalse();
    }

    // ========== SAVINGS TIPS PREVIEW TESTS ==========

    @Test
    @DisplayName("Should return full tips when short")
    void shouldReturnFullTipsWhenShort() {
        // Given
        AnalysisResponseDTO dto = AnalysisResponseDTO.builder()
                .savingsTips("Reduce peak consumption")
                .build();

        // When
        String preview = dto.getSavingsTipsPreview();

        // Then
        assertThat(preview).isEqualTo("Reduce peak consumption");
    }

    @Test
    @DisplayName("Should truncate long tips")
    void shouldTruncateLongTips() {
        // Given
        String longTips = "a".repeat(150);
        AnalysisResponseDTO dto = AnalysisResponseDTO.builder()
                .savingsTips(longTips)
                .build();

        // When
        String preview = dto.getSavingsTipsPreview();

        // Then
        assertThat(preview).hasSize(100);
        assertThat(preview).endsWith("...");
    }

    @Test
    @DisplayName("Should return empty when no tips")
    void shouldReturnEmptyWhenNoTips() {
        // Given
        AnalysisResponseDTO dto = AnalysisResponseDTO.builder()
                .savingsTips(null)
                .build();

        // When
        String preview = dto.getSavingsTipsPreview();

        // Then
        assertThat(preview).isEmpty();
    }

    // ========== SIGNIFICANT CHANGE TESTS ==========

    @Test
    @DisplayName("Should return true for significant increase")
    void shouldReturnTrueForSignificantIncrease() {
        // Given
        AnalysisResponseDTO dto = AnalysisResponseDTO.builder()
                .comparisonPrevMonth(new BigDecimal("15.5"))
                .build();

        // When & Then
        assertThat(dto.isSignificantIncrease()).isTrue();
    }

    @Test
    @DisplayName("Should return false for small increase")
    void shouldReturnFalseForSmallIncrease() {
        // Given
        AnalysisResponseDTO dto = AnalysisResponseDTO.builder()
                .comparisonPrevMonth(new BigDecimal("5.5"))
                .build();

        // When & Then
        assertThat(dto.isSignificantIncrease()).isFalse();
    }

    @Test
    @DisplayName("Should return true for significant decrease")
    void shouldReturnTrueForSignificantDecrease() {
        // Given
        AnalysisResponseDTO dto = AnalysisResponseDTO.builder()
                .comparisonPrevMonth(new BigDecimal("-15.5"))
                .build();

        // When & Then
        assertThat(dto.isSignificantDecrease()).isTrue();
    }

    @Test
    @DisplayName("Should return false for small decrease")
    void shouldReturnFalseForSmallDecrease() {
        // Given
        AnalysisResponseDTO dto = AnalysisResponseDTO.builder()
                .comparisonPrevMonth(new BigDecimal("-5.5"))
                .build();

        // When & Then
        assertThat(dto.isSignificantDecrease()).isFalse();
    }
}

