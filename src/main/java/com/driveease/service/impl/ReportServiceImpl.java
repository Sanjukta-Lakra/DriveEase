package com.driveease.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.driveease.dto.DateRangeReport;
import com.driveease.dto.MonthlyBookingReport;
import com.driveease.dto.MonthlyCustomerReport;
import com.driveease.dto.MonthlyRevenueReport;
import com.driveease.dto.MostRentedVehicleReport;
import com.driveease.dto.ReportSummary;
import com.driveease.entity.Booking;
import com.driveease.entity.Payment;
import com.driveease.entity.User;
import com.driveease.enums.BookingStatus;
import com.driveease.enums.PaymentStatus;
import com.driveease.enums.UserRole;
import com.driveease.repository.BookingRepository;
import com.driveease.repository.PaymentRepository;
import com.driveease.repository.UserRepository;
import com.driveease.service.ReportService;

@Service
public class ReportServiceImpl implements ReportService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    public ReportServiceImpl(
            BookingRepository bookingRepository,
            PaymentRepository paymentRepository,
            UserRepository userRepository) {

        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ReportSummary getReportSummary() {

        ReportSummary summary = new ReportSummary();

        // Booking statistics
        summary.setTotalBookings(bookingRepository.count());

        summary.setPendingBookings(
                bookingRepository.countByStatus(BookingStatus.PENDING));

        summary.setConfirmedBookings(
                bookingRepository.countByStatus(BookingStatus.CONFIRMED));

        summary.setActiveBookings(
                bookingRepository.countByStatus(BookingStatus.ACTIVE));

        summary.setCompletedBookings(
                bookingRepository.countByStatus(BookingStatus.COMPLETED));

        summary.setCancelledBookings(
                bookingRepository.countByStatus(BookingStatus.CANCELLED));

        summary.setRejectedBookings(
                bookingRepository.countByStatus(BookingStatus.REJECTED));

        // Customer and administrator statistics
        summary.setTotalCustomers(
                userRepository.countByRole(UserRole.CUSTOMER));

        summary.setTotalAdmins(
                userRepository.countByRole(UserRole.ADMIN));

        summary.setActiveUsers(userRepository.countByActiveTrue());

        summary.setInactiveUsers(userRepository.countByActiveFalse());

        // Payment statistics
        summary.setTotalPayments(paymentRepository.count());

        summary.setSuccessfulPayments(
                paymentRepository.countByPaymentStatus(PaymentStatus.SUCCESS));

        summary.setPendingPayments(
                paymentRepository.countByPaymentStatus(PaymentStatus.PENDING));

        summary.setFailedPayments(
                paymentRepository.countByPaymentStatus(PaymentStatus.FAILED));

        summary.setRefundedPayments(
                paymentRepository.countByPaymentStatus(PaymentStatus.REFUNDED));

        // Successful payment revenue only
        BigDecimal revenue = paymentRepository.getTotalRevenueByStatus(
                PaymentStatus.SUCCESS);

        summary.setTotalRevenue(
                revenue != null ? revenue : BigDecimal.ZERO);

        return summary;
    }
    

    @Override
    public List<MonthlyBookingReport> getMonthlyBookingReport() {

        List<MonthlyBookingReport> monthlyReports = new ArrayList<>();

        LocalDate currentDate = LocalDate.now();

        for (int i = 11; i >= 0; i--) {

            LocalDate monthDate = currentDate.minusMonths(i);

            LocalDateTime startDate = monthDate
                .withDayOfMonth(1)
                .atStartOfDay();

            LocalDateTime endDate = monthDate
                .withDayOfMonth(monthDate.lengthOfMonth())
                .atTime(23, 59, 59, 999999999);

            List<Booking> bookings = bookingRepository.findByCreatedAtBetween(
                startDate, endDate);

            String monthName = monthDate.getMonth()
                .getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                + " " + monthDate.getYear();

            monthlyReports.add(
                new MonthlyBookingReport(monthName, bookings.size()));
        }

        return monthlyReports;
    }

    

    @Override
    public List<MonthlyRevenueReport> getMonthlyRevenueReport() {

        List<MonthlyRevenueReport> monthlyReports = new ArrayList<>();

        LocalDate currentDate = LocalDate.now();

        for (int i = 11; i >= 0; i--) {

            LocalDate monthDate = currentDate.minusMonths(i);

            LocalDateTime startDate = monthDate
                .withDayOfMonth(1)
                .atStartOfDay();

            LocalDateTime endDate = monthDate
                .withDayOfMonth(monthDate.lengthOfMonth())
                .atTime(23, 59, 59, 999999999);

            List<Payment> payments = paymentRepository.findByPaymentDateBetween(
                startDate, endDate);

            BigDecimal monthlyRevenue = payments.stream()
                .filter(payment -> payment.getPaymentStatus()
                        == PaymentStatus.SUCCESS)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            String monthName = monthDate.getMonth()
                .getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                + " " + monthDate.getYear();

            monthlyReports.add(
                new MonthlyRevenueReport(monthName, monthlyRevenue));
        }

        return monthlyReports;
    }

	@Override
	public List<MostRentedVehicleReport> getMostRentedVehiclesReport() {
		List<BookingStatus> eligibleStatuses = List.of(
	            BookingStatus.CONFIRMED,
	            BookingStatus.ACTIVE,
	            BookingStatus.COMPLETED
	    );

	    List<Object[]> results =
	            bookingRepository.findMostRentedVehicles(eligibleStatuses);

	    List<MostRentedVehicleReport> reports = new ArrayList<>();

	    for (Object[] result : results) {

	        Long vehicleId = (Long) result[0];
	        String brand = (String) result[1];
	        String model = (String) result[2];
	        long totalBookings = ((Number) result[3]).longValue();

	        reports.add(
	                new MostRentedVehicleReport(
	                        vehicleId,
	                        brand,
	                        model,
	                        totalBookings
	                )
	        );
	    }

	    return reports;
	}

	@Override
	public List<MonthlyCustomerReport> getMonthlyCustomerReport() {
		List<MonthlyCustomerReport> monthlyReports = new ArrayList<>();

	    LocalDate currentDate = LocalDate.now();

	    for (int i = 11; i >= 0; i--) {

	        LocalDate monthDate = currentDate.minusMonths(i);

	        LocalDateTime startDate = monthDate
	                .withDayOfMonth(1)
	                .atStartOfDay();

	        LocalDateTime endDate = monthDate
	                .withDayOfMonth(monthDate.lengthOfMonth())
	                .atTime(23, 59, 59, 999999999);

	        List<User> customers = userRepository.findByRoleAndCreatedAtBetween(
	                UserRole.CUSTOMER,
	                startDate,
	                endDate
	        );

	        String monthName = monthDate.getMonth()
	                .getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
	                + " " + monthDate.getYear();

	        monthlyReports.add(
	                new MonthlyCustomerReport(monthName, customers.size())
	        );
	    }

	    return monthlyReports;
	}

	@Override
	public DateRangeReport getDateRangeReport(LocalDate startDate, LocalDate endDate) {
		if (startDate == null || endDate == null
	            || endDate.isBefore(startDate)) {
	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST,
	                "Please provide a valid date range."
	        );
	    }

	    LocalDateTime start = startDate.atStartOfDay();

	    LocalDateTime end = endDate.atTime(
	            23, 59, 59, 999999999
	    );

	    DateRangeReport report = new DateRangeReport();

	    report.setStartDate(startDate);
	    report.setEndDate(endDate);

	    // Booking statistics
	    List<Booking> bookings =
	            bookingRepository.findByCreatedAtBetween(start, end);

	    report.setTotalBookings(bookings.size());

	    report.setPendingBookings(bookings.stream()
	            .filter(b -> b.getStatus() == BookingStatus.PENDING)
	            .count());

	    report.setConfirmedBookings(bookings.stream()
	            .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
	            .count());

	    report.setActiveBookings(bookings.stream()
	            .filter(b -> b.getStatus() == BookingStatus.ACTIVE)
	            .count());

	    report.setCompletedBookings(bookings.stream()
	            .filter(b -> b.getStatus() == BookingStatus.COMPLETED)
	            .count());

	    report.setCancelledBookings(bookings.stream()
	            .filter(b -> b.getStatus() == BookingStatus.CANCELLED)
	            .count());

	    report.setRejectedBookings(bookings.stream()
	            .filter(b -> b.getStatus() == BookingStatus.REJECTED)
	            .count());

	    // Customer registrations
	    List<User> customers =
	            userRepository.findByRoleAndCreatedAtBetween(
	                    UserRole.CUSTOMER, start, end
	            );

	    report.setTotalCustomers(customers.size());

	    // Payment statistics
	    List<Payment> payments =
	            paymentRepository.findByPaymentDateBetween(start, end);

	    report.setTotalPayments(payments.size());

	    report.setSuccessfulPayments(payments.stream()
	            .filter(p -> p.getPaymentStatus() == PaymentStatus.SUCCESS)
	            .count());

	    report.setPendingPayments(payments.stream()
	            .filter(p -> p.getPaymentStatus() == PaymentStatus.PENDING)
	            .count());

	    report.setFailedPayments(payments.stream()
	            .filter(p -> p.getPaymentStatus() == PaymentStatus.FAILED)
	            .count());

	    report.setRefundedPayments(payments.stream()
	            .filter(p -> p.getPaymentStatus() == PaymentStatus.REFUNDED)
	            .count());

	    BigDecimal revenue = payments.stream()
	            .filter(p -> p.getPaymentStatus() == PaymentStatus.SUCCESS)
	            .map(Payment::getAmount)
	            .reduce(BigDecimal.ZERO, BigDecimal::add);

	    report.setTotalRevenue(revenue);

	    return report;
	}

}
