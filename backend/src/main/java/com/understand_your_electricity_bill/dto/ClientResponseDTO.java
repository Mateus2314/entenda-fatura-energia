package com.understand_your_electricity_bill.dto;

import com.understand_your_electricity_bill.model.enums.UserStatus;
import com.understand_your_electricity_bill.model.enums.UserType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for returning complete Client information.
 * Excludes sensitive data like passwordHash.
 * Used for detailed views and API responses.
 */
public record ClientResponseDTO(
        UUID id,
        String email,
        String name,
        String phone,
        UserType userType,
        UserStatus status,
        String cpf,
        String address,
        String city,
        String state,
        String zipCode,
        LocalDate registrationDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        int consultantCount
) {
    /**
     * Builder pattern for easier construction from Entity
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private String email;
        private String name;
        private String phone;
        private UserType userType;
        private UserStatus status;
        private String cpf;
        private String address;
        private String city;
        private String state;
        private String zipCode;
        private LocalDate registrationDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private int consultantCount;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder userType(UserType userType) {
            this.userType = userType;
            return this;
        }

        public Builder status(UserStatus status) {
            this.status = status;
            return this;
        }

        public Builder cpf(String cpf) {
            this.cpf = cpf;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder state(String state) {
            this.state = state;
            return this;
        }

        public Builder zipCode(String zipCode) {
            this.zipCode = zipCode;
            return this;
        }

        public Builder registrationDate(LocalDate registrationDate) {
            this.registrationDate = registrationDate;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder consultantCount(int consultantCount) {
            this.consultantCount = consultantCount;
            return this;
        }

        public ClientResponseDTO build() {
            return new ClientResponseDTO(
                    id, email, name, phone, userType, status,
                    cpf, address, city, state, zipCode,
                    registrationDate, createdAt, updatedAt, consultantCount
            );
        }
    }

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
     * Get formatted ZIP code (XXXXX-XXX)
     */
    public String getFormattedZipCode() {
        if (zipCode == null || zipCode.length() != 8) {
            return zipCode;
        }
        return String.format("%s-%s",
                zipCode.substring(0, 5),
                zipCode.substring(5, 8)
        );
    }

    /**
     * Check if client has consultants
     */
    public boolean hasConsultants() {
        return consultantCount > 0;
    }
}

