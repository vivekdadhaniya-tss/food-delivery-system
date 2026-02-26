package com.fooddeliveryapp.strategy.Impl;

public interface PaymentStrategy {
    boolean pay(double amount);
    String getPaymentType();
}
