package com.driveease.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.driveease.entity.Vehicle;
import com.driveease.enums.VehicleCategory;
import com.driveease.enums.VehicleStatus;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByRegistrationNumber(String registrationNumber);

    boolean existsByRegistrationNumber(String registrationNumber);

    List<Vehicle> findByStatus(VehicleStatus status);

    List<Vehicle> findByCategory(VehicleCategory category);

    List<Vehicle> findByBrandContainingIgnoreCase(String brand);

}
