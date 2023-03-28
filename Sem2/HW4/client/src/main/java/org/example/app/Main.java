package org.example.app;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNext()) {
                String s = scanner.next();
                switch (s) {
                    case "add_user":
                        System.out.println(Lab4Client.addUser(scanner.next(), scanner.next()));
                        break;
                    case "add_item":
                        System.out.println(Lab4Client.addItem(scanner.next(), scanner.nextDouble()));
                        break;
                    case "catalog":
                        System.out.println(Lab4Client.catalog(scanner.next()));
                        break;
                    default:
                        break;
                }
            }
        }
    }

}
