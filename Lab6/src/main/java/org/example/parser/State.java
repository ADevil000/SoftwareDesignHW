package org.example.parser;

public interface State {
    void next(Tokenizer tokenizer);
}
