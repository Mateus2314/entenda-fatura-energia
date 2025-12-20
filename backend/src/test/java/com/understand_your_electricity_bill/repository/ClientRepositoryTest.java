package com.understand_your_electricity_bill.repository;


import com.understand_your_electricity_bill.model.Client;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = com.understand_your_electricity_bill.understand_your_electricity_bill.UnderstandYourElectricityBillApplication.class)
@ActiveProfiles("test")
class ClientRepositoryTest {

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
    private TestEntityManager entityManager;

    @Autowired
    private ClientRepository clientRepository;

    private Client testClient1;
    private Client testClient2;
    private Client testClient3;

    @BeforeEach
    void setUp() {
        // Cliente 1 - São Paulo
        testClient1 = new Client();
        testClient1.setEmail("client1@example.com");
        testClient1.setPasswordHash("hashedPassword123");
        testClient1.setUserType(UserType.CLIENT);
        testClient1.setName("João Silva");
        testClient1.setPhone("11987654321");
        testClient1.setStatus(UserStatus.ACTIVE);
        testClient1.setCpf("12345678901");
        testClient1.setAddress("Rua das Flores, 123");
        testClient1.setCity("São Paulo");
        testClient1.setState("SP");
        testClient1.setZipCode("01234-567");
        testClient1.setRegistrationDate(LocalDate.now().minusMonths(6));
        testClient1.setCreatedAt(LocalDateTime.now());
        testClient1.setUpdatedAt(LocalDateTime.now());

        // Cliente 2 - Rio de Janeiro
        testClient2 = new Client();
        testClient2.setEmail("client2@example.com");
        testClient2.setPasswordHash("hashedPassword456");
        testClient2.setUserType(UserType.CLIENT);
        testClient2.setName("Maria Santos");
        testClient2.setPhone("21987654321");
        testClient2.setStatus(UserStatus.ACTIVE);
        testClient2.setCpf("98765432109");
        testClient2.setAddress("Av. Atlântica, 456");
        testClient2.setCity("Rio de Janeiro");
        testClient2.setState("RJ");
        testClient2.setZipCode("22021-001");
        testClient2.setRegistrationDate(LocalDate.now().minusMonths(3));
        testClient2.setCreatedAt(LocalDateTime.now());
        testClient2.setUpdatedAt(LocalDateTime.now());

        // Cliente 3 - São Paulo (Pendente)
        testClient3 = new Client();
        testClient3.setEmail("client3@example.com");
        testClient3.setPasswordHash("hashedPassword789");
        testClient3.setUserType(UserType.CLIENT);
        testClient3.setName("Pedro Oliveira");
        testClient3.setPhone("11912345678");
        testClient3.setStatus(UserStatus.PENDING_VERIFICATION);
        testClient3.setCpf("11122233344");
        testClient3.setAddress("Rua Augusta, 789");
        testClient3.setCity("São Paulo");
        testClient3.setState("SP");
        testClient3.setZipCode("01305-100");
        testClient3.setRegistrationDate(LocalDate.now().minusDays(5));
        testClient3.setCreatedAt(LocalDateTime.now());
        testClient3.setUpdatedAt(LocalDateTime.now());
    }

    // ==================== findByCpf Tests ====================

    @Test
    @DisplayName("Should find client by CPF when client exists")
    void shouldFindClientByCpf() {
        entityManager.persistAndFlush(testClient1);

        Optional<Client> foundClient = clientRepository.findByCpf("12345678901");

        assertThat(foundClient).isPresent();
        assertThat(foundClient.get().getCpf()).isEqualTo("12345678901");
        assertThat(foundClient.get().getName()).isEqualTo("João Silva");
    }

    @Test
    @DisplayName("Should return empty when CPF does not exist")
    void shouldReturnEmptyWhenCpfNotFound() {
        Optional<Client> foundClient = clientRepository.findByCpf("00000000000");

        assertThat(foundClient).isEmpty();
    }

    // ==================== existsByCpf Tests ====================

    @Test
    @DisplayName("Should return true when CPF exists")
    void shouldReturnTrueWhenCpfExists() {
        entityManager.persistAndFlush(testClient1);

        boolean exists = clientRepository.existsByCpf("12345678901");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when CPF does not exist")
    void shouldReturnFalseWhenCpfDoesNotExist() {
        boolean exists = clientRepository.existsByCpf("00000000000");

        assertThat(exists).isFalse();
    }

    // ==================== save Tests ====================

    @Test
    @DisplayName("Should save client successfully")
    void shouldSaveClient() {
        Client savedClient = clientRepository.save(testClient1);
        entityManager.flush();
        entityManager.clear();

        assertThat(savedClient).isNotNull();
        assertThat(savedClient.getId()).isNotNull();
        assertThat(savedClient.getCpf()).isEqualTo("12345678901");
        assertThat(savedClient.getCreatedAt()).isNotNull();
    }

    // ==================== findByStatus Tests ====================

    @Test
    @DisplayName("Should find all active clients")
    void shouldFindAllActiveClients() {
        entityManager.persistAndFlush(testClient1);
        entityManager.persistAndFlush(testClient2);
        entityManager.persistAndFlush(testClient3);

        List<Client> activeClients = clientRepository.findByStatus(UserStatus.ACTIVE);

        assertThat(activeClients).hasSize(2);
        assertThat(activeClients).allMatch(c -> c.getStatus() == UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should find all pending clients")
    void shouldFindAllPendingClients() {
        entityManager.persistAndFlush(testClient1);
        entityManager.persistAndFlush(testClient2);
        entityManager.persistAndFlush(testClient3);

        List<Client> pendingClients = clientRepository.findByStatus(UserStatus.PENDING_VERIFICATION);

        assertThat(pendingClients).hasSize(1);
        assertThat(pendingClients.get(0).getStatus()).isEqualTo(UserStatus.PENDING_VERIFICATION);
    }

    // ==================== findByRegistrationDateAfter Tests ====================

    @Test
    @DisplayName("Should find clients registered after specific date")
    void shouldFindClientsRegisteredAfterDate() {
        LocalDate fourMonthsAgo = LocalDate.now().minusMonths(4);

        entityManager.persistAndFlush(testClient1);
        entityManager.persistAndFlush(testClient2);
        entityManager.persistAndFlush(testClient3);

        List<Client> recentClients = clientRepository.findByRegistrationDateAfter(fourMonthsAgo);

        assertThat(recentClients).hasSize(2);
    }

    // ==================== findByState Tests ====================

    @Test
    @DisplayName("Should find all clients in SP state")
    void shouldFindAllClientsInSP() {
        entityManager.persistAndFlush(testClient1);
        entityManager.persistAndFlush(testClient2);
        entityManager.persistAndFlush(testClient3);

        List<Client> spClients = clientRepository.findByState("SP");

        assertThat(spClients).hasSize(2);
        assertThat(spClients).allMatch(c -> c.getState().equals("SP"));
    }

    @Test
    @DisplayName("Should find all clients in RJ state")
    void shouldFindAllClientsInRJ() {
        entityManager.persistAndFlush(testClient1);
        entityManager.persistAndFlush(testClient2);
        entityManager.persistAndFlush(testClient3);

        List<Client> rjClients = clientRepository.findByState("RJ");

        assertThat(rjClients).hasSize(1);
        assertThat(rjClients.get(0).getState()).isEqualTo("RJ");
    }

    // ==================== findByCity Tests ====================

    @Test
    @DisplayName("Should find all clients in São Paulo city")
    void shouldFindAllClientsInSaoPaulo() {
        entityManager.persistAndFlush(testClient1);
        entityManager.persistAndFlush(testClient2);
        entityManager.persistAndFlush(testClient3);

        List<Client> saoPauloClients = clientRepository.findByCity("São Paulo");

        assertThat(saoPauloClients).hasSize(2);
        assertThat(saoPauloClients).allMatch(c -> c.getCity().equals("São Paulo"));
    }

    // ==================== findByZipCode Tests ====================

    @Test
    @DisplayName("Should find client by ZIP code")
    void shouldFindClientByZipCode() {
        entityManager.persistAndFlush(testClient1);

        List<Client> clients = clientRepository.findByZipCode("01234-567");

        assertThat(clients).hasSize(1);
        assertThat(clients.get(0).getZipCode()).isEqualTo("01234-567");
    }

    // ==================== countByStatus Tests ====================

    @Test
    @DisplayName("Should count active clients correctly")
    void shouldCountActiveClients() {
        entityManager.persistAndFlush(testClient1);
        entityManager.persistAndFlush(testClient2);
        entityManager.persistAndFlush(testClient3);

        long count = clientRepository.countByStatus(UserStatus.ACTIVE);

        assertThat(count).isEqualTo(2);
    }

    // ==================== countByState Tests ====================

    @Test
    @DisplayName("Should count clients by state correctly")
    void shouldCountClientsByState() {
        entityManager.persistAndFlush(testClient1);
        entityManager.persistAndFlush(testClient2);
        entityManager.persistAndFlush(testClient3);

        long spCount = clientRepository.countByState("SP");
        long rjCount = clientRepository.countByState("RJ");

        assertThat(spCount).isEqualTo(2);
        assertThat(rjCount).isEqualTo(1);
    }

    // ==================== findByNameContaining Tests ====================

    @Test
    @DisplayName("Should find clients by name containing search term")
    void shouldFindClientsByNameContaining() {
        entityManager.persistAndFlush(testClient1);
        entityManager.persistAndFlush(testClient2);
        entityManager.persistAndFlush(testClient3);

        List<Client> clients = clientRepository.findByNameContainingIgnoreCase("silva");

        assertThat(clients).hasSize(1);
        assertThat(clients.get(0).getName()).containsIgnoringCase("Silva");
    }

    @Test
    @DisplayName("Should handle case-insensitive search")
    void shouldHandleCaseInsensitiveSearch() {
        entityManager.persistAndFlush(testClient1);

        List<Client> clients = clientRepository.findByNameContainingIgnoreCase("JOÃO");

        assertThat(clients).hasSize(1);
        assertThat(clients.get(0).getName()).isEqualTo("João Silva");
    }

    // ==================== Edge Cases ====================

    @Test
    @DisplayName("Should handle special characters in address")
    void shouldHandleSpecialCharactersInAddress() {
        testClient1.setAddress("Rua José da Silva, nº 123 - Apto 45-B");
        entityManager.persistAndFlush(testClient1);

        Optional<Client> foundClient = clientRepository.findByCpf("12345678901");

        assertThat(foundClient).isPresent();
        assertThat(foundClient.get().getAddress()).isEqualTo("Rua José da Silva, nº 123 - Apto 45-B");
    }

}
