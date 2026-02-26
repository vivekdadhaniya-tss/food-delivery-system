package com.fooddeliveryapp.model;

public class OrderItem {

    private final String foodItemId;
    private final String foodItemName;
    private final double priceAtPurchase;
    private final int quantity;

    public OrderItem(String foodItemId,
                     String foodItemName,
                     double priceAtPurchase,
                     int quantity) {

        this.foodItemId = foodItemId;
        this.foodItemName = foodItemName;
        this.priceAtPurchase = priceAtPurchase;
        this.quantity = quantity;
    }

    public String getFoodItemId() { return foodItemId; }
    public String getFoodItemName() { return foodItemName; }
    public double getPriceAtPurchase() { return priceAtPurchase; }
    public int getQuantity() { return quantity; }
}