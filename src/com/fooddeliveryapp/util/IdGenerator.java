package com.fooddeliveryapp.util;

import java.util.concurrent.atomic.AtomicInteger;

public final class IdGenerator {

    private static final AtomicInteger USER_ID = new AtomicInteger(1);
    private static final AtomicInteger RESTAURANT_ID = new AtomicInteger(1);
    private static final AtomicInteger AGENT_ID = new AtomicInteger(1);
    private static final AtomicInteger ORDER_ID = new AtomicInteger(1000);
    private static final AtomicInteger PAYMENT_ID = new AtomicInteger(5000);
    private static final AtomicInteger FOOD_ID = new AtomicInteger(101); // Added for Food Items

    private IdGenerator() {}

    public static int nextUserId() { return USER_ID.getAndIncrement(); }
    public static int nextRestaurantId() { return RESTAURANT_ID.getAndIncrement(); }
    public static int nextAgentId() { return AGENT_ID.getAndIncrement(); }
    public static String nextOrderNumber() { return "ORD-" + ORDER_ID.getAndIncrement(); }
    public static String nextPaymentId() { return "PAY-" + PAYMENT_ID.getAndIncrement(); }
    public static String nextFoodId() { return "F" + FOOD_ID.getAndIncrement(); } // Auto-assign Food ID
}