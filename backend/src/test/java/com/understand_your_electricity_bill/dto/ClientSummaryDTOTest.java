package com.understand_your_electricity_bill.dto;

import com.understand_your_electricity_bill.model.enums.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ClientSummaryDTO Tests")
class ClientSummaryDTOTest {

    // ========== MASKEDCPF METHOD TESTS ==========

    @Test
    @DisplayName("Should format CPF with mask")
    void shouldFormatCpfWithMask() {
        // Given
        ClientSummaryDTO dto = new ClientSummaryDTO(
                java.util.UUID.randomUUID(),
                "João Silva",
                "joao@test.com",
                "12345678901",
                "São Paulo",
                "SP",
                UserStatus.ACTIVE,
                LocalDate.now()
        );

        // When
        String maskedCpf = dto.getMaskedCpf();

        // Then
        assertThat(maskedCpf).isEqualTo("123.456.789-01");
    }

    @Test
    @DisplayName("Should return original CPF when length is invalid")
    void shouldReturnOriginalCpfWhenLengthIsInvalid() {
        // Given
        ClientSummaryDTO dto = new ClientSummaryDTO(
                java.util.UUID.randomUUID(),
                "João Silva",
                "joao@test.com",
                "123456",  // Invalid length
                "São Paulo",
                "SP",
                UserStatus.ACTIVE,
                LocalDate.now()
        );

        // When
        String maskedCpf = dto.getMaskedCpf();

        // Then
        assertThat(maskedCpf).isEqualTo("123456");
    }

    @Test
    @DisplayName("Should handle null CPF")
    void shouldHandleNullCpf() {
        // Given
        ClientSummaryDTO dto = new ClientSummaryDTO(
                java.util.UUID.randomUUID(),
                "João Silva",
                "joao@test.com",
                null,
                "São Paulo",
                "SP",
                UserStatus.ACTIVE,
                LocalDate.now()
        );

        // When
        String maskedCpf = dto.getMaskedCpf();

        // Then
        assertThat(maskedCpf).isNull();
    }

    // ========== GETLOCATION METHOD TESTS ==========

    @Test
    @DisplayName("Should return city and state when both are present")
    void shouldReturnCityAndStateWhenBothArePresent() {
        // Given
        ClientSummaryDTO dto = new ClientSummaryDTO(
                java.util.UUID.randomUUID(),
                "João Silva",
                "joao@test.com",
                "12345678901",
                "São Paulo",
                "SP",
                UserStatus.ACTIVE,
                LocalDate.now()
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
        ClientSummaryDTO dto = new ClientSummaryDTO(
                java.util.UUID.randomUUID(),
                "João Silva",
                "joao@test.com",
                "12345678901",
                "São Paulo",
                null,
                UserStatus.ACTIVE,
                LocalDate.now()
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
        ClientSummaryDTO dto = new ClientSummaryDTO(
                java.util.UUID.randomUUID(),
                "João Silva",
                "joao@test.com",
                "12345678901",
                null,
                "SP",
                UserStatus.ACTIVE,
                LocalDate.now()
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
        ClientSummaryDTO dto = new ClientSummaryDTO(
                java.util.UUID.randomUUID(),
                "João Silva",
                "joao@test.com",
                "12345678901",
                null,
                null,
                UserStatus.ACTIVE,
                LocalDate.now()
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
        ClientSummaryDTO dto = new ClientSummaryDTO(
                java.util.UUID.randomUUID(),
                "João Silva",
                "joao@test.com",
                "12345678901",
                "São Paulo",
                "SP",
                UserStatus.ACTIVE,
                LocalDate.now()
        );

        // When & Then
        assertThat(dto.isActive()).isTrue();
    }

    @Test
    @DisplayName("Should return false when status is SUSPENDED")
    void shouldReturnFalseWhenStatusIsSuspended() {
        // Given
        ClientSummaryDTO dto = new ClientSummaryDTO(
                java.util.UUID.randomUUID(),
                "João Silva",
                "joao@test.com",
                "12345678901",
                "São Paulo",
                "SP",
                UserStatus.SUSPENDED,
                LocalDate.now()
        );

        // When & Then
        assertThat(dto.isActive()).isFalse();
    }

    @Test
    @DisplayName("Should return false when status is INACTIVE")
    void shouldReturnFalseWhenStatusIsInactive() {
        // Given
        ClientSummaryDTO dto = new ClientSummaryDTO(
                java.util.UUID.randomUUID(),
                "João Silva",
                "joao@test.com",
                "12345678901",
                "São Paulo",
                "SP",
                UserStatus.INACTIVE,
                LocalDate.now()
        );

        // When & Then
        assertThat(dto.isActive()).isFalse();
    }

    // ========== GETDISPLAYNAME METHOD TESTS ==========

    @Test
    @DisplayName("Should return name without status when active")
    void shouldReturnNameWithoutStatusWhenActive() {
        // Given
        ClientSummaryDTO dto = new ClientSummaryDTO(
                java.util.UUID.randomUUID(),
                "João Silva",
                "joao@test.com",
                "12345678901",
                "São Paulo",
                "SP",
                UserStatus.ACTIVE,
                LocalDate.now()
        );

        // When
        String displayName = dto.getDisplayName();

        // Then
        assertThat(displayName).isEqualTo("João Silva");
    }

    @Test
    @DisplayName("Should return name with status indicator when suspended")
    void shouldReturnNameWithStatusIndicatorWhenSuspended() {
        // Given
        ClientSummaryDTO dto = new ClientSummaryDTO(
                java.util.UUID.randomUUID(),
                "João Silva",
                "joao@test.com",
                "12345678901",
                "São Paulo",
                "SP",
                UserStatus.SUSPENDED,
                LocalDate.now()
        );

        // When
        String displayName = dto.getDisplayName();

        // Then
        assertThat(displayName).isEqualTo("João Silva [SUSPENDED]");
    }

    @Test
    @DisplayName("Should return name with status indicator when inactive")
    void shouldReturnNameWithStatusIndicatorWhenInactive() {
        // Given
        ClientSummaryDTO dto = new ClientSummaryDTO(
                java.util.UUID.randomUUID(),
                "João Silva",
                "joao@test.com",
                "12345678901",
                "São Paulo",
                "SP",
                UserStatus.INACTIVE,
                LocalDate.now()
        );

        // When
        String displayName = dto.getDisplayName();

        // Then
        assertThat(displayName).isEqualTo("João Silva [INACTIVE]");
    }

    @Test
    @DisplayName("Should return name with status indicator when pending verification")
    void shouldReturnNameWithStatusIndicatorWhenPendingVerification() {
        // Given
        ClientSummaryDTO dto = new ClientSummaryDTO(
                java.util.UUID.randomUUID(),
                "João Silva",
                "joao@test.com",
                "12345678901",
                "São Paulo",
                "SP",
                UserStatus.PENDING_VERIFICATION,
                LocalDate.now()
        );

        // When
        String displayName = dto.getDisplayName();

        // Then
        assertThat(displayName).isEqualTo("João Silva [PENDING_VERIFICATION]");
    }
}

