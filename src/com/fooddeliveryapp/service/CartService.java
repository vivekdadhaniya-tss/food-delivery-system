package com.fooddeliveryapp.service;

import com.fooddeliveryapp.model.Cart;

public interface CartService {

    Cart getCart(String customerId);

    void addItem(String customerId, String menuItemId, int quantity);

    void removeItem(String customerId, String menuItemId);

    void updateItemQuantity(String customerId, String menuItemId, int quantity);

    void clearCart(String customerId);

    double getTotalPrice(String customerId);

    int getTotalItems(String customerId);
}