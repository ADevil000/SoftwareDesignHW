import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.akirakozov.sd.refactoring.database.ProductDatabase;
import ru.akirakozov.sd.refactoring.servlet.QueryServlet;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class QueryServletTests {

    private ProductDatabase database;
    private PrintWriter writer;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ArrayList<String> result;

    @BeforeEach
    public void setUp() throws IOException {
        database = mock(ProductDatabase.class);

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
    public void testMaxResult() throws IOException {
        String name = "iphone";
        int price = 600;
        String query = "SELECT * FROM PRODUCT ORDER BY PRICE DESC LIMIT 1";

        List<Map<String, String>> mockDatabaseResults = new ArrayList<>();
        HashMap<String, String> pair = new HashMap<>();
        pair.put("name", name);
        pair.put("price", String.valueOf(price));
        mockDatabaseResults.add(pair);
        when(database.getNamesAndPrices(query)).thenReturn(mockDatabaseResults);

        when(request.getParameter("command")).thenReturn("max");

        final QueryServlet testObj = new QueryServlet(database);
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
    }

    @Test
    public void testMinResult() throws IOException {
        String name = "iphone";
        int price = 600;
        String query = "SELECT * FROM PRODUCT ORDER BY PRICE LIMIT 1";

        List<Map<String, String>> mockDatabaseResults = new ArrayList<>();
        HashMap<String, String> pair = new HashMap<>();
        pair.put("name", name);
        pair.put("price", String.valueOf(price));
        mockDatabaseResults.add(pair);
        when(database.getNamesAndPrices(query)).thenReturn(mockDatabaseResults);

        when(request.getParameter("command")).thenReturn("min");

        final QueryServlet testObj = new QueryServlet(database);
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
    }

    @Test
    public void testSumResult() throws IOException {
        int price = 600;
        String query = "SELECT SUM(price) FROM PRODUCT";
        when(database.getScalarNumber(query, QueryServlet.NEUTRAL)).thenReturn(600);

        when(request.getParameter("command")).thenReturn("sum");

        final QueryServlet testObj = new QueryServlet(database);
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
    }

    @Test
    public void testEmptySumResult() throws IOException {
        String query = "SELECT SUM(price) FROM PRODUCT";
        when(database.getScalarNumber(query, QueryServlet.NEUTRAL)).thenReturn(QueryServlet.NEUTRAL);

        when(request.getParameter("command")).thenReturn("sum");

        final QueryServlet testObj = new QueryServlet(database);
        testObj.doGet(request, response);

        String[] expected = new String[]{
                "<html><body>",
                "Summary price: ",
                "</body></html>"
        };
        assertArrayEquals(expected, result.toArray());
        verify(response).setContentType("text/html");
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testEmptyCountResult() throws IOException {
        String query = "SELECT COUNT(*) FROM PRODUCT";
        when(database.getScalarNumber(query, QueryServlet.NEUTRAL)).thenReturn(QueryServlet.NEUTRAL);

        when(request.getParameter("command")).thenReturn("count");

        final QueryServlet testObj = new QueryServlet(database);
        testObj.doGet(request, response);

        String[] expected = new String[]{
                "<html><body>",
                "Number of products: ",
                "</body></html>"
        };
        assertArrayEquals(expected, result.toArray());
        verify(response).setContentType("text/html");
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testCountResult() throws IOException {
        int count = 600;
        String query = "SELECT COUNT(*) FROM PRODUCT";
        when(database.getScalarNumber(query, QueryServlet.NEUTRAL)).thenReturn(count);

        when(request.getParameter("command")).thenReturn("count");

        final QueryServlet testObj = new QueryServlet(database);
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
    }

    @Test
    public void testUnexpectedCommandResult() throws IOException {
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
