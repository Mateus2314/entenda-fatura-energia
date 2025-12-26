package com.understand_your_electricity_bill.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BillItemTest {

    private ValidatorFactory factory;
    private Validator validator;
    private BillItem billItem;
    private ElectricityBill bill;
    private Client client;
    private Tariff tariff;

    @BeforeEach
    void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        // Setup Client
        client = new Client();
        client.setEmail("client@example.com");
        client.setPasswordHash("hashedPassword123");
        client.setName("Test Client");
        client.setAddress("Test Address, 123");
        client.setCpf("12345678901");

        // Setup Tariff
        tariff = new Tariff();
        tariff.setGenerationDate(java.time.LocalDate.now());
        tariff.setDistributor("CPFL JAGUARI");
        tariff.setCnpjDistributor("12345678000190");
        tariff.setValidFrom(java.time.LocalDate.now().minusYears(1));
        tariff.setTusdValue(new BigDecimal("0.5000"));
        tariff.setTeValue(new BigDecimal("0.3500"));

        // Setup ElectricityBill
        bill = new ElectricityBill();
        bill.setClient(client);
        bill.setTariff(tariff);
        bill.setReferenceMonth(java.time.LocalDate.of(2024, 1, 1));
        bill.setDueDate(java.time.LocalDate.of(2024, 2, 10));
        bill.setConsumptionKwh(new BigDecimal("350.00"));
        bill.setTotalAmount(new BigDecimal("298.00"));

        // Setup BillItem
        billItem = new BillItem();
    }

    @Test
    @DisplayName("Should create BillItem with all required fields")
    void shouldCreateBillItemWithRequiredFields() {
        billItem.setBill(bill);
        billItem.setItemType("OFF_PEAK_CONSUMPTION");
        billItem.setAmount(new BigDecimal("150.00"));

        assertNotNull(billItem);
        assertEquals(bill, billItem.getBill());
        assertEquals("OFF_PEAK_CONSUMPTION", billItem.getItemType());
        assertEquals(new BigDecimal("150.00"), billItem.getAmount());
    }

    @Test
    @DisplayName("Should not allow null bill")
    void shouldNotAllowNullBill() {
        billItem.setItemType("OFF_PEAK_CONSUMPTION");
        billItem.setAmount(new BigDecimal("150.00"));

        Set<ConstraintViolation<BillItem>> violations = validator.validate(billItem);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("bill")));
    }

    @Test
    @DisplayName("Should not allow null item type")
    void shouldNotAllowNullItemType() {
        billItem.setBill(bill);
        billItem.setAmount(new BigDecimal("150.00"));

        Set<ConstraintViolation<BillItem>> violations = validator.validate(billItem);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("itemType")));
    }

    @Test
    @DisplayName("Should not allow blank item type")
    void shouldNotAllowBlankItemType() {
        billItem.setBill(bill);
        billItem.setItemType("");
        billItem.setAmount(new BigDecimal("150.00"));

        Set<ConstraintViolation<BillItem>> violations = validator.validate(billItem);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("itemType")));
    }

    @Test
    @DisplayName("Should not allow null amount")
    void shouldNotAllowNullAmount() {
        billItem.setBill(bill);
        billItem.setItemType("OFF_PEAK_CONSUMPTION");

        Set<ConstraintViolation<BillItem>> violations = validator.validate(billItem);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("amount")));
    }

    @Test
    @DisplayName("Should not allow negative amount")
    void shouldNotAllowNegativeAmount() {
        billItem.setBill(bill);
        billItem.setItemType("OFF_PEAK_CONSUMPTION");
        billItem.setAmount(new BigDecimal("-50.00"));

        Set<ConstraintViolation<BillItem>> violations = validator.validate(billItem);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("must be greater than or equal to 0")));
    }

    @Test
    @DisplayName("Should allow zero amount")
    void shouldAllowZeroAmount() {
        billItem.setBill(bill);
        billItem.setItemType("DISCOUNT");
        billItem.setAmount(BigDecimal.ZERO);

        Set<ConstraintViolation<BillItem>> violations = validator.validate(billItem);
        assertTrue(violations.stream()
                .noneMatch(v -> v.getPropertyPath().toString().equals("amount")));
    }

    @Test
    @DisplayName("Should allow null description")
    void shouldAllowNullDescription() {
        billItem.setBill(bill);
        billItem.setItemType("OFF_PEAK_CONSUMPTION");
        billItem.setAmount(new BigDecimal("150.00"));
        billItem.setDescription(null);

        Set<ConstraintViolation<BillItem>> violations = validator.validate(billItem);
        assertTrue(violations.stream()
                .noneMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @Test
    @DisplayName("Should validate description max length")
    void shouldValidateDescriptionMaxLength() {
        billItem.setBill(bill);
        billItem.setItemType("OFF_PEAK_CONSUMPTION");
        billItem.setAmount(new BigDecimal("150.00"));
        billItem.setDescription("A".repeat(256)); // 256 characters

        Set<ConstraintViolation<BillItem>> violations = validator.validate(billItem);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("must not exceed 255 characters")));
    }

    @Test
    @DisplayName("Should allow null quantity")
    void shouldAllowNullQuantity() {
        billItem.setBill(bill);
        billItem.setItemType("OFF_PEAK_CONSUMPTION");
        billItem.setAmount(new BigDecimal("150.00"));
        billItem.setQuantity(null);

        Set<ConstraintViolation<BillItem>> violations = validator.validate(billItem);
        assertTrue(violations.stream()
                .noneMatch(v -> v.getPropertyPath().toString().equals("quantity")));
    }

    @Test
    @DisplayName("Should not allow negative quantity")
    void shouldNotAllowNegativeQuantity() {
        billItem.setBill(bill);
        billItem.setItemType("OFF_PEAK_CONSUMPTION");
        billItem.setAmount(new BigDecimal("150.00"));
        billItem.setQuantity(new BigDecimal("-100.00"));

        Set<ConstraintViolation<BillItem>> violations = validator.validate(billItem);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("must be non-negative")));
    }

    @Test
    @DisplayName("Should allow null unit price")
    void shouldAllowNullUnitPrice() {
        billItem.setBill(bill);
        billItem.setItemType("OFF_PEAK_CONSUMPTION");
        billItem.setAmount(new BigDecimal("150.00"));
        billItem.setUnitPrice(null);

        Set<ConstraintViolation<BillItem>> violations = validator.validate(billItem);
        assertTrue(violations.stream()
                .noneMatch(v -> v.getPropertyPath().toString().equals("unitPrice")));
    }

    @Test
    @DisplayName("Should not allow negative unit price")
    void shouldNotAllowNegativeUnitPrice() {
        billItem.setBill(bill);
        billItem.setItemType("OFF_PEAK_CONSUMPTION");
        billItem.setAmount(new BigDecimal("150.00"));
        billItem.setUnitPrice(new BigDecimal("-1.50"));

        Set<ConstraintViolation<BillItem>> violations = validator.validate(billItem);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("must be non-negative")));
    }

    @Test
    @DisplayName("Should validate item type max length")
    void shouldValidateItemTypeMaxLength() {
        billItem.setBill(bill);
        billItem.setItemType("A".repeat(51)); // 51 characters
        billItem.setAmount(new BigDecimal("150.00"));

        Set<ConstraintViolation<BillItem>> violations = validator.validate(billItem);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("must not exceed 50 characters")));
    }

    @Test
    @DisplayName("Should set timestamp automatically on create")
    void shouldSetTimestampAutomaticallyOnCreate() {
        billItem.setBill(bill);
        billItem.setItemType("OFF_PEAK_CONSUMPTION");
        billItem.setAmount(new BigDecimal("150.00"));

        billItem.onCreate();

        assertNotNull(billItem.getCreatedAt());
    }

    @Test
    @DisplayName("Should maintain relationship with ElectricityBill")
    void shouldMaintainRelationshipWithElectricityBill() {
        billItem.setBill(bill);
        billItem.setItemType("OFF_PEAK_CONSUMPTION");
        billItem.setAmount(new BigDecimal("150.00"));

        assertNotNull(billItem.getBill());
        assertEquals(bill, billItem.getBill());
    }

    @Test
    @DisplayName("Should accept common item types")
    void shouldAcceptCommonItemTypes() {
        String[] commonTypes = {
                "OFF_PEAK_CONSUMPTION",
                "PEAK_CONSUMPTION",
                "TUSD_CHARGE",
                "TE_CHARGE",
                "FLAG_CHARGE",
                "ICMS_TAX",
                "PIS_TAX",
                "COFINS_TAX",
                "PUBLIC_LIGHTING",
                "DISCOUNT",
                "CREDIT"
        };

        for (String type : commonTypes) {
            billItem.setBill(bill);
            billItem.setItemType(type);
            billItem.setAmount(new BigDecimal("50.00"));

            Set<ConstraintViolation<BillItem>> violations = validator.validate(billItem);
            assertTrue(violations.isEmpty(), "Failed for type: " + type);
        }
    }

    @Test
    @DisplayName("Should accept decimal values for quantity and unit price")
    void shouldAcceptDecimalValuesForQuantityAndUnitPrice() {
        billItem.setBill(bill);
        billItem.setItemType("OFF_PEAK_CONSUMPTION");
        billItem.setQuantity(new BigDecimal("350.75"));
        billItem.setUnitPrice(new BigDecimal("0.8545"));
        billItem.setAmount(new BigDecimal("299.45"));

        Set<ConstraintViolation<BillItem>> violations = validator.validate(billItem);
        assertTrue(violations.isEmpty());
        assertEquals(new BigDecimal("350.75"), billItem.getQuantity());
        assertEquals(new BigDecimal("0.8545"), billItem.getUnitPrice());
    }

    @Test
    @DisplayName("Should accept complete bill item with all fields")
    void shouldAcceptCompleteBillItemWithAllFields() {
        billItem.setBill(bill);
        billItem.setItemType("OFF_PEAK_CONSUMPTION");
        billItem.setDescription("Off-peak consumption from 23:00 to 06:00");
        billItem.setQuantity(new BigDecimal("200.00"));
        billItem.setUnitPrice(new BigDecimal("0.75"));
        billItem.setAmount(new BigDecimal("150.00"));

        Set<ConstraintViolation<BillItem>> violations = validator.validate(billItem);
        assertTrue(violations.isEmpty());

        assertNotNull(billItem.getBill());
        assertEquals("OFF_PEAK_CONSUMPTION", billItem.getItemType());
        assertEquals("Off-peak consumption from 23:00 to 06:00", billItem.getDescription());
        assertEquals(new BigDecimal("200.00"), billItem.getQuantity());
        assertEquals(new BigDecimal("0.75"), billItem.getUnitPrice());
        assertEquals(new BigDecimal("150.00"), billItem.getAmount());
    }
}

