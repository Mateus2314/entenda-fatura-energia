package com.understand_your_electricity_bill.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TariffResponseDTO Tests")
class TariffResponseDTOTest {

    // ========== BUILDER PATTERN TESTS ==========

    @Test
    @DisplayName("Should build DTO with builder pattern")
    void shouldBuildDtoWithBuilderPattern() {
        // Given
        UUID id = UUID.randomUUID();
        LocalDate generationDate = LocalDate.of(2024, 1, 1);
        LocalDate validFrom = LocalDate.of(2024, 1, 1);
        LocalDate validUntil = LocalDate.of(2024, 12, 31);
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2024, 1, 15, 14, 30);

        // When
        TariffResponseDTO dto = TariffResponseDTO.builder()
                .id(id)
                .generationDate(generationDate)
                .descriptionReh("RESOLUÇÃO HOMOLOGATÓRIA Nº 0.937")
                .distributor("CPFL PAULISTA")
                .cnpjDistributor("12345678000190")
                .validFrom(validFrom)
                .validUntil(validUntil)
                .tariffBaseDesc("Tarifa de Aplicação")
                .subgroup("A2")
                .tariffModality("Azul")
                .consumerClass("Residencial")
                .consumerSubclass("Residencial Normal")
                .detail("APE")
                .tariffPostName("Fora ponta")
                .tertiaryUnit("kW")
                .accessingAgent("Não se aplica")
                .tusdValue(new BigDecimal("1.8500"))
                .teValue(new BigDecimal("0.5000"))
                .flagGenerationDate(LocalDate.of(2024, 1, 1))
                .competenceDate(LocalDate.of(2024, 1, 1))
                .activatedFlagName("Verde")
                .flagAdditionalValue(BigDecimal.ZERO)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.distributor()).isEqualTo("CPFL PAULISTA");
        assertThat(dto.cnpjDistributor()).isEqualTo("12345678000190");
        assertThat(dto.tusdValue()).isEqualByComparingTo(new BigDecimal("1.8500"));
        assertThat(dto.teValue()).isEqualByComparingTo(new BigDecimal("0.5000"));
    }

    @Test
    @DisplayName("Should build DTO with minimal fields")
    void shouldBuildDtoWithMinimalFields() {
        // Given & When
        TariffResponseDTO dto = TariffResponseDTO.builder()
                .id(UUID.randomUUID())
                .generationDate(LocalDate.of(2024, 1, 1))
                .distributor("Test Distributor")
                .cnpjDistributor("98765432000100")
                .validFrom(LocalDate.of(2024, 1, 1))
                .tusdValue(new BigDecimal("1.5000"))
                .teValue(new BigDecimal("0.4000"))
                .build();

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.distributor()).isEqualTo("Test Distributor");
    }

    // ========== MASKEDCNPJ METHOD TESTS ==========

    @Test
    @DisplayName("Should format CNPJ with mask")
    void shouldFormatCnpjWithMask() {
        // Given
        TariffResponseDTO dto = TariffResponseDTO.builder()
                .id(UUID.randomUUID())
                .generationDate(LocalDate.of(2024, 1, 1))
                .distributor("CPFL PAULISTA")
                .cnpjDistributor("12345678000190")
                .validFrom(LocalDate.of(2024, 1, 1))
                .tusdValue(new BigDecimal("1.5000"))
                .teValue(new BigDecimal("0.4000"))
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
        TariffResponseDTO dto = TariffResponseDTO.builder()
                .id(UUID.randomUUID())
                .generationDate(LocalDate.of(2024, 1, 1))
                .distributor("CPFL PAULISTA")
                .cnpjDistributor("123456")
                .validFrom(LocalDate.of(2024, 1, 1))
                .tusdValue(new BigDecimal("1.5000"))
                .teValue(new BigDecimal("0.4000"))
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
        TariffResponseDTO dto = TariffResponseDTO.builder()
                .id(UUID.randomUUID())
                .generationDate(LocalDate.of(2024, 1, 1))
                .distributor("CPFL PAULISTA")
                .cnpjDistributor(null)
                .validFrom(LocalDate.of(2024, 1, 1))
                .tusdValue(new BigDecimal("1.5000"))
                .teValue(new BigDecimal("0.4000"))
                .build();

        // When
        String maskedCnpj = dto.getMaskedCnpj();

        // Then
        assertThat(maskedCnpj).isNull();
    }

    // ========== ISCURRENTLYVALID METHOD TESTS ==========

    @Test
    @DisplayName("Should return true when tariff is currently valid")
    void shouldReturnTrueWhenTariffIsCurrentlyValid() {
        // Given
        LocalDate today = LocalDate.now();
        TariffResponseDTO dto = TariffResponseDTO.builder()
                .id(UUID.randomUUID())
                .generationDate(LocalDate.of(2024, 1, 1))
                .distributor("CPFL PAULISTA")
                .cnpjDistributor("12345678000190")
                .validFrom(today.minusMonths(1))
                .validUntil(today.plusMonths(1))
                .tusdValue(new BigDecimal("1.5000"))
                .teValue(new BigDecimal("0.4000"))
                .build();

        // When & Then
        assertThat(dto.isCurrentlyValid()).isTrue();
    }

    @Test
    @DisplayName("Should return false when tariff is not yet valid")
    void shouldReturnFalseWhenTariffIsNotYetValid() {
        // Given
        LocalDate today = LocalDate.now();
        TariffResponseDTO dto = TariffResponseDTO.builder()
                .id(UUID.randomUUID())
                .generationDate(LocalDate.of(2024, 1, 1))
                .distributor("CPFL PAULISTA")
                .cnpjDistributor("12345678000190")
                .validFrom(today.plusDays(1))  // Starts tomorrow
                .validUntil(today.plusMonths(1))
                .tusdValue(new BigDecimal("1.5000"))
                .teValue(new BigDecimal("0.4000"))
                .build();

        // When & Then
        assertThat(dto.isCurrentlyValid()).isFalse();
    }

    @Test
    @DisplayName("Should return false when tariff has expired")
    void shouldReturnFalseWhenTariffHasExpired() {
        // Given
        LocalDate today = LocalDate.now();
        TariffResponseDTO dto = TariffResponseDTO.builder()
                .id(UUID.randomUUID())
                .generationDate(LocalDate.of(2024, 1, 1))
                .distributor("CPFL PAULISTA")
                .cnpjDistributor("12345678000190")
                .validFrom(today.minusMonths(2))
                .validUntil(today.minusDays(1))  // Expired yesterday
                .tusdValue(new BigDecimal("1.5000"))
                .teValue(new BigDecimal("0.4000"))
                .build();

        // When & Then
        assertThat(dto.isCurrentlyValid()).isFalse();
    }

    @Test
    @DisplayName("Should return true when valid until is null (indefinite)")
    void shouldReturnTrueWhenValidUntilIsNull() {
        // Given
        LocalDate today = LocalDate.now();
        TariffResponseDTO dto = TariffResponseDTO.builder()
                .id(UUID.randomUUID())
                .generationDate(LocalDate.of(2024, 1, 1))
                .distributor("CPFL PAULISTA")
                .cnpjDistributor("12345678000190")
                .validFrom(today.minusMonths(1))
                .validUntil(null)  // Indefinite
                .tusdValue(new BigDecimal("1.5000"))
                .teValue(new BigDecimal("0.4000"))
                .build();

        // When & Then
        assertThat(dto.isCurrentlyValid()).isTrue();
    }

    // ========== HASFLAG METHOD TESTS ==========

    @Test
    @DisplayName("Should return true when tariff has flag")
    void shouldReturnTrueWhenTariffHasFlag() {
        // Given
        TariffResponseDTO dto = TariffResponseDTO.builder()
                .id(UUID.randomUUID())
                .generationDate(LocalDate.of(2024, 1, 1))
                .distributor("CPFL PAULISTA")
                .cnpjDistributor("12345678000190")
                .validFrom(LocalDate.of(2024, 1, 1))
                .tusdValue(new BigDecimal("1.5000"))
                .teValue(new BigDecimal("0.4000"))
                .activatedFlagName("Verde")
                .build();

        // When & Then
        assertThat(dto.hasFlag()).isTrue();
    }

    @Test
    @DisplayName("Should return false when flag name is null")
    void shouldReturnFalseWhenFlagNameIsNull() {
        // Given
        TariffResponseDTO dto = TariffResponseDTO.builder()
                .id(UUID.randomUUID())
                .generationDate(LocalDate.of(2024, 1, 1))
                .distributor("CPFL PAULISTA")
                .cnpjDistributor("12345678000190")
                .validFrom(LocalDate.of(2024, 1, 1))
                .tusdValue(new BigDecimal("1.5000"))
                .teValue(new BigDecimal("0.4000"))
                .activatedFlagName(null)
                .build();

        // When & Then
        assertThat(dto.hasFlag()).isFalse();
    }

    @Test
    @DisplayName("Should return false when flag name is empty")
    void shouldReturnFalseWhenFlagNameIsEmpty() {
        // Given
        TariffResponseDTO dto = TariffResponseDTO.builder()
                .id(UUID.randomUUID())
                .generationDate(LocalDate.of(2024, 1, 1))
                .distributor("CPFL PAULISTA")
                .cnpjDistributor("12345678000190")
                .validFrom(LocalDate.of(2024, 1, 1))
                .tusdValue(new BigDecimal("1.5000"))
                .teValue(new BigDecimal("0.4000"))
                .activatedFlagName("")
                .build();

        // When & Then
        assertThat(dto.hasFlag()).isFalse();
    }

    // ========== GETTOTALTARIFFPERKWH METHOD TESTS ==========

    @Test
    @DisplayName("Should calculate total tariff per kWh")
    void shouldCalculateTotalTariffPerKwh() {
        // Given
        TariffResponseDTO dto = TariffResponseDTO.builder()
                .id(UUID.randomUUID())
                .generationDate(LocalDate.of(2024, 1, 1))
                .distributor("CPFL PAULISTA")
                .cnpjDistributor("12345678000190")
                .validFrom(LocalDate.of(2024, 1, 1))
                .tusdValue(new BigDecimal("1.8500"))
                .teValue(new BigDecimal("0.5000"))
                .build();

        // When
        BigDecimal total = dto.getTotalTariffPerKwh();

        // Then
        assertThat(total).isEqualByComparingTo(new BigDecimal("2.3500"));
    }

    @Test
    @DisplayName("Should return zero when TUSD or TE is null")
    void shouldReturnZeroWhenTusdOrTeIsNull() {
        // Given
        TariffResponseDTO dto = TariffResponseDTO.builder()
                .id(UUID.randomUUID())
                .generationDate(LocalDate.of(2024, 1, 1))
                .distributor("CPFL PAULISTA")
                .cnpjDistributor("12345678000190")
                .validFrom(LocalDate.of(2024, 1, 1))
                .tusdValue(null)
                .teValue(new BigDecimal("0.5000"))
                .build();

        // When
        BigDecimal total = dto.getTotalTariffPerKwh();

        // Then
        assertThat(total).isEqualByComparingTo(BigDecimal.ZERO);
    }

    // ========== CALCULATETOTALCOST METHOD TESTS ==========

    @Test
    @DisplayName("Should calculate total cost without flag")
    void shouldCalculateTotalCostWithoutFlag() {
        // Given
        TariffResponseDTO dto = TariffResponseDTO.builder()
                .id(UUID.randomUUID())
                .generationDate(LocalDate.of(2024, 1, 1))
                .distributor("CPFL PAULISTA")
                .cnpjDistributor("12345678000190")
                .validFrom(LocalDate.of(2024, 1, 1))
                .tusdValue(new BigDecimal("1.8500"))
                .teValue(new BigDecimal("0.5000"))
                .flagAdditionalValue(BigDecimal.ZERO)
                .build();

        // When
        BigDecimal totalCost = dto.calculateTotalCost(new BigDecimal("100"));

        // Then - (1.85 + 0.50) * 100 = 235.00
        assertThat(totalCost).isEqualByComparingTo(new BigDecimal("235.0000"));
    }

    @Test
    @DisplayName("Should calculate total cost with flag")
    void shouldCalculateTotalCostWithFlag() {
        // Given
        TariffResponseDTO dto = TariffResponseDTO.builder()
                .id(UUID.randomUUID())
                .generationDate(LocalDate.of(2024, 1, 1))
                .distributor("CPFL PAULISTA")
                .cnpjDistributor("12345678000190")
                .validFrom(LocalDate.of(2024, 1, 1))
                .tusdValue(new BigDecimal("1.8500"))
                .teValue(new BigDecimal("0.5000"))
                .flagAdditionalValue(new BigDecimal("30.00"))  // R$ 30.00 per 100kWh
                .build();

        // When
        BigDecimal totalCost = dto.calculateTotalCost(new BigDecimal("100"));

        // Then - (1.85 + 0.50) * 100 + (30/100) * 100 = 235 + 30 = 265.00
        assertThat(totalCost).isEqualByComparingTo(new BigDecimal("265.0000"));
    }

    @Test
    @DisplayName("Should return zero for zero consumption")
    void shouldReturnZeroForZeroConsumption() {
        // Given
        TariffResponseDTO dto = TariffResponseDTO.builder()
                .id(UUID.randomUUID())
                .generationDate(LocalDate.of(2024, 1, 1))
                .distributor("CPFL PAULISTA")
                .cnpjDistributor("12345678000190")
                .validFrom(LocalDate.of(2024, 1, 1))
                .tusdValue(new BigDecimal("1.8500"))
                .teValue(new BigDecimal("0.5000"))
                .build();

        // When
        BigDecimal totalCost = dto.calculateTotalCost(BigDecimal.ZERO);

        // Then
        assertThat(totalCost).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should return zero for null consumption")
    void shouldReturnZeroForNullConsumption() {
        // Given
        TariffResponseDTO dto = TariffResponseDTO.builder()
                .id(UUID.randomUUID())
                .generationDate(LocalDate.of(2024, 1, 1))
                .distributor("CPFL PAULISTA")
                .cnpjDistributor("12345678000190")
                .validFrom(LocalDate.of(2024, 1, 1))
                .tusdValue(new BigDecimal("1.8500"))
                .teValue(new BigDecimal("0.5000"))
                .build();

        // When
        BigDecimal totalCost = dto.calculateTotalCost(null);

        // Then
        assertThat(totalCost).isEqualByComparingTo(BigDecimal.ZERO);
    }

    // ========== GETVALIDITYPERIOD METHOD TESTS ==========

    @Test
    @DisplayName("Should return validity period with both dates")
    void shouldReturnValidityPeriodWithBothDates() {
        // Given
        TariffResponseDTO dto = TariffResponseDTO.builder()
                .id(UUID.randomUUID())
                .generationDate(LocalDate.of(2024, 1, 1))
                .distributor("CPFL PAULISTA")
                .cnpjDistributor("12345678000190")
                .validFrom(LocalDate.of(2024, 1, 1))
                .validUntil(LocalDate.of(2024, 12, 31))
                .tusdValue(new BigDecimal("1.5000"))
                .teValue(new BigDecimal("0.4000"))
                .build();

        // When
        String period = dto.getValidityPeriod();

        // Then
        assertThat(period).isEqualTo("2024-01-01 to 2024-12-31");
    }

    @Test
    @DisplayName("Should return validity period without end date")
    void shouldReturnValidityPeriodWithoutEndDate() {
        // Given
        TariffResponseDTO dto = TariffResponseDTO.builder()
                .id(UUID.randomUUID())
                .generationDate(LocalDate.of(2024, 1, 1))
                .distributor("CPFL PAULISTA")
                .cnpjDistributor("12345678000190")
                .validFrom(LocalDate.of(2024, 1, 1))
                .validUntil(null)
                .tusdValue(new BigDecimal("1.5000"))
                .teValue(new BigDecimal("0.4000"))
                .build();

        // When
        String period = dto.getValidityPeriod();

        // Then
        assertThat(period).isEqualTo("From 2024-01-01 onwards");
    }

    @Test
    @DisplayName("Should return N/A when valid from is null")
    void shouldReturnNAWhenValidFromIsNull() {
        // Given
        TariffResponseDTO dto = TariffResponseDTO.builder()
                .id(UUID.randomUUID())
                .generationDate(LocalDate.of(2024, 1, 1))
                .distributor("CPFL PAULISTA")
                .cnpjDistributor("12345678000190")
                .validFrom(null)
                .tusdValue(new BigDecimal("1.5000"))
                .teValue(new BigDecimal("0.4000"))
                .build();

        // When
        String period = dto.getValidityPeriod();

        // Then
        assertThat(period).isEqualTo("N/A");
    }

    // ========== COMPLETE OBJECT TESTS ==========

    @Test
    @DisplayName("Should create complete DTO and test all utility methods")
    void shouldCreateCompleteDtoAndTestAllUtilityMethods() {
        // Given
        LocalDate today = LocalDate.now();
        TariffResponseDTO dto = TariffResponseDTO.builder()
                .id(UUID.randomUUID())
                .generationDate(LocalDate.of(2024, 1, 1))
                .descriptionReh("RESOLUÇÃO HOMOLOGATÓRIA Nº 0.937")
                .distributor("CPFL PAULISTA")
                .cnpjDistributor("12345678000190")
                .validFrom(today.minusMonths(1))
                .validUntil(today.plusMonths(1))
                .tariffBaseDesc("Tarifa de Aplicação")
                .subgroup("A2")
                .tariffModality("Azul")
                .tusdValue(new BigDecimal("1.8500"))
                .teValue(new BigDecimal("0.5000"))
                .activatedFlagName("Verde")
                .flagAdditionalValue(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Then - Test all utility methods
        assertThat(dto.getMaskedCnpj()).isEqualTo("12.345.678/0001-90");
        assertThat(dto.isCurrentlyValid()).isTrue();
        assertThat(dto.hasFlag()).isTrue();
        assertThat(dto.getTotalTariffPerKwh()).isEqualByComparingTo(new BigDecimal("2.3500"));

        BigDecimal cost = dto.calculateTotalCost(new BigDecimal("100"));
        assertThat(cost).isEqualByComparingTo(new BigDecimal("235.0000"));

        String period = dto.getValidityPeriod();
        assertThat(period).contains("to");
    }
}

