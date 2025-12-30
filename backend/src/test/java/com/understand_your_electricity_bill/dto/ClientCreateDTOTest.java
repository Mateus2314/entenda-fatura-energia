package com.understand_your_electricity_bill.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ClientCreateDTO Tests")
class ClientCreateDTOTest {

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
        ClientCreateDTO dto = new ClientCreateDTO(
                "joao.silva@email.com",
                "SenhaForte@123",
                "João da Silva",
                "+5511999999999",
                "12345678901",
                "Rua das Flores, 123",
                "São Paulo",
                "SP",
                "01234567"
        );

        // When
        Set<ConstraintViolation<ClientCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should create valid DTO with only required fields")
    void shouldCreateValidDtoWithOnlyRequiredFields() {
        // Given
        Validator validator = getValidator();
        ClientCreateDTO dto = new ClientCreateDTO(
                "joao.silva@email.com",
                "SenhaForte@123",
                "João da Silva",
                null,  // phone is optional
                "12345678901",
                "Rua das Flores, 123",
                null,  // city is optional
                null,  // state is optional
                null   // zipCode is optional
        );

        // When
        Set<ConstraintViolation<ClientCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    // ========== EMAIL VALIDATION TESTS ==========

    @Test
    @DisplayName("Should fail when email format is invalid")
    void shouldFailWhenEmailFormatIsInvalid() {
        // Given
        Validator validator = getValidator();
        ClientCreateDTO dto = new ClientCreateDTO(
                "invalid-email",
                "SenhaForte@123",
                "João da Silva",
                null,
                "12345678901",
                "Rua das Flores, 123",
                null, null, null
        );

        // When
        Set<ConstraintViolation<ClientCreateDTO>> violations = validator.validate(dto);

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
        ClientCreateDTO dto = new ClientCreateDTO(
                "joao.silva@email.com",
                "",
                "João da Silva",
                null,
                "12345678901",
                "Rua das Flores, 123",
                null, null, null
        );

        // When
        Set<ConstraintViolation<ClientCreateDTO>> violations = validator.validate(dto);

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
        ClientCreateDTO dto = new ClientCreateDTO(
                "joao.silva@email.com",
                "Abc@123",  // Only 7 characters
                "João da Silva",
                null,
                "12345678901",
                "Rua das Flores, 123",
                null, null, null
        );

        // When
        Set<ConstraintViolation<ClientCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("password"));
    }

    @Test
    @DisplayName("Should fail when password lacks uppercase")
    void shouldFailWhenPasswordLacksUppercase() {
        // Given
        Validator validator = getValidator();
        ClientCreateDTO dto = new ClientCreateDTO(
                "joao.silva@email.com",
                "senhafraca@123",  // No uppercase
                "João da Silva",
                null,
                "12345678901",
                "Rua das Flores, 123",
                null, null, null
        );

        // When
        Set<ConstraintViolation<ClientCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("password") &&
                        v.getMessage().contains("uppercase"));
    }

    @Test
    @DisplayName("Should fail when password lacks special character")
    void shouldFailWhenPasswordLacksSpecialCharacter() {
        // Given
        Validator validator = getValidator();
        ClientCreateDTO dto = new ClientCreateDTO(
                "joao.silva@email.com",
                "SenhaFraca123",  // No special char
                "João da Silva",
                null,
                "12345678901",
                "Rua das Flores, 123",
                null, null, null
        );

        // When
        Set<ConstraintViolation<ClientCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("password") &&
                        v.getMessage().contains("special character"));
    }

    // ========== EMAIL VALIDATION TESTS ==========

    @Test
    @DisplayName("Should fail when email is blank")
    void shouldFailWhenEmailIsBlank() {
        // Given
        Validator validator = getValidator();
        ClientCreateDTO dto = new ClientCreateDTO(
                "joao.silva@email.com",
                "SenhaForte@123",
                "João da Silva",
                null,
                "",
                "Rua das Flores, 123",
                null, null, null
        );

        // When
        Set<ConstraintViolation<ClientCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("cpf"));
    }

    @Test
    @DisplayName("Should fail when CPF has wrong length")
    void shouldFailWhenCpfHasWrongLength() {
        // Given
        Validator validator = getValidator();
        ClientCreateDTO dto = new ClientCreateDTO(
                "joao.silva@email.com",
                "SenhaForte@123",
                "João da Silva",
                null,
                "123456789",  // Only 9 digits
                "Rua das Flores, 123",
                null, null, null
        );

        // When
        Set<ConstraintViolation<ClientCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("cpf") &&
                        v.getMessage().contains("11 digits"));
    }


    // ========== NORMALIZATION TESTS ==========

    @Test
    @DisplayName("Should normalize email to lowercase")
    void shouldNormalizeEmailToLowercase() {
        // Given & When
        ClientCreateDTO dto = new ClientCreateDTO(
                "JOAO.SILVA@EMAIL.COM",
                "SenhaForte@123",
                "João da Silva",
                null,
                "12345678901",
                "Rua das Flores, 123",
                null, null, null
        );

        // Then
        assertThat(dto.email()).isEqualTo("joao.silva@email.com");
    }

    @Test
    @DisplayName("Should remove non-digits from CPF")
    void shouldRemoveNonDigitsFromCpf() {
        // Given & When
        ClientCreateDTO dto = new ClientCreateDTO(
                "joao.silva@email.com",
                "SenhaForte@123",
                "João da Silva",
                null,
                "123.456.789-01",
                "Rua das Flores, 123",
                null, null, null
        );

        // Then
        assertThat(dto.cpf()).isEqualTo("12345678901");
    }

    @Test
    @DisplayName("Should remove dashes from ZIP code")
    void shouldRemoveDashesFromZipCode() {
        // Given & When
        ClientCreateDTO dto = new ClientCreateDTO(
                "joao.silva@email.com",
                "SenhaForte@123",
                "João da Silva",
                null,
                "12345678901",
                "Rua das Flores, 123",
                null, null,
                "01234-567"
        );

        // Then
        assertThat(dto.zipCode()).isEqualTo("01234567");
    }

    @Test
    @DisplayName("Should normalize state to uppercase")
    void shouldNormalizeStateToUppercase() {
        // Given & When
        ClientCreateDTO dto = new ClientCreateDTO(
                "joao.silva@email.com",
                "SenhaForte@123",
                "João da Silva",
                null,
                "12345678901",
                "Rua das Flores, 123",
                "São Paulo",
                "sp",
                null
        );

        // Then
        assertThat(dto.state()).isEqualTo("SP");
    }

    @Test
    @DisplayName("Should trim whitespace from all string fields")
    void shouldTrimWhitespaceFromAllStringFields() {
        // Given & When
        ClientCreateDTO dto = new ClientCreateDTO(
                "  joao.silva@email.com  ",
                "SenhaForte@123",
                "  João da Silva  ",
                "  +5511999999999  ",
                "12345678901",
                "  Rua das Flores, 123  ",
                "  São Paulo  ",
                "SP",
                null
        );

        // Then
        assertThat(dto.email()).isEqualTo("joao.silva@email.com");
        assertThat(dto.name()).isEqualTo("João da Silva");
        assertThat(dto.phone()).isEqualTo("+5511999999999");
        assertThat(dto.address()).isEqualTo("Rua das Flores, 123");
        assertThat(dto.city()).isEqualTo("São Paulo");
    }

    // ========== SIZE VALIDATION TESTS ==========

    @Test
    @DisplayName("Should fail when name exceeds max length")
    void shouldFailWhenNameExceedsMaxLength() {
        // Given
        Validator validator = getValidator();
        String longName = "A".repeat(256); // 256 characters, max is 255
        ClientCreateDTO dto = new ClientCreateDTO(
                "joao.silva@email.com",
                "SenhaForte@123",
                longName,
                null,
                "12345678901",
                "Rua das Flores, 123",
                null, null, null
        );

        // When
        Set<ConstraintViolation<ClientCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("name"));
    }

    @Test
    @DisplayName("Should fail when address exceeds max length")
    void shouldFailWhenAddressExceedsMaxLength() {
        // Given
        Validator validator = getValidator();
        String longAddress = "A".repeat(501); // 501 characters, max is 500
        ClientCreateDTO dto = new ClientCreateDTO(
                "joao.silva@email.com",
                "SenhaForte@123",
                "João da Silva",
                null,
                "12345678901",
                longAddress,
                null, null, null
        );

        // When
        Set<ConstraintViolation<ClientCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("address"));
    }
}

