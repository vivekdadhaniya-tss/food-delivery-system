package com.fooddeliveryapp.repository.inmemory;

import com.fooddeliveryapp.model.DeliveryAgent;
import com.fooddeliveryapp.repository.DeliveryAgentRepository;
import com.fooddeliveryapp.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryDeliveryAgentRepository implements DeliveryAgentRepository {

    private final UserRepository userRepository;

    public InMemoryDeliveryAgentRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<DeliveryAgent> findById(String id) {
        return userRepository.findById(id)
                .filter(user -> user instanceof DeliveryAgent)
                .map(user -> (DeliveryAgent) user);
    }

    @Override
    public Optional<DeliveryAgent> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .filter(user -> user instanceof DeliveryAgent)
                .map(user -> (DeliveryAgent) user);
    }

    @Override
    public List<DeliveryAgent> findAll() {
        return userRepository.findAll()
                .stream()
                .filter(user -> user instanceof DeliveryAgent)
                .map(user -> (DeliveryAgent) user)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeliveryAgent> findAvailableAgents() {
        return findAll()
                .stream()
                .filter(DeliveryAgent::isAvailable)
                .collect(Collectors.toList());
    }
}