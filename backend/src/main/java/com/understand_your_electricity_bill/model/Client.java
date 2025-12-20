package com.understand_your_electricity_bill.model;

import com.understand_your_electricity_bill.model.enums.UserType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "clients")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter
@Setter
public class Client extends User {

    @NotBlank(message = "Address is required")
    @Size(max = 500, message = "Address must not exceed 500 characters")
    @Column(name = "address", nullable = false, length = 500)
    private String address;

    @Size(max = 100, message = "City must not exceed 100 characters")
    @Column(name = "city", length = 100)
    private String city;

    @Size(max = 2, message = "State must be 2 characters (e.g., SP)")
    @Column(name = "state", length = 2)
    private String state;

    @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "ZIP code must be in format 12345-678")
    @Column(name = "zip_code", length = 10)
    private String zipCode;

    @NotBlank(message = "CPF is required")
    @Pattern(regexp = "^\\d{11}$", message = "CPF must contain exactly 11 digits")
    @Column(name = "cpf", nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(name = "registration_date", nullable = false)
    private LocalDate registrationDate;

    public Client() {
        super();
        this.setUserType(UserType.CLIENT);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        if (this.registrationDate == null) {
            this.registrationDate = LocalDate.now();
        }
    }

}
