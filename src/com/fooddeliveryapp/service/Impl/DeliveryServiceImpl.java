package com.fooddeliveryapp.service.Impl;

import com.fooddeliveryapp.model.DeliveryAgent;
import com.fooddeliveryapp.model.Order;
import com.fooddeliveryapp.repository.DeliveryAgentRepository;
import com.fooddeliveryapp.service.DeliveryService;
import com.fooddeliveryapp.service.OrderService;

import java.util.*;
import java.util.stream.Collectors;

public class DeliveryServiceImpl implements DeliveryService {

    private OrderService orderService;
    private final Queue<Order> pendingOrdersQueue = new LinkedList<>();
    private final DeliveryAgentRepository agentRepository;

    public DeliveryServiceImpl(DeliveryAgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    @Override
    public Optional<DeliveryAgent> assignOrder(Order order) {
        Optional<DeliveryAgent> agentOpt = agentRepository.findAll().stream()
                .filter(DeliveryAgent::isAvailable)
                .findFirst();

        if (agentOpt.isEmpty()) {
            pendingOrdersQueue.offer(order);
            return Optional.empty();
        }

        DeliveryAgent agent = agentOpt.get();

        if (orderService != null) {
            orderService.assignDeliveryAgent(order.getOrderNumber(), agent);
        } else {
            agent.setAvailable(false);
            agentRepository.save(agent);
            order.setAssignedAgentId(agent.getId());
        }

        return Optional.of(agent);
    }

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void completeDelivery(Order order) {
        // OrderServiceImpl already updates the agent's availability and total deliveries!
        // We only need to check if there are pending orders waiting for this newly freed agent.
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