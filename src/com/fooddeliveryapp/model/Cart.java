package com.fooddeliveryapp.model;

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

    // add item
    public void addItem(Restaurant restaurant,
                        String foodItemId,
                        String foodItemName,
                        double price,
                        int quantity) {

        validateRestaurant(restaurant);
        validateQuantity(quantity);

        Optional<OrderItem> existingItem = items.stream()
                .filter(i -> i.getFoodItemId().equals(foodItemId))
                .findFirst();

        if (existingItem.isPresent()) {

            OrderItem oldItem = existingItem.get();
            int newQty = oldItem.getQuantity() + quantity;

            items.remove(oldItem);
            items.add(new OrderItem(
                    foodItemId,
                    foodItemName,
                    price,
                    newQty
            ));

        } else {

            items.add(new OrderItem(
                    foodItemId,
                    foodItemName,
                    price,
                    quantity
            ));
        }
    }

    // remove item
    public void removeItem(String foodItemId) {

        items.removeIf(i -> i.getFoodItemId().equals(foodItemId));

        if (items.isEmpty()) {
            restaurant = null;
        }
    }

    // clear cart
    public void clear() {
        items.clear();
        restaurant = null;
    }

    // calculations
    public double getSubTotal() {
        return items.stream()
                .mapToDouble(i -> i.getPriceAtPurchase() * i.getQuantity())
                .sum();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public int getTotalItems() {
        return items.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }

    // validations
    private void validateRestaurant(Restaurant newRestaurant) {

        if (restaurant == null) {
            restaurant = newRestaurant;
            return;
        }

        if (restaurant.getId() != newRestaurant.getId()) {
            throw new IllegalStateException(
                    "Cart can contain items from only one restaurant"
            );
        }
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }
    }

    // getters
    public Customer getCustomer() {
        return customer;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public List<OrderItem> getItems() {
        return List.copyOf(items);
    }
}