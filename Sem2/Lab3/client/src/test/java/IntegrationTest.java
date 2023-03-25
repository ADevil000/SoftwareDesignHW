import org.example.app.ExchangeChanger;
import org.example.app.ExchangeClient;
import org.junit.*;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {
    @ClassRule
    public static GenericContainer simpleWebServer
            = new FixedHostPortGenericContainer("exchange:1.0-SNAPSHOT")
            .withFixedExposedPort(8080, 8080)
            .withExposedPorts(8080);

    public static ExchangeClient client;

    @Before
    public void before() {
        ExchangeChanger.add("VK", 5, 10);
        ExchangeChanger.add("Yandex", 10, 10);
        ExchangeChanger.add("Google", 20, 10);
        ExchangeChanger.add("Microsoft", 20, 50);
        ExchangeChanger.add("StartUp", 1, 5);
        client = new ExchangeClient();
    }

    @After
    public void after() {
        ExchangeChanger.clear();
    }

    @Test
    public void successBuyStock() {
        long userId = client.addUser();
        double startMoney = 10000;
        String company = "Yandex";
        long count = 10;
        client.addMoney(userId, startMoney);
        double priceOfCompany = client.getPrice(company);

        assertTrue(client.buy(userId,company, count, priceOfCompany));
        assertEquals(startMoney - count * priceOfCompany, client.getFreeMoney(userId));
        ExchangeClient.SmallStockInfo expectedStock = new ExchangeClient.SmallStockInfo(company, count);
        assertEquals(expectedStock, client.getStock(userId, company));
    }

    @Test
    public void failBuyStockChangedPrice() {
        long userId = client.addUser();
        double startMoney = 10000;
        String company = "Yandex";
        long count = 10;
        client.addMoney(userId, startMoney);
        double priceOfCompany = client.getPrice(company);
        ExchangeChanger.update(company, 10);

        assertFalse(client.buy(userId,company, count, priceOfCompany));
        assertEquals(startMoney, client.getFreeMoney(userId));
        assertNull(client.getStock(userId, company));
    }

    @Test
    public void failBuyStockNotEnoughMoney() {
        long userId = client.addUser();
        double startMoney = 100000;
        String company = "Yandex";
        long count = 100;
        client.addMoney(userId, startMoney);
        double priceOfCompany = client.getPrice(company);

        assertFalse(client.buy(userId,company, count, priceOfCompany));
        assertEquals(startMoney, client.getFreeMoney(userId));
        assertNull(client.getStock(userId, company));
    }

    @Test
    public void failBuyStockNotEnoughFreeStocks() {
        long userId = client.addUser();
        double startMoney = 10000;
        String company = "Yandex";
        long count = 10;
        client.addMoney(userId, startMoney);
        double priceOfCompany = client.getPrice(company);
        ExchangeChanger.update(company, 10);

        assertFalse(client.buy(userId,company, count, priceOfCompany));
        assertEquals(startMoney, client.getFreeMoney(userId));
        assertNull(client.getStock(userId, company));
    }

    @Test
    public void successFullSellStock() {
        long userId = client.addUser();
        double startMoney = 10000;
        String company = "Yandex";
        long count = 10;
        client.addMoney(userId, startMoney);
        double priceOfCompany = client.getPrice(company);
        client.buy(userId,company, count, priceOfCompany);

        assertTrue(client.sell(userId, company, count, priceOfCompany));
        assertEquals(startMoney, client.getFreeMoney(userId));
        assertNull(client.getStock(userId, company));
    }

    @Test
    public void successPartSellStock() {
        long userId = client.addUser();
        double startMoney = 10000;
        String company = "Yandex";
        long count = 10;
        client.addMoney(userId, startMoney);
        double priceOfCompany = client.getPrice(company);
        client.buy(userId,company, count, priceOfCompany);

        assertTrue(client.sell(userId, company, count / 2, priceOfCompany));
        assertEquals(startMoney - priceOfCompany * (count / 2), client.getFreeMoney(userId));
        ExchangeClient.SmallStockInfo expectedStock = new ExchangeClient.SmallStockInfo(company, count / 2);
        assertEquals(expectedStock, client.getStock(userId, company));
    }

    @Test
    public void failSellStockChangedPrice() {
        long userId = client.addUser();
        double startMoney = 10000;
        String company = "Yandex";
        long count = 10;
        client.addMoney(userId, startMoney);
        double priceOfCompany = client.getPrice(company);
        client.buy(userId,company, count, priceOfCompany);
        ExchangeChanger.update(company, 10);

        assertFalse(client.sell(userId, company, count, priceOfCompany));
        assertEquals(startMoney - priceOfCompany * count, client.getFreeMoney(userId));
        ExchangeClient.SmallStockInfo expectedStock = new ExchangeClient.SmallStockInfo(company, count);
        assertEquals(expectedStock, client.getStock(userId, company));
    }

    @Test
    public void failSellStockNotEnoughStocks() {
        long userId = client.addUser();
        double startMoney = 10000;
        String company = "Yandex";
        long count = 10;
        client.addMoney(userId, startMoney);
        double priceOfCompany = client.getPrice(company);
        client.buy(userId,company, count, priceOfCompany);
        ExchangeChanger.update(company, 10);

        assertFalse(client.sell(userId, company, count + 1, priceOfCompany));
        assertEquals(startMoney - priceOfCompany * count, client.getFreeMoney(userId));
        ExchangeClient.SmallStockInfo expectedStock = new ExchangeClient.SmallStockInfo(company, count);
        assertEquals(expectedStock, client.getStock(userId, company));
    }

    @Test
    public void failSellStockAnotherCompany() {
        long userId = client.addUser();
        double startMoney = 10000;
        String company = "Yandex";
        long count = 10;
        client.addMoney(userId, startMoney);
        double priceOfCompany = client.getPrice(company);
        client.buy(userId,company, count, priceOfCompany);
        ExchangeChanger.update(company, 10);

        assertFalse(client.sell(userId, "Google", count, priceOfCompany));
        assertEquals(startMoney - priceOfCompany * count, client.getFreeMoney(userId));
        ExchangeClient.SmallStockInfo expectedStock = new ExchangeClient.SmallStockInfo(company, count);
        assertEquals(expectedStock, client.getStock(userId, company));
        assertNull(client.getStock(userId, "Google"));
    }

    @Test
    public void allMoneyNotChangedAfterBuy() {
        long userId = client.addUser();
        double startMoney = 10000;
        String company = "Yandex";
        long count = 10;
        client.addMoney(userId, startMoney);
        double priceOfCompany = client.getPrice(company);
        client.buy(userId,company, count, priceOfCompany);

        assertEquals(startMoney, client.getAllMoney(userId));
    }

    @Test
    public void allMoneyNotChangedWithoutActions() {
        long userId = client.addUser();
        double startMoney = 10000;
        client.addMoney(userId, startMoney);

        assertEquals(startMoney, client.getAllMoney(userId));
    }

    @Test
    public void allMoneyStartZero() {
        long userId = client.addUser();

        assertEquals(0, client.getAllMoney(userId));
    }

    @Test
    public void allMoneyChangedWithStockPrice() {
        long userId = client.addUser();
        double startMoney = 10000;
        String company = "Yandex";
        long count = 10;
        client.addMoney(userId, startMoney);
        double priceOfCompany = client.getPrice(company);
        client.buy(userId,company, count, priceOfCompany);
        double diff = 10;
        ExchangeChanger.update(company, diff);

        assertEquals(startMoney, client.getAllMoney(userId) - count * diff);
    }


    @Test
    public void getFullStocks() {
        long userId = client.addUser();
        double startMoney = 10000;
        client.addMoney(userId, startMoney);
        long count = 10;
        String company = "Yandex";
        String company2 = "Google";
        double priceOfCompany = client.getPrice(company);
        client.buy(userId,company, count, priceOfCompany);
        double priceOfCompany2 = client.getPrice(company2);
        client.buy(userId,company2, count, priceOfCompany2);

        List<ExchangeClient.StockInfo> expected = List.of(
            new ExchangeClient.StockInfo(company2, priceOfCompany2, count),
            new ExchangeClient.StockInfo(company, priceOfCompany, count)
        );
        assertEquals(expected, client.getStocksFullInfo(userId));
    }

    @Test
    public void getFullStocksWithChangedPrice() {
        long userId = client.addUser();
        double startMoney = 10000;
        client.addMoney(userId, startMoney);
        long count = 10;
        String company = "Yandex";
        String company2 = "Google";
        double priceOfCompany = client.getPrice(company);
        client.buy(userId,company, count, priceOfCompany);
        double priceOfCompany2 = client.getPrice(company2);
        client.buy(userId,company2, count, priceOfCompany2);
        ExchangeChanger.update(company, 10);
        ExchangeChanger.update(company2, 10);

        List<ExchangeClient.StockInfo> expected = List.of(
                new ExchangeClient.StockInfo(company2, priceOfCompany2 + 10, count),
                new ExchangeClient.StockInfo(company, priceOfCompany + 10, count)
        );
        assertEquals(expected, client.getStocksFullInfo(userId));
    }
}
