package com.fooddeliveryapp.service;

import com.fooddeliveryapp.model.Order;
import com.fooddeliveryapp.model.Payment;
import com.fooddeliveryapp.strategy.Impl.PaymentStrategy;

import java.util.List;
import java.util.Optional;

public interface PaymentService {

    // Processes the payment and returns the Payment object so the Order can link its ID
    Payment processPayment(Order order, PaymentStrategy strategy);

    Optional<Payment> getPaymentById(String paymentId);
    List<Payment> getPaymentsByOrderId(String orderId); // Because one order can have multiple payment attempts (failed -> success)
    List<Payment> getAllPayments();

    // Business logic for Admin Dashboard
    double calculateTotalRevenue();
}