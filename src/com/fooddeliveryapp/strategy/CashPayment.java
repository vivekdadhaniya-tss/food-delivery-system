package com.fooddeliveryapp.strategy;

import com.fooddeliveryapp.type.PaymentMode;
import com.fooddeliveryapp.strategy.Impl.PaymentStrategy;

public class CashPayment implements PaymentStrategy {

    @Override
    public boolean pay(double amount) {
        System.out.println("Collect ₹" + amount + " as Cash on Delivery");
        return true; // always success in simulation
    }

    @Override
    public PaymentMode getPaymentType() {
        return PaymentMode.CASH;
    }
}