package com.understand_your_electricity_bill.dto;

import com.understand_your_electricity_bill.model.enums.UserStatus;
import com.understand_your_electricity_bill.model.enums.UserType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ClientResponseDTO Tests")
class ClientResponseDTOTest {

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
        ClientResponseDTO dto = ClientResponseDTO.builder()
                .id(id)
                .email("joao.silva@test.com")
                .name("João Silva")
                .phone("+5511999999999")
                .userType(UserType.CLIENT)
                .status(UserStatus.ACTIVE)
                .cpf("12345678901")
                .address("Rua das Flores, 123")
                .city("São Paulo")
                .state("SP")
                .zipCode("01234567")
                .registrationDate(registrationDate)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .consultantCount(2)
                .build();

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.email()).isEqualTo("joao.silva@test.com");
        assertThat(dto.name()).isEqualTo("João Silva");
        assertThat(dto.phone()).isEqualTo("+5511999999999");
        assertThat(dto.userType()).isEqualTo(UserType.CLIENT);
        assertThat(dto.status()).isEqualTo(UserStatus.ACTIVE);
        assertThat(dto.cpf()).isEqualTo("12345678901");
        assertThat(dto.address()).isEqualTo("Rua das Flores, 123");
        assertThat(dto.city()).isEqualTo("São Paulo");
        assertThat(dto.state()).isEqualTo("SP");
        assertThat(dto.zipCode()).isEqualTo("01234567");
        assertThat(dto.registrationDate()).isEqualTo(registrationDate);
        assertThat(dto.createdAt()).isEqualTo(createdAt);
        assertThat(dto.updatedAt()).isEqualTo(updatedAt);
        assertThat(dto.consultantCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should build DTO with minimal fields")
    void shouldBuildDtoWithMinimalFields() {
        // Given & When
        ClientResponseDTO dto = ClientResponseDTO.builder()
                .id(UUID.randomUUID())
                .email("test@test.com")
                .name("Test User")
                .status(UserStatus.ACTIVE)
                .cpf("12345678901")
                .address("Test Address")
                .consultantCount(0)
                .build();

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.email()).isEqualTo("test@test.com");
        assertThat(dto.consultantCount()).isEqualTo(0);
    }

    // ========== MASKEDCPF METHOD TESTS ==========

    @Test
    @DisplayName("Should format CPF with mask")
    void shouldFormatCpfWithMask() {
        // Given
        ClientResponseDTO dto = ClientResponseDTO.builder()
                .id(UUID.randomUUID())
                .email("joao@test.com")
                .name("João Silva")
                .cpf("12345678901")
                .status(UserStatus.ACTIVE)
                .address("Test Address")
                .consultantCount(0)
                .build();

        // When
        String maskedCpf = dto.getMaskedCpf();

        // Then
        assertThat(maskedCpf).isEqualTo("123.456.789-01");
    }

    @Test
    @DisplayName("Should return original CPF when length is invalid")
    void shouldReturnOriginalCpfWhenLengthIsInvalid() {
        // Given
        ClientResponseDTO dto = ClientResponseDTO.builder()
                .id(UUID.randomUUID())
                .email("joao@test.com")
                .name("João Silva")
                .cpf("123456")
                .status(UserStatus.ACTIVE)
                .address("Test Address")
                .consultantCount(0)
                .build();

        // When
        String maskedCpf = dto.getMaskedCpf();

        // Then
        assertThat(maskedCpf).isEqualTo("123456");
    }

    @Test
    @DisplayName("Should handle null CPF")
    void shouldHandleNullCpf() {
        // Given
        ClientResponseDTO dto = ClientResponseDTO.builder()
                .id(UUID.randomUUID())
                .email("joao@test.com")
                .name("João Silva")
                .cpf(null)
                .status(UserStatus.ACTIVE)
                .address("Test Address")
                .consultantCount(0)
                .build();

        // When
        String maskedCpf = dto.getMaskedCpf();

        // Then
        assertThat(maskedCpf).isNull();
    }

    // ========== FORMATTEDZIPCODE METHOD TESTS ==========

    @Test
    @DisplayName("Should format ZIP code")
    void shouldFormatZipCode() {
        // Given
        ClientResponseDTO dto = ClientResponseDTO.builder()
                .id(UUID.randomUUID())
                .email("joao@test.com")
                .name("João Silva")
                .cpf("12345678901")
                .zipCode("01234567")
                .status(UserStatus.ACTIVE)
                .address("Test Address")
                .consultantCount(0)
                .build();

        // When
        String formattedZipCode = dto.getFormattedZipCode();

        // Then
        assertThat(formattedZipCode).isEqualTo("01234-567");
    }

    @Test
    @DisplayName("Should return original ZIP code when length is invalid")
    void shouldReturnOriginalZipCodeWhenLengthIsInvalid() {
        // Given
        ClientResponseDTO dto = ClientResponseDTO.builder()
                .id(UUID.randomUUID())
                .email("joao@test.com")
                .name("João Silva")
                .cpf("12345678901")
                .zipCode("123")
                .status(UserStatus.ACTIVE)
                .address("Test Address")
                .consultantCount(0)
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
        ClientResponseDTO dto = ClientResponseDTO.builder()
                .id(UUID.randomUUID())
                .email("joao@test.com")
                .name("João Silva")
                .cpf("12345678901")
                .zipCode(null)
                .status(UserStatus.ACTIVE)
                .address("Test Address")
                .consultantCount(0)
                .build();

        // When
        String formattedZipCode = dto.getFormattedZipCode();

        // Then
        assertThat(formattedZipCode).isNull();
    }

    // ========== HASCONSULTANTS METHOD TESTS ==========

    @Test
    @DisplayName("Should return true when client has consultants")
    void shouldReturnTrueWhenClientHasConsultants() {
        // Given
        ClientResponseDTO dto = ClientResponseDTO.builder()
                .id(UUID.randomUUID())
                .email("joao@test.com")
                .name("João Silva")
                .cpf("12345678901")
                .status(UserStatus.ACTIVE)
                .address("Test Address")
                .consultantCount(3)
                .build();

        // When & Then
        assertThat(dto.hasConsultants()).isTrue();
    }

    @Test
    @DisplayName("Should return false when client has no consultants")
    void shouldReturnFalseWhenClientHasNoConsultants() {
        // Given
        ClientResponseDTO dto = ClientResponseDTO.builder()
                .id(UUID.randomUUID())
                .email("joao@test.com")
                .name("João Silva")
                .cpf("12345678901")
                .status(UserStatus.ACTIVE)
                .address("Test Address")
                .consultantCount(0)
                .build();

        // When & Then
        assertThat(dto.hasConsultants()).isFalse();
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
        ClientResponseDTO dto = ClientResponseDTO.builder()
                .id(id)
                .email("joao.silva@test.com")
                .name("João Silva")
                .phone("+5511999999999")
                .userType(UserType.CLIENT)
                .status(UserStatus.ACTIVE)
                .cpf("12345678901")
                .address("Rua das Flores, 123, Apto 45")
                .city("São Paulo")
                .state("SP")
                .zipCode("01234567")
                .registrationDate(registrationDate)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .consultantCount(2)
                .build();

        // Then - Test all utility methods
        assertThat(dto.getMaskedCpf()).isEqualTo("123.456.789-01");
        assertThat(dto.getFormattedZipCode()).isEqualTo("01234-567");
        assertThat(dto.hasConsultants()).isTrue();
    }

    @Test
    @DisplayName("Should create DTO without optional fields")
    void shouldCreateDtoWithoutOptionalFields() {
        // Given & When
        ClientResponseDTO dto = ClientResponseDTO.builder()
                .id(UUID.randomUUID())
                .email("minimal@test.com")
                .name("Minimal User")
                .userType(UserType.CLIENT)
                .status(UserStatus.PENDING_VERIFICATION)
                .cpf("98765432109")
                .address("Minimal Address")
                .registrationDate(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .consultantCount(0)
                .build();

        // Then
        assertThat(dto.phone()).isNull();
        assertThat(dto.city()).isNull();
        assertThat(dto.state()).isNull();
        assertThat(dto.zipCode()).isNull();
        assertThat(dto.hasConsultants()).isFalse();
    }
}

