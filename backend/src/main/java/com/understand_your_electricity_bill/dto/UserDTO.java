package com.understand_your_electricity_bill.dto;

import com.understand_your_electricity_bill.model.enums.UserStatus;
import com.understand_your_electricity_bill.model.enums.UserType;

import java.util.UUID;

public record UserDTO(
        UUID id,
        String email,
        String name,
        String phone,
        UserType userType,
        UserStatus status
) {
}
