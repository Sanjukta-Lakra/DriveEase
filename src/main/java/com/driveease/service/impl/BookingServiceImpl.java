package com.driveease.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.driveease.entity.Booking;
import com.driveease.entity.Payment;
import com.driveease.entity.User;
import com.driveease.entity.Vehicle;
import com.driveease.enums.BookingStatus;
import com.driveease.enums.PaymentStatus;
import com.driveease.enums.VehicleStatus;
import com.driveease.exception.ResourceNotFoundException;
import com.driveease.repository.BookingRepository;
import com.driveease.repository.PaymentRepository;
import com.driveease.service.BookingService;
import com.driveease.service.UserService;
import com.driveease.service.VehicleService;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final VehicleService vehicleService;
    private final PaymentRepository paymentRepository;

    public BookingServiceImpl(
            BookingRepository bookingRepository,
            UserService userService,
            VehicleService vehicleService,
            PaymentRepository paymentRepository) {

        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.vehicleService = vehicleService;
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional
    public Booking createBooking(Long userId, Long vehicleId, Booking booking) {

        if (booking.getPickupDate() == null
                || booking.getReturnDate() == null) {
            throw new IllegalArgumentException(
                    "Pickup date and return date are required");
        }

        if (!booking.getReturnDate().isAfter(booking.getPickupDate())) {
            throw new IllegalArgumentException(
                    "Return date must be after pickup date");
        }

        if (booking.getPickupDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Pickup date cannot be in the past");
        }

        User user = userService.getUserById(userId);
        Vehicle vehicle = vehicleService.getVehicleById(vehicleId);

        if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
            throw new IllegalArgumentException(
                    "This vehicle is not available for booking");
        }

        List<Booking> existingBookings =
                bookingRepository.findByVehicleIdAndDeletedFalse(vehicleId);

        for (Booking existing : existingBookings) {

            BookingStatus status = existing.getStatus();

            if (status == BookingStatus.PENDING
                    || status == BookingStatus.CONFIRMED
                    || status == BookingStatus.ACTIVE) {

                boolean datesOverlap =
                        booking.getPickupDate().isBefore(
                                existing.getReturnDate())
                        && booking.getReturnDate().isAfter(
                                existing.getPickupDate());

                if (datesOverlap) {
                    throw new IllegalArgumentException(
                            "Vehicle is already booked for these dates");
                }
            }
        }

        long rentalDays = ChronoUnit.DAYS.between(
                booking.getPickupDate(),
                booking.getReturnDate());

        BigDecimal totalAmount = BigDecimal.valueOf(
                vehicle.getPricePerDay())
                .multiply(BigDecimal.valueOf(rentalDays));

        booking.setUser(user);
        booking.setVehicle(vehicle);
        booking.setTotalAmount(totalAmount);
        booking.setStatus(BookingStatus.PENDING);
        booking.setDeleted(false);

        return bookingRepository.save(booking);
    }

    // Get booking only if it is not in Trash
    @Override
    public Booking getBookingById(Long id) {

        return bookingRepository.findById(id)
                .filter(booking -> !booking.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Booking not found with ID: " + id));
    }

    // Get all normal bookings
    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findByDeletedFalse();
    }

    // Get bookings of a customer
    @Override
    public List<Booking> getBookingsByUser(Long userId) {

        return bookingRepository
                .findByUserIdAndDeletedFalseOrderByCreatedAtDesc(userId);
    }

    // Get bookings for a vehicle
    @Override
    public List<Booking> getBookingsByVehicle(Long vehicleId) {

        return bookingRepository.findByVehicleIdAndDeletedFalse(vehicleId);
    }

    // Update booking status
    @Override
    @Transactional
    public Booking updateBookingStatus(Long id, BookingStatus status) {

        Booking booking = getBookingById(id);

        if (status == null) {
            throw new IllegalArgumentException(
                    "Booking status is required");
        }

        BookingStatus currentStatus = booking.getStatus();

        boolean validTransition = false;

        switch (currentStatus) {

            case PENDING:
                validTransition =
                        status == BookingStatus.CONFIRMED
                        || status == BookingStatus.REJECTED
                        || status == BookingStatus.CANCELLED;
                break;

            case CONFIRMED:
                validTransition =
                        status == BookingStatus.ACTIVE
                        || status == BookingStatus.CANCELLED;
                break;

            case ACTIVE:
                validTransition =
                        status == BookingStatus.COMPLETED;
                break;

            case COMPLETED:
            case CANCELLED:
            case REJECTED:
                validTransition = false;
                break;
        }

        if (!validTransition) {
            throw new IllegalArgumentException(
                    "Invalid booking status transition: "
                    + currentStatus + " to " + status);
        }

        // Verify successful payment before activation
        if (status == BookingStatus.ACTIVE) {

            Payment payment = paymentRepository
                    .findByBookingId(id)
                    .orElse(null);

            if (payment == null
                    || payment.getPaymentStatus() != PaymentStatus.SUCCESS) {

                throw new IllegalArgumentException(
                        "Booking can be activated only after successful payment");
            }
        }

        // Prevent cancellation of successfully paid bookings
        if (status == BookingStatus.CANCELLED) {

            Payment payment = paymentRepository
                    .findByBookingId(id)
                    .orElse(null);

            if (payment != null
                    && payment.getPaymentStatus() == PaymentStatus.SUCCESS) {

                throw new IllegalArgumentException(
                        "A booking with successful payment cannot be cancelled directly. "
                        + "Please process a refund first.");
            }
        }

        Vehicle vehicle = vehicleService.getVehicleById(
                booking.getVehicle().getId());

        // Mark vehicle as RENTED
        if (status == BookingStatus.ACTIVE) {

            if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
                throw new IllegalArgumentException(
                        "Vehicle is not available to start this rental");
            }

            vehicleService.updateVehicleStatus(
                    vehicle.getId(), VehicleStatus.RENTED);
        }

        // Release vehicle after completion
        if (status == BookingStatus.COMPLETED
                && vehicle.getStatus() == VehicleStatus.RENTED) {

            vehicleService.updateVehicleStatus(
                    vehicle.getId(), VehicleStatus.AVAILABLE);
        }

        booking.setStatus(status);

        return bookingRepository.save(booking);
    }

    // Cancel booking
    @Override
    @Transactional
    public void cancelBooking(Long id) {

        Booking booking = getBookingById(id);

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalArgumentException(
                    "Booking is already cancelled");
        }

        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalArgumentException(
                    "Completed bookings cannot be cancelled");
        }

        if (booking.getStatus() == BookingStatus.REJECTED) {
            throw new IllegalArgumentException(
                    "Rejected bookings cannot be cancelled");
        }

        if (booking.getStatus() == BookingStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Active bookings cannot be cancelled");
        }

        Payment payment = paymentRepository
                .findByBookingId(id)
                .orElse(null);

        if (payment != null
                && payment.getPaymentStatus() == PaymentStatus.SUCCESS) {

            throw new IllegalArgumentException(
                    "A booking with successful payment cannot be cancelled directly. "
                    + "Please process a refund first.");
        }

        booking.setStatus(BookingStatus.CANCELLED);

        bookingRepository.save(booking);
    }

    // Move booking to Trash
    @Override
    @Transactional
    public void moveBookingToTrash(Long id) {

    	Booking booking = getBookingById(id);

        BookingStatus status = booking.getStatus();

        if (status != BookingStatus.COMPLETED
                && status != BookingStatus.CANCELLED
                && status != BookingStatus.REJECTED) {

            throw new IllegalArgumentException(
                    "Only completed, cancelled or rejected bookings can be moved to Trash");
        }

        booking.setDeleted(true);

        bookingRepository.save(booking);
    }

    // Get all trashed bookings
    @Override
    public List<Booking> getTrashedBookings() {

        return bookingRepository.findByDeletedTrue();
    }

    // Restore booking
    @Override
    @Transactional
    public void restoreBooking(Long id) {

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Booking not found with ID: " + id));

        if (!booking.isDeleted()) {
            throw new IllegalArgumentException(
                    "Booking is not in Trash");
        }

        booking.setDeleted(false);

        bookingRepository.save(booking);
    }

    // Permanently delete booking
    @Override
    @Transactional
    public void permanentlyDeleteBooking(Long id) {

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Booking not found with ID: " + id));

        if (!booking.isDeleted()) {
            throw new IllegalArgumentException(
                    "Move the booking to Trash before permanently deleting it");
        }

        if (booking.getStatus() == BookingStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Active bookings cannot be permanently deleted");
        }

        // Protect the booking-payment relationship.
        // Delete the associated payment through PaymentService first.
        boolean paymentExists = paymentRepository.existsByBookingId(id);

        if (paymentExists) {
            throw new IllegalArgumentException(
                    "This booking has an associated payment. "
                    + "Permanently delete its payment first.");
        }

        bookingRepository.delete(booking);
    }
}
