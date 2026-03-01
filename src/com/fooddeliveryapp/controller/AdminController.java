package com.fooddeliveryapp.controller;

import com.fooddeliveryapp.config.SystemConfig;
import com.fooddeliveryapp.exception.FoodDeliveryException;
import com.fooddeliveryapp.model.*;
import com.fooddeliveryapp.type.Role;
import com.fooddeliveryapp.service.*;
import com.fooddeliveryapp.strategy.FlatDiscount;
import com.fooddeliveryapp.strategy.NoDiscount;
import com.fooddeliveryapp.strategy.PercentageDiscount;
import com.fooddeliveryapp.util.ConsoleInput;
import com.fooddeliveryapp.util.FormatUtil;
import com.fooddeliveryapp.util.TablePrinter;

import java.util.List;
import java.util.stream.Collectors;

public class AdminController {
    private final MenuService menuService;
    private final UserService userService;
    private final OrderService orderService;
    private final PaymentService paymentService;

    public AdminController(MenuService menuService, UserService userService, OrderService orderService, PaymentService paymentService) {
        this.menuService = menuService;
        this.userService = userService;
        this.orderService = orderService;
        this.paymentService = paymentService;
    }

    public void start(User admin) {
        while (true) {
            System.out.println("\n=======================================");
            System.out.println("        🛡️ ADMIN DASHBOARD 🛡️        ");
            System.out.println("=======================================");
            System.out.println("--- 🍔 Menu Management ---");
            System.out.println("1. Manage Categories");
            System.out.println("2. Manage Menu Items");
            System.out.println("--- 👥 Users & Agents ---");
            System.out.println("3. View All Customers");
            System.out.println("4. View Delivery Agents");
            System.out.println("--- 📦 Orders & Finance ---");
            System.out.println("5. View All Orders");
            System.out.println("6. View Financial Overview");
            System.out.println("--- ⚙️ System Settings ---");
            System.out.println("7. Configure Discount Strategy");
            System.out.println("8. Configure Delivery Fee & Tax");
            System.out.println("9. Logout");
            System.out.println("=======================================");

            int choice = ConsoleInput.getInt("Select an option: ");

            try {
                switch (choice) {
                    case 1 -> manageCategories();
                    case 2 -> manageMenuItems();
                    case 3 -> viewUsers(Role.CUSTOMER);
                    case 4 -> viewDeliveryAgents();
                    case 5 -> viewOrders();
                    case 6 -> viewFinance();
                    case 7 -> configureDiscounts();
                    case 8 -> configureFees();
                    case 9 -> {
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

    private void manageCategories() {
        System.out.println("\n1. Add Category | 2. View Categories | 3. Toggle Status");
        int choice = ConsoleInput.getInt("Choose: ");
        if (choice == 1) {
            String name = ConsoleInput.getString("Category Name: ");
            Category c = menuService.addCategory(name);
            System.out.println("✅ Category added with ID: " + c.getId());
        } else if (choice == 2) {
            List<String[]> rows = menuService.getAllCategories().stream()
                    .map(c -> new String[]{c.getId(), c.getName(), c.isActive() ? "Active" : "Inactive"})
                    .collect(Collectors.toList());
            TablePrinter.print(new String[]{"ID", "Name", "Status"}, rows);
        } else if (choice == 3) {
            String id = ConsoleInput.getString("Category ID: ");
            boolean activate = ConsoleInput.getInt("1 to Activate, 0 to Deactivate: ") == 1;
            menuService.toggleCategoryStatus(id, activate);
            System.out.println("✅ Status updated.");
        }
    }

    private void manageMenuItems() {
        System.out.println("\n1. Add Item | 2. View Items | 3. Toggle Availability");
        int choice = ConsoleInput.getInt("Choose: ");
        if (choice == 1) {
            String catId = ConsoleInput.getString("Category ID: ");
            String name = ConsoleInput.getString("Item Name: ");
            double price = ConsoleInput.getDouble("Price: ");
            MenuItem item = menuService.addMenuItem(name, price, catId);
            System.out.println("✅ Item added with ID: " + item.getId());
        } else if (choice == 2) {
            List<String[]> rows = menuService.getAllMenuItems().stream()
                    .map(m -> new String[]{m.getId(), m.getName(), m.getCategoryId(), FormatUtil.formatCurrency(m.getPrice()), m.isAvailable() ? "Yes" : "No"})
                    .collect(Collectors.toList());
            TablePrinter.print(new String[]{"ID", "Name", "Category ID", "Price", "Available"}, rows);
        } else if (choice == 3) {
            String id = ConsoleInput.getString("Item ID: ");
            boolean available = ConsoleInput.getInt("1 to make Available, 0 to make Unavailable: ") == 1;
            menuService.toggleMenuItemAvailability(id, available);
            System.out.println("✅ Availability updated.");
        }
    }

    private void viewUsers(Role role) {
        List<String[]> rows = userService.getUsersByRole(role).stream()
                .map(u -> new String[]{u.getId(), u.getName(), u.getEmail(), u.getPhone()})
                .collect(Collectors.toList());
        TablePrinter.print(new String[]{"ID", "Name", "Email", "Phone"}, rows);
    }

    private void viewDeliveryAgents() {
        List<String[]> rows = userService.getAllDeliveryAgents().stream()
                .map(a -> new String[]{a.getId(), a.getName(), a.isAvailable() ? "Yes" : "No", String.valueOf(a.getTotalDeliveries()), String.format("%.1f", a.getRating())})
                .collect(Collectors.toList());
        TablePrinter.print(new String[]{"ID", "Name", "Available", "Deliveries", "Rating"}, rows);
    }

    private void viewOrders() {
        List<String[]> rows = orderService.getAllOrders().stream()
                .map(o -> new String[]{o.getOrderNumber(), o.getCustomerId(), o.getDeliveryAgentId() != null ? o.getDeliveryAgentId() : "Unassigned", o.getStatus().name(), FormatUtil.formatCurrency(o.getFinalAmount())})
                .collect(Collectors.toList());
        TablePrinter.print(new String[]{"Order No", "Customer ID", "Agent ID", "Status", "Total"}, rows);
    }

    private void viewFinance() {
        System.out.println("\n💰 Total Revenue: " + FormatUtil.formatCurrency(paymentService.calculateTotalRevenue()));
        List<String[]> rows = paymentService.getAllPayments().stream()
                .map(p -> new String[]{p.getPaymentId(), p.getOrderId(), p.getMode().name(), p.getStatus().name(), FormatUtil.formatCurrency(p.getAmount())})
                .collect(Collectors.toList());
        TablePrinter.print(new String[]{"Payment ID", "Order ID", "Mode", "Status", "Amount"}, rows);
    }

    private void configureDiscounts() {
        System.out.println("\n1. No Discount | 2. Flat Discount | 3. Percentage Discount");
        int choice = ConsoleInput.getInt("Choose Strategy: ");
        SystemConfig config = SystemConfig.getInstance();

        if (choice == 1) {
            config.setDiscountStrategy(new NoDiscount());
        } else if (choice == 2) {
            double threshold = ConsoleInput.getDouble("Minimum Order Amount: ");
            double amount = ConsoleInput.getDouble("Flat Discount Amount: ");
            config.setDiscountStrategy(new FlatDiscount(threshold, amount));
        } else if (choice == 3) {
            double threshold = ConsoleInput.getDouble("Minimum Order Amount: ");
            double percent = ConsoleInput.getDouble("Discount Percentage (e.g., 10 for 10%): ");
            config.setDiscountStrategy(new PercentageDiscount(threshold, percent));
        }
        System.out.println("✅ Discount Strategy Updated.");
    }

    private void configureFees() {
        SystemConfig config = SystemConfig.getInstance();
        config.setDeliveryFee(ConsoleInput.getDouble("Enter new Delivery Fee: "));
        config.setTaxRate(ConsoleInput.getDouble("Enter new Tax Rate (%): "));
        System.out.println("✅ Fees Updated.");
    }
}