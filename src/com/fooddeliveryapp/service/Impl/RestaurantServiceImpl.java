package com.fooddeliveryapp.service.Impl;

import com.fooddeliveryapp.model.FoodItem;
import com.fooddeliveryapp.model.Restaurant;
import com.fooddeliveryapp.model.type.FoodCategory;
import com.fooddeliveryapp.repository.RestaurantRepository;
import com.fooddeliveryapp.service.RestaurantService;

import java.util.*;
import java.util.stream.Collectors;

public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public RestaurantServiceImpl(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    // RESTAURANT CRUD
    @Override
    public Restaurant addRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    @Override
    public Restaurant updateRestaurant(Restaurant restaurant) {
        if (!restaurantRepository.existsById(restaurant.getId())) {
            throw new NoSuchElementException("Restaurant not found with ID: " + restaurant.getId());
        }
        return restaurantRepository.save(restaurant);
    }

    @Override
    public void removeRestaurant(int restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new NoSuchElementException("Restaurant not found with ID: " + restaurantId);
        }
        restaurantRepository.deleteById(restaurantId);
    }

    @Override
    public Optional<Restaurant> getRestaurantById(int restaurantId) {
        return restaurantRepository.findById(restaurantId);
    }

    @Override
    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    @Override
    public List<Restaurant> getActiveRestaurants() {
        return restaurantRepository.findAll().stream()
                .filter(Restaurant::isActive)
                .collect(Collectors.toList());
    }

    // MENU OPERATIONS
    @Override
    public void addMenuItem(int restaurantId, FoodItem item) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new NoSuchElementException("Restaurant not found"));
        restaurant.getMenu().computeIfAbsent(item.getCategory(), k -> new ArrayList<>()).add(item);
        restaurantRepository.save(restaurant);
    }

    @Override
    public void updateMenuItem(int restaurantId, FoodItem item) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new NoSuchElementException("Restaurant not found"));
        List<FoodItem> categoryItems = restaurant.getMenu().get(item.getCategory());
        if (categoryItems == null) {
            throw new NoSuchElementException("Category not found in menu");
        }

        // Remove old item with same ID and add updated
        categoryItems.removeIf(i -> i.getId().equals(item.getId()));
        categoryItems.add(item);
        restaurantRepository.save(restaurant);
    }

    @Override
    public void removeMenuItem(int restaurantId, String foodItemId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new NoSuchElementException("Restaurant not found"));

        restaurant.getMenu().values().forEach(list -> list.removeIf(i -> i.getId().equals(foodItemId)));

        restaurantRepository.save(restaurant);
    }

    @Override
    public List<FoodItem> getMenu(int restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .map(r -> r.getMenu().values().stream()
                        .flatMap(List::stream)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    @Override
    public List<FoodItem> searchMenuItems(int restaurantId, String name, FoodCategory category) {
        return restaurantRepository.findById(restaurantId)
                .map(r -> r.getMenu().entrySet().stream()
                        .filter(e -> category == null || e.getKey() == category)
                        .flatMap(e -> e.getValue().stream())
                        .filter(f -> name == null || f.getName().toLowerCase().contains(name.toLowerCase()))
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    @Override
    public Optional<FoodItem> getMenuItemById(int restaurantId, String foodItemId) {
        return restaurantRepository.findById(restaurantId)
                .flatMap(r -> r.getMenu().values().stream()
                        .flatMap(List::stream)
                        .filter(f -> f.getId().equals(foodItemId))
                        .findFirst());
    }
}