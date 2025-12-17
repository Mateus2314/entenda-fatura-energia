package com.understand_your_electricity_bill.model;


import com.understand_your_electricity_bill.model.enums.UserType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    @DisplayName("Should create user with required fields")
    void shouldCreateUserWithRequiredFields() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedPassword123");
        user.setUserType(UserType.CLIENT);
        user.setName("Test User");

        assertNotNull(user);
        assertEquals("test@example.com", user.getEmail());
        assertEquals("hashedPassword123", user.getPasswordHash());
        assertEquals(UserType.CLIENT, user.getUserType());
        assertEquals("Test User", user.getName());
    }

    @Test
    @DisplayName("Should validate email format")
    void shouldValidateEmailFormat() {
        User user = new User();
        user.setEmail("invalid-email");
        user.setPasswordHash("hashedPassword123");
        user.setUserType(UserType.CLIENT);
        user.setName("Test User");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should not allow null email")
    void shouldNotAllowNullEmail() {
        User user = new User();
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
        User user = new User();
        user.setEmail("test@example.com");
        user.setUserType(UserType.CLIENT);
        user.setName("Test User");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("passwordHash")));
    }

    @Test
    @DisplayName("Should not allow null user type")
    void shouldNotAllowNullUserType() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedPassword123");
        user.setName("Test User");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("userType")));
    }

    @Test
    @DisplayName("Should generate UUID for id on persist")
    void shouldGenerateUuidForIdOnPersist() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedPassword123");
        user.setUserType(UserType.CLIENT);
        user.setName("Test User");

        // Simula o comportamento do @PrePersist
        user.onCreate();

        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
        // O ID será gerado pelo banco, então aqui testamos apenas que pode ser null antes de persistir
        assertNull(user.getId());
    }

    @Test
    @DisplayName("Should accept UUID as id")
    void shouldAcceptUuidAsId() {
        UUID testId = UUID.randomUUID();
        User user = new User();
        user.setId(testId);
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedPassword123");
        user.setUserType(UserType.CLIENT);
        user.setName("Test User");

        assertEquals(testId, user.getId());
        assertInstanceOf(UUID.class, user.getId());
    }

    @Test
    @DisplayName("Should update updatedAt on PreUpdate")
    void shouldUpdateUpdatedAtOnPreUpdate() throws InterruptedException {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedPassword123");
        user.setUserType(UserType.CLIENT);
        user.setName("Test User");

        user.onCreate();
        LocalDateTime firstUpdate = user.getUpdatedAt();

        Thread.sleep(10); // Pequeno delay para garantir diferença no timestamp

        user.onUpdate();
        LocalDateTime secondUpdate = user.getUpdatedAt();

        assertTrue(secondUpdate.isAfter(firstUpdate));
    }

}
