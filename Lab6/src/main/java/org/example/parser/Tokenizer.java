package org.example.parser;

import org.example.tokens.Token;

import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class Tokenizer {
    State state;
    String input;
    Deque<Token> tokens = new LinkedList<>();

    public Tokenizer(String input) {
        this.input = input;
        this.state = new Start();
    }

    public List<Token> parse() {
        while (!(state instanceof End || state instanceof Error)) {
            state.next(this);
        }
        return List.copyOf(tokens);
    }
}
