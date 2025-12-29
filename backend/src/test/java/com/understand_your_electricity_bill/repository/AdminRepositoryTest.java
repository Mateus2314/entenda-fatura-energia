package com.understand_your_electricity_bill.repository;

import com.understand_your_electricity_bill.model.Admin;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = com.understand_your_electricity_bill.understand_your_electricity_bill.UnderstandYourElectricityBillApplication.class)
@ActiveProfiles("test")
class AdminRepositoryTest {

    // Load .env file
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
    private AdminRepository adminRepository;

    private Admin testAdmin1;
    private Admin testAdmin2;
    private Admin testAdmin3;
    private Admin testAdmin4;

    @BeforeEach
    void setUp() {
        // Admin 1 - Super Admin (ACTIVE)
        testAdmin1 = new Admin();
        testAdmin1.setEmail("superadmin@system.com");
        testAdmin1.setPasswordHash("$2a$10$hashedPassword123");
        testAdmin1.setUserType(UserType.ADMIN);
        testAdmin1.setName("Super Administrator");
        testAdmin1.setPhone("+5511999999991");
        testAdmin1.setStatus(UserStatus.ACTIVE);
        testAdmin1.setRole("SUPER_ADMIN");
        testAdmin1.setPermissions("{\"all\": true, \"users\": \"full\", \"bills\": \"full\", \"reports\": \"full\", \"system\": \"full\"}");
        testAdmin1.setCreatedAt(LocalDateTime.now());
        testAdmin1.setUpdatedAt(LocalDateTime.now());

        // Admin 2 - Support Admin (ACTIVE)
        testAdmin2 = new Admin();
        testAdmin2.setEmail("support@system.com");
        testAdmin2.setPasswordHash("$2a$10$hashedPassword456");
        testAdmin2.setUserType(UserType.ADMIN);
        testAdmin2.setName("Support Administrator");
        testAdmin2.setPhone("+5511999999992");
        testAdmin2.setStatus(UserStatus.ACTIVE);
        testAdmin2.setRole("SUPPORT_ADMIN");
        testAdmin2.setPermissions("{\"users\": \"read-write\", \"bills\": \"read\", \"reports\": \"read\"}");
        testAdmin2.setCreatedAt(LocalDateTime.now());
        testAdmin2.setUpdatedAt(LocalDateTime.now());

        // Admin 3 - Finance Admin (ACTIVE)
        testAdmin3 = new Admin();
        testAdmin3.setEmail("finance@system.com");
        testAdmin3.setPasswordHash("$2a$10$hashedPassword789");
        testAdmin3.setUserType(UserType.ADMIN);
        testAdmin3.setName("Finance Administrator");
        testAdmin3.setPhone("+5511999999993");
        testAdmin3.setStatus(UserStatus.ACTIVE);
        testAdmin3.setRole("FINANCE_ADMIN");
        testAdmin3.setPermissions("{\"bills\": \"full\", \"reports\": \"full\", \"payments\": \"full\"}");
        testAdmin3.setCreatedAt(LocalDateTime.now());
        testAdmin3.setUpdatedAt(LocalDateTime.now());

        // Admin 4 - Tech Admin (SUSPENDED)
        testAdmin4 = new Admin();
        testAdmin4.setEmail("tech@system.com");
        testAdmin4.setPasswordHash("$2a$10$hashedPasswordABC");
        testAdmin4.setUserType(UserType.ADMIN);
        testAdmin4.setName("Tech Administrator");
        testAdmin4.setPhone("+5511999999994");
        testAdmin4.setStatus(UserStatus.SUSPENDED);
        testAdmin4.setRole("TECH_ADMIN");
        testAdmin4.setPermissions("{\"system\": \"full\", \"logs\": \"full\", \"monitoring\": \"full\"}");
        testAdmin4.setCreatedAt(LocalDateTime.now());
        testAdmin4.setUpdatedAt(LocalDateTime.now());
    }

    // ========================================================================
    // BASIC CRUD TESTS
    // ========================================================================

    @Test
    @DisplayName("Should save admin successfully")
    void shouldSaveAdmin() {
        // When
        Admin savedAdmin = adminRepository.save(testAdmin1);

        // Then
        assertThat(savedAdmin).isNotNull();
        assertThat(savedAdmin.getId()).isNotNull();
        assertThat(savedAdmin.getRole()).isEqualTo("SUPER_ADMIN");
        assertThat(savedAdmin.getUserType()).isEqualTo(UserType.ADMIN);
    }

    @Test
    @DisplayName("Should find admin by ID")
    void shouldFindAdminById() {
        // Given
        Admin savedAdmin = entityManager.persistAndFlush(testAdmin1);

        // When
        Optional<Admin> foundAdmin = adminRepository.findById(savedAdmin.getId());

        // Then
        assertThat(foundAdmin).isPresent();
        assertThat(foundAdmin.get().getRole()).isEqualTo("SUPER_ADMIN");
        assertThat(foundAdmin.get().getEmail()).isEqualTo("superadmin@system.com");
    }

    @Test
    @DisplayName("Should find all admins")
    void shouldFindAllAdmins() {
        // Given
        entityManager.persistAndFlush(testAdmin1);
        entityManager.persistAndFlush(testAdmin2);
        entityManager.persistAndFlush(testAdmin3);

        // When
        List<Admin> admins = adminRepository.findAll();

        // Then
        assertThat(admins).hasSizeGreaterThanOrEqualTo(3);
    }

    @Test
    @DisplayName("Should update admin")
    void shouldUpdateAdmin() {
        // Given
        Admin savedAdmin = entityManager.persistAndFlush(testAdmin1);
        String newRole = "MASTER_ADMIN";

        // When
        savedAdmin.setRole(newRole);
        Admin updatedAdmin = adminRepository.save(savedAdmin);

        // Then
        assertThat(updatedAdmin.getRole()).isEqualTo(newRole);
    }

    @Test
    @DisplayName("Should delete admin")
    void shouldDeleteAdmin() {
        // Given
        Admin savedAdmin = entityManager.persistAndFlush(testAdmin1);
        Long countBefore = adminRepository.count();

        // When
        adminRepository.delete(savedAdmin);
        Long countAfter = adminRepository.count();

        // Then
        assertThat(countAfter).isLessThan(countBefore);
    }

    // ========================================================================
    // SEARCH BY ROLE TESTS
    // ========================================================================

    @Test
    @DisplayName("Should find admins by role")
    void shouldFindByRole() {
        // Given
        entityManager.persistAndFlush(testAdmin1);
        entityManager.persistAndFlush(testAdmin2);

        // When
        List<Admin> superAdmins = adminRepository.findByRole("SUPER_ADMIN");

        // Then
        assertThat(superAdmins).isNotEmpty();
        assertThat(superAdmins).allMatch(a -> a.getRole().equals("SUPER_ADMIN"));
    }

    @Test
    @DisplayName("Should find first admin by role")
    void shouldFindFirstByRole() {
        // Given
        entityManager.persistAndFlush(testAdmin1);

        // When
        Optional<Admin> superAdmin = adminRepository.findFirstByRole("SUPER_ADMIN");

        // Then
        assertThat(superAdmin).isPresent();
        assertThat(superAdmin.get().getRole()).isEqualTo("SUPER_ADMIN");
        assertThat(superAdmin.get().getName()).isEqualTo("Super Administrator");
    }

    @Test
    @DisplayName("Should check if admin exists by role")
    void shouldCheckExistsByRole() {
        // Given
        entityManager.persistAndFlush(testAdmin1);

        // When
        boolean exists = adminRepository.existsByRole("SUPER_ADMIN");
        boolean notExists = adminRepository.existsByRole("NONEXISTENT_ROLE");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should find admins by role containing (case-insensitive)")
    void shouldFindByRoleContaining() {
        // Given
        entityManager.persistAndFlush(testAdmin1);
        entityManager.persistAndFlush(testAdmin2);
        entityManager.persistAndFlush(testAdmin3);

        // When
        List<Admin> adminsWithAdmin = adminRepository.findByRoleContainingIgnoreCase("admin");

        // Then
        assertThat(adminsWithAdmin).hasSizeGreaterThanOrEqualTo(3);
        assertThat(adminsWithAdmin).allMatch(a -> a.getRole().toLowerCase().contains("admin"));
    }

    @Test
    @DisplayName("Should count admins by role")
    void shouldCountByRole() {
        // Given
        entityManager.persistAndFlush(testAdmin1);
        entityManager.persistAndFlush(testAdmin2);

        // When
        long superAdminCount = adminRepository.countByRole("SUPER_ADMIN");
        long supportAdminCount = adminRepository.countByRole("SUPPORT_ADMIN");

        // Then
        assertThat(superAdminCount).isGreaterThanOrEqualTo(1);
        assertThat(supportAdminCount).isGreaterThanOrEqualTo(1);
    }

    // ========================================================================
    // SEARCH BY STATUS TESTS
    // ========================================================================

    @Test
    @DisplayName("Should find admins by status")
    void shouldFindByStatus() {
        // Given
        entityManager.persistAndFlush(testAdmin1);
        entityManager.persistAndFlush(testAdmin4);

        // When
        List<Admin> activeAdmins = adminRepository.findByStatus(UserStatus.ACTIVE);
        List<Admin> suspendedAdmins = adminRepository.findByStatus(UserStatus.SUSPENDED);

        // Then
        assertThat(activeAdmins).isNotEmpty();
        assertThat(activeAdmins).allMatch(a -> a.getStatus() == UserStatus.ACTIVE);
        assertThat(suspendedAdmins).isNotEmpty();
        assertThat(suspendedAdmins).allMatch(a -> a.getStatus() == UserStatus.SUSPENDED);
    }

    @Test
    @DisplayName("Should find active admins")
    void shouldFindActiveAdmins() {
        // Given
        entityManager.persistAndFlush(testAdmin1);
        entityManager.persistAndFlush(testAdmin2);
        entityManager.persistAndFlush(testAdmin4); // SUSPENDED

        // When
        List<Admin> activeAdmins = adminRepository.findActiveAdmins();

        // Then
        assertThat(activeAdmins).isNotEmpty();
        assertThat(activeAdmins).allMatch(a -> a.getStatus() == UserStatus.ACTIVE);
        assertThat(activeAdmins).noneMatch(a -> a.getStatus() == UserStatus.SUSPENDED);
    }

    @Test
    @DisplayName("Should count admins by status")
    void shouldCountByStatus() {
        // Given
        entityManager.persistAndFlush(testAdmin1);
        entityManager.persistAndFlush(testAdmin2);
        entityManager.persistAndFlush(testAdmin3);
        entityManager.persistAndFlush(testAdmin4);

        // When
        long activeCount = adminRepository.countByStatus(UserStatus.ACTIVE);
        long suspendedCount = adminRepository.countByStatus(UserStatus.SUSPENDED);

        // Then
        assertThat(activeCount).isGreaterThanOrEqualTo(3);
        assertThat(suspendedCount).isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("Should count active admins")
    void shouldCountActiveAdmins() {
        // Given
        entityManager.persistAndFlush(testAdmin1);
        entityManager.persistAndFlush(testAdmin2);
        entityManager.persistAndFlush(testAdmin4);

        // When
        long activeCount = adminRepository.countActiveAdmins();

        // Then
        assertThat(activeCount).isGreaterThanOrEqualTo(2);
    }

    // ========================================================================
    // SEARCH BY EMAIL TESTS
    // ========================================================================

    @Test
    @DisplayName("Should find admin by email")
    void shouldFindByEmail() {
        // Given
        entityManager.persistAndFlush(testAdmin1);

        // When
        Optional<Admin> foundAdmin = adminRepository.findByEmail("superadmin@system.com");

        // Then
        assertThat(foundAdmin).isPresent();
        assertThat(foundAdmin.get().getEmail()).isEqualTo("superadmin@system.com");
        assertThat(foundAdmin.get().getRole()).isEqualTo("SUPER_ADMIN");
    }

    @Test
    @DisplayName("Should check if admin exists by email")
    void shouldCheckExistsByEmail() {
        // Given
        entityManager.persistAndFlush(testAdmin1);

        // When
        boolean exists = adminRepository.existsByEmail("superadmin@system.com");
        boolean notExists = adminRepository.existsByEmail("nonexistent@system.com");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should return empty when email not found")
    void shouldReturnEmptyWhenEmailNotFound() {
        // When
        Optional<Admin> foundAdmin = adminRepository.findByEmail("notfound@system.com");

        // Then
        assertThat(foundAdmin).isEmpty();
    }

    // ========================================================================
    // SEARCH BY NAME TESTS
    // ========================================================================

    @Test
    @DisplayName("Should find admins by name containing (case-insensitive)")
    void shouldFindByNameContaining() {
        // Given
        entityManager.persistAndFlush(testAdmin1);
        entityManager.persistAndFlush(testAdmin2);
        entityManager.persistAndFlush(testAdmin3);

        // When
        List<Admin> adminsWithSuper = adminRepository.findByNameContainingIgnoreCase("super");
        List<Admin> adminsWithAdmin = adminRepository.findByNameContainingIgnoreCase("administrator");

        // Then
        assertThat(adminsWithSuper).isNotEmpty();
        assertThat(adminsWithSuper).anyMatch(a -> a.getName().toLowerCase().contains("super"));
        assertThat(adminsWithAdmin).hasSizeGreaterThanOrEqualTo(3);
    }

    // ========================================================================
    // PERMISSIONS TESTS (JSONB)
    // ========================================================================

    @Test
    @DisplayName("Should validate permissions JSON structure")
    void shouldValidatePermissionsJson() {
        // Given
        Admin savedAdmin = entityManager.persistAndFlush(testAdmin1);

        // When
        Optional<Admin> foundAdmin = adminRepository.findById(savedAdmin.getId());

        // Then
        assertThat(foundAdmin).isPresent();
        assertThat(foundAdmin.get().getPermissions()).isNotNull();
        assertThat(foundAdmin.get().getPermissions()).contains("\"all\"");
        assertThat(foundAdmin.get().getPermissions()).contains("\"users\"");
    }

    // ========================================================================
    // INHERITANCE TESTS
    // ========================================================================

    @Test
    @DisplayName("Should inherit User properties correctly")
    void shouldInheritUserProperties() {
        // Given
        Admin savedAdmin = entityManager.persistAndFlush(testAdmin1);

        // When
        Optional<Admin> foundAdmin = adminRepository.findById(savedAdmin.getId());

        // Then
        assertThat(foundAdmin).isPresent();
        assertThat(foundAdmin.get().getEmail()).isEqualTo("superadmin@system.com");
        assertThat(foundAdmin.get().getName()).isEqualTo("Super Administrator");
        assertThat(foundAdmin.get().getPhone()).isEqualTo("+5511999999991");
        assertThat(foundAdmin.get().getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(foundAdmin.get().getUserType()).isEqualTo(UserType.ADMIN);
    }

    @Test
    @DisplayName("Should have user_type automatically set to ADMIN")
    void shouldHaveUserTypeAdmin() {
        // Given
        Admin newAdmin = new Admin();
        newAdmin.setEmail("test@system.com");
        newAdmin.setPasswordHash("hash");
        newAdmin.setName("Test Admin");
        newAdmin.setStatus(UserStatus.ACTIVE);
        newAdmin.setRole("TEST_ADMIN");
        newAdmin.setCreatedAt(LocalDateTime.now());
        newAdmin.setUpdatedAt(LocalDateTime.now());

        // When
        Admin savedAdmin = entityManager.persistAndFlush(newAdmin);

        // Then
        assertThat(savedAdmin.getUserType()).isEqualTo(UserType.ADMIN);
    }

    // ========================================================================
    // EDGE CASES AND VALIDATION TESTS
    // ========================================================================

    @Test
    @DisplayName("Should handle null permissions")
    void shouldHandleNullPermissions() {
        // Given
        testAdmin1.setPermissions(null);
        Admin savedAdmin = entityManager.persistAndFlush(testAdmin1);

        // When
        Optional<Admin> foundAdmin = adminRepository.findById(savedAdmin.getId());

        // Then
        assertThat(foundAdmin).isPresent();
        assertThat(foundAdmin.get().getPermissions()).isNull();
    }

    @Test
    @DisplayName("Should return empty list when no admins with role exist")
    void shouldReturnEmptyListWhenNoAdminsWithRole() {
        // When
        List<Admin> admins = adminRepository.findByRole("NONEXISTENT_ROLE");

        // Then
        assertThat(admins).isEmpty();
    }

    @Test
    @DisplayName("Should handle multiple admins with different roles")
    void shouldHandleMultipleAdminsWithDifferentRoles() {
        // Given
        entityManager.persistAndFlush(testAdmin1);
        entityManager.persistAndFlush(testAdmin2);
        entityManager.persistAndFlush(testAdmin3);
        entityManager.persistAndFlush(testAdmin4);

        // When
        List<Admin> allAdmins = adminRepository.findAll();

        // Then
        assertThat(allAdmins).hasSizeGreaterThanOrEqualTo(4);
        assertThat(allAdmins).extracting(Admin::getRole)
                .contains("SUPER_ADMIN", "SUPPORT_ADMIN", "FINANCE_ADMIN", "TECH_ADMIN");
    }

    @Test
    @DisplayName("Should maintain referential integrity with users table")
    void shouldMaintainReferentialIntegrity() {
        // Given
        Admin savedAdmin = entityManager.persistAndFlush(testAdmin1);
        UUID adminId = savedAdmin.getId();

        // When
        entityManager.clear();
        Optional<Admin> foundAdmin = adminRepository.findById(adminId);

        // Then
        assertThat(foundAdmin).isPresent();
        assertThat(foundAdmin.get().getId()).isEqualTo(adminId);
    }
}

