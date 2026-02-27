package com.fooddeliveryapp.model;

import com.fooddeliveryapp.model.type.Role;

import java.util.ArrayList;
import java.util.List;

public class Customer extends User {

    private String address;
    private Cart activeCart;
    private List<Order> orderHistory = new ArrayList<>();

    public Customer(int id, String name, String phone, String email, String address, String password) {
        super(id, name, phone, email, password);
        this.address = address;
        this.activeCart = new Cart(this);
    }

    @Override
    public Role getRole() {
        return Role.CUSTOMER;
    }
    public Cart getActiveCart() {
        return activeCart;
    }
    public List<Order> getOrderHistory() {
        return List.copyOf(orderHistory);
    }

    public void addOrder(Order order) {
        orderHistory.add(order);
    }
}