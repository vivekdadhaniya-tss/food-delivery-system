package com.fooddeliveryapp;

import com.fooddeliveryapp.config.SystemConfig;
import com.fooddeliveryapp.controller.*;
import com.fooddeliveryapp.exception.FoodDeliveryException;
import com.fooddeliveryapp.model.User;
import com.fooddeliveryapp.type.Role;
import com.fooddeliveryapp.repository.*;
import com.fooddeliveryapp.repository.inmemory.*;
import com.fooddeliveryapp.service.*;
import com.fooddeliveryapp.service.Impl.*;
import com.fooddeliveryapp.util.ConsoleInput;

public class Application {
    public static void main(String[] args) {

        // 1. Initialize Repositories
        UserRepository              userRepo    = new InMemoryUserRepository();
        DeliveryAgentRepository     agentRepo   = new InMemoryDeliveryAgentRepository(userRepo);
        CategoryRepository          categoryRepo = new InMemoryCategoryRepository();
        MenuItemRepository          menuRepo    = new InMemoryMenuItemRepository(categoryRepo);
        OrderRepository             orderRepo   = new InMemoryOrderRepository();
        PaymentRepository           paymentRepo = new InMemoryPaymentRepository();

        // 2. Initialize Services
        AuthService     authService     = new AuthServiceImpl(userRepo);
        UserService     userService     = new UserServiceImpl(userRepo, agentRepo);
        MenuService     menuService     = new MenuServiceImpl(categoryRepo, menuRepo);
        CartService     cartService     = new CartServiceImpl(userRepo, menuRepo);
        PaymentService  paymentService  = new PaymentServiceImpl(paymentRepo);
        OrderService    orderService    = new OrderServiceImpl(orderRepo, cartService, paymentService);
        DeliveryService deliveryService = new DeliveryServiceImpl(agentRepo, userRepo, orderService);

        // 3. Initialize Controllers
        AuthController          authController      = new AuthController(authService);
        AdminController         adminController     = new AdminController(menuService, userService, orderService, paymentService);
        CustomerController      customerController  = new CustomerController(menuService, cartService, orderService, paymentService, deliveryService);
        DeliveryAgentController agentController     = new DeliveryAgentController(orderService, deliveryService);


        // 4. Seed Default System Data directly via SystemConfig
        SystemConfig.getInstance().initializeSystemDefaults(authService);

        // 5. Main Application Loop
        while (true) {
            System.out.println("\n=======================================");
            System.out.println("      🍔 WELCOME TO FOOD APP 🍔      ");
            System.out.println("=======================================");
            System.out.println("1. Login");
            System.out.println("2. Register as Customer");
            System.out.println("3. Register as Delivery Agent");
            System.out.println("4. Exit");
            System.out.println("=======================================");

            int choice = ConsoleInput.getInt("Select an option: ");

            try {
                switch (choice) {
                    case 1 -> {
                        User user = authController.login();
                        System.out.println("✅ Login Successful! Welcome, " + user.getName());

                        // Route to the correct dashboard based on Role
                        if      (user.getRole() == Role.ADMIN)          adminController.start(user);
                        else if (user.getRole() == Role.CUSTOMER)       customerController.start(user);
                        else if (user.getRole() == Role.DELIVERY_AGENT) agentController.start(user);
                    }
                    case 2 -> authController.registerCustomer();
                    case 3 -> authController.registerDeliveryAgent();
                    case 4 -> {
                        System.out.println("Goodbye! Have a great day! 👋");
                        System.exit(0);
                    }
                    default -> System.out.println("❌ Invalid choice. Please try again.");
                }
            } catch (FoodDeliveryException e) {
                System.out.println("❌ Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("❌ System Error: Something went wrong!");
            }
        }
    }
}