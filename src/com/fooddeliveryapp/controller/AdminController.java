package com.fooddeliveryapp.controller;

import com.fooddeliveryapp.exception.FoodDeliveryException;
import com.fooddeliveryapp.model.*;
import com.fooddeliveryapp.model.type.*;
import com.fooddeliveryapp.service.*;
import com.fooddeliveryapp.service.Impl.OrderServiceImpl;
import com.fooddeliveryapp.strategy.*;
import com.fooddeliveryapp.util.InputUtil;

import java.util.*;

public class AdminController {
    private final RestaurantService restaurantService;
    private final UserService userService;
    private final OrderService orderService;
    private final PaymentService paymentService;

    public AdminController(RestaurantService restaurantService, UserService userService, OrderService orderService, PaymentService paymentService) {
        this.restaurantService = restaurantService;
        this.userService = userService;
        this.orderService = orderService;
        this.paymentService = paymentService;
    }

    public void start(Scanner scan, User user) {
        while (true) {
            System.out.println("\n=== ADMIN DASHBOARD ===");
            System.out.println("--- Restaurant & Menu ---");
            System.out.println("1. Manage Restaurants");
            System.out.println("2. Manage Menu Items");
            System.out.println("--- Users & Agents ---");
            System.out.println("3. View All Customers");
            System.out.println("4. View All Delivery Agents");
            System.out.println("5. View Available Delivery Agents");
            System.out.println("--- Orders & Finance ---");
            System.out.println("6. View All Order History");
            System.out.println("7. View Ongoing Orders");
            System.out.println("8. View Payment History");
            System.out.println("9. View Total Revenue");
            System.out.println("--- Settings ---");
            System.out.println("10. Set Discount Strategy");
            System.out.println("11. Logout");

            int choice = InputUtil.getInt("Choose an option: ");

            try {
                switch (choice) {
                    case 1 -> manageRestaurants();
                    case 2 -> manageMenuItems();
                    case 3 -> viewUsersByRole(Role.CUSTOMER);
                    case 4 -> viewUsersByRole(Role.DELIVERY_AGENT);
                    case 5 -> viewAvailableAgents();
                    case 6 -> viewAllOrders();
                    case 7 -> viewOngoingOrders();
                    case 8 -> viewPaymentHistory();
                    case 9 -> viewTotalRevenue();
                    case 10 -> setDiscountStrategy();
                    case 11 -> {
                        System.out.println("Logging out...");
                        return;
                    }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (FoodDeliveryException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("System Error: Something went wrong!");
            }
        }
    }

    private void manageRestaurants() {
        System.out.println("\n1. Add | 2. View | 3. Remove | 4. Activate | 5. Deactivate");
        int choice = InputUtil.getInt("Choose: ");
        if (choice == 1) {
            String name = InputUtil.getString("Restaurant Name: ");
            Restaurant r = new Restaurant(com.fooddeliveryapp.util.IdGenerator.nextRestaurantId(), name, true, new HashMap<>(), new ArrayList<>(), 0);
            restaurantService.addRestaurant(r);
            System.out.println("Added ID: " + r.getId());
        } else if (choice == 2) {
            restaurantService.getAllRestaurants().forEach(r -> System.out.println("ID: " + r.getId() + " | Name: " + r.getName() + " | Active: " + r.isActive()));
        } else if (choice == 3) {
            restaurantService.removeRestaurant(InputUtil.getInt("ID to remove: "));
            System.out.println("Removed.");
        } else if (choice == 4 || choice == 5) {
            int id = InputUtil.getInt("Restaurant ID: ");
            Restaurant r = restaurantService.getRestaurantById(id).orElseThrow(() -> new FoodDeliveryException("Not found"));
            r.setActive(choice == 4);
            restaurantService.updateRestaurant(r);
            System.out.println("Status updated.");
        }
    }

    private void manageMenuItems() {
        int rId = InputUtil.getInt("Restaurant ID: ");
        System.out.println("\n1. Add | 2. View | 3. Remove | 4. Update");
        int choice = InputUtil.getInt("Choose: ");
        if (choice == 1) {
            FoodItem item = new FoodItem(InputUtil.getString("ID: "), InputUtil.getString("Name: "), InputUtil.getDouble("Price: "), InputUtil.getInt("Stock: "), FoodCategory.VEG);
            restaurantService.addMenuItem(rId, item);
        } else if (choice == 2) {
            restaurantService.getMenu(rId).forEach(i -> System.out.println(i.getId() + " | " + i.getName() + " | ₹" + i.getPrice()));
        } else if (choice == 3) {
            restaurantService.removeMenuItem(rId, InputUtil.getString("Item ID: "));
        }
    }

    private void viewUsersByRole(Role role) {
        userService.getUsersByRole(role).forEach(u -> System.out.println("ID: " + u.getId() + " | Name: " + u.getName()));
    }

    private void viewAvailableAgents() {
        userService.getAvailableDeliveryAgents().forEach(a -> System.out.println("ID: " + a.getId() + " | Name: " + a.getName()));
    }

    private void viewAllOrders() {
        orderService.getAllOrders().forEach(o -> System.out.println(o.getOrderNumber() + " | Status: " + o.getStatus() + " | ₹" + o.getFinalAmount()));
    }

    private void viewOngoingOrders() {
        orderService.getAllOrders().stream()
                .filter(o -> o.getStatus() != OrderStatus.DELIVERED && o.getStatus() != OrderStatus.CANCELLED)
                .forEach(o -> System.out.println(o.getOrderNumber() + " | Status: " + o.getStatus()));
    }

    private void viewPaymentHistory() {
        paymentService.getAllPayments().forEach(p -> System.out.println(p.getPaymentId() + " | Order: " + p.getOrderNumber() + " | ₹" + p.getAmount() + " | " + p.getMethod()));
    }

    private void viewTotalRevenue() {
        double total = paymentService.getAllPayments().stream().mapToDouble(Payment::getAmount).sum();
        System.out.println("Total Revenue: ₹" + total);
    }

    private void setDiscountStrategy() {
        System.out.println("1. No Discount | 2. Flat | 3. Percentage");
        int choice = InputUtil.getInt("Choose: ");
        if (orderService instanceof OrderServiceImpl impl) {
            if (choice == 1) impl.setDiscountStrategy(new NoDiscount());
            else if (choice == 2) impl.setDiscountStrategy(new FlatDiscount(InputUtil.getDouble("Min Amount: "), InputUtil.getDouble("Flat Discount: ")));
            else if (choice == 3) impl.setDiscountStrategy(new PercentageDiscount(InputUtil.getDouble("Min Amount: "), InputUtil.getDouble("Percentage: ")));
            System.out.println("Strategy Updated.");
        }
    }
}