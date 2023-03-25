package org.example.app;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class ExchangeController {

    private static ExchangeEmulator exchange = new ExchangeEmulator();

    // Mark: endpoints for tests

    @RequestMapping("/add")
    public String add(String company, Double price, Long count) {
        return String.valueOf(exchange.add(company, price, count));
    }

    @RequestMapping("/update")
    public String update(String company, Double diff) {
        return String.valueOf(exchange.update(company, diff));
    }

    @RequestMapping("/clear")
    public String update() {
        exchange.clear();
        return "cleared";
    }

    // Mark: API

    @RequestMapping("/get")
    public String get(String company) {
        return Objects.toString(exchange.get(company), "not found");
    }

    @RequestMapping("/buy")
    public String buy(String company, Double price, Integer count) {
        return String.valueOf(exchange.buy(company, count, price));
    }

    @RequestMapping("/sell")
    public String sell(String company, Double price, Integer count) {
        return String.valueOf(exchange.sell(company, count, price));
    }
}
