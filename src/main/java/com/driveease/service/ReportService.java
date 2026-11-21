package com.driveease.service;

import java.time.LocalDate;
import java.util.List;

import com.driveease.dto.DateRangeReport;
import com.driveease.dto.MonthlyBookingReport;
import com.driveease.dto.MonthlyCustomerReport;
import com.driveease.dto.MonthlyRevenueReport;
import com.driveease.dto.MostRentedVehicleReport;
import com.driveease.dto.ReportSummary;

public interface ReportService {

    ReportSummary getReportSummary();

    List<MonthlyBookingReport> getMonthlyBookingReport();
    
    List<MonthlyRevenueReport> getMonthlyRevenueReport();
    
    List<MostRentedVehicleReport> getMostRentedVehiclesReport();
    
    List<MonthlyCustomerReport> getMonthlyCustomerReport();
    
    DateRangeReport getDateRangeReport(LocalDate startDate, LocalDate endDate);
}
