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

@DisplayName("ConsultantUpdateDTO Tests")
class ConsultantUpdateDTOTest {

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
        UUID consultantId = UUID.randomUUID();
        ConsultantUpdateDTO dto = new ConsultantUpdateDTO(
                consultantId,
                null, null, null, null, null, null, null, null, null, null, null, null
        );

        // When
        Set<ConstraintViolation<ConsultantUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should create valid DTO with partial update")
    void shouldCreateValidDtoWithPartialUpdate() {
        // Given
        Validator validator = getValidator();
        UUID consultantId = UUID.randomUUID();
        ConsultantUpdateDTO dto = new ConsultantUpdateDTO(
                consultantId,
                null,
                "New Name",
                "+5511988888888",
                null,
                "New Company Name",
                null, null, null, null, null, null,
                UserStatus.ACTIVE
        );

        // When
        Set<ConstraintViolation<ConsultantUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    // ========== ID VALIDATION TESTS ==========

    @Test
    @DisplayName("Should fail when ID is null")
    void shouldFailWhenIdIsNull() {
        // Given
        Validator validator = getValidator();
        ConsultantUpdateDTO dto = new ConsultantUpdateDTO(
                null,
                "new.email@test.com",
                null, null, null, null, null, null, null, null, null, null, null
        );

        // When
        Set<ConstraintViolation<ConsultantUpdateDTO>> violations = validator.validate(dto);

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
        UUID consultantId = UUID.randomUUID();
        ConsultantUpdateDTO dto = new ConsultantUpdateDTO(
                consultantId,
                "invalid-email",
                null, null, null, null, null, null, null, null, null, null, null
        );

        // When
        Set<ConstraintViolation<ConsultantUpdateDTO>> violations = validator.validate(dto);

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
        UUID consultantId = UUID.randomUUID();
        ConsultantUpdateDTO dto = new ConsultantUpdateDTO(
                consultantId,
                null,  // Email not being updated
                "New Name",
                null, null, null, null, null, null, null, null, null, null
        );

        // When
        Set<ConstraintViolation<ConsultantUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    // ========== STATE VALIDATION TESTS ==========

    @Test
    @DisplayName("Should fail when state format is invalid")
    void shouldFailWhenStateFormatIsInvalid() {
        // Given
        Validator validator = getValidator();
        UUID consultantId = UUID.randomUUID();
        ConsultantUpdateDTO dto = new ConsultantUpdateDTO(
                consultantId,
                null, null, null, null, null, null, null, null,
                "SP1",  // Invalid format (3 chars)
                null, null, null
        );

        // When
        Set<ConstraintViolation<ConsultantUpdateDTO>> violations = validator.validate(dto);

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
        UUID consultantId = UUID.randomUUID();
        ConsultantUpdateDTO dto = new ConsultantUpdateDTO(
                consultantId,
                "NEW.EMAIL@COMPANY.COM",
                null, null, null, null, null, null, null, null, null, null, null
        );

        // Then
        assertThat(dto.email()).isEqualTo("new.email@company.com");
    }

    @Test
    @DisplayName("Should normalize state to uppercase")
    void shouldNormalizeStateToUppercase() {
        // Given & When
        UUID consultantId = UUID.randomUUID();
        ConsultantUpdateDTO dto = new ConsultantUpdateDTO(
                consultantId,
                null, null, null, null, null, null, null, null,
                "rj",
                null, null, null
        );

        // Then
        assertThat(dto.state()).isEqualTo("RJ");
    }

    @Test
    @DisplayName("Should remove dashes from ZIP code")
    void shouldRemoveDashesFromZipCode() {
        // Given & When
        UUID consultantId = UUID.randomUUID();
        ConsultantUpdateDTO dto = new ConsultantUpdateDTO(
                consultantId,
                null, null, null, null, null, null, null, null, null,
                "20000-000",
                null, null
        );

        // Then
        assertThat(dto.zipCode()).isEqualTo("20000000");
    }

    @Test
    @DisplayName("Should trim all string fields")
    void shouldTrimAllStringFields() {
        // Given & When
        UUID consultantId = UUID.randomUUID();
        ConsultantUpdateDTO dto = new ConsultantUpdateDTO(
                consultantId,
                "  new.email@company.com  ",
                "  New Name  ",
                "  +5511999999999  ",
                "  New Consultant Name  ",
                "  New Company  ",
                "  REG999  ",
                "  New Address  ",
                "  New City  ",
                "RJ",
                "20000000",
                "  https://newlogo.url  ",
                null
        );

        // Then
        assertThat(dto.email()).isEqualTo("new.email@company.com");
        assertThat(dto.name()).isEqualTo("New Name");
        assertThat(dto.phone()).isEqualTo("+5511999999999");
        assertThat(dto.consultantName()).isEqualTo("New Consultant Name");
        assertThat(dto.company()).isEqualTo("New Company");
        assertThat(dto.registrationNumber()).isEqualTo("REG999");
        assertThat(dto.address()).isEqualTo("New Address");
        assertThat(dto.city()).isEqualTo("New City");
        assertThat(dto.companyLogo()).isEqualTo("https://newlogo.url");
    }

    // ========== HASUPDATE METHOD TESTS ==========

    @Test
    @DisplayName("Should return false when no fields are updated")
    void shouldReturnFalseWhenNoFieldsAreUpdated() {
        // Given
        UUID consultantId = UUID.randomUUID();
        ConsultantUpdateDTO dto = new ConsultantUpdateDTO(
                consultantId,
                null, null, null, null, null, null, null, null, null, null, null, null
        );

        // When & Then
        assertThat(dto.hasUpdates()).isFalse();
    }

    @Test
    @DisplayName("Should return true when email is updated")
    void shouldReturnTrueWhenEmailIsUpdated() {
        // Given
        UUID consultantId = UUID.randomUUID();
        ConsultantUpdateDTO dto = new ConsultantUpdateDTO(
                consultantId,
                "new.email@company.com",
                null, null, null, null, null, null, null, null, null, null, null
        );

        // When & Then
        assertThat(dto.hasUpdates()).isTrue();
    }

    @Test
    @DisplayName("Should return true when consultant name is updated")
    void shouldReturnTrueWhenConsultantNameIsUpdated() {
        // Given
        UUID consultantId = UUID.randomUUID();
        ConsultantUpdateDTO dto = new ConsultantUpdateDTO(
                consultantId,
                null, null, null,
                "New Consultant Name",
                null, null, null, null, null, null, null, null
        );

        // When & Then
        assertThat(dto.hasUpdates()).isTrue();
    }

    @Test
    @DisplayName("Should return true when company logo is updated")
    void shouldReturnTrueWhenCompanyLogoIsUpdated() {
        // Given
        UUID consultantId = UUID.randomUUID();
        ConsultantUpdateDTO dto = new ConsultantUpdateDTO(
                consultantId,
                null, null, null, null, null, null, null, null, null, null,
                "https://newlogo.url",
                null
        );

        // When & Then
        assertThat(dto.hasUpdates()).isTrue();
    }

    @Test
    @DisplayName("Should return true when status is updated")
    void shouldReturnTrueWhenStatusIsUpdated() {
        // Given
        UUID consultantId = UUID.randomUUID();
        ConsultantUpdateDTO dto = new ConsultantUpdateDTO(
                consultantId,
                null, null, null, null, null, null, null, null, null, null, null,
                UserStatus.SUSPENDED
        );

        // When & Then
        assertThat(dto.hasUpdates()).isTrue();
    }

    @Test
    @DisplayName("Should return true when multiple fields are updated")
    void shouldReturnTrueWhenMultipleFieldsAreUpdated() {
        // Given
        UUID consultantId = UUID.randomUUID();
        ConsultantUpdateDTO dto = new ConsultantUpdateDTO(
                consultantId,
                "new.email@company.com",
                "New Name",
                "+5511988888888",
                "New Consultant Name",
                "New Company",
                "REG999",
                "New Address",
                "New City",
                "RJ",
                "20000000",
                "https://newlogo.url",
                UserStatus.ACTIVE
        );

        // When & Then
        assertThat(dto.hasUpdates()).isTrue();
    }

    // ========== SIZE VALIDATION TESTS ==========

    @Test
    @DisplayName("Should fail when consultant name exceeds max length")
    void shouldFailWhenConsultantNameExceedsMaxLength() {
        // Given
        Validator validator = getValidator();
        UUID consultantId = UUID.randomUUID();
        String longName = "A".repeat(256);
        ConsultantUpdateDTO dto = new ConsultantUpdateDTO(
                consultantId,
                null, null, null,
                longName,
                null, null, null, null, null, null, null, null
        );

        // When
        Set<ConstraintViolation<ConsultantUpdateDTO>> violations = validator.validate(dto);

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
        UUID consultantId = UUID.randomUUID();
        String longAddress = "A".repeat(501);
        ConsultantUpdateDTO dto = new ConsultantUpdateDTO(
                consultantId,
                null, null, null, null, null, null,
                longAddress,
                null, null, null, null, null
        );

        // When
        Set<ConstraintViolation<ConsultantUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("address"));
    }

    // ========== IMMUTABLE FIELD TESTS (BUSINESS RULES) ==========

    @Test
    @DisplayName("Should not include CNPJ field (immutable business rule)")
    void shouldNotIncludeCnpjField() {
        // This test documents that CNPJ is intentionally excluded from UpdateDTO
        // CNPJ cannot be changed after consultant creation (business rule)

        // Given
        UUID consultantId = UUID.randomUUID();
        ConsultantUpdateDTO dto = new ConsultantUpdateDTO(
                consultantId,
                "new.email@company.com",
                "New Name",
                null, null, null, null, null, null, null, null, null, null
        );

        // Then - CNPJ is not a field in the DTO
        assertThat(dto).isNotNull();
        // This test serves as documentation of the business rule
    }
}

