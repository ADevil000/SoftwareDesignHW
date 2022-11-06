package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.database.ProductDatabase;
import ru.akirakozov.sd.refactoring.database.SimpleProductDatabase;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author akirakozov
 */
public class QueryServlet extends HttpServlet {
    public final ProductDatabase database;
    public static final int NEUTRAL = Integer.MIN_VALUE;

    public QueryServlet() {
        this.database  = new SimpleProductDatabase("jdbc:sqlite:test.db");
    }

    public QueryServlet(ProductDatabase database) {
        this.database = database;
    }
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String command = request.getParameter("command");

        if ("max".equals(command)) {
            String query = "SELECT * FROM PRODUCT ORDER BY PRICE DESC LIMIT 1";
            List<Map<String, String>> result = database.getNamesAndPrices(query);

            response.getWriter().println("<html><body>");
            response.getWriter().println("<h1>Product with max price: </h1>");
            for (Map<String, String> pairs : result) {
                String name = pairs.get("name");
                String price  = pairs.get("price");
                response.getWriter().println(name + "\t" + price + "</br>");
            }
            response.getWriter().println("</body></html>");
        } else if ("min".equals(command)) {
            String query = "SELECT * FROM PRODUCT ORDER BY PRICE LIMIT 1";
            List<Map<String, String>> result = database.getNamesAndPrices(query);

            response.getWriter().println("<html><body>");
            response.getWriter().println("<h1>Product with min price: </h1>");
            for (Map<String, String> pairs : result) {
                String name = pairs.get("name");
                String price  = pairs.get("price");
                response.getWriter().println(name + "\t" + price + "</br>");
            }
            response.getWriter().println("</body></html>");
        } else if ("sum".equals(command)) {
            String query = "SELECT SUM(price) FROM PRODUCT";
            int result = database.getScalarNumber(query, NEUTRAL);

            response.getWriter().println("<html><body>");
            response.getWriter().println("Summary price: ");
            if (result != NEUTRAL) {
                response.getWriter().println(result);
            }
            response.getWriter().println("</body></html>");
        } else if ("count".equals(command)) {
            String query = "SELECT COUNT(*) FROM PRODUCT";
            int result = database.getScalarNumber(query, NEUTRAL);

            response.getWriter().println("<html><body>");
            response.getWriter().println("Number of products: ");
            if (result != NEUTRAL) {
                response.getWriter().println(result);
            }
            response.getWriter().println("</body></html>");
        } else {
            response.getWriter().println("Unknown command: " + command);
        }

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }

}
