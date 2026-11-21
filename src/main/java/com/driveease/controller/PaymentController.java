package com.driveease.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.driveease.dto.DtoMapper;
import com.driveease.dto.PaymentRequest;
import com.driveease.dto.PaymentResponse;
import com.driveease.entity.Booking;
import com.driveease.entity.Payment;
import com.driveease.entity.User;
import com.driveease.service.BookingService;
import com.driveease.service.PaymentService;
import com.driveease.service.UserService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final BookingService bookingService;
    private final UserService userService;
    private final DtoMapper dtoMapper;

    public PaymentController(
            PaymentService paymentService,
            BookingService bookingService,
            UserService userService,
            DtoMapper dtoMapper) {

        this.paymentService = paymentService;
        this.bookingService = bookingService;
        this.userService = userService;
        this.dtoMapper = dtoMapper;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> makePayment(
            @RequestParam Long bookingId,
            @Valid @RequestBody PaymentRequest request,
            Authentication authentication) {

        Booking booking = bookingService.getBookingById(bookingId);

        verifyOwnerOrAdmin(booking.getUser().getId(), authentication);

        Payment payment = new Payment();
        payment.setPaymentMethod(request.getPaymentMethod());

        Payment savedPayment =
                paymentService.makePayment(bookingId, payment);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dtoMapper.toPaymentResponse(savedPayment));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<PaymentResponse> confirmPayment(
            @PathVariable Long id,
            Authentication authentication) {

        Payment payment = paymentService.getPaymentById(id);

        Booking booking =
                bookingService.getBookingById(payment.getBooking().getId());

        verifyOwnerOrAdmin(booking.getUser().getId(), authentication);

        Payment confirmedPayment = paymentService.confirmPayment(id);

        return ResponseEntity.ok(
                dtoMapper.toPaymentResponse(confirmedPayment));
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments(
            Authentication authentication) {

        requireAdmin(authentication);

        List<PaymentResponse> payments = paymentService.getAllPayments()
                .stream()
                .map(dtoMapper::toPaymentResponse)
                .toList();

        return ResponseEntity.ok(payments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(
            @PathVariable Long id,
            Authentication authentication) {

        Payment payment = paymentService.getPaymentById(id);

        Booking booking =
                bookingService.getBookingById(payment.getBooking().getId());

        verifyOwnerOrAdmin(booking.getUser().getId(), authentication);

        return ResponseEntity.ok(dtoMapper.toPaymentResponse(payment));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<PaymentResponse> getPaymentByBookingId(
            @PathVariable Long bookingId,
            Authentication authentication) {

        Booking booking = bookingService.getBookingById(bookingId);

        verifyOwnerOrAdmin(booking.getUser().getId(), authentication);

        Payment payment =
                paymentService.getPaymentByBookingId(bookingId);

        return ResponseEntity.ok(dtoMapper.toPaymentResponse(payment));
    }

    private User getLoggedInUser(Authentication authentication) {
        return userService.getUserByEmail(authentication.getName());
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));
    }

    private void requireAdmin(Authentication authentication) {
        if (!isAdmin(authentication)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Only admins can access all payments");
        }
    }
    

    // ADMIN: Move payment to Trash
    @PatchMapping("/{id}/trash")
    public ResponseEntity<Void> movePaymentToTrash(
            @PathVariable Long id,
            Authentication authentication) {

        requireAdmin(authentication);

        paymentService.movePaymentToTrash(id);

        return ResponseEntity.noContent().build();
    }

    // ADMIN: View trashed payments
    @GetMapping("/trash")
    public ResponseEntity<List<PaymentResponse>> getTrashedPayments(
            Authentication authentication) {

        requireAdmin(authentication);

        List<PaymentResponse> payments =
                paymentService.getTrashedPayments()
                .stream()
                .map(dtoMapper::toPaymentResponse)
                .toList();

        return ResponseEntity.ok(payments);
    }

    // ADMIN: Restore payment from Trash
    @PatchMapping("/{id}/restore")
    public ResponseEntity<Void> restorePayment(
            @PathVariable Long id,
            Authentication authentication) {

        requireAdmin(authentication);

        paymentService.restorePayment(id);

        return ResponseEntity.noContent().build();
    }

    // ADMIN: Permanently delete payment
    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<Void> permanentlyDeletePayment(
            @PathVariable Long id,
            Authentication authentication) {

        requireAdmin(authentication);

        paymentService.permanentlyDeletePayment(id);

        return ResponseEntity.noContent().build();
    }

    private void verifyOwnerOrAdmin(
            Long bookingUserId,
            Authentication authentication) {

        User loggedInUser = getLoggedInUser(authentication);

        if (!isAdmin(authentication)
                && !loggedInUser.getId().equals(bookingUserId)) {

            throw new org.springframework.security.access.AccessDeniedException(
                    "You are not allowed to access this payment");
        }
    }
}
