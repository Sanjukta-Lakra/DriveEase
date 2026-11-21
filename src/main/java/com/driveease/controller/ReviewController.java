package com.driveease.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.driveease.dto.DtoMapper;
import com.driveease.dto.ReviewRequest;
import com.driveease.dto.ReviewResponse;
import com.driveease.entity.Review;
import com.driveease.entity.User;
import com.driveease.service.ReviewService;
import com.driveease.service.UserService;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;
    private final DtoMapper dtoMapper;

    public ReviewController(
            ReviewService reviewService,
            UserService userService,
            DtoMapper dtoMapper) {

        this.reviewService = reviewService;
        this.userService = userService;
        this.dtoMapper = dtoMapper;
    }

    // Add review using logged-in user's account
    @PostMapping
    public ResponseEntity<ReviewResponse> addReview(
            @RequestParam Long vehicleId,
            @Valid @RequestBody ReviewRequest request,
            Authentication authentication) {

        User user = getLoggedInUser(authentication);

        Review review = new Review();
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review savedReview =
                reviewService.addReview(user.getId(), vehicleId, review);

        return ResponseEntity.status(201)
                .body(dtoMapper.toReviewResponse(savedReview));
    }
    
    // View all reviews (Admin only)
    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getAllReviews() {

        List<ReviewResponse> reviews =
                reviewService.getAllReviews()
                .stream()
                .map(dtoMapper::toReviewResponse)
                .toList();

        return ResponseEntity.ok(reviews);
    }

    // View individual review
    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> getReviewById(
            @PathVariable Long id) {

        Review review = reviewService.getReviewById(id);

        return ResponseEntity.ok(dtoMapper.toReviewResponse(review));
    }

    // View all reviews for a vehicle
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByVehicle(
            @PathVariable Long vehicleId) {

        List<ReviewResponse> reviews =
                reviewService.getReviewsByVehicle(vehicleId)
                .stream()
                .map(dtoMapper::toReviewResponse)
                .toList();

        return ResponseEntity.ok(reviews);
    }

    // View reviews by user (owner or admin)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByUser(
            @PathVariable Long userId,
            Authentication authentication) {

        verifyOwnerOrAdmin(userId, authentication);

        List<ReviewResponse> reviews =
                reviewService.getReviewsByUser(userId)
                .stream()
                .map(dtoMapper::toReviewResponse)
                .toList();

        return ResponseEntity.ok(reviews);
    }

    // Delete review (owner or admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long id,
            Authentication authentication) {

        Review review = reviewService.getReviewById(id);

        verifyOwnerOrAdmin(review.getUser().getId(), authentication);

        reviewService.deleteReview(id);

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

    private void verifyOwnerOrAdmin(
            Long reviewUserId,
            Authentication authentication) {

        User loggedInUser = getLoggedInUser(authentication);

        if (!isAdmin(authentication)
                && !loggedInUser.getId().equals(reviewUserId)) {

            throw new AccessDeniedException(
                    "You are not allowed to access this review");
        }
    }
}
