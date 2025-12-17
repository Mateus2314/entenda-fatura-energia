package com.understand_your_electricity_bill.model;

import com.understand_your_electricity_bill.model.enums.UserType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConsultantTest {

    private ValidatorFactory factory;
    private Validator validator;
    private Consultant consultant;

    @BeforeEach
    void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        consultant = new Consultant();
    }

    @Test
    @DisplayName("Should create Consultant with all required fields")
    void shouldCreateConsultantWithRequiredFields() {
        consultant.setEmail("consultant@example.com");
        consultant.setPasswordHash("hashedPassword123");
        consultant.setName("João Silva");
        consultant.setConsultantName("João Silva - Consultor");
        consultant.setCompany("Energia Consulting Ltda");
        consultant.setCnpj("12345678000190");
        consultant.setAddress("Av. Paulista, 1000");

        assertNotNull(consultant);
        assertEquals("consultant@example.com", consultant.getEmail());
        assertEquals("hashedPassword123", consultant.getPasswordHash());
        assertEquals("João Silva", consultant.getName());
        assertEquals("João Silva - Consultor", consultant.getConsultantName());
        assertEquals("Energia Consulting Ltda", consultant.getCompany());
        assertEquals("12345678000190", consultant.getCnpj());
        assertEquals("Av. Paulista, 1000", consultant.getAddress());
        assertEquals(UserType.CONSULTANT, consultant.getUserType());
    }

    @Test
    @DisplayName("Should not allow null consultant_name")
    void shouldNotAllowNullConsultantName() {
        consultant.setEmail("consultant@example.com");
        consultant.setPasswordHash("hashedPassword123");
        consultant.setName("João Silva");
        consultant.setCompany("Energia Consulting Ltda");
        consultant.setCnpj("12345678000190");
        consultant.setAddress("Av. Paulista, 1000");

        Set<ConstraintViolation<Consultant>> violations = validator.validate(consultant);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("consultantName")));
    }

    @Test
    @DisplayName("Should not allow null company")
    void shouldNotAllowNullCompany() {
        consultant.setEmail("consultant@example.com");
        consultant.setPasswordHash("hashedPassword123");
        consultant.setName("João Silva");
        consultant.setConsultantName("João Silva - Consultor");
        consultant.setCnpj("12345678000190");
        consultant.setAddress("Av. Paulista, 1000");

        Set<ConstraintViolation<Consultant>> violations = validator.validate(consultant);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("company")));
    }

    @Test
    @DisplayName("Should not allow null CNPJ")
    void shouldNotAllowNullCnpj() {
        consultant.setEmail("consultant@example.com");
        consultant.setPasswordHash("hashedPassword123");
        consultant.setName("João Silva");
        consultant.setConsultantName("João Silva - Consultor");
        consultant.setCompany("Energia Consulting Ltda");
        consultant.setAddress("Av. Paulista, 1000");

        Set<ConstraintViolation<Consultant>> violations = validator.validate(consultant);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("cnpj")));
    }

    @Test
    @DisplayName("Should validate CNPJ format - exactly 14 digits")
    void shouldValidateCnpjFormat() {
        consultant.setEmail("consultant@example.com");
        consultant.setPasswordHash("hashedPassword123");
        consultant.setName("João Silva");
        consultant.setConsultantName("João Silva - Consultor");
        consultant.setCompany("Energia Consulting Ltda");
        consultant.setCnpj("123456789"); // Invalid - less than 14 digits
        consultant.setAddress("Av. Paulista, 1000");

        Set<ConstraintViolation<Consultant>> violations = validator.validate(consultant);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("cnpj")));
    }

    @Test
    @DisplayName("Should not allow null address")
    void shouldNotAllowNullAddress() {
        consultant.setEmail("consultant@example.com");
        consultant.setPasswordHash("hashedPassword123");
        consultant.setName("João Silva");
        consultant.setConsultantName("João Silva - Consultor");
        consultant.setCompany("Energia Consulting Ltda");
        consultant.setCnpj("12345678000190");

        Set<ConstraintViolation<Consultant>> violations = validator.validate(consultant);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("address")));
    }

    @Test
    @DisplayName("Should allow null registration_number")
    void shouldAllowNullRegistrationNumber() {
        consultant.setEmail("consultant@example.com");
        consultant.setPasswordHash("hashedPassword123");
        consultant.setName("João Silva");
        consultant.setConsultantName("João Silva - Consultor");
        consultant.setCompany("Energia Consulting Ltda");
        consultant.setCnpj("12345678000190");
        consultant.setAddress("Av. Paulista, 1000");
        consultant.setRegistrationNumber(null);

        Set<ConstraintViolation<Consultant>> violations = validator.validate(consultant);
        assertTrue(violations.stream()
                .noneMatch(v -> v.getPropertyPath().toString().equals("registrationNumber")));
    }

    @Test
    @DisplayName("Should allow null company_logo")
    void shouldAllowNullCompanyLogo() {
        consultant.setEmail("consultant@example.com");
        consultant.setPasswordHash("hashedPassword123");
        consultant.setName("João Silva");
        consultant.setConsultantName("João Silva - Consultor");
        consultant.setCompany("Energia Consulting Ltda");
        consultant.setCnpj("12345678000190");
        consultant.setAddress("Av. Paulista, 1000");
        consultant.setCompanyLogo(null);

        Set<ConstraintViolation<Consultant>> violations = validator.validate(consultant);
        assertTrue(violations.stream()
                .noneMatch(v -> v.getPropertyPath().toString().equals("companyLogo")));
    }

    @Test
    @DisplayName("Should set registration date automatically")
    void shouldSetRegistrationDateAutomatically() {
        consultant.setEmail("consultant@example.com");
        consultant.setPasswordHash("hashedPassword123");
        consultant.setName("João Silva");
        consultant.setConsultantName("João Silva - Consultor");
        consultant.setCompany("Energia Consulting Ltda");
        consultant.setCnpj("12345678000190");
        consultant.setAddress("Av. Paulista, 1000");

        consultant.onCreate();

        assertNotNull(consultant.getRegistrationDate());
        assertEquals(LocalDate.now(), consultant.getRegistrationDate());
    }

    @Test
    @DisplayName("Should inherit all User properties")
    void shouldInheritAllUserProperties() {
        consultant.setEmail("consultant@example.com");
        consultant.setPasswordHash("hashedPassword123");
        consultant.setName("João Silva");
        consultant.setConsultantName("João Silva - Consultor");
        consultant.setCompany("Energia Consulting Ltda");
        consultant.setCnpj("12345678000190");
        consultant.setAddress("Av. Paulista, 1000");

        assertInstanceOf(User.class, consultant);
        assertEquals("consultant@example.com", consultant.getEmail());
        assertEquals("João Silva", consultant.getName());
        assertEquals(UserType.CONSULTANT, consultant.getUserType());
    }

    @Test
    @DisplayName("Should automatically set UserType to CONSULTANT")
    void shouldAutomaticallySetUserTypeToConsultant() {
        Consultant newConsultant = new Consultant();

        assertEquals(UserType.CONSULTANT, newConsultant.getUserType());
    }

    @Test
    @DisplayName("Should accept valid CNPJ with 14 digits")
    void shouldAcceptValidCnpj() {
        consultant.setEmail("consultant@example.com");
        consultant.setPasswordHash("hashedPassword123");
        consultant.setName("João Silva");
        consultant.setConsultantName("João Silva - Consultor");
        consultant.setCompany("Energia Consulting Ltda");
        consultant.setCnpj("12345678000190");
        consultant.setAddress("Av. Paulista, 1000");

        Set<ConstraintViolation<Consultant>> violations = validator.validate(consultant);
        assertTrue(violations.stream()
                .noneMatch(v -> v.getPropertyPath().toString().equals("cnpj")));
    }

    @Test
    @DisplayName("Should validate address max length")
    void shouldValidateAddressMaxLength() {
        String longAddress = "A".repeat(501);
        consultant.setEmail("consultant@example.com");
        consultant.setPasswordHash("hashedPassword123");
        consultant.setName("João Silva");
        consultant.setConsultantName("João Silva - Consultor");
        consultant.setCompany("Energia Consulting Ltda");
        consultant.setCnpj("12345678000190");
        consultant.setAddress(longAddress);

        Set<ConstraintViolation<Consultant>> violations = validator.validate(consultant);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("address")));
    }

}
