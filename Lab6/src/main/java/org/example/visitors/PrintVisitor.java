package org.example.visitors;

import org.example.tokens.Brace;
import org.example.tokens.NumberToken;
import org.example.tokens.Operation;
import org.example.tokens.Token;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;

public class PrintVisitor implements TokenVisitor {
    private static final Map<Operation.Sign, String> OPERATOR_TO_SIGN = Map.of(
            Operation.Sign.SUM, "+",
            Operation.Sign.SUB, "-",
            Operation.Sign.MUL, "*",
            Operation.Sign.DIV, "/"
    );

    private static final Map<Brace.Side, String> BRACE_TO_SIGN = Map.of(
            Brace.Side.OPEN, "(",
            Brace.Side.CLOSE, ")"
    );
    private final PrintStream outputStream;

    public void visitAll(List<Token> tokens) {
        for (Token token : tokens) {
            token.accept(this);
        }
        outputStream.println();
    }

    public PrintVisitor(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    private void print(String value) {
        outputStream.print(value + " ");
    }
    @Override
    public void visit(NumberToken token) {
        print(token.value.toString());
    }

    @Override
    public void visit(Operation token) {
        print(OPERATOR_TO_SIGN.get(token.value));
    }

    @Override
    public void visit(Brace token) {
        print(BRACE_TO_SIGN.get(token.value));
    }
}
