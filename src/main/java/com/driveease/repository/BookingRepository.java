package com.driveease.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.driveease.entity.Booking;
import com.driveease.enums.BookingStatus;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Normal bookings (not in Trash)
    List<Booking> findByDeletedFalse();

    List<Booking> findByUserIdAndDeletedFalse(Long userId);

    List<Booking> findByVehicleIdAndDeletedFalse(Long vehicleId);

    List<Booking> findByStatusAndDeletedFalse(BookingStatus status);

    List<Booking> findByUserIdAndDeletedFalseOrderByCreatedAtDesc(Long userId);

    // Trashed bookings
    List<Booking> findByDeletedTrue();

    // Existing methods retained for compatibility
    List<Booking> findByUserId(Long userId);

    List<Booking> findByVehicleId(Long vehicleId);

    List<Booking> findByStatus(BookingStatus status);

    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);

    boolean existsByVehicleId(Long vehicleId);

    // Counts excluding Trash
    long countByStatusAndDeletedFalse(BookingStatus status);

    long countByCreatedAtBetweenAndDeletedFalse(
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    // Existing count methods retained for compatibility
    long countByStatus(BookingStatus status);
    
    boolean existsByUserId(Long userId);

    long countByCreatedAtBetween(
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    // Fetch bookings excluding Trash
    List<Booking> findByCreatedAtBetweenAndDeletedFalse(
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    // Existing method retained for compatibility
    List<Booking> findByCreatedAtBetween(
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    // Total booking amount excluding Trash
    @Query("""
            SELECT COALESCE(SUM(b.totalAmount), 0)
            FROM Booking b
            WHERE b.deleted = false
            """)
    BigDecimal getTotalBookingAmount();

    // Most-rented vehicles excluding Trash
    @Query("""
            SELECT b.vehicle.id, b.vehicle.brand, b.vehicle.model,
                   COUNT(b.id)
            FROM Booking b
            WHERE b.deleted = false
              AND b.status IN :statuses
            GROUP BY b.vehicle.id, b.vehicle.brand, b.vehicle.model
            ORDER BY COUNT(b.id) DESC
            """)
    List<Object[]> findMostRentedVehicles(
            @Param("statuses") List<BookingStatus> statuses
    );
}
