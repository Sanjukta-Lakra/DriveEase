package com.driveease.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.AssertTrue;

public class BookingRequest {

    @NotNull(message = "Pickup date is required")
    @FutureOrPresent(message = "Pickup date cannot be in the past")
    private LocalDate pickupDate;

    @NotNull(message = "Return date is required")
    @FutureOrPresent(message = "Return date cannot be in the past")
    private LocalDate returnDate;

    public BookingRequest() {
    }

    public LocalDate getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(LocalDate pickupDate) {
        this.pickupDate = pickupDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    @AssertTrue(message = "Return date must be after pickup date")
    public boolean isReturnDateValid() {

        if (pickupDate == null || returnDate == null) {
            return true;
        }

        return returnDate.isAfter(pickupDate);
    }
}
