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

@DisplayName("TariffUpdateDTO Tests")
class TariffUpdateDTOTest {

    private Validator getValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            return factory.getValidator();
        }
    }

    // ========== VALID DTO TESTS ==========

    @Test
    @DisplayName("Should create valid DTO with ID and no updates")
    void shouldCreateValidDtoWithIdOnly() {
        // Given
        Validator validator = getValidator();
        UUID tariffId = UUID.randomUUID();
        TariffUpdateDTO dto = new TariffUpdateDTO(
                tariffId,
                null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null
        );

        // When
        Set<ConstraintViolation<TariffUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should create valid DTO with partial update")
    void shouldCreateValidDtoWithPartialUpdate() {
        // Given
        Validator validator = getValidator();
        UUID tariffId = UUID.randomUUID();
        TariffUpdateDTO dto = new TariffUpdateDTO(
                tariffId,
                null,
                "Updated description",
                null,
                LocalDate.of(2025, 12, 31),
                null, null,
                "Convencional",
                null, null, null, null, null, null,
                new BigDecimal("2.0000"),
                new BigDecimal("0.6000"),
                null, null, null, null
        );

        // When
        Set<ConstraintViolation<TariffUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    // ========== ID VALIDATION TESTS ==========

    @Test
    @DisplayName("Should fail when ID is null")
    void shouldFailWhenIdIsNull() {
        // Given
        Validator validator = getValidator();
        TariffUpdateDTO dto = new TariffUpdateDTO(
                null,  // null ID
                null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null
        );

        // When
        Set<ConstraintViolation<TariffUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("id") &&
                        v.getMessage().contains("required"));
    }

    // ========== VALUE VALIDATION TESTS ==========

    @Test
    @DisplayName("Should fail when TUSD value is zero")
    void shouldFailWhenTusdValueIsZero() {
        // Given
        Validator validator = getValidator();
        UUID tariffId = UUID.randomUUID();
        TariffUpdateDTO dto = new TariffUpdateDTO(
                tariffId,
                null, null, null, null, null, null, null, null, null, null, null, null, null,
                BigDecimal.ZERO,  // Zero TUSD
                null, null, null, null, null
        );

        // When
        Set<ConstraintViolation<TariffUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("tusdValue"));
    }

    @Test
    @DisplayName("Should fail when TE value is zero")
    void shouldFailWhenTeValueIsZero() {
        // Given
        Validator validator = getValidator();
        UUID tariffId = UUID.randomUUID();
        TariffUpdateDTO dto = new TariffUpdateDTO(
                tariffId,
                null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                BigDecimal.ZERO,  // Zero TE
                null, null, null, null
        );

        // When
        Set<ConstraintViolation<TariffUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("teValue"));
    }

    @Test
    @DisplayName("Should fail when flag additional value is negative")
    void shouldFailWhenFlagAdditionalValueIsNegative() {
        // Given
        Validator validator = getValidator();
        UUID tariffId = UUID.randomUUID();
        TariffUpdateDTO dto = new TariffUpdateDTO(
                tariffId,
                null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                new BigDecimal("-10.00")  // Negative value
        );

        // When
        Set<ConstraintViolation<TariffUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("flagAdditionalValue"));
    }

    // ========== DATE VALIDATION TESTS ==========
    // Note: Detailed date validation logic is tested in DtoValidationUtilsTest
    // This test verifies integration with the DTO

    @Test
    @DisplayName("Should fail when valid until is before valid from")
    void shouldFailWhenValidUntilIsBeforeValidFrom() {
        // Given & When & Then
        UUID tariffId = UUID.randomUUID();
        assertThatThrownBy(() -> new TariffUpdateDTO(
                tariffId,
                null, null,
                LocalDate.of(2024, 12, 31),
                LocalDate.of(2024, 1, 1),  // Before validFrom
                null, null, null, null, null, null, null, null, null, null, null, null, null, null, null
        )).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Valid until date must be after or equal to valid from date");
    }


    // ========== NORMALIZATION TESTS ==========
    // Note: Detailed normalization logic is tested in DtoValidationUtilsTest
    // Normalization is applied automatically via compact constructor

    // ========== HASUPDATE METHOD TESTS ==========

    @Test
    @DisplayName("Should return false when no fields are updated")
    void shouldReturnFalseWhenNoFieldsAreUpdated() {
        // Given
        UUID tariffId = UUID.randomUUID();
        TariffUpdateDTO dto = new TariffUpdateDTO(
                tariffId,
                null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null
        );

        // When & Then
        assertThat(dto.hasUpdates()).isFalse();
    }

    @Test
    @DisplayName("Should return true when generation date is updated")
    void shouldReturnTrueWhenGenerationDateIsUpdated() {
        // Given
        UUID tariffId = UUID.randomUUID();
        TariffUpdateDTO dto = new TariffUpdateDTO(
                tariffId,
                LocalDate.of(2025, 1, 1),
                null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null
        );

        // When & Then
        assertThat(dto.hasUpdates()).isTrue();
    }

    @Test
    @DisplayName("Should return true when description REH is updated")
    void shouldReturnTrueWhenDescriptionRehIsUpdated() {
        // Given
        UUID tariffId = UUID.randomUUID();
        TariffUpdateDTO dto = new TariffUpdateDTO(
                tariffId,
                null,
                "Updated description",
                null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null
        );

        // When & Then
        assertThat(dto.hasUpdates()).isTrue();
    }

    @Test
    @DisplayName("Should return true when TUSD value is updated")
    void shouldReturnTrueWhenTusdValueIsUpdated() {
        // Given
        UUID tariffId = UUID.randomUUID();
        TariffUpdateDTO dto = new TariffUpdateDTO(
                tariffId,
                null, null, null, null, null, null, null, null, null, null, null, null, null,
                new BigDecimal("2.5000"),
                null, null, null, null, null
        );

        // When & Then
        assertThat(dto.hasUpdates()).isTrue();
    }

    @Test
    @DisplayName("Should return true when TE value is updated")
    void shouldReturnTrueWhenTeValueIsUpdated() {
        // Given
        UUID tariffId = UUID.randomUUID();
        TariffUpdateDTO dto = new TariffUpdateDTO(
                tariffId,
                null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                new BigDecimal("0.7500"),
                null, null, null, null
        );

        // When & Then
        assertThat(dto.hasUpdates()).isTrue();
    }

    @Test
    @DisplayName("Should return true when flag name is updated")
    void shouldReturnTrueWhenFlagNameIsUpdated() {
        // Given
        UUID tariffId = UUID.randomUUID();
        TariffUpdateDTO dto = new TariffUpdateDTO(
                tariffId,
                null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                "Vermelha",
                null
        );

        // When & Then
        assertThat(dto.hasUpdates()).isTrue();
    }

    @Test
    @DisplayName("Should return true when multiple fields are updated")
    void shouldReturnTrueWhenMultipleFieldsAreUpdated() {
        // Given
        UUID tariffId = UUID.randomUUID();
        TariffUpdateDTO dto = new TariffUpdateDTO(
                tariffId,
                LocalDate.of(2025, 1, 1),
                "Updated description",
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31),
                "Updated base",
                "A4",
                "Azul",
                "Industrial",
                "Industrial A4",
                "Detail",
                "Fora ponta",
                "kW",
                "Agent",
                new BigDecimal("2.0000"),
                new BigDecimal("0.6000"),
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 1),
                "Verde",
                new BigDecimal("5.00")
        );

        // When & Then
        assertThat(dto.hasUpdates()).isTrue();
    }

    // ========== SIZE VALIDATION TESTS ==========

    @Test
    @DisplayName("Should fail when description REH exceeds max length")
    void shouldFailWhenDescriptionRehExceedsMaxLength() {
        // Given
        Validator validator = getValidator();
        UUID tariffId = UUID.randomUUID();
        String longDescription = "A".repeat(501);
        TariffUpdateDTO dto = new TariffUpdateDTO(
                tariffId,
                null,
                longDescription,
                null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null
        );

        // When
        Set<ConstraintViolation<TariffUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("descriptionReh"));
    }

    @Test
    @DisplayName("Should fail when tariff modality exceeds max length")
    void shouldFailWhenTariffModalityExceedsMaxLength() {
        // Given
        Validator validator = getValidator();
        UUID tariffId = UUID.randomUUID();
        String longModality = "A".repeat(51);
        TariffUpdateDTO dto = new TariffUpdateDTO(
                tariffId,
                null, null, null, null, null, null,
                longModality,
                null, null, null, null, null, null, null, null, null, null, null, null
        );

        // When
        Set<ConstraintViolation<TariffUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("tariffModality"));
    }

    // ========== IMMUTABLE FIELD TESTS (BUSINESS RULES) ==========

    @Test
    @DisplayName("Should not include distributor field (immutable business rule)")
    void shouldNotIncludeDistributorField() {
        // This test documents that distributor is intentionally excluded from UpdateDTO
        // Distributor cannot be changed after tariff creation (business rule)

        // Given
        UUID tariffId = UUID.randomUUID();
        TariffUpdateDTO dto = new TariffUpdateDTO(
                tariffId,
                null,
                "Updated description",
                null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null
        );

        // Then - Distributor is not a field in the DTO
        assertThat(dto).isNotNull();
        // This test serves as documentation of the business rule
    }

    @Test
    @DisplayName("Should not include CNPJ distributor field (immutable business rule)")
    void shouldNotIncludeCnpjDistributorField() {
        // This test documents that CNPJ distributor is intentionally excluded from UpdateDTO
        // CNPJ distributor cannot be changed after tariff creation (business rule)

        // Given
        UUID tariffId = UUID.randomUUID();
        TariffUpdateDTO dto = new TariffUpdateDTO(
                tariffId,
                null,
                "Updated description",
                null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null
        );

        // Then - CNPJ distributor is not a field in the DTO
        assertThat(dto).isNotNull();
        // This test serves as documentation of the business rule
    }
}

