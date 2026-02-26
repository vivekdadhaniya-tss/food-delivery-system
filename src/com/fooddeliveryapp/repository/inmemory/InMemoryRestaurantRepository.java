package com.fooddeliveryapp.repository.inmemory;

import com.fooddeliveryapp.model.Restaurant;
import com.fooddeliveryapp.repository.RestaurantRepository;

import java.util.*;

public class InMemoryRestaurantRepository implements RestaurantRepository {

    private final Map<Integer, Restaurant> restaurants = new HashMap<>();

    @Override
    public Restaurant save(Restaurant restaurant) {
        restaurants.put(restaurant.getId(), restaurant);
        return restaurant;
    }

    @Override
    public Optional<Restaurant> findById(int id) {
        return Optional.ofNullable(restaurants.get(id));
    }

    @Override
    public Optional<Restaurant> findByExactName(String name) {
        return restaurants.values()
                .stream()
                .filter(r -> r.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    @Override
    public List<Restaurant> searchByName(String keyword) {
        return restaurants.values()
                .stream()
                .filter(r -> r.getName().toLowerCase()
                        .contains(keyword.toLowerCase()))
                .toList();
    }

    @Override
    public List<Restaurant> findAll() {
        return new ArrayList<>(restaurants.values());
    }

    @Override
    public void deleteById(int id) {
        restaurants.remove(id);
    }

    @Override
    public boolean existsById(int id) {
        return restaurants.containsKey(id);
    }
}