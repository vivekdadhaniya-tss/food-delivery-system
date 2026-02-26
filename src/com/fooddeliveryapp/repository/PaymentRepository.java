package com.fooddeliveryapp.repository;

import com.fooddeliveryapp.model.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findById(String paymentId);

    Optional<Payment> findByOrderNumber(String orderNumber);

    List<Payment> findAll();
}