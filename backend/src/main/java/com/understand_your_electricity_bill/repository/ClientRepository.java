package com.understand_your_electricity_bill.repository;

import com.understand_your_electricity_bill.model.Client;
import com.understand_your_electricity_bill.model.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Client entity.
 * Provides database access operations for the Client table.
 *
 * <p>All CRUD operations are inherited from JpaRepository:</p>
 * <ul>
 *   <li>save(Client) - Create or update</li>
 *   <li>findById(UUID) - Find by ID</li>
 *   <li>findAll() - List all clients</li>
 *   <li>deleteById(UUID) - Delete by ID</li>
 *   <li>count() - Count all clients</li>
 * </ul>
 *
 * @see Client
 * @see JpaRepository
 */

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {

    /**
     * Finds a client by their CPF (Brazilian individual taxpayer ID).
     *
     * @param cpf The CPF to search for (11 digits)
     * @return An Optional containing the client if found, or empty otherwise
     */
    Optional<Client> findByCpf(String cpf);

    /**
     * Checks if a client with the given CPF exists.
     * More efficient than findByCpf when you only need to check existence.
     *
     * @param cpf The CPF to check
     * @return true if a client with this CPF exists, false otherwise
     */
    boolean existsByCpf(String cpf);

    /**
     * Finds clients by their account status.
     *
     * @param status The account status to filter by
     * @return List of clients with the specified status
     */
    List<Client> findByStatus(UserStatus status);

    /**
     * Finds clients registered after a specific date.
     *
     * @param date The reference date
     * @return List of clients registered after the date
     */
    List<Client> findByRegistrationDateAfter(LocalDate date);

    /**
     * Finds clients by state (UF).
     *
     * @param state The state code (e.g., "SP", "RJ")
     * @return List of clients in the specified state
     */
    List<Client> findByState(String state);

    /**
     * Finds clients by city.
     *
     * @param city The city name
     * @return List of clients in the specified city
     */
    List<Client> findByCity(String city);

    /**
     * Finds clients by ZIP code.
     *
     * @param zipCode The ZIP code
     * @return List of clients with the specified ZIP code
     */
    List<Client> findByZipCode(String zipCode);

    /**
     * Counts clients by status.
     *
     * @param status The status to count
     * @return Number of clients with the specified status
     */
    long countByStatus(UserStatus status);

    /**
     * Counts clients by state.
     *
     * @param state The state to count
     * @return Number of clients in the specified state
     */
    long countByState(String state);

    /**
     * Finds clients with name containing the search term (case-insensitive).
     *
     * @param name The search term
     * @return List of clients matching the search
     */
    @Query("SELECT c FROM Client c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Client> findByNameContainingIgnoreCase(@Param("name") String name);

}
