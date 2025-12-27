package com.understand_your_electricity_bill.model;

import com.understand_your_electricity_bill.model.enums.UserType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "consultants")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter
@Setter
public class Consultant extends User {

    @NotBlank(message = "Consultant name is required")
    @Size(max = 255, message = "Consultant name must not exceed 255 characters")
    @Column(name = "consultant_name", nullable = false, length = 255)
    private String consultantName;

    @NotBlank(message = "Company is required")
    @Size(max = 255, message = "Company name must not exceed 255 characters")
    @Column(name = "company", nullable = false, length = 255)
    private String company;

    @NotBlank(message = "CNPJ is required")
    @Pattern(regexp = "^\\d{14}$", message = "CNPJ must contain exactly 14 digits")
    @Column(name = "cnpj", nullable = false, unique = true, length = 14)
    private String cnpj;

    @Size(max = 50, message = "Registration number must not exceed 50 characters")
    @Column(name = "registration_number", length = 50)
    private String registrationNumber;

    @NotBlank(message = "Address is required")
    @Size(max = 500, message = "Address must not exceed 500 characters")
    @Column(name = "address", nullable = false, length = 500)
    private String address;

    @Size(max = 100, message = "City must not exceed 100 characters")
    @Column(name = "city", length = 100)
    private String city;

    @Pattern(regexp = "^[A-Z]{2}$", message = "State must be 2 uppercase letters")
    @Column(name = "state", length = 2)
    private String state;

    @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "ZIP code must be in format 00000-000 or 00000000")
    @Column(name = "zip_code", length = 10)
    private String zipCode;

    @Column(name = "company_logo", columnDefinition = "TEXT")
    private String companyLogo;

    @Column(name = "registration_date", nullable = false)
    private LocalDate registrationDate;

    // Many-to-Many relationship with Clients
    @ManyToMany
    @JoinTable(
        name = "consultant_clients",
        joinColumns = @JoinColumn(name = "consultant_id"),
        inverseJoinColumns = @JoinColumn(name = "client_id")
    )
    private Set<Client> managedClients = new HashSet<>();

    public Consultant() {
        super();
        this.setUserType(UserType.CONSULTANT);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        if (this.registrationDate == null) {
            this.registrationDate = LocalDate.now();
        }
    }

    /**
     * Adiciona um cliente à lista de clientes gerenciados
     * Mantém consistência bidirecional da relação Many-to-Many
     * @param client Cliente a ser adicionado
     */
    public void addManagedClient(Client client) {
        this.managedClients.add(client);
        client.getConsultants().add(this);
    }

    /**
     * Remove um cliente da lista de clientes gerenciados
     * Mantém consistência bidirecional da relação Many-to-Many
     * @param client Cliente a ser removido
     */
    public void removeManagedClient(Client client) {
        this.managedClients.remove(client);
        client.getConsultants().remove(this);
    }

}
