package com.fooddeliveryapp.type;

public enum IdType {

    USER("USR-", 1),
    CATEGORY("CAT-", 1),
    MENU_ITEM("ITEM-", 1),
    ORDER("ORD-", 1),
    PAYMENT("PAY-", 1);

    private final String prefix;
    private final int initialValue;

    IdType(String prefix, int initialValue) {
        this.prefix = prefix;
        this.initialValue = initialValue;
    }

    public String getPrefix() {
        return prefix;
    }

    public int getInitialValue() {
        return initialValue;
    }
}