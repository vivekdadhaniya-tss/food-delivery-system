package com.fooddeliveryapp.repository;


import com.fooddeliveryapp.model.Order;
import com.fooddeliveryapp.type.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findAll();
    List<Order> findAllByStatus(OrderStatus status);
    List<Order> findByCustomerId(String customerId);
    List<Order> findOngoingOrders(); // CREATED, PAID, ASSIGNED, OUT_FOR_DELIVERY
    List<Order> findByDeliveryAgentId(String agentId);
    Optional<Order> findByPaymentId(String paymentId);

    boolean existsByOrderNumber(String orderNumber);

    void update(Order order); // future proof
}