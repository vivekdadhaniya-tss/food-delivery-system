package com.fooddeliveryapp.service.Impl;

import com.fooddeliveryapp.model.Order;
import com.fooddeliveryapp.repository.PaymentRepository;
import com.fooddeliveryapp.service.PaymentService;
import com.fooddeliveryapp.strategy.Impl.PaymentStrategy;

public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public boolean processPayment(Order order, PaymentStrategy strategy) {

        if (order.getFinalAmount() <= 0) {
            throw new IllegalStateException("Order amount must be greater than zero");
        }

        // Process payment using strategy
        strategy.pay(order.getFinalAmount());

        // Record payment in repository
        // paymentRepository.save(order.getOrderNumber(), strategy.getPaymentType(), order.getFinalAmount());   AFTER CHECK

        return true;
    }
}