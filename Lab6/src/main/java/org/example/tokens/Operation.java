package org.example.tokens;

import org.example.visitors.TokenVisitor;

public class Operation implements Token {
    public enum Sign {
        SUM, SUB, DIV, MUL;
    }
    public final Sign value;

    public Operation(Sign value) {
        this.value = value;
    }

    public Operation(Character symbol) {
        switch (symbol) {
            case '-' -> this.value = Sign.SUB;
            case '+' -> this.value = Sign.SUM;
            case '*' -> this.value = Sign.MUL;
            case '/' -> this.value = Sign.DIV;
            default -> throw new IllegalArgumentException("Illegal symbol. Wait operation, has " + symbol);
        }
    }
    @Override
    public void accept(TokenVisitor visitor) {
        visitor.visit(this);
    }
}
