package com.driveease.dto;

import java.time.LocalDateTime;

public class ReviewResponse {

    private Long id;
    private Long userId;
    private String customerName;
    private Long vehicleId;
    private String vehicleName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;

    public ReviewResponse() {
    }

    public ReviewResponse(Long id, Long userId, String customerName,
            Long vehicleId, String vehicleName,
            Integer rating, String comment,
            LocalDateTime createdAt) {

        this.id = id;
        this.userId = userId;
        this.customerName = customerName;
        this.vehicleId = vehicleId;
        this.vehicleName = vehicleName;
        this.rating = rating;
        this.comment = comment;
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

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
