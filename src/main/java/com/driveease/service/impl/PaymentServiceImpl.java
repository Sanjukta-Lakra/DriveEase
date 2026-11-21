package com.driveease.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.driveease.entity.Booking;
import com.driveease.entity.Payment;
import com.driveease.enums.BookingStatus;
import com.driveease.enums.PaymentStatus;
import com.driveease.exception.ResourceNotFoundException;
import com.driveease.repository.PaymentRepository;
import com.driveease.service.BookingService;
import com.driveease.service.PaymentService;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingService bookingService;

    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            BookingService bookingService) {

        this.paymentRepository = paymentRepository;
        this.bookingService = bookingService;
    }

    // Create payment
    @Override
    @Transactional
    public Payment makePayment(Long bookingId, Payment payment) {

        if (payment == null) {
            throw new IllegalArgumentException(
                    "Payment details are required");
        }

        Booking booking = bookingService.getBookingById(bookingId);

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalArgumentException(
                    "Payment is allowed only for confirmed bookings");
        }

        // Includes payments in Trash to prevent duplicate payments
        if (paymentRepository.existsByBookingId(bookingId)) {
            throw new IllegalArgumentException(
                    "Payment already exists for this booking");
        }

        if (payment.getPaymentMethod() == null
                || payment.getPaymentMethod().isBlank()) {
            throw new IllegalArgumentException(
                    "Payment method is required");
        }

        payment.setBooking(booking);
        payment.setAmount(booking.getTotalAmount());
        payment.setTransactionId(UUID.randomUUID().toString());
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setDeleted(false);

        return paymentRepository.save(payment);
    }

    // Confirm payment
    @Override
    @Transactional
    public Payment confirmPayment(Long paymentId) {

        Payment payment = getPaymentById(paymentId);

        if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new IllegalArgumentException(
                    "Only pending payments can be confirmed");
        }

        if (payment.getBooking().getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalArgumentException(
                    "Payment can be confirmed only for confirmed bookings");
        }

        payment.setPaymentStatus(PaymentStatus.SUCCESS);

        return paymentRepository.save(payment);
    }

    // Get payment by ID (excluding Trash)
    @Override
    public Payment getPaymentById(Long id) {

        return paymentRepository.findById(id)
                .filter(payment -> !payment.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found with ID: " + id));
    }

    // Get payment by booking ID (excluding Trash)
    @Override
    public Payment getPaymentByBookingId(Long bookingId) {

        return paymentRepository.findByBookingIdAndDeletedFalse(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found for booking ID: " + bookingId));
    }

    // Get all normal payments
    @Override
    public List<Payment> getAllPayments() {

        return paymentRepository.findByDeletedFalse();
    }

    // Move payment to Trash
    @Override
    @Transactional
    public void movePaymentToTrash(Long id) {

        Payment payment = getPaymentById(id);

        // Keep the original payment status unchanged.
        payment.setDeleted(true);

        paymentRepository.save(payment);
    }

    // Get all trashed payments
    @Override
    public List<Payment> getTrashedPayments() {

        return paymentRepository.findByDeletedTrue();
    }

    // Restore payment
    @Override
    @Transactional
    public void restorePayment(Long id) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found with ID: " + id));

        if (!payment.isDeleted()) {
            throw new IllegalArgumentException(
                    "Payment is not in Trash");
        }

        payment.setDeleted(false);

        paymentRepository.save(payment);
    }

    // Permanently delete payment
    @Override
    @Transactional
    public void permanentlyDeletePayment(Long id) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found with ID: " + id));

        if (!payment.isDeleted()) {
            throw new IllegalArgumentException(
                    "Move the payment to Trash before permanently deleting it");
        }

        paymentRepository.delete(payment);
    }
}
