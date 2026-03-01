package com.fooddeliveryapp.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class FormatUtil {

    // Define the standard date format for the whole app (e.g., "01-Mar-2026 14:30")
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");

    private FormatUtil() {
        // Prevent instantiation of utility class
    }

    public static String formatCurrency(double amount) {
        return String.format("₹%.2f", amount);
    }

    public static String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "N/A";
        }
        return dateTime.format(DATE_FORMATTER);
    }
}