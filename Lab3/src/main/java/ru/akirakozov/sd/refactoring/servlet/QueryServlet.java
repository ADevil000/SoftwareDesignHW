package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.database.ProductDatabase;
import ru.akirakozov.sd.refactoring.database.SimpleProductDatabase;
import ru.akirakozov.sd.refactoring.html.HTMLWriter;

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
        this.database = new SimpleProductDatabase("jdbc:sqlite:test.db");
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
        PrintWriter printWriter = response.getWriter();

        switch (action) {
            case MAX:
                max(printWriter);
                break;
            case MIN:
                min(printWriter);
                break;
            case SUM:
                sum(printWriter);
                break;
            case COUNT:
                count(printWriter);
                break;
            case UNKNOWN:
                printWriter.println("Unknown command: " + command);
                break;
        }

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void max(PrintWriter writer) {
        String query = "SELECT * FROM PRODUCT ORDER BY PRICE DESC LIMIT 1";
        String info = "Product with max price: ";
        doSortQuery(writer, query, info);
    }

    private void min(PrintWriter writer) {
        String query = "SELECT * FROM PRODUCT ORDER BY PRICE LIMIT 1";
        String info = "Product with min price: ";
        doSortQuery(writer, query, info);
    }

    private void doSortQuery(PrintWriter printWriter, String query, String header) {
        List<Map<String, String>> result = database.getNamesAndPrices(query);
        try (HTMLWriter writer = new HTMLWriter(printWriter)) {
            writer.addHeader1(header);
            for (Map<String, String> pairs : result) {
                String name = pairs.get("name");
                String price = pairs.get("price");
                writer.addParagraph(name + "\t" + price);
            }
        }
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

    private void doScalarQuery(PrintWriter printWriter, String query, String info) {
        int result = database.getScalarNumber(query, EMPTY);
        try (HTMLWriter writer = new HTMLWriter(printWriter)) {
            writer.addLine(info);
            if (result != EMPTY) {
                writer.addLine(String.valueOf(result));
            }
        }
    }

}
