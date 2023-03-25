package org.example.app;

import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.DoubleStream;

public class ExchangeClient {
    public static class UserInfo {
        double freeMoney = 0;
        Map<String, Long> stocks = new HashMap<>();
    }

    public static class StockInfo {
        String company;
        double price;
        long count;

        public StockInfo(String company, double price, long count) {
            this.company = company;
            this.price = price;
            this.count = count;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StockInfo stockInfo = (StockInfo) o;
            return Double.compare(stockInfo.price, price) == 0 && count == stockInfo.count && company.equals(stockInfo.company);
        }

        @Override
        public int hashCode() {
            return Objects.hash(company, price, count);
        }
    }

    public static class SmallStockInfo {
        String company;
        long count;

        public SmallStockInfo(String company, long count) {
            this.company = company;
            this.count = count;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SmallStockInfo that = (SmallStockInfo) o;
            return count == that.count && company.equals(that.company);
        }

        @Override
        public int hashCode() {
            return Objects.hash(company, count);
        }
    }
    public Map<Long, UserInfo> usersToInfo = new HashMap<>();
    Random random = new Random();

    HttpClient httpClient = HttpClient.newHttpClient();

    String baseURL = "http://localhost:8080";

    public long addUser() {
        long id = random.nextLong();
        while (usersToInfo.containsKey(id)) {
            id = random.nextLong();
        }
        usersToInfo.put(id, new UserInfo());
        return id;
    }

    public boolean addMoney(Long userId, double extraMoney) {
        UserInfo userInfo = usersToInfo.get(userId);
        if (userInfo == null) {
            return false;
        }
        userInfo.freeMoney += extraMoney;
        return true;
    }

    public List<StockInfo> getStocksFullInfo(long userId) {
        UserInfo userInfo = usersToInfo.get(userId);
        if (userInfo == null) {
            return Collections.emptyList();
        }
        return getStocksFullInfo(userInfo);
    }
    public double getAllMoney(long userId) {
        UserInfo userInfo = usersToInfo.get(userId);
        if (userInfo == null) {
            return 0;
        }
        return userInfo.freeMoney + getStocksFullInfo(userInfo).stream().flatMapToDouble(u -> DoubleStream.of(u.count * u.price)).sum();
    }

    public boolean sell(long userId, String company, long count, double price) {
        UserInfo userInfo = usersToInfo.get(userId);
        if (userInfo == null) {
            return false;
        }
        Long numberOfStock = userInfo.stocks.get(company);
        if (numberOfStock == null || numberOfStock < count) {
            return false;
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(baseURL + "/sell?company=" + company + "&price=" + price + "&count=" + count))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String result = response.body();
            boolean flag =  Boolean.parseBoolean(result);
            if (flag) {
                userInfo.freeMoney += price * count;
                Long prevStock = userInfo.stocks.get(company);
                if (prevStock == count) {
                    userInfo.stocks.remove(company);
                } else {
                    userInfo.stocks.put(company, prevStock - count);
                }
            }
            return flag;
        } catch (Exception exception) {
            return false;
        }
    }

    public boolean buy(long userId, String company, long count, double price) {
        UserInfo userInfo = usersToInfo.get(userId);
        if (userInfo == null || userInfo.freeMoney < count * price) {
            return false;
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(baseURL + "/buy?company=" + company + "&price=" + price + "&count=" + count))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String result = response.body();
            boolean flag = Boolean.parseBoolean(result);
            if (flag) {
                userInfo.stocks.merge(company, count, Long::sum);
                userInfo.freeMoney -= price * count;
            }
            return flag;
        } catch (Exception exception) {
            return false;
        }
    }

    private List<StockInfo> getStocksFullInfo(UserInfo userInfo) {
        List<StockInfo> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : userInfo.stocks.entrySet()) {
            result.add(getStockFullInfo(entry.getKey(), entry.getValue()));
        }
        result.sort(Comparator.comparing(o -> o.company));
        return result;
    }

    private StockInfo getStockFullInfo(String company, long count) {
        double curStockPrice = getPrice(company);
        return new StockInfo(company, curStockPrice, count);
    }

    public double getPrice(String company) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(baseURL + "/get?company=" + company))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String jsonString = response.body();
            JSONObject obj = new JSONObject(jsonString);
            return obj.getDouble("price");
        } catch (Exception exception) {
            return 0;
        }
    }

    public SmallStockInfo getStock(long userId, String company) {
        Map<String, Long> stocks = usersToInfo.get(userId).stocks;
        if (stocks.containsKey(company)) {
            return new SmallStockInfo(company, usersToInfo.get(userId).stocks.get(company));
        } else {
            return null;
        }
    }

    public double getFreeMoney(long userId) {
        return usersToInfo.get(userId).freeMoney;
    }
}
