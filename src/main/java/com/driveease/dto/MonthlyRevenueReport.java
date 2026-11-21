package com.driveease.dto;

import java.math.BigDecimal;

public class MonthlyRevenueReport {

    private String month;
    private BigDecimal totalRevenue;

    public MonthlyRevenueReport() {
    }

    public MonthlyRevenueReport(String month, BigDecimal totalRevenue) {
        this.month = month;
        this.totalRevenue = totalRevenue;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
