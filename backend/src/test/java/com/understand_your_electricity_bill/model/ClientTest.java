package com.understand_your_electricity_bill.model;

import com.understand_your_electricity_bill.model.enums.UserType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    @DisplayName("Should create Client with all required fields")
    void shouldCreateClientWithRequiredFields() {
        Client client = new Client();
        client.setEmail("client@example.com");
        client.setPasswordHash("hashedPassword123");
        client.setName("João Silva");
        client.setAddress("Rua das Flores, 123");
        client.setCpf("12345678901");

        assertNotNull(client);
        assertEquals("client@example.com", client.getEmail());
        assertEquals("hashedPassword123", client.getPasswordHash());
        assertEquals("João Silva", client.getName());
        assertEquals("Rua das Flores, 123", client.getAddress());
        assertEquals("12345678901", client.getCpf());
        assertEquals(UserType.CLIENT, client.getUserType());
    }

    @Test
    @DisplayName("Should not allow null CPF")
    void shouldNotAllowNullCpf() {
        Client client = new Client();
        client.setEmail("client@example.com");
        client.setPasswordHash("hashedPassword123");
        client.setName("João Silva");
        client.setAddress("Rua das Flores, 123");

        Set<ConstraintViolation<Client>> violations = validator.validate(client);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("cpf")));
    }

    @Test
    @DisplayName("Should validate CPF format - exactly 11 digits")
    void shouldValidateCpfFormat() {
        Client client = new Client();
        client.setEmail("client@example.com");
        client.setPasswordHash("hashedPassword123");
        client.setName("João Silva");
        client.setAddress("Rua das Flores, 123");
        client.setCpf("123456789"); // Invalid - less than 11 digits

        Set<ConstraintViolation<Client>> violations = validator.validate(client);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("cpf")));
    }

    @Test
    @DisplayName("Should not allow null address")
    void shouldNotAllowNullAddress() {
        Client client = new Client();
        client.setEmail("client@example.com");
        client.setPasswordHash("hashedPassword123");
        client.setName("João Silva");
        client.setCpf("12345678901");

        Set<ConstraintViolation<Client>> violations = validator.validate(client);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("address")));
    }

    @Test
    @DisplayName("Should set registration date automatically")
    void shouldSetRegistrationDateAutomatically() {
        Client client = new Client();
        client.setEmail("client@example.com");
        client.setPasswordHash("hashedPassword123");
        client.setName("João Silva");
        client.setAddress("Rua das Flores, 123");
        client.setCpf("12345678901");

        client.onCreate();

        assertNotNull(client.getRegistrationDate());
        assertEquals(LocalDate.now(), client.getRegistrationDate());
    }

    @Test
    @DisplayName("Should inherit all User properties")
    void shouldInheritAllUserProperties() {
        Client client = new Client();
        client.setEmail("client@example.com");
        client.setPasswordHash("hashedPassword123");
        client.setName("João Silva");
        client.setAddress("Rua das Flores, 123");
        client.setCpf("12345678901");

        assertInstanceOf(User.class, client);
        assertEquals("client@example.com", client.getEmail());
        assertEquals("João Silva", client.getName());
        assertEquals(UserType.CLIENT, client.getUserType());
    }


    @Test
    @DisplayName("Should automatically set UserType to CLIENT")
    void shouldAutomaticallySetUserTypeToClient() {
        Client client = new Client();

        assertEquals(UserType.CLIENT, client.getUserType());
    }

}
