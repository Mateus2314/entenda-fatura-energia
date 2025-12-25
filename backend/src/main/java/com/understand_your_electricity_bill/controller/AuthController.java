package com.understand_your_electricity_bill.controller;

import com.understand_your_electricity_bill.dto.AuthenticationRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticate(
            @Valid @RequestBody AuthenticationRequestDTO request
    ) {
        // Se o e-mail no 'request' for inválido, o Spring lançará uma exceção
        // MethodArgumentNotValidException antes mesmo de executar este código,
        // resultando em uma resposta de erro 400 (Bad Request).

        // Se a validação passar, a lógica de autenticação continua aqui.
        return ResponseEntity.ok("Autenticado com sucesso!");
    }

}
