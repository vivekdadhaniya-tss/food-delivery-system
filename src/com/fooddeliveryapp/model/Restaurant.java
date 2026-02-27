package com.fooddeliveryapp.model;

import com.fooddeliveryapp.model.type.FoodCategory;

import java.util.List;
import java.util.Map;

public class Restaurant {

    private final int id;
    private String name;
    private boolean active;

    // only data storage
    private Map<FoodCategory, List<FoodItem>> menu;
    private List<Integer> ratings;
    private int totalOrders;

    public Restaurant(int id,
                      String name,
                      boolean active,
                      Map<FoodCategory, List<FoodItem>> menu,
                      List<Integer> ratings,
                      int totalOrders) {
        this.id = id;
        this.name = name;
        this.active = active;
        this.menu = menu;
        this.ratings = ratings;
        this.totalOrders = totalOrders;
    }

    // getters
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public boolean isActive() {
        return active;
    }
    public Map<FoodCategory, List<FoodItem>> getMenu() {
        return menu;
    }
    public List<Integer> getRatings() {
        return ratings;
    }
    public int getTotalOrders() {
        return totalOrders;
    }

    // setters
    public void setName(String name) {
        this.name = name;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    public void setMenu(Map<FoodCategory, List<FoodItem>> menu) {
        this.menu = menu;
    }
    public void setRatings(List<Integer> ratings) {
        this.ratings = ratings;
    }
    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Restaurant that)) return false;
        return id == that.id;
    }

}