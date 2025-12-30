package com.understand_your_electricity_bill.repository;

import com.understand_your_electricity_bill.model.BillItem;
import com.understand_your_electricity_bill.model.ElectricityBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for BillItem entity operations.
 * Provides custom queries for bill item management, filtering, and calculations.
 *
 * @see BillItem
 */
@Repository
public interface BillItemRepository extends JpaRepository<BillItem, UUID> {

    // ========== BASIC QUERIES ==========

    /**
     * Find all items by electricity bill ID
     *
     * @param billId Electricity Bill UUID
     * @return List of bill items
     */
    @Query("SELECT bi FROM BillItem bi " +
            "JOIN FETCH bi.bill b " +
            "WHERE b.id = :billId " +
            "ORDER BY bi.createdAt ASC")
    List<BillItem> findByBillId(@Param("billId") UUID billId);

    /**
     * Find all items by electricity bill entity
     *
     * @param bill Electricity Bill entity
     * @return List of bill items
     */
    List<BillItem> findByBill(ElectricityBill bill);

    /**
     * Find all items by item type
     *
     * @param itemType Item type string
     * @return List of bill items
     */
    @Query("SELECT bi FROM BillItem bi " +
            "WHERE bi.itemType = :itemType " +
            "ORDER BY bi.createdAt DESC")
    List<BillItem> findByItemType(@Param("itemType") String itemType);

    /**
     * Find items by bill ID and item type
     *
     * @param billId   Electricity Bill UUID
     * @param itemType Item type string
     * @return List of bill items
     */
    @Query("SELECT bi FROM BillItem bi " +
            "WHERE bi.bill.id = :billId " +
            "AND bi.itemType = :itemType " +
            "ORDER BY bi.createdAt ASC")
    List<BillItem> findByBillIdAndItemType(
            @Param("billId") UUID billId,
            @Param("itemType") String itemType
    );

    // ========== SEARCH QUERIES ==========

    /**
     * Find items by description containing text (case-insensitive)
     *
     * @param description Search text
     * @return List of bill items
     */
    @Query("SELECT bi FROM BillItem bi " +
            "WHERE LOWER(bi.description) LIKE LOWER(CONCAT('%', :description, '%')) " +
            "ORDER BY bi.createdAt DESC")
    List<BillItem> findByDescriptionContainingIgnoreCase(@Param("description") String description);

    /**
     * Find items with amount greater than threshold
     *
     * @param threshold Amount threshold
     * @return List of bill items
     */
    @Query("SELECT bi FROM BillItem bi " +
            "WHERE bi.amount > :threshold " +
            "ORDER BY bi.amount DESC")
    List<BillItem> findByAmountGreaterThan(@Param("threshold") BigDecimal threshold);

    /**
     * Find items with amount between range
     *
     * @param minAmount Minimum amount
     * @param maxAmount Maximum amount
     * @return List of bill items
     */
    @Query("SELECT bi FROM BillItem bi " +
            "WHERE bi.amount BETWEEN :minAmount AND :maxAmount " +
            "ORDER BY bi.amount DESC")
    List<BillItem> findByAmountBetween(
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount
    );

    // ========== STATISTICAL QUERIES ==========

    /**
     * Count items by bill ID
     *
     * @param billId Electricity Bill UUID
     * @return Number of items
     */
    @Query("SELECT COUNT(bi) FROM BillItem bi WHERE bi.bill.id = :billId")
    Long countByBillId(@Param("billId") UUID billId);

    /**
     * Count items by item type
     *
     * @param itemType Item type string
     * @return Number of items
     */
    @Query("SELECT COUNT(bi) FROM BillItem bi WHERE bi.itemType = :itemType")
    Long countByItemType(@Param("itemType") String itemType);

    /**
     * Calculate total amount for a bill
     *
     * @param billId Electricity Bill UUID
     * @return Total amount
     */
    @Query("SELECT COALESCE(SUM(bi.amount), 0) FROM BillItem bi WHERE bi.bill.id = :billId")
    BigDecimal sumAmountByBillId(@Param("billId") UUID billId);

    /**
     * Calculate total amount by item type for a bill
     *
     * @param billId   Electricity Bill UUID
     * @param itemType Item type string
     * @return Total amount
     */
    @Query("SELECT COALESCE(SUM(bi.amount), 0) FROM BillItem bi " +
            "WHERE bi.bill.id = :billId AND bi.itemType = :itemType")
    BigDecimal sumAmountByBillIdAndItemType(
            @Param("billId") UUID billId,
            @Param("itemType") String itemType
    );

    /**
     * Calculate average amount by item type
     *
     * @param itemType Item type string
     * @return Average amount
     */
    @Query("SELECT COALESCE(AVG(bi.amount), 0) FROM BillItem bi WHERE bi.itemType = :itemType")
    BigDecimal avgAmountByItemType(@Param("itemType") String itemType);

    // ========== ADVANCED QUERIES ==========

    /**
     * Find all distinct item types in the system
     *
     * @return List of distinct item types
     */
    @Query("SELECT DISTINCT bi.itemType FROM BillItem bi ORDER BY bi.itemType")
    List<String> findDistinctItemTypes();

    /**
     * Find all distinct item types for a specific bill
     *
     * @param billId Electricity Bill UUID
     * @return List of distinct item types
     */
    @Query("SELECT DISTINCT bi.itemType FROM BillItem bi WHERE bi.bill.id = :billId ORDER BY bi.itemType")
    List<String> findDistinctItemTypesByBillId(@Param("billId") UUID billId);

    /**
     * Check if bill has items of specific type
     *
     * @param billId   Electricity Bill UUID
     * @param itemType Item type string
     * @return true if exists
     */
    @Query("SELECT CASE WHEN COUNT(bi) > 0 THEN true ELSE false END FROM BillItem bi " +
            "WHERE bi.bill.id = :billId AND bi.itemType = :itemType")
    boolean existsByBillIdAndItemType(
            @Param("billId") UUID billId,
            @Param("itemType") String itemType
    );

    /**
     * Find items with null or zero quantity
     *
     * @return List of bill items
     */
    @Query("SELECT bi FROM BillItem bi " +
            "WHERE bi.quantity IS NULL OR bi.quantity = 0 " +
            "ORDER BY bi.createdAt DESC")
    List<BillItem> findItemsWithoutQuantity();

    /**
     * Find items with null or zero unit price
     *
     * @return List of bill items
     */
    @Query("SELECT bi FROM BillItem bi " +
            "WHERE bi.unitPrice IS NULL OR bi.unitPrice = 0 " +
            "ORDER BY bi.createdAt DESC")
    List<BillItem> findItemsWithoutUnitPrice();

    /**
     * Delete all items by bill ID
     *
     * @param billId Electricity Bill UUID
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM BillItem bi WHERE bi.bill.id = :billId")
    void deleteByBillId(@Param("billId") UUID billId);

    /**
     * Find most expensive item in a bill
     *
     * @param billId Electricity Bill UUID
     * @return List with the most expensive item (or empty)
     */
    @Query("SELECT bi FROM BillItem bi " +
            "WHERE bi.bill.id = :billId " +
            "ORDER BY bi.amount DESC " +
            "LIMIT 1")
    List<BillItem> findMostExpensiveItemByBillId(@Param("billId") UUID billId);

    /**
     * Find items by bill ID ordered by amount descending
     *
     * @param billId Electricity Bill UUID
     * @return List of bill items ordered by amount
     */
    @Query("SELECT bi FROM BillItem bi " +
            "WHERE bi.bill.id = :billId " +
            "ORDER BY bi.amount DESC")
    List<BillItem> findByBillIdOrderByAmountDesc(@Param("billId") UUID billId);
}

