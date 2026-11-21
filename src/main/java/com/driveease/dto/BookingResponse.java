package com.driveease.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BookingResponse {

    private Long id;
    private Long userId;
    private String customerName;
    private Long vehicleId;
    private String vehicleName;
    private LocalDate pickupDate;
    private LocalDate returnDate;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;

    public BookingResponse() {
    }

    public BookingResponse(Long id, Long userId, String customerName,
            Long vehicleId, String vehicleName,
            LocalDate pickupDate, LocalDate returnDate,
            BigDecimal totalAmount, String status,
            LocalDateTime createdAt) {

        this.id = id;
        this.userId = userId;
        this.customerName = customerName;
        this.vehicleId = vehicleId;
        this.vehicleName = vehicleName;
        this.pickupDate = pickupDate;
        this.returnDate = returnDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }

    public String getVehicleName() { return vehicleName; }
    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public LocalDate getPickupDate() { return pickupDate; }
    public void setPickupDate(LocalDate pickupDate) {
        this.pickupDate = pickupDate;
    }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
