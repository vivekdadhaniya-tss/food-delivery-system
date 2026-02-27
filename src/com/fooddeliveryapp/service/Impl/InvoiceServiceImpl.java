package com.fooddeliveryapp.service.Impl;

import com.fooddeliveryapp.model.Order;
import com.fooddeliveryapp.model.OrderItem;
import com.fooddeliveryapp.service.InvoiceService;

import java.time.format.DateTimeFormatter;

public class InvoiceServiceImpl implements InvoiceService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    @Override
    public String generateInvoice(Order order) {

        StringBuilder sb = new StringBuilder();
        sb.append("\n\n===== FOOD DELIVERY INVOICE =====\n");
        sb.append("Order Number: ").append(order.getOrderNumber()).append("\n");
        sb.append("Customer ID  : ").append(order.getCustomerId()).append("\n");
        sb.append("Restaurant ID: ").append(order.getRestaurantId()).append("\n");
        sb.append("Created At   : ").append(order.getCreatedAt().format(FORMATTER)).append("\n");
        sb.append("Delivered At : ").append(
                order.getDeliveredAt() != null ? order.getDeliveredAt().format(FORMATTER) : "Pending"
        ).append("\n\n");

        sb.append("Items:\n");
        sb.append("--------------------------------\n");

        for (OrderItem item : order.getItems()) {
            sb.append(item.getFoodItemName())
                    .append(" x")
                    .append(item.getQuantity())
                    .append(" @ ₹")
                    .append(item.getPriceAtPurchase())
                    .append(" = ₹")
                    .append(item.getPriceAtPurchase() * item.getQuantity())
                    .append("\n");
        }

        sb.append("--------------------------------\n");
        sb.append("Subtotal    : ₹").append(order.getSubTotal()).append("\n");
        sb.append("Discount    : ₹").append(order.getDiscount()).append("\n");
        sb.append("Tax         : ₹").append(order.getTax()).append("\n");
        sb.append("DeliveryFee : ₹").append(order.getDeliveryFee()).append("\n");
        sb.append("Final Total : ₹").append(order.getFinalAmount()).append("\n");
        sb.append("Payment Mode: ").append(
                order.getFinalAmount() > 0 ? "Paid" : "Pending"
        ).append("\n");
        sb.append("=================================\n");

        return sb.toString();
    }
}