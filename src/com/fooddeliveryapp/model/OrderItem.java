package com.fooddeliveryapp.model;

import java.math.BigDecimal;
import java.util.Objects;

public class OrderItem {

    private final String menuItemId;
    private final String menuItemName;
    private final double priceAtPurchase;
    private final int quantity;

    public OrderItem(String menuItemId, String menuItemName, double priceAtPurchase, int quantity) {
        this.menuItemId = menuItemId;
        this.menuItemName = menuItemName;
        this.priceAtPurchase = priceAtPurchase;
        this.quantity = quantity;
    }

    public String getFoodItemId() {
        return menuItemId;
    }

    public String getFoodItemName() {
        return menuItemName;
    }

    public double getPriceAtPurchase() {
        return priceAtPurchase;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getLineTotal() {
        return priceAtPurchase * quantity;
    }
}