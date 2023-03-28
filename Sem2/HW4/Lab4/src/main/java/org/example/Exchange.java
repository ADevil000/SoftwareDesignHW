package org.example;

import org.example.database.model.Currency;

public class Exchange {
    public static double getServerExchange(Currency currency) {
        return switch (currency) {
            case EUR -> 1f / 80f;
            case USD -> 1f / 70f;
            case RUB -> 1;
            default -> throw new IllegalArgumentException();
        };
    }
}
