package com.fooddeliveryapp.model;

import com.fooddeliveryapp.model.type.Role;

public class DeliveryAgent extends User {

    private boolean available;
    private double averageRating;
    private int totalRatings;
    private int totalDeliveries;

    public DeliveryAgent(String id, String name, String phone, String email, String password) {
        super(id, name, phone, email, password);
        this.available = true;
        this.averageRating = 0.0;
        this.totalRatings = 0;
        this.totalDeliveries = 0;
    }

    @Override
    public Role getRole() {
        return Role.DELIVERY_AGENT;
    }



    public boolean isAvailable() {
        return available;
    }

    public double getRating() {
        return averageRating;
    }

    public int getTotalRatings() {
        return totalRatings;
    }

    public int getTotalDeliveries() {
        return totalDeliveries;
    }


    public void addRating(double rating) {
        if (rating < 1.0 || rating > 5.0) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        double totalScore = this.averageRating * this.totalRatings;
        totalScore += rating;

        this.totalRatings++;
        this.averageRating = totalScore / this.totalRatings;
    }

    public void incrementDeliveries() {
        this.totalDeliveries++;
    }
    public void markBusy() {
        this.available = false;
    }
    public void markAvailable() {
        this.available = true;
    }
    public void setAvailability(boolean available) {
        this.available = available;
    }
}