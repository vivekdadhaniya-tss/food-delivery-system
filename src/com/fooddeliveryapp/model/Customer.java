package com.fooddeliveryapp.model;

import com.fooddeliveryapp.model.type.Role;

import java.util.ArrayList;
import java.util.List;

public class Customer extends User {

    private String address;
    private Cart activeCart;

    public Customer(String id, String name, String phone, String email, String address, String password) {
        super(id, name, phone, email, password);
        this.address = address;
        this.activeCart = new Cart(id);
    }

    @Override
    public Role getRole() {
        return Role.CUSTOMER;
    }

    public String getAddress() { return address; }
    public Cart getActiveCart() { return activeCart; }
}