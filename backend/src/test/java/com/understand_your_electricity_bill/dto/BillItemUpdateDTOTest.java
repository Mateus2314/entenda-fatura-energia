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

@DisplayName("BillItemUpdateDTO Tests")
class BillItemUpdateDTOTest {

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
        UUID itemId = UUID.randomUUID();
        BillItemUpdateDTO dto = new BillItemUpdateDTO(
                itemId, null, null, null, null, null
        );

        // When
        Set<ConstraintViolation<BillItemUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should create valid DTO with partial update")
    void shouldCreateValidDtoWithPartialUpdate() {
        // Given
        Validator validator = getValidator();
        UUID itemId = UUID.randomUUID();
        BillItemUpdateDTO dto = new BillItemUpdateDTO(
                itemId,
                BillItemType.PEAK_CONSUMPTION,
                "Updated description",
                null,
                null,
                null
        );

        // When
        Set<ConstraintViolation<BillItemUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    // ========== ID VALIDATION TESTS ==========

    @Test
    @DisplayName("Should fail when ID is null")
    void shouldFailWhenIdIsNull() {
        // Given
        Validator validator = getValidator();
        BillItemUpdateDTO dto = new BillItemUpdateDTO(
                null, null, null, null, null, null
        );

        // When
        Set<ConstraintViolation<BillItemUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("id"));
    }

    // ========== VALUE VALIDATION TESTS ==========

    @Test
    @DisplayName("Should fail when amount is negative")
    void shouldFailWhenAmountIsNegative() {
        // Given
        Validator validator = getValidator();
        UUID itemId = UUID.randomUUID();
        BillItemUpdateDTO dto = new BillItemUpdateDTO(
                itemId, null, null, null, null,
                new BigDecimal("-100.00")
        );

        // When
        Set<ConstraintViolation<BillItemUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("amount"));
    }

    @Test
    @DisplayName("Should accept zero values")
    void shouldAcceptZeroValues() {
        // Given
        Validator validator = getValidator();
        UUID itemId = UUID.randomUUID();
        BillItemUpdateDTO dto = new BillItemUpdateDTO(
                itemId, null, null,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );

        // When
        Set<ConstraintViolation<BillItemUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    // ========== BUSINESS RULES VALIDATION TESTS ==========

    @Test
    @DisplayName("Should fail when amount does not match quantity × unit price")
    void shouldFailWhenAmountDoesNotMatchCalculation() {
        // Given & When & Then
        UUID itemId = UUID.randomUUID();
        assertThatThrownBy(() -> new BillItemUpdateDTO(
                itemId,
                null,
                null,
                new BigDecimal("350.00"),  // quantity
                new BigDecimal("0.7700"),  // unit price
                new BigDecimal("100.00")   // amount (wrong)
        )).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Amount must equal quantity × unit price");
    }

    @Test
    @DisplayName("Should not validate when only some values are updated")
    void shouldNotValidateWhenOnlySomeValuesAreUpdated() {
        // Given
        Validator validator = getValidator();
        UUID itemId = UUID.randomUUID();
        // Only updating amount, not quantity/unitPrice
        BillItemUpdateDTO dto = new BillItemUpdateDTO(
                itemId, null, null, null, null,
                new BigDecimal("300.00")
        );

        // When
        Set<ConstraintViolation<BillItemUpdateDTO>> violations = validator.validate(dto);

        // Then - Should not throw exception because not all three values are present
        assertThat(violations).isEmpty();
    }

    // ========== HASUPDATES METHOD TESTS ==========

    @Test
    @DisplayName("Should return false when no fields are updated")
    void shouldReturnFalseWhenNoFieldsAreUpdated() {
        // Given
        UUID itemId = UUID.randomUUID();
        BillItemUpdateDTO dto = new BillItemUpdateDTO(
                itemId, null, null, null, null, null
        );

        // When & Then
        assertThat(dto.hasUpdates()).isFalse();
    }

    @Test
    @DisplayName("Should return true when item type is updated")
    void shouldReturnTrueWhenItemTypeIsUpdated() {
        // Given
        UUID itemId = UUID.randomUUID();
        BillItemUpdateDTO dto = new BillItemUpdateDTO(
                itemId, BillItemType.DISCOUNT, null, null, null, null
        );

        // When & Then
        assertThat(dto.hasUpdates()).isTrue();
    }

    @Test
    @DisplayName("Should return true when description is updated")
    void shouldReturnTrueWhenDescriptionIsUpdated() {
        // Given
        UUID itemId = UUID.randomUUID();
        BillItemUpdateDTO dto = new BillItemUpdateDTO(
                itemId, null, "New description", null, null, null
        );

        // When & Then
        assertThat(dto.hasUpdates()).isTrue();
    }

    @Test
    @DisplayName("Should return true when multiple fields are updated")
    void shouldReturnTrueWhenMultipleFieldsAreUpdated() {
        // Given
        UUID itemId = UUID.randomUUID();
        BillItemUpdateDTO dto = new BillItemUpdateDTO(
                itemId,
                BillItemType.PEAK_CONSUMPTION,
                "Updated consumption",
                new BigDecimal("410.00"),
                new BigDecimal("0.8500"),
                new BigDecimal("348.50")
        );

        // When & Then
        assertThat(dto.hasUpdates()).isTrue();
    }

    // ========== NORMALIZATION TESTS ==========

    @Test
    @DisplayName("Should trim description")
    void shouldTrimDescription() {
        // Given & When
        UUID itemId = UUID.randomUUID();
        BillItemUpdateDTO dto = new BillItemUpdateDTO(
                itemId, null,
                "  Updated description  ",
                null, null, null
        );

        // Then
        assertThat(dto.description()).isEqualTo("Updated description");
    }
}

