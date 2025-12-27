package com.understand_your_electricity_bill.model;

import com.understand_your_electricity_bill.model.enums.UserType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsultantTest {

    private ValidatorFactory factory;
    private Validator validator;
    private Consultant consultant;

    @BeforeEach
    void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        consultant = new Consultant();
    }

    @AfterEach
    void tearDown() {
        if (factory != null) {
            factory.close();
        }
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

    // =========================================================================
    // Many-to-Many Relationship Tests
    // =========================================================================

    @Test
    @DisplayName("Should initialize managedClients as empty Set")
    void shouldInitializeManagedClientsAsEmptySet() {
        Consultant newConsultant = new Consultant();

        assertNotNull(newConsultant.getManagedClients());
        assertTrue(newConsultant.getManagedClients().isEmpty());
        // Note: Size check is redundant with isEmpty() check
    }

    @Test
    @DisplayName("Should add client to managedClients using helper method")
    void shouldAddClientToManagedClientsUsingHelper() {
        // Arrange
        consultant.setEmail("consultant@example.com");
        consultant.setPasswordHash("hashedPassword123");
        consultant.setName("João Silva");
        consultant.setConsultantName("João Silva - Consultor");
        consultant.setCompany("Energia Consulting Ltda");
        consultant.setCnpj("12345678000190");
        consultant.setAddress("Av. Paulista, 1000");

        Client client = new Client();
        client.setEmail("client@example.com");
        client.setPasswordHash("hashedPassword456");
        client.setName("Maria Santos");
        client.setAddress("Rua das Flores, 123");
        client.setCpf("12345678901");

        // Act
        consultant.addManagedClient(client);

        // Assert
        assertTrue(consultant.getManagedClients().contains(client));
        assertEquals(1, consultant.getManagedClients().size());
        assertTrue(client.getConsultants().contains(consultant));
    }

    @Test
    @DisplayName("Should add multiple clients to managedClients")
    void shouldAddMultipleClientsToManagedClients() {
        // Arrange
        consultant.setEmail("consultant@example.com");
        consultant.setPasswordHash("hashedPassword123");
        consultant.setName("João Silva");
        consultant.setConsultantName("João Silva - Consultor");
        consultant.setCompany("Energia Consulting Ltda");
        consultant.setCnpj("12345678000190");
        consultant.setAddress("Av. Paulista, 1000");

        Client client1 = new Client();
        client1.setEmail("client1@example.com");
        client1.setPasswordHash("hashedPassword456");
        client1.setName("Maria Santos");
        client1.setAddress("Rua das Flores, 123");
        client1.setCpf("12345678901");

        Client client2 = new Client();
        client2.setEmail("client2@example.com");
        client2.setPasswordHash("hashedPassword789");
        client2.setName("Pedro Oliveira");
        client2.setAddress("Av. Brasil, 456");
        client2.setCpf("98765432109");

        // Act
        consultant.addManagedClient(client1);
        consultant.addManagedClient(client2);

        // Assert
        assertEquals(2, consultant.getManagedClients().size());
        assertTrue(consultant.getManagedClients().contains(client1));
        assertTrue(consultant.getManagedClients().contains(client2));
    }

    @Test
    @DisplayName("Should remove client from managedClients using helper method")
    void shouldRemoveClientFromManagedClientsUsingHelper() {
        // Arrange
        consultant.setEmail("consultant@example.com");
        consultant.setPasswordHash("hashedPassword123");
        consultant.setName("João Silva");
        consultant.setConsultantName("João Silva - Consultor");
        consultant.setCompany("Energia Consulting Ltda");
        consultant.setCnpj("12345678000190");
        consultant.setAddress("Av. Paulista, 1000");

        Client client = new Client();
        client.setEmail("client@example.com");
        client.setPasswordHash("hashedPassword456");
        client.setName("Maria Santos");
        client.setAddress("Rua das Flores, 123");
        client.setCpf("12345678901");

        consultant.addManagedClient(client);
        assertEquals(1, consultant.getManagedClients().size());

        // Act
        consultant.removeManagedClient(client);

        // Assert
        assertFalse(consultant.getManagedClients().contains(client));
        assertEquals(0, consultant.getManagedClients().size());
        assertFalse(client.getConsultants().contains(consultant));
    }

    @Test
    @DisplayName("Should maintain bidirectional relationship when adding client")
    void shouldMaintainBidirectionalRelationshipWhenAddingClient() {
        // Arrange
        consultant.setEmail("consultant@example.com");
        consultant.setPasswordHash("hashedPassword123");
        consultant.setName("João Silva");
        consultant.setConsultantName("João Silva - Consultor");
        consultant.setCompany("Energia Consulting Ltda");
        consultant.setCnpj("12345678000190");
        consultant.setAddress("Av. Paulista, 1000");

        Client client = new Client();
        client.setEmail("client@example.com");
        client.setPasswordHash("hashedPassword456");
        client.setName("Maria Santos");
        client.setAddress("Rua das Flores, 123");
        client.setCpf("12345678901");

        // Act
        consultant.addManagedClient(client);

        // Assert - Both sides of the relationship are set
        assertTrue(consultant.getManagedClients().contains(client));
        assertTrue(client.getConsultants().contains(consultant));
        assertEquals(1, consultant.getManagedClients().size());
        assertEquals(1, client.getConsultants().size());
    }

    @Test
    @DisplayName("Should maintain bidirectional relationship when removing client")
    void shouldMaintainBidirectionalRelationshipWhenRemovingClient() {
        // Arrange
        consultant.setEmail("consultant@example.com");
        consultant.setPasswordHash("hashedPassword123");
        consultant.setName("João Silva");
        consultant.setConsultantName("João Silva - Consultor");
        consultant.setCompany("Energia Consulting Ltda");
        consultant.setCnpj("12345678000190");
        consultant.setAddress("Av. Paulista, 1000");

        Client client = new Client();
        client.setEmail("client@example.com");
        client.setPasswordHash("hashedPassword456");
        client.setName("Maria Santos");
        client.setAddress("Rua das Flores, 123");
        client.setCpf("12345678901");

        consultant.addManagedClient(client);

        // Act
        consultant.removeManagedClient(client);

        // Assert - Both sides of the relationship are removed
        assertFalse(consultant.getManagedClients().contains(client));
        assertFalse(client.getConsultants().contains(consultant));
        assertEquals(0, consultant.getManagedClients().size());
        assertEquals(0, client.getConsultants().size());
    }

    @Test
    @DisplayName("Should handle removing non-existent client gracefully")
    void shouldHandleRemovingNonExistentClientGracefully() {
        // Arrange
        consultant.setEmail("consultant@example.com");
        consultant.setPasswordHash("hashedPassword123");
        consultant.setName("João Silva");
        consultant.setConsultantName("João Silva - Consultor");
        consultant.setCompany("Energia Consulting Ltda");
        consultant.setCnpj("12345678000190");
        consultant.setAddress("Av. Paulista, 1000");

        Client client = new Client();
        client.setEmail("client@example.com");
        client.setPasswordHash("hashedPassword456");
        client.setName("Maria Santos");
        client.setAddress("Rua das Flores, 123");
        client.setCpf("12345678901");

        // Act - Remove client that was never added
        assertDoesNotThrow(() -> consultant.removeManagedClient(client));

        // Assert
        assertEquals(0, consultant.getManagedClients().size());
    }

}
