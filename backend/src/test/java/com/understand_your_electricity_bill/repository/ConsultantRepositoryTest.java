package com.understand_your_electricity_bill.repository;


import com.understand_your_electricity_bill.model.Consultant;
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
class ConsultantRepositoryTest {

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
    private ConsultantRepository consultantRepository;

    private Consultant testConsultant1;
    private Consultant testConsultant2;
    private Consultant testConsultant3;

    @BeforeEach
    void setUp() {
        // Consultant 1 - São Paulo
        testConsultant1 = new Consultant();
        testConsultant1.setEmail("consultant1@example.com");
        testConsultant1.setPasswordHash("hashedPassword123");
        testConsultant1.setUserType(UserType.CONSULTANT);
        testConsultant1.setName("Consultant User 1");
        testConsultant1.setPhone("11987654321");
        testConsultant1.setStatus(UserStatus.ACTIVE);
        testConsultant1.setConsultantName("João Silva");
        testConsultant1.setCompany("Energia Consultoria Ltda");
        testConsultant1.setCnpj("12345678000190");
        testConsultant1.setRegistrationNumber("CRE-SP-12345");
        testConsultant1.setAddress("Av. Paulista, 1000");
        testConsultant1.setCity("São Paulo");
        testConsultant1.setState("SP");
        testConsultant1.setZipCode("01310-100");
        testConsultant1.setCompanyLogo("https://example.com/logo1.png");
        testConsultant1.setRegistrationDate(LocalDate.now().minusMonths(12));
        testConsultant1.setCreatedAt(LocalDateTime.now());
        testConsultant1.setUpdatedAt(LocalDateTime.now());

        // Consultant 2 - Rio de Janeiro
        testConsultant2 = new Consultant();
        testConsultant2.setEmail("consultant2@example.com");
        testConsultant2.setPasswordHash("hashedPassword456");
        testConsultant2.setUserType(UserType.CONSULTANT);
        testConsultant2.setName("Consultant User 2");
        testConsultant2.setPhone("21987654321");
        testConsultant2.setStatus(UserStatus.ACTIVE);
        testConsultant2.setConsultantName("Maria Santos");
        testConsultant2.setCompany("Power Consulting S.A.");
        testConsultant2.setCnpj("98765432000109");
        testConsultant2.setRegistrationNumber("CRE-RJ-67890");
        testConsultant2.setAddress("Rua do Catete, 200");
        testConsultant2.setCity("Rio de Janeiro");
        testConsultant2.setState("RJ");
        testConsultant2.setZipCode("22220-000");
        testConsultant2.setCompanyLogo("https://example.com/logo2.png");
        testConsultant2.setRegistrationDate(LocalDate.now().minusMonths(6));
        testConsultant2.setCreatedAt(LocalDateTime.now());
        testConsultant2.setUpdatedAt(LocalDateTime.now());

        // Consultant 3 - São Paulo (Pending)
        testConsultant3 = new Consultant();
        testConsultant3.setEmail("consultant3@example.com");
        testConsultant3.setPasswordHash("hashedPassword789");
        testConsultant3.setUserType(UserType.CONSULTANT);
        testConsultant3.setName("Consultant User 3");
        testConsultant3.setPhone("11912345678");
        testConsultant3.setStatus(UserStatus.PENDING_VERIFICATION);
        testConsultant3.setConsultantName("Pedro Oliveira");
        testConsultant3.setCompany("Consultoria Energia Verde");
        testConsultant3.setCnpj("11122233000144");
        testConsultant3.setAddress("Rua Augusta, 500");
        testConsultant3.setCity("São Paulo");
        testConsultant3.setState("SP");
        testConsultant3.setZipCode("01305-000");
        testConsultant3.setRegistrationDate(LocalDate.now().minusDays(15));
        testConsultant3.setCreatedAt(LocalDateTime.now());
        testConsultant3.setUpdatedAt(LocalDateTime.now());
    }

    // ==================== findByCnpj Tests ====================

    @Test
    @DisplayName("Should find consultant by CNPJ when consultant exists")
    void shouldFindConsultantByCnpj() {
        entityManager.persistAndFlush(testConsultant1);

        Optional<Consultant> foundConsultant = consultantRepository.findByCnpj("12345678000190");

        assertThat(foundConsultant).isPresent();
        assertThat(foundConsultant.get().getCnpj()).isEqualTo("12345678000190");
        assertThat(foundConsultant.get().getCompany()).isEqualTo("Energia Consultoria Ltda");
    }

    @Test
    @DisplayName("Should return empty when CNPJ does not exist")
    void shouldReturnEmptyWhenCnpjNotFound() {
        Optional<Consultant> foundConsultant = consultantRepository.findByCnpj("00000000000000");

        assertThat(foundConsultant).isEmpty();
    }

    // ==================== existsByCnpj Tests ====================

    @Test
    @DisplayName("Should return true when CNPJ exists")
    void shouldReturnTrueWhenCnpjExists() {
        entityManager.persistAndFlush(testConsultant1);

        boolean exists = consultantRepository.existsByCnpj("12345678000190");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when CNPJ does not exist")
    void shouldReturnFalseWhenCnpjDoesNotExist() {
        boolean exists = consultantRepository.existsByCnpj("00000000000000");

        assertThat(exists).isFalse();
    }

    // ==================== save Tests ====================

    @Test
    @DisplayName("Should save consultant successfully")
    void shouldSaveConsultant() {
        Consultant savedConsultant = consultantRepository.save(testConsultant1);
        entityManager.flush();
        entityManager.clear();

        assertThat(savedConsultant).isNotNull();
        assertThat(savedConsultant.getId()).isNotNull();
        assertThat(savedConsultant.getCnpj()).isEqualTo("12345678000190");
        assertThat(savedConsultant.getCreatedAt()).isNotNull();
    }

    // ==================== findByStatus Tests ====================

    @Test
    @DisplayName("Should find all active consultants")
    void shouldFindAllActiveConsultants() {
        entityManager.persistAndFlush(testConsultant1);
        entityManager.persistAndFlush(testConsultant2);
        entityManager.persistAndFlush(testConsultant3);

        List<Consultant> activeConsultants = consultantRepository.findByStatus(UserStatus.ACTIVE);

        assertThat(activeConsultants).hasSize(2);
        assertThat(activeConsultants).allMatch(c -> c.getStatus() == UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should find all pending consultants")
    void shouldFindAllPendingConsultants() {
        entityManager.persistAndFlush(testConsultant1);
        entityManager.persistAndFlush(testConsultant2);
        entityManager.persistAndFlush(testConsultant3);

        List<Consultant> pendingConsultants = consultantRepository.findByStatus(UserStatus.PENDING_VERIFICATION);

        assertThat(pendingConsultants).hasSize(1);
        assertThat(pendingConsultants.get(0).getStatus()).isEqualTo(UserStatus.PENDING_VERIFICATION);
    }

    // ==================== findByRegistrationDateAfter Tests ====================

    @Test
    @DisplayName("Should find consultants registered after specific date")
    void shouldFindConsultantsRegisteredAfterDate() {
        LocalDate eightMonthsAgo = LocalDate.now().minusMonths(8);

        entityManager.persistAndFlush(testConsultant1);
        entityManager.persistAndFlush(testConsultant2);
        entityManager.persistAndFlush(testConsultant3);

        List<Consultant> recentConsultants = consultantRepository.findByRegistrationDateAfter(eightMonthsAgo);

        assertThat(recentConsultants).hasSize(2);
    }

    // ==================== findByCompany Tests ====================

    @Test
    @DisplayName("Should find consultant by exact company name")
    void shouldFindConsultantByCompany() {
        entityManager.persistAndFlush(testConsultant1);
        entityManager.persistAndFlush(testConsultant2);

        List<Consultant> consultants = consultantRepository.findByCompany("Energia Consultoria Ltda");

        assertThat(consultants).hasSize(1);
        assertThat(consultants.get(0).getCompany()).isEqualTo("Energia Consultoria Ltda");
    }

    // ==================== findByState Tests ====================

    @Test
    @DisplayName("Should find all consultants in SP state")
    void shouldFindAllConsultantsInSP() {
        entityManager.persistAndFlush(testConsultant1);
        entityManager.persistAndFlush(testConsultant2);
        entityManager.persistAndFlush(testConsultant3);

        List<Consultant> spConsultants = consultantRepository.findByState("SP");

        assertThat(spConsultants).hasSize(2);
        assertThat(spConsultants).allMatch(c -> c.getState().equals("SP"));
    }

    @Test
    @DisplayName("Should find all consultants in RJ state")
    void shouldFindAllConsultantsInRJ() {
        entityManager.persistAndFlush(testConsultant1);
        entityManager.persistAndFlush(testConsultant2);
        entityManager.persistAndFlush(testConsultant3);

        List<Consultant> rjConsultants = consultantRepository.findByState("RJ");

        assertThat(rjConsultants).hasSize(1);
        assertThat(rjConsultants.get(0).getState()).isEqualTo("RJ");
    }

    // ==================== findByCity Tests ====================

    @Test
    @DisplayName("Should find all consultants in São Paulo city")
    void shouldFindAllConsultantsInSaoPaulo() {
        entityManager.persistAndFlush(testConsultant1);
        entityManager.persistAndFlush(testConsultant2);
        entityManager.persistAndFlush(testConsultant3);

        List<Consultant> saoPauloConsultants = consultantRepository.findByCity("São Paulo");

        assertThat(saoPauloConsultants).hasSize(2);
        assertThat(saoPauloConsultants).allMatch(c -> c.getCity().equals("São Paulo"));
    }

    // ==================== findByZipCode Tests ====================

    @Test
    @DisplayName("Should find consultant by ZIP code")
    void shouldFindConsultantByZipCode() {
        entityManager.persistAndFlush(testConsultant1);

        List<Consultant> consultants = consultantRepository.findByZipCode("01310-100");

        assertThat(consultants).hasSize(1);
        assertThat(consultants.get(0).getZipCode()).isEqualTo("01310-100");
    }

    // ==================== countByStatus Tests ====================

    @Test
    @DisplayName("Should count active consultants correctly")
    void shouldCountActiveConsultants() {
        entityManager.persistAndFlush(testConsultant1);
        entityManager.persistAndFlush(testConsultant2);
        entityManager.persistAndFlush(testConsultant3);

        long count = consultantRepository.countByStatus(UserStatus.ACTIVE);

        assertThat(count).isEqualTo(2);
    }

    // ==================== countByState Tests ====================

    @Test
    @DisplayName("Should count consultants by state correctly")
    void shouldCountConsultantsByState() {
        entityManager.persistAndFlush(testConsultant1);
        entityManager.persistAndFlush(testConsultant2);
        entityManager.persistAndFlush(testConsultant3);

        long spCount = consultantRepository.countByState("SP");
        long rjCount = consultantRepository.countByState("RJ");

        assertThat(spCount).isEqualTo(2);
        assertThat(rjCount).isEqualTo(1);
    }

    // ==================== findByCompanyContaining Tests ====================

    @Test
    @DisplayName("Should find consultants by company name containing search term")
    void shouldFindConsultantsByCompanyContaining() {
        entityManager.persistAndFlush(testConsultant1);
        entityManager.persistAndFlush(testConsultant2);
        entityManager.persistAndFlush(testConsultant3);

        List<Consultant> consultants = consultantRepository.findByCompanyContainingIgnoreCase("energia");

        assertThat(consultants).hasSize(2);
    }

    @Test
    @DisplayName("Should handle case-insensitive company search")
    void shouldHandleCaseInsensitiveCompanySearch() {
        entityManager.persistAndFlush(testConsultant1);

        List<Consultant> consultants = consultantRepository.findByCompanyContainingIgnoreCase("ENERGIA");

        assertThat(consultants).hasSize(1);
    }

    // ==================== findByConsultantNameContaining Tests ====================

    @Test
    @DisplayName("Should find consultants by consultant name containing search term")
    void shouldFindConsultantsByNameContaining() {
        entityManager.persistAndFlush(testConsultant1);
        entityManager.persistAndFlush(testConsultant2);
        entityManager.persistAndFlush(testConsultant3);

        List<Consultant> consultants = consultantRepository.findByConsultantNameContainingIgnoreCase("silva");

        assertThat(consultants).hasSize(1);
        assertThat(consultants.get(0).getConsultantName()).containsIgnoringCase("Silva");
    }

    @Test
    @DisplayName("Should handle case-insensitive consultant name search")
    void shouldHandleCaseInsensitiveConsultantNameSearch() {
        entityManager.persistAndFlush(testConsultant1);

        List<Consultant> consultants = consultantRepository.findByConsultantNameContainingIgnoreCase("JOÃO");

        assertThat(consultants).hasSize(1);
        assertThat(consultants.get(0).getConsultantName()).isEqualTo("João Silva");
    }

    // ==================== Edge Cases ====================

    @Test
    @DisplayName("Should handle special characters in company name")
    void shouldHandleSpecialCharactersInCompanyName() {
        testConsultant1.setCompany("Energia & Consultoria - Soluções S/A");
        entityManager.persistAndFlush(testConsultant1);

        Optional<Consultant> foundConsultant = consultantRepository.findByCnpj("12345678000190");

        assertThat(foundConsultant).isPresent();
        assertThat(foundConsultant.get().getCompany()).isEqualTo("Energia & Consultoria - Soluções S/A");
    }

    @Test
    @DisplayName("Should handle consultant without registration number")
    void shouldHandleConsultantWithoutRegistrationNumber() {
        testConsultant1.setRegistrationNumber(null);
        entityManager.persistAndFlush(testConsultant1);

        Optional<Consultant> foundConsultant = consultantRepository.findByCnpj("12345678000190");

        assertThat(foundConsultant).isPresent();
        assertThat(foundConsultant.get().getRegistrationNumber()).isNull();
    }

    @Test
    @DisplayName("Should handle consultant without company logo")
    void shouldHandleConsultantWithoutCompanyLogo() {
        testConsultant1.setCompanyLogo(null);
        entityManager.persistAndFlush(testConsultant1);

        Optional<Consultant> foundConsultant = consultantRepository.findByCnpj("12345678000190");

        assertThat(foundConsultant).isPresent();
        assertThat(foundConsultant.get().getCompanyLogo()).isNull();
    }

}
