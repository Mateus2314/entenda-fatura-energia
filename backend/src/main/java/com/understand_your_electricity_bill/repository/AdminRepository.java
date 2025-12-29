package com.understand_your_electricity_bill.repository;

import com.understand_your_electricity_bill.model.Admin;
import com.understand_your_electricity_bill.model.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Admin entity.
 * Provides database access operations for the Admin table.
 *
 * <p>All CRUD operations are inherited from JpaRepository:</p>
 * <ul>
 *   <li>save(Admin) - Create or update</li>
 *   <li>findById(UUID) - Find by ID</li>
 *   <li>findAll() - List all admins</li>
 *   <li>deleteById(UUID) - Delete by ID</li>
 *   <li>count() - Count all admins</li>
 * </ul>
 *
 * @see Admin
 * @see JpaRepository
 */
@Repository
public interface AdminRepository extends JpaRepository<Admin, UUID> {

    /**
     * Finds admins by their role.
     *
     * @param role The admin role (e.g., "SUPER_ADMIN", "SUPPORT_ADMIN", "FINANCE_ADMIN")
     * @return List of admins with the specified role
     */
    List<Admin> findByRole(String role);

    /**
     * Finds an admin by their role (expecting a single result).
     * Useful for roles that should have only one admin (e.g., SUPER_ADMIN).
     *
     * @param role The admin role
     * @return Optional containing the admin if found, or empty otherwise
     */
    Optional<Admin> findFirstByRole(String role);

    /**
     * Checks if an admin with the given role exists.
     *
     * @param role The role to check
     * @return true if an admin with this role exists, false otherwise
     */
    boolean existsByRole(String role);

    /**
     * Finds admins by their account status.
     *
     * @param status The account status to filter by
     * @return List of admins with the specified status
     */
    List<Admin> findByStatus(UserStatus status);

    /**
     * Finds active admins (status = ACTIVE).
     *
     * @return List of active admins
     */
    @Query("SELECT a FROM Admin a WHERE a.status = 'ACTIVE'")
    List<Admin> findActiveAdmins();

    /**
     * Finds admins with role containing the search term (case-insensitive).
     * Useful for searching roles like "ADMIN", "SUPPORT", etc.
     *
     * @param role The search term
     * @return List of admins matching the search
     */
    @Query("SELECT a FROM Admin a WHERE LOWER(a.role) LIKE LOWER(CONCAT('%', :role, '%'))")
    List<Admin> findByRoleContainingIgnoreCase(@Param("role") String role);

    /**
     * Finds admins with name containing the search term (case-insensitive).
     * Inherited from User entity.
     *
     * @param name The search term
     * @return List termof admins matching the search
     */
    @Query("SELECT a FROM Admin a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Admin> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Counts admins by role.
     *
     * @param role The role to count
     * @return Number of admins with the specified role
     */
    long countByRole(String role);

    /**
     * Counts admins by status.
     *
     * @param status The status to count
     * @return Number of admins with the specified status
     */
    long countByStatus(UserStatus status);

    /**
     * Counts active admins.
     *
     * @return Number of active admins
     */
    @Query("SELECT COUNT(a) FROM Admin a WHERE a.status = 'ACTIVE'")
    long countActiveAdmins();

    /**
     * Finds admins with specific permissions (JSONB contains query).
     * Note: This uses PostgreSQL JSONB operators.
     * Uses jsonb_exists function instead of ? operator to avoid parameter conflicts.
     *
     * @param permissionKey The permission key to search for
     * @return List of admins with the specified permission
     */
    @Query(value = "SELECT * FROM admins WHERE jsonb_exists(permissions::jsonb, :permissionKey)", nativeQuery = true)
    List<Admin> findByPermissionKey(@Param("permissionKey") String permissionKey);

    /**
     * Finds admins by email (inherited from User).
     * Useful for login and authentication.
     *
     * @param email The email address
     * @return Optional containing the admin if found
     */
    @Query("SELECT a FROM Admin a WHERE a.email = :email")
    Optional<Admin> findByEmail(@Param("email") String email);

    /**
     * Checks if an admin with the given email exists.
     *
     * @param email The email to check
     * @return true if an admin with this email exists, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Admin a WHERE a.email = :email")
    boolean existsByEmail(@Param("email") String email);
}

