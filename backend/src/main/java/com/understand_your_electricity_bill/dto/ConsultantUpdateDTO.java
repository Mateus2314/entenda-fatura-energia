package com.understand_your_electricity_bill.dto;

import com.understand_your_electricity_bill.model.enums.UserStatus;
import jakarta.validation.constraints.*;

import java.util.UUID;

/**
 * DTO for updating an existing Consultant.
 * Includes ID for identification and specific validations.
 * Fields are optional (nullable) to allow partial updates.
 * Password change is handled separately for security.
 * CNPJ cannot be updated (immutable business rule).
 */
public record ConsultantUpdateDTO(
        @NotNull(message = "Consultant ID is required for update")
        UUID id,

        @Email(message = "Email must be valid")
        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
                message = "Email format is invalid")
        String email,

        @Size(max = 255, message = "Name must not exceed 255 characters")
        String name,

        @Pattern(regexp = "^\\+?[0-9]{10,15}$",
                message = "Phone must contain 10-15 digits and may start with +")
        String phone,

        @Size(max = 255, message = "Consultant name must not exceed 255 characters")
        String consultantName,

        @Size(max = 255, message = "Company name must not exceed 255 characters")
        String company,

        @Size(max = 50, message = "Registration number must not exceed 50 characters")
        String registrationNumber,

        @Size(max = 500, message = "Address must not exceed 500 characters")
        String address,

        @Size(max = 100, message = "City must not exceed 100 characters")
        String city,

        @Size(max = 2, message = "State must be 2 characters (e.g., SP)")
        @Pattern(regexp = "^[A-Z]{2}$", message = "State must be 2 uppercase letters")
        String state,

        @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "ZIP code must be in format 12345-678 or 12345678")
        String zipCode,

        String companyLogo,

        UserStatus status
) {
    /**
     * Compact constructor with validation and normalization
     */
    public ConsultantUpdateDTO {
        // Trim and normalize strings
        if (email != null) email = email.trim().toLowerCase();
        if (name != null) name = name.trim();
        if (consultantName != null) consultantName = consultantName.trim();
        if (company != null) company = company.trim();
        if (registrationNumber != null) registrationNumber = registrationNumber.trim();
        if (phone != null) phone = phone.trim();
        if (address != null) address = address.trim();
        if (city != null) city = city.trim();
        if (state != null) state = state.trim().toUpperCase();
        if (zipCode != null) zipCode = zipCode.replaceAll("[^0-9]", ""); // Remove dashes
        if (companyLogo != null) companyLogo = companyLogo.trim();
    }

    /**
     * Check if at least one field is being updated
     */
    public boolean hasUpdates() {
        return email != null || name != null || phone != null ||
               consultantName != null || company != null || registrationNumber != null ||
               address != null || city != null || state != null ||
               zipCode != null || companyLogo != null || status != null;
    }
}

