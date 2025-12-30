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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("TariffCreateDTO Tests")
class TariffCreateDTOTest {

    private Validator getValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            return factory.getValidator();
        }
    }

    // ========== VALID DTO TESTS ==========

    @Test
    @DisplayName("Should create valid DTO with all fields")
    void shouldCreateValidDtoWithAllFields() {
        // Given
        Validator validator = getValidator();
        TariffCreateDTO dto = new TariffCreateDTO(
                LocalDate.of(2024, 1, 1),
                "RESOLUÇÃO HOMOLOGATÓRIA Nº 0.937",
                "CPFL PAULISTA",
                "12345678000190",
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31),
                "Tarifa de Aplicação",
                "A2",
                "Azul",
                "Residencial",
                "Residencial Normal",
                "APE",
                "Fora ponta",
                "kW",
                "Não se aplica",
                new BigDecimal("1.8500"),
                new BigDecimal("0.5000"),
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 1),
                "Verde",
                new BigDecimal("0.00")
        );

        // When
        Set<ConstraintViolation<TariffCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should create valid DTO with only required fields")
    void shouldCreateValidDtoWithOnlyRequiredFields() {
        // Given
        Validator validator = getValidator();
        TariffCreateDTO dto = new TariffCreateDTO(
                LocalDate.of(2024, 1, 1),
                null,  // descriptionReh optional
                "CPFL PAULISTA",
                "12345678000190",
                LocalDate.of(2024, 1, 1),
                null,  // validUntil optional
                null, null, null, null, null, null, null, null, null,
                new BigDecimal("1.8500"),
                new BigDecimal("0.5000"),
                null, null, null, null
        );

        // When
        Set<ConstraintViolation<TariffCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    // ========== REQUIRED FIELDS VALIDATION TESTS ==========

    @Test
    @DisplayName("Should fail when generation date is null")
    void shouldFailWhenGenerationDateIsNull() {
        // Given
        Validator validator = getValidator();
        TariffCreateDTO dto = new TariffCreateDTO(
                null,  // null generation date
                null,
                "CPFL PAULISTA",
                "12345678000190",
                LocalDate.of(2024, 1, 1),
                null,
                null, null, null, null, null, null, null, null, null,
                new BigDecimal("1.8500"),
                new BigDecimal("0.5000"),
                null, null, null, null
        );

        // When
        Set<ConstraintViolation<TariffCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("generationDate"));
    }

    @Test
    @DisplayName("Should fail when distributor is blank")
    void shouldFailWhenDistributorIsBlank() {
        // Given
        Validator validator = getValidator();
        TariffCreateDTO dto = new TariffCreateDTO(
                LocalDate.of(2024, 1, 1),
                null,
                "",  // blank distributor
                "12345678000190",
                LocalDate.of(2024, 1, 1),
                null,
                null, null, null, null, null, null, null, null, null,
                new BigDecimal("1.8500"),
                new BigDecimal("0.5000"),
                null, null, null, null
        );

        // When
        Set<ConstraintViolation<TariffCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("distributor"));
    }

    @Test
    @DisplayName("Should fail when CNPJ distributor is blank")
    void shouldFailWhenCnpjDistributorIsBlank() {
        // Given
        Validator validator = getValidator();
        TariffCreateDTO dto = new TariffCreateDTO(
                LocalDate.of(2024, 1, 1),
                null,
                "CPFL PAULISTA",
                "",  // blank CNPJ
                LocalDate.of(2024, 1, 1),
                null,
                null, null, null, null, null, null, null, null, null,
                new BigDecimal("1.8500"),
                new BigDecimal("0.5000"),
                null, null, null, null
        );

        // When
        Set<ConstraintViolation<TariffCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("cnpjDistributor"));
    }

    @Test
    @DisplayName("Should fail when valid from is null")
    void shouldFailWhenValidFromIsNull() {
        // Given
        Validator validator = getValidator();
        TariffCreateDTO dto = new TariffCreateDTO(
                LocalDate.of(2024, 1, 1),
                null,
                "CPFL PAULISTA",
                "12345678000190",
                null,  // null validFrom
                null,
                null, null, null, null, null, null, null, null, null,
                new BigDecimal("1.8500"),
                new BigDecimal("0.5000"),
                null, null, null, null
        );

        // When
        Set<ConstraintViolation<TariffCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("validFrom"));
    }

    @Test
    @DisplayName("Should fail when TUSD value is null")
    void shouldFailWhenTusdValueIsNull() {
        // Given
        Validator validator = getValidator();
        TariffCreateDTO dto = new TariffCreateDTO(
                LocalDate.of(2024, 1, 1),
                null,
                "CPFL PAULISTA",
                "12345678000190",
                LocalDate.of(2024, 1, 1),
                null,
                null, null, null, null, null, null, null, null, null,
                null,  // null TUSD
                new BigDecimal("0.5000"),
                null, null, null, null
        );

        // When
        Set<ConstraintViolation<TariffCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("tusdValue"));
    }

    @Test
    @DisplayName("Should fail when TE value is null")
    void shouldFailWhenTeValueIsNull() {
        // Given
        Validator validator = getValidator();
        TariffCreateDTO dto = new TariffCreateDTO(
                LocalDate.of(2024, 1, 1),
                null,
                "CPFL PAULISTA",
                "12345678000190",
                LocalDate.of(2024, 1, 1),
                null,
                null, null, null, null, null, null, null, null, null,
                new BigDecimal("1.8500"),
                null,  // null TE
                null, null, null, null
        );

        // When
        Set<ConstraintViolation<TariffCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("teValue"));
    }

    // ========== CNPJ VALIDATION TESTS ==========

    @Test
    @DisplayName("Should fail when CNPJ has wrong length")
    void shouldFailWhenCnpjHasWrongLength() {
        // Given
        Validator validator = getValidator();
        TariffCreateDTO dto = new TariffCreateDTO(
                LocalDate.of(2024, 1, 1),
                null,
                "CPFL PAULISTA",
                "123456789",  // Only 9 digits
                LocalDate.of(2024, 1, 1),
                null,
                null, null, null, null, null, null, null, null, null,
                new BigDecimal("1.8500"),
                new BigDecimal("0.5000"),
                null, null, null, null
        );

        // When
        Set<ConstraintViolation<TariffCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("cnpjDistributor") &&
                        v.getMessage().contains("14 digits"));
    }

    // ========== VALUE VALIDATION TESTS ==========

    @Test
    @DisplayName("Should fail when TUSD value is zero")
    void shouldFailWhenTusdValueIsZero() {
        // Given
        Validator validator = getValidator();
        TariffCreateDTO dto = new TariffCreateDTO(
                LocalDate.of(2024, 1, 1),
                null,
                "CPFL PAULISTA",
                "12345678000190",
                LocalDate.of(2024, 1, 1),
                null,
                null, null, null, null, null, null, null, null, null,
                BigDecimal.ZERO,  // Zero TUSD
                new BigDecimal("0.5000"),
                null, null, null, null
        );

        // When
        Set<ConstraintViolation<TariffCreateDTO>> violations = validator.validate(dto);

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
        TariffCreateDTO dto = new TariffCreateDTO(
                LocalDate.of(2024, 1, 1),
                null,
                "CPFL PAULISTA",
                "12345678000190",
                LocalDate.of(2024, 1, 1),
                null,
                null, null, null, null, null, null, null, null, null,
                new BigDecimal("1.8500"),
                BigDecimal.ZERO,  // Zero TE
                null, null, null, null
        );

        // When
        Set<ConstraintViolation<TariffCreateDTO>> violations = validator.validate(dto);

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
        TariffCreateDTO dto = new TariffCreateDTO(
                LocalDate.of(2024, 1, 1),
                null,
                "CPFL PAULISTA",
                "12345678000190",
                LocalDate.of(2024, 1, 1),
                null,
                null, null, null, null, null, null, null, null, null,
                new BigDecimal("1.8500"),
                new BigDecimal("0.5000"),
                null, null, null,
                new BigDecimal("-10.00")  // Negative flag value
        );

        // When
        Set<ConstraintViolation<TariffCreateDTO>> violations = validator.validate(dto);

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
        assertThatThrownBy(() -> new TariffCreateDTO(
                LocalDate.of(2024, 1, 1),
                null,
                "CPFL PAULISTA",
                "12345678000190",
                LocalDate.of(2024, 12, 31),
                LocalDate.of(2024, 1, 1),  // Before validFrom
                null, null, null, null, null, null, null, null, null,
                new BigDecimal("1.8500"),
                new BigDecimal("0.5000"),
                null, null, null, null
        )).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Valid until date must be after or equal to valid from date");
    }

    // ========== NORMALIZATION TESTS ==========
    // Note: Detailed normalization logic is tested in DtoValidationUtilsTest
    // This test verifies integration with the DTO

    @Test
    @DisplayName("Should remove non-digits from CNPJ")
    void shouldRemoveNonDigitsFromCnpj() {
        // Given & When
        TariffCreateDTO dto = new TariffCreateDTO(
                LocalDate.of(2024, 1, 1),
                null,
                "CPFL PAULISTA",
                "12.345.678/0001-90",  // Formatted CNPJ
                LocalDate.of(2024, 1, 1),
                null,
                null, null, null, null, null, null, null, null, null,
                new BigDecimal("1.8500"),
                new BigDecimal("0.5000"),
                null, null, null, null
        );

        // Then
        assertThat(dto.cnpjDistributor()).isEqualTo("12345678000190");
    }


    // ========== SIZE VALIDATION TESTS ==========


    @Test
    @DisplayName("Should fail when distributor exceeds max length")
    void shouldFailWhenDistributorExceedsMaxLength() {
        // Given
        Validator validator = getValidator();
        String longDistributor = "A".repeat(101);
        TariffCreateDTO dto = new TariffCreateDTO(
                LocalDate.of(2024, 1, 1),
                null,
                longDistributor,
                "12345678000190",
                LocalDate.of(2024, 1, 1),
                null,
                null, null, null, null, null, null, null, null, null,
                new BigDecimal("1.8500"),
                new BigDecimal("0.5000"),
                null, null, null, null
        );

        // When
        Set<ConstraintViolation<TariffCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("distributor"));
    }
}

