package com.understand_your_electricity_bill.repository;

import com.understand_your_electricity_bill.model.User;
import com.understand_your_electricity_bill.model.enums.UserStatus;
import com.understand_your_electricity_bill.model.enums.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for User entity.
 * Provides database access operations for the base User table.
 *
 * <p>All CRUD operations are inherited from JpaRepository:</p>
 * <ul>
 *   <li>save(User) - Create or update</li>
 *   <li>findById(UUID) - Find by ID</li>
 *   <li>findAll() - List all users</li>
 *   <li>deleteById(UUID) - Delete by ID</li>
 *   <li>count() - Count all users</li>
 * </ul>
 *
 * @see User
 * @see JpaRepository
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Finds a user by their email address.
     * Email comparison is case-sensitive as per PostgreSQL default.
     *
     * @param email The email address to search for
     * @return An Optional containing the user if found, or empty otherwise
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user with the given email exists.
     * More efficient than findByEmail when you only need to check existence.
     *
     * @param email The email address to check
     * @return true if a user with this email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Finds users by their type (CLIENT, CONSULTANT, ADMIN).
     *
     * @param userType The type of user to search for
     * @return List of users with the specified type
     */
    List<User> findByUserType(UserType userType);

    /**
     * Finds users by their account status.
     *
     * @param status The account status to filter by
     * @return List of users with the specified status
     */
    List<User> findByStatus(UserStatus status);

    /**
     * Finds users created after a specific date.
     *
     * @param date The reference date
     * @return List of users created after the date
     */
    List<User> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Counts users by type.
     *
     * @param userType The type to count
     * @return Number of users of the specified type
     */
    long countByUserType(UserType userType);

    /**
     * Counts users by status.
     *
     * @param status The status to count
     * @return Number of users with the specified status
     */
    long countByStatus(UserStatus status);
}

