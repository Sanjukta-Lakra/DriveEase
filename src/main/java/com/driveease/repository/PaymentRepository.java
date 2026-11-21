package com.driveease.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.driveease.entity.Payment;
import com.driveease.enums.PaymentStatus;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Normal payments (not in Trash)
    List<Payment> findByDeletedFalse();

    Optional<Payment> findByBookingIdAndDeletedFalse(Long bookingId);

    // Trashed payments
    List<Payment> findByDeletedTrue();

    // Existing methods - retained for compatibility
    Optional<Payment> findByBookingId(Long bookingId);

    Optional<Payment> findByTransactionId(String transactionId);

    boolean existsByBookingId(Long bookingId);

    boolean existsByTransactionId(String transactionId);

    // Total successful payment revenue (excluding Trash)
    @Query("""
            SELECT COALESCE(SUM(p.amount), 0)
            FROM Payment p
            WHERE p.paymentStatus = :status
              AND p.deleted = false
            """)
    BigDecimal getTotalRevenueByStatus(
            @Param("status") PaymentStatus status
    );

    // Count payments by status (excluding Trash)
    long countByPaymentStatusAndDeletedFalse(PaymentStatus status);

    // Existing count method - retained for compatibility
    long countByPaymentStatus(PaymentStatus status);

    // Successful revenue within a date range (excluding Trash)
    @Query("""
            SELECT COALESCE(SUM(p.amount), 0)
            FROM Payment p
            WHERE p.paymentStatus = :status
              AND p.paymentDate BETWEEN :startDate AND :endDate
              AND p.deleted = false
            """)
    BigDecimal getRevenueBetweenDates(
            @Param("status") PaymentStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Count payments within a date range (excluding Trash)
    long countByPaymentDateBetweenAndDeletedFalse(
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    // Fetch payments within a date range (excluding Trash)
    List<Payment> findByPaymentDateBetweenAndDeletedFalse(
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    // Existing methods - retained for compatibility
    long countByPaymentDateBetween(
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    List<Payment> findByPaymentDateBetween(
            LocalDateTime startDate,
            LocalDateTime endDate
    );
}
