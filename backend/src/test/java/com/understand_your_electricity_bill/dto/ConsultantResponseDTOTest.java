package com.understand_your_electricity_bill.dto;

import com.understand_your_electricity_bill.model.enums.UserStatus;
import com.understand_your_electricity_bill.model.enums.UserType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ConsultantResponseDTO Tests")
class ConsultantResponseDTOTest {

    // ========== BUILDER PATTERN TESTS ==========

    @Test
    @DisplayName("Should build DTO with builder pattern")
    void shouldBuildDtoWithBuilderPattern() {
        // Given
        UUID id = UUID.randomUUID();
        LocalDate registrationDate = LocalDate.of(2024, 1, 15);
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 15, 10, 30);
        LocalDateTime updatedAt = LocalDateTime.of(2024, 1, 20, 15, 45);

        // When
        ConsultantResponseDTO dto = ConsultantResponseDTO.builder()
                .id(id)
                .email("consultant@company.com")
                .name("João Consultor")
                .phone("+5511999999999")
                .userType(UserType.CONSULTANT)
                .status(UserStatus.ACTIVE)
                .consultantName("João Consultor")
                .company("Energy Consulting Ltda")
                .cnpj("12345678000190")
                .registrationNumber("REG123456")
                .address("Av. Paulista, 1000")
                .city("São Paulo")
                .state("SP")
                .zipCode("01310100")
                .companyLogo("https://logo.url/image.png")
                .registrationDate(registrationDate)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .managedClientsCount(5)
                .build();

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.email()).isEqualTo("consultant@company.com");
        assertThat(dto.name()).isEqualTo("João Consultor");
        assertThat(dto.consultantName()).isEqualTo("João Consultor");
        assertThat(dto.company()).isEqualTo("Energy Consulting Ltda");
        assertThat(dto.cnpj()).isEqualTo("12345678000190");
        assertThat(dto.managedClientsCount()).isEqualTo(5);
    }

    @Test
    @DisplayName("Should build DTO with minimal fields")
    void shouldBuildDtoWithMinimalFields() {
        // Given & When
        ConsultantResponseDTO dto = ConsultantResponseDTO.builder()
                .id(UUID.randomUUID())
                .email("test@company.com")
                .name("Test Consultant")
                .status(UserStatus.ACTIVE)
                .consultantName("Test Consultant")
                .company("Test Company")
                .cnpj("12345678000190")
                .address("Test Address")
                .managedClientsCount(0)
                .build();

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.email()).isEqualTo("test@company.com");
        assertThat(dto.managedClientsCount()).isEqualTo(0);
    }

    // ========== MASKEDCNPJ METHOD TESTS ==========

    @Test
    @DisplayName("Should format CNPJ with mask")
    void shouldFormatCnpjWithMask() {
        // Given
        ConsultantResponseDTO dto = ConsultantResponseDTO.builder()
                .id(UUID.randomUUID())
                .email("consultant@company.com")
                .name("João Consultor")
                .consultantName("João Consultor")
                .company("Energy Consulting Ltda")
                .cnpj("12345678000190")
                .status(UserStatus.ACTIVE)
                .address("Test Address")
                .managedClientsCount(0)
                .build();

        // When
        String maskedCnpj = dto.getMaskedCnpj();

        // Then
        assertThat(maskedCnpj).isEqualTo("12.345.678/0001-90");
    }

    @Test
    @DisplayName("Should return original CNPJ when length is invalid")
    void shouldReturnOriginalCnpjWhenLengthIsInvalid() {
        // Given
        ConsultantResponseDTO dto = ConsultantResponseDTO.builder()
                .id(UUID.randomUUID())
                .email("consultant@company.com")
                .name("João Consultor")
                .consultantName("João Consultor")
                .company("Energy Consulting Ltda")
                .cnpj("123456")
                .status(UserStatus.ACTIVE)
                .address("Test Address")
                .managedClientsCount(0)
                .build();

        // When
        String maskedCnpj = dto.getMaskedCnpj();

        // Then
        assertThat(maskedCnpj).isEqualTo("123456");
    }

    @Test
    @DisplayName("Should handle null CNPJ")
    void shouldHandleNullCnpj() {
        // Given
        ConsultantResponseDTO dto = ConsultantResponseDTO.builder()
                .id(UUID.randomUUID())
                .email("consultant@company.com")
                .name("João Consultor")
                .consultantName("João Consultor")
                .company("Energy Consulting Ltda")
                .cnpj(null)
                .status(UserStatus.ACTIVE)
                .address("Test Address")
                .managedClientsCount(0)
                .build();

        // When
        String maskedCnpj = dto.getMaskedCnpj();

        // Then
        assertThat(maskedCnpj).isNull();
    }

    // ========== FORMATTEDZIPCODE METHOD TESTS ==========

    @Test
    @DisplayName("Should format ZIP code")
    void shouldFormatZipCode() {
        // Given
        ConsultantResponseDTO dto = ConsultantResponseDTO.builder()
                .id(UUID.randomUUID())
                .email("consultant@company.com")
                .name("João Consultor")
                .consultantName("João Consultor")
                .company("Energy Consulting Ltda")
                .cnpj("12345678000190")
                .zipCode("01310100")
                .status(UserStatus.ACTIVE)
                .address("Test Address")
                .managedClientsCount(0)
                .build();

        // When
        String formattedZipCode = dto.getFormattedZipCode();

        // Then
        assertThat(formattedZipCode).isEqualTo("01310-100");
    }

    @Test
    @DisplayName("Should return original ZIP code when length is invalid")
    void shouldReturnOriginalZipCodeWhenLengthIsInvalid() {
        // Given
        ConsultantResponseDTO dto = ConsultantResponseDTO.builder()
                .id(UUID.randomUUID())
                .email("consultant@company.com")
                .name("João Consultor")
                .consultantName("João Consultor")
                .company("Energy Consulting Ltda")
                .cnpj("12345678000190")
                .zipCode("123")
                .status(UserStatus.ACTIVE)
                .address("Test Address")
                .managedClientsCount(0)
                .build();

        // When
        String formattedZipCode = dto.getFormattedZipCode();

        // Then
        assertThat(formattedZipCode).isEqualTo("123");
    }

    @Test
    @DisplayName("Should handle null ZIP code")
    void shouldHandleNullZipCode() {
        // Given
        ConsultantResponseDTO dto = ConsultantResponseDTO.builder()
                .id(UUID.randomUUID())
                .email("consultant@company.com")
                .name("João Consultor")
                .consultantName("João Consultor")
                .company("Energy Consulting Ltda")
                .cnpj("12345678000190")
                .zipCode(null)
                .status(UserStatus.ACTIVE)
                .address("Test Address")
                .managedClientsCount(0)
                .build();

        // When
        String formattedZipCode = dto.getFormattedZipCode();

        // Then
        assertThat(formattedZipCode).isNull();
    }

    // ========== HASMANAGEDCLIENTS METHOD TESTS ==========

    @Test
    @DisplayName("Should return true when consultant has managed clients")
    void shouldReturnTrueWhenConsultantHasManagedClients() {
        // Given
        ConsultantResponseDTO dto = ConsultantResponseDTO.builder()
                .id(UUID.randomUUID())
                .email("consultant@company.com")
                .name("João Consultor")
                .consultantName("João Consultor")
                .company("Energy Consulting Ltda")
                .cnpj("12345678000190")
                .status(UserStatus.ACTIVE)
                .address("Test Address")
                .managedClientsCount(5)
                .build();

        // When & Then
        assertThat(dto.hasManagedClients()).isTrue();
    }

    @Test
    @DisplayName("Should return false when consultant has no managed clients")
    void shouldReturnFalseWhenConsultantHasNoManagedClients() {
        // Given
        ConsultantResponseDTO dto = ConsultantResponseDTO.builder()
                .id(UUID.randomUUID())
                .email("consultant@company.com")
                .name("João Consultor")
                .consultantName("João Consultor")
                .company("Energy Consulting Ltda")
                .cnpj("12345678000190")
                .status(UserStatus.ACTIVE)
                .address("Test Address")
                .managedClientsCount(0)
                .build();

        // When & Then
        assertThat(dto.hasManagedClients()).isFalse();
    }

    // ========== HASCOMPANYLOGO METHOD TESTS ==========

    @Test
    @DisplayName("Should return true when company logo is set")
    void shouldReturnTrueWhenCompanyLogoIsSet() {
        // Given
        ConsultantResponseDTO dto = ConsultantResponseDTO.builder()
                .id(UUID.randomUUID())
                .email("consultant@company.com")
                .name("João Consultor")
                .consultantName("João Consultor")
                .company("Energy Consulting Ltda")
                .cnpj("12345678000190")
                .companyLogo("https://logo.url/image.png")
                .status(UserStatus.ACTIVE)
                .address("Test Address")
                .managedClientsCount(0)
                .build();

        // When & Then
        assertThat(dto.hasCompanyLogo()).isTrue();
    }

    @Test
    @DisplayName("Should return false when company logo is null")
    void shouldReturnFalseWhenCompanyLogoIsNull() {
        // Given
        ConsultantResponseDTO dto = ConsultantResponseDTO.builder()
                .id(UUID.randomUUID())
                .email("consultant@company.com")
                .name("João Consultor")
                .consultantName("João Consultor")
                .company("Energy Consulting Ltda")
                .cnpj("12345678000190")
                .companyLogo(null)
                .status(UserStatus.ACTIVE)
                .address("Test Address")
                .managedClientsCount(0)
                .build();

        // When & Then
        assertThat(dto.hasCompanyLogo()).isFalse();
    }

    @Test
    @DisplayName("Should return false when company logo is empty")
    void shouldReturnFalseWhenCompanyLogoIsEmpty() {
        // Given
        ConsultantResponseDTO dto = ConsultantResponseDTO.builder()
                .id(UUID.randomUUID())
                .email("consultant@company.com")
                .name("João Consultor")
                .consultantName("João Consultor")
                .company("Energy Consulting Ltda")
                .cnpj("12345678000190")
                .companyLogo("")
                .status(UserStatus.ACTIVE)
                .address("Test Address")
                .managedClientsCount(0)
                .build();

        // When & Then
        assertThat(dto.hasCompanyLogo()).isFalse();
    }

    // ========== COMPLETE OBJECT TESTS ==========

    @Test
    @DisplayName("Should create complete DTO with all fields")
    void shouldCreateCompleteDtoWithAllFields() {
        // Given
        UUID id = UUID.randomUUID();
        LocalDate registrationDate = LocalDate.of(2024, 1, 15);
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 15, 10, 30);
        LocalDateTime updatedAt = LocalDateTime.of(2024, 1, 20, 15, 45);

        // When
        ConsultantResponseDTO dto = ConsultantResponseDTO.builder()
                .id(id)
                .email("consultant@company.com")
                .name("João Consultor")
                .phone("+5511999999999")
                .userType(UserType.CONSULTANT)
                .status(UserStatus.ACTIVE)
                .consultantName("João Consultor")
                .company("Energy Consulting Ltda")
                .cnpj("12345678000190")
                .registrationNumber("REG123456")
                .address("Av. Paulista, 1000, Sala 500")
                .city("São Paulo")
                .state("SP")
                .zipCode("01310100")
                .companyLogo("https://logo.url/image.png")
                .registrationDate(registrationDate)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .managedClientsCount(5)
                .build();

        // Then - Test all utility methods
        assertThat(dto.getMaskedCnpj()).isEqualTo("12.345.678/0001-90");
        assertThat(dto.getFormattedZipCode()).isEqualTo("01310-100");
        assertThat(dto.hasManagedClients()).isTrue();
        assertThat(dto.hasCompanyLogo()).isTrue();
    }

    @Test
    @DisplayName("Should create DTO without optional fields")
    void shouldCreateDtoWithoutOptionalFields() {
        // Given & When
        ConsultantResponseDTO dto = ConsultantResponseDTO.builder()
                .id(UUID.randomUUID())
                .email("minimal@company.com")
                .name("Minimal Consultant")
                .userType(UserType.CONSULTANT)
                .status(UserStatus.PENDING_VERIFICATION)
                .consultantName("Minimal Consultant")
                .company("Minimal Company")
                .cnpj("98765432000100")
                .address("Minimal Address")
                .registrationDate(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .managedClientsCount(0)
                .build();

        // Then
        assertThat(dto.phone()).isNull();
        assertThat(dto.registrationNumber()).isNull();
        assertThat(dto.city()).isNull();
        assertThat(dto.state()).isNull();
        assertThat(dto.zipCode()).isNull();
        assertThat(dto.companyLogo()).isNull();
        assertThat(dto.hasManagedClients()).isFalse();
        assertThat(dto.hasCompanyLogo()).isFalse();
    }
}

