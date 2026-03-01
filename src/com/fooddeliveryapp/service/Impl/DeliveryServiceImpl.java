package com.fooddeliveryapp.service.Impl;

import com.fooddeliveryapp.exception.FoodDeliveryException;
import com.fooddeliveryapp.model.DeliveryAgent;
import com.fooddeliveryapp.model.Order;
import com.fooddeliveryapp.repository.DeliveryAgentRepository;
import com.fooddeliveryapp.repository.UserRepository;
import com.fooddeliveryapp.service.DeliveryService;
import com.fooddeliveryapp.service.OrderService;
import com.fooddeliveryapp.type.ErrorType;
import com.fooddeliveryapp.type.OrderStatus;

import java.util.Comparator;
import java.util.List;
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
            userRepository.save(agent);
            orderService.assignDeliveryAgent(orderId, agent.getId());
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

        // 1. Free up the agent who just delivered the order
        String agentId = orderService.getOrderById(orderId).get().getDeliveryAgentId();
        if (agentId != null) {
            DeliveryAgent agent = deliveryAgentRepository.findById(agentId).orElseThrow();
            agent.markAvailable();
            agent.incrementDeliveries();
            userRepository.save(agent);
        }

        // 2. MAJOR FIX: Instantly check if there are pending orders waiting for an agent!
        autoAssignPendingOrders();
    }

    @Override
    public void rateDeliveryAgent(String agentId, double rating) {
        DeliveryAgent agent = deliveryAgentRepository.findById(agentId)
                .orElseThrow(() -> new FoodDeliveryException(ErrorType.RESOURCE_NOT_FOUND, "Agent not found"));
        agent.addRating(rating);
        userRepository.save(agent);
    }

    // --- The Queue Logic ---
    private void autoAssignPendingOrders() {
        // Find all PAID orders that don't have an agent yet, sorted by oldest first
        List<Order> pendingOrders = orderService.getOngoingOrders().stream()
                .filter(o -> o.getStatus() == OrderStatus.PAID && o.getDeliveryAgentId() == null)
                .sorted(Comparator.comparing(Order::getCreatedAt))
                .toList();

        for (Order order : pendingOrders) {
            Optional<DeliveryAgent> agentOpt = deliveryAgentRepository.findAvailableAgents().stream().findFirst();
            if (agentOpt.isPresent()) {
                DeliveryAgent agent = agentOpt.get();
                agent.markBusy();
                userRepository.save(agent);
                orderService.assignDeliveryAgent(order.getOrderNumber(), agent.getId());
                System.out.println("🔄 [SYSTEM] Auto-assigned pending order " + order.getOrderNumber() + " to Agent " + agent.getName());
            } else {
                break; // No more agents available, stop trying
            }
        }
    }
}