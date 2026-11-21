package com.driveease.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.driveease.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByVehicleIdOrderByCreatedAtDesc(Long vehicleId);

    List<Review> findByUserId(Long userId);

    boolean existsByUserIdAndVehicleId(Long userId, Long vehicleId);

    boolean existsByUserId(Long userId);
}
