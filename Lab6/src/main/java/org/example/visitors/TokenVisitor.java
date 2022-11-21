package org.example.visitors;

import org.example.tokens.Brace;
import org.example.tokens.NumberToken;
import org.example.tokens.Operation;

public interface TokenVisitor {
    void visit(NumberToken token);
    void visit(Operation token);
    void visit(Brace token);
}
