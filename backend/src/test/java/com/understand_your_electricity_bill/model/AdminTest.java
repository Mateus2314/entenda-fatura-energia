package com.understand_your_electricity_bill.model;

import com.understand_your_electricity_bill.model.enums.UserType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdminTest {

    private ValidatorFactory factory;
    private Validator validator;
    private Admin admin;

    @BeforeEach
    void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        admin = new Admin();
    }

    @Test
    @DisplayName("Should create Admin with all required fields")
    void shouldCreateAdminWithRequiredFields() {
        admin.setEmail("admin@example.com");
        admin.setPasswordHash("hashedPassword123");
        admin.setName("Admin User");
        admin.setRole("SYSTEM_ADMIN");

        assertNotNull(admin);
        assertEquals("admin@example.com", admin.getEmail());
        assertEquals("hashedPassword123", admin.getPasswordHash());
        assertEquals("Admin User", admin.getName());
        assertEquals("SYSTEM_ADMIN", admin.getRole());
        assertEquals(UserType.ADMIN, admin.getUserType());
    }

    @Test
    @DisplayName("Should not allow null role")
    void shouldNotAllowNullRole() {
        admin.setEmail("admin@example.com");
        admin.setPasswordHash("hashedPassword123");
        admin.setName("Admin User");

        Set<ConstraintViolation<Admin>> violations = validator.validate(admin);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("role")));
    }

    @Test
    @DisplayName("Should validate role max length")
    void shouldValidateRoleMaxLength() {
        String longRole = "A".repeat(51);
        admin.setEmail("admin@example.com");
        admin.setPasswordHash("hashedPassword123");
        admin.setName("Admin User");
        admin.setRole(longRole);

        Set<ConstraintViolation<Admin>> violations = validator.validate(admin);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("role")));
    }

    @Test
    @DisplayName("Should allow null permissions")
    void shouldAllowNullPermissions() {
        admin.setEmail("admin@example.com");
        admin.setPasswordHash("hashedPassword123");
        admin.setName("Admin User");
        admin.setRole("SYSTEM_ADMIN");
        admin.setPermissions(null);

        Set<ConstraintViolation<Admin>> violations = validator.validate(admin);
        assertTrue(violations.stream()
                .noneMatch(v -> v.getPropertyPath().toString().equals("permissions")));
    }

    @Test
    @DisplayName("Should store permissions as JSONB")
    void shouldStorePermissionsAsJsonb() {
        admin.setEmail("admin@example.com");
        admin.setPasswordHash("hashedPassword123");
        admin.setName("Admin User");
        admin.setRole("SYSTEM_ADMIN");
        admin.setPermissions("{\"read\": true, \"write\": true, \"delete\": false}");

        assertNotNull(admin.getPermissions());
        assertTrue(admin.getPermissions().contains("read"));
        assertTrue(admin.getPermissions().contains("write"));
    }

    @Test
    @DisplayName("Should inherit all User properties")
    void shouldInheritAllUserProperties() {
        admin.setEmail("admin@example.com");
        admin.setPasswordHash("hashedPassword123");
        admin.setName("Admin User");
        admin.setRole("SYSTEM_ADMIN");

        assertInstanceOf(User.class, admin);
        assertEquals("admin@example.com", admin.getEmail());
        assertEquals("Admin User", admin.getName());
        assertEquals(UserType.ADMIN, admin.getUserType());
    }

    @Test
    @DisplayName("Should automatically set UserType to ADMIN")
    void shouldAutomaticallySetUserTypeToAdmin() {
        Admin newAdmin = new Admin();

        assertEquals(UserType.ADMIN, newAdmin.getUserType());
    }

    @Test
    @DisplayName("Should accept valid role names")
    void shouldAcceptValidRoleNames() {
        admin.setEmail("admin@example.com");
        admin.setPasswordHash("hashedPassword123");
        admin.setName("Admin User");

        // Test different valid roles
        String[] validRoles = {"SYSTEM_ADMIN", "MODERATOR", "SUPPORT", "ANALYST"};

        for (String role : validRoles) {
            admin.setRole(role);
            Set<ConstraintViolation<Admin>> violations = validator.validate(admin);
            assertTrue(violations.stream()
                    .noneMatch(v -> v.getPropertyPath().toString().equals("role")));
        }
    }

}
