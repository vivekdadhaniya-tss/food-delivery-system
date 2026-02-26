package com.fooddeliveryapp.model;

import com.fooddeliveryapp.model.type.PaymentMethod;
import com.fooddeliveryapp.model.type.PaymentStatus;

import java.time.LocalDateTime;

public class Payment {

    private final String paymentId;
    private final String orderNumber;
    private final double amount;
    private final PaymentMethod method;

    private PaymentStatus status;
    private final LocalDateTime createdAt;

    public Payment(String paymentId,
                   String orderNumber,
                   double amount,
                   PaymentMethod method) {

        this.paymentId = paymentId;
        this.orderNumber = orderNumber;
        this.amount = amount;
        this.method = method;
        this.status = PaymentStatus.INITIATED;
        this.createdAt = LocalDateTime.now();
    }

    // ===== Business Methods =====

    public void markSuccess() {
        this.status = PaymentStatus.SUCCESS;
    }

    public void markFailed() {
        this.status = PaymentStatus.FAILED;
    }

    // ===== Getters =====

    public String getPaymentId() { return paymentId; }

    public String getOrderNumber() { return orderNumber; }

    public double getAmount() { return amount; }

    public PaymentMethod getMethod() { return method; }

    public PaymentStatus getStatus() { return status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}