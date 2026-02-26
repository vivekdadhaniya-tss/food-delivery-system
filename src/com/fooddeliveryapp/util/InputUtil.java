package com.fooddeliveryapp.util;

import java.util.Scanner;
import java.util.regex.Pattern;

public class InputUtil {

    private static final Scanner scan = new Scanner(System.in);

    private InputUtil() {}

    public static int getInt(String prompt) {
        int value;
        while (true) {
            System.out.print(prompt);
            try {
                value = Integer.parseInt(scan.nextLine());  // prevention: immediately reads the leftover newline (not used scan.nextInt())
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid integer. Please try again.");
            }
        }
        return value;
    }

    public static double getDouble(String prompt) {
        double value;
        while (true) {
            System.out.print(prompt);
            try {
                value = Double.parseDouble(scan.nextLine());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please try again.");
            }
        }
        return value;
    }

    public static String getString(String prompt) {
        String value;
        while (true) {
            System.out.print(prompt);
            value = scan.nextLine().trim();
            if (!value.isEmpty()) {
                break;
            }  else {
                System.out.println("Input cannot be empty. Please try again.");
            }
        }
        return value;
    }

    public static String getStringPattern(String prompt, String regex, String errorMsg) {
        String value;
        while (true) {
            System.out.print(prompt);
            value = scan.nextLine().trim();
            if (Pattern.matches(regex, value)) {
                break;
            } else  {
                System.out.println(errorMsg);
            }
        }
        return value;
    }

    public static int getChoice(String prompt, int min, int max) {
        int choice;
        while (true) {
            choice = getInt(prompt);
            if (choice >= min || choice <= max) break;
            System.out.println("Choice must be between " + min + " and " + max);
        }
        return choice;
    }

    public static String getUPI(String prompt) {
        return getStringPattern(prompt,
                "[\\w.-]+@[\\w]+",
                "Invalid UPI ID. Format example: abc@upi");
    }
}
