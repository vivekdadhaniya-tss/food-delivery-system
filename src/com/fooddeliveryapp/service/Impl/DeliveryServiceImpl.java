package com.fooddeliveryapp.service.Impl;

import com.fooddeliveryapp.exception.FoodDeliveryException;
import com.fooddeliveryapp.model.DeliveryAgent;
import com.fooddeliveryapp.repository.DeliveryAgentRepository;
import com.fooddeliveryapp.repository.UserRepository;
import com.fooddeliveryapp.service.DeliveryService;
import com.fooddeliveryapp.service.OrderService;
import com.fooddeliveryapp.type.ErrorType;

import java.util.Optional;

public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryAgentRepository deliveryAgentRepository;
    private final UserRepository userRepository;
    private final OrderService orderService;

    public DeliveryServiceImpl(DeliveryAgentRepository deliveryAgentRepository, UserRepository userRepository, OrderService orderService) {
        this.deliveryAgentRepository = deliveryAgentRepository;
        this.userRepository = userRepository;
        this.orderService = orderService;
    }

    @Override
    public Optional<DeliveryAgent> assignAgentToOrder(String orderId) {
        Optional<DeliveryAgent> agentOpt = deliveryAgentRepository.findAvailableAgents().stream().findFirst();

        if (agentOpt.isPresent()) {
            DeliveryAgent agent = agentOpt.get();
            agent.markBusy();
            userRepository.save(agent); // Save agent state

            orderService.assignDeliveryAgent(orderId, agent.getId()); // Update order
        }
        return agentOpt;
    }

    @Override
    public void markOrderOutForDelivery(String orderId) {
        orderService.markOrderOutForDelivery(orderId);
    }

    @Override
    public void markOrderDelivered(String orderId) {
        orderService.markOrderDelivered(orderId);

        // Free up the agent
        String agentId = orderService.getOrderById(orderId).get().getDeliveryAgentId();
        if (agentId != null) {
            DeliveryAgent agent = deliveryAgentRepository.findById(agentId).orElseThrow();
            agent.markAvailable();
            agent.incrementDeliveries();
            userRepository.save(agent);
        }
    }

    @Override
    public void rateDeliveryAgent(String agentId, double rating) {
        DeliveryAgent agent = deliveryAgentRepository.findById(agentId)
                .orElseThrow(() -> new FoodDeliveryException(ErrorType.RESOURCE_NOT_FOUND, "Agent not found"));

        agent.addRating(rating);
        userRepository.save(agent);
    }
}