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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = com.understand_your_electricity_bill.understand_your_electricity_bill.UnderstandYourElectricityBillApplication.class)
@ActiveProfiles("test")
@DisplayName("ElectricityBillRepository Tests")
class ElectricityBillRepositoryTest {

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
    private ElectricityBillRepository electricityBillRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Client testClient1;
    private Client testClient2;
    private Consultant testConsultant1;
    private Consultant testConsultant2;
    private Tariff testTariff1;
    private Tariff testTariff2;

    @BeforeEach
    void setUp() {
        // Create test clients
        testClient1 = createClient("client1@test.com", "Client One", "11111111111");
        testClient2 = createClient("client2@test.com", "Client Two", "22222222222");

        // Create test consultants
        testConsultant1 = createConsultant("consultant1@test.com", "Consultant One",
                "11111111000111", "Energy Consulting");
        testConsultant2 = createConsultant("consultant2@test.com", "Consultant Two",
                "22222222000122", "Green Energy");

        // Create test tariffs
        testTariff1 = createTariff("CPFL PAULISTA", "Azul", LocalDate.of(2024, 1, 1));
        testTariff2 = createTariff("LIGHT", "Verde", LocalDate.of(2024, 1, 1));

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

    private Consultant createConsultant(String email, String name, String cnpj, String company) {
        Consultant consultant = new Consultant();
        consultant.setEmail(email);
        consultant.setName(name);
        consultant.setPasswordHash("$2a$10$hashedPassword");
        consultant.setUserType(UserType.CONSULTANT);
        consultant.setStatus(UserStatus.ACTIVE);
        consultant.setPhone("+5511988888888");
        consultant.setCnpj(cnpj);
        consultant.setCompany(company);
        consultant.setConsultantName(name);
        consultant.setAddress("Consultant Address, 456");
        consultant.setRegistrationDate(LocalDate.now());
        return entityManager.persistAndFlush(consultant);
    }

    private Tariff createTariff(String distributor, String tariffModality, LocalDate validFrom) {
        Tariff tariff = new Tariff();
        tariff.setGenerationDate(LocalDate.now());
        tariff.setDistributor(distributor);
        tariff.setCnpjDistributor("12345678000190");
        tariff.setValidFrom(validFrom);
        tariff.setValidUntil(validFrom.plusYears(1));
        tariff.setTariffBaseDesc("Tarifa de Aplicação");
        tariff.setSubgroup("B1");
        tariff.setTariffModality(tariffModality);
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

    // ========== BASIC QUERY TESTS ==========

    @Test
    @DisplayName("Should find all bills by client ID")
    void shouldFindAllBillsByClientId() {
        // Given
        createBill(testClient1, testConsultant1, testTariff1,
                LocalDate.of(2024, 11, 1), new BigDecimal("350"), new BigDecimal("400.00"));
        createBill(testClient1, testConsultant1, testTariff1,
                LocalDate.of(2024, 12, 1), new BigDecimal("400"), new BigDecimal("450.00"));
        createBill(testClient2, testConsultant2, testTariff2,
                LocalDate.of(2024, 11, 1), new BigDecimal("300"), new BigDecimal("350.00"));

        entityManager.flush();
        entityManager.clear();

        // When
        List<ElectricityBill> bills = electricityBillRepository.findByClientId(testClient1.getId());

        // Then
        assertThat(bills).hasSize(2);
        assertThat(bills).allMatch(bill -> bill.getClient().getId().equals(testClient1.getId()));
        assertThat(bills.get(0).getReferenceMonth()).isAfter(bills.get(1).getReferenceMonth());
    }

    @Test
    @DisplayName("Should find bills by client ID with pagination")
    void shouldFindBillsByClientIdWithPagination() {
        // Given
        for (int i = 1; i <= 5; i++) {
            createBill(testClient1, testConsultant1, testTariff1,
                    LocalDate.of(2024, i, 1), new BigDecimal("350"), new BigDecimal("400.00"));
        }

        entityManager.flush();
        entityManager.clear();

        // When
        Pageable pageable = PageRequest.of(0, 3);
        Page<ElectricityBill> billsPage = electricityBillRepository.findByClientId(testClient1.getId(), pageable);

        // Then
        assertThat(billsPage.getContent()).hasSize(3);
        assertThat(billsPage.getTotalElements()).isEqualTo(5);
        assertThat(billsPage.getTotalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should find all bills by consultant ID")
    void shouldFindAllBillsByConsultantId() {
        // Given
        createBill(testClient1, testConsultant1, testTariff1,
                LocalDate.of(2024, 11, 1), new BigDecimal("350"), new BigDecimal("400.00"));
        createBill(testClient2, testConsultant1, testTariff1,
                LocalDate.of(2024, 11, 1), new BigDecimal("300"), new BigDecimal("350.00"));
        createBill(testClient2, testConsultant2, testTariff2,
                LocalDate.of(2024, 11, 1), new BigDecimal("280"), new BigDecimal("320.00"));

        entityManager.flush();
        entityManager.clear();

        // When
        List<ElectricityBill> bills = electricityBillRepository.findByConsultantId(testConsultant1.getId());

        // Then
        assertThat(bills).hasSize(2);
        assertThat(bills).allMatch(bill -> bill.getConsultant().getId().equals(testConsultant1.getId()));
    }

    @Test
    @DisplayName("Should find bills by consultant ID with pagination")
    void shouldFindBillsByConsultantIdWithPagination() {
        // Given
        for (int i = 1; i <= 4; i++) {
            createBill(testClient1, testConsultant1, testTariff1,
                    LocalDate.of(2024, i, 1), new BigDecimal("350"), new BigDecimal("400.00"));
        }

        entityManager.flush();
        entityManager.clear();

        // When
        Pageable pageable = PageRequest.of(0, 2);
        Page<ElectricityBill> billsPage = electricityBillRepository.findByConsultantId(testConsultant1.getId(), pageable);

        // Then
        assertThat(billsPage.getContent()).hasSize(2);
        assertThat(billsPage.getTotalElements()).isEqualTo(4);
        assertThat(billsPage.getTotalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should find all bills by reference month")
    void shouldFindAllBillsByReferenceMonth() {
        // Given
        LocalDate refMonth = LocalDate.of(2024, 11, 1);
        createBill(testClient1, testConsultant1, testTariff1, refMonth,
                new BigDecimal("350"), new BigDecimal("400.00"));
        createBill(testClient2, testConsultant2, testTariff2, refMonth,
                new BigDecimal("300"), new BigDecimal("350.00"));
        createBill(testClient1, testConsultant1, testTariff1,
                LocalDate.of(2024, 12, 1), new BigDecimal("400"), new BigDecimal("450.00"));

        entityManager.flush();
        entityManager.clear();

        // When
        List<ElectricityBill> bills = electricityBillRepository.findByReferenceMonth(refMonth);

        // Then
        assertThat(bills).hasSize(2);
        assertThat(bills).allMatch(bill -> bill.getReferenceMonth().equals(refMonth));
    }

    @Test
    @DisplayName("Should find bill by client ID and reference month")
    void shouldFindBillByClientIdAndReferenceMonth() {
        // Given
        LocalDate refMonth = LocalDate.of(2024, 11, 1);
        ElectricityBill createdBill = createBill(testClient1, testConsultant1, testTariff1, refMonth,
                new BigDecimal("350"), new BigDecimal("400.00"));

        entityManager.flush();
        entityManager.clear();

        // When
        Optional<ElectricityBill> foundBill = electricityBillRepository
                .findByClientIdAndReferenceMonth(testClient1.getId(), refMonth);

        // Then
        assertThat(foundBill).isPresent();
        assertThat(foundBill.get().getId()).isEqualTo(createdBill.getId());
        assertThat(foundBill.get().getClient().getId()).isEqualTo(testClient1.getId());
        assertThat(foundBill.get().getReferenceMonth()).isEqualTo(refMonth);
    }

    // ========== RANGE QUERY TESTS ==========

    @Test
    @DisplayName("Should find bills by client ID within date range")
    void shouldFindBillsByClientIdWithinDateRange() {
        // Given
        createBill(testClient1, testConsultant1, testTariff1,
                LocalDate.of(2024, 9, 1), new BigDecimal("300"), new BigDecimal("350.00"));
        createBill(testClient1, testConsultant1, testTariff1,
                LocalDate.of(2024, 10, 1), new BigDecimal("350"), new BigDecimal("400.00"));
        createBill(testClient1, testConsultant1, testTariff1,
                LocalDate.of(2024, 11, 1), new BigDecimal("400"), new BigDecimal("450.00"));
        createBill(testClient1, testConsultant1, testTariff1,
                LocalDate.of(2024, 12, 1), new BigDecimal("420"), new BigDecimal("470.00"));

        entityManager.flush();
        entityManager.clear();

        // When
        List<ElectricityBill> bills = electricityBillRepository.findByClientIdAndReferenceMonthBetween(
                testClient1.getId(),
                LocalDate.of(2024, 10, 1),
                LocalDate.of(2024, 11, 30)
        );

        // Then
        assertThat(bills).hasSize(2);
        assertThat(bills).allMatch(bill ->
                !bill.getReferenceMonth().isBefore(LocalDate.of(2024, 10, 1)) &&
                        !bill.getReferenceMonth().isAfter(LocalDate.of(2024, 11, 30))
        );
    }

    @Test
    @DisplayName("Should find bills by consultant ID within date range")
    void shouldFindBillsByConsultantIdWithinDateRange() {
        // Given
        createBill(testClient1, testConsultant1, testTariff1,
                LocalDate.of(2024, 9, 1), new BigDecimal("300"), new BigDecimal("350.00"));
        createBill(testClient1, testConsultant1, testTariff1,
                LocalDate.of(2024, 10, 1), new BigDecimal("350"), new BigDecimal("400.00"));
        createBill(testClient2, testConsultant1, testTariff1,
                LocalDate.of(2024, 11, 1), new BigDecimal("400"), new BigDecimal("450.00"));

        entityManager.flush();
        entityManager.clear();

        // When
        List<ElectricityBill> bills = electricityBillRepository.findByConsultantIdAndReferenceMonthBetween(
                testConsultant1.getId(),
                LocalDate.of(2024, 10, 1),
                LocalDate.of(2024, 11, 30)
        );

        // Then
        assertThat(bills).hasSize(2);
        assertThat(bills).allMatch(bill -> bill.getConsultant().getId().equals(testConsultant1.getId()));
    }

    // ========== STATISTICAL QUERY TESTS ==========

    @Test
    @DisplayName("Should count bills by client ID")
    void shouldCountBillsByClientId() {
        // Given
        createBill(testClient1, testConsultant1, testTariff1,
                LocalDate.of(2024, 10, 1), new BigDecimal("350"), new BigDecimal("400.00"));
        createBill(testClient1, testConsultant1, testTariff1,
                LocalDate.of(2024, 11, 1), new BigDecimal("400"), new BigDecimal("450.00"));
        createBill(testClient2, testConsultant2, testTariff2,
                LocalDate.of(2024, 11, 1), new BigDecimal("300"), new BigDecimal("350.00"));

        entityManager.flush();
        entityManager.clear();

        // When
        Long count = electricityBillRepository.countByClientId(testClient1.getId());

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Should count bills by consultant ID")
    void shouldCountBillsByConsultantId() {
        // Given
        createBill(testClient1, testConsultant1, testTariff1,
                LocalDate.of(2024, 10, 1), new BigDecimal("350"), new BigDecimal("400.00"));
        createBill(testClient2, testConsultant1, testTariff1,
                LocalDate.of(2024, 11, 1), new BigDecimal("400"), new BigDecimal("450.00"));
        createBill(testClient2, testConsultant2, testTariff2,
                LocalDate.of(2024, 11, 1), new BigDecimal("300"), new BigDecimal("350.00"));

        entityManager.flush();
        entityManager.clear();

        // When
        Long count = electricityBillRepository.countByConsultantId(testConsultant1.getId());

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Should calculate total consumption by client ID")
    void shouldCalculateTotalConsumptionByClientId() {
        // Given
        createBill(testClient1, testConsultant1, testTariff1,
                LocalDate.of(2024, 10, 1), new BigDecimal("350.00"), new BigDecimal("400.00"));
        createBill(testClient1, testConsultant1, testTariff1,
                LocalDate.of(2024, 11, 1), new BigDecimal("400.00"), new BigDecimal("450.00"));

        entityManager.flush();
        entityManager.clear();

        // When
        BigDecimal totalConsumption = electricityBillRepository.sumConsumptionByClientId(testClient1.getId());

        // Then
        assertThat(totalConsumption).isEqualByComparingTo(new BigDecimal("750.00"));
    }

    @Test
    @DisplayName("Should calculate average consumption by client ID")
    void shouldCalculateAverageConsumptionByClientId() {
        // Given
        createBill(testClient1, testConsultant1, testTariff1,
                LocalDate.of(2024, 10, 1), new BigDecimal("300.00"), new BigDecimal("350.00"));
        createBill(testClient1, testConsultant1, testTariff1,
                LocalDate.of(2024, 11, 1), new BigDecimal("400.00"), new BigDecimal("450.00"));

        entityManager.flush();
        entityManager.clear();

        // When
        BigDecimal avgConsumption = electricityBillRepository.avgConsumptionByClientId(testClient1.getId());

        // Then
        assertThat(avgConsumption).isEqualByComparingTo(new BigDecimal("350.00"));
    }

    @Test
    @DisplayName("Should calculate total amount by client ID")
    void shouldCalculateTotalAmountByClientId() {
        // Given
        createBill(testClient1, testConsultant1, testTariff1,
                LocalDate.of(2024, 10, 1), new BigDecimal("350"), new BigDecimal("400.00"));
        createBill(testClient1, testConsultant1, testTariff1,
                LocalDate.of(2024, 11, 1), new BigDecimal("400"), new BigDecimal("450.00"));

        entityManager.flush();
        entityManager.clear();

        // When
        BigDecimal totalAmount = electricityBillRepository.sumTotalAmountByClientId(testClient1.getId());

        // Then
        assertThat(totalAmount).isEqualByComparingTo(new BigDecimal("850.00"));
    }

    // ========== SEARCH QUERY TESTS ==========

    @Test
    @DisplayName("Should find bills by installation number")
    void shouldFindBillsByInstallationNumber() {
        // Given
        String installationNumber = "INST-123456789";
        ElectricityBill bill1 = createBill(testClient1, testConsultant1, testTariff1,
                LocalDate.of(2024, 10, 1), new BigDecimal("350"), new BigDecimal("400.00"));
        bill1.setInstallationNumber(installationNumber);
        entityManager.merge(bill1);

        ElectricityBill bill2 = createBill(testClient1, testConsultant1, testTariff1,
                LocalDate.of(2024, 11, 1), new BigDecimal("400"), new BigDecimal("450.00"));
        bill2.setInstallationNumber(installationNumber);
        entityManager.merge(bill2);

        entityManager.flush();
        entityManager.clear();

        // When
        List<ElectricityBill> bills = electricityBillRepository.findByInstallationNumber(installationNumber);

        // Then
        assertThat(bills).hasSize(2);
        assertThat(bills).allMatch(bill -> bill.getInstallationNumber().equals(installationNumber));
    }

    @Test
    @DisplayName("Should find bill by invoice number")
    void shouldFindBillByInvoiceNumber() {
        // Given
        String invoiceNumber = "INV-2024-11-001";
        ElectricityBill createdBill = createBill(testClient1, testConsultant1, testTariff1,
                LocalDate.of(2024, 11, 1), new BigDecimal("350"), new BigDecimal("400.00"));
        createdBill.setInvoiceNumber(invoiceNumber);
        entityManager.merge(createdBill);

        entityManager.flush();
        entityManager.clear();

        // When
        Optional<ElectricityBill> foundBill = electricityBillRepository.findByInvoiceNumber(invoiceNumber);

        // Then
        assertThat(foundBill).isPresent();
        assertThat(foundBill.get().getInvoiceNumber()).isEqualTo(invoiceNumber);
    }

    @Test
    @DisplayName("Should find bills with consumption above threshold")
    void shouldFindBillsWithConsumptionAboveThreshold() {
        // Given
        createBill(testClient1, testConsultant1, testTariff1,
                LocalDate.of(2024, 9, 1), new BigDecimal("250"), new BigDecimal("300.00"));
        createBill(testClient1, testConsultant1, testTariff1,
                LocalDate.of(2024, 10, 1), new BigDecimal("450"), new BigDecimal("500.00"));
        createBill(testClient1, testConsultant1, testTariff1,
                LocalDate.of(2024, 11, 1), new BigDecimal("600"), new BigDecimal("650.00"));

        entityManager.flush();
        entityManager.clear();

        // When
        List<ElectricityBill> bills = electricityBillRepository
                .findByConsumptionKwhGreaterThan(new BigDecimal("400"));

        // Then
        assertThat(bills).hasSize(2);
        assertThat(bills).allMatch(bill -> bill.getConsumptionKwh().compareTo(new BigDecimal("400")) > 0);
    }

    @Test
    @DisplayName("Should find bills without consultant assigned")
    void shouldFindBillsWithoutConsultant() {
        // Given
        createBill(testClient1, testConsultant1, testTariff1,
                LocalDate.of(2024, 10, 1), new BigDecimal("350"), new BigDecimal("400.00"));
        createBill(testClient2, null, testTariff2,
                LocalDate.of(2024, 11, 1), new BigDecimal("400"), new BigDecimal("450.00"));
        createBill(testClient1, null, testTariff1,
                LocalDate.of(2024, 11, 1), new BigDecimal("380"), new BigDecimal("420.00"));

        entityManager.flush();
        entityManager.clear();

        // When
        List<ElectricityBill> bills = electricityBillRepository.findBillsWithoutConsultant();

        // Then
        assertThat(bills).hasSize(2);
        assertThat(bills).allMatch(bill -> bill.getConsultant() == null);
    }

    // ========== ADVANCED QUERY TESTS ==========

    @Test
    @DisplayName("Should find most recent bill for a client")
    void shouldFindMostRecentBillForClient() {
        // Given
        createBill(testClient1, testConsultant1, testTariff1,
                LocalDate.of(2024, 9, 1), new BigDecimal("300"), new BigDecimal("350.00"));
        createBill(testClient1, testConsultant1, testTariff1,
                LocalDate.of(2024, 10, 1), new BigDecimal("350"), new BigDecimal("400.00"));
        ElectricityBill mostRecent = createBill(testClient1, testConsultant1, testTariff1,
                LocalDate.of(2024, 11, 1), new BigDecimal("400"), new BigDecimal("450.00"));

        entityManager.flush();
        entityManager.clear();

        // When
        Optional<ElectricityBill> foundBill = electricityBillRepository.findMostRecentByClientId(testClient1.getId());

        // Then
        assertThat(foundBill).isPresent();
        assertThat(foundBill.get().getId()).isEqualTo(mostRecent.getId());
        assertThat(foundBill.get().getReferenceMonth()).isEqualTo(LocalDate.of(2024, 11, 1));
    }

    @Test
    @DisplayName("Should find overdue bills")
    void shouldFindOverdueBills() {
        // Given
        LocalDate today = LocalDate.now();
        createBill(testClient1, testConsultant1, testTariff1,
                today.minusMonths(2), new BigDecimal("350"), new BigDecimal("400.00"));
        createBill(testClient1, testConsultant1, testTariff1,
                today.minusMonths(1), new BigDecimal("400"), new BigDecimal("450.00"));
        createBill(testClient1, testConsultant1, testTariff1,
                today, new BigDecimal("420"), new BigDecimal("470.00"));

        entityManager.flush();
        entityManager.clear();

        // When
        List<ElectricityBill> overdueBills = electricityBillRepository.findOverdueBills(today);

        // Then
        assertThat(overdueBills).isNotEmpty();
        assertThat(overdueBills).allMatch(bill -> bill.getDueDate().isBefore(today));
    }

    @Test
    @DisplayName("Should find bills by tariff ID")
    void shouldFindBillsByTariffId() {
        // Given
        createBill(testClient1, testConsultant1, testTariff1,
                LocalDate.of(2024, 10, 1), new BigDecimal("350"), new BigDecimal("400.00"));
        createBill(testClient2, testConsultant1, testTariff1,
                LocalDate.of(2024, 11, 1), new BigDecimal("400"), new BigDecimal("450.00"));
        createBill(testClient1, testConsultant2, testTariff2,
                LocalDate.of(2024, 11, 1), new BigDecimal("300"), new BigDecimal("350.00"));

        entityManager.flush();
        entityManager.clear();

        // When
        List<ElectricityBill> bills = electricityBillRepository.findByTariffId(testTariff1.getId());

        // Then
        assertThat(bills).hasSize(2);
        assertThat(bills).allMatch(bill -> bill.getTariff().getId().equals(testTariff1.getId()));
    }

    @Test
    @DisplayName("Should check if bill exists for client and reference month")
    void shouldCheckIfBillExistsForClientAndReferenceMonth() {
        // Given
        LocalDate refMonth = LocalDate.of(2024, 11, 1);
        createBill(testClient1, testConsultant1, testTariff1, refMonth,
                new BigDecimal("350"), new BigDecimal("400.00"));

        entityManager.flush();
        entityManager.clear();

        // When
        boolean exists = electricityBillRepository.existsByClientIdAndReferenceMonth(testClient1.getId(), refMonth);
        boolean notExists = electricityBillRepository.existsByClientIdAndReferenceMonth(
                testClient2.getId(), refMonth);

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should return empty list when no bills found for client")
    void shouldReturnEmptyListWhenNoBillsFoundForClient() {
        // When
        List<ElectricityBill> bills = electricityBillRepository.findByClientId(testClient1.getId());

        // Then
        assertThat(bills).isEmpty();
    }

    @Test
    @DisplayName("Should return empty list when no bills found for consultant")
    void shouldReturnEmptyListWhenNoBillsFoundForConsultant() {
        // When
        List<ElectricityBill> bills = electricityBillRepository.findByConsultantId(testConsultant1.getId());

        // Then
        assertThat(bills).isEmpty();
    }

    @Test
    @DisplayName("Should return zero when summing consumption with no bills")
    void shouldReturnZeroWhenSummingConsumptionWithNoBills() {
        // When
        BigDecimal totalConsumption = electricityBillRepository.sumConsumptionByClientId(testClient1.getId());

        // Then
        assertThat(totalConsumption).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should return zero when averaging consumption with no bills")
    void shouldReturnZeroWhenAveragingConsumptionWithNoBills() {
        // When
        BigDecimal avgConsumption = electricityBillRepository.avgConsumptionByClientId(testClient1.getId());

        // Then
        assertThat(avgConsumption).isEqualByComparingTo(BigDecimal.ZERO);
    }
}

