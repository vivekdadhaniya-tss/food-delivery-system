package com.fooddeliveryapp.model;

import com.fooddeliveryapp.exception.CartOperationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Cart {
    private final Customer customer;
    private Restaurant restaurant;
    private final List<OrderItem> items = new ArrayList<>();

    public Cart(Customer customer) {
        this.customer = Objects.requireNonNull(customer);
    }

    public void addItem(Restaurant restaurant, String foodItemId, String foodItemName, double price, int quantity) {
        validateRestaurant(restaurant);
        validateQuantity(quantity);

        // FIX: Assign the restaurant if the cart is currently empty
        if (this.restaurant == null) {
            this.restaurant = restaurant;
        }

        Optional<OrderItem> existingItem = items.stream()
                .filter(i -> i.getFoodItemId().equals(foodItemId))
                .findFirst();

        if (existingItem.isPresent()) {
            OrderItem oldItem = existingItem.get();
            int newQty = oldItem.getQuantity() + quantity;
            items.remove(oldItem);
            items.add(new OrderItem(foodItemId, foodItemName, price, newQty));
        } else {
            items.add(new OrderItem(foodItemId, foodItemName, price, quantity));
        }
    }

    public void removeItem(String foodItemId) {
        items.removeIf(i -> i.getFoodItemId().equals(foodItemId));
        if (items.isEmpty()) {
            restaurant = null; // Reset when empty
        }
    }

    public void clear() {
        items.clear();
        restaurant = null;
    }

    public double getSubTotal() {
        return items.stream().mapToDouble(i -> i.getPriceAtPurchase() * i.getQuantity()).sum();
    }

    public boolean isEmpty() { return items.isEmpty(); }
    public int getTotalItems() { return items.stream().mapToInt(OrderItem::getQuantity).sum(); }
    public Customer getCustomer() { return customer; }
    public Restaurant getRestaurant() { return restaurant; }
    public List<OrderItem> getItems() { return List.copyOf(items); }

    private void validateRestaurant(Restaurant newRestaurant) {
        if (restaurant != null && restaurant.getId() != newRestaurant.getId()) {
            throw new CartOperationException("Cart can contain items from only one restaurant. Please clear your cart first.");
        }
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) throw new CartOperationException("Quantity must be greater than zero");
    }
}