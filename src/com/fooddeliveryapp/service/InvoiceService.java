package com.fooddeliveryapp.service;

import com.fooddeliveryapp.model.Order;

public interface InvoiceService {
    String generateInvoice(Order order);
}