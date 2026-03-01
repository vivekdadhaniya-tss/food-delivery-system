package com.fooddeliveryapp.service.Impl;

import com.fooddeliveryapp.exception.FoodDeliveryException;
import com.fooddeliveryapp.model.Cart;
import com.fooddeliveryapp.model.CartItem;
import com.fooddeliveryapp.model.Customer;
import com.fooddeliveryapp.model.MenuItem;
import com.fooddeliveryapp.model.User;
import com.fooddeliveryapp.repository.MenuItemRepository;
import com.fooddeliveryapp.repository.UserRepository;
import com.fooddeliveryapp.service.CartService;
import com.fooddeliveryapp.type.ErrorType;

public class CartServiceImpl implements CartService {

    private final UserRepository userRepository;
    private final MenuItemRepository menuItemRepository;

    public CartServiceImpl(UserRepository userRepository, MenuItemRepository menuItemRepository) {
        this.userRepository = userRepository;
        this.menuItemRepository = menuItemRepository;
    }

    // Helper method with strict type checking
    private Cart getCustomerCart(String customerId) {
        User user = userRepository.findById(customerId)
                .orElseThrow(() -> new FoodDeliveryException(ErrorType.RESOURCE_NOT_FOUND, "Customer not found"));

        if (!(user instanceof Customer)) {
            throw new FoodDeliveryException(ErrorType.CART_ERROR, "Invalid operation: User is not a customer");
        }

        return ((Customer) user).getActiveCart();
    }

    @Override
    public Cart getCart(String customerId) {
        return getCustomerCart(customerId);
    }

    @Override
    public void addItem(String customerId, String menuItemId, int quantity) {
        Cart cart = getCustomerCart(customerId);

        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new FoodDeliveryException(ErrorType.RESOURCE_NOT_FOUND, "Menu item not found"));

        if (!menuItem.isAvailable()) {
            throw new FoodDeliveryException(ErrorType.CART_ERROR, "Item is currently unavailable");
        }

        cart.getItems().stream()
                .filter(item -> item.getMenuItemId().equals(menuItemId))
                .findFirst()
                .ifPresentOrElse(
                        existingItem -> existingItem.setQuantity(existingItem.getQuantity() + quantity),
                        () -> cart.getItems().add(new CartItem(menuItem.getId(), menuItem.getName(), menuItem.getPrice(), quantity))
                );
    }

    @Override
    public void removeItem(String customerId, String menuItemId) {
        Cart cart = getCustomerCart(customerId);
        boolean removed = cart.getItems().removeIf(item -> item.getMenuItemId().equals(menuItemId));

        if (!removed) {
            throw new FoodDeliveryException(ErrorType.CART_ERROR, "Item not found in cart");
        }
    }

    @Override
    public void updateItemQuantity(String customerId, String menuItemId, int quantity) {
        Cart cart = getCustomerCart(customerId);
        cart.getItems().stream()
                .filter(item -> item.getMenuItemId().equals(menuItemId))
                .findFirst()
                .ifPresentOrElse(
                        existing -> existing.setQuantity(quantity),
                        () -> { throw new FoodDeliveryException(ErrorType.CART_ERROR, "Item not found in cart"); }
                );
    }

    @Override
    public void clearCart(String customerId) {
        getCustomerCart(customerId).clear();
    }

    @Override
    public double getTotalPrice(String customerId) {
        // Uses the getSubTotal() method already built in the Cart model --> Delegation
        return getCustomerCart(customerId).getSubTotal();
    }

    @Override
    public int getTotalItems(String customerId) {
        // Uses the getTotalItems() method already built in the Cart model --> Delegation
        return getCustomerCart(customerId).getTotalItems();
    }
}