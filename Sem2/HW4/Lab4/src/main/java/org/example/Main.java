package org.example;

import io.reactivex.netty.protocol.http.server.HttpServer;
import org.example.database.ReactiveMongoDriver;
import org.example.database.model.Currency;
import org.example.database.model.Item;
import org.example.database.model.User;
import rx.Observable;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        try (ReactiveMongoDriver driver = new ReactiveMongoDriver("mongodb://localhost:27017")) {
            HttpServer
                .newServer(8080)
                .start((req, resp) -> {
                    String[] paths = req.getDecodedPath().substring(1).split("/");
                    Observable<String> response;
                    try {
                        switch (paths[0]) {
                            case "add_item":
                                response = driver
                                        .insertItems(new Item(paths[1], Double.parseDouble(paths[2])))
                                        .map(Objects::toString);
                                break;
                            case "add_user":
                                response = driver
                                        .insertUser(new User(paths[1], Currency.valueOf(paths[2])))
                                        .map(Objects::toString);
                                break;
                            case "catalog":
                                Observable<User> foundUser = driver.getUser(paths[1]);
                                Observable<Item> items = driver.getItems();
                                response =
                                        foundUser.flatMap(user -> {
                                            Observable<String> stringItems = items.map(item -> {
                                                double exchange = Exchange.getServerExchange(user.chosenCurrency);
                                                return item.name + " " + (exchange * item.priceInRub) + " " + user.chosenCurrency;
                                            });
                                            return Observable.just("User " + user.name + ":").mergeWith(stringItems);
                                        }).toList().map(l -> l.stream().collect(Collectors.joining(System.lineSeparator())));
                                break;
                            default:
                                response = Observable.just("Default branch");
                                break;
                        }
                    } catch (Exception e) {
                        response = Observable.just(e.getMessage());
                    }
                    response.onErrorReturn(Throwable::getMessage);
                    response.subscribe(System.out::println);
                    return resp.writeString(response);
                })
                .awaitShutdown();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}