package com.understand_your_electricity_bill.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ElectricityBillUpdateDTO Tests")
class ElectricityBillUpdateDTOTest {

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
        UUID billId = UUID.randomUUID();
        ElectricityBillUpdateDTO dto = new ElectricityBillUpdateDTO(
                billId, null, null, null, null, null, null, null, null
        );

        // When
        Set<ConstraintViolation<ElectricityBillUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should create valid DTO with partial update")
    void shouldCreateValidDtoWithPartialUpdate() {
        // Given
        Validator validator = getValidator();
        UUID billId = UUID.randomUUID();
        ElectricityBillUpdateDTO dto = new ElectricityBillUpdateDTO(
                billId,
                UUID.randomUUID(), // Update consultant
                null,
                null,
                new BigDecimal("425.80"),
                null,
                null, null, null
        );

        // When
        Set<ConstraintViolation<ElectricityBillUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    // ========== ID VALIDATION TESTS ==========

    @Test
    @DisplayName("Should fail when ID is null")
    void shouldFailWhenIdIsNull() {
        // Given
        Validator validator = getValidator();
        ElectricityBillUpdateDTO dto = new ElectricityBillUpdateDTO(
                null, null, null, null, null, null, null, null, null
        );

        // When
        Set<ConstraintViolation<ElectricityBillUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("id"));
    }

    // ========== VALUE VALIDATION TESTS ==========

    @Test
    @DisplayName("Should fail when total amount is negative")
    void shouldFailWhenTotalAmountIsNegative() {
        // Given
        Validator validator = getValidator();
        UUID billId = UUID.randomUUID();
        ElectricityBillUpdateDTO dto = new ElectricityBillUpdateDTO(
                billId, null, null, null,
                new BigDecimal("-100.00"),
                null, null, null, null
        );

        // When
        Set<ConstraintViolation<ElectricityBillUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("totalAmount"));
    }

    @Test
    @DisplayName("Should accept zero values")
    void shouldAcceptZeroValues() {
        // Given
        Validator validator = getValidator();
        UUID billId = UUID.randomUUID();
        ElectricityBillUpdateDTO dto = new ElectricityBillUpdateDTO(
                billId, null, null, null,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                null, null, null
        );

        // When
        Set<ConstraintViolation<ElectricityBillUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    // ========== BUSINESS RULES VALIDATION TESTS ==========

    @Test
    @DisplayName("Should fail when due date is before reference month")
    void shouldFailWhenDueDateIsBeforeReferenceMonth() {
        // Given & When & Then
        UUID billId = UUID.randomUUID();
        assertThatThrownBy(() -> new ElectricityBillUpdateDTO(
                billId, null,
                LocalDate.of(2024, 1, 20),
                LocalDate.of(2024, 1, 10), // Before reference
                null, null, null, null, null
        )).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Due date must be after reference month");
    }

    // ========== HASUPDATES METHOD TESTS ==========

    @Test
    @DisplayName("Should return false when no fields are updated")
    void shouldReturnFalseWhenNoFieldsAreUpdated() {
        // Given
        UUID billId = UUID.randomUUID();
        ElectricityBillUpdateDTO dto = new ElectricityBillUpdateDTO(
                billId, null, null, null, null, null, null, null, null
        );

        // When & Then
        assertThat(dto.hasUpdates()).isFalse();
    }

    @Test
    @DisplayName("Should return true when consultant is updated")
    void shouldReturnTrueWhenConsultantIsUpdated() {
        // Given
        UUID billId = UUID.randomUUID();
        ElectricityBillUpdateDTO dto = new ElectricityBillUpdateDTO(
                billId, UUID.randomUUID(), null, null, null, null, null, null, null
        );

        // When & Then
        assertThat(dto.hasUpdates()).isTrue();
    }

    @Test
    @DisplayName("Should return true when total amount is updated")
    void shouldReturnTrueWhenTotalAmountIsUpdated() {
        // Given
        UUID billId = UUID.randomUUID();
        ElectricityBillUpdateDTO dto = new ElectricityBillUpdateDTO(
                billId, null, null, null, new BigDecimal("425.80"), null, null, null, null
        );

        // When & Then
        assertThat(dto.hasUpdates()).isTrue();
    }

    @Test
    @DisplayName("Should return true when multiple fields are updated")
    void shouldReturnTrueWhenMultipleFieldsAreUpdated() {
        // Given
        UUID billId = UUID.randomUUID();
        ElectricityBillUpdateDTO dto = new ElectricityBillUpdateDTO(
                billId,
                UUID.randomUUID(),
                LocalDate.of(2024, 2, 1),
                LocalDate.of(2024, 2, 20),
                new BigDecimal("425.80"),
                new BigDecimal("410.00"),
                "/new/path/invoice.pdf",
                "9876543210",
                "NEW-INV-002"
        );

        // When & Then
        assertThat(dto.hasUpdates()).isTrue();
    }

    // ========== NORMALIZATION TESTS ==========
    // Note: Normalization logic is tested in DtoValidationUtilsTest

    @Test
    @DisplayName("Should trim PDF URL")
    void shouldTrimPdfUrl() {
        // Given & When
        UUID billId = UUID.randomUUID();
        ElectricityBillUpdateDTO dto = new ElectricityBillUpdateDTO(
                billId, null, null, null, null, null,
                "  /new/invoice.pdf  ",
                null, null
        );

        // Then
        assertThat(dto.pdfUrl()).isEqualTo("/new/invoice.pdf");
    }
}

