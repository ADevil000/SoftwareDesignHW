package org.example;

import org.example.parser.Tokenizer;
import org.example.tokens.Token;
import org.example.visitors.CalcVisitor;
import org.example.visitors.ParserVisitor;
import org.example.visitors.PrintVisitor;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            String input = scanner.nextLine();
            Tokenizer tokenizer = new Tokenizer(input);
            List<Token> tokens = tokenizer.parse();
            ParserVisitor parserVisitor = new ParserVisitor();
            tokens = parserVisitor.visitAll(tokens);
            PrintVisitor printVisitor = new PrintVisitor(System.out);
            printVisitor.visitAll(tokens);
            CalcVisitor calcVisitor = new CalcVisitor();
            calcVisitor.visitAll(tokens);
            System.out.println(calcVisitor.getResult());
        }
    }
}