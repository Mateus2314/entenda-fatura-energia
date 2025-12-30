package com.understand_your_electricity_bill.dto;

import com.understand_your_electricity_bill.model.enums.UserStatus;
import com.understand_your_electricity_bill.model.enums.UserType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for returning complete Consultant information.
 * Excludes sensitive data like passwordHash.
 * Used for detailed views and API responses.
 */
public record ConsultantResponseDTO(
        UUID id,
        String email,
        String name,
        String phone,
        UserType userType,
        UserStatus status,
        String consultantName,
        String company,
        String cnpj,
        String registrationNumber,
        String address,
        String city,
        String state,
        String zipCode,
        String companyLogo,
        LocalDate registrationDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        int managedClientsCount
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
        private String consultantName;
        private String company;
        private String cnpj;
        private String registrationNumber;
        private String address;
        private String city;
        private String state;
        private String zipCode;
        private String companyLogo;
        private LocalDate registrationDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private int managedClientsCount;

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

        public Builder consultantName(String consultantName) {
            this.consultantName = consultantName;
            return this;
        }

        public Builder company(String company) {
            this.company = company;
            return this;
        }

        public Builder cnpj(String cnpj) {
            this.cnpj = cnpj;
            return this;
        }

        public Builder registrationNumber(String registrationNumber) {
            this.registrationNumber = registrationNumber;
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

        public Builder companyLogo(String companyLogo) {
            this.companyLogo = companyLogo;
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

        public Builder managedClientsCount(int managedClientsCount) {
            this.managedClientsCount = managedClientsCount;
            return this;
        }

        public ConsultantResponseDTO build() {
            return new ConsultantResponseDTO(
                    id, email, name, phone, userType, status,
                    consultantName, company, cnpj, registrationNumber,
                    address, city, state, zipCode, companyLogo,
                    registrationDate, createdAt, updatedAt, managedClientsCount
            );
        }
    }

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
     * Check if consultant has managed clients
     */
    public boolean hasManagedClients() {
        return managedClientsCount > 0;
    }

    /**
     * Check if company logo is set
     */
    public boolean hasCompanyLogo() {
        return companyLogo != null && !companyLogo.isEmpty();
    }
}

