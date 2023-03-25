package org.example.app;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class ExchangeEmulator {
    public static class Stock {
        public final long count;
        public final double price;

        public Stock(long count, double price) {
            this.count = count;
            this.price = price;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"count\":" + count + "," +
                    "\"price\":" + price +
                    '}';
        }
    }

    HashMap<String, Stock> exchange = new HashMap<>();

    private ReentrantLock lock = new ReentrantLock();

    public boolean add(String company, double startPrice, long numberOfStocks) {
        lock.lock();
        try {
            Stock curStock = exchange.putIfAbsent(company, new Stock(numberOfStocks, startPrice));
            return curStock == null;
        } finally {
            lock.unlock();
        }
    }

    public boolean add(String company, double startPrice) {
        return add(company, startPrice, 0);
    }

    public Stock get(String company) {
        lock.lock();
        try {
            return exchange.get(company);
        } finally {
            lock.unlock();
        }
    }

    public boolean update(String company, double diff) {
        lock.lock();
        try {
            Stock curStock = exchange.get(company);
            if (curStock == null) {
                return false;
            }
            exchange.put(company, new Stock(curStock.count, curStock.price + diff));
            return true;
        } finally {
            lock.unlock();
        }
    }

    public boolean buy(String company, int count, double priceForOne) {
        lock.lock();
        try {
            Stock curStock = exchange.get(company);
            if (curStock == null || curStock.count < count || curStock.price != priceForOne) {
                return false;
            } else {
                exchange.put(company, new Stock(curStock.count - count, curStock.price));
                return true;
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean sell(String company, int count, double priceForOne) {
        lock.lock();
        try {
            Stock curStock = exchange.get(company);
            if (curStock == null || curStock.price != priceForOne) {
                return false;
            } else {
                exchange.put(company, new Stock(curStock.count + count, curStock.price));
                return true;
            }
        } finally {
            lock.unlock();
        }
    }


    public void clear() {
        lock.lock();
        try {
            exchange.clear();
        } finally {
            lock.unlock();
        }
    }
}
