package com.fooddeliveryapp.service.Impl;

import com.fooddeliveryapp.exception.FoodDeliveryException;
import com.fooddeliveryapp.model.Admin;
import com.fooddeliveryapp.model.Customer;
import com.fooddeliveryapp.model.DeliveryAgent;
import com.fooddeliveryapp.model.User;
import com.fooddeliveryapp.repository.UserRepository;
import com.fooddeliveryapp.service.AuthService;
import com.fooddeliveryapp.type.ErrorType;
import com.fooddeliveryapp.type.IdType;
import com.fooddeliveryapp.util.IdGenerator;

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
        checkEmailExists(email);
        Customer customer = new Customer(IdGenerator.generate(IdType.USER), name, phone, email, address, password);
        return (Customer) userRepository.save(customer);
    }

    @Override
    public DeliveryAgent registerDeliveryAgent(String name, String phone, String email, String password) {
        checkEmailExists(email);
        DeliveryAgent agent = new DeliveryAgent(IdGenerator.generate(IdType.USER), name, phone, email, password);
        return (DeliveryAgent) userRepository.save(agent);
    }

    @Override
    public Admin registerAdmin(String name, String phone, String email, String password) {
        checkEmailExists(email);
        Admin admin = new Admin(IdGenerator.generate(IdType.USER), name, phone, email, password);
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