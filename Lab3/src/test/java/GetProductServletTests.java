import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.akirakozov.sd.refactoring.database.ProductDatabase;
import ru.akirakozov.sd.refactoring.servlet.GetProductsServlet;


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

public class GetProductServletTests {
    private ProductDatabase database;
    private PrintWriter writer;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ArrayList<String> result;
    private String query;
    List<Map<String, String>> mockDatabaseResults;

    @BeforeEach
    public void setUp() throws IOException {
        database = mock(ProductDatabase.class);
        query = "SELECT * FROM PRODUCT";
        mockDatabaseResults = new ArrayList<>();
        when(database.getNamesAndPrices(query)).thenReturn(mockDatabaseResults);

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
    public void testZeroResults() throws IOException {
        final GetProductsServlet testObj = new GetProductsServlet(database);
        testObj.doGet(request, response);

        String[] expected = new String[]{
                "<html><body>",
                "</body></html>"
        };
        assertArrayEquals(expected, result.toArray());
        verify(response).setContentType("text/html");
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testOneResult() throws IOException {
        String name = "iphone";
        int price = 600;

        HashMap<String, String> pair = new HashMap<>();
        pair.put("name", name);
        pair.put("price", String.valueOf(price));
        mockDatabaseResults.add(pair);

        final GetProductsServlet testObj = new GetProductsServlet(database);
        testObj.doGet(request, response);

        String[] expected = new String[]{
                "<html><body>",
                name + "\t" + price + "</br>",
                "</body></html>"
        };
        assertArrayEquals(expected, result.toArray());
        verify(response).setContentType("text/html");
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testTwoResult() throws IOException {
        String name = "iphone";
        int price = 600;

        HashMap<String, String> pair = new HashMap<>();
        pair.put("name", name);
        pair.put("price", String.valueOf(price));
        mockDatabaseResults.add(pair);

        HashMap<String, String> pair2 = new HashMap<>();
        pair2.put("name", name + name);
        pair2.put("price", String.valueOf(price + price));
        mockDatabaseResults.add(pair2);

        final GetProductsServlet testObj = new GetProductsServlet(database);
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
    }
}
