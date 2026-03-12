package com.fooddeliveryapp.service.Impl;

import com.fooddeliveryapp.config.SystemConfig;
import com.fooddeliveryapp.exception.FoodDeliveryException;
import com.fooddeliveryapp.model.Cart;
import com.fooddeliveryapp.model.Order;
import com.fooddeliveryapp.model.OrderItem;
import com.fooddeliveryapp.model.Payment;
import com.fooddeliveryapp.repository.OrderRepository;
import com.fooddeliveryapp.service.CartService;
import com.fooddeliveryapp.service.OrderService;
import com.fooddeliveryapp.service.PaymentService;
import com.fooddeliveryapp.strategy.Impl.PaymentStrategy;
import com.fooddeliveryapp.type.ErrorType;
import com.fooddeliveryapp.type.IdType;
import com.fooddeliveryapp.type.OrderStatus;
import com.fooddeliveryapp.util.IdGenerator;

import java.util.List;
import java.util.Optional;

public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final PaymentService paymentService;

    public OrderServiceImpl(OrderRepository orderRepository, CartService cartService, PaymentService paymentService) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.paymentService = paymentService;
    }

    @Override
    public Order placeOrder(String customerId, PaymentStrategy paymentStrategy) {
        Cart cart = cartService.getCart(customerId);
        if (cart.isEmpty()) {
            throw new FoodDeliveryException(ErrorType.ORDER_ERROR, "Cannot place order with an empty cart");
        }

        List<OrderItem> orderItems = cart.getItems().stream()
                .map(ci -> new OrderItem(ci.getMenuItemId(), ci.getMenuItemName(), ci.getPriceAtAddTime(), ci.getQuantity()))
                .toList();

        SystemConfig config = SystemConfig.getInstance();
        double subTotal = cart.getSubTotal();

        // Temp order to calculate discount
        Order tempOrder = new Order("TEMP", customerId, orderItems, subTotal, 0, 0);
        double discount = config.getDiscountStrategy().calculateDiscount(tempOrder);

        // Create actual order
        Order order = new Order(
                IdGenerator.generate(IdType.ORDER),
                customerId,
                orderItems,
                subTotal,
                discount,
                config.getDeliveryFee()
        );

        // Process Payment
        Payment payment = paymentService.processPayment(order, paymentStrategy);
        order.attachPayment(payment.getPaymentId());

        orderRepository.save(order);
        cartService.clearCart(customerId);

        return order;
    }

    @Override
    public void assignDeliveryAgent(String orderNumber, String agentId) {
        Order order = getOrderOrThrow(orderNumber);
        order.assignDeliveryAgent(agentId);
        orderRepository.update(order);
    }

    @Override
    public void markOrderOutForDelivery(String orderNumber) {
        Order order = getOrderOrThrow(orderNumber);
        order.markOutForDelivery();
        orderRepository.update(order);
    }

    @Override
    public void markOrderDelivered(String orderNumber) {
        Order order = getOrderOrThrow(orderNumber);
        order.markDelivered();
        orderRepository.update(order);
    }

    @Override
    public void cancelOrder(String orderNumber) {
        Order order = getOrderOrThrow(orderNumber);
        order.cancel();
        orderRepository.update(order);
    }

    @Override
    public Optional<Order> getOrderById(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }

    @Override
    public List<Order> getOrdersByCustomer(String customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    @Override
    public List<Order> getOrdersByDeliveryAgent(String agentId) {
        return orderRepository.findByDeliveryAgentId(agentId);
    }

    @Override
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findAllByStatus(status);
    }

    @Override
    public List<Order> getOngoingOrders() {
        return orderRepository.findOngoingOrders();
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    private Order getOrderOrThrow(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new FoodDeliveryException(ErrorType.RESOURCE_NOT_FOUND, "Order not found"));
    }
}