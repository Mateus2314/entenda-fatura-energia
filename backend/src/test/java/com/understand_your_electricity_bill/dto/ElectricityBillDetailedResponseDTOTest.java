package com.understand_your_electricity_bill.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ElectricityBillDetailedResponseDTO Tests")
class ElectricityBillDetailedResponseDTOTest {

    // ========== BUILDER PATTERN TESTS ==========

    @Test
    @DisplayName("Should build DTO using builder pattern")
    void shouldBuildDtoUsingBuilderPattern() {
        // Given & When
        ElectricityBillDetailedResponseDTO dto = ElectricityBillDetailedResponseDTO.builder()
                .id(UUID.randomUUID())
                .client(createMockClientSummary())
                .tariff(createMockTariffResponse())
                .referenceMonth(LocalDate.of(2024, 1, 1))
                .dueDate(LocalDate.of(2024, 1, 20))
                .totalAmount(new BigDecimal("385.50"))
                .consumptionKwh(new BigDecimal("350.00"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isNotNull();
        assertThat(dto.client()).isNotNull();
        assertThat(dto.tariff()).isNotNull();
    }

    // ========== HAS CONSULTANT TESTS ==========

    @Test
    @DisplayName("Should return true when consultant is assigned")
    void shouldReturnTrueWhenConsultantIsAssigned() {
        // Given
        ElectricityBillDetailedResponseDTO dto = ElectricityBillDetailedResponseDTO.builder()
                .consultant(createMockConsultantSummary())
                .build();

        // When & Then
        assertThat(dto.hasConsultant()).isTrue();
    }

    @Test
    @DisplayName("Should return false when consultant is not assigned")
    void shouldReturnFalseWhenConsultantIsNotAssigned() {
        // Given
        ElectricityBillDetailedResponseDTO dto = ElectricityBillDetailedResponseDTO.builder()
                .consultant(null)
                .build();

        // When & Then
        assertThat(dto.hasConsultant()).isFalse();
    }

    // ========== HAS ANALYSIS TESTS ==========

    @Test
    @DisplayName("Should return true when analysis exists")
    void shouldReturnTrueWhenAnalysisExists() {
        // Given
        ElectricityBillDetailedResponseDTO.AnalysisSummaryDTO analysis =
                new ElectricityBillDetailedResponseDTO.AnalysisSummaryDTO(
                        UUID.randomUUID(),
                        new BigDecimal("350.00"),
                        new BigDecimal("1.1014"),
                        new BigDecimal("5.5"),
                        "Reduce consumption during peak hours"
                );
        ElectricityBillDetailedResponseDTO dto = ElectricityBillDetailedResponseDTO.builder()
                .analysis(analysis)
                .build();

        // When & Then
        assertThat(dto.hasAnalysis()).isTrue();
    }

    @Test
    @DisplayName("Should return false when analysis does not exist")
    void shouldReturnFalseWhenAnalysisDoesNotExist() {
        // Given
        ElectricityBillDetailedResponseDTO dto = ElectricityBillDetailedResponseDTO.builder()
                .analysis(null)
                .build();

        // When & Then
        assertThat(dto.hasAnalysis()).isFalse();
    }

    // ========== HAS ITEMS TESTS ==========

    @Test
    @DisplayName("Should return true when items exist")
    void shouldReturnTrueWhenItemsExist() {
        // Given
        List<ElectricityBillDetailedResponseDTO.BillItemSummaryDTO> items = new ArrayList<>();
        items.add(new ElectricityBillDetailedResponseDTO.BillItemSummaryDTO(
                UUID.randomUUID(), "CONSUMPTION", "Energy consumption",
                new BigDecimal("350"), new BigDecimal("0.77"), new BigDecimal("269.50")
        ));

        ElectricityBillDetailedResponseDTO dto = ElectricityBillDetailedResponseDTO.builder()
                .items(items)
                .build();

        // When & Then
        assertThat(dto.hasItems()).isTrue();
    }

    @Test
    @DisplayName("Should return false when items are empty")
    void shouldReturnFalseWhenItemsAreEmpty() {
        // Given
        ElectricityBillDetailedResponseDTO dto = ElectricityBillDetailedResponseDTO.builder()
                .items(new ArrayList<>())
                .build();

        // When & Then
        assertThat(dto.hasItems()).isFalse();
    }

    @Test
    @DisplayName("Should return false when items are null")
    void shouldReturnFalseWhenItemsAreNull() {
        // Given
        ElectricityBillDetailedResponseDTO dto = ElectricityBillDetailedResponseDTO.builder()
                .items(null)
                .build();

        // When & Then
        assertThat(dto.hasItems()).isFalse();
    }

    // ========== ITEMS COUNT TESTS ==========

    @Test
    @DisplayName("Should return correct items count")
    void shouldReturnCorrectItemsCount() {
        // Given
        List<ElectricityBillDetailedResponseDTO.BillItemSummaryDTO> items = new ArrayList<>();
        items.add(createMockBillItem());
        items.add(createMockBillItem());
        items.add(createMockBillItem());

        ElectricityBillDetailedResponseDTO dto = ElectricityBillDetailedResponseDTO.builder()
                .items(items)
                .build();

        // When & Then
        assertThat(dto.getItemsCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should return zero when items are null")
    void shouldReturnZeroWhenItemsAreNull() {
        // Given
        ElectricityBillDetailedResponseDTO dto = ElectricityBillDetailedResponseDTO.builder()
                .items(null)
                .build();

        // When & Then
        assertThat(dto.getItemsCount()).isEqualTo(0);
    }

    // ========== ADD ITEM TESTS ==========

    @Test
    @DisplayName("Should add item using builder")
    void shouldAddItemUsingBuilder() {
        // Given & When
        ElectricityBillDetailedResponseDTO dto = ElectricityBillDetailedResponseDTO.builder()
                .addItem(createMockBillItem())
                .addItem(createMockBillItem())
                .build();

        // Then
        assertThat(dto.items()).hasSize(2);
    }

    // ========== COST PER KWH TESTS ==========

    @Test
    @DisplayName("Should calculate cost per kWh correctly")
    void shouldCalculateCostPerKwhCorrectly() {
        // Given
        ElectricityBillDetailedResponseDTO dto = ElectricityBillDetailedResponseDTO.builder()
                .totalAmount(new BigDecimal("385.50"))
                .consumptionKwh(new BigDecimal("350.00"))
                .build();

        // When
        BigDecimal costPerKwh = dto.getCostPerKwh();

        // Then
        assertThat(costPerKwh).isEqualByComparingTo(new BigDecimal("1.1014"));
    }

    // ========== IS OVERDUE TESTS ==========

    @Test
    @DisplayName("Should return true when bill is overdue")
    void shouldReturnTrueWhenBillIsOverdue() {
        // Given
        ElectricityBillDetailedResponseDTO dto = ElectricityBillDetailedResponseDTO.builder()
                .dueDate(LocalDate.now().minusDays(10))
                .build();

        // When & Then
        assertThat(dto.isOverdue()).isTrue();
    }

    @Test
    @DisplayName("Should return false when bill is not overdue")
    void shouldReturnFalseWhenBillIsNotOverdue() {
        // Given
        ElectricityBillDetailedResponseDTO dto = ElectricityBillDetailedResponseDTO.builder()
                .dueDate(LocalDate.now().plusDays(10))
                .build();

        // When & Then
        assertThat(dto.isOverdue()).isFalse();
    }

    // ========== IMMUTABLE LIST TESTS ==========

    @Test
    @DisplayName("Should return immutable list of items")
    void shouldReturnImmutableListOfItems() {
        // Given
        List<ElectricityBillDetailedResponseDTO.BillItemSummaryDTO> items = new ArrayList<>();
        items.add(createMockBillItem());

        ElectricityBillDetailedResponseDTO dto = ElectricityBillDetailedResponseDTO.builder()
                .items(items)
                .build();

        // When & Then
        assertThat(dto.items()).isUnmodifiable();
    }

    // ========== HELPER METHODS ==========

    private ClientSummaryDTO createMockClientSummary() {
        return new ClientSummaryDTO(
                UUID.randomUUID(),
                "João Silva",
                "client@email.com",
                "12345678901",
                "São Paulo",
                "SP",
                com.understand_your_electricity_bill.model.enums.UserStatus.ACTIVE,
                LocalDate.of(2024, 1, 15)
        );
    }

    private ConsultantSummaryDTO createMockConsultantSummary() {
        return new ConsultantSummaryDTO(
                UUID.randomUUID(),
                "Maria Santos",
                "Energy Consulting Ltda",
                "consultant@company.com",
                "12345678000190",
                "Rio de Janeiro",
                "RJ",
                com.understand_your_electricity_bill.model.enums.UserStatus.ACTIVE,
                LocalDate.of(2024, 1, 10),
                5 // managedClientsCount
        );
    }

    private TariffResponseDTO createMockTariffResponse() {
        return TariffResponseDTO.builder()
                .id(UUID.randomUUID())
                .distributor("CPFL PAULISTA")
                .tariffModality("Convencional")
                .tusdValue(new BigDecimal("1.85"))
                .teValue(new BigDecimal("0.50"))
                .build();
    }

    private ElectricityBillDetailedResponseDTO.BillItemSummaryDTO createMockBillItem() {
        return new ElectricityBillDetailedResponseDTO.BillItemSummaryDTO(
                UUID.randomUUID(),
                "CONSUMPTION",
                "Energy consumption",
                new BigDecimal("350"),
                new BigDecimal("0.77"),
                new BigDecimal("269.50")
        );
    }
}

