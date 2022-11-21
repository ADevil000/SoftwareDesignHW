package org.example.visitors;

import org.example.tokens.Brace;
import org.example.tokens.NumberToken;
import org.example.tokens.Operation;
import org.example.tokens.Token;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ParserVisitor implements TokenVisitor {
    private static final Map<Operation.Sign, Integer> OPERATION_TO_PRIORITY = Map.of(
            Operation.Sign.DIV, 2,
            Operation.Sign.MUL, 2,
            Operation.Sign.SUM, 1,
            Operation.Sign.SUB, 1
    );

    private Deque<Token> stack = new LinkedList<>();
    private List<Token> result = new LinkedList<>();

    public List<Token> visitAll(List<Token> tokens) {
        for (Token token : tokens) {
            token.accept(this);
        }
        while (!stack.isEmpty()) {
            result.add(stack.pollLast());
        }
        return List.copyOf(result);
    }

    @Override
    public void visit(NumberToken token) {
        result.add(token);
    }

    @Override
    public void visit(Operation token) {
        int tokenOperationPriority = OPERATION_TO_PRIORITY.get(token.value);
        if (stack.isEmpty()) {
            stack.add(token);
            return;
        }
        Token lastStackToken = stack.getLast();
        while (lastStackToken instanceof Operation && tokenOperationPriority <= OPERATION_TO_PRIORITY.get(((Operation) lastStackToken).value)) {
            result.add(stack.pollLast());
            if (stack.isEmpty()) {
                break;
            }
            lastStackToken = stack.getLast();
        }
        stack.add(token);
    }

    @Override
    public void visit(Brace token) {
        switch (token.value) {
            case OPEN -> stack.add(token);
            case CLOSE -> {
                Token curToken = stack.pollLast();
                while (!(curToken instanceof Brace)) {
                    result.add(curToken);
                    curToken = stack.pollLast();
                }
            }
        }
    }
}
