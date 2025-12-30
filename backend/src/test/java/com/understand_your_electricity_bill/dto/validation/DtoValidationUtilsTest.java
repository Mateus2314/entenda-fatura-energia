package com.understand_your_electricity_bill.dto.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("DtoValidationUtils Tests")
class DtoValidationUtilsTest {

    // ========== VALIDATEDATERANGE TESTS ==========

    @Test
    @DisplayName("Should pass when validUntil is after validFrom")
    void shouldPassWhenValidUntilIsAfterValidFrom() {
        // Given
        LocalDate validFrom = LocalDate.of(2024, 1, 1);
        LocalDate validUntil = LocalDate.of(2024, 12, 31);

        // When & Then - Should not throw exception
        DtoValidationUtils.validateDateRange(validFrom, validUntil);
    }

    @Test
    @DisplayName("Should pass when validUntil equals validFrom")
    void shouldPassWhenValidUntilEqualsValidFrom() {
        // Given
        LocalDate sameDate = LocalDate.of(2024, 1, 1);

        // When & Then - Should not throw exception
        DtoValidationUtils.validateDateRange(sameDate, sameDate);
    }

    @Test
    @DisplayName("Should throw exception when validUntil is before validFrom")
    void shouldThrowExceptionWhenValidUntilIsBeforeValidFrom() {
        // Given
        LocalDate validFrom = LocalDate.of(2024, 12, 31);
        LocalDate validUntil = LocalDate.of(2024, 1, 1);

        // When & Then
        assertThatThrownBy(() -> DtoValidationUtils.validateDateRange(validFrom, validUntil))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Valid until date must be after or equal to valid from date");
    }

    @Test
    @DisplayName("Should pass when validFrom is null")
    void shouldPassWhenValidFromIsNull() {
        // Given
        LocalDate validUntil = LocalDate.of(2024, 12, 31);

        // When & Then - Should not throw exception
        DtoValidationUtils.validateDateRange(null, validUntil);
    }

    @Test
    @DisplayName("Should pass when validUntil is null")
    void shouldPassWhenValidUntilIsNull() {
        // Given
        LocalDate validFrom = LocalDate.of(2024, 1, 1);

        // When & Then - Should not throw exception
        DtoValidationUtils.validateDateRange(validFrom, null);
    }

    @Test
    @DisplayName("Should pass when both dates are null")
    void shouldPassWhenBothDatesAreNull() {
        // When & Then - Should not throw exception
        DtoValidationUtils.validateDateRange(null, null);
    }

    // ========== TRIMIFNOTNULL TESTS ==========

    @Test
    @DisplayName("Should trim whitespace from string")
    void shouldTrimWhitespaceFromString() {
        // Given
        String input = "  test value  ";

        // When
        String result = DtoValidationUtils.trimIfNotNull(input);

        // Then
        assertThat(result).isEqualTo("test value");
    }


    @Test
    @DisplayName("Should return empty string when input is only whitespace")
    void shouldReturnEmptyStringWhenInputIsOnlyWhitespace() {
        // Given
        String input = "   ";

        // When
        String result = DtoValidationUtils.trimIfNotNull(input);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return same string when no whitespace")
    void shouldReturnSameStringWhenNoWhitespace() {
        // Given
        String input = "test";

        // When
        String result = DtoValidationUtils.trimIfNotNull(input);

        // Then
        assertThat(result).isEqualTo("test");
    }

    // ========== KEEPONLYDIGITS TESTS ==========

    @Test
    @DisplayName("Should remove all non-digit characters")
    void shouldRemoveAllNonDigitCharacters() {
        // Given
        String input = "12.345.678/0001-90";

        // When
        String result = DtoValidationUtils.keepOnlyDigits(input);

        // Then
        assertThat(result).isEqualTo("12345678000190");
    }


    @Test
    @DisplayName("Should return empty string when no digits present")
    void shouldReturnEmptyStringWhenNoDigitsPresent() {
        // Given
        String input = "abc-def/xyz";

        // When
        String result = DtoValidationUtils.keepOnlyDigits(input);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return same string when only digits present")
    void shouldReturnSameStringWhenOnlyDigitsPresent() {
        // Given
        String input = "1234567890";

        // When
        String result = DtoValidationUtils.keepOnlyDigits(input);

        // Then
        assertThat(result).isEqualTo("1234567890");
    }

    @Test
    @DisplayName("Should handle CPF format")
    void shouldHandleCpfFormat() {
        // Given
        String input = "123.456.789-00";

        // When
        String result = DtoValidationUtils.keepOnlyDigits(input);

        // Then
        assertThat(result).isEqualTo("12345678900");
    }

    @Test
    @DisplayName("Should handle phone format")
    void shouldHandlePhoneFormat() {
        // Given
        String input = "+55 (11) 99999-9999";

        // When
        String result = DtoValidationUtils.keepOnlyDigits(input);

        // Then
        assertThat(result).isEqualTo("5511999999999");
    }

    @Test
    @DisplayName("Should handle ZIP code format")
    void shouldHandleZipCodeFormat() {
        // Given
        String input = "01310-100";

        // When
        String result = DtoValidationUtils.keepOnlyDigits(input);

        // Then
        assertThat(result).isEqualTo("01310100");
    }

    // ========== HASUPDATES TESTS ==========

    /**
     * Simple test record to test hasUpdates() method
     */
    record TestUpdateDTO(java.util.UUID id, String field1, String field2, Integer field3) {}

    @Test
    @DisplayName("Should return true when at least one non-id field is not null")
    void shouldReturnTrueWhenFieldsAreNotNull() {
        // Given
        TestUpdateDTO dto = new TestUpdateDTO(java.util.UUID.randomUUID(), "value", null, null);

        // When
        boolean result = DtoValidationUtils.hasUpdates(dto);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return false when all non-id fields are null")
    void shouldReturnFalseWhenAllFieldsAreNull() {
        // Given
        TestUpdateDTO dto = new TestUpdateDTO(java.util.UUID.randomUUID(), null, null, null);

        // When
        boolean result = DtoValidationUtils.hasUpdates(dto);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return true when multiple fields are not null")
    void shouldReturnTrueWhenMultipleFieldsAreNotNull() {
        // Given
        TestUpdateDTO dto = new TestUpdateDTO(java.util.UUID.randomUUID(), "value1", "value2", 42);

        // When
        boolean result = DtoValidationUtils.hasUpdates(dto);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should exclude id field from hasUpdates check")
    void shouldExcludeIdFieldFromCheck() {
        // Given - Only ID is not null
        TestUpdateDTO dto = new TestUpdateDTO(java.util.UUID.randomUUID(), null, null, null);

        // When
        boolean result = DtoValidationUtils.hasUpdates(dto);

        // Then - Should return false because ID is excluded
        assertThat(result).isFalse();
    }


    // ========== CONSTRUCTOR TEST ==========

    @Test
    @DisplayName("Should not be instantiable")
    void shouldNotBeInstantiable() {
        // When & Then
        assertThatThrownBy(() -> {
            var constructor = DtoValidationUtils.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        })
        .hasCauseInstanceOf(UnsupportedOperationException.class)
        .cause()
        .hasMessageContaining("This is a utility class and cannot be instantiated");
    }
}

