package com.fooddeliveryapp.service;

import com.fooddeliveryapp.model.DeliveryAgent;

import java.util.Optional;

public interface DeliveryService {

    // Auto-finds an available agent, marks them as busy, and links them to the Order
    // Returns the assigned agent, or Optional.empty() if all agents are currently busy
    Optional<DeliveryAgent> assignAgentToOrder(String orderId);

    // Updates the order status to OUT_FOR_DELIVERY
    void markOrderOutForDelivery(String orderId);

    // Updates the order status to DELIVERED, increments agent's total deliveries,
    // and marks the agent as available again
    void markOrderDelivered(String orderId);

    // Allows a customer to rate the agent after delivery
    void rateDeliveryAgent(String agentId, double rating);
}