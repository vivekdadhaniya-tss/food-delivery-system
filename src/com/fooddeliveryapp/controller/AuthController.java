package com.fooddeliveryapp.controller;

import com.fooddeliveryapp.exception.FoodDeliveryException;
import com.fooddeliveryapp.model.User;
import com.fooddeliveryapp.service.AuthService;
import com.fooddeliveryapp.util.ConsoleInput;
import com.fooddeliveryapp.util.InputUtil;

public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public User login() {
        System.out.println("\n--- LOGIN ---");
        String email = ConsoleInput.getString("Email     : ");
        String password = ConsoleInput.getString("Password  : ");
        return authService.login(email, password);
    }

    public void registerCustomer() {
        System.out.println("\n--- REGISTER CUSTOMER ---");
        try {
            String name = InputUtil.requireNonBlank(ConsoleInput.getString("Name                  : "), "Name");
            String phone = InputUtil.validatePhone(ConsoleInput.getString("Phone (10 digits)     : "));
            String email = InputUtil.validateEmail(ConsoleInput.getString("Email                 : "));
            String password = InputUtil.validatePassword(ConsoleInput.getString("Password (min 4 chars): "));
            String address = InputUtil.requireNonBlank(ConsoleInput.getString("Address               : "), "Address");

            authService.registerCustomer(name, phone, email, password, address);
            System.out.println("Customer registered successfully!");
        } catch (IllegalArgumentException | FoodDeliveryException e) {
            System.out.println("Registration Failed: " + e.getMessage());
        }
    }

    public void registerDeliveryAgent() {
        System.out.println("\n--- REGISTER DELIVERY AGENT ---");
        try {
            String name = InputUtil.requireNonBlank(ConsoleInput.getString("Name                  : "), "Name");
            String phone = InputUtil.validatePhone(ConsoleInput.getString("Phone (10 digits)     : "));
            String email = InputUtil.validateEmail(ConsoleInput.getString("Email                 : "));
            String password = InputUtil.validatePassword(ConsoleInput.getString("Password (min 4 chars): "));

            authService.registerDeliveryAgent(name, phone, email, password);
            System.out.println("Delivery Agent registered successfully!");
        } catch (IllegalArgumentException | FoodDeliveryException e) {
            System.out.println("Registration Failed: " + e.getMessage());
        }
    }
}