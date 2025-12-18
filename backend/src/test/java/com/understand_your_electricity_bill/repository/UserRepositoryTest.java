package com.understand_your_electricity_bill.repository;

import com.understand_your_electricity_bill.model.User;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = com.understand_your_electricity_bill.understand_your_electricity_bill.UnderstandYourElectricityBillApplication.class)
@ActiveProfiles("test")
class UserRepositoryTest {

    // Carrega o .env antes de qualquer coisa
    static {
        Dotenv dotenv = Dotenv.configure()
                .directory("../../") // Diretório raiz do projeto
                .ignoreIfMissing() // Não falha se o arquivo não existir
                .load();

        // Define as variáveis de ambiente como propriedades do sistema
        System.setProperty("DB_URL", dotenv.get("DB_URL"));
        System.setProperty("DB_USER", dotenv.get("DB_USER"));
        System.setProperty("DB_PASS", dotenv.get("DB_PASS"));
    }

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private User adminUser;
    private User consultantUser;

    @BeforeEach
    void setUp() {
        // User padrão (Client)
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("hashedPassword123");
        testUser.setUserType(UserType.CLIENT);
        testUser.setName("Test User");
        testUser.setStatus(UserStatus.ACTIVE);

        // Admin
        adminUser = new User();
        adminUser.setEmail("admin@example.com");
        adminUser.setPasswordHash("hashedPassword456");
        adminUser.setUserType(UserType.ADMIN);
        adminUser.setName("Admin User");
        adminUser.setStatus(UserStatus.ACTIVE);

        // Consultant
        consultantUser = new User();
        consultantUser.setEmail("consultant@example.com");
        consultantUser.setPasswordHash("hashedPassword789");
        consultantUser.setUserType(UserType.CONSULTANT);
        consultantUser.setName("Consultant User");
        consultantUser.setStatus(UserStatus.PENDING_VERIFICATION);
    }

    // ==================== findByEmail Tests ====================

    @Test
    @DisplayName("Should find user by email when user exists")
    void shouldFindUserByEmail() {
        entityManager.persistAndFlush(testUser);

        Optional<User> foundUser = userRepository.findByEmail("test@example.com");

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
        assertThat(foundUser.get().getName()).isEqualTo("Test User");
    }

    @Test
    @DisplayName("Should return empty when user with email does not exist")
    void shouldReturnEmptyWhenUserNotFound() {
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        assertThat(foundUser).isEmpty();
    }

    // ==================== existsByEmail Tests ====================

    @Test
    @DisplayName("Should return true when email exists")
    void shouldReturnTrueWhenEmailExists() {
        entityManager.persistAndFlush(testUser);

        boolean exists = userRepository.existsByEmail("test@example.com");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when email does not exist")
    void shouldReturnFalseWhenEmailDoesNotExist() {
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        assertThat(exists).isFalse();
    }

    // ==================== save Tests ====================

    @Test
    @DisplayName("Should save user successfully")
    void shouldSaveUser() {
        // Garante que o createdAt seja definido se não houver @PrePersist
        if (testUser.getCreatedAt() == null) {
            testUser.setCreatedAt(LocalDateTime.now());
        }

        // Garante que o updatedAt seja definido se for obrigatório
        if (testUser.getUpdatedAt() == null) {
            testUser.setUpdatedAt(LocalDateTime.now());
        }

        User savedUser = userRepository.save(testUser);

        // Força o flush para garantir persistência
        entityManager.flush();
        entityManager.clear();

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.getCreatedAt()).isNotNull();
    }

    // ==================== findByUserType Tests ====================

    @Test
    @DisplayName("Should find all users by user type CLIENT")
    void shouldFindAllClientUsers() {
        entityManager.persistAndFlush(testUser);
        entityManager.persistAndFlush(adminUser);
        entityManager.persistAndFlush(consultantUser);

        List<User> clients = userRepository.findByUserType(UserType.CLIENT);

        assertThat(clients).hasSize(1);
        assertThat(clients.get(0).getUserType()).isEqualTo(UserType.CLIENT);
    }

    @Test
    @DisplayName("Should find all users by user type ADMIN")
    void shouldFindAllAdminUsers() {
        entityManager.persistAndFlush(testUser);
        entityManager.persistAndFlush(adminUser);
        entityManager.persistAndFlush(consultantUser);

        List<User> admins = userRepository.findByUserType(UserType.ADMIN);

        assertThat(admins).hasSize(1);
        assertThat(admins.get(0).getUserType()).isEqualTo(UserType.ADMIN);
    }

    // ==================== findByStatus Tests ====================

    @Test
    @DisplayName("Should find all users by status ACTIVE")
    void shouldFindAllActiveUsers() {
        entityManager.persistAndFlush(testUser);
        entityManager.persistAndFlush(adminUser);
        entityManager.persistAndFlush(consultantUser);

        List<User> activeUsers = userRepository.findByStatus(UserStatus.ACTIVE);

        assertThat(activeUsers).hasSize(2);
        assertThat(activeUsers).allMatch(u -> u.getStatus() == UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should find all users by status PENDING_VERIFICATION")
    void shouldFindAllPendingUsers() {
        entityManager.persistAndFlush(testUser);
        entityManager.persistAndFlush(adminUser);
        entityManager.persistAndFlush(consultantUser);

        List<User> pendingUsers = userRepository.findByStatus(UserStatus.PENDING_VERIFICATION);

        assertThat(pendingUsers).hasSize(1);
        assertThat(pendingUsers.get(0).getStatus()).isEqualTo(UserStatus.PENDING_VERIFICATION);
    }

    // ==================== findByCreatedAtAfter Tests ====================

    @Test
    @DisplayName("Should find users created after specific date")
    void shouldFindUsersCreatedAfterDate() {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);

        entityManager.persistAndFlush(testUser);
        entityManager.persistAndFlush(adminUser);

        List<User> recentUsers = userRepository.findByCreatedAtAfter(yesterday);

        assertThat(recentUsers).hasSize(2);
    }

    @Test
    @DisplayName("Should return empty list when no users created after date")
    void shouldReturnEmptyWhenNoUsersAfterDate() {
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);

        entityManager.persistAndFlush(testUser);

        List<User> futureUsers = userRepository.findByCreatedAtAfter(tomorrow);

        assertThat(futureUsers).isEmpty();
    }

    // ==================== countByUserType Tests ====================

    @Test
    @DisplayName("Should count users by type CLIENT correctly")
    void shouldCountClientUsers() {
        entityManager.persistAndFlush(testUser);
        entityManager.persistAndFlush(adminUser);
        entityManager.persistAndFlush(consultantUser);

        long count = userRepository.countByUserType(UserType.CLIENT);

        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("Should return zero when no users of specified type exist")
    void shouldReturnZeroWhenNoUsersOfType() {
        entityManager.persistAndFlush(testUser);

        long count = userRepository.countByUserType(UserType.ADMIN);

        assertThat(count).isZero();
    }

    // ==================== countByStatus Tests ====================

    @Test
    @DisplayName("Should count users by status ACTIVE correctly")
    void shouldCountActiveUsers() {
        entityManager.persistAndFlush(testUser);
        entityManager.persistAndFlush(adminUser);
        entityManager.persistAndFlush(consultantUser);

        long count = userRepository.countByStatus(UserStatus.ACTIVE);

        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Should return zero when no users with specified status exist")
    void shouldReturnZeroWhenNoUsersWithStatus() {
        entityManager.persistAndFlush(testUser);

        long count = userRepository.countByStatus(UserStatus.SUSPENDED);

        assertThat(count).isZero();
    }

    // ==================== Edge Cases ====================

    @Test
    @DisplayName("Should handle case-sensitive email search correctly")
    void shouldHandleCaseSensitiveEmail() {
        entityManager.persistAndFlush(testUser);

        Optional<User> foundUser = userRepository.findByEmail("TEST@EXAMPLE.COM");

        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("Should handle special characters in email")
    void shouldHandleSpecialCharactersInEmail() {
        testUser.setEmail("test+special@example.com");
        entityManager.persistAndFlush(testUser);

        Optional<User> foundUser = userRepository.findByEmail("test+special@example.com");

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("test+special@example.com");
    }
}

