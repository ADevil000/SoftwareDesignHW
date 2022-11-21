package org.example.tokens;

import org.example.visitors.TokenVisitor;

public interface Token {
    void accept(TokenVisitor visitor);
}
