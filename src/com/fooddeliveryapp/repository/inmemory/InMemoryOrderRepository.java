package com.fooddeliveryapp.repository.inmemory;

import com.fooddeliveryapp.model.Order;
import com.fooddeliveryapp.repository.OrderRepository;

import java.util.*;

public class InMemoryOrderRepository implements OrderRepository {

    private final Map<String, Order> orders = new HashMap<>();

    @Override
    public Order save(Order order) {
        orders.put(order.getOrderNumber(), order);
        return order;
    }

    @Override
    public Optional<Order> findByOrderNumber(String orderNumber) {
        return Optional.ofNullable(orders.get(orderNumber));
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }

    @Override
    public List<Order> findByCustomerId(int customerId) {
        return orders.values()
                .stream()
                .filter(o -> o.getCustomerId() == customerId)
                .toList();
    }

    @Override
    public List<Order> findByRestaurantId(int restaurantId) {
        return orders.values()
                .stream()
                .filter(o -> o.getRestaurantId() == restaurantId)
                .toList();
    }

    @Override
    public void deleteByOrderNumber(String orderNumber) {
        orders.remove(orderNumber);
    }
}