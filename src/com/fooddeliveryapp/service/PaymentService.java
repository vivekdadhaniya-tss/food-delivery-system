package com.fooddeliveryapp.service;

import com.fooddeliveryapp.model.Order;
import com.fooddeliveryapp.model.Payment;
import com.fooddeliveryapp.strategy.Impl.PaymentStrategy;
import java.util.List;

public interface PaymentService {
    boolean processPayment(Order order, PaymentStrategy strategy);
    List<Payment> getAllPayments();
}