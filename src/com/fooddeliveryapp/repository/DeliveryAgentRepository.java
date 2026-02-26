package com.fooddeliveryapp.repository;

import com.fooddeliveryapp.model.DeliveryAgent;

import java.util.List;
import java.util.Optional;

public interface DeliveryAgentRepository {

    DeliveryAgent save(DeliveryAgent agent);

    Optional<DeliveryAgent> findById(int id);

    List<DeliveryAgent> findAll();

    List<DeliveryAgent> findAvailableAgents();

    void deleteById(int id);
}