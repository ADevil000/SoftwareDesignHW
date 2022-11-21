package org.example.tokens;

import org.example.visitors.TokenVisitor;

public class Brace implements Token {
    public enum Side {
        OPEN, CLOSE
    }
    public final Side value;

    public Brace(Side value) {
        this.value = value;
    }

    public Brace(Character symbol) {
        switch (symbol) {
            case '(' -> this.value = Side.OPEN;
            case ')' -> this.value = Side.CLOSE;
            default -> throw new IllegalArgumentException("Illegal symbol. Wait brace, has " + symbol);
        }
    }
    @Override
    public void accept(TokenVisitor visitor) {
        visitor.visit(this);
    }
}
