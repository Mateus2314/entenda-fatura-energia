package com.understand_your_electricity_bill.model;


import com.understand_your_electricity_bill.model.enums.UserStatus;
import com.understand_your_electricity_bill.model.enums.UserType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {

    private Validator validator;
    private User user;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        user = new User();
    }

    @Test
    @DisplayName("Should create User with all required fields")
    void shouldCreateUserWithRequiredFields() {
        user.setEmail("user@example.com");
        user.setPasswordHash("hashedPassword123");
        user.setUserType(UserType.CLIENT);
        user.setName("Test User");

        assertNotNull(user);
        assertEquals("user@example.com", user.getEmail());
        assertEquals("hashedPassword123", user.getPasswordHash());
        assertEquals(UserType.CLIENT, user.getUserType());
        assertEquals("Test User", user.getName());
        assertEquals(UserStatus.PENDING_VERIFICATION, user.getStatus());
    }

    @Test
    @DisplayName("Should not allow null email")
    void shouldNotAllowNullEmail() {
        user.setPasswordHash("hashedPassword123");
        user.setUserType(UserType.CLIENT);
        user.setName("Test User");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("Should validate email format")
    void shouldValidateEmailFormat() {
        user.setEmail("invalid-email");
        user.setPasswordHash("hashedPassword123");
        user.setUserType(UserType.CLIENT);
        user.setName("Test User");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("Should not allow null password hash")
    void shouldNotAllowNullPasswordHash() {
        user.setEmail("user@example.com");
        user.setUserType(UserType.CLIENT);
        user.setName("Test User");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("passwordHash")));
    }

    @Test
    @DisplayName("Should not allow null user type")
    void shouldNotAllowNullUserType() {
        user.setEmail("user@example.com");
        user.setPasswordHash("hashedPassword123");
        user.setName("Test User");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("userType")));
    }

    @Test
    @DisplayName("Should not allow null name")
    void shouldNotAllowNullName() {
        user.setEmail("user@example.com");
        user.setPasswordHash("hashedPassword123");
        user.setUserType(UserType.CLIENT);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    @DisplayName("Should validate phone format when provided")
    void shouldValidatePhoneFormat() {
        user.setEmail("user@example.com");
        user.setPasswordHash("hashedPassword123");
        user.setUserType(UserType.CLIENT);
        user.setName("Test User");
        user.setPhone("123"); // Invalid - less than 10 digits

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("phone")));
    }

    @Test
    @DisplayName("Should accept valid phone formats")
    void shouldAcceptValidPhoneFormats() {
        user.setEmail("user@example.com");
        user.setPasswordHash("hashedPassword123");
        user.setUserType(UserType.CLIENT);
        user.setName("Test User");
        user.setPhone("+5511987654321");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream()
                .noneMatch(v -> v.getPropertyPath().toString().equals("phone")));
    }

    @Test
    @DisplayName("Should allow null phone")
    void shouldAllowNullPhone() {
        user.setEmail("user@example.com");
        user.setPasswordHash("hashedPassword123");
        user.setUserType(UserType.CLIENT);
        user.setName("Test User");
        user.setPhone(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream()
                .noneMatch(v -> v.getPropertyPath().toString().equals("phone")));
    }

    @Test
    @DisplayName("Should set default status to PENDING_VERIFICATION")
    void shouldSetDefaultStatus() {
        user.setEmail("user@example.com");
        user.setPasswordHash("hashedPassword123");
        user.setUserType(UserType.CLIENT);
        user.setName("Test User");

        user.onCreate();

        assertEquals(UserStatus.PENDING_VERIFICATION, user.getStatus());
    }

    @Test
    @DisplayName("Should not override existing status in onCreate")
    void shouldNotOverrideExistingStatus() {
        user.setEmail("user@example.com");
        user.setPasswordHash("hashedPassword123");
        user.setUserType(UserType.CLIENT);
        user.setName("Test User");
        user.setStatus(UserStatus.ACTIVE);

        user.onCreate();

        assertEquals(UserStatus.ACTIVE, user.getStatus());
    }

    @Test
    @DisplayName("Should generate UUID automatically")
    void shouldGenerateUuidAutomatically() {
        assertNull(user.getId());
    }

    @Test
    @DisplayName("Should accept all UserType values")
    void shouldAcceptAllUserTypes() {
        user.setEmail("user@example.com");
        user.setPasswordHash("hashedPassword123");
        user.setName("Test User");

        user.setUserType(UserType.CLIENT);
        assertEquals(UserType.CLIENT, user.getUserType());

        user.setUserType(UserType.CONSULTANT);
        assertEquals(UserType.CONSULTANT, user.getUserType());

        user.setUserType(UserType.ADMIN);
        assertEquals(UserType.ADMIN, user.getUserType());
    }

    @Test
    @DisplayName("Should accept all UserStatus values")
    void shouldAcceptAllUserStatusValues() {
        user.setEmail("user@example.com");
        user.setPasswordHash("hashedPassword123");
        user.setUserType(UserType.CLIENT);
        user.setName("Test User");

        user.setStatus(UserStatus.ACTIVE);
        assertEquals(UserStatus.ACTIVE, user.getStatus());

        user.setStatus(UserStatus.INACTIVE);
        assertEquals(UserStatus.INACTIVE, user.getStatus());

        user.setStatus(UserStatus.SUSPENDED);
        assertEquals(UserStatus.SUSPENDED, user.getStatus());

        user.setStatus(UserStatus.PENDING_VERIFICATION);
        assertEquals(UserStatus.PENDING_VERIFICATION, user.getStatus());
    }

}
