package com.fooddeliveryapp.service;

import com.fooddeliveryapp.model.Order;
import com.fooddeliveryapp.model.type.OrderStatus;
import com.fooddeliveryapp.model.DeliveryAgent;
import com.fooddeliveryapp.strategy.Impl.PaymentStrategy;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    // order creation
    Order placeOrder(int customerId, PaymentStrategy paymentStrategy);

    // order status
    void assignDeliveryAgent(String orderNumber, DeliveryAgent agent);

    void markOrderOutForDelivery(String orderNumber);

    void markOrderDelivered(String orderNumber);

    void cancelOrder(String orderNumber);

    // retrieval
    Optional<Order> getOrderById(String orderNumber);

    List<Order> getOrdersByCustomer(int customerId);

    List<Order> getAllOrders();

    List<Order> getOrdersByStatus(OrderStatus status);

    List<Order> getOrdersByDeliveryAgent(int agentId);
}