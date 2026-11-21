package com.driveease.dto;

import java.time.LocalDateTime;

public class VehicleResponse {

    private Long id;
    private String name;
    private String brand;
    private String model;
    private String registrationNumber;
    private String category;
    private Double pricePerDay;
    private Integer seatingCapacity;
    private String fuelType;
    private String transmission;
    private String imageUrl;
    private String status;
    private String description;
    private LocalDateTime createdAt;

    public VehicleResponse() {
    }

    public VehicleResponse(Long id, String name, String brand,
            String model, String registrationNumber,
            String category, Double pricePerDay,
            Integer seatingCapacity, String fuelType,
            String transmission, String imageUrl,
            String status, String description,
            LocalDateTime createdAt) {

        this.id = id;
        this.name = name;
        this.brand = brand;
        this.model = model;
        this.registrationNumber = registrationNumber;
        this.category = category;
        this.pricePerDay = pricePerDay;
        this.seatingCapacity = seatingCapacity;
        this.fuelType = fuelType;
        this.transmission = transmission;
        this.imageUrl = imageUrl;
        this.status = status;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Double getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(Double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public Integer getSeatingCapacity() { return seatingCapacity; }
    public void setSeatingCapacity(Integer seatingCapacity) {
        this.seatingCapacity = seatingCapacity;
    }

    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }

    public String getTransmission() { return transmission; }
    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
