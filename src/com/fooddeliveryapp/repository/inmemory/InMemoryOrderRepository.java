package com.fooddeliveryapp.repository.inmemory;

import com.fooddeliveryapp.model.Order;
import com.fooddeliveryapp.repository.OrderRepository;
import com.fooddeliveryapp.type.OrderStatus;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryOrderRepository implements OrderRepository {

    private final Map<String, Order> store = new HashMap<>();        // orderNumber -> Order
    private final Map<String, String> paymentIndex = new HashMap<>(); // paymentId -> orderNumber

    @Override
    public Order save(Order order) {
        if (order == null || order.getOrderNumber() == null || order.getOrderNumber().isBlank()) {
            throw new IllegalArgumentException("Order and OrderNumber cannot be null/blank");
        }

        store.put(order.getOrderNumber(), order);

        if (order.getPaymentId() != null && !order.getPaymentId().isBlank()) {
            paymentIndex.put(order.getPaymentId(), order.getOrderNumber());
        }

        return order;
    }

    @Override
    public Optional<Order> findByOrderNumber(String orderNumber) {
        if (orderNumber == null || orderNumber.isBlank()) return Optional.empty();
        return Optional.ofNullable(store.get(orderNumber));
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<Order> findAllByStatus(OrderStatus status) {
        if (status == null) return List.of();
        return store.values().stream()
                .filter(order -> order.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findByCustomerId(String customerId) {
        if (customerId == null || customerId.isBlank()) return List.of();
        return store.values().stream()
                .filter(order -> customerId.equals(order.getCustomerId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findOngoingOrders() {
        return store.values().stream()
                .filter(order -> {
                    OrderStatus status = order.getStatus();
                    return status == OrderStatus.CREATED ||
                            status == OrderStatus.PAID ||
                            status == OrderStatus.ASSIGNED ||
                            status == OrderStatus.OUT_FOR_DELIVERY;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findByDeliveryAgentId(String agentId) {
        if (agentId == null || agentId.isBlank()) return List.of();
        return store.values().stream()
                .filter(order -> agentId.equals(order.getDeliveryAgentId()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Order> findByPaymentId(String paymentId) {
        if (paymentId == null || paymentId.isBlank()) return Optional.empty();
        String orderNumber = paymentIndex.get(paymentId);
        if (orderNumber == null) return Optional.empty();
        return Optional.ofNullable(store.get(orderNumber));
    }

    @Override
    public boolean existsByOrderNumber(String orderNumber) {
        if (orderNumber == null || orderNumber.isBlank()) return false;
        return store.containsKey(orderNumber);
    }

    @Override
    public void update(Order order) {
        if (order == null || order.getOrderNumber() == null || order.getOrderNumber().isBlank()) {
            throw new IllegalArgumentException("Invalid order for update");
        }

        if (!store.containsKey(order.getOrderNumber())) {
            throw new IllegalArgumentException("Order does not exist. Cannot update.");
        }

        // Remove old paymentIndex if paymentId changed
        Order old = store.get(order.getOrderNumber());
        String oldPaymentId = old.getPaymentId();
        if (oldPaymentId != null && !oldPaymentId.equals(order.getPaymentId())) {
            paymentIndex.remove(oldPaymentId);
        }

        store.put(order.getOrderNumber(), order);

        if (order.getPaymentId() != null && !order.getPaymentId().isBlank()) {
            paymentIndex.put(order.getPaymentId(), order.getOrderNumber());
        }
    }
}