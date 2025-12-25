package com.understand_your_electricity_bill.dto;

import com.understand_your_electricity_bill.model.enums.UserType;

/**
 * DTO para encapsular a resposta de uma autenticação bem-sucedida.
 *
 * @param email O email do usuário autenticado.
 * @param userType O tipo do usuário (CLIENT, CONSULTANT, ADMIN).
 * @param token O token JWT gerado para a sessão.
 */

public record AuthenticationResponseDTO(
        String email,
        UserType userType,
        String token
) {
}
