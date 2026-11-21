package com.driveease.service;

import java.util.List;

import com.driveease.entity.Review;

public interface ReviewService {

    Review addReview(Long userId, Long vehicleId, Review review);

    Review getReviewById(Long id);

    List<Review> getAllReviews();

    List<Review> getReviewsByVehicle(Long vehicleId);

    List<Review> getReviewsByUser(Long userId);

    void deleteReview(Long id);

}
