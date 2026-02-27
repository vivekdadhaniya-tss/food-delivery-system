package com.fooddeliveryapp.model;

import com.fooddeliveryapp.model.type.Role;

public class DeliveryAgent extends User {

    private boolean available;
    private double rating;
    private int totalDeliveries;

    public DeliveryAgent(int id,
                         String name,
                         String phone,
                         String email,
                         String password) {
        super(id, name, phone, email, password);
        this.available = true;
        this.rating = 0.0;
        this.totalDeliveries = 0;
    }

    @Override
    public Role getRole() {
        return Role.DELIVERY_AGENT;
    }

    // getters
    public boolean isAvailable() {
        return available;
    }
    public double getRating() {
        return rating;
    }
    public int getTotalDeliveries() {
        return totalDeliveries;
    }

    // setters
    public void setAvailable(boolean available) {
        this.available = available;
    }
    public void setRating(double rating) {
        this.rating = rating;
    }
    public void setTotalDeliveries(int totalDeliveries) {
        this.totalDeliveries = totalDeliveries;
    }
}