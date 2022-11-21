package org.example.visitors;

import org.example.tokens.Brace;
import org.example.tokens.NumberToken;
import org.example.tokens.Operation;
import org.example.tokens.Token;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class CalcVisitor implements TokenVisitor {
    private Deque<Double> stack = new LinkedList<>();

    public Double getResult() {
        if (stack.size() != 1) {
            throw new IllegalStateException("Wrong state. Has " + stack.size() + " elements when wait 1");
        }
        return stack.getLast();
    }

    public void visitAll(List<Token> tokens) {
        for (Token token : tokens) {
            token.accept(this);
        }
    }
    @Override
    public void visit(NumberToken token) {
        stack.add(token.value);
    }

    @Override
    public void visit(Operation token) {
        Double right = stack.pollLast();
        Double left = stack.pollLast();
        switch (token.value) {
            case SUM -> stack.add(left + right);
            case SUB -> stack.add(left - right);
            case DIV -> stack.add(left / right);
            case MUL -> stack.add(left * right);
        }
    }

    @Override
    public void visit(Brace token) {
        throw new IllegalStateException("Meet brace in Reverse Polish notation");
    }
}
