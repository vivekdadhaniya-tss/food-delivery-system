package com.fooddeliveryapp.util;

import com.fooddeliveryapp.type.IdType;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class IdGenerator {

    private static final HashMap<IdType, AtomicInteger> counters = new HashMap<>();

    static {
        for (IdType type : IdType.values()) {
            counters.put(type, new AtomicInteger(type.getInitialValue()));
        }
    }

    private IdGenerator() {}

    public static String generate(IdType type) {
        return type.getPrefix() + counters.get(type).incrementAndGet();
    }
}