package com.fooddeliveryapp.model;

public class MenuItem {

    private final String id;
    private String name;
    private double price;
    private String categoryId;
    private boolean available;

    public MenuItem(String id, String name, double price, String categoryId) {

        this.id = id;
        this.name = name;
        this.price = price;
        this.categoryId = categoryId;
        this.available = true;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public double getPrice() {
        return price;
    }
    public String getCategoryId() {
        return categoryId;
    }
    public boolean isAvailable() {
        return available;
    }



    public void updatePrice(double newPrice) {
        this.price = newPrice;
    }
    public void rename(String newName) {
        this.name = newName;
    }
    public void changeAvailability(boolean status) {
        this.available = status;
    }
    public void changeCategory(String newCategoryId) {
        this.categoryId = newCategoryId;
    }

}