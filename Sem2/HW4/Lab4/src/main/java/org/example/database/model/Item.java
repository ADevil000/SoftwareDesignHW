package org.example.database.model;

import org.bson.Document;

public class Item {
    public String name;
    public double priceInRub;

    public Item(String name, double priceInRub) {
        this.name = name;
        this.priceInRub = priceInRub;
    }

    public Item(Document doc) {
        this(doc.getString("name"), doc.getDouble("priceInRub"));
    }

    @Override
    public String toString() {
        return "ItemDB{" +
                "name='" + name + '\'' +
                ", priceInRub=" + priceInRub +
                '}';
    }
}

