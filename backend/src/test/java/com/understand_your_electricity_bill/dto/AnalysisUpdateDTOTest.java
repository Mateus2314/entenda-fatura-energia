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

@DisplayName("AnalysisUpdateDTO Tests")
class AnalysisUpdateDTOTest {

    private Validator getValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            return factory.getValidator();
        }
    }

    // ========== VALID DTO TESTS ==========

    @Test
    @DisplayName("Should create valid DTO with ID only")
    void shouldCreateValidDtoWithIdOnly() {
        // Given
        Validator validator = getValidator();
        UUID analysisId = UUID.randomUUID();
        AnalysisUpdateDTO dto = new AnalysisUpdateDTO(
                analysisId, null, null, null, null, null
        );

        // When
        Set<ConstraintViolation<AnalysisUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should create valid DTO with partial update")
    void shouldCreateValidDtoWithPartialUpdate() {
        // Given
        Validator validator = getValidator();
        UUID analysisId = UUID.randomUUID();
        AnalysisUpdateDTO dto = new AnalysisUpdateDTO(
                analysisId,
                new BigDecimal("400.00"),
                null,
                new BigDecimal("10.5"),
                null, null
        );

        // When
        Set<ConstraintViolation<AnalysisUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    // ========== ID VALIDATION TESTS ==========

    @Test
    @DisplayName("Should fail when ID is null")
    void shouldFailWhenIdIsNull() {
        // Given
        Validator validator = getValidator();
        AnalysisUpdateDTO dto = new AnalysisUpdateDTO(
                null, null, null, null, null, null
        );

        // When
        Set<ConstraintViolation<AnalysisUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("id"));
    }

    // ========== VALUE VALIDATION TESTS ==========

    @Test
    @DisplayName("Should fail when average consumption is negative")
    void shouldFailWhenAverageConsumptionIsNegative() {
        // Given
        Validator validator = getValidator();
        UUID analysisId = UUID.randomUUID();
        AnalysisUpdateDTO dto = new AnalysisUpdateDTO(
                analysisId,
                new BigDecimal("-100.00"),
                null, null, null, null
        );

        // When
        Set<ConstraintViolation<AnalysisUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("averageConsumption"));
    }

    @Test
    @DisplayName("Should accept zero values")
    void shouldAcceptZeroValues() {
        // Given
        Validator validator = getValidator();
        UUID analysisId = UUID.randomUUID();
        AnalysisUpdateDTO dto = new AnalysisUpdateDTO(
                analysisId,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                null, null
        );

        // When
        Set<ConstraintViolation<AnalysisUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should accept negative comparison within range")
    void shouldAcceptNegativeComparisonWithinRange() {
        // Given
        Validator validator = getValidator();
        UUID analysisId = UUID.randomUUID();
        AnalysisUpdateDTO dto = new AnalysisUpdateDTO(
                analysisId, null, null,
                new BigDecimal("-50.0"),
                null, null
        );

        // When
        Set<ConstraintViolation<AnalysisUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail when comparison exceeds limits")
    void shouldFailWhenComparisonExceedsLimits() {
        // Given
        Validator validator = getValidator();
        UUID analysisId = UUID.randomUUID();
        AnalysisUpdateDTO dto = new AnalysisUpdateDTO(
                analysisId, null, null,
                new BigDecimal("2000.0"),
                null, null
        );

        // When
        Set<ConstraintViolation<AnalysisUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("comparisonPrevMonth"));
    }

    // ========== HASUPDATES METHOD TESTS ==========

    @Test
    @DisplayName("Should return false when no fields are updated")
    void shouldReturnFalseWhenNoFieldsAreUpdated() {
        // Given
        UUID analysisId = UUID.randomUUID();
        AnalysisUpdateDTO dto = new AnalysisUpdateDTO(
                analysisId, null, null, null, null, null
        );

        // When & Then
        assertThat(dto.hasUpdates()).isFalse();
    }

    @Test
    @DisplayName("Should return true when average consumption is updated")
    void shouldReturnTrueWhenAverageConsumptionIsUpdated() {
        // Given
        UUID analysisId = UUID.randomUUID();
        AnalysisUpdateDTO dto = new AnalysisUpdateDTO(
                analysisId,
                new BigDecimal("400.00"),
                null, null, null, null
        );

        // When & Then
        assertThat(dto.hasUpdates()).isTrue();
    }

    @Test
    @DisplayName("Should return true when savings tips is updated")
    void shouldReturnTrueWhenSavingsTipsIsUpdated() {
        // Given
        UUID analysisId = UUID.randomUUID();
        AnalysisUpdateDTO dto = new AnalysisUpdateDTO(
                analysisId, null, null, null,
                "New savings tips",
                null
        );

        // When & Then
        assertThat(dto.hasUpdates()).isTrue();
    }

    @Test
    @DisplayName("Should return true when multiple fields are updated")
    void shouldReturnTrueWhenMultipleFieldsAreUpdated() {
        // Given
        UUID analysisId = UUID.randomUUID();
        AnalysisUpdateDTO dto = new AnalysisUpdateDTO(
                analysisId,
                new BigDecimal("400.00"),
                new BigDecimal("1.2500"),
                new BigDecimal("15.5"),
                "Updated tips",
                "/new/report.pdf"
        );

        // When & Then
        assertThat(dto.hasUpdates()).isTrue();
    }

    // ========== NORMALIZATION TESTS ==========

    @Test
    @DisplayName("Should trim savings tips")
    void shouldTrimSavingsTips() {
        // Given & When
        UUID analysisId = UUID.randomUUID();
        AnalysisUpdateDTO dto = new AnalysisUpdateDTO(
                analysisId, null, null, null,
                "  Updated tips  ",
                null
        );

        // Then
        assertThat(dto.savingsTips()).isEqualTo("Updated tips");
    }

    @Test
    @DisplayName("Should trim report PDF URL")
    void shouldTrimReportPdfUrl() {
        // Given & When
        UUID analysisId = UUID.randomUUID();
        AnalysisUpdateDTO dto = new AnalysisUpdateDTO(
                analysisId, null, null, null, null,
                "  /new/report.pdf  "
        );

        // Then
        assertThat(dto.reportPdfUrl()).isEqualTo("/new/report.pdf");
    }
}

