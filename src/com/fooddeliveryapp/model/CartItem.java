package com.fooddeliveryapp.model;

public class CartItem {

    private String menuItemId;
    private String menuItemName;
    private double priceAtAddTime;
    private int quantity;

    public CartItem(String menuItemId, String menuItemName, double priceAtAddTime, int quantity) {
        this.menuItemId = menuItemId;
        this.menuItemName = menuItemName;
        this.priceAtAddTime = priceAtAddTime;
        this.quantity = quantity;
    }

    public double getLineTotal() {
        return priceAtAddTime * quantity;
    }

    public String getMenuItemId() {
        return menuItemId;
    }

    public String getMenuItemName() {
        return menuItemName;
    }

    public double getPriceAtAddTime() {
        return priceAtAddTime;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be greater than zero");
        this.quantity = quantity;
    }
}
