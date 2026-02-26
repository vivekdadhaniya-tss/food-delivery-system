package com.fooddeliveryapp.service;

import com.fooddeliveryapp.model.FoodItem;
import com.fooddeliveryapp.model.Restaurant;
import com.fooddeliveryapp.model.type.FoodCategory;

import java.util.List;
import java.util.Optional;

public interface RestaurantService {

    // RESTAURANT CRUD
    Restaurant addRestaurant(Restaurant restaurant);

    Restaurant updateRestaurant(Restaurant restaurant);

    void removeRestaurant(int restaurantId);

    Optional<Restaurant> getRestaurantById(int restaurantId);

    List<Restaurant> getAllRestaurants();

    List<Restaurant> getActiveRestaurants();

    // MENU OPERATIONS
    void addMenuItem(int restaurantId, FoodItem item);

    void updateMenuItem(int restaurantId, FoodItem item);

    void removeMenuItem(int restaurantId, String foodItemId);

    List<FoodItem> getMenu(int restaurantId);

    List<FoodItem> searchMenuItems(int restaurantId, String name, FoodCategory category);

    Optional<FoodItem> getMenuItemById(int restaurantId, String foodItemId);
}