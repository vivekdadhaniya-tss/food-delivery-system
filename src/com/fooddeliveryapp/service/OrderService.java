package com.fooddeliveryapp.service;

import com.fooddeliveryapp.model.Order;
import com.fooddeliveryapp.type.OrderStatus;
import com.fooddeliveryapp.model.DeliveryAgent;
import com.fooddeliveryapp.strategy.Impl.PaymentStrategy;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    // ===== ORDER CREATION =====
    Order placeOrder(int customerId, PaymentStrategy paymentStrategy);

    // ===== ORDER STATUS =====
    void assignDeliveryAgent(String orderNumber, DeliveryAgent agent);

    void markOrderOutForDelivery(String orderNumber);

    void markOrderDelivered(String orderNumber);

    void cancelOrder(String orderNumber);

    // ===== RETRIEVAL =====
    Optional<Order> getOrderById(String orderNumber);

    List<Order> getOrdersByCustomer(int customerId);

    List<Order> getAllOrders();

    List<Order> getOrdersByStatus(OrderStatus status);

    List<Order> getOrdersByDeliveryAgent(int agentId);
}