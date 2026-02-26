package com.fooddeliveryapp.service;

import com.fooddeliveryapp.model.DeliveryAgent;
import com.fooddeliveryapp.model.Order;

import java.util.List;
import java.util.Optional;

public interface DeliveryService {

    // Assigns an order to an available delivery agent.
    // Returns the assigned agent, or empty if none available.
    Optional<DeliveryAgent> assignOrder(Order order);

    // Marks the order as delivered by the agent and updates agent availability.
    void completeDelivery(Order order);

    List<DeliveryAgent> getAvailableAgents();

    List<DeliveryAgent> getAllAgents();
}