import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import ru.akirakozov.sd.refactoring.servlet.GetProductsServlet;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class GetProductServletTests {

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
        when(statement.executeQuery("SELECT * FROM PRODUCT")).thenReturn(resultSet);

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
    }

    @Test
    public void testZeroResults() throws SQLException, IOException {
        when(resultSet.next()).thenReturn(false);

        final GetProductsServlet testObj = new GetProductsServlet();
        testObj.doGet(request, response);

        String[] expected = new String[]{
                "<html><body>",
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
    public void testOneResult() throws SQLException, IOException {
        String name = "iphone";
        int price = 600;
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getString("name")).thenReturn(name);
        when(resultSet.getInt("price")).thenReturn(price);

        final GetProductsServlet testObj = new GetProductsServlet();
        testObj.doGet(request, response);

        String[] expected = new String[]{
                "<html><body>",
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
    public void testTwoResult() throws SQLException, IOException {
        String name = "iphone";
        int price = 600;
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(resultSet.getString("name")).thenReturn(name).thenReturn(name + name);
        when(resultSet.getInt("price")).thenReturn(price).thenReturn(price + price);

        final GetProductsServlet testObj = new GetProductsServlet();
        testObj.doGet(request, response);

        String[] expected = new String[]{
                "<html><body>",
                name + "\t" + price + "</br>",
                name + name + "\t" + (price + price) + "</br>",
                "</body></html>"
        };
        assertArrayEquals(expected, result.toArray());
        verify(response).setContentType("text/html");
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(resultSet).close();
        verify(statement).close();
        verify(connection).close();
    }
}
