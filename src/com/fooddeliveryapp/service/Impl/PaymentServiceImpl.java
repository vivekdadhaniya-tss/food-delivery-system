package com.fooddeliveryapp.service.Impl;

import com.fooddeliveryapp.exception.OrderProcessingException;
import com.fooddeliveryapp.model.Order;
import com.fooddeliveryapp.model.Payment;
import com.fooddeliveryapp.model.type.PaymentMode;
import com.fooddeliveryapp.repository.PaymentRepository;
import com.fooddeliveryapp.service.PaymentService;
import com.fooddeliveryapp.strategy.Impl.PaymentStrategy;
import com.fooddeliveryapp.util.IdGenerator;
import java.util.List;

public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public boolean processPayment(Order order, PaymentStrategy strategy) {
        if (order.getFinalAmount() <= 0) {
            throw new OrderProcessingException("Order amount must be greater than zero");
        }
        strategy.pay(order.getFinalAmount());

        Payment payment = new Payment(
                IdGenerator.nextPaymentId(),
                order.getOrderNumber(),
                order.getFinalAmount(),
                PaymentMode.valueOf(strategy.getPaymentType())
        );
        payment.markSuccess();
        paymentRepository.save(payment);
        return true;
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
}