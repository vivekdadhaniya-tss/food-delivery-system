package com.fooddeliveryapp.service;

import com.fooddeliveryapp.model.Order;
import com.fooddeliveryapp.strategy.Impl.PaymentStrategy;

public interface PaymentService {

    // Processes payment for the given order using the provided payment strategy.
    // Updates order's payment status and returns true if successful.
    boolean processPayment(Order order, PaymentStrategy strategy);
}