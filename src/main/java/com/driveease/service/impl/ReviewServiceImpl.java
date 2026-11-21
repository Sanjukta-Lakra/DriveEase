package com.driveease.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.driveease.entity.Booking;
import com.driveease.entity.Review;
import com.driveease.enums.BookingStatus;
import com.driveease.exception.ResourceNotFoundException;
import com.driveease.repository.ReviewRepository;
import com.driveease.service.BookingService;
import com.driveease.service.ReviewService;
import com.driveease.service.UserService;
import com.driveease.service.VehicleService;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final VehicleService vehicleService;
    private final BookingService bookingService;

    public ReviewServiceImpl(
            ReviewRepository reviewRepository,
            UserService userService,
            VehicleService vehicleService,
            BookingService bookingService) {

        this.reviewRepository = reviewRepository;
        this.userService = userService;
        this.vehicleService = vehicleService;
        this.bookingService = bookingService;
    }

    @Override
    public Review addReview(Long userId, Long vehicleId, Review review) {

        if (review.getRating() == null
                || review.getRating() < 1
                || review.getRating() > 5) {
            throw new IllegalArgumentException(
                    "Rating must be between 1 and 5");
        }

        if (reviewRepository.existsByUserIdAndVehicleId(
                userId, vehicleId)) {
            throw new IllegalArgumentException(
                    "You have already reviewed this vehicle");
        }

        List<Booking> bookings =
                bookingService.getBookingsByUser(userId);

        boolean completedRental = bookings.stream()
                .anyMatch(booking ->
                        booking.getVehicle().getId().equals(vehicleId)
                        && booking.getStatus() == BookingStatus.COMPLETED);

        if (!completedRental) {
            throw new IllegalArgumentException(
                    "Only customers with completed rentals can review this vehicle");
        }

        review.setUser(userService.getUserById(userId));
        review.setVehicle(vehicleService.getVehicleById(vehicleId));

        return reviewRepository.save(review);
    }

    @Override
    public Review getReviewById(Long id) {

        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Review not found with ID: " + id));
    }

    // Get all reviews for admin
    @Override
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @Override
    public List<Review> getReviewsByVehicle(Long vehicleId) {
        return reviewRepository.findByVehicleIdOrderByCreatedAtDesc(vehicleId);
    }

    @Override
    public List<Review> getReviewsByUser(Long userId) {
        return reviewRepository.findByUserId(userId);
    }

    @Override
    public void deleteReview(Long id) {

        Review review = getReviewById(id);

        reviewRepository.delete(review);
    }
}
