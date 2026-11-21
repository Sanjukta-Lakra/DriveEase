package com.driveease.service;

import java.util.List;

import com.driveease.entity.Vehicle;
import com.driveease.enums.VehicleCategory;
import com.driveease.enums.VehicleStatus;

public interface VehicleService {

    Vehicle addVehicle(Vehicle vehicle);

    Vehicle updateVehicle(Long id, Vehicle vehicle);

    Vehicle getVehicleById(Long id);

    List<Vehicle> getAllVehicles();

    List<Vehicle> getAvailableVehicles();

    List<Vehicle> getVehiclesByCategory(VehicleCategory category);

    List<Vehicle> getVehiclesByBrand(String brand);

    void deleteVehicle(Long id);

    Vehicle updateVehicleStatus(Long id, VehicleStatus status);

}
