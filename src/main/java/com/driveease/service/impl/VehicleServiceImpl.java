package com.driveease.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.driveease.entity.Vehicle;
import com.driveease.enums.VehicleCategory;
import com.driveease.enums.VehicleStatus;
import com.driveease.exception.DuplicateResourceException;
import com.driveease.exception.ResourceNotFoundException;
import com.driveease.repository.BookingRepository;
import com.driveease.repository.VehicleRepository;
import com.driveease.service.VehicleService;

@Service
public class VehicleServiceImpl implements VehicleService {

	private final BookingRepository bookingRepository;
    private final VehicleRepository vehicleRepository;

    public VehicleServiceImpl(VehicleRepository vehicleRepository,
            BookingRepository bookingRepository) {

        this.vehicleRepository = vehicleRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Vehicle addVehicle(Vehicle vehicle) {

        if (vehicleRepository.existsByRegistrationNumber(
                vehicle.getRegistrationNumber())) {
        	throw new DuplicateResourceException(
        	        "Vehicle registration number already exists"
        	);
        }

        vehicle.setStatus(VehicleStatus.AVAILABLE);

        return vehicleRepository.save(vehicle);
    }

    @Override
    public Vehicle updateVehicle(Long id, Vehicle vehicle) {

        Vehicle existingVehicle = getVehicleById(id);

        vehicleRepository.findByRegistrationNumber(
                vehicle.getRegistrationNumber())
                .ifPresent(found -> {
                    if (!found.getId().equals(id)) {
                        throw new IllegalArgumentException(
                                "Registration number already exists");
                    }
                });

        existingVehicle.setName(vehicle.getName());
        existingVehicle.setBrand(vehicle.getBrand());
        existingVehicle.setModel(vehicle.getModel());
        existingVehicle.setRegistrationNumber(vehicle.getRegistrationNumber());
        existingVehicle.setCategory(vehicle.getCategory());
        existingVehicle.setPricePerDay(vehicle.getPricePerDay());
        existingVehicle.setSeatingCapacity(vehicle.getSeatingCapacity());
        existingVehicle.setFuelType(vehicle.getFuelType());
        existingVehicle.setTransmission(vehicle.getTransmission());
        existingVehicle.setImageUrl(vehicle.getImageUrl());
        existingVehicle.setStatus(vehicle.getStatus());
        existingVehicle.setDescription(vehicle.getDescription());

        return vehicleRepository.save(existingVehicle);
    }

    @Override
    public Vehicle getVehicleById(Long id) {

        return vehicleRepository.findById(id)
        		.orElseThrow(() -> new ResourceNotFoundException(
        		        "Vehicle not found with ID: " + id));
    }

    @Override
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    @Override
    public List<Vehicle> getAvailableVehicles() {
        return vehicleRepository.findByStatus(VehicleStatus.AVAILABLE);
    }

    @Override
    public List<Vehicle> getVehiclesByCategory(VehicleCategory category) {
        return vehicleRepository.findByCategory(category);
    }

    @Override
    public List<Vehicle> getVehiclesByBrand(String brand) {
        return vehicleRepository.findByBrandContainingIgnoreCase(brand);
    }

    @Override
    public void deleteVehicle(Long id) {

    	Vehicle vehicle = getVehicleById(id);

        if (bookingRepository.existsByVehicleId(id)) {
            throw new DuplicateResourceException(
                    "Cannot delete this vehicle because bookings are associated with it"
            );
        }

        vehicleRepository.delete(vehicle);
    }

    @Override
    public Vehicle updateVehicleStatus(Long id, VehicleStatus status) {

        Vehicle vehicle = getVehicleById(id);

        vehicle.setStatus(status);

        return vehicleRepository.save(vehicle);
    }
}
