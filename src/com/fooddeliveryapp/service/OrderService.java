package com.fooddeliveryapp.service;

import com.fooddeliveryapp.model.Order;
import com.fooddeliveryapp.type.OrderStatus;
import com.fooddeliveryapp.strategy.Impl.PaymentStrategy;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    // order creation
    Order placeOrder(String customerId, PaymentStrategy paymentStrategy);

    // order status
    void assignDeliveryAgent(String orderNumber, String agentId);
    void markOrderOutForDelivery(String orderNumber);
    void markOrderDelivered(String orderNumber);
    void cancelOrder(String orderNumber);

    // retrieval
    Optional<Order> getOrderById(String orderNumber);
    List<Order> getOrdersByCustomer(String customerId);
    List<Order> getOrdersByDeliveryAgent(String agentId);
    List<Order> getOrdersByStatus(OrderStatus status);
    List<Order> getOngoingOrders(); // Returns CREATED, PAID, ASSIGNED, OUT_FOR_DELIVERY
    List<Order> getAllOrders();
}