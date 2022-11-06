package ru.akirakozov.sd.refactoring.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleProductDatabase implements ProductDatabase {
    String databasePath;

    public SimpleProductDatabase(String databasePath) {
        this.databasePath = databasePath;
    }

    public int getScalarNumber(String query, int neutral) {
        int result = neutral;
        try (Connection c = DriverManager.getConnection(databasePath)) {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                result = rs.getInt(1);
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public List<Map<String, String>> getNamesAndPrices(String query) {
        ArrayList<Map<String, String>> result = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(databasePath)) {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                HashMap<String, String> record = new HashMap<>();
                String name = rs.getString("name");
                record.put("name", name);
                int price  = rs.getInt("price");
                record.put("price", String.valueOf(price));
                result.add(record);
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public void addValue(String sql) {
        try (Connection c = DriverManager.getConnection(databasePath)) {
            Statement stmt = c.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
