package com.understand_your_electricity_bill.dto;

import com.understand_your_electricity_bill.model.enums.UserStatus;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO with essential Client information only.
 * Used for lists, dropdowns, and summary views.
 * Lightweight for performance in bulk operations.
 */
public record ClientSummaryDTO(
        UUID id,
        String name,
        String email,
        String cpf,
        String city,
        String state,
        UserStatus status,
        LocalDate registrationDate
) {
    /**
     * Get masked CPF for display (XXX.XXX.XXX-XX)
     */
    public String getMaskedCpf() {
        if (cpf == null || cpf.length() != 11) {
            return cpf;
        }
        return String.format("%s.%s.%s-%s",
                cpf.substring(0, 3),
                cpf.substring(3, 6),
                cpf.substring(6, 9),
                cpf.substring(9, 11)
        );
    }

    /**
     * Get client location as "City/State" or just "City" or "State"
     */
    public String getLocation() {
        if (city != null && state != null) {
            return city + "/" + state;
        } else if (city != null) {
            return city;
        } else if (state != null) {
            return state;
        }
        return "N/A";
    }

    /**
     * Check if client is active
     */
    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

    /**
     * Get display name with status indicator
     */
    public String getDisplayName() {
        return name + (isActive() ? "" : " [" + status + "]");
    }
}

