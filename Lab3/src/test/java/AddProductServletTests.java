import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.MockedStatic;
import ru.akirakozov.sd.refactoring.servlet.AddProductServlet;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class AddProductServletTests {

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
    }

    @Test
    public void testAddValue() throws SQLException, IOException {
        String name = "iphone";
        String price = "600";
        when(request.getParameter("name")).thenReturn(name);
        when(request.getParameter("price")).thenReturn(price);

        when(resultSet.next()).thenReturn(false);

        final AddProductServlet testObj = new AddProductServlet();
        testObj.doGet(request, response);

        String expectedSQL = "INSERT INTO PRODUCT " + "(NAME, PRICE) VALUES (\"" + name + "\"," + price + ")";
        InOrder inOrder = inOrder(statement);
        inOrder.verify(statement).executeUpdate(expectedSQL);
        inOrder.verify(statement, never()).executeUpdate(anyString());
        inOrder.verify(statement).close();

        String[] expected = new String[]{"OK"};
        assertArrayEquals(expected, result.toArray());
        verify(response).setContentType("text/html");
        verify(response).setStatus(HttpServletResponse.SC_OK);

        verify(connection).close();
    }
}
