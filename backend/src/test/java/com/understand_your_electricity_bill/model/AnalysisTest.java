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

class AnalysisTest {

    private ValidatorFactory factory;
    private Validator validator;
    private Analysis analysis;
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

        // Setup Analysis
        analysis = new Analysis();
    }

    @Test
    @DisplayName("Should create Analysis with required field")
    void shouldCreateAnalysisWithRequiredField() {
        analysis.setBill(bill);

        assertNotNull(analysis);
        assertEquals(bill, analysis.getBill());
    }

    @Test
    @DisplayName("Should not allow null bill")
    void shouldNotAllowNullBill() {
        Set<ConstraintViolation<Analysis>> violations = validator.validate(analysis);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("bill")));
    }

    @Test
    @DisplayName("Should allow null average consumption")
    void shouldAllowNullAverageConsumption() {
        analysis.setBill(bill);
        analysis.setAverageConsumption(null);

        Set<ConstraintViolation<Analysis>> violations = validator.validate(analysis);
        assertTrue(violations.stream()
                .noneMatch(v -> v.getPropertyPath().toString().equals("averageConsumption")));
    }

    @Test
    @DisplayName("Should allow null cost per kWh")
    void shouldAllowNullCostPerKwh() {
        analysis.setBill(bill);
        analysis.setCostPerKwh(null);

        Set<ConstraintViolation<Analysis>> violations = validator.validate(analysis);
        assertTrue(violations.stream()
                .noneMatch(v -> v.getPropertyPath().toString().equals("costPerKwh")));
    }

    @Test
    @DisplayName("Should allow null comparison with previous month")
    void shouldAllowNullComparisonPrevMonth() {
        analysis.setBill(bill);
        analysis.setComparisonPrevMonth(null);

        Set<ConstraintViolation<Analysis>> violations = validator.validate(analysis);
        assertTrue(violations.stream()
                .noneMatch(v -> v.getPropertyPath().toString().equals("comparisonPrevMonth")));
    }

    @Test
    @DisplayName("Should allow null savings tips")
    void shouldAllowNullSavingsTips() {
        analysis.setBill(bill);
        analysis.setSavingsTips(null);

        Set<ConstraintViolation<Analysis>> violations = validator.validate(analysis);
        assertTrue(violations.stream()
                .noneMatch(v -> v.getPropertyPath().toString().equals("savingsTips")));
    }

    @Test
    @DisplayName("Should allow null report PDF URL")
    void shouldAllowNullReportPdfUrl() {
        analysis.setBill(bill);
        analysis.setReportPdfUrl(null);

        Set<ConstraintViolation<Analysis>> violations = validator.validate(analysis);
        assertTrue(violations.stream()
                .noneMatch(v -> v.getPropertyPath().toString().equals("reportPdfUrl")));
    }

    @Test
    @DisplayName("Should set timestamp automatically on create")
    void shouldSetTimestampAutomaticallyOnCreate() {
        analysis.setBill(bill);
        analysis.onCreate();

        assertNotNull(analysis.getCreatedAt());
    }

    @Test
    @DisplayName("Should maintain relationship with ElectricityBill")
    void shouldMaintainRelationshipWithElectricityBill() {
        analysis.setBill(bill);

        assertNotNull(analysis.getBill());
        assertEquals(bill, analysis.getBill());
        assertEquals(client, analysis.getBill().getClient());
    }

    @Test
    @DisplayName("Should accept positive average consumption")
    void shouldAcceptPositiveAverageConsumption() {
        analysis.setBill(bill);
        analysis.setAverageConsumption(new BigDecimal("325.50"));

        Set<ConstraintViolation<Analysis>> violations = validator.validate(analysis);
        assertTrue(violations.isEmpty());
        assertEquals(new BigDecimal("325.50"), analysis.getAverageConsumption());
    }

    @Test
    @DisplayName("Should accept positive cost per kWh")
    void shouldAcceptPositiveCostPerKwh() {
        analysis.setBill(bill);
        analysis.setCostPerKwh(new BigDecimal("0.8514"));

        Set<ConstraintViolation<Analysis>> violations = validator.validate(analysis);
        assertTrue(violations.isEmpty());
        assertEquals(new BigDecimal("0.8514"), analysis.getCostPerKwh());
    }

    @Test
    @DisplayName("Should accept positive comparison percentage")
    void shouldAcceptPositiveComparisonPercentage() {
        analysis.setBill(bill);
        analysis.setComparisonPrevMonth(new BigDecimal("15.50"));

        Set<ConstraintViolation<Analysis>> violations = validator.validate(analysis);
        assertTrue(violations.isEmpty());
        assertEquals(new BigDecimal("15.50"), analysis.getComparisonPrevMonth());
    }

    @Test
    @DisplayName("Should accept negative comparison percentage")
    void shouldAcceptNegativeComparisonPercentage() {
        analysis.setBill(bill);
        analysis.setComparisonPrevMonth(new BigDecimal("-8.25"));

        Set<ConstraintViolation<Analysis>> violations = validator.validate(analysis);
        assertTrue(violations.isEmpty());
        assertEquals(new BigDecimal("-8.25"), analysis.getComparisonPrevMonth());
    }

    @Test
    @DisplayName("Should accept long savings tips text")
    void shouldAcceptLongSavingsTipsText() {
        analysis.setBill(bill);
        String longTips = "A".repeat(5000); // 5000 characters
        analysis.setSavingsTips(longTips);

        Set<ConstraintViolation<Analysis>> violations = validator.validate(analysis);
        assertTrue(violations.isEmpty());
        assertEquals(longTips, analysis.getSavingsTips());
    }

    @Test
    @DisplayName("Should accept valid report PDF URL")
    void shouldAcceptValidReportPdfUrl() {
        analysis.setBill(bill);
        analysis.setReportPdfUrl("/reports/2024/01/bill_123456.pdf");

        Set<ConstraintViolation<Analysis>> violations = validator.validate(analysis);
        assertTrue(violations.isEmpty());
        assertEquals("/reports/2024/01/bill_123456.pdf", analysis.getReportPdfUrl());
    }

    @Test
    @DisplayName("Should accept complete analysis with all fields")
    void shouldAcceptCompleteAnalysisWithAllFields() {
        analysis.setBill(bill);
        analysis.setAverageConsumption(new BigDecimal("325.50"));
        analysis.setCostPerKwh(new BigDecimal("0.8514"));
        analysis.setComparisonPrevMonth(new BigDecimal("12.30"));
        analysis.setSavingsTips("Reduce consumption during peak hours. Consider using energy-efficient appliances.");
        analysis.setReportPdfUrl("/reports/2024/01/analysis_123.pdf");

        Set<ConstraintViolation<Analysis>> violations = validator.validate(analysis);
        assertTrue(violations.isEmpty());

        assertEquals(bill, analysis.getBill());
        assertEquals(new BigDecimal("325.50"), analysis.getAverageConsumption());
        assertEquals(new BigDecimal("0.8514"), analysis.getCostPerKwh());
        assertEquals(new BigDecimal("12.30"), analysis.getComparisonPrevMonth());
        assertEquals("Reduce consumption during peak hours. Consider using energy-efficient appliances.",
                     analysis.getSavingsTips());
        assertEquals("/reports/2024/01/analysis_123.pdf", analysis.getReportPdfUrl());
    }

    @Test
    @DisplayName("Should accept zero average consumption")
    void shouldAcceptZeroAverageConsumption() {
        analysis.setBill(bill);
        analysis.setAverageConsumption(BigDecimal.ZERO);

        Set<ConstraintViolation<Analysis>> violations = validator.validate(analysis);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should accept zero cost per kWh")
    void shouldAcceptZeroCostPerKwh() {
        analysis.setBill(bill);
        analysis.setCostPerKwh(BigDecimal.ZERO);

        Set<ConstraintViolation<Analysis>> violations = validator.validate(analysis);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should accept zero comparison percentage")
    void shouldAcceptZeroComparisonPercentage() {
        analysis.setBill(bill);
        analysis.setComparisonPrevMonth(BigDecimal.ZERO);

        Set<ConstraintViolation<Analysis>> violations = validator.validate(analysis);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should accept JSON format for savings tips")
    void shouldAcceptJsonFormatForSavingsTips() {
        analysis.setBill(bill);
        String jsonTips = "{\"tips\":[\"Reduce peak hour usage\",\"Use efficient appliances\"],\"estimated_savings\":\"R$ 50.00\"}";
        analysis.setSavingsTips(jsonTips);

        Set<ConstraintViolation<Analysis>> violations = validator.validate(analysis);
        assertTrue(violations.isEmpty());
        assertTrue(analysis.getSavingsTips().contains("tips"));
    }

    @Test
    @DisplayName("Should maintain one-to-one relationship constraint conceptually")
    void shouldMaintainOneToOneRelationshipConceptually() {
        // This test documents the one-to-one relationship
        // In actual database, bill_id would have UNIQUE constraint
        analysis.setBill(bill);

        assertNotNull(analysis.getBill());
        // Each bill should have at most one analysis
        // This is enforced by UNIQUE constraint on bill_id in database
    }
}

