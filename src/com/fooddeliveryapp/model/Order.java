package com.fooddeliveryapp.model;

import com.fooddeliveryapp.type.OrderStatus;
import com.fooddeliveryapp.util.AppConstants;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class Order {

    private final String orderNumber;
    private final String customerId;
    private final List<OrderItem> items;

    private double subTotal;
    private double discountAmount;
    private double taxAmount;
    private double deliveryFee;
    private double finalAmount;

    private String paymentId;
    private String deliveryAgentId;

    private OrderStatus status;

    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deliveredAt;

    public Order(String orderNumber, String customerId, List<OrderItem> items, double subTotal, double discountAmount) {

        this.orderNumber = orderNumber;
        this.customerId = customerId;
        this.items = List.copyOf(items); // immutable copy

        this.subTotal = subTotal;
        this.discountAmount = discountAmount;
        this.deliveryFee = AppConstants.DEFAULT_DELIVERY_FEE;

        calculateAmounts();

        this.status = OrderStatus.CREATED;

        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void calculateAmounts() {
        this.taxAmount = (subTotal * AppConstants.DEFAULT_TAX_PERCENT) / 100;
        this.finalAmount = subTotal - discountAmount + taxAmount + deliveryFee;
    }

    // getters
    public String getOrderNumber() {
        return orderNumber;
    }

    public String getCustomerId() {
        return customerId;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public double getSubTotal() {
        return subTotal;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public double getDeliveryFee() {
        return deliveryFee;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getDeliveryAgentId() {
        return deliveryAgentId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }


    // business logic
    public void assignDeliveryAgent(String agentId) {
        this.deliveryAgentId = agentId;
        updateStatus(OrderStatus.ASSIGNED);
    }

    public void attachPayment(String paymentId) {
        this.paymentId = paymentId;
        updateStatus(OrderStatus.PAID);
    }

    public void markOutForDelivery() {
        updateStatus(OrderStatus.OUT_FOR_DELIVERY);
    }

    public void markDelivered() {
        updateStatus(OrderStatus.DELIVERED);
        this.deliveredAt = LocalDateTime.now();
    }

    public void cancel() {
        updateStatus(OrderStatus.CANCELLED);
    }

    public void updateStatus(OrderStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }
}