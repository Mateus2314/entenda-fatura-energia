package com.understand_your_electricity_bill.dto;

import com.understand_your_electricity_bill.model.enums.UserStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ClientUpdateDTO Tests")
class ClientUpdateDTOTest {

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
        UUID clientId = UUID.randomUUID();
        ClientUpdateDTO dto = new ClientUpdateDTO(
                clientId,
                null, null, null, null, null, null, null, null
        );

        // When
        Set<ConstraintViolation<ClientUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should create valid DTO with partial update")
    void shouldCreateValidDtoWithPartialUpdate() {
        // Given
        Validator validator = getValidator();
        UUID clientId = UUID.randomUUID();
        ClientUpdateDTO dto = new ClientUpdateDTO(
                clientId,
                null,
                "New Name",
                "+5511988888888",
                null, null, null, null,
                UserStatus.ACTIVE
        );

        // When
        Set<ConstraintViolation<ClientUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    // ========== ID VALIDATION TESTS ==========

    @Test
    @DisplayName("Should fail when ID is null")
    void shouldFailWhenIdIsNull() {
        // Given
        Validator validator = getValidator();
        ClientUpdateDTO dto = new ClientUpdateDTO(
                null,
                "new.email@test.com",
                null, null, null, null, null, null, null
        );

        // When
        Set<ConstraintViolation<ClientUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("id") &&
                        v.getMessage().contains("required"));
    }

    // ========== EMAIL VALIDATION TESTS ==========

    @Test
    @DisplayName("Should fail when email format is invalid")
    void shouldFailWhenEmailFormatIsInvalid() {
        // Given
        Validator validator = getValidator();
        UUID clientId = UUID.randomUUID();
        ClientUpdateDTO dto = new ClientUpdateDTO(
                clientId,
                "invalid-email",
                null, null, null, null, null, null, null
        );

        // When
        Set<ConstraintViolation<ClientUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("email"));
    }

    @Test
    @DisplayName("Should accept null email for partial update")
    void shouldAcceptNullEmailForPartialUpdate() {
        // Given
        Validator validator = getValidator();
        UUID clientId = UUID.randomUUID();
        ClientUpdateDTO dto = new ClientUpdateDTO(
                clientId,
                null,  // Email not being updated
                "New Name",
                null, null, null, null, null, null
        );

        // When
        Set<ConstraintViolation<ClientUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    // ========== STATE VALIDATION TESTS ==========

    @Test
    @DisplayName("Should fail when state format is invalid")
    void shouldFailWhenStateFormatIsInvalid() {
        // Given
        Validator validator = getValidator();
        UUID clientId = UUID.randomUUID();
        ClientUpdateDTO dto = new ClientUpdateDTO(
                clientId,
                null, null, null, null, null,
                "SP1",  // Invalid format (3 chars)
                null, null
        );

        // When
        Set<ConstraintViolation<ClientUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("state"));
    }

    // ========== NORMALIZATION TESTS ==========

    @Test
    @DisplayName("Should normalize email to lowercase")
    void shouldNormalizeEmailToLowercase() {
        // Given & When
        UUID clientId = UUID.randomUUID();
        ClientUpdateDTO dto = new ClientUpdateDTO(
                clientId,
                "NEW.EMAIL@TEST.COM",
                null, null, null, null, null, null, null
        );

        // Then
        assertThat(dto.email()).isEqualTo("new.email@test.com");
    }

    @Test
    @DisplayName("Should normalize state to uppercase")
    void shouldNormalizeStateToUppercase() {
        // Given & When
        UUID clientId = UUID.randomUUID();
        ClientUpdateDTO dto = new ClientUpdateDTO(
                clientId,
                null, null, null, null, null,
                "rj",
                null, null
        );

        // Then
        assertThat(dto.state()).isEqualTo("RJ");
    }

    @Test
    @DisplayName("Should remove dashes from ZIP code")
    void shouldRemoveDashesFromZipCode() {
        // Given & When
        UUID clientId = UUID.randomUUID();
        ClientUpdateDTO dto = new ClientUpdateDTO(
                clientId,
                null, null, null, null, null, null,
                "20000-000",
                null
        );

        // Then
        assertThat(dto.zipCode()).isEqualTo("20000000");
    }

    @Test
    @DisplayName("Should trim all string fields")
    void shouldTrimAllStringFields() {
        // Given & When
        UUID clientId = UUID.randomUUID();
        ClientUpdateDTO dto = new ClientUpdateDTO(
                clientId,
                "  new.email@test.com  ",
                "  New Name  ",
                "  +5511999999999  ",
                "  New Address  ",
                "  New City  ",
                "RJ",
                "20000000",
                null
        );

        // Then
        assertThat(dto.email()).isEqualTo("new.email@test.com");
        assertThat(dto.name()).isEqualTo("New Name");
        assertThat(dto.phone()).isEqualTo("+5511999999999");
        assertThat(dto.address()).isEqualTo("New Address");
        assertThat(dto.city()).isEqualTo("New City");
    }

    // ========== HASUPDATE METHOD TESTS ==========

    @Test
    @DisplayName("Should return false when no fields are updated")
    void shouldReturnFalseWhenNoFieldsAreUpdated() {
        // Given
        UUID clientId = UUID.randomUUID();
        ClientUpdateDTO dto = new ClientUpdateDTO(
                clientId,
                null, null, null, null, null, null, null, null
        );

        // When & Then
        assertThat(dto.hasUpdates()).isFalse();
    }

    @Test
    @DisplayName("Should return true when email is updated")
    void shouldReturnTrueWhenEmailIsUpdated() {
        // Given
        UUID clientId = UUID.randomUUID();
        ClientUpdateDTO dto = new ClientUpdateDTO(
                clientId,
                "new.email@test.com",
                null, null, null, null, null, null, null
        );

        // When & Then
        assertThat(dto.hasUpdates()).isTrue();
    }

    @Test
    @DisplayName("Should return true when name is updated")
    void shouldReturnTrueWhenNameIsUpdated() {
        // Given
        UUID clientId = UUID.randomUUID();
        ClientUpdateDTO dto = new ClientUpdateDTO(
                clientId,
                null,
                "New Name",
                null, null, null, null, null, null
        );

        // When & Then
        assertThat(dto.hasUpdates()).isTrue();
    }

    @Test
    @DisplayName("Should return true when status is updated")
    void shouldReturnTrueWhenStatusIsUpdated() {
        // Given
        UUID clientId = UUID.randomUUID();
        ClientUpdateDTO dto = new ClientUpdateDTO(
                clientId,
                null, null, null, null, null, null, null,
                UserStatus.SUSPENDED
        );

        // When & Then
        assertThat(dto.hasUpdates()).isTrue();
    }

    @Test
    @DisplayName("Should return true when multiple fields are updated")
    void shouldReturnTrueWhenMultipleFieldsAreUpdated() {
        // Given
        UUID clientId = UUID.randomUUID();
        ClientUpdateDTO dto = new ClientUpdateDTO(
                clientId,
                "new.email@test.com",
                "New Name",
                "+5511988888888",
                "New Address",
                "New City",
                "RJ",
                "20000000",
                UserStatus.ACTIVE
        );

        // When & Then
        assertThat(dto.hasUpdates()).isTrue();
    }

    // ========== SIZE VALIDATION TESTS ==========

    @Test
    @DisplayName("Should fail when name exceeds max length")
    void shouldFailWhenNameExceedsMaxLength() {
        // Given
        Validator validator = getValidator();
        UUID clientId = UUID.randomUUID();
        String longName = "A".repeat(256);
        ClientUpdateDTO dto = new ClientUpdateDTO(
                clientId,
                null,
                longName,
                null, null, null, null, null, null
        );

        // When
        Set<ConstraintViolation<ClientUpdateDTO>> violations = validator.validate(dto);

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
        UUID clientId = UUID.randomUUID();
        String longAddress = "A".repeat(501);
        ClientUpdateDTO dto = new ClientUpdateDTO(
                clientId,
                null, null, null,
                longAddress,
                null, null, null, null
        );

        // When
        Set<ConstraintViolation<ClientUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("address"));
    }

    // ========== IMMUTABLE FIELD TESTS (BUSINESS RULES) ==========

    @Test
    @DisplayName("Should not include CPF field (immutable business rule)")
    void shouldNotIncludeCpfField() {
        // This test documents that CPF is intentionally excluded from UpdateDTO
        // CPF cannot be changed after client creation (business rule)

        // Given
        UUID clientId = UUID.randomUUID();
        ClientUpdateDTO dto = new ClientUpdateDTO(
                clientId,
                "new.email@test.com",
                "New Name",
                null, null, null, null, null, null
        );

        // Then - CPF is not a field in the DTO
        assertThat(dto).isNotNull();
        // This test serves as documentation of the business rule
    }
}

