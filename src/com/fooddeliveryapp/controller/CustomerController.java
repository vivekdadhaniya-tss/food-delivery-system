package com.fooddeliveryapp.controller;

import com.fooddeliveryapp.model.*;
import com.fooddeliveryapp.service.OrderService;
import com.fooddeliveryapp.service.PaymentService;
import com.fooddeliveryapp.service.RestaurantService;
import com.fooddeliveryapp.strategy.CashPayment;
import com.fooddeliveryapp.strategy.Impl.PaymentStrategy;
import com.fooddeliveryapp.strategy.UPIPayment;
import com.fooddeliveryapp.util.InputUtil;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class CustomerController {

    private final OrderService orderService;
    private final PaymentService paymentService;
    private final RestaurantService restaurantService;

    public CustomerController(OrderService orderService, PaymentService paymentService, RestaurantService restaurantService) {
        this.orderService = orderService;
        this.paymentService = paymentService;
        this.restaurantService = restaurantService;
    }

    public void start(Scanner scan, User user) {
        Customer customer = (Customer) user;
        while (true) {
            System.out.println("\n=== CUSTOMER DASHBOARD ===");
            System.out.println("1. View Restaurants");
            System.out.println("2. View Menu & Add to Cart");
            System.out.println("3. View Cart");
            System.out.println("4. Checkout / Place Order");
            System.out.println("5. View Order History");
            System.out.println("6. Logout");

            int choice = InputUtil.getInt("Choose an option: ");

            try {
                switch (choice) {
                    case 1 -> viewRestaurants();
                    case 2 -> viewMenuAndAddToCart(customer);
                    case 3 -> viewCart(customer);
                    case 4 -> checkout(customer);
                    case 5 -> viewOrderHistory(customer);
                    case 6 -> {
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

    private void viewRestaurants() {
        List<Restaurant> restaurants = restaurantService.getActiveRestaurants();
        if (restaurants.isEmpty()) {
            System.out.println("No active restaurants available.");
        } else {
            System.out.println("\n--- Available Restaurants ---");
            restaurants.forEach(r -> System.out.println("ID: " + r.getId() + " | Name: " + r.getName()));
        }
    }

    private void viewMenuAndAddToCart(Customer customer) {
        int restaurantId = InputUtil.getInt("Enter Restaurant ID to view menu: ");
        Optional<Restaurant> restaurantOpt = restaurantService.getRestaurantById(restaurantId);

        if (restaurantOpt.isEmpty() || !restaurantOpt.get().isActive()) {
            System.out.println("Restaurant not found or inactive.");
            return;
        }

        Restaurant restaurant = restaurantOpt.get();
        List<FoodItem> menu = restaurantService.getMenu(restaurantId);

        if (menu.isEmpty()) {
            System.out.println("Menu is empty for this restaurant.");
            return;
        }

        System.out.println("\n--- Menu for " + restaurant.getName() + " ---");
        menu.forEach(item -> System.out.println("ID: " + item.getId() + " | Name: " + item.getName() + " | Price: ₹" + item.getPrice() + " | Category: " + item.getCategory()));

        String addToCart = InputUtil.getString("Do you want to add an item to cart? (y/n): ");
        if (addToCart.equalsIgnoreCase("y")) {
            String foodId = InputUtil.getString("Enter Food Item ID: ");
            Optional<FoodItem> foodOpt = restaurantService.getMenuItemById(restaurantId, foodId);

            if (foodOpt.isEmpty()) {
                System.out.println("Invalid Food Item ID.");
                return;
            }

            FoodItem foodItem = foodOpt.get();
            int quantity = InputUtil.getInt("Enter quantity: ");

            customer.getActiveCart().addItem(restaurant, foodItem.getId(), foodItem.getName(), foodItem.getPrice(), quantity);
            System.out.println("Item added to cart successfully!");
        }
    }

    private void viewCart(Customer customer) {
        Cart cart = customer.getActiveCart();
        if (cart.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }

        System.out.println("\n--- Your Cart ---");
        System.out.println("Restaurant: " + cart.getRestaurant().getName());
        cart.getItems().forEach(item -> System.out.println(item.getFoodItemName() + " x" + item.getQuantity() + " @ ₹" + item.getPriceAtPurchase() + " = ₹" + (item.getQuantity() * item.getPriceAtPurchase())));
        System.out.println("Subtotal: ₹" + cart.getSubTotal());

        String clear = InputUtil.getString("Do you want to clear the cart? (y/n): ");
        if (clear.equalsIgnoreCase("y")) {
            cart.clear();
            System.out.println("Cart cleared.");
        }
    }

    private void checkout(Customer customer) {
        if (customer.getActiveCart().isEmpty()) {
            System.out.println("Cart is empty. Cannot checkout.");
            return;
        }

        System.out.println("\n--- Checkout ---");
        System.out.println("Select Payment Method:");
        System.out.println("1. Cash on Delivery");
        System.out.println("2. UPI");
        int payChoice = InputUtil.getInt("Choose: ");

        PaymentStrategy paymentStrategy;
        if (payChoice == 1) {
            paymentStrategy = new CashPayment();
        } else if (payChoice == 2) {
            String upiId = InputUtil.getUPI("Enter UPI ID: ");
            paymentStrategy = new UPIPayment(upiId);
        } else {
            System.out.println("Invalid payment choice.");
            return;
        }

        try {
            Order order = orderService.placeOrder(customer.getId(), paymentStrategy);
            System.out.println("Order placed successfully! Order Number: " + order.getOrderNumber());
            System.out.println("Final Amount: ₹" + order.getFinalAmount());
        } catch (Exception e) {
            System.out.println("Checkout failed: " + e.getMessage());
        }
    }

    private void viewOrderHistory(Customer customer) {
        List<Order> orders = orderService.getOrdersByCustomer(customer.getId());
        if (orders.isEmpty()) {
            System.out.println("No order history found.");
        } else {
            System.out.println("\n--- Order History ---");
            orders.forEach(o -> System.out.println("Order No: " + o.getOrderNumber() + " | Status: " + o.getStatus() + " | Amount: ₹" + o.getFinalAmount()));
        }
    }
}
