package com.fooddeliveryapp.controller;

import com.fooddeliveryapp.exception.FoodDeliveryException;
import com.fooddeliveryapp.model.DeliveryAgent;
import com.fooddeliveryapp.model.User;
import com.fooddeliveryapp.service.DeliveryService;
import com.fooddeliveryapp.service.OrderService;
import com.fooddeliveryapp.util.InputUtil;

import java.util.Scanner;

public class DeliveryAgentController {
    private final OrderService orderService;
    private final DeliveryService deliveryService;

    public DeliveryAgentController(OrderService orderService, DeliveryService deliveryService) {
        this.orderService = orderService;
        this.deliveryService = deliveryService;
    }

    public void start(Scanner scan, User user) {
        DeliveryAgent agent = (DeliveryAgent) user;
        while (true) {
            System.out.println("\n=== DELIVERY AGENT DASHBOARD ===");
            System.out.println("1. View Profile & Earnings\n2. View Assigned Order\n3. Update Order Status\n4. Logout");

            int choice = InputUtil.getInt("Choose an option: ");

            try {
                switch (choice) {
                    case 1 -> viewProfile(agent);
                    case 2 -> viewAssignedOrders(agent);
                    case 3 -> updateOrderStatus(agent);
                    case 4 -> { System.out.println("Logging out..."); return; }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (FoodDeliveryException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void viewProfile(DeliveryAgent agent) {
        System.out.println("\n--- Profile & Earnings ---");
        System.out.println("Name: " + agent.getName());
        System.out.println("Total Deliveries: " + agent.getTotalDeliveries());
        System.out.println("Rating: " + agent.getRating());
        System.out.println("Estimated Earnings: ₹" + (agent.getTotalDeliveries() * 40));
    }

    private void viewAssignedOrders(DeliveryAgent agent) {
        orderService.getOrdersByDeliveryAgent(agent.getId()).forEach(o ->
                System.out.println("Order: " + o.getOrderNumber() + " | Status: " + o.getStatus() + " | Amount: ₹" + o.getFinalAmount() + " | Customer ID: " + o.getCustomerId())
        );
    }

    private void updateOrderStatus(DeliveryAgent agent) {
        String orderNumber = InputUtil.getString("Enter Order Number: ");
        System.out.println("1. Mark Out for Delivery | 2. Mark Delivered");
        int choice = InputUtil.getInt("Choose: ");
        if (choice == 1) {
            orderService.markOrderOutForDelivery(orderNumber);
            System.out.println("Order marked as Out for Delivery.");
        } else if (choice == 2) {
            orderService.markOrderDelivered(orderNumber);
            deliveryService.completeDelivery(orderService.getOrderById(orderNumber).get());
            System.out.println("Order marked as Delivered.");
        }
    }
}