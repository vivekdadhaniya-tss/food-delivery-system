package com.fooddeliveryapp.strategy.Impl;

import com.fooddeliveryapp.type.PaymentMode;

public interface PaymentStrategy {
    boolean pay(double amount);
    PaymentMode getPaymentType();
}
