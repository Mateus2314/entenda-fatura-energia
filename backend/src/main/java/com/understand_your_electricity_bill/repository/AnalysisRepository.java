package com.understand_your_electricity_bill.repository;

import com.understand_your_electricity_bill.model.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Analysis entity operations.
 * Provides custom queries for analysis management, filtering, and reporting.
 *
 * @see Analysis
 */
@Repository
public interface AnalysisRepository extends JpaRepository<Analysis, UUID> {

    // ========== BASIC QUERIES ==========

    /**
     * Find analysis by electricity bill ID
     *
     * @param billId Electricity Bill UUID
     * @return Optional Analysis
     */
    @Query("SELECT a FROM Analysis a " +
            "JOIN FETCH a.bill b " +
            "WHERE b.id = :billId")
    Optional<Analysis> findByBillId(@Param("billId") UUID billId);

    /**
     * Find all analyses by client ID
     *
     * @param clientId Client UUID
     * @return List of analyses
     */
    @Query("SELECT a FROM Analysis a " +
            "JOIN FETCH a.bill b " +
            "JOIN FETCH b.client c " +
            "WHERE c.id = :clientId " +
            "ORDER BY a.createdAt DESC")
    List<Analysis> findByClientId(@Param("clientId") UUID clientId);

    /**
     * Find all analyses by consultant ID
     *
     * @param consultantId Consultant UUID
     * @return List of analyses
     */
    @Query("SELECT a FROM Analysis a " +
            "JOIN FETCH a.bill b " +
            "LEFT JOIN FETCH b.consultant cons " +
            "WHERE cons.id = :consultantId " +
            "ORDER BY a.createdAt DESC")
    List<Analysis> findByConsultantId(@Param("consultantId") UUID consultantId);

    // ========== SEARCH QUERIES ==========

    /**
     * Find analyses with cost per kWh greater than threshold
     *
     * @param threshold Cost threshold
     * @return List of analyses
     */
    @Query("SELECT a FROM Analysis a " +
            "WHERE a.costPerKwh > :threshold " +
            "ORDER BY a.costPerKwh DESC")
    List<Analysis> findByCostPerKwhGreaterThan(@Param("threshold") BigDecimal threshold);

    /**
     * Find analyses with cost per kWh between range
     *
     * @param minCost Minimum cost
     * @param maxCost Maximum cost
     * @return List of analyses
     */
    @Query("SELECT a FROM Analysis a " +
            "WHERE a.costPerKwh BETWEEN :minCost AND :maxCost " +
            "ORDER BY a.costPerKwh DESC")
    List<Analysis> findByCostPerKwhBetween(
            @Param("minCost") BigDecimal minCost,
            @Param("maxCost") BigDecimal maxCost
    );

    /**
     * Find analyses with average consumption greater than threshold
     *
     * @param threshold Consumption threshold in kWh
     * @return List of analyses
     */
    @Query("SELECT a FROM Analysis a " +
            "WHERE a.averageConsumption > :threshold " +
            "ORDER BY a.averageConsumption DESC")
    List<Analysis> findByAverageConsumptionGreaterThan(@Param("threshold") BigDecimal threshold);

    /**
     * Find analyses with positive comparison (consumption increased)
     *
     * @return List of analyses with consumption increase
     */
    @Query("SELECT a FROM Analysis a " +
            "WHERE a.comparisonPrevMonth > 0 " +
            "ORDER BY a.comparisonPrevMonth DESC")
    List<Analysis> findWithConsumptionIncrease();

    /**
     * Find analyses with negative comparison (consumption decreased)
     *
     * @return List of analyses with consumption decrease
     */
    @Query("SELECT a FROM Analysis a " +
            "WHERE a.comparisonPrevMonth < 0 " +
            "ORDER BY a.comparisonPrevMonth ASC")
    List<Analysis> findWithConsumptionDecrease();

    /**
     * Find analyses created within date range
     *
     * @param startDate Start date
     * @param endDate   End date
     * @return List of analyses
     */
    @Query("SELECT a FROM Analysis a " +
            "WHERE a.createdAt BETWEEN :startDate AND :endDate " +
            "ORDER BY a.createdAt DESC")
    List<Analysis> findByCreatedAtBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // ========== STATISTICAL QUERIES ==========

    /**
     * Count analyses by client ID
     *
     * @param clientId Client UUID
     * @return Number of analyses
     */
    @Query("SELECT COUNT(a) FROM Analysis a WHERE a.bill.client.id = :clientId")
    Long countByClientId(@Param("clientId") UUID clientId);

    /**
     * Count analyses by consultant ID
     *
     * @param consultantId Consultant UUID
     * @return Number of analyses
     */
    @Query("SELECT COUNT(a) FROM Analysis a WHERE a.bill.consultant.id = :consultantId")
    Long countByConsultantId(@Param("consultantId") UUID consultantId);

    /**
     * Calculate average cost per kWh across all analyses
     *
     * @return Average cost per kWh
     */
    @Query("SELECT COALESCE(AVG(a.costPerKwh), 0) FROM Analysis a WHERE a.costPerKwh IS NOT NULL")
    BigDecimal calculateAverageCostPerKwh();

    /**
     * Calculate average consumption across all analyses
     *
     * @return Average consumption
     */
    @Query("SELECT COALESCE(AVG(a.averageConsumption), 0) FROM Analysis a WHERE a.averageConsumption IS NOT NULL")
    BigDecimal calculateAverageConsumption();

    /**
     * Calculate average cost per kWh by client
     *
     * @param clientId Client UUID
     * @return Average cost per kWh for client
     */
    @Query("SELECT COALESCE(AVG(a.costPerKwh), 0) FROM Analysis a " +
            "WHERE a.bill.client.id = :clientId AND a.costPerKwh IS NOT NULL")
    BigDecimal calculateAverageCostPerKwhByClientId(@Param("clientId") UUID clientId);

    /**
     * Calculate average consumption by client
     *
     * @param clientId Client UUID
     * @return Average consumption for client
     */
    @Query("SELECT COALESCE(AVG(a.averageConsumption), 0) FROM Analysis a " +
            "WHERE a.bill.client.id = :clientId AND a.averageConsumption IS NOT NULL")
    BigDecimal calculateAverageConsumptionByClientId(@Param("clientId") UUID clientId);

    // ========== ADVANCED QUERIES ==========

    /**
     * Check if analysis exists for bill
     *
     * @param billId Electricity Bill UUID
     * @return true if exists
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Analysis a WHERE a.bill.id = :billId")
    boolean existsByBillId(@Param("billId") UUID billId);

    /**
     * Find analyses without PDF report
     *
     * @return List of analyses without report
     */
    @Query("SELECT a FROM Analysis a " +
            "WHERE a.reportPdfUrl IS NULL OR a.reportPdfUrl = '' " +
            "ORDER BY a.createdAt DESC")
    List<Analysis> findWithoutReport();

    /**
     * Find analyses with PDF report
     *
     * @return List of analyses with report
     */
    @Query("SELECT a FROM Analysis a " +
            "WHERE a.reportPdfUrl IS NOT NULL AND a.reportPdfUrl <> '' " +
            "ORDER BY a.createdAt DESC")
    List<Analysis> findWithReport();

    /**
     * Find most recent analysis for client
     *
     * @param clientId Client UUID
     * @return Optional Analysis
     */
    @Query("SELECT a FROM Analysis a " +
            "WHERE a.bill.client.id = :clientId " +
            "ORDER BY a.createdAt DESC " +
            "LIMIT 1")
    Optional<Analysis> findMostRecentByClientId(@Param("clientId") UUID clientId);

    /**
     * Find analyses with savings tips containing text
     *
     * @param keyword Search keyword
     * @return List of analyses
     */
    @Query("SELECT a FROM Analysis a " +
            "WHERE LOWER(a.savingsTips) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "ORDER BY a.createdAt DESC")
    List<Analysis> findBySavingsTipsContaining(@Param("keyword") String keyword);

    /**
     * Find analyses created after date
     *
     * @param date Reference date
     * @return List of analyses
     */
    @Query("SELECT a FROM Analysis a " +
            "WHERE a.createdAt > :date " +
            "ORDER BY a.createdAt DESC")
    List<Analysis> findByCreatedAtAfter(@Param("date") LocalDateTime date);

    /**
     * Find analyses ordered by cost per kWh descending (most expensive first)
     *
     * @return List of analyses
     */
    @Query("SELECT a FROM Analysis a " +
            "WHERE a.costPerKwh IS NOT NULL " +
            "ORDER BY a.costPerKwh DESC")
    List<Analysis> findAllOrderByCostPerKwhDesc();

    /**
     * Find analyses with highest consumption increase
     *
     * @param limit Maximum results
     * @return List of analyses with highest increase
     */
    @Query(value = "SELECT a FROM Analysis a " +
            "WHERE a.comparisonPrevMonth IS NOT NULL " +
            "ORDER BY a.comparisonPrevMonth DESC " +
            "LIMIT :limit")
    List<Analysis> findTopConsumptionIncreases(@Param("limit") int limit);

    /**
     * Find analyses with highest consumption decrease (savings)
     *
     * @param limit Maximum results
     * @return List of analyses with highest decrease
     */
    @Query(value = "SELECT a FROM Analysis a " +
            "WHERE a.comparisonPrevMonth IS NOT NULL " +
            "ORDER BY a.comparisonPrevMonth ASC " +
            "LIMIT :limit")
    List<Analysis> findTopConsumptionDecreases(@Param("limit") int limit);
}

