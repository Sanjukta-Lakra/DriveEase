package com.driveease.dto;

public class MonthlyCustomerReport {

    private String month;
    private long totalCustomers;

    public MonthlyCustomerReport() {
    }

    public MonthlyCustomerReport(String month, long totalCustomers) {
        this.month = month;
        this.totalCustomers = totalCustomers;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public long getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(long totalCustomers) {
        this.totalCustomers = totalCustomers;
    }
}
