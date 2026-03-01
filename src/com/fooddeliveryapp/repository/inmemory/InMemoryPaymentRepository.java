package com.fooddeliveryapp.repository.inmemory;

import com.fooddeliveryapp.model.Payment;
import com.fooddeliveryapp.repository.PaymentRepository;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryPaymentRepository implements PaymentRepository {

    private final Map<String, Payment> paymentStore = new HashMap<>();
    private final Map<String, List<String>> orderIndex = new HashMap<>(); // orderId -> list of paymentIds

    @Override
    public Payment save(Payment payment) {
        if (payment == null || payment.getPaymentId() == null || payment.getPaymentId().isBlank()) {
            throw new IllegalArgumentException("Payment or PaymentId cannot be null/blank");
        }

        paymentStore.put(payment.getPaymentId(), payment);

        // Update orderIndex
        if (payment.getOrderId() != null && !payment.getOrderId().isBlank()) {
            orderIndex.computeIfAbsent(payment.getOrderId(), key -> new ArrayList<>()) // here key -> orderId
                    .add(payment.getPaymentId());
        }

        return payment;
    }

    @Override
    public Optional<Payment> findById(String paymentId) {
        if (paymentId == null || paymentId.isBlank()) return Optional.empty();
        return Optional.ofNullable(paymentStore.get(paymentId));
    }

    @Override
    public List<Payment> findAll() {
        return new ArrayList<>(paymentStore.values());
    }

    @Override
    public List<Payment> findByOrderId(String orderId) {
        if (orderId == null || orderId.isBlank()) return List.of();
        List<String> paymentIds = orderIndex.get(orderId);
        if (paymentIds == null || paymentIds.isEmpty()) return List.of();

        return paymentIds.stream()
                .map(paymentStore::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void update(Payment payment) {
        if (payment == null || payment.getPaymentId() == null || payment.getPaymentId().isBlank()) {
            throw new IllegalArgumentException("Invalid payment for update");
        }

        if (!paymentStore.containsKey(payment.getPaymentId())) {
            throw new IllegalArgumentException("Payment does not exist. Cannot update.");
        }

        // Remove old orderIndex entry if orderId changed
        Payment old = paymentStore.get(payment.getPaymentId());
        String oldOrderId = old.getOrderId();
        if (oldOrderId != null && !oldOrderId.equals(payment.getOrderId())) {
            List<String> payments = orderIndex.get(oldOrderId);
            if (payments != null) {
                payments.remove(payment.getPaymentId());
                if (payments.isEmpty()) orderIndex.remove(oldOrderId);
            }
        }

        // Save updated payment
        paymentStore.put(payment.getPaymentId(), payment);

        // Add to new orderIndex if necessary
        if (payment.getOrderId() != null && !payment.getOrderId().isBlank()) {
            orderIndex.computeIfAbsent(payment.getOrderId(), k -> new ArrayList<>())
                    .add(payment.getPaymentId());
        }
    }

    @Override
    public boolean existsById(String paymentId) {
        if (paymentId == null || paymentId.isBlank()) return false;
        return paymentStore.containsKey(paymentId);
    }
}