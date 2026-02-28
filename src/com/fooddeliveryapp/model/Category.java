package com.fooddeliveryapp.model;

import java.util.Objects;

public class Category {

    private final String id;
    private String name;
    private boolean active;

    public Category(String id, String name) {
        this.id = id;
        this.name = validateName(name);
        this.active = true;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public boolean isActive() {
        return active;
    }


    public void rename(String newName) {
        this.name = validateName(newName);
    }
    public void activate() {
        this.active = true;
    }
    public void deactivate() {
        this.active = false;
    }


    private String validateName(String name) {
        Objects.requireNonNull(name, "Category name cannot be null");

        if (name.isBlank()) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }

        return name.trim();
    }
}