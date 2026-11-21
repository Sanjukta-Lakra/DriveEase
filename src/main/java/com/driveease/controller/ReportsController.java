package com.driveease.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.driveease.dto.DateRangeReport;
import com.driveease.dto.MonthlyBookingReport;
import com.driveease.dto.MonthlyCustomerReport;
import com.driveease.dto.MonthlyRevenueReport;
import com.driveease.dto.MostRentedVehicleReport;
import com.driveease.dto.ReportSummary;
import com.driveease.service.ReportService;

@RestController
@RequestMapping("/api/admin/reports")
public class ReportsController {

    private final ReportService reportService;

    public ReportsController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/summary")
    public ResponseEntity<ReportSummary> getReportSummary() {

        ReportSummary summary = reportService.getReportSummary();

        return ResponseEntity.ok(summary);
    }
    

    @GetMapping("/monthly-bookings")
    public ResponseEntity<List<MonthlyBookingReport>> getMonthlyBookingReport() {

        List<MonthlyBookingReport> report =
            reportService.getMonthlyBookingReport();

        return ResponseEntity.ok(report);
    }


    @GetMapping("/monthly-revenue")
    public ResponseEntity<List<MonthlyRevenueReport>> getMonthlyRevenueReport() {

        List<MonthlyRevenueReport> report = reportService.getMonthlyRevenueReport();

        return ResponseEntity.ok(report);
    }


    @GetMapping("/most-rented-vehicles")
    public ResponseEntity<List<MostRentedVehicleReport>> getMostRentedVehiclesReport() {

        List<MostRentedVehicleReport> report =
            reportService.getMostRentedVehiclesReport();

        return ResponseEntity.ok(report);
    }


    @GetMapping("/monthly-customers")
    public ResponseEntity<List<MonthlyCustomerReport>> getMonthlyCustomerReport() {

        List<MonthlyCustomerReport> report =
            reportService.getMonthlyCustomerReport();

        return ResponseEntity.ok(report);
    }

    @GetMapping("/date-range")
    public ResponseEntity<DateRangeReport> getDateRangeReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        DateRangeReport report = reportService.getDateRangeReport(startDate, endDate);

        return ResponseEntity.ok(report);
    }
}
