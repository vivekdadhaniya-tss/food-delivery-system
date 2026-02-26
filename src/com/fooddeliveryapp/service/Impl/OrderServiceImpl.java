package com.fooddeliveryapp.service.Impl;

import com.fooddeliveryapp.exception.CartOperationException;
import com.fooddeliveryapp.exception.OrderProcessingException;
import com.fooddeliveryapp.exception.ResourceNotFoundException;
import com.fooddeliveryapp.model.*;
import com.fooddeliveryapp.model.type.OrderStatus;
import com.fooddeliveryapp.repository.DeliveryAgentRepository;
import com.fooddeliveryapp.repository.OrderRepository;
import com.fooddeliveryapp.repository.UserRepository;
import com.fooddeliveryapp.strategy.Impl.DiscountStrategy;
import com.fooddeliveryapp.strategy.NoDiscount;
import com.fooddeliveryapp.strategy.Impl.PaymentStrategy;
import com.fooddeliveryapp.util.IdGenerator;
import com.fooddeliveryapp.service.OrderService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final DeliveryAgentRepository agentRepository;
    private DiscountStrategy discountStrategy = new NoDiscount();
    private final double deliveryFee = 40; // flat fee for simplicity
    private final double taxRate = 0.05;   // 5% tax

    public OrderServiceImpl(OrderRepository orderRepository,
                            UserRepository userRepository,
                            DeliveryAgentRepository agentRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.agentRepository = agentRepository;
    }

    // Admin can set discount strategy anytime
    public void setDiscountStrategy(DiscountStrategy discountStrategy) {
        if (discountStrategy == null) {
            this.discountStrategy = new NoDiscount(); // fallback
        } else {
            this.discountStrategy = discountStrategy;
        }
    }

    // ORDER CREATION
    @Override
    public Order placeOrder(int customerId, PaymentStrategy paymentStrategy) {
        if(paymentStrategy == null){
            throw new OrderProcessingException("Payment method must be provided");
        }

        Customer customer = (Customer) userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Cart cart = customer.getActiveCart();
        if (cart.isEmpty()) {
            throw new CartOperationException("Cart is empty");
        }

        String orderNumber = IdGenerator.nextOrderNumber();

        List<OrderItem> orderItems = cart.getItems();
        Order order = new Order(orderNumber, customerId,
                cart.getRestaurant().getId(),
                orderItems);


        double subTotal = cart.getSubTotal();
        double discount = discountStrategy.calculateDiscount(order);
        double tax = (subTotal - discount) * taxRate;
        double finalAmount = subTotal - discount + tax + deliveryFee;

        order.setSubTotal(subTotal);
        order.setDiscount(discount);
        order.setTax(tax);
        order.setDeliveryFee(deliveryFee);
        order.setFinalAmount(finalAmount);
        order.setStatus(OrderStatus.PLACED);

        orderRepository.save(order);
        cart.clear();

        // Dynamic payment per order
        paymentStrategy.pay(finalAmount);

        return order;
    }

    // DELIVERY AGENT ASSIGNMENT
    @Override
    public void assignDeliveryAgent(String orderNumber, DeliveryAgent agent) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!agent.isAvailable()) {
            throw new OrderProcessingException("Agent not available");
        }

        agent.setAvailable(false);
        order.setAssignedAgentId(agent.getId());
        order.setStatus(OrderStatus.ASSIGNED);

        agentRepository.save(agent);
        orderRepository.save(order);
    }

    @Override
    public void markOrderOutForDelivery(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
        orderRepository.save(order);
    }

    @Override
    public void markOrderDelivered(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.setStatus(OrderStatus.DELIVERED);
        order.setDeliveredAt(LocalDateTime.now());

        if (order.getAssignedAgentId() != null) {
            DeliveryAgent agent = agentRepository.findById(order.getAssignedAgentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Agent not found"));
            agent.setTotalDeliveries(agent.getTotalDeliveries() + 1);
            agent.setAvailable(true);
            agentRepository.save(agent);
        }

        orderRepository.save(order);
    }

    @Override
    public void cancelOrder(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.setStatus(OrderStatus.CANCELLED);

        // Free the agent if assigned
        if (order.getAssignedAgentId() != null) {
            DeliveryAgent agent = agentRepository.findById(order.getAssignedAgentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Agent not found"));
            agent.setAvailable(true);
            agentRepository.save(agent);
        }

        orderRepository.save(order);
    }

    // RETRIEVAL METHODS
    @Override
    public Optional<Order> getOrderById(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }

    @Override
    public List<Order> getOrdersByCustomer(int customerId) {
        return orderRepository.findAll().stream()
                .filter(o -> o.getCustomerId() == customerId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findAll().stream()
                .filter(o -> o.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> getOrdersByDeliveryAgent(int agentId) {
        return orderRepository.findAll().stream()
                .filter(o -> o.getAssignedAgentId() != null && o.getAssignedAgentId() == agentId)
                .collect(Collectors.toList());
    }
}