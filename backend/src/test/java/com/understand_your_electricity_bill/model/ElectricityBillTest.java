package com.understand_your_electricity_bill.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ElectricityBillTest {

    private ValidatorFactory factory;
    private Validator validator;
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
        client.setAddress("Rua Teste, 123");
        client.setCpf("12345678901");

        // Setup Tariff
        tariff = new Tariff();
        tariff.setGenerationDate(LocalDate.now());
        tariff.setDistributor("CPFL JAGUARI");
        tariff.setCnpjDistributor("12345678000190");
        tariff.setValidFrom(LocalDate.now().minusYears(1));
        tariff.setTusdValue(new BigDecimal("0.5000"));
        tariff.setTeValue(new BigDecimal("0.3500"));

        // Setup ElectricityBill
        bill = new ElectricityBill();
    }

    @Test
    @DisplayName("Should create ElectricityBill with all required fields")
    void shouldCreateElectricityBillWithRequiredFields() {
        bill.setClient(client);
        bill.setTariff(tariff);
        bill.setReferenceMonth(LocalDate.of(2024, 1, 1));
        bill.setDueDate(LocalDate.of(2024, 2, 10));
        bill.setConsumptionKwh(new BigDecimal("350.00"));
        bill.setTotalAmount(new BigDecimal("298.00"));

        assertNotNull(bill);
        assertEquals(client, bill.getClient());
        assertEquals(tariff, bill.getTariff());
        assertEquals(LocalDate.of(2024, 1, 1), bill.getReferenceMonth());
        assertEquals(LocalDate.of(2024, 2, 10), bill.getDueDate());
        assertEquals(new BigDecimal("350.00"), bill.getConsumptionKwh());
        assertEquals(new BigDecimal("298.00"), bill.getTotalAmount());
    }

    @Test
    @DisplayName("Should not allow null client")
    void shouldNotAllowNullClient() {
        bill.setTariff(tariff);
        bill.setReferenceMonth(LocalDate.of(2024, 1, 1));
        bill.setDueDate(LocalDate.of(2024, 2, 10));
        bill.setConsumptionKwh(new BigDecimal("350.00"));
        bill.setTotalAmount(new BigDecimal("298.00"));

        Set<ConstraintViolation<ElectricityBill>> violations = validator.validate(bill);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("client")));
    }

    @Test
    @DisplayName("Should not allow null tariff")
    void shouldNotAllowNullTariff() {
        bill.setClient(client);
        bill.setReferenceMonth(LocalDate.of(2024, 1, 1));
        bill.setDueDate(LocalDate.of(2024, 2, 10));
        bill.setConsumptionKwh(new BigDecimal("350.00"));
        bill.setTotalAmount(new BigDecimal("298.00"));

        Set<ConstraintViolation<ElectricityBill>> violations = validator.validate(bill);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("tariff")));
    }

    @Test
    @DisplayName("Should not allow null reference month")
    void shouldNotAllowNullReferenceMonth() {
        bill.setClient(client);
        bill.setTariff(tariff);
        bill.setDueDate(LocalDate.of(2024, 2, 10));
        bill.setConsumptionKwh(new BigDecimal("350.00"));
        bill.setTotalAmount(new BigDecimal("298.00"));

        Set<ConstraintViolation<ElectricityBill>> violations = validator.validate(bill);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("referenceMonth")));
    }

    @Test
    @DisplayName("Should not allow null due date")
    void shouldNotAllowNullDueDate() {
        bill.setClient(client);
        bill.setTariff(tariff);
        bill.setReferenceMonth(LocalDate.of(2024, 1, 1));
        bill.setConsumptionKwh(new BigDecimal("350.00"));
        bill.setTotalAmount(new BigDecimal("298.00"));

        Set<ConstraintViolation<ElectricityBill>> violations = validator.validate(bill);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("dueDate")));
    }

    @Test
    @DisplayName("Should not allow null consumption")
    void shouldNotAllowNullConsumption() {
        bill.setClient(client);
        bill.setTariff(tariff);
        bill.setReferenceMonth(LocalDate.of(2024, 1, 1));
        bill.setDueDate(LocalDate.of(2024, 2, 10));
        bill.setTotalAmount(new BigDecimal("298.00"));

        Set<ConstraintViolation<ElectricityBill>> violations = validator.validate(bill);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("consumptionKwh")));
    }

    @Test
    @DisplayName("Should not allow negative consumption")
    void shouldNotAllowNegativeConsumption() {
        bill.setClient(client);
        bill.setTariff(tariff);
        bill.setReferenceMonth(LocalDate.of(2024, 1, 1));
        bill.setDueDate(LocalDate.of(2024, 2, 10));
        bill.setConsumptionKwh(new BigDecimal("-100.00"));
        bill.setTotalAmount(new BigDecimal("298.00"));

        Set<ConstraintViolation<ElectricityBill>> violations = validator.validate(bill);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("must be greater than or equal to 0")));
    }

    @Test
    @DisplayName("Should not allow null total amount")
    void shouldNotAllowNullTotalAmount() {
        bill.setClient(client);
        bill.setTariff(tariff);
        bill.setReferenceMonth(LocalDate.of(2024, 1, 1));
        bill.setDueDate(LocalDate.of(2024, 2, 10));
        bill.setConsumptionKwh(new BigDecimal("350.00"));

        Set<ConstraintViolation<ElectricityBill>> violations = validator.validate(bill);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("totalAmount")));
    }

    @Test
    @DisplayName("Should not allow negative total amount")
    void shouldNotAllowNegativeTotalAmount() {
        bill.setClient(client);
        bill.setTariff(tariff);
        bill.setReferenceMonth(LocalDate.of(2024, 1, 1));
        bill.setDueDate(LocalDate.of(2024, 2, 10));
        bill.setConsumptionKwh(new BigDecimal("350.00"));
        bill.setTotalAmount(new BigDecimal("-298.00"));

        Set<ConstraintViolation<ElectricityBill>> violations = validator.validate(bill);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("must be greater than or equal to 0")));
    }

    @Test
    @DisplayName("Should allow null installation number")
    void shouldAllowNullInstallationNumber() {
        bill.setClient(client);
        bill.setTariff(tariff);
        bill.setReferenceMonth(LocalDate.of(2024, 1, 1));
        bill.setDueDate(LocalDate.of(2024, 2, 10));
        bill.setConsumptionKwh(new BigDecimal("350.00"));
        bill.setTotalAmount(new BigDecimal("298.00"));
        bill.setInstallationNumber(null);

        Set<ConstraintViolation<ElectricityBill>> violations = validator.validate(bill);
        assertTrue(violations.stream()
                .noneMatch(v -> v.getPropertyPath().toString().equals("installationNumber")));
    }

    @Test
    @DisplayName("Should validate installation number max length")
    void shouldValidateInstallationNumberMaxLength() {
        bill.setClient(client);
        bill.setTariff(tariff);
        bill.setReferenceMonth(LocalDate.of(2024, 1, 1));
        bill.setDueDate(LocalDate.of(2024, 2, 10));
        bill.setConsumptionKwh(new BigDecimal("350.00"));
        bill.setTotalAmount(new BigDecimal("298.00"));
        bill.setInstallationNumber("A".repeat(51)); // 51 caracteres

        Set<ConstraintViolation<ElectricityBill>> violations = validator.validate(bill);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("must not exceed 50 characters")));
    }

    @Test
    @DisplayName("Should allow null invoice number")
    void shouldAllowNullInvoiceNumber() {
        bill.setClient(client);
        bill.setTariff(tariff);
        bill.setReferenceMonth(LocalDate.of(2024, 1, 1));
        bill.setDueDate(LocalDate.of(2024, 2, 10));
        bill.setConsumptionKwh(new BigDecimal("350.00"));
        bill.setTotalAmount(new BigDecimal("298.00"));
        bill.setInvoiceNumber(null);

        Set<ConstraintViolation<ElectricityBill>> violations = validator.validate(bill);
        assertTrue(violations.stream()
                .noneMatch(v -> v.getPropertyPath().toString().equals("invoiceNumber")));
    }

    @Test
    @DisplayName("Should set timestamps automatically on create")
    void shouldSetTimestampsAutomaticallyOnCreate() {
        bill.setClient(client);
        bill.setTariff(tariff);
        bill.setReferenceMonth(LocalDate.of(2024, 1, 1));
        bill.setDueDate(LocalDate.of(2024, 2, 10));
        bill.setConsumptionKwh(new BigDecimal("350.00"));
        bill.setTotalAmount(new BigDecimal("298.00"));

        bill.onCreate();

        assertNotNull(bill.getCreatedAt());
        assertNotNull(bill.getUpdatedAt());
        assertEquals(bill.getCreatedAt(), bill.getUpdatedAt());
    }

    @Test
    @DisplayName("Should update timestamp on update")
    void shouldUpdateTimestampOnUpdate() throws InterruptedException {
        bill.setClient(client);
        bill.setTariff(tariff);
        bill.setReferenceMonth(LocalDate.of(2024, 1, 1));
        bill.setDueDate(LocalDate.of(2024, 2, 10));
        bill.setConsumptionKwh(new BigDecimal("350.00"));
        bill.setTotalAmount(new BigDecimal("298.00"));

        bill.onCreate();
        var createdAt = bill.getCreatedAt();

        Thread.sleep(10); // Pequeno delay para garantir diferença no timestamp
        bill.onUpdate();

        assertNotNull(bill.getUpdatedAt());
        assertEquals(createdAt, bill.getCreatedAt()); // createdAt não muda
        assertTrue(bill.getUpdatedAt().isAfter(createdAt)); // updatedAt é posterior
    }

    @Test
    @DisplayName("Should accept zero consumption")
    void shouldAcceptZeroConsumption() {
        bill.setClient(client);
        bill.setTariff(tariff);
        bill.setReferenceMonth(LocalDate.of(2024, 1, 1));
        bill.setDueDate(LocalDate.of(2024, 2, 10));
        bill.setConsumptionKwh(BigDecimal.ZERO);
        bill.setTotalAmount(new BigDecimal("298.00"));

        Set<ConstraintViolation<ElectricityBill>> violations = validator.validate(bill);
        assertTrue(violations.stream()
                .noneMatch(v -> v.getPropertyPath().toString().equals("consumptionKwh")));
    }

    @Test
    @DisplayName("Should accept decimal consumption values")
    void shouldAcceptDecimalConsumptionValues() {
        bill.setClient(client);
        bill.setTariff(tariff);
        bill.setReferenceMonth(LocalDate.of(2024, 1, 1));
        bill.setDueDate(LocalDate.of(2024, 2, 10));
        bill.setConsumptionKwh(new BigDecimal("350.45"));
        bill.setTotalAmount(new BigDecimal("298.38"));

        Set<ConstraintViolation<ElectricityBill>> violations = validator.validate(bill);
        assertTrue(violations.isEmpty());
        assertEquals(new BigDecimal("350.45"), bill.getConsumptionKwh());
    }

    @Test
    @DisplayName("Should maintain relationship with Client")
    void shouldMaintainRelationshipWithClient() {
        bill.setClient(client);
        bill.setTariff(tariff);
        bill.setReferenceMonth(LocalDate.of(2024, 1, 1));
        bill.setDueDate(LocalDate.of(2024, 2, 10));
        bill.setConsumptionKwh(new BigDecimal("350.00"));
        bill.setTotalAmount(new BigDecimal("298.00"));

        assertNotNull(bill.getClient());
        assertEquals("client@example.com", bill.getClient().getEmail());
        assertEquals("12345678901", bill.getClient().getCpf());
    }

    @Test
    @DisplayName("Should maintain relationship with Tariff")
    void shouldMaintainRelationshipWithTariff() {
        bill.setClient(client);
        bill.setTariff(tariff);
        bill.setReferenceMonth(LocalDate.of(2024, 1, 1));
        bill.setDueDate(LocalDate.of(2024, 2, 10));
        bill.setConsumptionKwh(new BigDecimal("350.00"));
        bill.setTotalAmount(new BigDecimal("298.00"));

        assertNotNull(bill.getTariff());
        assertEquals("CPFL JAGUARI", bill.getTariff().getDistributor());
        assertEquals(new BigDecimal("0.5000"), bill.getTariff().getTusdValue());
    }

    @Test
    @DisplayName("Should format reference month correctly")
    void shouldFormatReferenceMonthCorrectly() {
        bill.setReferenceMonth(LocalDate.of(2024, 1, 1));

        assertEquals(LocalDate.of(2024, 1, 1), bill.getReferenceMonth());
        assertEquals(2024, bill.getReferenceMonth().getYear());
        assertEquals(1, bill.getReferenceMonth().getMonthValue());
    }

}
