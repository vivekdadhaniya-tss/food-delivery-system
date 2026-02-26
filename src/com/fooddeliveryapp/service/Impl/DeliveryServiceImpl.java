package com.fooddeliveryapp.service.Impl;

import com.fooddeliveryapp.model.DeliveryAgent;
import com.fooddeliveryapp.model.Order;
import com.fooddeliveryapp.repository.DeliveryAgentRepository;
import com.fooddeliveryapp.service.DeliveryService;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;

public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryAgentRepository agentRepository;

    // Queue of available agents for FIFO assignment
    private final Queue<DeliveryAgent> availableAgentsQueue = new ArrayBlockingQueue<>(100);
//    private final Queue<DeliveryAgent> availableAgentsQueue = new LinkedList<>();

    private final Queue<Order> pendingOrdersQueue = new LinkedList<>(); // currently work

    public DeliveryServiceImpl(DeliveryAgentRepository agentRepository) {
        this.agentRepository = agentRepository;

        // Initialize queue with available agents at startup
        agentRepository.findAll().stream()
                .filter(DeliveryAgent::isAvailable)
                .forEach(availableAgentsQueue::offer);
    }

    @Override
    public Optional<DeliveryAgent> assignOrder(Order order) {

        DeliveryAgent agent = availableAgentsQueue.poll();

        if (agent == null) {
            // No available agents, add order to pending queue
            pendingOrdersQueue.offer(order);
            return Optional.empty();
        }

        // Assign order
        agent.setAvailable(false);
        agentRepository.save(agent);
        order.setAssignedAgentId(agent.getId());
        return Optional.of(agent);
    }

    @Override
    public void completeDelivery(Order order) {

        Integer agentId = order.getAssignedAgentId();
        if (agentId == null) throw new IllegalStateException("Order has no assigned agent");

        DeliveryAgent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new NoSuchElementException("Agent not found"));

        // Mark agent available again
        agent.setAvailable(true);
        agent.setTotalDeliveries(agent.getTotalDeliveries() + 1);
        agentRepository.save(agent);

        // Add agent back to available queue
        availableAgentsQueue.offer(agent);

        // Check if there are pending orders
        Order pendingOrder = pendingOrdersQueue.poll();
        if (pendingOrder != null) {
            assignOrder(pendingOrder);
        }
    }

    @Override
    public List<DeliveryAgent> getAvailableAgents() {
        return agentRepository.findAll().stream()
                .filter(DeliveryAgent::isAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeliveryAgent> getAllAgents() {
        return agentRepository.findAll();
    }
}