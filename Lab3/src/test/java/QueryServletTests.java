import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import ru.akirakozov.sd.refactoring.servlet.QueryServlet;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class QueryServletTests {

    private static MockedStatic<DriverManager> mockedStaticDriverManager;
    private ResultSet resultSet;
    private Statement statement;
    private Connection connection;
    private PrintWriter writer;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ArrayList<String> result;

    @BeforeAll
    public static void init() {
        mockedStaticDriverManager = mockStatic(DriverManager.class);
    }

    @AfterAll
    public static void close() {
        mockedStaticDriverManager.close();
    }

    @BeforeEach
    public void setUp() throws SQLException, IOException {
        resultSet = mock(ResultSet.class);

        statement = mock(Statement.class);

        connection = mock(Connection.class);
        when(connection.createStatement()).thenReturn(statement);
        when(DriverManager.getConnection("jdbc:sqlite:test.db")).thenReturn(connection);

        writer = mock(PrintWriter.class);

        request = mock(HttpServletRequest.class);

        response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(writer);

        result = new ArrayList<>();
        doAnswer(invocation -> {
            String s = invocation.getArgument(0, String.class);
            result.add(s);
            return null;
        }).when(writer).println(anyString());
        doAnswer(invocation -> {
            Integer i = invocation.getArgument(0, Integer.class);
            result.add(i.toString());
            return null;
        }).when(writer).println(anyInt());
    }


    @Test
    public void testMaxResult() throws SQLException, IOException {
        String name = "iphone";
        int price = 600;
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getString("name")).thenReturn(name);
        when(resultSet.getInt("price")).thenReturn(price);

        when(statement.executeQuery("SELECT * FROM PRODUCT ORDER BY PRICE DESC LIMIT 1")).thenReturn(resultSet);
        when(request.getParameter("command")).thenReturn("max");

        final QueryServlet testObj = new QueryServlet();
        testObj.doGet(request, response);

        String[] expected = new String[]{
                "<html><body>",
                "<h1>Product with max price: </h1>",
                name + "\t" + price + "</br>",
                "</body></html>"
        };
        assertArrayEquals(expected, result.toArray());
        verify(response).setContentType("text/html");
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(resultSet).close();
        verify(statement).close();
        verify(connection).close();
    }

    @Test
    public void testMinResult() throws SQLException, IOException {
        String name = "iphone";
        int price = 600;
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getString("name")).thenReturn(name);
        when(resultSet.getInt("price")).thenReturn(price);

        when(statement.executeQuery("SELECT * FROM PRODUCT ORDER BY PRICE LIMIT 1")).thenReturn(resultSet);
        when(request.getParameter("command")).thenReturn("min");

        final QueryServlet testObj = new QueryServlet();
        testObj.doGet(request, response);

        String[] expected = new String[]{
                "<html><body>",
                "<h1>Product with min price: </h1>",
                name + "\t" + price + "</br>",
                "</body></html>"
        };
        assertArrayEquals(expected, result.toArray());
        verify(response).setContentType("text/html");
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(resultSet).close();
        verify(statement).close();
        verify(connection).close();
    }

    @Test
    public void testSumResult() throws SQLException, IOException {
        int price = 600;
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getInt(1)).thenReturn(price);

        when(statement.executeQuery("SELECT SUM(price) FROM PRODUCT")).thenReturn(resultSet);
        when(request.getParameter("command")).thenReturn("sum");

        final QueryServlet testObj = new QueryServlet();
        testObj.doGet(request, response);

        String[] expected = new String[]{
                "<html><body>",
                "Summary price: ",
                String.valueOf(price),
                "</body></html>"
        };
        assertArrayEquals(expected, result.toArray());
        verify(response).setContentType("text/html");
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(resultSet).close();
        verify(statement).close();
        verify(connection).close();
    }

    @Test
    public void testCountResult() throws SQLException, IOException {
        int count = 600;
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getInt(1)).thenReturn(count);

        when(statement.executeQuery("SELECT COUNT(*) FROM PRODUCT")).thenReturn(resultSet);
        when(request.getParameter("command")).thenReturn("count");

        final QueryServlet testObj = new QueryServlet();
        testObj.doGet(request, response);

        String[] expected = new String[]{
                "<html><body>",
                "Number of products: ",
                String.valueOf(count),
                "</body></html>"
        };
        assertArrayEquals(expected, result.toArray());
        verify(response).setContentType("text/html");
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(resultSet).close();
        verify(statement).close();
        verify(connection).close();
    }

    @Test
    public void testUnexpectedCommandResult() throws SQLException, IOException {
        String command = "hello world";
        when(request.getParameter("command")).thenReturn(command);

        final QueryServlet testObj = new QueryServlet();
        testObj.doGet(request, response);

        String[] expected = new String[]{
                "Unknown command: " + command
        };
        assertArrayEquals(expected, result.toArray());
        verify(response).setContentType("text/html");
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

}
