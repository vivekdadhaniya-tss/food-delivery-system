package com.fooddeliveryapp.controller;

import com.fooddeliveryapp.model.*;
import com.fooddeliveryapp.model.type.FoodCategory;
import com.fooddeliveryapp.service.OrderService;
import com.fooddeliveryapp.service.RestaurantService;
import com.fooddeliveryapp.service.UserService;
import com.fooddeliveryapp.service.Impl.OrderServiceImpl;
import com.fooddeliveryapp.strategy.FlatDiscount;
import com.fooddeliveryapp.strategy.NoDiscount;
import com.fooddeliveryapp.strategy.PercentageDiscount;
import com.fooddeliveryapp.util.InputUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.ArrayList;

public class AdminController {

    private final RestaurantService restaurantService;
    private final UserService userService;
    private final OrderService orderService;

    public AdminController(RestaurantService restaurantService, UserService userService, OrderService orderService) {
        this.restaurantService = restaurantService;
        this.userService = userService;
        this.orderService = orderService;
    }

    public void start(Scanner scan, User user) {
        while (true) {
            System.out.println("\n=== ADMIN DASHBOARD ===");
            System.out.println("1. Manage Restaurants");
            System.out.println("2. Manage Menu Items");
            System.out.println("3. View All Users");
            System.out.println("4. View All Orders");
            System.out.println("5. Assign Delivery Agent to Order");
            System.out.println("6. Set Discount Strategy");
            System.out.println("7. Logout");

            int choice = InputUtil.getInt("Choose an option: ");

            try {
                switch (choice) {
                    case 1 -> manageRestaurants();
                    case 2 -> manageMenuItems();
                    case 3 -> viewAllUsers();
                    case 4 -> viewAllOrders();
                    case 5 -> assignDeliveryAgent();
                    case 6 -> setDiscountStrategy();
                    case 7 -> {
                        System.out.println("Logging out...");
                        return;
                    }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void manageRestaurants() {
        System.out.println("\n--- Manage Restaurants ---");
        System.out.println("1. Add Restaurant");
        System.out.println("2. View All Restaurants");
        System.out.println("3. Remove Restaurant");
        int choice = InputUtil.getInt("Choose: ");

        if (choice == 1) {
            String name = InputUtil.getString("Restaurant Name: ");
            Restaurant restaurant = new Restaurant(com.fooddeliveryapp.util.IdGenerator.nextRestaurantId(), name, true, new HashMap<>(), new ArrayList<>(), 0);
            restaurantService.addRestaurant(restaurant);
            System.out.println("Restaurant added successfully with ID: " + restaurant.getId());
        } else if (choice == 2) {
            List<Restaurant> restaurants = restaurantService.getAllRestaurants();
            if (restaurants.isEmpty()) {
                System.out.println("No restaurants found.");
            } else {
                restaurants.forEach(r -> System.out.println("ID: " + r.getId() + " | Name: " + r.getName() + " | Active: " + r.isActive()));
            }
        } else if (choice == 3) {
            int id = InputUtil.getInt("Restaurant ID to remove: ");
            restaurantService.removeRestaurant(id);
            System.out.println("Restaurant removed.");
        }
    }

    private void manageMenuItems() {
        System.out.println("\n--- Manage Menu Items ---");
        int restaurantId = InputUtil.getInt("Enter Restaurant ID: ");
        if (restaurantService.getRestaurantById(restaurantId).isEmpty()) {
            System.out.println("Restaurant not found.");
            return;
        }

        System.out.println("1. Add Menu Item");
        System.out.println("2. View Menu");
        System.out.println("3. Remove Menu Item");
        int choice = InputUtil.getInt("Choose: ");

        if (choice == 1) {
            String id = InputUtil.getString("Food Item ID (e.g., F101): ");
            String name = InputUtil.getString("Food Name: ");
            double price = InputUtil.getDouble("Price: ");
            int stock = InputUtil.getInt("Stock: ");
            System.out.println("Categories: 1.VEG 2.NON_VEG 3.DRINKS 4.DESSERT");
            int catChoice = InputUtil.getInt("Choose Category (1-4): ");
            FoodCategory category = FoodCategory.values()[catChoice - 1];

            FoodItem item = new FoodItem(id, name, price, stock, category);
            restaurantService.addMenuItem(restaurantId, item);
            System.out.println("Menu item added.");
        } else if (choice == 2) {
            List<FoodItem> menu = restaurantService.getMenu(restaurantId);
            if (menu.isEmpty()) {
                System.out.println("Menu is empty.");
            } else {
                menu.forEach(item -> System.out.println("ID: " + item.getId() + " | Name: " + item.getName() + " | Price: ₹" + item.getPrice() + " | Stock: " + item.getStock() + " | Category: " + item.getCategory()));
            }
        } else if (choice == 3) {
            String id = InputUtil.getString("Food Item ID to remove: ");
            restaurantService.removeMenuItem(restaurantId, id);
            System.out.println("Menu item removed.");
        }
    }

    private void viewAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users found.");
        } else {
            System.out.println("\n--- All Users ---");
            users.forEach(u -> System.out.println("ID: " + u.getId() + " | Name: " + u.getName() + " | Role: " + u.getRole() + " | Email: " + u.getEmail()));
        }
    }

    private void viewAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        if (orders.isEmpty()) {
            System.out.println("No orders found.");
        } else {
            System.out.println("\n--- All Orders ---");
            orders.forEach(o -> System.out.println("Order No: " + o.getOrderNumber() + " | Status: " + o.getStatus() + " | Amount: ₹" + o.getFinalAmount() + " | Agent ID: " + (o.getAssignedAgentId() != null ? o.getAssignedAgentId() : "Unassigned")));
        }
    }

    private void assignDeliveryAgent() {
        System.out.println("\n--- Assign Delivery Agent ---");
        String orderNumber = InputUtil.getString("Enter Order Number: ");
        int agentId = InputUtil.getInt("Enter Delivery Agent ID: ");

        Optional<User> agentOpt = userService.getUserById(agentId);
        if (agentOpt.isPresent() && agentOpt.get() instanceof DeliveryAgent) {
            try {
                orderService.assignDeliveryAgent(orderNumber, (DeliveryAgent) agentOpt.get());
                System.out.println("Agent assigned successfully.");
            } catch (Exception e) {
                System.out.println("Failed to assign agent: " + e.getMessage());
            }
        } else {
            System.out.println("Invalid Delivery Agent ID or User is not a Delivery Agent.");
        }
    }

    private void setDiscountStrategy() {
        System.out.println("\n--- Set Discount Strategy ---");
        System.out.println("1. No Discount");
        System.out.println("2. Flat Discount");
        System.out.println("3. Percentage Discount");
        int choice = InputUtil.getInt("Choose: ");

        if (orderService instanceof OrderServiceImpl) {
            OrderServiceImpl impl = (OrderServiceImpl) orderService;
            if (choice == 1) {
                impl.setDiscountStrategy(new NoDiscount());
                System.out.println("Strategy set to No Discount.");
            } else if (choice == 2) {
                double threshold = InputUtil.getDouble("Minimum Order Amount: ");
                double discount = InputUtil.getDouble("Flat Discount Amount: ");
                impl.setDiscountStrategy(new FlatDiscount(threshold, discount));
                System.out.println("Strategy set to Flat Discount.");
            } else if (choice == 3) {
                double threshold = InputUtil.getDouble("Minimum Order Amount: ");
                double percentage = InputUtil.getDouble("Discount Percentage: ");
                impl.setDiscountStrategy(new PercentageDiscount(threshold, percentage));
                System.out.println("Strategy set to Percentage Discount.");
            }
        } else {
            System.out.println("Discount strategy cannot be changed with the current OrderService implementation.");
        }
    }
}
