package com.fooddeliveryapp.model;

import com.fooddeliveryapp.model.type.FoodCategory;

import java.util.Objects;

public class FoodItem {

    private final String id;
    private String name;
    private double price;
    private int stock;
    private FoodCategory category;

    public FoodItem(String id, String name,
                    double price,
                    int stock,
                    FoodCategory category) {

        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.category = category;
    }

    // getters
    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    public FoodCategory getCategory() { return category; }

    // setters
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setStock(int stock) { this.stock = stock; }
    public void setCategory(FoodCategory category) {
        this.category = category;
    }

    // equals based only on id
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FoodItem that)) return false;
        return Objects.equals(id, that.id);
    }
}