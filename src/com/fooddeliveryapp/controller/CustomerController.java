package com.fooddeliveryapp.controller;

import com.fooddeliveryapp.exception.FoodDeliveryException;
import com.fooddeliveryapp.model.*;
import com.fooddeliveryapp.service.*;
import com.fooddeliveryapp.strategy.CashPayment;
import com.fooddeliveryapp.strategy.UPIPayment;
import com.fooddeliveryapp.strategy.Impl.PaymentStrategy;
import com.fooddeliveryapp.util.ConsoleInput;
import com.fooddeliveryapp.util.FormatUtil;
import com.fooddeliveryapp.util.TablePrinter;
import com.fooddeliveryapp.view.InvoiceView;

import java.util.List;

public class CustomerController {
    private final MenuService menuService;
    private final CartService cartService;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final DeliveryService deliveryService;

    public CustomerController(MenuService menuService, CartService cartService, OrderService orderService, PaymentService paymentService, DeliveryService deliveryService) {
        this.menuService = menuService;
        this.cartService = cartService;
        this.orderService = orderService;
        this.paymentService = paymentService;
        this.deliveryService = deliveryService;
    }

    public void start(User user) {
        Customer customer = (Customer) user;

        while (true) {
            System.out.println("\n=======================================");
            System.out.println("      🍽️ CUSTOMER DASHBOARD 🍽️      ");
            System.out.println("=======================================");
            System.out.println("👤 Welcome, " + customer.getName());
            System.out.println("1. 📜 Browse Menu");
            System.out.println("2. 🛒 Manage Cart (" + cartService.getTotalItems(customer.getId()) + " items)");
            System.out.println("3. 💳 Checkout & Place Order");
            System.out.println("4. 📍 Track Ongoing Orders");
            System.out.println("5. 🧾 View Order History & Invoices");
            System.out.println("6. ⭐ Rate Delivery Agent");
            System.out.println("7. 🚪 Logout");
            System.out.println("=======================================");

            int choice = ConsoleInput.getInt("Select an option: ");

            try {
                switch (choice) {
                    case 1 -> browseMenu(customer);
                    case 2 -> manageCart(customer);
                    case 3 -> checkout(customer);
                    case 4 -> trackOrders(customer);
                    case 5 -> viewHistoryAndInvoice(customer);
                    case 6 -> rateAgent();
                    case 7 -> {
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

    private void browseMenu(Customer customer) {
        List<MenuItem> items = menuService.getAllMenuItems().stream()
                .filter(MenuItem::isAvailable)
                .toList();

        if (items.isEmpty()) {
            System.out.println("\n📭 The menu is currently empty. Please check back later!");
            return;
        }

        List<String[]> rows = items.stream()
                .map(m -> new String[]{m.getId(), m.getName(), FormatUtil.formatCurrency(m.getPrice())})
                .toList();

        TablePrinter.print(new String[]{"Item ID", "Name", "Price"}, rows);

        if (ConsoleInput.getString("Add item to cart? (y/n): ").equalsIgnoreCase("y")) {
            String itemId = ConsoleInput.getString("Enter Item ID: ");
            int qty = ConsoleInput.getInt("Enter Quantity: ");

            cartService.addItem(customer.getId(), itemId, qty);
            System.out.println("✅ Item added to cart!");
        }
    }

    private void manageCart(Customer customer) {
        Cart cart = cartService.getCart(customer.getId());

        if (cart.isEmpty()) {
            System.out.println("\n🛒 Your cart is empty.");
            return;
        }

        List<String[]> rows = cart.getItems().stream()
                .map(i -> new String[]{i.getMenuItemId(), i.getMenuItemName(), String.valueOf(i.getQuantity()), FormatUtil.formatCurrency(i.getLineTotal())})
                .toList();

        TablePrinter.print(new String[]{"Item ID", "Name", "Qty", "Total"}, rows);
        System.out.println("Cart Subtotal: " + FormatUtil.formatCurrency(cart.getSubTotal()));

        System.out.println("\n1. Remove Item | 2. Clear Cart | 3. Go Back");
        int choice = ConsoleInput.getInt("Choose: ");

        if (choice == 1) {
            String itemId = ConsoleInput.getString("Enter Item ID to remove: ");
            cartService.removeItem(customer.getId(), itemId);
            System.out.println("✅ Item removed.");
        } else if (choice == 2) {
            cartService.clearCart(customer.getId());
            System.out.println("✅ Cart cleared.");
        }
    }

    private void checkout(Customer customer) {
        if (cartService.getCart(customer.getId()).isEmpty()) {
            System.out.println("\n❌ Cart is empty. Add items before checking out.");
            return;
        }

        System.out.println("\n--- Select Payment Method ---");
        System.out.println("1. Cash on Delivery | 2. UPI");

        PaymentStrategy strategy = (ConsoleInput.getInt("Choose: ") == 1)
                ? new CashPayment()
                : new UPIPayment(ConsoleInput.getString("Enter UPI ID: "));

        Order order = orderService.placeOrder(customer.getId(), strategy);

        // Try to assign an agent. If none available, it stays in the queue.
        if (deliveryService.assignAgentToOrder(order.getOrderNumber()).isEmpty()) {
            System.out.println("⏳ Order Placed! Waiting for an available delivery agent...");
        } else {
            System.out.println("✅ Order Placed Successfully! Agent assigned.");
        }
        System.out.println("Order No: " + order.getOrderNumber());
    }

    private void trackOrders(Customer customer) {
        List<String[]> rows = orderService.getOngoingOrders().stream()
                .filter(o -> o.getCustomerId().equals(customer.getId()))
                .map(o -> new String[]{
                        o.getOrderNumber(),
                        o.getStatus().name(),
                        o.getDeliveryAgentId() != null ? o.getDeliveryAgentId() : "⏳ In Queue..."
                })
                .toList();

        if (rows.isEmpty()) {
            System.out.println("\n📭 You have no ongoing orders.");
            return;
        }

        TablePrinter.print(new String[]{"Order No", "Status", "Agent ID"}, rows);
    }

    private void viewHistoryAndInvoice(Customer customer) {
        List<String[]> rows = orderService.getOrdersByCustomer(customer.getId()).stream()
                .map(o -> new String[]{o.getOrderNumber(), o.getStatus().name(), FormatUtil.formatCurrency(o.getFinalAmount()), FormatUtil.formatDate(o.getCreatedAt())})
                .toList();

        if (rows.isEmpty()) {
            System.out.println("\n📭 No order history found.");
            return;
        }

        TablePrinter.print(new String[]{"Order No", "Status", "Total", "Date"}, rows);

        if (ConsoleInput.getString("Generate Invoice for an order? (y/n): ").equalsIgnoreCase("y")) {
            String orderId = ConsoleInput.getString("Enter Order No: ");
            Order order = orderService.getOrderById(orderId).orElseThrow(() -> new FoodDeliveryException(null, "Order not found"));
            List<Payment> payments = paymentService.getPaymentsByOrderId(orderId);

            InvoiceView.printInvoice(order, payments, customer);
        }
    }

    private void rateAgent() {
        String agentId = ConsoleInput.getString("Enter Delivery Agent ID: ");
        double rating = ConsoleInput.getDouble("Enter Rating (1.0 to 5.0): ");

        deliveryService.rateDeliveryAgent(agentId, rating);
        System.out.println("✅ Thank you for your feedback!");
    }
}