package com.understand_your_electricity_bill.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private JwtService jwtService;
    private final String testEmail = "test@example.com";

    @BeforeEach
    void setUp() {
        // Usamos uma chave secreta longa e codificada em Base64 para o teste
        String secretKey = "======================SECRET=========================";
        long expiration = 3600000; // 1 hora
        jwtService = new JwtService(secretKey, expiration);
    }

    @Test
    @DisplayName("Should generate a non-empty JWT token for a given email")
    void shouldGenerateToken() {
        // When
        String token = jwtService.generateToken(testEmail);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        // Um token JWT válido tem duas casas decimais
        assertEquals(2, token.chars().filter(ch -> ch == '.').count());
    }

    @Test
    @DisplayName("Should extract the correct email from a valid JWT token")
    void shouldExtractEmailFromToken() {
        // Given
        String token = jwtService.generateToken(testEmail);

        // When
        String extractedEmail = jwtService.extractEmail(token);

        // Then
        assertEquals(testEmail, extractedEmail);
    }

    @Test
    @DisplayName("Should validate a correctly signed and non-expired token")
    void shouldValidateToken() {
        // Given
        String token = jwtService.generateToken(testEmail);

        // When
        boolean isValid = jwtService.isTokenValid(token, testEmail);

        // Then
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should invalidate a token with the wrong email")
    void shouldInvalidateTokenWithWrongEmail() {
        // Given
        String token = jwtService.generateToken(testEmail);

        // When
        boolean isValid = jwtService.isTokenValid(token, "wrong@example.com");

        // Then
        assertFalse(isValid);
    }

}
