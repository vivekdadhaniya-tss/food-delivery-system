package com.fooddeliveryapp.model;

import com.fooddeliveryapp.model.type.FoodCategory;

public final class FoodSearchResult {

    private final int restaurantId;
    private final String restaurantName;
    private final double restaurantRating;

    private final String foodItemId;
    private final String foodItemName;
    private final FoodCategory category;
    private final double price;
    private final int availableStock;

    public FoodSearchResult(
            int restaurantId,
            String restaurantName,
            double restaurantRating,
            String foodItemId,
            String foodItemName,
            FoodCategory category,
            double price,
            int availableStock) {

        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.restaurantRating = restaurantRating;
        this.foodItemId = foodItemId;
        this.foodItemName = foodItemName;
        this.category = category;
        this.price = price;
        this.availableStock = availableStock;
    }

    public int getRestaurantId() { return restaurantId; }
    public String getRestaurantName() { return restaurantName; }
    public double getRestaurantRating() { return restaurantRating; }
    public String getFoodItemId() { return foodItemId; }
    public String getFoodItemName() { return foodItemName; }
    public FoodCategory getCategory() { return category; }
    public double getPrice() { return price; }
    public int getAvailableStock() { return availableStock; }
}