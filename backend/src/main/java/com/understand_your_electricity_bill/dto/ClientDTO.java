package com.understand_your_electricity_bill.dto;


import com.understand_your_electricity_bill.model.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record ClientDTO(
        UUID id,
        String name,
        String email,
        String phone,
        UserStatus status,
        String cpf,
        String address,
        String city,
        String state,
        String zipCode,
        LocalDate registrationDate) {
}
