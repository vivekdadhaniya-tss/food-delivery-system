package com.fooddeliveryapp.exception;

public class UserAlreadyExistsException extends FoodDeliveryException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}