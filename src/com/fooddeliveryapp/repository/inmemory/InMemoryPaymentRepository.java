package com.fooddeliveryapp.repository.inmemory;

import com.fooddeliveryapp.model.Payment;
import com.fooddeliveryapp.repository.PaymentRepository;

import java.util.*;

public class InMemoryPaymentRepository implements PaymentRepository {

    private final Map<String, Payment> payments = new HashMap<>();

    @Override
    public Payment save(Payment payment) {
        payments.put(payment.getPaymentId(), payment);
        return payment;
    }

    @Override
    public Optional<Payment> findById(String paymentId) {
        return Optional.ofNullable(payments.get(paymentId));
    }

    @Override
    public Optional<Payment> findByOrderNumber(String orderNumber) {

        return payments.values()
                .stream()
                .filter(p -> p.getOrderNumber().equals(orderNumber))
                .findFirst();
    }

    @Override
    public List<Payment> findAll() {
        return new ArrayList<>(payments.values());
    }
}