package org.example.parser;

import org.example.tokens.Brace;
import org.example.tokens.NumberToken;
import org.example.tokens.Operation;

public class Start implements State {
    @Override
    public void next(Tokenizer tokenizer) {
        if (tokenizer.input.isEmpty()) {
            tokenizer.state = new End();
            return;
        }
        Character symbol = tokenizer.input.charAt(0);
        tokenizer.input = tokenizer.input.substring(1);
        if (Character.isWhitespace(symbol)) {
            // skip
            return;
        } else if (Character.isDigit(symbol)) {
            tokenizer.state = new NumberState(symbol);
            return;
        } else if (symbol.equals('-')) {
            if (tokenizer.tokens.isEmpty() || !(tokenizer.tokens.getLast() instanceof NumberToken)) {
                tokenizer.state = new NumberState(symbol);
            } else {
                tokenizer.tokens.add(new Operation(symbol));
            }
            return;
        } else if (symbol.equals('+') || symbol.equals('*') || symbol.equals('/')) {
            tokenizer.tokens.add(new Operation(symbol));
            return;
        } else if (symbol.equals('(') || symbol.equals(')')) {
            tokenizer.tokens.add(new Brace(symbol));
            return;
        } else {
            tokenizer.state = new Error(new IllegalStateException("Undefinad synbol in Start state: " + symbol));
            return;
        }
    }
}
