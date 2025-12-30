package com.understand_your_electricity_bill.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ConsultantCreateDTO Tests")
class ConsultantCreateDTOTest {

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
        ConsultantCreateDTO dto = new ConsultantCreateDTO(
                "consultant@company.com",
                "StrongPass@123",
                "João Consultor",
                "+5511999999999",
                "João Consultor",
                "Energy Consulting Ltda",
                "12345678000190",
                "REG123456",
                "Av. Paulista, 1000",
                "São Paulo",
                "SP",
                "01310100",
                "https://logo.url/image.png"
        );

        // When
        Set<ConstraintViolation<ConsultantCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should create valid DTO with only required fields")
    void shouldCreateValidDtoWithOnlyRequiredFields() {
        // Given
        Validator validator = getValidator();
        ConsultantCreateDTO dto = new ConsultantCreateDTO(
                "consultant@company.com",
                "StrongPass@123",
                "João Consultor",
                null,  // phone optional
                "João Consultor",
                "Energy Consulting Ltda",
                "12345678000190",
                null,  // registrationNumber optional
                "Av. Paulista, 1000",
                null,  // city optional
                null,  // state optional
                null,  // zipCode optional
                null   // companyLogo optional
        );

        // When
        Set<ConstraintViolation<ConsultantCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    // ========== EMAIL VALIDATION TESTS ==========

    @Test
    @DisplayName("Should fail when email is blank")
    void shouldFailWhenEmailIsBlank() {
        // Given
        Validator validator = getValidator();
        ConsultantCreateDTO dto = new ConsultantCreateDTO(
                "",
                "StrongPass@123",
                "João Consultor",
                null,
                "João Consultor",
                "Energy Consulting Ltda",
                "12345678000190",
                null,
                "Av. Paulista, 1000",
                null, null, null, null
        );

        // When
        Set<ConstraintViolation<ConsultantCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("email"));
    }

    @Test
    @DisplayName("Should fail when email format is invalid")
    void shouldFailWhenEmailFormatIsInvalid() {
        // Given
        Validator validator = getValidator();
        ConsultantCreateDTO dto = new ConsultantCreateDTO(
                "invalid-email",
                "StrongPass@123",
                "João Consultor",
                null,
                "João Consultor",
                "Energy Consulting Ltda",
                "12345678000190",
                null,
                "Av. Paulista, 1000",
                null, null, null, null
        );

        // When
        Set<ConstraintViolation<ConsultantCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("email"));
    }

    // ========== PASSWORD VALIDATION TESTS ==========

    @Test
    @DisplayName("Should fail when password is blank")
    void shouldFailWhenPasswordIsBlank() {
        // Given
        Validator validator = getValidator();
        ConsultantCreateDTO dto = new ConsultantCreateDTO(
                "consultant@company.com",
                "",
                "João Consultor",
                null,
                "João Consultor",
                "Energy Consulting Ltda",
                "12345678000190",
                null,
                "Av. Paulista, 1000",
                null, null, null, null
        );

        // When
        Set<ConstraintViolation<ConsultantCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("password"));
    }

    @Test
    @DisplayName("Should fail when password is too short")
    void shouldFailWhenPasswordIsTooShort() {
        // Given
        Validator validator = getValidator();
        ConsultantCreateDTO dto = new ConsultantCreateDTO(
                "consultant@company.com",
                "Pass@12",  // Only 7 characters
                "João Consultor",
                null,
                "João Consultor",
                "Energy Consulting Ltda",
                "12345678000190",
                null,
                "Av. Paulista, 1000",
                null, null, null, null
        );

        // When
        Set<ConstraintViolation<ConsultantCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("password"));
    }

    // ========== CNPJ VALIDATION TESTS ==========

    @Test
    @DisplayName("Should fail when CNPJ is blank")
    void shouldFailWhenCnpjIsBlank() {
        // Given
        Validator validator = getValidator();
        ConsultantCreateDTO dto = new ConsultantCreateDTO(
                "consultant@company.com",
                "StrongPass@123",
                "João Consultor",
                null,
                "João Consultor",
                "Energy Consulting Ltda",
                "",
                null,
                "Av. Paulista, 1000",
                null, null, null, null
        );

        // When
        Set<ConstraintViolation<ConsultantCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("cnpj"));
    }

    @Test
    @DisplayName("Should fail when CNPJ has wrong length")
    void shouldFailWhenCnpjHasWrongLength() {
        // Given
        Validator validator = getValidator();
        ConsultantCreateDTO dto = new ConsultantCreateDTO(
                "consultant@company.com",
                "StrongPass@123",
                "João Consultor",
                null,
                "João Consultor",
                "Energy Consulting Ltda",
                "123456789",  // Only 9 digits
                null,
                "Av. Paulista, 1000",
                null, null, null, null
        );

        // When
        Set<ConstraintViolation<ConsultantCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("cnpj") &&
                        v.getMessage().contains("14 digits"));
    }

    // ========== REQUIRED FIELDS VALIDATION TESTS ==========

    @Test
    @DisplayName("Should fail when consultant name is blank")
    void shouldFailWhenConsultantNameIsBlank() {
        // Given
        Validator validator = getValidator();
        ConsultantCreateDTO dto = new ConsultantCreateDTO(
                "consultant@company.com",
                "StrongPass@123",
                "João Consultor",
                null,
                "",  // blank consultant name
                "Energy Consulting Ltda",
                "12345678000190",
                null,
                "Av. Paulista, 1000",
                null, null, null, null
        );

        // When
        Set<ConstraintViolation<ConsultantCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("consultantName"));
    }

    @Test
    @DisplayName("Should fail when company is blank")
    void shouldFailWhenCompanyIsBlank() {
        // Given
        Validator validator = getValidator();
        ConsultantCreateDTO dto = new ConsultantCreateDTO(
                "consultant@company.com",
                "StrongPass@123",
                "João Consultor",
                null,
                "João Consultor",
                "",  // blank company
                "12345678000190",
                null,
                "Av. Paulista, 1000",
                null, null, null, null
        );

        // When
        Set<ConstraintViolation<ConsultantCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("company"));
    }

    @Test
    @DisplayName("Should fail when address is blank")
    void shouldFailWhenAddressIsBlank() {
        // Given
        Validator validator = getValidator();
        ConsultantCreateDTO dto = new ConsultantCreateDTO(
                "consultant@company.com",
                "StrongPass@123",
                "João Consultor",
                null,
                "João Consultor",
                "Energy Consulting Ltda",
                "12345678000190",
                null,
                "",  // blank address
                null, null, null, null
        );

        // When
        Set<ConstraintViolation<ConsultantCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("address"));
    }

    // ========== NORMALIZATION TESTS ==========

    @Test
    @DisplayName("Should normalize email to lowercase")
    void shouldNormalizeEmailToLowercase() {
        // Given & When
        ConsultantCreateDTO dto = new ConsultantCreateDTO(
                "CONSULTANT@COMPANY.COM",
                "StrongPass@123",
                "João Consultor",
                null,
                "João Consultor",
                "Energy Consulting Ltda",
                "12345678000190",
                null,
                "Av. Paulista, 1000",
                null, null, null, null
        );

        // Then
        assertThat(dto.email()).isEqualTo("consultant@company.com");
    }

    @Test
    @DisplayName("Should remove non-digits from CNPJ")
    void shouldRemoveNonDigitsFromCnpj() {
        // Given & When
        ConsultantCreateDTO dto = new ConsultantCreateDTO(
                "consultant@company.com",
                "StrongPass@123",
                "João Consultor",
                null,
                "João Consultor",
                "Energy Consulting Ltda",
                "12.345.678/0001-90",
                null,
                "Av. Paulista, 1000",
                null, null, null, null
        );

        // Then
        assertThat(dto.cnpj()).isEqualTo("12345678000190");
    }

    @Test
    @DisplayName("Should remove dashes from ZIP code")
    void shouldRemoveDashesFromZipCode() {
        // Given & When
        ConsultantCreateDTO dto = new ConsultantCreateDTO(
                "consultant@company.com",
                "StrongPass@123",
                "João Consultor",
                null,
                "João Consultor",
                "Energy Consulting Ltda",
                "12345678000190",
                null,
                "Av. Paulista, 1000",
                null, null,
                "01310-100",
                null
        );

        // Then
        assertThat(dto.zipCode()).isEqualTo("01310100");
    }

    @Test
    @DisplayName("Should normalize state to uppercase")
    void shouldNormalizeStateToUppercase() {
        // Given & When
        ConsultantCreateDTO dto = new ConsultantCreateDTO(
                "consultant@company.com",
                "StrongPass@123",
                "João Consultor",
                null,
                "João Consultor",
                "Energy Consulting Ltda",
                "12345678000190",
                null,
                "Av. Paulista, 1000",
                "São Paulo",
                "sp",
                null, null
        );

        // Then
        assertThat(dto.state()).isEqualTo("SP");
    }

    @Test
    @DisplayName("Should trim whitespace from all string fields")
    void shouldTrimWhitespaceFromAllStringFields() {
        // Given & When
        ConsultantCreateDTO dto = new ConsultantCreateDTO(
                "  consultant@company.com  ",
                "StrongPass@123",
                "  João Consultor  ",
                "  +5511999999999  ",
                "  João Consultor  ",
                "  Energy Consulting Ltda  ",
                "12345678000190",
                "  REG123456  ",
                "  Av. Paulista, 1000  ",
                "  São Paulo  ",
                "SP",
                null,
                "  https://logo.url  "
        );

        // Then
        assertThat(dto.email()).isEqualTo("consultant@company.com");
        assertThat(dto.name()).isEqualTo("João Consultor");
        assertThat(dto.phone()).isEqualTo("+5511999999999");
        assertThat(dto.consultantName()).isEqualTo("João Consultor");
        assertThat(dto.company()).isEqualTo("Energy Consulting Ltda");
        assertThat(dto.registrationNumber()).isEqualTo("REG123456");
        assertThat(dto.address()).isEqualTo("Av. Paulista, 1000");
        assertThat(dto.city()).isEqualTo("São Paulo");
        assertThat(dto.companyLogo()).isEqualTo("https://logo.url");
    }

    // ========== SIZE VALIDATION TESTS ==========

    @Test
    @DisplayName("Should fail when consultant name exceeds max length")
    void shouldFailWhenConsultantNameExceedsMaxLength() {
        // Given
        Validator validator = getValidator();
        String longName = "A".repeat(256);
        ConsultantCreateDTO dto = new ConsultantCreateDTO(
                "consultant@company.com",
                "StrongPass@123",
                "João Consultor",
                null,
                longName,
                "Energy Consulting Ltda",
                "12345678000190",
                null,
                "Av. Paulista, 1000",
                null, null, null, null
        );

        // When
        Set<ConstraintViolation<ConsultantCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("consultantName"));
    }

    @Test
    @DisplayName("Should fail when address exceeds max length")
    void shouldFailWhenAddressExceedsMaxLength() {
        // Given
        Validator validator = getValidator();
        String longAddress = "A".repeat(501);
        ConsultantCreateDTO dto = new ConsultantCreateDTO(
                "consultant@company.com",
                "StrongPass@123",
                "João Consultor",
                null,
                "João Consultor",
                "Energy Consulting Ltda",
                "12345678000190",
                null,
                longAddress,
                null, null, null, null
        );

        // When
        Set<ConstraintViolation<ConsultantCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("address"));
    }
}

