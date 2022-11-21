package org.example.tokens;

import org.example.visitors.TokenVisitor;

public class NumberToken implements Token {
    public final Double value;

    public NumberToken(Double value) {
        this.value = value;
    }

    @Override
    public void accept(TokenVisitor visitor) {
        visitor.visit(this);
    }
}
