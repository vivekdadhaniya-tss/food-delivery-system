package com.fooddeliveryapp.model;

import com.fooddeliveryapp.model.type.PaymentMode;
import com.fooddeliveryapp.model.type.PaymentStatus;

import java.time.LocalDateTime;

public class Payment {

    private final String id;
    private final String orderId;
    private final double amount;
    private final PaymentMode mode;

    private PaymentStatus status;

    private final LocalDateTime createdAt;
    private LocalDateTime paidAt;

    public Payment(String id, String orderId, double amount, PaymentMode mode) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.mode = mode;
        this.status = PaymentStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }


    // getters
    public String getId() {
        return id;
    }
    public String getOrderId() {
        return orderId;
    }
    public double getAmount() {
        return amount;
    }
    public PaymentMode getMode() {
        return mode;
    }
    public PaymentStatus getStatus() {
        return status;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public LocalDateTime getPaidAt() {
        return paidAt;
    }


    // business logic
    public void markSuccess() {
        if (status != PaymentStatus.PENDING) {
            throw new IllegalStateException("Payment already processed");
        }

        this.status = PaymentStatus.SUCCESS;
        this.paidAt = LocalDateTime.now();
    }

    public void markFailed() {
        if (status != PaymentStatus.PENDING) {
            throw new IllegalStateException("Payment already processed");
        }

        this.status = PaymentStatus.FAILED;
    }
}