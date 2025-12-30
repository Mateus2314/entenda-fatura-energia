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

@DisplayName("ElectricityBillCreateDTO Tests")
class ElectricityBillCreateDTOTest {

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
        ElectricityBillCreateDTO dto = new ElectricityBillCreateDTO(
                UUID.randomUUID(),
                null,
                UUID.randomUUID(),
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 20),
                new BigDecimal("385.50"),
                new BigDecimal("350.00"),
                null, null, null
        );

        // When
        Set<ConstraintViolation<ElectricityBillCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should create valid DTO with all fields")
    void shouldCreateValidDtoWithAllFields() {
        // Given
        Validator validator = getValidator();
        ElectricityBillCreateDTO dto = new ElectricityBillCreateDTO(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 20),
                new BigDecimal("385.50"),
                new BigDecimal("350.00"),
                "/bills/2024/01/invoice.pdf",
                "1234567890",
                "CPFL-202401-001"
        );

        // When
        Set<ConstraintViolation<ElectricityBillCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    // ========== REQUIRED FIELDS VALIDATION TESTS ==========

    @Test
    @DisplayName("Should fail when client ID is null")
    void shouldFailWhenClientIdIsNull() {
        // Given
        Validator validator = getValidator();
        ElectricityBillCreateDTO dto = new ElectricityBillCreateDTO(
                null,
                null,
                UUID.randomUUID(),
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 20),
                new BigDecimal("385.50"),
                new BigDecimal("350.00"),
                null, null, null
        );

        // When
        Set<ConstraintViolation<ElectricityBillCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("clientId"));
    }

    @Test
    @DisplayName("Should fail when tariff ID is null")
    void shouldFailWhenTariffIdIsNull() {
        // Given
        Validator validator = getValidator();
        ElectricityBillCreateDTO dto = new ElectricityBillCreateDTO(
                UUID.randomUUID(),
                null,
                null,
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 20),
                new BigDecimal("385.50"),
                new BigDecimal("350.00"),
                null, null, null
        );

        // When
        Set<ConstraintViolation<ElectricityBillCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("tariffId"));
    }

    @Test
    @DisplayName("Should fail when reference month is null")
    void shouldFailWhenReferenceMonthIsNull() {
        // Given
        Validator validator = getValidator();
        ElectricityBillCreateDTO dto = new ElectricityBillCreateDTO(
                UUID.randomUUID(),
                null,
                UUID.randomUUID(),
                null,
                LocalDate.of(2024, 1, 20),
                new BigDecimal("385.50"),
                new BigDecimal("350.00"),
                null, null, null
        );

        // When
        Set<ConstraintViolation<ElectricityBillCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("referenceMonth"));
    }

    // ========== VALUE VALIDATION TESTS ==========

    @Test
    @DisplayName("Should fail when total amount is negative")
    void shouldFailWhenTotalAmountIsNegative() {
        // Given
        Validator validator = getValidator();
        ElectricityBillCreateDTO dto = new ElectricityBillCreateDTO(
                UUID.randomUUID(),
                null,
                UUID.randomUUID(),
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 20),
                new BigDecimal("-100.00"),
                new BigDecimal("350.00"),
                null, null, null
        );

        // When
        Set<ConstraintViolation<ElectricityBillCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("totalAmount"));
    }

    @Test
    @DisplayName("Should accept zero consumption")
    void shouldAcceptZeroConsumption() {
        // Given
        Validator validator = getValidator();
        ElectricityBillCreateDTO dto = new ElectricityBillCreateDTO(
                UUID.randomUUID(),
                null,
                UUID.randomUUID(),
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 20),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                null, null, null
        );

        // When
        Set<ConstraintViolation<ElectricityBillCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    // ========== BUSINESS RULES VALIDATION TESTS ==========

    @Test
    @DisplayName("Should fail when due date is before reference month")
    void shouldFailWhenDueDateIsBeforeReferenceMonth() {
        // Given & When & Then
        assertThatThrownBy(() -> new ElectricityBillCreateDTO(
                UUID.randomUUID(),
                null,
                UUID.randomUUID(),
                LocalDate.of(2024, 1, 20),
                LocalDate.of(2024, 1, 10), // Before reference
                new BigDecimal("385.50"),
                new BigDecimal("350.00"),
                null, null, null
        )).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Due date must be after reference month");
    }

    @Test
    @DisplayName("Should fail when due date equals reference month")
    void shouldFailWhenDueDateEqualsReferenceMonth() {
        // Given & When & Then
        LocalDate sameDate = LocalDate.of(2024, 1, 15);
        assertThatThrownBy(() -> new ElectricityBillCreateDTO(
                UUID.randomUUID(),
                null,
                UUID.randomUUID(),
                sameDate,
                sameDate,
                new BigDecimal("385.50"),
                new BigDecimal("350.00"),
                null, null, null
        )).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Due date must be after reference month");
    }

    // ========== NORMALIZATION TESTS ==========
    // Note: Normalization logic is tested in DtoValidationUtilsTest

    @Test
    @DisplayName("Should trim PDF URL")
    void shouldTrimPdfUrl() {
        // Given & When
        ElectricityBillCreateDTO dto = new ElectricityBillCreateDTO(
                UUID.randomUUID(),
                null,
                UUID.randomUUID(),
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 20),
                new BigDecimal("385.50"),
                new BigDecimal("350.00"),
                "  /bills/invoice.pdf  ",
                null, null
        );

        // Then
        assertThat(dto.pdfUrl()).isEqualTo("/bills/invoice.pdf");
    }

    @Test
    @DisplayName("Should trim installation number")
    void shouldTrimInstallationNumber() {
        // Given & When
        ElectricityBillCreateDTO dto = new ElectricityBillCreateDTO(
                UUID.randomUUID(),
                null,
                UUID.randomUUID(),
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 20),
                new BigDecimal("385.50"),
                new BigDecimal("350.00"),
                null,
                "  1234567890  ",
                null
        );

        // Then
        assertThat(dto.installationNumber()).isEqualTo("1234567890");
    }

    // ========== SIZE VALIDATION TESTS ==========

    @Test
    @DisplayName("Should fail when PDF URL exceeds max length")
    void shouldFailWhenPdfUrlExceedsMaxLength() {
        // Given
        Validator validator = getValidator();
        String longUrl = "a".repeat(1001);
        ElectricityBillCreateDTO dto = new ElectricityBillCreateDTO(
                UUID.randomUUID(),
                null,
                UUID.randomUUID(),
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 20),
                new BigDecimal("385.50"),
                new BigDecimal("350.00"),
                longUrl,
                null, null
        );

        // When
        Set<ConstraintViolation<ElectricityBillCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("pdfUrl"));
    }
}

