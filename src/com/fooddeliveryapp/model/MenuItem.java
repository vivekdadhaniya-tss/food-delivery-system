package com.fooddeliveryapp.model;

public class MenuItem {

    private final String id;
    private String name;
    private double price;
    private String categoryId;
    private boolean available;

    public MenuItem(String id, String name, double price, String categoryId) {

        this.id = validateId(id);
        this.name = validateName(name);
        this.price = validatePrice(price);
        this.categoryId = validateCategory(categoryId);
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
        this.price = validatePrice(newPrice);
    }
    public void rename(String newName) {
        this.name = validateName(newName);
    }
    public void changeAvailability(boolean status) {
        this.available = status;
    }
    public void changeCategory(String newCategoryId) {
        this.categoryId = validateCategory(newCategoryId);
    }



    private String validateId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("MenuItem id cannot be empty");
        }
        return id;
    }
    private String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("MenuItem name cannot be empty");
        }
        return name.trim();
    }
    private double validatePrice(double price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }
        return price;
    }
    private String validateCategory(String categoryId) {
        if (categoryId == null || categoryId.isBlank()) {
            throw new IllegalArgumentException("CategoryId cannot be empty");
        }
        return categoryId;
    }
}