package com.fooddeliveryapp.strategy;

import com.fooddeliveryapp.model.Order;
import com.fooddeliveryapp.strategy.Impl.DiscountStrategy;

public class FlatDiscount implements DiscountStrategy {

    private final double threshold;      // Minimum subtotal to apply discount
    private final double discountAmount; // Fixed discount amount

    public FlatDiscount(double threshold, double discountAmount) {
        this.threshold = threshold;
        this.discountAmount = discountAmount;
    }

    @Override
    public double calculateDiscount(Order order) {
        double subTotal = order.getItems()
                .stream()
                .mapToDouble(i -> i.getPriceAtPurchase() * i.getQuantity())
                .sum();

        if (subTotal >= threshold) {
            return discountAmount;
        }
        return 0;
    }
}