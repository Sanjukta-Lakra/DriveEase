package com.driveease.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.driveease.dto.DtoMapper;
import com.driveease.dto.VehicleRequest;
import com.driveease.dto.VehicleResponse;
import com.driveease.entity.Vehicle;
import com.driveease.enums.VehicleCategory;
import com.driveease.service.VehicleService;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;
    private final DtoMapper dtoMapper;

    public VehicleController(VehicleService vehicleService,
                             DtoMapper dtoMapper) {
        this.vehicleService = vehicleService;
        this.dtoMapper = dtoMapper;
    }

    @PostMapping
    public ResponseEntity<VehicleResponse> addVehicle(
            @Valid @RequestBody VehicleRequest request) {

        Vehicle vehicle = new Vehicle();

        vehicle.setName(request.getName());
        vehicle.setBrand(request.getBrand());
        vehicle.setModel(request.getModel());
        vehicle.setRegistrationNumber(request.getRegistrationNumber());
        vehicle.setCategory(request.getCategory());
        vehicle.setPricePerDay(request.getPricePerDay());
        vehicle.setSeatingCapacity(request.getSeatingCapacity());
        vehicle.setFuelType(request.getFuelType());
        vehicle.setTransmission(request.getTransmission());
        vehicle.setImageUrl(request.getImageUrl());
        vehicle.setDescription(request.getDescription());

        Vehicle savedVehicle = vehicleService.addVehicle(vehicle);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dtoMapper.toVehicleResponse(savedVehicle));
    }

    @GetMapping
    public ResponseEntity<List<VehicleResponse>> getAllVehicles() {

        List<VehicleResponse> vehicles = vehicleService.getAllVehicles()
                .stream()
                .map(dtoMapper::toVehicleResponse)
                .toList();

        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> getVehicleById(
            @PathVariable Long id) {

        Vehicle vehicle = vehicleService.getVehicleById(id);

        return ResponseEntity.ok(dtoMapper.toVehicleResponse(vehicle));
    }

    @GetMapping("/available")
    public ResponseEntity<List<VehicleResponse>> getAvailableVehicles() {

        List<VehicleResponse> vehicles = vehicleService.getAvailableVehicles()
                .stream()
                .map(dtoMapper::toVehicleResponse)
                .toList();

        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<VehicleResponse>> getVehiclesByCategory(
            @PathVariable VehicleCategory category) {

        List<VehicleResponse> vehicles =
                vehicleService.getVehiclesByCategory(category)
                .stream()
                .map(dtoMapper::toVehicleResponse)
                .toList();

        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/search")
    public ResponseEntity<List<VehicleResponse>> getVehiclesByBrand(
            @RequestParam String brand) {

        List<VehicleResponse> vehicles =
                vehicleService.getVehiclesByBrand(brand)
                .stream()
                .map(dtoMapper::toVehicleResponse)
                .toList();

        return ResponseEntity.ok(vehicles);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponse> updateVehicle(
            @PathVariable Long id,
            @Valid @RequestBody VehicleRequest request) {

        Vehicle vehicle = new Vehicle();

        vehicle.setName(request.getName());
        vehicle.setBrand(request.getBrand());
        vehicle.setModel(request.getModel());
        vehicle.setRegistrationNumber(request.getRegistrationNumber());
        vehicle.setCategory(request.getCategory());
        vehicle.setPricePerDay(request.getPricePerDay());
        vehicle.setSeatingCapacity(request.getSeatingCapacity());
        vehicle.setFuelType(request.getFuelType());
        vehicle.setTransmission(request.getTransmission());
        vehicle.setImageUrl(request.getImageUrl());
        vehicle.setStatus(request.getStatus());
        vehicle.setDescription(request.getDescription());

        Vehicle updatedVehicle = vehicleService.updateVehicle(id, vehicle);

        return ResponseEntity.ok(dtoMapper.toVehicleResponse(updatedVehicle));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {

        vehicleService.deleteVehicle(id);

        return ResponseEntity.noContent().build();
    }
}
