package com.fooddeliveryapp.service.Impl;

import com.fooddeliveryapp.model.DeliveryAgent;
import com.fooddeliveryapp.model.User;
import com.fooddeliveryapp.repository.DeliveryAgentRepository;
import com.fooddeliveryapp.repository.UserRepository;
import com.fooddeliveryapp.service.UserService;
import com.fooddeliveryapp.type.Role;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final DeliveryAgentRepository deliveryAgentRepository;

    public UserServiceImpl(UserRepository userRepository, DeliveryAgentRepository deliveryAgentRepository) {
        this.userRepository = userRepository;
        this.deliveryAgentRepository = deliveryAgentRepository;
    }

    @Override
    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public boolean existsById(String id) {
        return userRepository.existsById(id);
    }

    @Override
    public void deleteUserById(String id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<User> getUsersByRole(Role role) {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == role)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeliveryAgent> getAllDeliveryAgents() {
        return deliveryAgentRepository.findAll();
    }

    @Override
    public List<DeliveryAgent> getAvailableDeliveryAgents() {
        return deliveryAgentRepository.findAvailableAgents();
    }

    @Override
    public Optional<DeliveryAgent> getNextAvailableDeliveryAgent() {
        return deliveryAgentRepository.findAvailableAgents().stream().findFirst();
    }
}