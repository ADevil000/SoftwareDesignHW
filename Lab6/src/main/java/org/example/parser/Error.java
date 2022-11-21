package org.example.parser;

public record Error(Exception exception) implements State {
    @Override
    public void next(Tokenizer tokenizer) {
        // nothing
    }
}
