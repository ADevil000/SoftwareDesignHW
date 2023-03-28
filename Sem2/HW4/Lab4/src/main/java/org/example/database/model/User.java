package org.example.database.model;

import org.bson.Document;

public class User {
    public final String name;
    public final Currency chosenCurrency;
    public User(Document doc) {
        this(doc.getString("name"), Currency.valueOf(doc.getString("chosenCurrency")));
    }

    public User(String name, Currency chosenCurrency) {
        this.name = name;
        this.chosenCurrency = chosenCurrency;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", chosenCurrency='" + chosenCurrency + '\'' +
                '}';
    }
}
