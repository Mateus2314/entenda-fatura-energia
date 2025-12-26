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

class TariffTest {
    private ValidatorFactory factory;
    private Validator validator;
    private Tariff tariff;

    @BeforeEach
    void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        tariff = new Tariff();
    }

    @Test
    @DisplayName("Should create Tariff with all required fields")
    void shouldCreateTariffWithRequiredFields() {
        tariff.setGenerationDate(LocalDate.of(2024, 1, 15));
        tariff.setDistributor("CPFL JAGUARI");
        tariff.setCnpjDistributor("12345678000190");
        tariff.setValidFrom(LocalDate.of(2024, 1, 1));
        tariff.setTusdValue(new BigDecimal("0.5000"));
        tariff.setTeValue(new BigDecimal("0.3500"));

        assertNotNull(tariff);
        assertEquals(LocalDate.of(2024, 1, 15), tariff.getGenerationDate());
        assertEquals("CPFL JAGUARI", tariff.getDistributor());
        assertEquals("12345678000190", tariff.getCnpjDistributor());
        assertEquals(LocalDate.of(2024, 1, 1), tariff.getValidFrom());
        assertEquals(new BigDecimal("0.5000"), tariff.getTusdValue());
        assertEquals(new BigDecimal("0.3500"), tariff.getTeValue());
    }

    @Test
    @DisplayName("Should not allow null generation date")
    void shouldNotAllowNullGenerationDate() {
        tariff.setDistributor("CPFL JAGUARI");
        tariff.setCnpjDistributor("12345678000190");
        tariff.setValidFrom(LocalDate.of(2024, 1, 1));
        tariff.setTusdValue(new BigDecimal("0.5000"));
        tariff.setTeValue(new BigDecimal("0.3500"));

        Set<ConstraintViolation<Tariff>> violations = validator.validate(tariff);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("generationDate")));
    }

    @Test
    @DisplayName("Should not allow null distributor")
    void shouldNotAllowNullDistributor() {
        tariff.setGenerationDate(LocalDate.of(2024, 1, 15));
        tariff.setCnpjDistributor("12345678000190");
        tariff.setValidFrom(LocalDate.of(2024, 1, 1));
        tariff.setTusdValue(new BigDecimal("0.5000"));
        tariff.setTeValue(new BigDecimal("0.3500"));

        Set<ConstraintViolation<Tariff>> violations = validator.validate(tariff);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("distributor")));
    }

    @Test
    @DisplayName("Should not allow null CNPJ distributor")
    void shouldNotAllowNullCnpjDistributor() {
        tariff.setGenerationDate(LocalDate.of(2024, 1, 15));
        tariff.setDistributor("CPFL JAGUARI");
        tariff.setValidFrom(LocalDate.of(2024, 1, 1));
        tariff.setTusdValue(new BigDecimal("0.5000"));
        tariff.setTeValue(new BigDecimal("0.3500"));

        Set<ConstraintViolation<Tariff>> violations = validator.validate(tariff);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("cnpjDistributor")));
    }

    @Test
    @DisplayName("Should validate CNPJ format - exactly 14 digits")
    void shouldValidateCnpjFormat() {
        tariff.setGenerationDate(LocalDate.of(2024, 1, 15));
        tariff.setDistributor("CPFL JAGUARI");
        tariff.setCnpjDistributor("123456789"); // Invalid - less than 14 digits
        tariff.setValidFrom(LocalDate.of(2024, 1, 1));
        tariff.setTusdValue(new BigDecimal("0.5000"));
        tariff.setTeValue(new BigDecimal("0.3500"));

        Set<ConstraintViolation<Tariff>> violations = validator.validate(tariff);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("cnpjDistributor")));
    }

    @Test
    @DisplayName("Should not allow null valid from date")
    void shouldNotAllowNullValidFrom() {
        tariff.setGenerationDate(LocalDate.of(2024, 1, 15));
        tariff.setDistributor("CPFL JAGUARI");
        tariff.setCnpjDistributor("12345678000190");
        tariff.setTusdValue(new BigDecimal("0.5000"));
        tariff.setTeValue(new BigDecimal("0.3500"));

        Set<ConstraintViolation<Tariff>> violations = validator.validate(tariff);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("validFrom")));
    }

    @Test
    @DisplayName("Should allow null valid until date for current tariffs")
    void shouldAllowNullValidUntil() {
        tariff.setGenerationDate(LocalDate.of(2024, 1, 15));
        tariff.setDistributor("CPFL JAGUARI");
        tariff.setCnpjDistributor("12345678000190");
        tariff.setValidFrom(LocalDate.of(2024, 1, 1));
        tariff.setTusdValue(new BigDecimal("0.5000"));
        tariff.setTeValue(new BigDecimal("0.3500"));
        tariff.setValidUntil(null);

        Set<ConstraintViolation<Tariff>> violations = validator.validate(tariff);
        assertTrue(violations.stream()
                .noneMatch(v -> v.getPropertyPath().toString().equals("validUntil")));
    }

    @Test
    @DisplayName("Should not allow null TUSD value")
    void shouldNotAllowNullTusdValue() {
        tariff.setGenerationDate(LocalDate.of(2024, 1, 15));
        tariff.setDistributor("CPFL JAGUARI");
        tariff.setCnpjDistributor("12345678000190");
        tariff.setValidFrom(LocalDate.of(2024, 1, 1));
        tariff.setTeValue(new BigDecimal("0.3500"));

        Set<ConstraintViolation<Tariff>> violations = validator.validate(tariff);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("tusdValue")));
    }

    @Test
    @DisplayName("Should not allow negative TUSD value")
    void shouldNotAllowNegativeTusdValue() {
        tariff.setGenerationDate(LocalDate.of(2024, 1, 15));
        tariff.setDistributor("CPFL JAGUARI");
        tariff.setCnpjDistributor("12345678000190");
        tariff.setValidFrom(LocalDate.of(2024, 1, 1));
        tariff.setTusdValue(new BigDecimal("-0.5000"));
        tariff.setTeValue(new BigDecimal("0.3500"));

        Set<ConstraintViolation<Tariff>> violations = validator.validate(tariff);
        assertFalse(violations.isEmpty(), "Should have validation errors for negative TUSD");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("tusdValue")));
    }

    @Test
    @DisplayName("Should not allow null TE value")
    void shouldNotAllowNullTeValue() {
        tariff.setGenerationDate(LocalDate.of(2024, 1, 15));
        tariff.setDistributor("CPFL JAGUARI");
        tariff.setCnpjDistributor("12345678000190");
        tariff.setValidFrom(LocalDate.of(2024, 1, 1));
        tariff.setTusdValue(new BigDecimal("0.5000"));

        Set<ConstraintViolation<Tariff>> violations = validator.validate(tariff);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("teValue")));
    }

    @Test
    @DisplayName("Should not allow negative TE value")
    void shouldNotAllowNegativeTeValue() {
        tariff.setTeValue(new BigDecimal("-0.35")); // Valor negativo

        Set<ConstraintViolation<Tariff>> violations = validator.validate(tariff);

        assertFalse(violations.isEmpty(), "Should have validation errors");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("must be positive")));
    }


    @Test
    @DisplayName("Should allow all optional descriptive fields to be null")
    void shouldAllowNullOptionalFields() {
        tariff.setGenerationDate(LocalDate.of(2024, 1, 15));
        tariff.setDistributor("CPFL JAGUARI");
        tariff.setCnpjDistributor("12345678000190");
        tariff.setValidFrom(LocalDate.of(2024, 1, 1));
        tariff.setTusdValue(new BigDecimal("0.5000"));
        tariff.setTeValue(new BigDecimal("0.3500"));

        tariff.setDescriptionReh(null);
        tariff.setTariffBaseDesc(null);
        tariff.setSubgroup(null);
        tariff.setTariffModality(null);
        tariff.setConsumerClass(null);
        tariff.setConsumerSubclass(null);
        tariff.setDetail(null);

        Set<ConstraintViolation<Tariff>> violations = validator.validate(tariff);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should validate distributor max length")
    void shouldValidateDistributorMaxLength() {
        tariff.setGenerationDate(LocalDate.of(2024, 1, 15));
        tariff.setDistributor("A".repeat(101)); // 101 caracteres
        tariff.setCnpjDistributor("12345678000190");
        tariff.setValidFrom(LocalDate.of(2024, 1, 1));
        tariff.setTusdValue(new BigDecimal("0.5000"));
        tariff.setTeValue(new BigDecimal("0.3500"));

        Set<ConstraintViolation<Tariff>> violations = validator.validate(tariff);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("must not exceed 100 characters")));
    }

    @Test
    @DisplayName("Should set timestamps automatically on create")
    void shouldSetTimestampsAutomaticallyOnCreate() {
        tariff.setGenerationDate(LocalDate.of(2024, 1, 15));
        tariff.setDistributor("CPFL JAGUARI");
        tariff.setCnpjDistributor("12345678000190");
        tariff.setValidFrom(LocalDate.of(2024, 1, 1));
        tariff.setTusdValue(new BigDecimal("0.5000"));
        tariff.setTeValue(new BigDecimal("0.3500"));

        tariff.onCreate();

        assertNotNull(tariff.getCreatedAt());
        assertNotNull(tariff.getUpdatedAt());
        assertEquals(tariff.getCreatedAt(), tariff.getUpdatedAt());
    }

    @Test
    @DisplayName("Should update timestamp on update")
    void shouldUpdateTimestampOnUpdate() throws InterruptedException {
        tariff.setGenerationDate(LocalDate.of(2024, 1, 15));
        tariff.setDistributor("CPFL JAGUARI");
        tariff.setCnpjDistributor("12345678000190");
        tariff.setValidFrom(LocalDate.of(2024, 1, 1));
        tariff.setTusdValue(new BigDecimal("0.5000"));
        tariff.setTeValue(new BigDecimal("0.3500"));

        tariff.onCreate();
        var createdAt = tariff.getCreatedAt();

        Thread.sleep(10); // Pequeno delay para garantir diferença no timestamp
        tariff.onUpdate();

        assertNotNull(tariff.getUpdatedAt());
        assertEquals(createdAt, tariff.getCreatedAt()); // createdAt não muda
        assertTrue(tariff.getUpdatedAt().isAfter(createdAt)); // updatedAt é posterior
    }

    @Test
    @DisplayName("Should accept valid tariff modalities")
    void shouldAcceptValidTariffModalities() {
        tariff.setGenerationDate(LocalDate.of(2024, 1, 15));
        tariff.setDistributor("CPFL JAGUARI");
        tariff.setCnpjDistributor("12345678000190");
        tariff.setValidFrom(LocalDate.of(2024, 1, 1));
        tariff.setTusdValue(new BigDecimal("0.5000"));
        tariff.setTeValue(new BigDecimal("0.3500"));

        String[] validModalities = {"Azul", "Verde", "Convencional", "Branca"};

        for (String modality : validModalities) {
            tariff.setTariffModality(modality);
            Set<ConstraintViolation<Tariff>> violations = validator.validate(tariff);
            assertTrue(violations.isEmpty());
        }
    }

    @Test
    @DisplayName("Should accept valid subgroups")
    void shouldAcceptValidSubgroups() {
        tariff.setGenerationDate(LocalDate.of(2024, 1, 15));
        tariff.setDistributor("CPFL JAGUARI");
        tariff.setCnpjDistributor("12345678000190");
        tariff.setValidFrom(LocalDate.of(2024, 1, 1));
        tariff.setTusdValue(new BigDecimal("0.5000"));
        tariff.setTeValue(new BigDecimal("0.3500"));

        String[] validSubgroups = {"A2", "A3", "A3a", "A4", "B1", "B2", "B3"};

        for (String subgroup : validSubgroups) {
            tariff.setSubgroup(subgroup);
            Set<ConstraintViolation<Tariff>> violations = validator.validate(tariff);
            assertTrue(violations.isEmpty());
        }
    }

    @Test
    @DisplayName("Should allow null flag fields for older tariffs")
    void shouldAllowNullFlagFields() {
        tariff.setGenerationDate(LocalDate.of(2024, 1, 15));
        tariff.setDistributor("CPFL JAGUARI");
        tariff.setCnpjDistributor("12345678000190");
        tariff.setValidFrom(LocalDate.of(2024, 1, 1));
        tariff.setTusdValue(new BigDecimal("0.5000"));
        tariff.setTeValue(new BigDecimal("0.3500"));

        tariff.setFlagGenerationDate(null);
        tariff.setCompetenceDate(null);
        tariff.setActivatedFlagName(null);
        tariff.setFlagAdditionalValue(null);

        Set<ConstraintViolation<Tariff>> violations = validator.validate(tariff);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should accept valid tariff flags")
    void shouldAcceptValidTariffFlags() {
        tariff.setGenerationDate(LocalDate.of(2024, 1, 15));
        tariff.setDistributor("CPFL JAGUARI");
        tariff.setCnpjDistributor("12345678000190");
        tariff.setValidFrom(LocalDate.of(2024, 1, 1));
        tariff.setTusdValue(new BigDecimal("0.5000"));
        tariff.setTeValue(new BigDecimal("0.3500"));

        tariff.setFlagGenerationDate(LocalDate.of(2024, 1, 1));
        tariff.setCompetenceDate(LocalDate.of(2024, 1, 1));
        tariff.setActivatedFlagName("Verde");
        tariff.setFlagAdditionalValue(BigDecimal.ZERO);

        Set<ConstraintViolation<Tariff>> violations = validator.validate(tariff);
        assertTrue(violations.isEmpty());
        assertEquals("Verde", tariff.getActivatedFlagName());
    }

    @Test
    @DisplayName("Should accept all valid flag colors")
    void shouldAcceptAllValidFlagColors() {
        tariff.setGenerationDate(LocalDate.of(2024, 1, 15));
        tariff.setDistributor("CPFL JAGUARI");
        tariff.setCnpjDistributor("12345678000190");
        tariff.setValidFrom(LocalDate.of(2024, 1, 1));
        tariff.setTusdValue(new BigDecimal("0.5000"));
        tariff.setTeValue(new BigDecimal("0.3500"));

        String[] validFlags = {"Verde", "Amarela", "Vermelha P1", "Vermelha P2"};

        for (String flag : validFlags) {
            tariff.setActivatedFlagName(flag);
            Set<ConstraintViolation<Tariff>> violations = validator.validate(tariff);
            assertTrue(violations.isEmpty());
        }
    }

    @Test
    @DisplayName("Should not allow negative flag additional value")
    void shouldNotAllowNegativeFlagValue() {
        // Setup completo da entidade
        tariff.setGenerationDate(LocalDate.now());
        tariff.setDistributor("CPFL JAGUARI");
        tariff.setCnpjDistributor("12345678000190");
        tariff.setValidFrom(LocalDate.now());
        tariff.setTusdValue(new BigDecimal("0.5000"));
        tariff.setTeValue(new BigDecimal("0.3500"));

        tariff.setFlagAdditionalValue(new BigDecimal("-10.00")); // Negativo

        Set<ConstraintViolation<Tariff>> violations = validator.validate(tariff);

        // Debug
        System.out.println("Total de violações: " + violations.size());
        violations.forEach(v -> System.out.println(v.getPropertyPath() + ": " + v.getMessage()));

        assertTrue(violations.stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals("flagAdditionalValue")),
                "Deveria rejeitar valor negativo");
    }

}
