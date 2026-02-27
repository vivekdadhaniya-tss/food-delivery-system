package com.fooddeliveryapp.controller;

import com.fooddeliveryapp.exception.FoodDeliveryException;
import com.fooddeliveryapp.model.*;
import com.fooddeliveryapp.model.type.FoodCategory;
import com.fooddeliveryapp.service.*;
import com.fooddeliveryapp.strategy.*;
import com.fooddeliveryapp.strategy.Impl.PaymentStrategy;
import com.fooddeliveryapp.util.InputUtil;

import java.util.Optional;
import java.util.Scanner;

public class CustomerController {
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final RestaurantService restaurantService;
    private final InvoiceService invoiceService;
    private final DeliveryService deliveryService; // Added DeliveryService

    public CustomerController(OrderService orderService, PaymentService paymentService, RestaurantService restaurantService, InvoiceService invoiceService, DeliveryService deliveryService) {
        this.orderService = orderService;
        this.paymentService = paymentService;
        this.restaurantService = restaurantService;
        this.invoiceService = invoiceService;
        this.deliveryService = deliveryService;
    }

    public void start(User user) {
        Customer customer = (Customer) user;
        while (true) {
            System.out.println("\n=== CUSTOMER DASHBOARD ===");
            System.out.println("--- Browse & Search ---");
            System.out.println("1. View All Restaurants\n2. View All Food Items\n3. Search Food by Category\n4. Search Food by Name");
            System.out.println("--- Cart & Checkout ---");
            System.out.println("5. Manage Cart\n6. Checkout / Place Order");
            System.out.println("--- Order Management ---");
            System.out.println("7. Track Ongoing Order\n8. Cancel Order");
            System.out.println("--- History & Feedback ---");
            System.out.println("9. View Order History & Re-order\n10. Generate / View Invoice\n11. Rate & Review\n12. Logout");

            int choice = InputUtil.getInt("Choose an option: ");

            try {
                switch (choice) {
                    case 1 -> restaurantService.getActiveRestaurants().forEach(r -> System.out.println(r.getId() + " | " + r.getName()));
                    case 2 -> viewAllFoodItems();
                    case 3 -> searchFoodByCategory();
                    case 4 -> searchFoodByName();
                    case 5 -> manageCart(customer);
                    case 6 -> checkout(customer);
                    case 7 -> trackOrders(customer);
                    case 8 -> cancelOrder(customer);
                    case 9 -> viewOrderHistory(customer);
                    case 10 -> generateInvoice(customer);
                    case 11 -> rateAndReview();
                    case 12 -> { return; }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (FoodDeliveryException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void viewAllFoodItems() {
        restaurantService.getActiveRestaurants().forEach(r -> {
            System.out.println("\n--- " + r.getName() + " ---");
            restaurantService.getMenu(r.getId()).forEach(i -> System.out.println("ID: " + i.getId() + " | Name: " + i.getName() + " | Price: ₹" + i.getPrice() + " | Stock: " + i.getStock() + " | Category: " + i.getCategory()));
        });
    }

    private void searchFoodByCategory() {
        System.out.println("1.VEG 2.NON_VEG 3.DRINKS 4.DESSERT");
        FoodCategory cat = FoodCategory.values()[InputUtil.getInt("Choose (1-4): ") - 1];
        restaurantService.getActiveRestaurants().forEach(r -> {
            restaurantService.searchMenuItems(r.getId(), null, cat).forEach(i -> System.out.println(r.getName() + " | ID: " + i.getId() + " | Name: " + i.getName() + " | Price: ₹" + i.getPrice() + " | Stock: " + i.getStock()));
        });
    }

    private void searchFoodByName() {
        String name = InputUtil.getString("Enter food name: ");
        restaurantService.getActiveRestaurants().forEach(r -> {
            restaurantService.searchMenuItems(r.getId(), name, null).forEach(i -> System.out.println(r.getName() + " | ID: " + i.getId() + " | Name: " + i.getName() + " | Price: ₹" + i.getPrice() + " | Stock: " + i.getStock()));
        });
    }

    private void manageCart(Customer customer) {
        Cart cart = customer.getActiveCart();
        System.out.println("1. View Cart | 2. Add Item | 3. Remove Item");
        int choice = InputUtil.getInt("Choose: ");
        if (choice == 1) {
            if (cart.isEmpty()) System.out.println("Cart is empty.");
            else {
                cart.getItems().forEach(i -> System.out.println(i.getFoodItemName() + " x" + i.getQuantity() + " = ₹" + (i.getPriceAtPurchase() * i.getQuantity())));
                System.out.println("Total: ₹" + cart.getSubTotal());
            }
        } else if (choice == 2) {
            int rId = InputUtil.getInt("Restaurant ID: ");
            Restaurant r = restaurantService.getRestaurantById(rId).orElseThrow(() -> new FoodDeliveryException("Not found"));
            String fId = InputUtil.getString("Food ID: ");
            FoodItem f = restaurantService.getMenuItemById(rId, fId).orElseThrow(() -> new FoodDeliveryException("Not found"));
            cart.addItem(r, f.getId(), f.getName(), f.getPrice(), InputUtil.getInt("Quantity: "));
            System.out.println("Added to cart.");
        } else if (choice == 3) {
            cart.removeItem(InputUtil.getString("Food ID to remove: "));
            System.out.println("Removed.");
        }
    }

    private void checkout(Customer customer) {
        if (customer.getActiveCart().isEmpty()) { System.out.println("Cart is empty."); return; }
        System.out.println("1. Cash | 2. UPI");
        PaymentStrategy strategy = InputUtil.getInt("Choose: ") == 1 ? new CashPayment() : new UPIPayment(InputUtil.getUPI("UPI ID: "));
        Order order = orderService.placeOrder(customer.getId(), strategy);
        paymentService.processPayment(order, strategy);
        System.out.println("Order Placed: " + order.getOrderNumber());

        // assign order to a delivery agent
        Optional<DeliveryAgent> agentOpt = deliveryService.assignOrder(order);
        if (agentOpt.isPresent()) {
            System.out.println("Order assigned to Delivery Agent: " + agentOpt.get().getName());
        } else {
            System.out.println("No delivery agents available right now. Order will be assigned later.");
        }
    }

    private void trackOrders(Customer customer) {
        orderService.getOrdersByCustomer(customer.getId()).stream()
                .filter(o -> o.getStatus() != com.fooddeliveryapp.model.type.OrderStatus.DELIVERED && o.getStatus() != com.fooddeliveryapp.model.type.OrderStatus.CANCELLED)
                .forEach(o -> System.out.println("Order: " + o.getOrderNumber() + " | Status: " + o.getStatus() + " | Agent ID: " + (o.getAssignedAgentId() != null ? o.getAssignedAgentId() : "Unassigned")));
    }

    private void cancelOrder(Customer customer) {
        orderService.cancelOrder(InputUtil.getString("Order Number: "));
        System.out.println("Order Cancelled.");
    }

    private void viewOrderHistory(Customer customer) {
        orderService.getOrdersByCustomer(customer.getId()).forEach(o -> System.out.println("Order: " + o.getOrderNumber() + " | Status: " + o.getStatus() + " | Amount: ₹" + o.getFinalAmount()));
    }

    private void generateInvoice(Customer customer) {
        Order o = orderService.getOrderById(InputUtil.getString("Order Number: ")).orElseThrow(() -> new FoodDeliveryException("Not found"));
        System.out.println(invoiceService.generateInvoice(o));
    }

    private void rateAndReview() {
        System.out.println("Rating submitted successfully! (Mocked)");
    }
}