package com.fooddeliveryapp.service.Impl;

import com.fooddeliveryapp.exception.AuthenticationException;
import com.fooddeliveryapp.exception.FoodDeliveryException;
import com.fooddeliveryapp.exception.UserAlreadyExistsException;
import com.fooddeliveryapp.model.*;
import com.fooddeliveryapp.model.type.Role;
import com.fooddeliveryapp.repository.UserRepository;
import com.fooddeliveryapp.util.IdGenerator;
import com.fooddeliveryapp.service.AuthService;

public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Customer Registration
    @Override
    public User registerCustomer(String name,
                                 String phone,
                                 String email,
                                 String password,
                                 String address) {

        if (userRepository.findByEmail(email) != null) {
            throw new UserAlreadyExistsException("Email already registered");
        }

        Customer customer = new Customer(
                IdGenerator.nextUserId(),
                name,
                phone,
                email,
                address,
                password
        );

        userRepository.save(customer);
        return customer;
    }

    // Admin / DeliveryAgent Registration
    @Override
    public User registerUser(String name,
                             String phone,
                             String email,
                             String password,
                             Role role) {

        if (role == Role.CUSTOMER) {
            throw new FoodDeliveryException("Use registerCustomer for customers");
        }

        if (userRepository.findByEmail(email) != null) {
            throw new UserAlreadyExistsException("Email already registered");
        }

        if (role == Role.ADMIN) {
            // Check if admin already exists
            if (userRepository.findAll().stream().anyMatch(u -> u.getRole() == Role.ADMIN)) {
                throw new UserAlreadyExistsException("Admin already exists");
            }
            Admin admin = new Admin(
                    IdGenerator.nextUserId(),
                    name,
                    phone,
                    email,
                    password
            );
            userRepository.save(admin);
            return admin;
        }

        if (role == Role.DELIVERY_AGENT) {
            DeliveryAgent agent = new DeliveryAgent(
                    IdGenerator.nextUserId(),
                    name,
                    phone,
                    email,
                    password
            );
            userRepository.save(agent);
            return agent;
        }

        throw new UserAlreadyExistsException("Invalid role");
    }

    // Login
    @Override
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new AuthenticationException("User not found");
        }

        if (!user.getPassword().equals(password)) {
            throw new AuthenticationException("Invalid password");
        }

        return user;
    }
}