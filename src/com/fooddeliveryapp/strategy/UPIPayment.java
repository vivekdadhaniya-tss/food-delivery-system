package com.fooddeliveryapp.strategy;

import com.fooddeliveryapp.strategy.Impl.PaymentStrategy;

public class UPIPayment implements PaymentStrategy {

    private final String upiId;

    public UPIPayment(String upiId) {
        this.upiId = upiId;
    }

    @Override
    public boolean pay(double amount) {

        System.out.println("Processing UPI payment of ₹" + amount);
        System.out.println("UPI ID: " + upiId);

        // simulate success
        return true;
    }

    @Override
    public String getPaymentType() {
        return "UPI";
    }
}