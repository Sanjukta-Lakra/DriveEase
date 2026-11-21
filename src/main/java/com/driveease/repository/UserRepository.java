package com.driveease.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.driveease.entity.User;
import com.driveease.enums.UserRole;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // Count users by role
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
	long countByRole(UserRole role);

    // Count active and inactive users
    long countByActiveTrue();

    @Query("SELECT COUNT(u) FROM User u WHERE u.active = FALSE")
	long countByActiveFalse();

    // Count customer registrations within a date range
    long countByRoleAndCreatedAtBetween(
            UserRole role,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    // Fetch customers registered within a date range
    List<User> findByRoleAndCreatedAtBetween(
            UserRole role,
            LocalDateTime startDate,
            LocalDateTime endDate
    );
    
    // Fetch users not in Trash
    List<User> findByDeletedFalse();

    // Fetch users in Trash
    List<User> findByDeletedTrue();
    
    // Check whether a customer has any bookings
    boolean existsByIdAndRoleAndDeletedFalse(Long id, UserRole role);
}
