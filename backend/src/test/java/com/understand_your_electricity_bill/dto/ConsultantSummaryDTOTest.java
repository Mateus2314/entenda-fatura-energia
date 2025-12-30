package com.understand_your_electricity_bill.dto;

import com.understand_your_electricity_bill.model.enums.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ConsultantSummaryDTO Tests")
class ConsultantSummaryDTOTest {

    // ========== MASKEDCNPJ METHOD TESTS ==========

    @Test
    @DisplayName("Should format CNPJ with mask")
    void shouldFormatCnpjWithMask() {
        // Given
        ConsultantSummaryDTO dto = new ConsultantSummaryDTO(
                UUID.randomUUID(),
                "João Consultor",
                "Energy Consulting Ltda",
                "consultant@company.com",
                "12345678000190",
                "São Paulo",
                "SP",
                UserStatus.ACTIVE,
                LocalDate.now(),
                5
        );

        // When
        String maskedCnpj = dto.getMaskedCnpj();

        // Then
        assertThat(maskedCnpj).isEqualTo("12.345.678/0001-90");
    }

    @Test
    @DisplayName("Should return original CNPJ when length is invalid")
    void shouldReturnOriginalCnpjWhenLengthIsInvalid() {
        // Given
        ConsultantSummaryDTO dto = new ConsultantSummaryDTO(
                UUID.randomUUID(),
                "João Consultor",
                "Energy Consulting Ltda",
                "consultant@company.com",
                "123456",  // Invalid length
                "São Paulo",
                "SP",
                UserStatus.ACTIVE,
                LocalDate.now(),
                5
        );

        // When
        String maskedCnpj = dto.getMaskedCnpj();

        // Then
        assertThat(maskedCnpj).isEqualTo("123456");
    }

    @Test
    @DisplayName("Should handle null CNPJ")
    void shouldHandleNullCnpj() {
        // Given
        ConsultantSummaryDTO dto = new ConsultantSummaryDTO(
                UUID.randomUUID(),
                "João Consultor",
                "Energy Consulting Ltda",
                "consultant@company.com",
                null,
                "São Paulo",
                "SP",
                UserStatus.ACTIVE,
                LocalDate.now(),
                5
        );

        // When
        String maskedCnpj = dto.getMaskedCnpj();

        // Then
        assertThat(maskedCnpj).isNull();
    }

    // ========== GETLOCATION METHOD TESTS ==========

    @Test
    @DisplayName("Should return city and state when both are present")
    void shouldReturnCityAndStateWhenBothArePresent() {
        // Given
        ConsultantSummaryDTO dto = new ConsultantSummaryDTO(
                UUID.randomUUID(),
                "João Consultor",
                "Energy Consulting Ltda",
                "consultant@company.com",
                "12345678000190",
                "São Paulo",
                "SP",
                UserStatus.ACTIVE,
                LocalDate.now(),
                5
        );

        // When
        String location = dto.getLocation();

        // Then
        assertThat(location).isEqualTo("São Paulo/SP");
    }

    @Test
    @DisplayName("Should return only city when state is null")
    void shouldReturnOnlyCityWhenStateIsNull() {
        // Given
        ConsultantSummaryDTO dto = new ConsultantSummaryDTO(
                UUID.randomUUID(),
                "João Consultor",
                "Energy Consulting Ltda",
                "consultant@company.com",
                "12345678000190",
                "São Paulo",
                null,
                UserStatus.ACTIVE,
                LocalDate.now(),
                5
        );

        // When
        String location = dto.getLocation();

        // Then
        assertThat(location).isEqualTo("São Paulo");
    }

    @Test
    @DisplayName("Should return only state when city is null")
    void shouldReturnOnlyStateWhenCityIsNull() {
        // Given
        ConsultantSummaryDTO dto = new ConsultantSummaryDTO(
                UUID.randomUUID(),
                "João Consultor",
                "Energy Consulting Ltda",
                "consultant@company.com",
                "12345678000190",
                null,
                "SP",
                UserStatus.ACTIVE,
                LocalDate.now(),
                5
        );

        // When
        String location = dto.getLocation();

        // Then
        assertThat(location).isEqualTo("SP");
    }

    @Test
    @DisplayName("Should return N/A when both city and state are null")
    void shouldReturnNAWhenBothCityAndStateAreNull() {
        // Given
        ConsultantSummaryDTO dto = new ConsultantSummaryDTO(
                UUID.randomUUID(),
                "João Consultor",
                "Energy Consulting Ltda",
                "consultant@company.com",
                "12345678000190",
                null,
                null,
                UserStatus.ACTIVE,
                LocalDate.now(),
                5
        );

        // When
        String location = dto.getLocation();

        // Then
        assertThat(location).isEqualTo("N/A");
    }

    // ========== ISACTIVE METHOD TESTS ==========

    @Test
    @DisplayName("Should return true when status is ACTIVE")
    void shouldReturnTrueWhenStatusIsActive() {
        // Given
        ConsultantSummaryDTO dto = new ConsultantSummaryDTO(
                UUID.randomUUID(),
                "João Consultor",
                "Energy Consulting Ltda",
                "consultant@company.com",
                "12345678000190",
                "São Paulo",
                "SP",
                UserStatus.ACTIVE,
                LocalDate.now(),
                5
        );

        // When & Then
        assertThat(dto.isActive()).isTrue();
    }

    @Test
    @DisplayName("Should return false when status is SUSPENDED")
    void shouldReturnFalseWhenStatusIsSuspended() {
        // Given
        ConsultantSummaryDTO dto = new ConsultantSummaryDTO(
                UUID.randomUUID(),
                "João Consultor",
                "Energy Consulting Ltda",
                "consultant@company.com",
                "12345678000190",
                "São Paulo",
                "SP",
                UserStatus.SUSPENDED,
                LocalDate.now(),
                5
        );

        // When & Then
        assertThat(dto.isActive()).isFalse();
    }

    // ========== GETDISPLAYNAME METHOD TESTS ==========

    @Test
    @DisplayName("Should return name without status when active")
    void shouldReturnNameWithoutStatusWhenActive() {
        // Given
        ConsultantSummaryDTO dto = new ConsultantSummaryDTO(
                UUID.randomUUID(),
                "João Consultor",
                "Energy Consulting Ltda",
                "consultant@company.com",
                "12345678000190",
                "São Paulo",
                "SP",
                UserStatus.ACTIVE,
                LocalDate.now(),
                5
        );

        // When
        String displayName = dto.getDisplayName();

        // Then
        assertThat(displayName).isEqualTo("João Consultor");
    }

    @Test
    @DisplayName("Should return name with status indicator when suspended")
    void shouldReturnNameWithStatusIndicatorWhenSuspended() {
        // Given
        ConsultantSummaryDTO dto = new ConsultantSummaryDTO(
                UUID.randomUUID(),
                "João Consultor",
                "Energy Consulting Ltda",
                "consultant@company.com",
                "12345678000190",
                "São Paulo",
                "SP",
                UserStatus.SUSPENDED,
                LocalDate.now(),
                5
        );

        // When
        String displayName = dto.getDisplayName();

        // Then
        assertThat(displayName).isEqualTo("João Consultor [SUSPENDED]");
    }

    // ========== GETCOMPANYDISPLAY METHOD TESTS ==========

    @Test
    @DisplayName("Should return company display with consultant name")
    void shouldReturnCompanyDisplayWithConsultantName() {
        // Given
        ConsultantSummaryDTO dto = new ConsultantSummaryDTO(
                UUID.randomUUID(),
                "João Consultor",
                "Energy Consulting Ltda",
                "consultant@company.com",
                "12345678000190",
                "São Paulo",
                "SP",
                UserStatus.ACTIVE,
                LocalDate.now(),
                5
        );

        // When
        String companyDisplay = dto.getCompanyDisplay();

        // Then
        assertThat(companyDisplay).isEqualTo("Energy Consulting Ltda - João Consultor");
    }

    // ========== HASMANAGEDCLIENTS METHOD TESTS ==========

    @Test
    @DisplayName("Should return true when consultant has managed clients")
    void shouldReturnTrueWhenConsultantHasManagedClients() {
        // Given
        ConsultantSummaryDTO dto = new ConsultantSummaryDTO(
                UUID.randomUUID(),
                "João Consultor",
                "Energy Consulting Ltda",
                "consultant@company.com",
                "12345678000190",
                "São Paulo",
                "SP",
                UserStatus.ACTIVE,
                LocalDate.now(),
                5
        );

        // When & Then
        assertThat(dto.hasManagedClients()).isTrue();
    }

    @Test
    @DisplayName("Should return false when consultant has no managed clients")
    void shouldReturnFalseWhenConsultantHasNoManagedClients() {
        // Given
        ConsultantSummaryDTO dto = new ConsultantSummaryDTO(
                UUID.randomUUID(),
                "João Consultor",
                "Energy Consulting Ltda",
                "consultant@company.com",
                "12345678000190",
                "São Paulo",
                "SP",
                UserStatus.ACTIVE,
                LocalDate.now(),
                0
        );

        // When & Then
        assertThat(dto.hasManagedClients()).isFalse();
    }
}

