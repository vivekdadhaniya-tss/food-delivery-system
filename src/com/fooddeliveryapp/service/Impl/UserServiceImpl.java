package com.fooddeliveryapp.service.Impl;

import com.fooddeliveryapp.exception.UserAlreadyExistsException;
import com.fooddeliveryapp.model.DeliveryAgent;
import com.fooddeliveryapp.model.User;
import com.fooddeliveryapp.model.type.Role;
import com.fooddeliveryapp.repository.UserRepository;
import com.fooddeliveryapp.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ===== Generic User CRUD =====
    @Override
    public User createUser(User user) {
        // Only allow one Admin
        if(user.getRole() == Role.ADMIN) {
            boolean adminExists = userRepository.findAll().stream()
                    .anyMatch(u -> u.getRole() == Role.ADMIN);
            if(adminExists) {
                throw new UserAlreadyExistsException("Admin already exists!");
            }
        }
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(int id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUserById(int id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean existsById(int id) {
        return userRepository.existsById(id);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findAll().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    // ===== Role-based Queries =====
    @Override
    public List<User> getUsersByRole(Role role) {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == role)
                .collect(Collectors.toList());
    }

    // ===== Delivery Agent Specific =====
    @Override
    public List<DeliveryAgent> getAllDeliveryAgents() {
        return userRepository.findAll().stream()
                .filter(u -> u instanceof DeliveryAgent)
                .map(u -> (DeliveryAgent) u)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeliveryAgent> getAvailableDeliveryAgents() {
        return getAllDeliveryAgents().stream()
                .filter(DeliveryAgent::isAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<DeliveryAgent> getNextAvailableDeliveryAgent() {
        return getAvailableDeliveryAgents().stream().findFirst();
    }
}