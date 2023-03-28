package org.example.database;

import com.mongodb.rx.client.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.database.model.Item;
import org.example.database.model.User;
import rx.Observable;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Time;
import java.util.Date;
import java.util.Map;

public class ReactiveMongoDriver implements Closeable, AutoCloseable {
    private static final String USERS = "users";
    private static final String ITEMS = "items";
    private static final String DATABASE_NAME = "rxdb";
    private final MongoClient client;

    private final MongoCollection<Document> users;

    private final MongoCollection<Document> items;

    // "mongodb://localhost:27017"
    public ReactiveMongoDriver(String uri) {
        client = MongoClients.create(uri);
        MongoDatabase database = client.getDatabase(DATABASE_NAME);
        database.createCollection(USERS);
        users = database.getCollection(USERS);
        database.createCollection(ITEMS);
        items = database.getCollection(ITEMS);
    }

    public Observable<User> getUser(String name) {
        return users.find(new Document(Map.of(
            "name", name
        ))).first().map(User::new);
    }

    public Observable<Item> getItems() {
         return items.find().toObservable().map(Item::new);
    }

    public Observable<Success> insertUser(User user) {
        Document newUser = new Document(Map.of(
                "_id", new ObjectId(),
                "name", user.name,
                "chosenCurrency", user.chosenCurrency.toString()
        ));
        return users.insertOne(newUser);
    }

    public Observable<Success> insertItems(Item item) {
        Document newItem = new Document(Map.of(
                "_id", new ObjectId(),
                "name", item.name,
                "priceInRub", item.priceInRub
        ));
        return items.insertOne(newItem);
    }

    @Override
    public void close() throws IOException {
        client.close();
    }

}
