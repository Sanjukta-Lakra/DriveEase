package com.driveease.service;

import java.util.List;

import com.driveease.entity.Booking;
import com.driveease.enums.BookingStatus;

public interface BookingService {

    Booking createBooking(Long userId, Long vehicleId, Booking booking);

    Booking getBookingById(Long id);

    List<Booking> getAllBookings();

    List<Booking> getBookingsByUser(Long userId);

    List<Booking> getBookingsByVehicle(Long vehicleId);

    Booking updateBookingStatus(Long id, BookingStatus status);

    void cancelBooking(Long id);

    // Trash operations
    void moveBookingToTrash(Long id);

    List<Booking> getTrashedBookings();

    void restoreBooking(Long id);

    void permanentlyDeleteBooking(Long id);
}
