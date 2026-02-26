package com.fooddeliveryapp.exception;

public class ResourceNotFoundException extends FoodDeliveryException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}