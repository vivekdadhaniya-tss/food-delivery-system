package com.fooddeliveryapp.service.Impl;

import com.fooddeliveryapp.exception.OrderProcessingException;
import com.fooddeliveryapp.exception.ResourceNotFoundException;
import com.fooddeliveryapp.model.DeliveryAgent;
import com.fooddeliveryapp.model.Order;
import com.fooddeliveryapp.repository.DeliveryAgentRepository;
import com.fooddeliveryapp.service.DeliveryService;
import com.fooddeliveryapp.service.OrderService;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;

public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryAgentRepository agentRepository;

    private OrderService orderService;

    // Queue of available agents for FIFO assignment
    private final Queue<DeliveryAgent> availableAgentsQueue = new ArrayBlockingQueue<>(100);
    private final Queue<Order> pendingOrdersQueue = new LinkedList<>();

    public DeliveryServiceImpl(DeliveryAgentRepository agentRepository) {
        this.agentRepository = agentRepository;

        agentRepository.findAll().stream()
                .filter(DeliveryAgent::isAvailable)
                .forEach(availableAgentsQueue::offer);
    }

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public Optional<DeliveryAgent> assignOrder(Order order) {

        DeliveryAgent agent = availableAgentsQueue.poll();

        if (agent == null) {
            // No available agents, add order to pending queue
            pendingOrdersQueue.offer(order);
            return Optional.empty();
        }

        // Automatically update the order status in the database
        if (orderService != null) {
            orderService.assignDeliveryAgent(order.getOrderNumber(), agent);
        } else {
            // Fallback just in case orderService isn't linked yet
            agent.setAvailable(false);
            agentRepository.save(agent);
            order.setAssignedAgentId(agent.getId());
        }

        return Optional.of(agent);
    }

    @Override
    public void completeDelivery(Order order) {

        Integer agentId = order.getAssignedAgentId();
        if (agentId == null)
            throw new OrderProcessingException("Order has no assigned agent");

        DeliveryAgent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new ResourceNotFoundException("Agent not found"));

        agent.setAvailable(true);
        agent.setTotalDeliveries(agent.getTotalDeliveries() + 1);
        agentRepository.save(agent);

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