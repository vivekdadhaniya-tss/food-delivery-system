package com.fooddeliveryapp.service;

import com.fooddeliveryapp.model.DeliveryAgent;
import com.fooddeliveryapp.model.User;
import com.fooddeliveryapp.type.Role;

import java.util.List;
import java.util.Optional;

public interface UserService {

    // ===== Generic User CRUD =====
    User createUser(User user);
    Optional<User> getUserById(int id);
    List<User> getAllUsers();
    void deleteUserById(int id);

    boolean existsById(int id);
    Optional<User> getUserByEmail(String email);

    // ===== Role-based Queries =====
    List<User> getUsersByRole(Role role);

    // ===== Delivery Agent Specific =====
    List<DeliveryAgent> getAllDeliveryAgents();
    List<DeliveryAgent> getAvailableDeliveryAgents();
    Optional<DeliveryAgent> getNextAvailableDeliveryAgent(); // first available strategy

}