package com.understand_your_electricity_bill.repository;

import com.understand_your_electricity_bill.model.ElectricityBill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for ElectricityBill entity operations.
 * Provides custom queries for bill management, filtering, and analysis.
 *
 * @see ElectricityBill
 */
@Repository
public interface ElectricityBillRepository extends JpaRepository<ElectricityBill, UUID> {

    // ========== BASIC QUERIES ==========

    /**
     * Find all bills by client ID
     *
     * @param clientId Client UUID
     * @return List of electricity bills
     */
    @Query("SELECT eb FROM ElectricityBill eb " +
            "JOIN FETCH eb.client c " +
            "LEFT JOIN FETCH eb.consultant " +
            "LEFT JOIN FETCH eb.tariff " +
            "WHERE c.id = :clientId " +
            "ORDER BY eb.referenceMonth DESC")
    List<ElectricityBill> findByClientId(@Param("clientId") UUID clientId);

    /**
     * Find all bills by client ID with pagination
     *
     * @param clientId Client UUID
     * @param pageable Pagination parameters
     * @return Page of electricity bills
     */
    @Query("SELECT eb FROM ElectricityBill eb " +
            "WHERE eb.client.id = :clientId " +
            "ORDER BY eb.referenceMonth DESC")
    Page<ElectricityBill> findByClientId(@Param("clientId") UUID clientId, Pageable pageable);

    /**
     * Find all bills by consultant ID
     *
     * @param consultantId Consultant UUID
     * @return List of electricity bills
     */
    @Query("SELECT eb FROM ElectricityBill eb " +
            "JOIN FETCH eb.consultant cons " +
            "JOIN FETCH eb.client " +
            "LEFT JOIN FETCH eb.tariff " +
            "WHERE cons.id = :consultantId " +
            "ORDER BY eb.referenceMonth DESC")
    List<ElectricityBill> findByConsultantId(@Param("consultantId") UUID consultantId);

    /**
     * Find all bills by consultant ID with pagination
     *
     * @param consultantId Consultant UUID
     * @param pageable     Pagination parameters
     * @return Page of electricity bills
     */
    @Query("SELECT eb FROM ElectricityBill eb " +
            "WHERE eb.consultant.id = :consultantId " +
            "ORDER BY eb.referenceMonth DESC")
    Page<ElectricityBill> findByConsultantId(@Param("consultantId") UUID consultantId, Pageable pageable);

    /**
     * Find all bills by reference month
     *
     * @param referenceMonth Reference month
     * @return List of electricity bills
     */
    @Query("SELECT eb FROM ElectricityBill eb " +
            "JOIN FETCH eb.client c " +
            "WHERE eb.referenceMonth = :referenceMonth " +
            "ORDER BY c.name ASC")
    List<ElectricityBill> findByReferenceMonth(@Param("referenceMonth") LocalDate referenceMonth);

    /**
     * Find bill by client ID and reference month
     *
     * @param clientId       Client UUID
     * @param referenceMonth Reference month
     * @return Optional ElectricityBill
     */
    @Query("SELECT eb FROM ElectricityBill eb " +
            "JOIN FETCH eb.client c " +
            "LEFT JOIN FETCH eb.consultant " +
            "LEFT JOIN FETCH eb.tariff " +
            "WHERE c.id = :clientId " +
            "AND eb.referenceMonth = :referenceMonth")
    Optional<ElectricityBill> findByClientIdAndReferenceMonth(
            @Param("clientId") UUID clientId,
            @Param("referenceMonth") LocalDate referenceMonth
    );

    // ========== RANGE QUERIES ==========

    /**
     * Find bills by client ID within date range
     *
     * @param clientId  Client UUID
     * @param startDate Start date (inclusive)
     * @param endDate   End date (inclusive)
     * @return List of electricity bills
     */
    @Query("SELECT eb FROM ElectricityBill eb " +
            "WHERE eb.client.id = :clientId " +
            "AND eb.referenceMonth BETWEEN :startDate AND :endDate " +
            "ORDER BY eb.referenceMonth DESC")
    List<ElectricityBill> findByClientIdAndReferenceMonthBetween(
            @Param("clientId") UUID clientId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Find bills by consultant ID within date range
     *
     * @param consultantId Consultant UUID
     * @param startDate    Start date (inclusive)
     * @param endDate      End date (inclusive)
     * @return List of electricity bills
     */
    @Query("SELECT eb FROM ElectricityBill eb " +
            "WHERE eb.consultant.id = :consultantId " +
            "AND eb.referenceMonth BETWEEN :startDate AND :endDate " +
            "ORDER BY eb.referenceMonth DESC")
    List<ElectricityBill> findByConsultantIdAndReferenceMonthBetween(
            @Param("consultantId") UUID consultantId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // ========== STATISTICAL QUERIES ==========

    /**
     * Count bills by client ID
     *
     * @param clientId Client UUID
     * @return Number of bills
     */
    @Query("SELECT COUNT(eb) FROM ElectricityBill eb WHERE eb.client.id = :clientId")
    Long countByClientId(@Param("clientId") UUID clientId);

    /**
     * Count bills by consultant ID
     *
     * @param consultantId Consultant UUID
     * @return Number of bills
     */
    @Query("SELECT COUNT(eb) FROM ElectricityBill eb WHERE eb.consultant.id = :consultantId")
    Long countByConsultantId(@Param("consultantId") UUID consultantId);

    /**
     * Calculate total consumption by client ID
     *
     * @param clientId Client UUID
     * @return Total consumption in kWh
     */
    @Query("SELECT COALESCE(SUM(eb.consumptionKwh), 0) FROM ElectricityBill eb WHERE eb.client.id = :clientId")
    BigDecimal sumConsumptionByClientId(@Param("clientId") UUID clientId);

    /**
     * Calculate average consumption by client ID
     *
     * @param clientId Client UUID
     * @return Average consumption in kWh
     */
    @Query("SELECT COALESCE(AVG(eb.consumptionKwh), 0) FROM ElectricityBill eb WHERE eb.client.id = :clientId")
    BigDecimal avgConsumptionByClientId(@Param("clientId") UUID clientId);

    /**
     * Calculate total amount by client ID
     *
     * @param clientId Client UUID
     * @return Total amount spent
     */
    @Query("SELECT COALESCE(SUM(eb.totalAmount), 0) FROM ElectricityBill eb WHERE eb.client.id = :clientId")
    BigDecimal sumTotalAmountByClientId(@Param("clientId") UUID clientId);

    // ========== SEARCH QUERIES ==========

    /**
     * Find bills by installation number
     *
     * @param installationNumber Installation number
     * @return List of electricity bills
     */
    @Query("SELECT eb FROM ElectricityBill eb " +
            "WHERE eb.installationNumber = :installationNumber " +
            "ORDER BY eb.referenceMonth DESC")
    List<ElectricityBill> findByInstallationNumber(@Param("installationNumber") String installationNumber);

    /**
     * Find bill by invoice number
     *
     * @param invoiceNumber Invoice number
     * @return Optional ElectricityBill
     */
    @Query("SELECT eb FROM ElectricityBill eb " +
            "WHERE eb.invoiceNumber = :invoiceNumber")
    Optional<ElectricityBill> findByInvoiceNumber(@Param("invoiceNumber") String invoiceNumber);

    /**
     * Find bills with consumption above threshold
     *
     * @param threshold Consumption threshold in kWh
     * @return List of electricity bills
     */
    @Query("SELECT eb FROM ElectricityBill eb " +
            "WHERE eb.consumptionKwh > :threshold " +
            "ORDER BY eb.consumptionKwh DESC")
    List<ElectricityBill> findByConsumptionKwhGreaterThan(@Param("threshold") BigDecimal threshold);

    /**
     * Find bills without consultant assigned
     *
     * @return List of electricity bills
     */
    @Query("SELECT eb FROM ElectricityBill eb " +
            "WHERE eb.consultant IS NULL " +
            "ORDER BY eb.referenceMonth DESC")
    List<ElectricityBill> findBillsWithoutConsultant();

    // ========== ADVANCED QUERIES ==========

    /**
     * Find most recent bill for a client
     *
     * @param clientId Client UUID
     * @return Optional ElectricityBill
     */
    @Query("SELECT eb FROM ElectricityBill eb " +
            "WHERE eb.client.id = :clientId " +
            "ORDER BY eb.referenceMonth DESC " +
            "LIMIT 1")
    Optional<ElectricityBill> findMostRecentByClientId(@Param("clientId") UUID clientId);

    /**
     * Find bills with overdue payment
     *
     * @param currentDate Current date for comparison
     * @return List of electricity bills
     */
    @Query("SELECT eb FROM ElectricityBill eb " +
            "WHERE eb.dueDate < :currentDate " +
            "ORDER BY eb.dueDate ASC")
    List<ElectricityBill> findOverdueBills(@Param("currentDate") LocalDate currentDate);

    /**
     * Find bills by tariff ID
     *
     * @param tariffId Tariff UUID
     * @return List of electricity bills
     */
    @Query("SELECT eb FROM ElectricityBill eb " +
            "WHERE eb.tariff.id = :tariffId " +
            "ORDER BY eb.referenceMonth DESC")
    List<ElectricityBill> findByTariffId(@Param("tariffId") UUID tariffId);

    /**
     * Check if bill exists for client and reference month
     *
     * @param clientId       Client UUID
     * @param referenceMonth Reference month
     * @return true if exists
     */
    @Query("SELECT CASE WHEN COUNT(eb) > 0 THEN true ELSE false END " +
            "FROM ElectricityBill eb " +
            "WHERE eb.client.id = :clientId " +
            "AND eb.referenceMonth = :referenceMonth")
    boolean existsByClientIdAndReferenceMonth(
            @Param("clientId") UUID clientId,
            @Param("referenceMonth") LocalDate referenceMonth
    );
}

