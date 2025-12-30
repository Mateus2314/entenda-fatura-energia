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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = com.understand_your_electricity_bill.understand_your_electricity_bill.UnderstandYourElectricityBillApplication.class)
@ActiveProfiles("test")
@DisplayName("BillItemRepository Tests")
class BillItemRepositoryTest {

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
    private BillItemRepository billItemRepository;

    @Autowired
    private TestEntityManager entityManager;

    private ElectricityBill testBill1;
    private ElectricityBill testBill2;
    private Client testClient;
    private Tariff testTariff;

    @BeforeEach
    void setUp() {
        // Create test client
        testClient = createClient("client@test.com", "Test Client", "12345678901");

        // Create test tariff
        testTariff = createTariff("CPFL PAULISTA", "Convencional", LocalDate.of(2024, 1, 1));

        // Create test bills
        testBill1 = createBill(testClient, testTariff, LocalDate.of(2024, 11, 1),
                new BigDecimal("350.00"), new BigDecimal("400.00"));
        testBill2 = createBill(testClient, testTariff, LocalDate.of(2024, 12, 1),
                new BigDecimal("400.00"), new BigDecimal("450.00"));

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

    private ElectricityBill createBill(Client client, Tariff tariff, LocalDate referenceMonth,
                                       BigDecimal consumption, BigDecimal amount) {
        ElectricityBill bill = new ElectricityBill();
        bill.setClient(client);
        bill.setTariff(tariff);
        bill.setReferenceMonth(referenceMonth);
        bill.setDueDate(referenceMonth.plusDays(20));
        bill.setTotalAmount(amount);
        bill.setConsumptionKwh(consumption);
        bill.setInstallationNumber("INST-" + System.currentTimeMillis());
        bill.setInvoiceNumber("INV-" + System.currentTimeMillis());
        return entityManager.persistAndFlush(bill);
    }

    private BillItem createBillItem(ElectricityBill bill, String itemType, String description,
                                    BigDecimal quantity, BigDecimal unitPrice, BigDecimal amount) {
        BillItem item = new BillItem();
        item.setBill(bill);
        item.setItemType(itemType);
        item.setDescription(description);
        item.setQuantity(quantity);
        item.setUnitPrice(unitPrice);
        item.setAmount(amount);
        return entityManager.persistAndFlush(item);
    }

    // ========== BASIC QUERY TESTS ==========

    @Test
    @DisplayName("Should find all items by bill ID")
    void shouldFindAllItemsByBillId() {
        // Given
        createBillItem(testBill1, "OFF_PEAK_CONSUMPTION", "Consumo Fora Ponta - 350 kWh",
                new BigDecimal("350.00"), new BigDecimal("0.80"), new BigDecimal("280.00"));
        createBillItem(testBill1, "ICMS_TAX", "ICMS 18%",
                null, null, new BigDecimal("50.40"));
        createBillItem(testBill1, "PUBLIC_LIGHTING", "Contrib. Iluminação Pública",
                null, null, new BigDecimal("35.00"));

        createBillItem(testBill2, "OFF_PEAK_CONSUMPTION", "Consumo Fora Ponta - 400 kWh",
                new BigDecimal("400.00"), new BigDecimal("0.85"), new BigDecimal("340.00"));

        entityManager.flush();
        entityManager.clear();

        // When
        List<BillItem> items = billItemRepository.findByBillId(testBill1.getId());

        // Then
        assertThat(items).hasSize(3);
        assertThat(items).allMatch(item -> item.getBill().getId().equals(testBill1.getId()));
    }

    @Test
    @DisplayName("Should find items by bill entity")
    void shouldFindItemsByBillEntity() {
        // Given
        createBillItem(testBill1, "OFF_PEAK_CONSUMPTION", "Consumo",
                new BigDecimal("350.00"), new BigDecimal("0.80"), new BigDecimal("280.00"));
        createBillItem(testBill1, "ICMS_TAX", "ICMS",
                null, null, new BigDecimal("50.40"));

        entityManager.flush();
        entityManager.clear();

        // When
        ElectricityBill bill = entityManager.find(ElectricityBill.class, testBill1.getId());
        List<BillItem> items = billItemRepository.findByBill(bill);

        // Then
        assertThat(items).hasSize(2);
    }

    @Test
    @DisplayName("Should find items by item type")
    void shouldFindItemsByItemType() {
        // Given
        createBillItem(testBill1, "OFF_PEAK_CONSUMPTION", "Consumo Fora Ponta - Bill 1",
                new BigDecimal("350.00"), new BigDecimal("0.80"), new BigDecimal("280.00"));
        createBillItem(testBill2, "OFF_PEAK_CONSUMPTION", "Consumo Fora Ponta - Bill 2",
                new BigDecimal("400.00"), new BigDecimal("0.85"), new BigDecimal("340.00"));
        createBillItem(testBill1, "ICMS_TAX", "ICMS",
                null, null, new BigDecimal("50.40"));

        entityManager.flush();
        entityManager.clear();

        // When
        List<BillItem> consumptionItems = billItemRepository.findByItemType("OFF_PEAK_CONSUMPTION");

        // Then
        assertThat(consumptionItems).hasSize(2);
        assertThat(consumptionItems).allMatch(item -> item.getItemType().equals("OFF_PEAK_CONSUMPTION"));
    }

    @Test
    @DisplayName("Should find items by bill ID and item type")
    void shouldFindItemsByBillIdAndItemType() {
        // Given
        createBillItem(testBill1, "OFF_PEAK_CONSUMPTION", "Consumo Fora Ponta",
                new BigDecimal("350.00"), new BigDecimal("0.80"), new BigDecimal("280.00"));
        createBillItem(testBill1, "PEAK_CONSUMPTION", "Consumo Ponta",
                new BigDecimal("50.00"), new BigDecimal("1.20"), new BigDecimal("60.00"));
        createBillItem(testBill1, "ICMS_TAX", "ICMS",
                null, null, new BigDecimal("50.40"));

        entityManager.flush();
        entityManager.clear();

        // When
        List<BillItem> items = billItemRepository.findByBillIdAndItemType(
                testBill1.getId(), "OFF_PEAK_CONSUMPTION");

        // Then
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getItemType()).isEqualTo("OFF_PEAK_CONSUMPTION");
        assertThat(items.get(0).getBill().getId()).isEqualTo(testBill1.getId());
    }

    // ========== SEARCH QUERY TESTS ==========

    @Test
    @DisplayName("Should find items by description containing text")
    void shouldFindItemsByDescriptionContaining() {
        // Given
        createBillItem(testBill1, "OFF_PEAK_CONSUMPTION", "Consumo de energia elétrica fora ponta",
                new BigDecimal("350.00"), new BigDecimal("0.80"), new BigDecimal("280.00"));
        createBillItem(testBill1, "ICMS_TAX", "Imposto ICMS sobre consumo",
                null, null, new BigDecimal("50.40"));
        createBillItem(testBill2, "PUBLIC_LIGHTING", "Contrib. Iluminação Pública",
                null, null, new BigDecimal("35.00"));

        entityManager.flush();
        entityManager.clear();

        // When
        List<BillItem> items = billItemRepository.findByDescriptionContainingIgnoreCase("consumo");

        // Then
        assertThat(items).hasSize(2);
        assertThat(items).allMatch(item ->
                item.getDescription().toLowerCase().contains("consumo"));
    }

    @Test
    @DisplayName("Should find items with amount greater than threshold")
    void shouldFindItemsWithAmountGreaterThan() {
        // Given
        createBillItem(testBill1, "OFF_PEAK_CONSUMPTION", "Consumo Fora Ponta",
                new BigDecimal("350.00"), new BigDecimal("0.80"), new BigDecimal("280.00"));
        createBillItem(testBill1, "ICMS_TAX", "ICMS",
                null, null, new BigDecimal("50.40"));
        createBillItem(testBill1, "PUBLIC_LIGHTING", "Contrib. Iluminação",
                null, null, new BigDecimal("35.00"));

        entityManager.flush();
        entityManager.clear();

        // When
        List<BillItem> items = billItemRepository.findByAmountGreaterThan(new BigDecimal("100.00"));

        // Then
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getAmount()).isGreaterThan(new BigDecimal("100.00"));
    }

    @Test
    @DisplayName("Should find items with amount between range")
    void shouldFindItemsWithAmountBetween() {
        // Given
        createBillItem(testBill1, "OFF_PEAK_CONSUMPTION", "Consumo",
                new BigDecimal("350.00"), new BigDecimal("0.80"), new BigDecimal("280.00"));
        createBillItem(testBill1, "ICMS_TAX", "ICMS",
                null, null, new BigDecimal("50.40"));
        createBillItem(testBill1, "PUBLIC_LIGHTING", "Iluminação",
                null, null, new BigDecimal("35.00"));

        entityManager.flush();
        entityManager.clear();

        // When
        List<BillItem> items = billItemRepository.findByAmountBetween(
                new BigDecimal("40.00"), new BigDecimal("60.00"));

        // Then
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getAmount()).isBetween(
                new BigDecimal("40.00"), new BigDecimal("60.00"));
    }

    // ========== STATISTICAL QUERY TESTS ==========

    @Test
    @DisplayName("Should count items by bill ID")
    void shouldCountItemsByBillId() {
        // Given
        createBillItem(testBill1, "OFF_PEAK_CONSUMPTION", "Consumo",
                new BigDecimal("350.00"), new BigDecimal("0.80"), new BigDecimal("280.00"));
        createBillItem(testBill1, "ICMS_TAX", "ICMS",
                null, null, new BigDecimal("50.40"));
        createBillItem(testBill1, "PUBLIC_LIGHTING", "Iluminação",
                null, null, new BigDecimal("35.00"));

        entityManager.flush();
        entityManager.clear();

        // When
        Long count = billItemRepository.countByBillId(testBill1.getId());

        // Then
        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("Should count items by item type")
    void shouldCountItemsByItemType() {
        // Given
        createBillItem(testBill1, "OFF_PEAK_CONSUMPTION", "Consumo Bill 1",
                new BigDecimal("350.00"), new BigDecimal("0.80"), new BigDecimal("280.00"));
        createBillItem(testBill2, "OFF_PEAK_CONSUMPTION", "Consumo Bill 2",
                new BigDecimal("400.00"), new BigDecimal("0.85"), new BigDecimal("340.00"));
        createBillItem(testBill1, "ICMS_TAX", "ICMS",
                null, null, new BigDecimal("50.40"));

        entityManager.flush();
        entityManager.clear();

        // When
        Long count = billItemRepository.countByItemType("OFF_PEAK_CONSUMPTION");

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Should calculate total amount by bill ID")
    void shouldCalculateTotalAmountByBillId() {
        // Given
        createBillItem(testBill1, "OFF_PEAK_CONSUMPTION", "Consumo",
                new BigDecimal("350.00"), new BigDecimal("0.80"), new BigDecimal("280.00"));
        createBillItem(testBill1, "ICMS_TAX", "ICMS",
                null, null, new BigDecimal("50.40"));
        createBillItem(testBill1, "PUBLIC_LIGHTING", "Iluminação",
                null, null, new BigDecimal("35.00"));

        entityManager.flush();
        entityManager.clear();

        // When
        BigDecimal total = billItemRepository.sumAmountByBillId(testBill1.getId());

        // Then
        assertThat(total).isEqualByComparingTo(new BigDecimal("365.40"));
    }

    @Test
    @DisplayName("Should calculate total amount by bill ID and item type")
    void shouldCalculateTotalAmountByBillIdAndItemType() {
        // Given
        createBillItem(testBill1, "OFF_PEAK_CONSUMPTION", "Consumo Fora Ponta",
                new BigDecimal("300.00"), new BigDecimal("0.80"), new BigDecimal("240.00"));
        createBillItem(testBill1, "PEAK_CONSUMPTION", "Consumo Ponta",
                new BigDecimal("50.00"), new BigDecimal("1.20"), new BigDecimal("60.00"));
        createBillItem(testBill1, "ICMS_TAX", "ICMS",
                null, null, new BigDecimal("50.40"));

        entityManager.flush();
        entityManager.clear();

        // When
        BigDecimal consumptionTotal = billItemRepository.sumAmountByBillIdAndItemType(
                testBill1.getId(), "OFF_PEAK_CONSUMPTION");

        // Then
        assertThat(consumptionTotal).isEqualByComparingTo(new BigDecimal("240.00"));
    }

    @Test
    @DisplayName("Should calculate average amount by item type")
    void shouldCalculateAverageAmountByItemType() {
        // Given
        createBillItem(testBill1, "PUBLIC_LIGHTING", "Iluminação Bill 1",
                null, null, new BigDecimal("30.00"));
        createBillItem(testBill2, "PUBLIC_LIGHTING", "Iluminação Bill 2",
                null, null, new BigDecimal("40.00"));
        createBillItem(testBill1, "ICMS_TAX", "ICMS",
                null, null, new BigDecimal("50.00"));

        entityManager.flush();
        entityManager.clear();

        // When
        BigDecimal average = billItemRepository.avgAmountByItemType("PUBLIC_LIGHTING");

        // Then
        assertThat(average).isEqualByComparingTo(new BigDecimal("35.00"));
    }

    // ========== ADVANCED QUERY TESTS ==========

    @Test
    @DisplayName("Should find all distinct item types")
    void shouldFindDistinctItemTypes() {
        // Given
        createBillItem(testBill1, "OFF_PEAK_CONSUMPTION", "Consumo",
                new BigDecimal("350.00"), new BigDecimal("0.80"), new BigDecimal("280.00"));
        createBillItem(testBill1, "ICMS_TAX", "ICMS",
                null, null, new BigDecimal("50.40"));
        createBillItem(testBill2, "OFF_PEAK_CONSUMPTION", "Consumo 2",
                new BigDecimal("400.00"), new BigDecimal("0.85"), new BigDecimal("340.00"));
        createBillItem(testBill2, "PUBLIC_LIGHTING", "Iluminação",
                null, null, new BigDecimal("35.00"));

        entityManager.flush();
        entityManager.clear();

        // When
        List<String> types = billItemRepository.findDistinctItemTypes();

        // Then
        assertThat(types).hasSize(3);
        assertThat(types).contains("OFF_PEAK_CONSUMPTION", "ICMS_TAX", "PUBLIC_LIGHTING");
    }

    @Test
    @DisplayName("Should find distinct item types by bill ID")
    void shouldFindDistinctItemTypesByBillId() {
        // Given
        createBillItem(testBill1, "OFF_PEAK_CONSUMPTION", "Consumo Fora Ponta",
                new BigDecimal("300.00"), new BigDecimal("0.80"), new BigDecimal("240.00"));
        createBillItem(testBill1, "PEAK_CONSUMPTION", "Consumo Ponta",
                new BigDecimal("50.00"), new BigDecimal("1.20"), new BigDecimal("60.00"));
        createBillItem(testBill1, "ICMS_TAX", "ICMS",
                null, null, new BigDecimal("50.40"));
        createBillItem(testBill1, "PUBLIC_LIGHTING", "Iluminação",
                null, null, new BigDecimal("35.00"));

        entityManager.flush();
        entityManager.clear();

        // When
        List<String> types = billItemRepository.findDistinctItemTypesByBillId(testBill1.getId());

        // Then
        assertThat(types).hasSize(4);
        assertThat(types).contains("OFF_PEAK_CONSUMPTION", "PEAK_CONSUMPTION", "ICMS_TAX", "PUBLIC_LIGHTING");
    }

    @Test
    @DisplayName("Should check if bill has items of specific type")
    void shouldCheckIfBillHasItemsOfType() {
        // Given
        createBillItem(testBill1, "OFF_PEAK_CONSUMPTION", "Consumo",
                new BigDecimal("350.00"), new BigDecimal("0.80"), new BigDecimal("280.00"));
        createBillItem(testBill1, "ICMS_TAX", "ICMS",
                null, null, new BigDecimal("50.40"));

        entityManager.flush();
        entityManager.clear();

        // When
        boolean hasConsumption = billItemRepository.existsByBillIdAndItemType(
                testBill1.getId(), "OFF_PEAK_CONSUMPTION");
        boolean hasLighting = billItemRepository.existsByBillIdAndItemType(
                testBill1.getId(), "PUBLIC_LIGHTING");

        // Then
        assertThat(hasConsumption).isTrue();
        assertThat(hasLighting).isFalse();
    }

    @Test
    @DisplayName("Should find items without quantity")
    void shouldFindItemsWithoutQuantity() {
        // Given
        createBillItem(testBill1, "OFF_PEAK_CONSUMPTION", "Consumo",
                new BigDecimal("350.00"), new BigDecimal("0.80"), new BigDecimal("280.00"));
        createBillItem(testBill1, "ICMS_TAX", "ICMS",
                null, null, new BigDecimal("50.40"));
        createBillItem(testBill1, "PUBLIC_LIGHTING", "Iluminação",
                BigDecimal.ZERO, null, new BigDecimal("35.00"));

        entityManager.flush();
        entityManager.clear();

        // When
        List<BillItem> items = billItemRepository.findItemsWithoutQuantity();

        // Then
        assertThat(items).hasSize(2);
        assertThat(items).allMatch(item ->
                item.getQuantity() == null || item.getQuantity().compareTo(BigDecimal.ZERO) == 0);
    }

    @Test
    @DisplayName("Should find items without unit price")
    void shouldFindItemsWithoutUnitPrice() {
        // Given
        createBillItem(testBill1, "OFF_PEAK_CONSUMPTION", "Consumo",
                new BigDecimal("350.00"), new BigDecimal("0.80"), new BigDecimal("280.00"));
        createBillItem(testBill1, "ICMS_TAX", "ICMS",
                null, null, new BigDecimal("50.40"));
        createBillItem(testBill1, "PUBLIC_LIGHTING", "Iluminação",
                null, BigDecimal.ZERO, new BigDecimal("35.00"));

        entityManager.flush();
        entityManager.clear();

        // When
        List<BillItem> items = billItemRepository.findItemsWithoutUnitPrice();

        // Then
        assertThat(items).hasSize(2);
        assertThat(items).allMatch(item ->
                item.getUnitPrice() == null || item.getUnitPrice().compareTo(BigDecimal.ZERO) == 0);
    }

    @Test
    @DisplayName("Should find most expensive item in bill")
    void shouldFindMostExpensiveItem() {
        // Given
        createBillItem(testBill1, "OFF_PEAK_CONSUMPTION", "Consumo",
                new BigDecimal("350.00"), new BigDecimal("0.80"), new BigDecimal("280.00"));
        createBillItem(testBill1, "ICMS_TAX", "ICMS",
                null, null, new BigDecimal("50.40"));
        createBillItem(testBill1, "PUBLIC_LIGHTING", "Iluminação",
                null, null, new BigDecimal("35.00"));

        entityManager.flush();
        entityManager.clear();

        // When
        List<BillItem> mostExpensive = billItemRepository.findMostExpensiveItemByBillId(testBill1.getId());

        // Then
        assertThat(mostExpensive).hasSize(1);
        assertThat(mostExpensive.get(0).getAmount()).isEqualByComparingTo(new BigDecimal("280.00"));
        assertThat(mostExpensive.get(0).getItemType()).isEqualTo("OFF_PEAK_CONSUMPTION");
    }

    @Test
    @DisplayName("Should find items by bill ID ordered by amount descending")
    void shouldFindItemsByBillIdOrderedByAmount() {
        // Given
        createBillItem(testBill1, "PUBLIC_LIGHTING", "Iluminação",
                null, null, new BigDecimal("35.00"));
        createBillItem(testBill1, "OFF_PEAK_CONSUMPTION", "Consumo",
                new BigDecimal("350.00"), new BigDecimal("0.80"), new BigDecimal("280.00"));
        createBillItem(testBill1, "ICMS_TAX", "ICMS",
                null, null, new BigDecimal("50.40"));

        entityManager.flush();
        entityManager.clear();

        // When
        List<BillItem> items = billItemRepository.findByBillIdOrderByAmountDesc(testBill1.getId());

        // Then
        assertThat(items).hasSize(3);
        assertThat(items.get(0).getAmount()).isGreaterThan(items.get(1).getAmount());
        assertThat(items.get(1).getAmount()).isGreaterThan(items.get(2).getAmount());
    }

    @Test
    @DisplayName("Should return empty list when no items found for bill")
    void shouldReturnEmptyListWhenNoItemsFound() {
        // When
        List<BillItem> items = billItemRepository.findByBillId(testBill1.getId());

        // Then
        assertThat(items).isEmpty();
    }

    @Test
    @DisplayName("Should return zero when calculating total with no items")
    void shouldReturnZeroWhenCalculatingTotalWithNoItems() {
        // When
        BigDecimal total = billItemRepository.sumAmountByBillId(testBill1.getId());

        // Then
        assertThat(total).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should return zero when calculating average with no items")
    void shouldReturnZeroWhenCalculatingAverageWithNoItems() {
        // When
        BigDecimal average = billItemRepository.avgAmountByItemType("NON_EXISTENT_TYPE");

        // Then
        assertThat(average).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should find items with multiple item types in same bill")
    void shouldFindItemsWithMultipleTypesInSameBill() {
        // Given
        createBillItem(testBill1, "OFF_PEAK_CONSUMPTION", "Consumo Fora Ponta",
                new BigDecimal("300.00"), new BigDecimal("0.80"), new BigDecimal("240.00"));
        createBillItem(testBill1, "PEAK_CONSUMPTION", "Consumo Ponta",
                new BigDecimal("50.00"), new BigDecimal("1.20"), new BigDecimal("60.00"));
        createBillItem(testBill1, "ICMS_TAX", "ICMS",
                null, null, new BigDecimal("50.40"));
        createBillItem(testBill1, "PIS_TAX", "PIS",
                null, null, new BigDecimal("10.20"));
        createBillItem(testBill1, "COFINS_TAX", "COFINS",
                null, null, new BigDecimal("22.50"));

        entityManager.flush();
        entityManager.clear();

        // When
        List<BillItem> allItems = billItemRepository.findByBillId(testBill1.getId());
        List<BillItem> consumptionItems = billItemRepository.findByBillIdAndItemType(
                testBill1.getId(), "OFF_PEAK_CONSUMPTION");
        List<String> distinctTypes = billItemRepository.findDistinctItemTypesByBillId(testBill1.getId());

        // Then
        assertThat(allItems).hasSize(5);
        assertThat(consumptionItems).hasSize(1);
        assertThat(distinctTypes).hasSize(5);
    }
}

