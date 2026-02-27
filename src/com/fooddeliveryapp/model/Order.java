package com.fooddeliveryapp.model;

import com.fooddeliveryapp.model.type.OrderStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {

    private final String orderNumber;
    private final int customerId;
    private final int restaurantId;
    private final List<OrderItem> items;

    private double subTotal;
    private double discount;
    private double tax;
    private double deliveryFee;
    private double finalAmount;

    private OrderStatus status;
    private Integer assignedAgentId;

    private final LocalDateTime createdAt;
    private LocalDateTime deliveredAt;

    public Order(String orderNumber,
                 int customerId,
                 int restaurantId,
                 List<OrderItem> items) {

        this.orderNumber = orderNumber;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.items = new ArrayList<>(items);
        this.status = OrderStatus.CREATED;
        this.createdAt = LocalDateTime.now();
    }

    // getters

    public String getOrderNumber() { return orderNumber; }
    public int getCustomerId() { return customerId; }
    public int getRestaurantId() { return restaurantId; }
    public List<OrderItem> getItems() { return List.copyOf(items); }

    public double getSubTotal() { return subTotal; }
    public double getDiscount() { return discount; }
    public double getTax() { return tax; }
    public double getDeliveryFee() { return deliveryFee; }
    public double getFinalAmount() { return finalAmount; }

    public OrderStatus getStatus() { return status; }
    public Integer getAssignedAgentId() { return assignedAgentId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getDeliveredAt() { return deliveredAt; }

    // setters

    public void setSubTotal(double subTotal) { this.subTotal = subTotal; }
    public void setDiscount(double discount) { this.discount = discount; }
    public void setTax(double tax) { this.tax = tax; }
    public void setDeliveryFee(double deliveryFee) { this.deliveryFee = deliveryFee; }
    public void setFinalAmount(double finalAmount) { this.finalAmount = finalAmount; }

    public void setStatus(OrderStatus status) { this.status = status; }
    public void setAssignedAgentId(Integer assignedAgentId) {
        this.assignedAgentId = assignedAgentId;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }
}