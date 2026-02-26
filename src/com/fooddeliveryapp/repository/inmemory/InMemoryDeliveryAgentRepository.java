package com.fooddeliveryapp.repository.inmemory;

import com.fooddeliveryapp.model.DeliveryAgent;
import com.fooddeliveryapp.repository.DeliveryAgentRepository;

import java.util.*;

public class InMemoryDeliveryAgentRepository implements DeliveryAgentRepository {

    private final Map<Integer, DeliveryAgent> agents = new HashMap<>();

    @Override
    public DeliveryAgent save(DeliveryAgent agent) {
        agents.put(agent.getId(), agent);
        return agent;
    }

    @Override
    public Optional<DeliveryAgent> findById(int id) {
        return Optional.ofNullable(agents.get(id));
    }

    @Override
    public List<DeliveryAgent> findAll() {
        return new ArrayList<>(agents.values());
    }

    @Override
    public List<DeliveryAgent> findAvailableAgents() {
        return agents.values()
                .stream()
                .filter(DeliveryAgent::isAvailable)
                .toList();
    }

    @Override
    public void deleteById(int id) {
        agents.remove(id);
    }
}