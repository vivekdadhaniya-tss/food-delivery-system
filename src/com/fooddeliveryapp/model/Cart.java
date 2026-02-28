package com.fooddeliveryapp.model;

import com.fooddeliveryapp.exception.CartOperationException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Cart {

    private final String customerId;
    private List<CartItem> items = new ArrayList<>();

    public Cart(String  customerId) {
        this.customerId = customerId;
    }

    public void clear() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public String getCustomerId() {
        return customerId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public int getTotalItems() {
        return items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    public double getSubTotal() {
        return items.stream()
                .mapToDouble(CartItem::getLineTotal)
                .sum();
    }

}