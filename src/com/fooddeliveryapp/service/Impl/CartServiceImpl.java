package com.fooddeliveryapp.service.Impl;

import com.fooddeliveryapp.exception.CartOperationException;
import com.fooddeliveryapp.model.Cart;
import com.fooddeliveryapp.model.CartItem;

public class CartServiceImpl {

    public void addItem(Cart cart, CartItem cartItem) {
        cart.getItems().stream()
                .filter(item -> item.getMenuItemId().equals(cartItem.getMenuItemId()))
                .findFirst()
                .ifPresentOrElse(
                        existingItem -> existingItem.setQuantity(existingItem.getQuantity() + cartItem.getQuantity()),
                        () -> cart.getItems().add(cartItem)
                );
    }

    public void removeItem(Cart cart, String menuItemId) {
        cart.getItems().removeIf(item -> item.getMenuItemId().equals(menuItemId));
    }

    public void updateItemQuantity(Cart cart, String menuItemId, int quantity) {
        cart.getItems().stream()
                .filter(item -> item.getMenuItemId().equals(menuItemId))
                .findFirst()
                .ifPresentOrElse(
                        existing -> existing.setQuantity(quantity),
                        () -> { throw new CartOperationException("Item not found in cart"); }
                );
    }

    public void clearCart(Cart cart) {
        cart.clear();
    }

    public double getTotalPrice(Cart cart) {
        return cart.getSubTotal();
    }

    public int getTotalItems(Cart cart) {
        return cart.getTotalItems();
    }

}
