package com.fooddeliveryapp.service.Impl;

import com.fooddeliveryapp.exception.FoodDeliveryException;
import com.fooddeliveryapp.model.*;
import com.fooddeliveryapp.repository.UserRepository;
import com.fooddeliveryapp.service.AuthService;
import com.fooddeliveryapp.type.ErrorType;
import com.fooddeliveryapp.type.IdType;
import com.fooddeliveryapp.util.IdGenerator;
import com.fooddeliveryapp.util.InputUtil;

public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new FoodDeliveryException(ErrorType.AUTHENTICATION_ERROR, "User not found with this email"));

        if (!user.getPassword().equals(password)) {
            throw new FoodDeliveryException(ErrorType.AUTHENTICATION_ERROR, "Invalid password");
        }
        return user;
    }

    @Override
    public Customer registerCustomer(String name, String phone, String email, String password, String address) {
        // Validate FIRST to prevent wasting IDs
        InputUtil.requireNonBlank(name, "Name");
        InputUtil.validatePhone(phone);
        InputUtil.validateEmail(email);
        InputUtil.validatePassword(password);
        InputUtil.requireNonBlank(address, "Address");
        checkEmailExists(email);

        // Generate ID LAST
        String id = IdGenerator.generate(IdType.USER);
        Customer customer = new Customer(id, name, phone, email, address, password);
        return (Customer) userRepository.save(customer);
    }

    @Override
    public DeliveryAgent registerDeliveryAgent(String name, String phone, String email, String password) {
        InputUtil.requireNonBlank(name, "Name");
        InputUtil.validatePhone(phone);
        InputUtil.validateEmail(email);
        InputUtil.validatePassword(password);
        checkEmailExists(email);

        String id = IdGenerator.generate(IdType.USER);
        DeliveryAgent agent = new DeliveryAgent(id, name, phone, email, password);
        return (DeliveryAgent) userRepository.save(agent);
    }

    @Override
    public Admin registerAdmin(String name, String phone, String email, String password) {
        checkEmailExists(email);
        String id = IdGenerator.generate(IdType.USER);
        Admin admin = new Admin(id, name, phone, email, password);
        return (Admin) userRepository.save(admin);
    }

    @Override
    public boolean adminExists(String defaultAdminEmail) {
        return userRepository.findByEmail(defaultAdminEmail)
                .filter(user -> user instanceof Admin)
                .isPresent();
    }

    private void checkEmailExists(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new FoodDeliveryException(ErrorType.USER_ALREADY_EXISTS, "Email is already registered");
        }
    }
}