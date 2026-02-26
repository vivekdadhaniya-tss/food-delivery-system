package com.fooddeliveryapp.strategy.Impl;

import com.fooddeliveryapp.model.Order;

public interface DiscountStrategy  {

    double calculateDiscount(Order order);
}
