package com.fooddeliveryapp.controller;

import com.fooddeliveryapp.model.User;
import com.fooddeliveryapp.model.type.Role;
import com.fooddeliveryapp.service.AuthService;
import com.fooddeliveryapp.util.InputUtil;

public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public void register(int choice) {
        String name = InputUtil.getString("Name: ");
        String phone = InputUtil.getStringPattern("Phone: ", "\\d{10}", "Phone must be 10 digits");
        String email = InputUtil.getStringPattern("Email: ", "[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}", "Invalid email");
        String password = InputUtil.getString("Password: ");

        switch (choice) {
            case 1 -> {
                String addtress = InputUtil.getString("Address: ");
                authService.registerCustomer(name,phone,email,password,addtress);
                System.out.println("Customer registered successfully!");
            }
            case 2 -> {
                authService.registerUser(name, phone, email, password, Role.DELIVERY_AGENT);
                System.out.println("Delivery Agent registered successfully!");
            }
            case 3 -> {
                authService.registerUser(name, phone, email, password, Role.ADMIN);
                System.out.println("Admin registered successfully!");
            }
            default -> System.out.println("Invalid registration choice!");
        }
    }

    public User login() {
        String email = InputUtil.getStringPattern("Email: ", "[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}", "Invalid email");
        String password = InputUtil.getString("Password: ");
        User user = authService.login(email, password);
        System.out.println("Logged in as: " + user.getRole());
        return user;
    }
}
