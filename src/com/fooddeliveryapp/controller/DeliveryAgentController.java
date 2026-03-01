package com.fooddeliveryapp.controller;

import com.fooddeliveryapp.exception.FoodDeliveryException;
import com.fooddeliveryapp.model.DeliveryAgent;
import com.fooddeliveryapp.model.User;
import com.fooddeliveryapp.service.DeliveryService;
import com.fooddeliveryapp.service.OrderService;
import com.fooddeliveryapp.util.ConsoleInput;
import com.fooddeliveryapp.util.FormatUtil;
import com.fooddeliveryapp.util.TablePrinter;

import java.util.List;
import java.util.stream.Collectors;

public class DeliveryAgentController {
    private final OrderService orderService;
    private final DeliveryService deliveryService;

    public DeliveryAgentController(OrderService orderService, DeliveryService deliveryService) {
        this.orderService = orderService;
        this.deliveryService = deliveryService;
    }

    public void start(User user) {
        DeliveryAgent agent = (DeliveryAgent) user;
        while (true) {
            System.out.println("\n=======================================");
            System.out.println("     🛵 DELIVERY AGENT DASHBOARD 🛵    ");
            System.out.println("=======================================");
            System.out.println("1. View Profile & Earnings");
            System.out.println("2. View Assigned Orders");
            System.out.println("3. Update Order Status");
            System.out.println("4. Logout");
            System.out.println("=======================================");

            int choice = ConsoleInput.getInt("Select an option: ");

            try {
                switch (choice) {
                    case 1 -> viewProfile(agent);
                    case 2 -> viewAssignedOrders(agent);
                    case 3 -> updateOrderStatus(agent);
                    case 4 -> {
                        System.out.println("Logging out...");
                        return;
                    }
                    default -> System.out.println("❌ Invalid choice.");
                }
            } catch (FoodDeliveryException | IllegalArgumentException e) {
                System.out.println("❌ Error: " + e.getMessage());
            }
        }
    }

    private void viewProfile(DeliveryAgent agent) {
        System.out.println("\n--- Profile ---");
        System.out.println("Name: " + agent.getName());
        System.out.println("Status: " + (agent.isAvailable() ? "🟢 Available" : "🔴 Busy"));
        System.out.println("Rating: ⭐ " + String.format("%.1f", agent.getRating()));
        System.out.println("Total Deliveries: " + agent.getTotalDeliveries());
        // Assuming agent gets the delivery fee as earnings
        double earnings = agent.getTotalDeliveries() * com.fooddeliveryapp.config.SystemConfig.getInstance().getDeliveryFee();
        System.out.println("Estimated Earnings: " + FormatUtil.formatCurrency(earnings));
    }

    private void viewAssignedOrders(DeliveryAgent agent) {
        List<String[]> rows = orderService.getOrdersByDeliveryAgent(agent.getId()).stream()
                .map(o -> new String[]{o.getOrderNumber(), o.getCustomerId(), o.getStatus().name(), FormatUtil.formatCurrency(o.getFinalAmount())})
                .collect(Collectors.toList());
        TablePrinter.print(new String[]{"Order No", "Customer ID", "Status", "Amount"}, rows);
    }

    private void updateOrderStatus(DeliveryAgent agent) {
        String orderId = ConsoleInput.getString("Enter Order No: ");
        System.out.println("1. Mark Out for Delivery \n 2. Mark Delivered");
        int choice = ConsoleInput.getInt("Choose: ");

        if (choice == 1) {
            deliveryService.markOrderOutForDelivery(orderId);
            System.out.println("✅ Order is out for delivery!");
        } else if (choice == 2) {
            deliveryService.markOrderDelivered(orderId);
            System.out.println("✅ Order delivered successfully! You are now available for new orders.");
        }
    }
}