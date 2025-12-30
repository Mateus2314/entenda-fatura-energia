package com.understand_your_electricity_bill.repository;

import com.understand_your_electricity_bill.model.*;
import com.understand_your_electricity_bill.model.enums.UserStatus;
import com.understand_your_electricity_bill.model.enums.UserType;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = com.understand_your_electricity_bill.understand_your_electricity_bill.UnderstandYourElectricityBillApplication.class)
@ActiveProfiles("test")
@DisplayName("AnalysisRepository Tests")
class AnalysisRepositoryTest {

    // Carrega o .env
    static {
        Dotenv dotenv = Dotenv.configure()
                .directory("../../")
                .ignoreIfMissing()
                .load();

        System.setProperty("DB_URL", dotenv.get("DB_URL"));
        System.setProperty("DB_USER", dotenv.get("DB_USER"));
        System.setProperty("DB_PASS", dotenv.get("DB_PASS"));
    }

    @Autowired
    private AnalysisRepository analysisRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Client testClient1;
    private Client testClient2;
    private Consultant testConsultant;
    private Tariff testTariff;
    private ElectricityBill testBill1;
    private ElectricityBill testBill2;
    private ElectricityBill testBill3;

    @BeforeEach
    void setUp() {
        // Create test clients
        testClient1 = createClient("client1@test.com", "Client One", "11111111111");
        testClient2 = createClient("client2@test.com", "Client Two", "22222222222");

        // Create test consultant
        testConsultant = createConsultant();

        // Create test tariff
        testTariff = createTariff();

        // Create test bills
        testBill1 = createBill(testClient1, testConsultant, testTariff, LocalDate.of(2024, 10, 1),
                new BigDecimal("350.00"), new BigDecimal("400.00"));
        testBill2 = createBill(testClient1, testConsultant, testTariff, LocalDate.of(2024, 11, 1),
                new BigDecimal("400.00"), new BigDecimal("450.00"));
        testBill3 = createBill(testClient2, testConsultant, testTariff, LocalDate.of(2024, 11, 1),
                new BigDecimal("300.00"), new BigDecimal("350.00"));

        entityManager.flush();
        entityManager.clear();
    }

    // ========== HELPER METHODS ==========

    private Client createClient(String email, String name, String cpf) {
        Client client = new Client();
        client.setEmail(email);
        client.setName(name);
        client.setPasswordHash("$2a$10$hashedPassword");
        client.setUserType(UserType.CLIENT);
        client.setStatus(UserStatus.ACTIVE);
        client.setPhone("+5511999999999");
        client.setCpf(cpf);
        client.setAddress("Test Address, 123");
        client.setRegistrationDate(LocalDate.now());
        return entityManager.persistAndFlush(client);
    }

    private Consultant createConsultant() {
        Consultant consultant = new Consultant();
        consultant.setEmail("consultant@test.com");
        consultant.setName("Consultant One");
        consultant.setPasswordHash("$2a$10$hashedPassword");
        consultant.setUserType(UserType.CONSULTANT);
        consultant.setStatus(UserStatus.ACTIVE);
        consultant.setPhone("+5511988888888");
        consultant.setCnpj("11111111000111");
        consultant.setCompany("Energy Consulting");
        consultant.setConsultantName("Consultant One");
        consultant.setAddress("Consultant Address, 456");
        consultant.setRegistrationDate(LocalDate.now());
        return entityManager.persistAndFlush(consultant);
    }

    private Tariff createTariff() {
        Tariff tariff = new Tariff();
        tariff.setGenerationDate(LocalDate.now());
        tariff.setDistributor("CPFL PAULISTA");
        tariff.setCnpjDistributor("12345678000190");
        tariff.setValidFrom(LocalDate.of(2024, 1, 1));
        tariff.setValidUntil(LocalDate.of(2025, 1, 1));
        tariff.setTariffBaseDesc("Tarifa de Aplicação");
        tariff.setSubgroup("B1");
        tariff.setTariffModality("Convencional");
        tariff.setConsumerClass("Residencial");
        tariff.setConsumerSubclass("Residencial");
        tariff.setDetail("Normal");
        tariff.setTariffPostName("Fora Ponta");
        tariff.setTertiaryUnit("kWh");
        tariff.setAccessingAgent("Não se aplica");
        tariff.setTusdValue(new BigDecimal("0.50"));
        tariff.setTeValue(new BigDecimal("0.30"));
        tariff.setCompetenceDate(LocalDate.now());
        tariff.setActivatedFlagName("Verde");
        tariff.setFlagAdditionalValue(BigDecimal.ZERO);
        return entityManager.persistAndFlush(tariff);
    }

    private ElectricityBill createBill(Client client, Consultant consultant, Tariff tariff,
                                       LocalDate referenceMonth, BigDecimal consumption, BigDecimal amount) {
        ElectricityBill bill = new ElectricityBill();
        bill.setClient(client);
        bill.setConsultant(consultant);
        bill.setTariff(tariff);
        bill.setReferenceMonth(referenceMonth);
        bill.setDueDate(referenceMonth.plusDays(20));
        bill.setTotalAmount(amount);
        bill.setConsumptionKwh(consumption);
        bill.setInstallationNumber("INST-" + System.currentTimeMillis());
        bill.setInvoiceNumber("INV-" + System.currentTimeMillis());
        return entityManager.persistAndFlush(bill);
    }

    private Analysis createAnalysis(ElectricityBill bill, BigDecimal avgConsumption,
                                    BigDecimal costPerKwh, BigDecimal comparison, String tips) {
        Analysis analysis = new Analysis();
        analysis.setBill(bill);
        analysis.setAverageConsumption(avgConsumption);
        analysis.setCostPerKwh(costPerKwh);
        analysis.setComparisonPrevMonth(comparison);
        analysis.setSavingsTips(tips);
        return entityManager.persistAndFlush(analysis);
    }

    // ========== BASIC QUERY TESTS ==========

    @Test
    @DisplayName("Should find analysis by bill ID")
    void shouldFindAnalysisByBillId() {
        // Given
        Analysis createdAnalysis = createAnalysis(testBill1,
                new BigDecimal("350.00"), new BigDecimal("1.1428"),
                new BigDecimal("5.5"), "Dica: Reduza o consumo no horário de ponta");

        entityManager.flush();
        entityManager.clear();

        // When
        Optional<Analysis> foundAnalysis = analysisRepository.findByBillId(testBill1.getId());

        // Then
        assertThat(foundAnalysis).isPresent();
        assertThat(foundAnalysis.get().getId()).isEqualTo(createdAnalysis.getId());
        assertThat(foundAnalysis.get().getBill().getId()).isEqualTo(testBill1.getId());
    }

    @Test
    @DisplayName("Should find all analyses by client ID")
    void shouldFindAllAnalysesByClientId() {
        // Given
        createAnalysis(testBill1, new BigDecimal("350.00"), new BigDecimal("1.1428"),
                new BigDecimal("5.5"), "Dica 1");
        createAnalysis(testBill2, new BigDecimal("400.00"), new BigDecimal("1.1250"),
                new BigDecimal("10.0"), "Dica 2");
        createAnalysis(testBill3, new BigDecimal("300.00"), new BigDecimal("1.1666"),
                new BigDecimal("-2.5"), "Dica 3");

        entityManager.flush();
        entityManager.clear();

        // When
        List<Analysis> analyses = analysisRepository.findByClientId(testClient1.getId());

        // Then
        assertThat(analyses).hasSize(2);
        assertThat(analyses).allMatch(a -> a.getBill().getClient().getId().equals(testClient1.getId()));
    }

    @Test
    @DisplayName("Should find all analyses by consultant ID")
    void shouldFindAllAnalysesByConsultantId() {
        // Given
        createAnalysis(testBill1, new BigDecimal("350.00"), new BigDecimal("1.1428"),
                new BigDecimal("5.5"), "Dica 1");
        createAnalysis(testBill2, new BigDecimal("400.00"), new BigDecimal("1.1250"),
                new BigDecimal("10.0"), "Dica 2");
        createAnalysis(testBill3, new BigDecimal("300.00"), new BigDecimal("1.1666"),
                new BigDecimal("-2.5"), "Dica 3");

        entityManager.flush();
        entityManager.clear();

        // When
        List<Analysis> analyses = analysisRepository.findByConsultantId(testConsultant.getId());

        // Then
        assertThat(analyses).hasSize(3);
        assertThat(analyses).allMatch(a -> a.getBill().getConsultant().getId().equals(testConsultant.getId()));
    }

    // ========== SEARCH QUERY TESTS ==========

    @Test
    @DisplayName("Should find analyses with cost per kWh greater than threshold")
    void shouldFindAnalysesWithCostPerKwhGreaterThan() {
        // Given
        createAnalysis(testBill1, new BigDecimal("350.00"), new BigDecimal("1.5000"),
                new BigDecimal("5.5"), "Alta tarifa");
        createAnalysis(testBill2, new BigDecimal("400.00"), new BigDecimal("0.9000"),
                new BigDecimal("10.0"), "Tarifa normal");
        createAnalysis(testBill3, new BigDecimal("300.00"), new BigDecimal("1.8000"),
                new BigDecimal("-2.5"), "Tarifa muito alta");

        entityManager.flush();
        entityManager.clear();

        // When
        List<Analysis> highCostAnalyses = analysisRepository.findByCostPerKwhGreaterThan(
                new BigDecimal("1.2000"));

        // Then
        assertThat(highCostAnalyses).hasSize(2);
        assertThat(highCostAnalyses).allMatch(a ->
                a.getCostPerKwh().compareTo(new BigDecimal("1.2000")) > 0);
    }

    @Test
    @DisplayName("Should find analyses with cost per kWh between range")
    void shouldFindAnalysesWithCostPerKwhBetween() {
        // Given
        createAnalysis(testBill1, new BigDecimal("350.00"), new BigDecimal("0.8000"),
                new BigDecimal("5.5"), "Baixa tarifa");
        createAnalysis(testBill2, new BigDecimal("400.00"), new BigDecimal("1.1000"),
                new BigDecimal("10.0"), "Tarifa média");
        createAnalysis(testBill3, new BigDecimal("300.00"), new BigDecimal("1.5000"),
                new BigDecimal("-2.5"), "Alta tarifa");

        entityManager.flush();
        entityManager.clear();

        // When
        List<Analysis> midRangeAnalyses = analysisRepository.findByCostPerKwhBetween(
                new BigDecimal("1.0000"), new BigDecimal("1.3000"));

        // Then
        assertThat(midRangeAnalyses).hasSize(1);
        assertThat(midRangeAnalyses.get(0).getCostPerKwh()).isBetween(
                new BigDecimal("1.0000"), new BigDecimal("1.3000"));
    }

    @Test
    @DisplayName("Should find analyses with average consumption greater than threshold")
    void shouldFindAnalysesWithAverageConsumptionGreaterThan() {
        // Given
        createAnalysis(testBill1, new BigDecimal("250.00"), new BigDecimal("1.1000"),
                new BigDecimal("5.5"), "Consumo baixo");
        createAnalysis(testBill2, new BigDecimal("450.00"), new BigDecimal("1.1000"),
                new BigDecimal("10.0"), "Consumo alto");
        createAnalysis(testBill3, new BigDecimal("600.00"), new BigDecimal("1.1000"),
                new BigDecimal("-2.5"), "Consumo muito alto");

        entityManager.flush();
        entityManager.clear();

        // When
        List<Analysis> highConsumptionAnalyses = analysisRepository
                .findByAverageConsumptionGreaterThan(new BigDecimal("400.00"));

        // Then
        assertThat(highConsumptionAnalyses).hasSize(2);
        assertThat(highConsumptionAnalyses).allMatch(a ->
                a.getAverageConsumption().compareTo(new BigDecimal("400.00")) > 0);
    }

    @Test
    @DisplayName("Should find analyses with consumption increase")
    void shouldFindAnalysesWithConsumptionIncrease() {
        // Given
        createAnalysis(testBill1, new BigDecimal("350.00"), new BigDecimal("1.1000"),
                new BigDecimal("15.5"), "Aumento significativo");
        createAnalysis(testBill2, new BigDecimal("400.00"), new BigDecimal("1.1000"),
                new BigDecimal("8.2"), "Aumento moderado");
        createAnalysis(testBill3, new BigDecimal("300.00"), new BigDecimal("1.1000"),
                new BigDecimal("-5.0"), "Redução");

        entityManager.flush();
        entityManager.clear();

        // When
        List<Analysis> increaseAnalyses = analysisRepository.findWithConsumptionIncrease();

        // Then
        assertThat(increaseAnalyses).hasSize(2);
        assertThat(increaseAnalyses).allMatch(a ->
                a.getComparisonPrevMonth().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Should find analyses with consumption decrease")
    void shouldFindAnalysesWithConsumptionDecrease() {
        // Given
        createAnalysis(testBill1, new BigDecimal("350.00"), new BigDecimal("1.1000"),
                new BigDecimal("5.5"), "Aumento");
        createAnalysis(testBill2, new BigDecimal("400.00"), new BigDecimal("1.1000"),
                new BigDecimal("-10.0"), "Grande redução");
        createAnalysis(testBill3, new BigDecimal("300.00"), new BigDecimal("1.1000"),
                new BigDecimal("-3.5"), "Pequena redução");

        entityManager.flush();
        entityManager.clear();

        // When
        List<Analysis> decreaseAnalyses = analysisRepository.findWithConsumptionDecrease();

        // Then
        assertThat(decreaseAnalyses).hasSize(2);
        assertThat(decreaseAnalyses).allMatch(a ->
                a.getComparisonPrevMonth().compareTo(BigDecimal.ZERO) < 0);
        // Should be ordered by comparison ASC (most negative first)
        assertThat(decreaseAnalyses.get(0).getComparisonPrevMonth())
                .isLessThan(decreaseAnalyses.get(1).getComparisonPrevMonth());
    }


    // ========== STATISTICAL QUERY TESTS ==========

    @Test
    @DisplayName("Should count analyses by client ID")
    void shouldCountAnalysesByClientId() {
        // Given
        createAnalysis(testBill1, new BigDecimal("350.00"), new BigDecimal("1.1000"),
                new BigDecimal("5.5"), "Dica 1");
        createAnalysis(testBill2, new BigDecimal("400.00"), new BigDecimal("1.1000"),
                new BigDecimal("10.0"), "Dica 2");
        createAnalysis(testBill3, new BigDecimal("300.00"), new BigDecimal("1.1000"),
                new BigDecimal("-2.5"), "Dica 3");

        entityManager.flush();
        entityManager.clear();

        // When
        Long count = analysisRepository.countByClientId(testClient1.getId());

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Should count analyses by consultant ID")
    void shouldCountAnalysesByConsultantId() {
        // Given
        createAnalysis(testBill1, new BigDecimal("350.00"), new BigDecimal("1.1000"),
                new BigDecimal("5.5"), "Dica 1");
        createAnalysis(testBill2, new BigDecimal("400.00"), new BigDecimal("1.1000"),
                new BigDecimal("10.0"), "Dica 2");
        createAnalysis(testBill3, new BigDecimal("300.00"), new BigDecimal("1.1000"),
                new BigDecimal("-2.5"), "Dica 3");

        entityManager.flush();
        entityManager.clear();

        // When
        Long count = analysisRepository.countByConsultantId(testConsultant.getId());

        // Then
        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("Should calculate average cost per kWh")
    void shouldCalculateAverageCostPerKwh() {
        // Given
        createAnalysis(testBill1, new BigDecimal("350.00"), new BigDecimal("1.0000"),
                new BigDecimal("5.5"), "Dica 1");
        createAnalysis(testBill2, new BigDecimal("400.00"), new BigDecimal("1.2000"),
                new BigDecimal("10.0"), "Dica 2");
        createAnalysis(testBill3, new BigDecimal("300.00"), new BigDecimal("1.4000"),
                new BigDecimal("-2.5"), "Dica 3");

        entityManager.flush();
        entityManager.clear();

        // When
        BigDecimal average = analysisRepository.calculateAverageCostPerKwh();

        // Then
        // Average of 1.0, 1.2, 1.4 = 1.2
        assertThat(average).isEqualByComparingTo(new BigDecimal("1.2000"));
    }

    @Test
    @DisplayName("Should calculate average consumption")
    void shouldCalculateAverageConsumption() {
        // Given
        createAnalysis(testBill1, new BigDecimal("300.00"), new BigDecimal("1.1000"),
                new BigDecimal("5.5"), "Dica 1");
        createAnalysis(testBill2, new BigDecimal("400.00"), new BigDecimal("1.1000"),
                new BigDecimal("10.0"), "Dica 2");
        createAnalysis(testBill3, new BigDecimal("500.00"), new BigDecimal("1.1000"),
                new BigDecimal("-2.5"), "Dica 3");

        entityManager.flush();
        entityManager.clear();

        // When
        BigDecimal average = analysisRepository.calculateAverageConsumption();

        // Then
        // Average of 300, 400, 500 = 400
        assertThat(average).isEqualByComparingTo(new BigDecimal("400.00"));
    }

    @Test
    @DisplayName("Should calculate average cost per kWh by client")
    void shouldCalculateAverageCostPerKwhByClient() {
        // Given
        createAnalysis(testBill1, new BigDecimal("350.00"), new BigDecimal("1.0000"),
                new BigDecimal("5.5"), "Dica 1");
        createAnalysis(testBill2, new BigDecimal("400.00"), new BigDecimal("1.4000"),
                new BigDecimal("10.0"), "Dica 2");
        createAnalysis(testBill3, new BigDecimal("300.00"), new BigDecimal("2.0000"),
                new BigDecimal("-2.5"), "Dica 3");

        entityManager.flush();
        entityManager.clear();

        // When
        BigDecimal average = analysisRepository.calculateAverageCostPerKwhByClientId(testClient1.getId());

        // Then
        // Average of 1.0, 1.4 = 1.2
        assertThat(average).isEqualByComparingTo(new BigDecimal("1.2000"));
    }

    @Test
    @DisplayName("Should calculate average consumption by client")
    void shouldCalculateAverageConsumptionByClient() {
        // Given
        createAnalysis(testBill1, new BigDecimal("300.00"), new BigDecimal("1.1000"),
                new BigDecimal("5.5"), "Dica 1");
        createAnalysis(testBill2, new BigDecimal("500.00"), new BigDecimal("1.1000"),
                new BigDecimal("10.0"), "Dica 2");
        createAnalysis(testBill3, new BigDecimal("800.00"), new BigDecimal("1.1000"),
                new BigDecimal("-2.5"), "Dica 3");

        entityManager.flush();
        entityManager.clear();

        // When
        BigDecimal average = analysisRepository.calculateAverageConsumptionByClientId(testClient1.getId());

        // Then
        // Average of 300, 500 = 400
        assertThat(average).isEqualByComparingTo(new BigDecimal("400.00"));
    }

    // ========== ADVANCED QUERY TESTS ==========

    @Test
    @DisplayName("Should check if analysis exists for bill")
    void shouldCheckIfAnalysisExistsForBill() {
        // Given
        createAnalysis(testBill1, new BigDecimal("350.00"), new BigDecimal("1.1000"),
                new BigDecimal("5.5"), "Dica 1");

        entityManager.flush();
        entityManager.clear();

        // When
        boolean exists = analysisRepository.existsByBillId(testBill1.getId());
        boolean notExists = analysisRepository.existsByBillId(testBill2.getId());

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should find analyses without PDF report")
    void shouldFindAnalysesWithoutReport() {
        // Given
        createAnalysis(testBill1, new BigDecimal("350.00"), new BigDecimal("1.1000"),
                new BigDecimal("5.5"), "Dica 1"); // No PDF

        Analysis withReport = createAnalysis(testBill2, new BigDecimal("400.00"), new BigDecimal("1.1000"),
                new BigDecimal("10.0"), "Dica 2");
        withReport.setReportPdfUrl("/reports/bill2.pdf");
        entityManager.merge(withReport);

        entityManager.flush();
        entityManager.clear();

        // When
        List<Analysis> withoutReport = analysisRepository.findWithoutReport();

        // Then
        assertThat(withoutReport).hasSize(1);
        assertThat(withoutReport).allMatch(a ->
                a.getReportPdfUrl() == null || a.getReportPdfUrl().isEmpty());
    }

    @Test
    @DisplayName("Should find analyses with PDF report")
    void shouldFindAnalysesWithReport() {
        // Given
        createAnalysis(testBill1, new BigDecimal("350.00"), new BigDecimal("1.1000"),
                new BigDecimal("5.5"), "Dica 1"); // No PDF

        Analysis withReport1 = createAnalysis(testBill2, new BigDecimal("400.00"), new BigDecimal("1.1000"),
                new BigDecimal("10.0"), "Dica 2");
        withReport1.setReportPdfUrl("/reports/bill2.pdf");
        entityManager.merge(withReport1);

        Analysis withReport2 = createAnalysis(testBill3, new BigDecimal("300.00"), new BigDecimal("1.1000"),
                new BigDecimal("-2.5"), "Dica 3");
        withReport2.setReportPdfUrl("/reports/bill3.pdf");
        entityManager.merge(withReport2);

        entityManager.flush();
        entityManager.clear();

        // When
        List<Analysis> withReport = analysisRepository.findWithReport();

        // Then
        assertThat(withReport).hasSize(2);
        assertThat(withReport).allMatch(a ->
                a.getReportPdfUrl() != null && !a.getReportPdfUrl().isEmpty());
    }

    @Test
    @DisplayName("Should find most recent analysis for client")
    void shouldFindMostRecentAnalysisForClient() {
        // Given
        createAnalysis(testBill1, new BigDecimal("350.00"), new BigDecimal("1.1000"),
                new BigDecimal("5.5"), "Análise antiga");

        // Wait to ensure different timestamps
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Analysis mostRecent = createAnalysis(testBill2, new BigDecimal("400.00"), new BigDecimal("1.1000"),
                new BigDecimal("10.0"), "Análise recente");

        entityManager.flush();
        entityManager.clear();

        // When
        Optional<Analysis> found = analysisRepository.findMostRecentByClientId(testClient1.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(mostRecent.getId());
    }

    @Test
    @DisplayName("Should find analyses by savings tips containing keyword")
    void shouldFindAnalysesBySavingsTipsContaining() {
        // Given
        createAnalysis(testBill1, new BigDecimal("350.00"), new BigDecimal("1.1000"),
                new BigDecimal("5.5"), "Reduza o consumo no horário de ponta");
        createAnalysis(testBill2, new BigDecimal("400.00"), new BigDecimal("1.1000"),
                new BigDecimal("10.0"), "Desligue aparelhos em stand-by");
        createAnalysis(testBill3, new BigDecimal("300.00"), new BigDecimal("1.1000"),
                new BigDecimal("-2.5"), "Use lâmpadas LED para economizar");

        entityManager.flush();
        entityManager.clear();

        // When
        List<Analysis> analyses = analysisRepository.findBySavingsTipsContaining("consumo");

        // Then
        assertThat(analyses).hasSize(1);
        assertThat(analyses.get(0).getSavingsTips()).containsIgnoringCase("consumo");
    }

    @Test
    @DisplayName("Should find analyses created after date")
    void shouldFindAnalysesCreatedAfterDate() {
        // Given
        LocalDateTime cutoffDate = LocalDateTime.now();

        // Wait to ensure creation after cutoff
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        createAnalysis(testBill1, new BigDecimal("350.00"), new BigDecimal("1.1000"),
                new BigDecimal("5.5"), "Nova análise");

        entityManager.flush();
        entityManager.clear();

        // When
        List<Analysis> analyses = analysisRepository.findByCreatedAtAfter(cutoffDate);

        // Then
        assertThat(analyses).hasSize(1);
        assertThat(analyses).allMatch(a -> a.getCreatedAt().isAfter(cutoffDate));
    }

    @Test
    @DisplayName("Should find all analyses ordered by cost per kWh descending")
    void shouldFindAllOrderedByCostPerKwhDesc() {
        // Given
        createAnalysis(testBill1, new BigDecimal("350.00"), new BigDecimal("1.0000"),
                new BigDecimal("5.5"), "Tarifa baixa");
        createAnalysis(testBill2, new BigDecimal("400.00"), new BigDecimal("1.5000"),
                new BigDecimal("10.0"), "Tarifa alta");
        createAnalysis(testBill3, new BigDecimal("300.00"), new BigDecimal("1.2000"),
                new BigDecimal("-2.5"), "Tarifa média");

        entityManager.flush();
        entityManager.clear();

        // When
        List<Analysis> analyses = analysisRepository.findAllOrderByCostPerKwhDesc();

        // Then
        assertThat(analyses).hasSize(3);
        assertThat(analyses.get(0).getCostPerKwh()).isGreaterThan(analyses.get(1).getCostPerKwh());
        assertThat(analyses.get(1).getCostPerKwh()).isGreaterThan(analyses.get(2).getCostPerKwh());
    }

    @Test
    @DisplayName("Should find top consumption increases")
    void shouldFindTopConsumptionIncreases() {
        // Given
        createAnalysis(testBill1, new BigDecimal("350.00"), new BigDecimal("1.1000"),
                new BigDecimal("15.5"), "Grande aumento");
        createAnalysis(testBill2, new BigDecimal("400.00"), new BigDecimal("1.1000"),
                new BigDecimal("5.0"), "Pequeno aumento");

        ElectricityBill bill4 = createBill(testClient2, testConsultant, testTariff,
                LocalDate.of(2024, 12, 1), new BigDecimal("450.00"), new BigDecimal("500.00"));
        createAnalysis(bill4, new BigDecimal("450.00"), new BigDecimal("1.1000"),
                new BigDecimal("25.0"), "Maior aumento");

        entityManager.flush();
        entityManager.clear();

        // When
        List<Analysis> topIncreases = analysisRepository.findTopConsumptionIncreases(2);

        // Then
        assertThat(topIncreases).hasSize(2);
        assertThat(topIncreases.get(0).getComparisonPrevMonth())
                .isGreaterThan(topIncreases.get(1).getComparisonPrevMonth());
    }

    @Test
    @DisplayName("Should find top consumption decreases")
    void shouldFindTopConsumptionDecreases() {
        // Given
        createAnalysis(testBill1, new BigDecimal("350.00"), new BigDecimal("1.1000"),
                new BigDecimal("-15.5"), "Grande redução");
        createAnalysis(testBill2, new BigDecimal("400.00"), new BigDecimal("1.1000"),
                new BigDecimal("-5.0"), "Pequena redução");

        ElectricityBill bill4 = createBill(testClient2, testConsultant, testTariff,
                LocalDate.of(2024, 12, 1), new BigDecimal("250.00"), new BigDecimal("300.00"));
        createAnalysis(bill4, new BigDecimal("250.00"), new BigDecimal("1.1000"),
                new BigDecimal("-25.0"), "Maior redução");

        entityManager.flush();
        entityManager.clear();

        // When
        List<Analysis> topDecreases = analysisRepository.findTopConsumptionDecreases(2);

        // Then
        assertThat(topDecreases).hasSize(2);
        assertThat(topDecreases.get(0).getComparisonPrevMonth())
                .isLessThan(topDecreases.get(1).getComparisonPrevMonth());
    }

    // ========== EDGE CASE TESTS ==========

    @Test
    @DisplayName("Should return empty when no analysis found for bill")
    void shouldReturnEmptyWhenNoAnalysisFound() {
        // When
        Optional<Analysis> found = analysisRepository.findByBillId(testBill1.getId());

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should return zero when calculating average with no analyses")
    void shouldReturnZeroWhenCalculatingAverageWithNoAnalyses() {
        // When
        BigDecimal avgCost = analysisRepository.calculateAverageCostPerKwh();
        BigDecimal avgConsumption = analysisRepository.calculateAverageConsumption();

        // Then
        assertThat(avgCost).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(avgConsumption).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should return empty list when no analyses match criteria")
    void shouldReturnEmptyListWhenNoAnalysesMatchCriteria() {
        // Given
        createAnalysis(testBill1, new BigDecimal("350.00"), new BigDecimal("1.1000"),
                new BigDecimal("5.5"), "Análise normal");

        entityManager.flush();
        entityManager.clear();

        // When
        List<Analysis> highCost = analysisRepository.findByCostPerKwhGreaterThan(new BigDecimal("2.0000"));
        List<Analysis> increases = analysisRepository.findWithConsumptionIncrease();

        // Then
        assertThat(highCost).isEmpty();
        assertThat(increases).isNotEmpty(); // This one should have data
    }
}

