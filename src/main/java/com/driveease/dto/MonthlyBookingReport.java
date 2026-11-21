package com.driveease.dto;

public class MonthlyBookingReport {

    private String month;
    private long totalBookings;

    public MonthlyBookingReport() {
    }

    public MonthlyBookingReport(String month, long totalBookings) {
        this.month = month;
        this.totalBookings = totalBookings;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public long getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(long totalBookings) {
        this.totalBookings = totalBookings;
    }
}
