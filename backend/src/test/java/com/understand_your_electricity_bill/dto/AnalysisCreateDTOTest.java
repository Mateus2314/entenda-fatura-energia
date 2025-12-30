package com.understand_your_electricity_bill.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AnalysisCreateDTO Tests")
class AnalysisCreateDTOTest {

    private Validator getValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            return factory.getValidator();
        }
    }

    // ========== VALID DTO TESTS ==========

    @Test
    @DisplayName("Should create valid DTO with all required fields")
    void shouldCreateValidDtoWithAllRequiredFields() {
        // Given
        Validator validator = getValidator();
        AnalysisCreateDTO dto = new AnalysisCreateDTO(
                UUID.randomUUID(),
                null, null, null, null, null
        );

        // When
        Set<ConstraintViolation<AnalysisCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should create valid DTO with all fields")
    void shouldCreateValidDtoWithAllFields() {
        // Given
        Validator validator = getValidator();
        AnalysisCreateDTO dto = new AnalysisCreateDTO(
                UUID.randomUUID(),
                new BigDecimal("350.00"),
                new BigDecimal("1.1014"),
                new BigDecimal("5.5"),
                "Reduce consumption during peak hours",
                "/reports/2024/01/analysis.pdf"
        );

        // When
        Set<ConstraintViolation<AnalysisCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    // ========== REQUIRED FIELDS VALIDATION TESTS ==========

    @Test
    @DisplayName("Should fail when bill ID is null")
    void shouldFailWhenBillIdIsNull() {
        // Given
        Validator validator = getValidator();
        AnalysisCreateDTO dto = new AnalysisCreateDTO(
                null,
                new BigDecimal("350.00"),
                new BigDecimal("1.1014"),
                new BigDecimal("5.5"),
                "Tips",
                "/report.pdf"
        );

        // When
        Set<ConstraintViolation<AnalysisCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("billId"));
    }

    // ========== VALUE VALIDATION TESTS ==========

    @Test
    @DisplayName("Should fail when average consumption is negative")
    void shouldFailWhenAverageConsumptionIsNegative() {
        // Given
        Validator validator = getValidator();
        AnalysisCreateDTO dto = new AnalysisCreateDTO(
                UUID.randomUUID(),
                new BigDecimal("-100.00"),
                new BigDecimal("1.1014"),
                new BigDecimal("5.5"),
                null, null
        );

        // When
        Set<ConstraintViolation<AnalysisCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("averageConsumption"));
    }

    @Test
    @DisplayName("Should accept zero average consumption")
    void shouldAcceptZeroAverageConsumption() {
        // Given
        Validator validator = getValidator();
        AnalysisCreateDTO dto = new AnalysisCreateDTO(
                UUID.randomUUID(),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                null, null
        );

        // When
        Set<ConstraintViolation<AnalysisCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail when cost per kWh is negative")
    void shouldFailWhenCostPerKwhIsNegative() {
        // Given
        Validator validator = getValidator();
        AnalysisCreateDTO dto = new AnalysisCreateDTO(
                UUID.randomUUID(),
                new BigDecimal("350.00"),
                new BigDecimal("-1.00"),
                null, null, null
        );

        // When
        Set<ConstraintViolation<AnalysisCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("costPerKwh"));
    }

    @Test
    @DisplayName("Should accept negative comparison (decrease)")
    void shouldAcceptNegativeComparison() {
        // Given
        Validator validator = getValidator();
        AnalysisCreateDTO dto = new AnalysisCreateDTO(
                UUID.randomUUID(),
                new BigDecimal("350.00"),
                new BigDecimal("1.1014"),
                new BigDecimal("-15.5"),
                null, null
        );

        // When
        Set<ConstraintViolation<AnalysisCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail when comparison is less than -100%")
    void shouldFailWhenComparisonIsLessThanMinus100() {
        // Given
        Validator validator = getValidator();
        AnalysisCreateDTO dto = new AnalysisCreateDTO(
                UUID.randomUUID(),
                new BigDecimal("350.00"),
                new BigDecimal("1.1014"),
                new BigDecimal("-150.0"),
                null, null
        );

        // When
        Set<ConstraintViolation<AnalysisCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("comparisonPrevMonth"));
    }

    @Test
    @DisplayName("Should fail when comparison is greater than 1000%")
    void shouldFailWhenComparisonIsGreaterThan1000() {
        // Given
        Validator validator = getValidator();
        AnalysisCreateDTO dto = new AnalysisCreateDTO(
                UUID.randomUUID(),
                new BigDecimal("350.00"),
                new BigDecimal("1.1014"),
                new BigDecimal("1500.0"),
                null, null
        );

        // When
        Set<ConstraintViolation<AnalysisCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("comparisonPrevMonth"));
    }

    // ========== NORMALIZATION TESTS ==========

    @Test
    @DisplayName("Should trim savings tips")
    void shouldTrimSavingsTips() {
        // Given & When
        AnalysisCreateDTO dto = new AnalysisCreateDTO(
                UUID.randomUUID(),
                new BigDecimal("350.00"),
                new BigDecimal("1.1014"),
                new BigDecimal("5.5"),
                "  Reduce peak consumption  ",
                null
        );

        // Then
        assertThat(dto.savingsTips()).isEqualTo("Reduce peak consumption");
    }

    @Test
    @DisplayName("Should trim report PDF URL")
    void shouldTrimReportPdfUrl() {
        // Given & When
        AnalysisCreateDTO dto = new AnalysisCreateDTO(
                UUID.randomUUID(),
                null, null, null, null,
                "  /reports/analysis.pdf  "
        );

        // Then
        assertThat(dto.reportPdfUrl()).isEqualTo("/reports/analysis.pdf");
    }

    // ========== SIZE VALIDATION TESTS ==========

    @Test
    @DisplayName("Should fail when savings tips exceeds max length")
    void shouldFailWhenSavingsTipsExceedsMaxLength() {
        // Given
        Validator validator = getValidator();
        String longTips = "a".repeat(5001);
        AnalysisCreateDTO dto = new AnalysisCreateDTO(
                UUID.randomUUID(),
                new BigDecimal("350.00"),
                new BigDecimal("1.1014"),
                new BigDecimal("5.5"),
                longTips,
                null
        );

        // When
        Set<ConstraintViolation<AnalysisCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("savingsTips"));
    }

    @Test
    @DisplayName("Should fail when report PDF URL exceeds max length")
    void shouldFailWhenReportPdfUrlExceedsMaxLength() {
        // Given
        Validator validator = getValidator();
        String longUrl = "a".repeat(1001);
        AnalysisCreateDTO dto = new AnalysisCreateDTO(
                UUID.randomUUID(),
                null, null, null, null,
                longUrl
        );

        // When
        Set<ConstraintViolation<AnalysisCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("reportPdfUrl"));
    }
}

