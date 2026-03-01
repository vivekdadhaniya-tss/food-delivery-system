package com.fooddeliveryapp.service;

import com.fooddeliveryapp.model.User;
import com.fooddeliveryapp.type.Role;

public interface AuthService {

    // Customer registration
    User registerCustomer(String name,
                          String phone,
                          String email,
                          String password,
                          String address);

    // Admin or DeliveryAgent registration
    User registerUser(String name,
                      String phone,
                      String email,
                      String password,
                      Role role);

    // Login
    User login(String email, String password);
}