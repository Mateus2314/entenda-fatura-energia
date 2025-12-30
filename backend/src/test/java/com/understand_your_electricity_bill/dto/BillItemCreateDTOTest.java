package com.understand_your_electricity_bill.dto;

import com.understand_your_electricity_bill.model.enums.BillItemType;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("BillItemCreateDTO Tests")
class BillItemCreateDTOTest {

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
        BillItemCreateDTO dto = new BillItemCreateDTO(
                UUID.randomUUID(),
                BillItemType.OFF_PEAK_CONSUMPTION,
                null,
                null,
                null,
                new BigDecimal("269.50")
        );

        // When
        Set<ConstraintViolation<BillItemCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should create valid DTO with all fields")
    void shouldCreateValidDtoWithAllFields() {
        // Given
        Validator validator = getValidator();
        BillItemCreateDTO dto = new BillItemCreateDTO(
                UUID.randomUUID(),
                BillItemType.OFF_PEAK_CONSUMPTION,
                "Consumo de energia fora ponta",
                new BigDecimal("350.00"),
                new BigDecimal("0.7700"),
                new BigDecimal("269.50")
        );

        // When
        Set<ConstraintViolation<BillItemCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    // ========== REQUIRED FIELDS VALIDATION TESTS ==========

    @Test
    @DisplayName("Should fail when bill ID is null")
    void shouldFailWhenBillIdIsNull() {
        // Given
        Validator validator = getValidator();
        BillItemCreateDTO dto = new BillItemCreateDTO(
                null,
                BillItemType.OFF_PEAK_CONSUMPTION,
                "Description",
                null,
                null,
                new BigDecimal("269.50")
        );

        // When
        Set<ConstraintViolation<BillItemCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("billId"));
    }

    @Test
    @DisplayName("Should fail when item type is null")
    void shouldFailWhenItemTypeIsNull() {
        // Given
        Validator validator = getValidator();
        BillItemCreateDTO dto = new BillItemCreateDTO(
                UUID.randomUUID(),
                null,
                "Description",
                null,
                null,
                new BigDecimal("269.50")
        );

        // When
        Set<ConstraintViolation<BillItemCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("itemType"));
    }

    @Test
    @DisplayName("Should fail when amount is null")
    void shouldFailWhenAmountIsNull() {
        // Given
        Validator validator = getValidator();
        BillItemCreateDTO dto = new BillItemCreateDTO(
                UUID.randomUUID(),
                BillItemType.OFF_PEAK_CONSUMPTION,
                "Description",
                null,
                null,
                null
        );

        // When
        Set<ConstraintViolation<BillItemCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("amount"));
    }

    // ========== VALUE VALIDATION TESTS ==========

    @Test
    @DisplayName("Should fail when amount is negative")
    void shouldFailWhenAmountIsNegative() {
        // Given
        Validator validator = getValidator();
        BillItemCreateDTO dto = new BillItemCreateDTO(
                UUID.randomUUID(),
                BillItemType.OFF_PEAK_CONSUMPTION,
                "Description",
                null,
                null,
                new BigDecimal("-100.00")
        );

        // When
        Set<ConstraintViolation<BillItemCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("amount"));
    }

    @Test
    @DisplayName("Should accept zero amount")
    void shouldAcceptZeroAmount() {
        // Given
        Validator validator = getValidator();
        BillItemCreateDTO dto = new BillItemCreateDTO(
                UUID.randomUUID(),
                BillItemType.DISCOUNT,
                "Desconto",
                null,
                null,
                BigDecimal.ZERO
        );

        // When
        Set<ConstraintViolation<BillItemCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail when quantity is negative")
    void shouldFailWhenQuantityIsNegative() {
        // Given
        Validator validator = getValidator();
        BillItemCreateDTO dto = new BillItemCreateDTO(
                UUID.randomUUID(),
                BillItemType.OFF_PEAK_CONSUMPTION,
                "Description",
                new BigDecimal("-10.00"),
                null,  // No unit price to avoid calculation validation
                new BigDecimal("100.00")
        );

        // When
        Set<ConstraintViolation<BillItemCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("quantity"));
    }

    // ========== BUSINESS RULES VALIDATION TESTS ==========

    @Test
    @DisplayName("Should fail when amount does not match quantity × unit price")
    void shouldFailWhenAmountDoesNotMatchCalculation() {
        // Given & When & Then
        assertThatThrownBy(() -> new BillItemCreateDTO(
                UUID.randomUUID(),
                BillItemType.OFF_PEAK_CONSUMPTION,
                "Description",
                new BigDecimal("350.00"),  // quantity
                new BigDecimal("0.7700"),  // unit price
                new BigDecimal("100.00")   // amount (wrong: should be ~269.50)
        )).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Amount must equal quantity × unit price");
    }

    @Test
    @DisplayName("Should accept amount with small rounding difference")
    void shouldAcceptAmountWithSmallRoundingDifference() {
        // Given
        Validator validator = getValidator();
        // 350 × 0.7700 = 269.50, but we'll use 269.51 (0.01 difference - acceptable)
        BillItemCreateDTO dto = new BillItemCreateDTO(
                UUID.randomUUID(),
                BillItemType.OFF_PEAK_CONSUMPTION,
                "Description",
                new BigDecimal("350.00"),
                new BigDecimal("0.7700"),
                new BigDecimal("269.51")  // 0.01 difference is acceptable
        );

        // When
        Set<ConstraintViolation<BillItemCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should not validate calculation when values are null")
    void shouldNotValidateCalculationWhenValuesAreNull() {
        // Given
        Validator validator = getValidator();
        BillItemCreateDTO dto = new BillItemCreateDTO(
                UUID.randomUUID(),
                BillItemType.PUBLIC_LIGHTING,
                "Fixed charge",
                null,  // No quantity
                null,  // No unit price
                new BigDecimal("35.00")
        );

        // When
        Set<ConstraintViolation<BillItemCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    // ========== NORMALIZATION TESTS ==========

    @Test
    @DisplayName("Should trim description")
    void shouldTrimDescription() {
        // Given & When
        BillItemCreateDTO dto = new BillItemCreateDTO(
                UUID.randomUUID(),
                BillItemType.OFF_PEAK_CONSUMPTION,
                "  Energy consumption  ",
                null,
                null,
                new BigDecimal("269.50")
        );

        // Then
        assertThat(dto.description()).isEqualTo("Energy consumption");
    }

    // ========== SIZE VALIDATION TESTS ==========

    @Test
    @DisplayName("Should fail when description exceeds max length")
    void shouldFailWhenDescriptionExceedsMaxLength() {
        // Given
        Validator validator = getValidator();
        String longDescription = "a".repeat(256);
        BillItemCreateDTO dto = new BillItemCreateDTO(
                UUID.randomUUID(),
                BillItemType.OFF_PEAK_CONSUMPTION,
                longDescription,
                null,
                null,
                new BigDecimal("269.50")
        );

        // When
        Set<ConstraintViolation<BillItemCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("description"));
    }
}

