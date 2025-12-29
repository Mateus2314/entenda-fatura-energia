package com.understand_your_electricity_bill.repository;

import com.understand_your_electricity_bill.model.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Tariff entity.
 * Provides database access operations for the Tariff table.
 *
 * <p>All CRUD operations are inherited from JpaRepository:</p>
 * <ul>
 *   <li>save(Tariff) - Create or update</li>
 *   <li>findById(UUID) - Find by ID</li>
 *   <li>findAll() - List all tariffs</li>
 *   <li>deleteById(UUID) - Delete by ID</li>
 *   <li>count() - Count all tariffs</li>
 * </ul>
 *
 * @see Tariff
 * @see JpaRepository
 */
@Repository
public interface TariffRepository extends JpaRepository<Tariff, UUID> {

    /**
     * Finds tariffs by distributor name (exact match).
     *
     * @param distributor The distributor name (e.g., "CPFL PAULISTA", "LIGHT")
     * @return List of tariffs from the specified distributor
     */
    List<Tariff> findByDistributor(String distributor);

    /**
     * Finds tariffs by distributor CNPJ.
     *
     * @param cnpjDistributor The CNPJ of the distributor (14 digits)
     * @return List of tariffs from the distributor with the specified CNPJ
     */
    List<Tariff> findByCnpjDistributor(String cnpjDistributor);

    /**
     * Finds tariffs by subgroup (e.g., "B1", "B3", "A4").
     *
     * @param subgroup The tariff subgroup
     * @return List of tariffs in the specified subgroup
     */
    List<Tariff> findBySubgroup(String subgroup);

    /**
     * Finds tariffs by modality (e.g., "Convencional", "Azul", "Verde").
     *
     * @param tariffModality The tariff modality
     * @return List of tariffs with the specified modality
     */
    List<Tariff> findByTariffModality(String tariffModality);

    /**
     * Finds tariffs by consumer class (e.g., "Residencial", "Comercial", "Industrial").
     *
     * @param consumerClass The consumer class
     * @return List of tariffs for the specified consumer class
     */
    List<Tariff> findByConsumerClass(String consumerClass);

    /**
     * Finds tariffs by consumer subclass (e.g., "Residencial Baixa Renda").
     *
     * @param consumerSubclass The consumer subclass
     * @return List of tariffs for the specified consumer subclass
     */
    List<Tariff> findByConsumerSubclass(String consumerSubclass);

    /**
     * Finds tariffs valid at a specific date.
     * A tariff is valid if the date is between valid_from and valid_until (or valid_until is null).
     *
     * @param date The reference date
     * @return List of tariffs valid at the specified date
     */
    @Query("SELECT t FROM Tariff t WHERE t.validFrom <= :date AND (t.validUntil IS NULL OR t.validUntil >= :date)")
    List<Tariff> findValidAt(@Param("date") LocalDate date);

    /**
     * Finds current valid tariffs (valid today).
     *
     * @return List of currently valid tariffs
     */
    @Query("SELECT t FROM Tariff t WHERE t.validFrom <= CURRENT_DATE AND (t.validUntil IS NULL OR t.validUntil >= CURRENT_DATE)")
    List<Tariff> findCurrentlyValid();

    /**
     * Finds tariffs by distributor and subgroup, valid at a specific date.
     * Most common query for bill processing.
     *
     * @param distributor The distributor name
     * @param subgroup The tariff subgroup
     * @param date The reference date
     * @return List of matching tariffs
     */
    @Query("SELECT t FROM Tariff t WHERE t.distributor = :distributor AND t.subgroup = :subgroup " +
           "AND t.validFrom <= :date AND (t.validUntil IS NULL OR t.validUntil >= :date)")
    List<Tariff> findByDistributorAndSubgroupValidAt(
            @Param("distributor") String distributor,
            @Param("subgroup") String subgroup,
            @Param("date") LocalDate date
    );

    /**
     * Finds tariffs by distributor, subgroup, and modality, valid at a specific date.
     * More specific query for exact tariff matching.
     *
     * @param distributor The distributor name
     * @param subgroup The tariff subgroup
     * @param tariffModality The tariff modality
     * @param date The reference date
     * @return Optional containing the tariff if found, or empty otherwise
     */
    @Query("SELECT t FROM Tariff t WHERE t.distributor = :distributor AND t.subgroup = :subgroup " +
           "AND t.tariffModality = :tariffModality " +
           "AND t.validFrom <= :date AND (t.validUntil IS NULL OR t.validUntil >= :date)")
    Optional<Tariff> findByDistributorAndSubgroupAndModalityValidAt(
            @Param("distributor") String distributor,
            @Param("subgroup") String subgroup,
            @Param("tariffModality") String tariffModality,
            @Param("date") LocalDate date
    );

    /**
     * Finds expired tariffs (valid_until is in the past).
     *
     * @param date The reference date (typically today)
     * @return List of expired tariffs
     */
    @Query("SELECT t FROM Tariff t WHERE t.validUntil IS NOT NULL AND t.validUntil < :date")
    List<Tariff> findExpiredBefore(@Param("date") LocalDate date);

    /**
     * Finds tariffs that will become valid after a specific date.
     *
     * @param date The reference date
     * @return List of future tariffs
     */
    List<Tariff> findByValidFromAfter(LocalDate date);

    /**
     * Finds tariffs with a specific activated flag (bandeira tarifária).
     *
     * @param flagName The flag name (e.g., "Verde", "Amarela", "Vermelha P1")
     * @return List of tariffs with the specified flag
     */
    List<Tariff> findByActivatedFlagName(String flagName);

    /**
     * Counts tariffs by distributor.
     *
     * @param distributor The distributor name
     * @return Number of tariffs from the distributor
     */
    long countByDistributor(String distributor);

    /**
     * Counts currently valid tariffs.
     *
     * @return Number of currently valid tariffs
     */
    @Query("SELECT COUNT(t) FROM Tariff t WHERE t.validFrom <= CURRENT_DATE AND (t.validUntil IS NULL OR t.validUntil >= CURRENT_DATE)")
    long countCurrentlyValid();

    /**
     * Finds tariffs with distributor name containing the search term (case-insensitive).
     *
     * @param distributor The search term
     * @return List of tariffs matching the search
     */
    @Query("SELECT t FROM Tariff t WHERE LOWER(t.distributor) LIKE LOWER(CONCAT('%', :distributor, '%'))")
    List<Tariff> findByDistributorContainingIgnoreCase(@Param("distributor") String distributor);

    /**
     * Finds the most recent tariff for a distributor and subgroup.
     * Useful for getting the latest applicable tariff.
     *
     * @param distributor The distributor name
     * @param subgroup The tariff subgroup
     * @return Optional containing the most recent tariff if found
     */
    @Query("SELECT t FROM Tariff t WHERE t.distributor = :distributor AND t.subgroup = :subgroup " +
           "ORDER BY t.validFrom DESC LIMIT 1")
    Optional<Tariff> findMostRecentByDistributorAndSubgroup(
            @Param("distributor") String distributor,
            @Param("subgroup") String subgroup
    );

    /**
     * Checks if a tariff exists for the given parameters and date.
     * Useful for validation before creating bills.
     *
     * @param distributor The distributor name
     * @param subgroup The tariff subgroup
     * @param date The reference date
     * @return true if a valid tariff exists, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Tariff t " +
           "WHERE t.distributor = :distributor AND t.subgroup = :subgroup " +
           "AND t.validFrom <= :date AND (t.validUntil IS NULL OR t.validUntil >= :date)")
    boolean existsByDistributorAndSubgroupValidAt(
            @Param("distributor") String distributor,
            @Param("subgroup") String subgroup,
            @Param("date") LocalDate date
    );
}

