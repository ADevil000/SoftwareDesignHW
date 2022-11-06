package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.database.ProductDatabase;
import ru.akirakozov.sd.refactoring.database.SimpleProductDatabase;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * @author akirakozov
 */
public class QueryServlet extends HttpServlet {
    private enum Action {
        MAX, MIN, SUM, COUNT, UNKNOWN
    }
    public final ProductDatabase database;
    public static final int EMPTY = Integer.MIN_VALUE;

    public QueryServlet() {
        this.database  = new SimpleProductDatabase("jdbc:sqlite:test.db");
    }

    public QueryServlet(ProductDatabase database) {
        this.database = database;
    }
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String command = request.getParameter("command");
        Action action;
        try {
            String rawValue = command.toUpperCase();
            action = Action.valueOf(rawValue);
        } catch (Exception ignored) {
            action = Action.UNKNOWN;
        }
        PrintWriter writer = response.getWriter();

        switch (action) {
            case MAX:
                max(writer);
                break;
            case MIN:
                min(writer);
                break;
            case SUM:
                sum(writer);
                break;
            case COUNT:
                count(writer);
                break;
            case UNKNOWN:
                writer.println("Unknown command: " + command);
                break;
        }

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void max(PrintWriter writer) {
        String query = "SELECT * FROM PRODUCT ORDER BY PRICE DESC LIMIT 1";
        String info = "<h1>Product with max price: </h1>";
        doSortQuery(writer, query, info);
    }

    private void min(PrintWriter writer) {
        String query = "SELECT * FROM PRODUCT ORDER BY PRICE LIMIT 1";
        String info = "<h1>Product with min price: </h1>";
        doSortQuery(writer, query, info);
    }

    private void doSortQuery(PrintWriter writer, String query, String info) {
        List<Map<String, String>> result = database.getNamesAndPrices(query);

        writer.println("<html><body>");
        writer.println(info);
        for (Map<String, String> pairs : result) {
            String name = pairs.get("name");
            String price  = pairs.get("price");
            writer.println(name + "\t" + price + "</br>");
        }
        writer.println("</body></html>");
    }

    private void sum(PrintWriter writer) {
        String query = "SELECT SUM(price) FROM PRODUCT";
        String info = "Summary price: ";
        doScalarQuery(writer, query, info);
    }

    private void count(PrintWriter writer) {
        String query = "SELECT COUNT(*) FROM PRODUCT";
        String info = "Number of products: ";
        doScalarQuery(writer, query, info);
    }

    private void doScalarQuery(PrintWriter writer, String query, String info) {
        int result = database.getScalarNumber(query, EMPTY);

        writer.println("<html><body>");
        writer.println(info);
        if (result != EMPTY) {
            writer.println(result);
        }
        writer.println("</body></html>");
    }

}
