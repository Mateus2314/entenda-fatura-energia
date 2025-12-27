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

    /**
     * Helper method to create a valid Client instance for testing.
     *
     * @return A fully configured Client with all required fields
     */
    private Client createValidClient() {
        Client client = new Client();

        // Campos herdados de User
        client.setEmail("client@example.com");
        client.setPasswordHash("hashedPassword123");
        client.setName("Test Client");
        client.setPhone("+5511987654321");

        // Campos específicos de Client
        client.setAddress("Rua Exemplo, 123");
        client.setCity("São Paulo");
        client.setState("SP");
        client.setZipCode("01234-567");
        client.setCpf("12345678901");
        client.setRegistrationDate(LocalDate.now());

        return client;
    }

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
    @DisplayName("Should validate city when provided")
    void shouldValidateCityWhenProvided() {
        Client client = createValidClient();
        client.setCity("São Paulo");

        Set<ConstraintViolation<Client>> violations = validator.validate(client);

        assertTrue(violations.isEmpty());
        assertEquals("São Paulo", client.getCity());
    }

    @Test
    @DisplayName("Should reject city exceeding 100 characters")
    void shouldRejectCityExceedingMaxLength() {
        Client client = createValidClient();
        client.setCity("A".repeat(101)); // 101 caracteres

        Set<ConstraintViolation<Client>> violations = validator.validate(client);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("City must not exceed 100 characters")));
    }

    @Test
    @DisplayName("Should validate state when provided")
    void shouldValidateStateWhenProvided() {
        Client client = createValidClient();
        client.setState("SP");

        Set<ConstraintViolation<Client>> violations = validator.validate(client);

        assertTrue(violations.isEmpty());
        assertEquals("SP", client.getState());
    }

    @Test
    @DisplayName("Should reject state not matching 2 characters")
    void shouldRejectInvalidStateFormat() {
        Client client = createValidClient();
        client.setState("SAO"); // 3 caracteres - inválido

        Set<ConstraintViolation<Client>> violations = validator.validate(client);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("State must be 2 characters")));
    }

    @Test
    @DisplayName("Should validate zipCode in correct format")
    void shouldValidateZipCodeInCorrectFormat() {
        Client client = createValidClient();
        client.setZipCode("12345-678");

        Set<ConstraintViolation<Client>> violations = validator.validate(client);

        assertTrue(violations.isEmpty());
        assertEquals("12345-678", client.getZipCode());
    }

    @Test
    @DisplayName("Should validate zipCode without hyphen")
    void shouldValidateZipCodeWithoutHyphen() {
        Client client = createValidClient();
        client.setZipCode("12345678");

        Set<ConstraintViolation<Client>> violations = validator.validate(client);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should reject invalid zipCode format")
    void shouldRejectInvalidZipCodeFormat() {
        Client client = createValidClient();
        client.setZipCode("123"); // Formato inválido

        Set<ConstraintViolation<Client>> violations = validator.validate(client);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("ZIP code must be in format")));
    }

    @Test
    @DisplayName("Should accept null optional fields")
    void shouldAcceptNullOptionalFields() {
        Client client = createValidClient();
        client.setCity(null);
        client.setState(null);
        client.setZipCode(null);

        Set<ConstraintViolation<Client>> violations = validator.validate(client);

        assertTrue(violations.isEmpty());
    }




    @Test
    @DisplayName("Should automatically set UserType to CLIENT")
    void shouldAutomaticallySetUserTypeToClient() {
        Client client = new Client();

        assertEquals(UserType.CLIENT, client.getUserType());
    }

    // =========================================================================
    // Many-to-Many Relationship Tests
    // =========================================================================

    @Test
    @DisplayName("Should initialize consultants as empty Set")
    void shouldInitializeConsultantsAsEmptySet() {
        Client client = new Client();

        assertNotNull(client.getConsultants());
        assertTrue(client.getConsultants().isEmpty());
        // Note: Size check is redundant with isEmpty() check
    }

    @Test
    @DisplayName("Should add consultant through bidirectional relationship")
    void shouldAddConsultantThroughBidirectionalRelationship() {
        // Arrange
        Client client = createValidClient();

        Consultant consultant = new Consultant();
        consultant.setEmail("consultant@example.com");
        consultant.setPasswordHash("hashedPassword123");
        consultant.setName("João Silva");
        consultant.setConsultantName("João Silva - Consultor");
        consultant.setCompany("Energia Consulting Ltda");
        consultant.setCnpj("12345678000190");
        consultant.setAddress("Av. Paulista, 1000");

        // Act - Add through consultant side (owner of the relationship)
        consultant.addManagedClient(client);

        // Assert - Verify both sides
        assertTrue(client.getConsultants().contains(consultant));
        assertEquals(1, client.getConsultants().size());
        assertTrue(consultant.getManagedClients().contains(client));
    }

    @Test
    @DisplayName("Should have multiple consultants over time")
    void shouldHaveMultipleConsultantsOverTime() {
        // Arrange
        Client client = createValidClient();

        Consultant consultant1 = new Consultant();
        consultant1.setEmail("consultant1@example.com");
        consultant1.setPasswordHash("hashedPassword123");
        consultant1.setName("João Silva");
        consultant1.setConsultantName("João Silva - Consultor");
        consultant1.setCompany("Energia Consulting Ltda");
        consultant1.setCnpj("12345678000190");
        consultant1.setAddress("Av. Paulista, 1000");

        Consultant consultant2 = new Consultant();
        consultant2.setEmail("consultant2@example.com");
        consultant2.setPasswordHash("hashedPassword456");
        consultant2.setName("Maria Santos");
        consultant2.setConsultantName("Maria Santos - Consultora");
        consultant2.setCompany("Power Consulting SA");
        consultant2.setCnpj("98765432000199");
        consultant2.setAddress("Rua Augusta, 500");

        // Act
        consultant1.addManagedClient(client);
        consultant2.addManagedClient(client);

        // Assert
        assertEquals(2, client.getConsultants().size());
        assertTrue(client.getConsultants().contains(consultant1));
        assertTrue(client.getConsultants().contains(consultant2));
    }

    @Test
    @DisplayName("Should remove consultant through bidirectional relationship")
    void shouldRemoveConsultantThroughBidirectionalRelationship() {
        // Arrange
        Client client = createValidClient();

        Consultant consultant = new Consultant();
        consultant.setEmail("consultant@example.com");
        consultant.setPasswordHash("hashedPassword123");
        consultant.setName("João Silva");
        consultant.setConsultantName("João Silva - Consultor");
        consultant.setCompany("Energia Consulting Ltda");
        consultant.setCnpj("12345678000190");
        consultant.setAddress("Av. Paulista, 1000");

        consultant.addManagedClient(client);
        assertEquals(1, client.getConsultants().size());

        // Act - Remove through consultant side
        consultant.removeManagedClient(client);

        // Assert - Verify both sides
        assertFalse(client.getConsultants().contains(consultant));
        assertEquals(0, client.getConsultants().size());
        assertFalse(consultant.getManagedClients().contains(client));
    }

    @Test
    @DisplayName("Should maintain referential integrity when consultant is removed")
    void shouldMaintainReferentialIntegrityWhenConsultantIsRemoved() {
        // Arrange
        Client client = createValidClient();

        Consultant consultant1 = new Consultant();
        consultant1.setEmail("consultant1@example.com");
        consultant1.setPasswordHash("hashedPassword123");
        consultant1.setName("João Silva");
        consultant1.setConsultantName("João Silva - Consultor");
        consultant1.setCompany("Energia Consulting Ltda");
        consultant1.setCnpj("12345678000190");
        consultant1.setAddress("Av. Paulista, 1000");

        Consultant consultant2 = new Consultant();
        consultant2.setEmail("consultant2@example.com");
        consultant2.setPasswordHash("hashedPassword456");
        consultant2.setName("Maria Santos");
        consultant2.setConsultantName("Maria Santos - Consultora");
        consultant2.setCompany("Power Consulting SA");
        consultant2.setCnpj("98765432000199");
        consultant2.setAddress("Rua Augusta, 500");

        consultant1.addManagedClient(client);
        consultant2.addManagedClient(client);
        assertEquals(2, client.getConsultants().size());

        // Act - Remove only one consultant
        consultant1.removeManagedClient(client);

        // Assert - Other consultant remains
        assertEquals(1, client.getConsultants().size());
        assertFalse(client.getConsultants().contains(consultant1));
        assertTrue(client.getConsultants().contains(consultant2));
    }

    @Test
    @DisplayName("Should verify consultants collection is not null after instantiation")
    void shouldVerifyConsultantsCollectionIsNotNullAfterInstantiation() {
        Client client = new Client();

        assertNotNull(client.getConsultants());
        assertInstanceOf(java.util.Set.class, client.getConsultants());
    }

}
