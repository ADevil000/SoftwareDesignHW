package ru.akirakozov.sd.refactoring.database;

import java.util.List;
import java.util.Map;

public interface ProductDatabase {
    int getScalarNumber(String query, int empty);

    List<Map<String, String>> getNamesAndPrices(String query);

    void addValue(String sql);
}
