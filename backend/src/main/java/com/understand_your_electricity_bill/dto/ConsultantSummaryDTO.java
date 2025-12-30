package com.understand_your_electricity_bill.dto;

import com.understand_your_electricity_bill.model.enums.UserStatus;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO with essential Consultant information only.
 * Used for lists, dropdowns, and summary views.
 * Lightweight for performance in bulk operations.
 */
public record ConsultantSummaryDTO(
        UUID id,
        String consultantName,
        String company,
        String email,
        String cnpj,
        String city,
        String state,
        UserStatus status,
        LocalDate registrationDate,
        int managedClientsCount
) {
    /**
     * Get masked CNPJ for display (XX.XXX.XXX/XXXX-XX)
     */
    public String getMaskedCnpj() {
        if (cnpj == null || cnpj.length() != 14) {
            return cnpj;
        }
        return String.format("%s.%s.%s/%s-%s",
                cnpj.substring(0, 2),
                cnpj.substring(2, 5),
                cnpj.substring(5, 8),
                cnpj.substring(8, 12),
                cnpj.substring(12, 14)
        );
    }

    /**
     * Get consultant location as "City/State" or just "City" or "State"
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
     * Check if consultant is active
     */
    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

    /**
     * Get display name with status indicator
     */
    public String getDisplayName() {
        return consultantName + (isActive() ? "" : " [" + status + "]");
    }

    /**
     * Get company display with consultant name
     */
    public String getCompanyDisplay() {
        return company + " - " + consultantName;
    }

    /**
     * Check if consultant has managed clients
     */
    public boolean hasManagedClients() {
        return managedClientsCount > 0;
    }
}

