package com.driveease.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.driveease.dto.BookingRequest;
import com.driveease.dto.BookingResponse;
import com.driveease.dto.DtoMapper;
import com.driveease.entity.Booking;
import com.driveease.entity.User;
import com.driveease.enums.BookingStatus;
import com.driveease.service.BookingService;
import com.driveease.service.UserService;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;
    private final DtoMapper dtoMapper;

    public BookingController(
            BookingService bookingService,
            UserService userService,
            DtoMapper dtoMapper) {

        this.bookingService = bookingService;
        this.userService = userService;
        this.dtoMapper = dtoMapper;
    }

    // Create booking for the logged-in user
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @RequestParam Long vehicleId,
            @Valid @RequestBody BookingRequest request,
            Authentication authentication) {

        User user = getLoggedInUser(authentication);

        Booking booking = new Booking();
        booking.setPickupDate(request.getPickupDate());
        booking.setReturnDate(request.getReturnDate());

        Booking savedBooking = bookingService.createBooking(
                user.getId(), vehicleId, booking);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dtoMapper.toBookingResponse(savedBooking));
    }

    // Only ADMIN can view all bookings
    @GetMapping
    public ResponseEntity<List<BookingResponse>> getAllBookings(
            Authentication authentication) {

        requireAdmin(authentication);

        List<BookingResponse> bookings = bookingService.getAllBookings()
                .stream()
                .map(dtoMapper::toBookingResponse)
                .toList();

        return ResponseEntity.ok(bookings);
    }

    // Customer can view their own booking; ADMIN can view any booking
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(
            @PathVariable Long id,
            Authentication authentication) {

        Booking booking = bookingService.getBookingById(id);

        verifyOwnerOrAdmin(booking, authentication);

        return ResponseEntity.ok(dtoMapper.toBookingResponse(booking));
    }

    // Customer can view their own bookings; ADMIN can view any customer's bookings
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingResponse>> getBookingsByUser(
            @PathVariable Long userId,
            Authentication authentication) {

        User loggedInUser = getLoggedInUser(authentication);

        if (!isAdmin(authentication)
                && !loggedInUser.getId().equals(userId)) {
            throw new AccessDeniedException(
                    "You cannot access another customer's bookings");
        }

        List<BookingResponse> bookings =
                bookingService.getBookingsByUser(userId)
                .stream()
                .map(dtoMapper::toBookingResponse)
                .toList();

        return ResponseEntity.ok(bookings);
    }

    // Only ADMIN can view bookings for a vehicle
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<BookingResponse>> getBookingsByVehicle(
            @PathVariable Long vehicleId,
            Authentication authentication) {

        requireAdmin(authentication);

        List<BookingResponse> bookings =
                bookingService.getBookingsByVehicle(vehicleId)
                .stream()
                .map(dtoMapper::toBookingResponse)
                .toList();

        return ResponseEntity.ok(bookings);
    }

    // Only ADMIN can update booking status
    @PutMapping("/{id}/status")
    public ResponseEntity<BookingResponse> updateBookingStatus(
            @PathVariable Long id,
            @RequestParam BookingStatus status,
            Authentication authentication) {

        requireAdmin(authentication);

        Booking updatedBooking =
                bookingService.updateBookingStatus(id, status);

        return ResponseEntity.ok(
                dtoMapper.toBookingResponse(updatedBooking));
    }

    // Customer can cancel their own booking; ADMIN can cancel any booking
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(
            @PathVariable Long id,
            Authentication authentication) {

        Booking booking = bookingService.getBookingById(id);

        verifyOwnerOrAdmin(booking, authentication);

        bookingService.cancelBooking(id);

        return ResponseEntity.noContent().build();
    }


    // ADMIN: Move booking to Trash
    @PatchMapping("/{id}/trash")
    public ResponseEntity<Void> moveBookingToTrash(
            @PathVariable Long id,
            Authentication authentication) {

        requireAdmin(authentication);

        bookingService.moveBookingToTrash(id);

        return ResponseEntity.noContent().build();
    }

    // ADMIN: View trashed bookings
    @GetMapping("/trash")
    public ResponseEntity<List<BookingResponse>> getTrashedBookings(
            Authentication authentication) {

        requireAdmin(authentication);

        List<BookingResponse> bookings =
                bookingService.getTrashedBookings()
                .stream()
                .map(dtoMapper::toBookingResponse)
                .toList();

        return ResponseEntity.ok(bookings);
    }

    // ADMIN: Restore booking from Trash
    @PatchMapping("/{id}/restore")
    public ResponseEntity<Void> restoreBooking(
            @PathVariable Long id,
            Authentication authentication) {

        requireAdmin(authentication);

        bookingService.restoreBooking(id);

        return ResponseEntity.noContent().build();
    }

    // ADMIN: Permanently delete booking
    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<Void> permanentlyDeleteBooking(
            @PathVariable Long id,
            Authentication authentication) {

        requireAdmin(authentication);

        bookingService.permanentlyDeleteBooking(id);

        return ResponseEntity.noContent().build();
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
            throw new AccessDeniedException(
                    "Only administrators can perform this operation");
        }
    }

    private void verifyOwnerOrAdmin(
            Booking booking, Authentication authentication) {

        if (isAdmin(authentication)) {
            return;
        }

        User loggedInUser = getLoggedInUser(authentication);

        if (!booking.getUser().getId().equals(loggedInUser.getId())) {
            throw new AccessDeniedException(
                    "You cannot access another customer's booking");
        }
    }
}
