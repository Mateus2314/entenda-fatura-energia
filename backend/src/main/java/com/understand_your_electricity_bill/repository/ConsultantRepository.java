package com.understand_your_electricity_bill.repository;


import com.understand_your_electricity_bill.model.Consultant;
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
 * Repository interface for Consultant entity.
 * Provides database access operations for the Consultant table.
 *
 * <p>All CRUD operations are inherited from JpaRepository:</p>
 * <ul>
 *   <li>save(Consultant) - Create or update</li>
 *   <li>findById(UUID) - Find by ID</li>
 *   <li>findAll() - List all consultants</li>
 *   <li>deleteById(UUID) - Delete by ID</li>
 *   <li>count() - Count all consultants</li>
 * </ul>
 *
 * @see Consultant
 * @see JpaRepository
 */

@Repository
public interface ConsultantRepository extends JpaRepository<Consultant, UUID> {

    /**
     * Finds a consultant by their CNPJ (Brazilian company taxpayer ID).
     *
     * @param cnpj The CNPJ to search for (14 digits)
     * @return An Optional containing the consultant if found, or empty otherwise
     */
    Optional<Consultant> findByCnpj(String cnpj);

    /**
     * Checks if a consultant with the given CNPJ exists.
     * More efficient than findByCnpj when you only need to check existence.
     *
     * @param cnpj The CNPJ to check
     * @return true if a consultant with this CNPJ exists, false otherwise
     */
    boolean existsByCnpj(String cnpj);

    /**
     * Finds consultants by their account status.
     *
     * @param status The account status to filter by
     * @return List of consultants with the specified status
     */
    List<Consultant> findByStatus(UserStatus status);

    /**
     * Finds consultants registered after a specific date.
     *
     * @param date The reference date
     * @return List of consultants registered after the date
     */
    List<Consultant> findByRegistrationDateAfter(LocalDate date);

    /**
     * Finds consultants by company name (exact match).
     *
     * @param company The company name
     * @return List of consultants with the specified company name
     */
    List<Consultant> findByCompany(String company);

    /**
     * Finds consultants by state (UF).
     *
     * @param state The state code (e.g., "SP", "RJ")
     * @return List of consultants in the specified state
     */
    List<Consultant> findByState(String state);

    /**
     * Finds consultants by city.
     *
     * @param city The city name
     * @return List of consultants in the specified city
     */
    List<Consultant> findByCity(String city);

    /**
     * Finds consultants by ZIP code.
     *
     * @param zipCode The ZIP code
     * @return List of consultants with the specified ZIP code
     */
    List<Consultant> findByZipCode(String zipCode);

    /**
     * Counts consultants by status.
     *
     * @param status The status to count
     * @return Number of consultants with the specified status
     */
    long countByStatus(UserStatus status);

    /**
     * Counts consultants by state.
     *
     * @param state The state to count
     * @return Number of consultants in the specified state
     */
    long countByState(String state);

    /**
     * Finds consultants with company name containing the search term (case-insensitive).
     *
     * @param company The search term
     * @return List of consultants matching the search
     */
    @Query("SELECT c FROM Consultant c WHERE LOWER(c.company) LIKE LOWER(CONCAT('%', :company, '%'))")
    List<Consultant> findByCompanyContainingIgnoreCase(@Param("company") String company);

    /**
     * Finds consultants with consultant name containing the search term (case-insensitive).
     *
     * @param consultantName The search term
     * @return List of consultants matching the search
     */
    @Query("SELECT c FROM Consultant c WHERE LOWER(c.consultantName) LIKE LOWER(CONCAT('%', :consultantName, '%'))")
    List<Consultant> findByConsultantNameContainingIgnoreCase(@Param("consultantName") String consultantName);

}
