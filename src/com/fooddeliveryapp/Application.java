package com.fooddeliveryapp;

import com.fooddeliveryapp.controller.AdminController;
import com.fooddeliveryapp.controller.CustomerController;
import com.fooddeliveryapp.controller.DeliveryAgentController;
import com.fooddeliveryapp.model.User;
import com.fooddeliveryapp.model.type.Role;
import com.fooddeliveryapp.repository.*;
import com.fooddeliveryapp.repository.inmemory.*;
import com.fooddeliveryapp.service.*;
import com.fooddeliveryapp.service.Impl.*;
import com.fooddeliveryapp.service.impl.InvoiceServiceImpl;
import com.fooddeliveryapp.util.InputUtil;

import java.util.Scanner;

public class Application {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        UserRepository userRepo = new InMemoryUserRepository();
        RestaurantRepository restaurantRepo = new InMemoryRestaurantRepository();
        OrderRepository orderRepo = new InMemoryOrderRepository();
        DeliveryAgentRepository agentRepo = new InMemoryDeliveryAgentRepository();
        PaymentRepository paymentRepo = new InMemoryPaymentRepository();

        UserService userService = new UserServiceImpl(userRepo);
        AuthService authService = new AuthServiceImpl(userRepo);
        RestaurantService restaurantService = new RestaurantServiceImpl(restaurantRepo);
        OrderService orderService = new OrderServiceImpl(orderRepo, userRepo, agentRepo);
        DeliveryService deliveryService = new DeliveryServiceImpl(agentRepo);
        PaymentService paymentService = new PaymentServiceImpl(paymentRepo);
        InvoiceService invoiceService = new InvoiceServiceImpl();

        // Controllers
        AdminController adminController = new AdminController(restaurantService, userService, orderService);
        CustomerController customerController = new CustomerController(orderService, paymentService, restaurantService);
        DeliveryAgentController deliveryController = new DeliveryAgentController(orderService, deliveryService);

        // ===== Console Login/Registration Loop =====
        while (true) {
            System.out.println("\n=== FOOD DELIVERY APP ===");
            System.out.println("1. Register Customer");
            System.out.println("2. Register Delivery Agent");
            System.out.println("3. Register Admin");
            System.out.println("4. Login");
            System.out.println("5. Exit");

            int choice = InputUtil.getInt("Choose: ");

            try {
                switch (choice) {
                    case 1 -> authService.registerCustomer(
                            InputUtil.getString("Name: "),
                            InputUtil.getStringPattern("Phone: ", "\\d{10}", "Phone must be 10 digits"),
                            InputUtil.getStringPattern("Email: ", "[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}", "Invalid email"),
                            InputUtil.getString("Password: "),
                            InputUtil.getString("Address: ")
                    );
                    case 2 -> authService.registerUser(
                            InputUtil.getString("Name: "),
                            InputUtil.getStringPattern("Phone: ", "\\d{10}", "Phone must be 10 digits"),
                            InputUtil.getStringPattern("Email: ", "[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}", "Invalid email"),
                            InputUtil.getString("Password: "),
                            Role.DELIVERY_AGENT
                    );
                    case 3 -> authService.registerUser(
                            InputUtil.getString("Name: "),
                            InputUtil.getStringPattern("Phone: ", "\\d{10}", "Phone must be 10 digits"),
                            InputUtil.getStringPattern("Email: ", "[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}", "Invalid email"),
                            InputUtil.getString("Password: "),
                            Role.ADMIN
                    );
                    case 4 -> {
                        User user = authService.login(
                                InputUtil.getStringPattern("Email: ", "[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}", "Invalid email"),
                                InputUtil.getString("Password: ")
                        );
                        System.out.println("Logged in as: " + user.getRole());
                        // Direct to controller based on role
                        if (user.getRole() == Role.ADMIN) {
                            adminController.start(scan, user);
                        } else if (user.getRole() == Role.CUSTOMER) {
                            customerController.start(scan, user);
                        } else if (user.getRole() == Role.DELIVERY_AGENT) {
                            deliveryController.start(scan, user);
                        }
                    }
                    case 5 -> {
                        System.out.println("Exiting...");
                        System.exit(0);
                    }
                    default -> System.out.println("Invalid choice");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
