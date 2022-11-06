package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.database.ProductDatabase;
import ru.akirakozov.sd.refactoring.database.SimpleProductDatabase;
import ru.akirakozov.sd.refactoring.html.HTMLWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author akirakozov
 */
public class GetProductsServlet extends HttpServlet {
    public final ProductDatabase database;

    public GetProductsServlet() {
        this.database  = new SimpleProductDatabase("jdbc:sqlite:test.db");
    }

    public GetProductsServlet(ProductDatabase database) {
        this.database = database;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String query = "SELECT * FROM PRODUCT";
        List<Map<String, String>> result = database.getNamesAndPrices(query);
        try (HTMLWriter writer = new HTMLWriter(response.getWriter())) {
            for (Map<String, String> pairs : result) {
                String name = pairs.get("name");
                String price  = pairs.get("price");
                writer.addParagraph(name + "\t" + price);
            }
        }

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
