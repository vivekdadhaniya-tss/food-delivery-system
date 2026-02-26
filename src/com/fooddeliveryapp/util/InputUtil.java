package com.fooddeliveryapp.util;

import java.io.File;
import java.util.Scanner;
import java.util.regex.Pattern;

public class InputUtil {

    private static Scanner scan;
    private static boolean readingFromFile = false;

    static {
        try {
            File file = new File("input.txt");
            if (file.exists()) {
                scan = new Scanner(file);
                readingFromFile = true;
                System.out.println("--- Reading inputs from input.txt ---");
            } else {
                scan = new Scanner(System.in);
            }
        } catch (Exception e) {
            scan = new Scanner(System.in);
        }
    }

    private InputUtil() {}

    // Safer method to switch scanners
    private static String getNextLine() {
        if (readingFromFile) {
            if (scan.hasNextLine()) {
                String line = scan.nextLine();
                System.out.println(line);
                return line;
            } else {
                // File is empty, switch to console
                System.out.println("\n--- Finished reading from input.txt. Switching to manual input. ---");
                scan.close();
                scan = new Scanner(System.in);
                readingFromFile = false;
            }
        }
        // Read from console
        return scan.nextLine();
    }

    public static int getInt(String prompt) {
        int value;
        while (true) {
            System.out.print(prompt);
            try {
                value = Integer.parseInt(getNextLine());
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
                value = Double.parseDouble(getNextLine());
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
            value = getNextLine().trim();
            if (!value.isEmpty()) {
                break;
            } else {
                System.out.println("Input cannot be empty. Please try again.");
            }
        }
        return value;
    }

    public static String getStringPattern(String prompt, String regex, String errorMsg) {
        String value;
        while (true) {
            System.out.print(prompt);
            value = getNextLine().trim();
            if (java.util.regex.Pattern.matches(regex, value)) {
                break;
            } else {
                System.out.println(errorMsg);
            }
        }
        return value;
    }

    public static int getChoice(String prompt, int min, int max) {
        int choice;
        while (true) {
            choice = getInt(prompt);
            if (choice >= min && choice <= max) break;
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