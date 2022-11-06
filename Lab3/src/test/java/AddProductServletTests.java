import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.akirakozov.sd.refactoring.database.ProductDatabase;
import ru.akirakozov.sd.refactoring.servlet.AddProductServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class AddProductServletTests {

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
    }

    @Test
    public void testAddValue() throws IOException {
        String name = "iphone";
        String price = "600";
        when(request.getParameter("name")).thenReturn(name);
        when(request.getParameter("price")).thenReturn(price);

        final AddProductServlet testObj = new AddProductServlet(database);
        testObj.doGet(request, response);

        String expectedSQL = "INSERT INTO PRODUCT " + "(NAME, PRICE) VALUES (\"" + name + "\"," + price + ")";
        verify(database).addValue(expectedSQL);

        String[] expected = new String[]{"OK"};
        assertArrayEquals(expected, result.toArray());
        verify(response).setContentType("text/html");
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }
}
