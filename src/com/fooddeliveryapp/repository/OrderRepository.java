package com.fooddeliveryapp.repository;

import com.fooddeliveryapp.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findAll();

    List<Order> findByCustomerId(int customerId);

    List<Order> findByRestaurantId(int restaurantId);

    void deleteByOrderNumber(String orderNumber);
}