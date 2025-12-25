package com.understand_your_electricity_bill.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for user authentication requests.
 *
 * @param email    The user's email. Must not be blank and must be a valid email format.
 * @param password The user's password. Must not be blank.
 */

public record AuthenticationRequestDTO(

        @NotBlank(message = "O e-mail não pode estar em branco")
        @Email(message = "O formato do e-mail é inválido")
        @Size(max = 255, message = "O email não pode exceder 255 caracteres.")
        String email,

        @NotBlank(message = "A senha não pode estar em branco")
        @Size(min = 8, max = 100, message = "A senha deve ter entre 8 e 100 caracteres")
        String password
) {
}
