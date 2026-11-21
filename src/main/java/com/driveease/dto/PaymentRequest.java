package com.driveease.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PaymentRequest {

    @NotBlank(message = "Payment method is required")
    @Pattern(
        regexp = "UPI|CARD|NET_BANKING|CASH",
        message = "Payment method must be UPI, CARD, NET_BANKING or CASH"
    )
    private String paymentMethod;

    @Size(max = 100, message = "Transaction ID cannot exceed 100 characters")
    private String transactionId;

    public PaymentRequest() {
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
