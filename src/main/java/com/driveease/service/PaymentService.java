package com.driveease.service;

import java.util.List;

import com.driveease.entity.Payment;

public interface PaymentService {

    Payment makePayment(Long bookingId, Payment payment);

    Payment getPaymentById(Long id);

    Payment getPaymentByBookingId(Long bookingId);

    List<Payment> getAllPayments();

    Payment confirmPayment(Long paymentId);

    // Trash operations
    void movePaymentToTrash(Long id);

    List<Payment> getTrashedPayments();

    void restorePayment(Long id);

    void permanentlyDeletePayment(Long id);
}
