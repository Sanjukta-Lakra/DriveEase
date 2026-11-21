package com.driveease.dto;

import org.springframework.stereotype.Component;

import com.driveease.entity.User;
import com.driveease.entity.Vehicle;
import com.driveease.entity.Booking;
import com.driveease.entity.Payment;
import com.driveease.entity.Review;

@Component
public class DtoMapper {

    public UserResponse toUserResponse(User user) {

        return new UserResponse(
        	user.getId(),
            user.getName(),
            user.getEmail(),
            user.getPhone(),
            user.getDrivingLicenseNumber(),
            user.getRole().name(),
            user.getCreatedAt(),
            user.isActive()
        );
    }

    public VehicleResponse toVehicleResponse(Vehicle vehicle) {

        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getName(),
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getRegistrationNumber(),
                vehicle.getCategory().name(),
                vehicle.getPricePerDay(),
                vehicle.getSeatingCapacity(),
                vehicle.getFuelType(),
                vehicle.getTransmission(),
                vehicle.getImageUrl(),
                vehicle.getStatus().name(),
                vehicle.getDescription(),
                vehicle.getCreatedAt()
        );
    }

    public BookingResponse toBookingResponse(Booking booking) {

        return new BookingResponse(
                booking.getId(),
                booking.getUser().getId(),
                booking.getUser().getName(),
                booking.getVehicle().getId(),
                booking.getVehicle().getName(),
                booking.getPickupDate(),
                booking.getReturnDate(),
                booking.getTotalAmount(),
                booking.getStatus().name(),
                booking.getCreatedAt()
        );
    }

    public PaymentResponse toPaymentResponse(Payment payment) {

        return new PaymentResponse(
                payment.getId(),
                payment.getBooking().getId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getTransactionId(),
                payment.getPaymentStatus().name(),
                payment.getPaymentDate()
        );
    }

    public ReviewResponse toReviewResponse(Review review) {

        return new ReviewResponse(
                review.getId(),
                review.getUser().getId(),
                review.getUser().getName(),
                review.getVehicle().getId(),
                review.getVehicle().getName(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }
}
