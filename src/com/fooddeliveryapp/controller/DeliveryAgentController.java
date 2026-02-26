package com.fooddeliveryapp.controller;

import com.fooddeliveryapp.exception.FoodDeliveryException;
import com.fooddeliveryapp.model.DeliveryAgent;
import com.fooddeliveryapp.model.Order;
import com.fooddeliveryapp.model.User;
import com.fooddeliveryapp.service.DeliveryService;
import com.fooddeliveryapp.service.OrderService;
import com.fooddeliveryapp.util.InputUtil;

import java.util.List;
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
            System.out.println("1. View Assigned Orders");
            System.out.println("2. Mark Order Out for Delivery");
            System.out.println("3. Mark Order Delivered");
            System.out.println("4. View Profile & Stats");
            System.out.println("5. Logout");

            int choice = InputUtil.getInt("Choose an option: ");

            try {
                switch (choice) {
                    case 1 -> viewAssignedOrders(agent);
                    case 2 -> markOutForDelivery(agent);
                    case 3 -> markDelivered(agent);
                    case 4 -> viewProfile(agent);
                    case 5 -> {
                        System.out.println("Logging out...");
                        return;
                    }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (FoodDeliveryException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("System Error: Something went wrong!");
                e.printStackTrace();
            }
        }
    }

    private void viewAssignedOrders(DeliveryAgent agent) {
        List<Order> orders = orderService.getOrdersByDeliveryAgent(agent.getId());
        if (orders.isEmpty()) {
            System.out.println("No assigned orders.");
        } else {
            System.out.println("\n--- Assigned Orders ---");
            orders.forEach(o -> System.out.println("Order No: " + o.getOrderNumber() + " | Status: " + o.getStatus() + " | Amount: ₹" + o.getFinalAmount()));
        }
    }

    private void markOutForDelivery(DeliveryAgent agent) {
        String orderNumber = InputUtil.getString("Enter Order Number to mark Out for Delivery: ");
        try {
            orderService.markOrderOutForDelivery(orderNumber);
            System.out.println("Order marked as Out for Delivery.");
        } catch (Exception e) {
            System.out.println("Failed to update order: " + e.getMessage());
        }
    }

    private void markDelivered(DeliveryAgent agent) {
        String orderNumber = InputUtil.getString("Enter Order Number to mark Delivered: ");
        try {
            orderService.markOrderDelivered(orderNumber);
            System.out.println("Order marked as Delivered.");
        } catch (Exception e) {
            System.out.println("Failed to update order: " + e.getMessage());
        }
    }

    private void viewProfile(DeliveryAgent agent) {
        System.out.println("\n--- Profile & Stats ---");
        System.out.println("Name: " + agent.getName());
        System.out.println("Available: " + (agent.isAvailable() ? "Yes" : "No"));
        System.out.println("Total Deliveries: " + agent.getTotalDeliveries());
        System.out.println("Rating: " + agent.getRating());
    }
}
