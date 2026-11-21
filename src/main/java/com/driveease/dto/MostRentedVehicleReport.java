package com.driveease.dto;

public class MostRentedVehicleReport {

    private Long vehicleId;
    private String brand;
    private String model;
    private long totalBookings;

    public MostRentedVehicleReport() {
    }

    public MostRentedVehicleReport(
            Long vehicleId,
            String brand,
            String model,
            long totalBookings) {

        this.vehicleId = vehicleId;
        this.brand = brand;
        this.model = model;
        this.totalBookings = totalBookings;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public long getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(long totalBookings) {
        this.totalBookings = totalBookings;
    }
}
