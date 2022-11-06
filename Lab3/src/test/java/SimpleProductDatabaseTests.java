import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.MockedStatic;
import ru.akirakozov.sd.refactoring.database.SimpleProductDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class SimpleProductDatabaseTests {

    private static MockedStatic<DriverManager> mockedStaticDriverManager;
    private ResultSet resultSet;
    private Statement statement;
    private Connection connection;
    String databasePath;

    @BeforeAll
    public static void init() {
        mockedStaticDriverManager = mockStatic(DriverManager.class);
    }

    @AfterAll
    public static void close() {
        mockedStaticDriverManager.close();
    }

    @BeforeEach
    public void setUp() throws SQLException {
        databasePath = "jdbc:sqlite:test.db";

        resultSet = mock(ResultSet.class);

        statement = mock(Statement.class);


        connection = mock(Connection.class);
        when(connection.createStatement()).thenReturn(statement);
        when(DriverManager.getConnection(databasePath)).thenReturn(connection);
    }

    @Test
    public void testGetZeroNamesAndProducts() throws SQLException {
        String query = "SELECT * FROM PRODUCT";

        when(resultSet.next()).thenReturn(false);

        when(statement.executeQuery(query)).thenReturn(resultSet);

        final SimpleProductDatabase testObj = new SimpleProductDatabase(databasePath);
        List<Map<String, String>> result = testObj.getNamesAndPrices(query);

        List<Map<String, String>> expected = new ArrayList<>();

        assertEquals(expected, result);
        verify(resultSet).close();
        verify(statement).close();
        verify(connection).close();
    }

    @Test
    public void testGetOneNameAndProduct() throws SQLException {
        String query = "SELECT * FROM PRODUCT";

        String name = "iphone";
        int price = 600;
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getString("name")).thenReturn(name);
        when(resultSet.getInt("price")).thenReturn(price);

        when(statement.executeQuery(query)).thenReturn(resultSet);

        final SimpleProductDatabase testObj = new SimpleProductDatabase(databasePath);
        List<Map<String, String>> result = testObj.getNamesAndPrices(query);

        List<Map<String, String>> expected = new ArrayList<>();
        HashMap<String, String> pair = new HashMap<>();
        pair.put("name", name);
        pair.put("price", String.valueOf(price));
        expected.add(pair);

        assertEquals(expected, result);
        verify(resultSet).close();
        verify(statement).close();
        verify(connection).close();
    }

    @Test
    public void testGetMoreNameAndProducts() throws SQLException {
        String query = "SELECT * FROM PRODUCT";

        String name = "iphone";
        int price = 600;
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(resultSet.getString("name")).thenReturn(name).thenReturn(name + name);
        when(resultSet.getInt("price")).thenReturn(price). thenReturn(price + price);

        when(statement.executeQuery(query)).thenReturn(resultSet);

        final SimpleProductDatabase testObj = new SimpleProductDatabase(databasePath);
        List<Map<String, String>> result = testObj.getNamesAndPrices(query);

        List<Map<String, String>> expected = new ArrayList<>();
        HashMap<String, String> pair1 = new HashMap<>();
        pair1.put("name", name);
        pair1.put("price", String.valueOf(price));
        expected.add(pair1);
        HashMap<String, String> pair2 = new HashMap<>();
        pair2.put("name", name + name);
        pair2.put("price", String.valueOf(price + price));
        expected.add(pair2);

        assertEquals(expected, result);
        verify(resultSet).close();
        verify(statement).close();
        verify(connection).close();
    }

    @Test
    public void testAddValue() throws SQLException {
        String name = "iphone";
        String price = "600";
        String sql = "INSERT INTO PRODUCT " + "(NAME, PRICE) VALUES (\"" + name + "\"," + price + ")";

        when(resultSet.next()).thenReturn(false);

        final SimpleProductDatabase testObj = new SimpleProductDatabase(databasePath);
        testObj.addValue(sql);

        InOrder inOrder = inOrder(statement);
        inOrder.verify(statement).executeUpdate(sql);
        inOrder.verify(statement, never()).executeUpdate(anyString());
        inOrder.verify(statement).close();

        verify(connection).close();
    }

    @Test
    public void testNormalGetScalarNumber() throws SQLException {
        String query = "SELECT SUM(price) FROM PRODUCT";

        int sum = 600;
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getInt(1)).thenReturn(sum);

        when(statement.executeQuery(query)).thenReturn(resultSet);

        final SimpleProductDatabase testObj = new SimpleProductDatabase(databasePath);
        int result = testObj.getScalarNumber(query, 0);

        assertEquals(sum, result);
        verify(resultSet).close();
        verify(statement).close();
        verify(connection).close();
    }

    @Test
    public void testEmptyGetScalarNumber() throws SQLException {
        String query = "SELECT SUM(price) FROM PRODUCT";
        int neutral = 0;

        when(resultSet.next()).thenReturn(false);

        when(statement.executeQuery(query)).thenReturn(resultSet);

        final SimpleProductDatabase testObj = new SimpleProductDatabase(databasePath);
        int result = testObj.getScalarNumber(query, neutral);

        assertEquals(neutral, result);
        verify(resultSet).close();
        verify(statement).close();
        verify(connection).close();
    }
}
