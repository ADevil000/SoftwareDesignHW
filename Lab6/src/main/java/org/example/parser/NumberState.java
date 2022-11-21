package org.example.parser;

import org.example.tokens.NumberToken;

public class NumberState implements State {
    StringBuilder number;
    public NumberState(Character symbol) {
        this.number = new StringBuilder(String.valueOf(symbol));
    }

    @Override
    public void next(Tokenizer tokenizer) {
        if (tokenizer.input.isEmpty()) {
            tokenizer.state = new End();
            tokenizer.tokens.add(new NumberToken(Double.parseDouble(number.toString())));
            return;
        }
        Character symbol = tokenizer.input.charAt(0);
        if (Character.isDigit(symbol) || symbol.equals('.')) {
            number.append(symbol);
            tokenizer.input = tokenizer.input.substring(1);
        } else {
            tokenizer.state = new Start();
            tokenizer.tokens.add(new NumberToken(Double.parseDouble(number.toString())));
        }
    }
}
