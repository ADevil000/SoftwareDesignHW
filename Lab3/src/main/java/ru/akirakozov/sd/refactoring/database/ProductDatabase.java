package ru.akirakozov.sd.refactoring.database;

import java.util.List;
import java.util.Map;

public interface ProductDatabase {
    public int getScalarNumber(String query, int neutral);

    public List<Map<String, String>> getNamesAndPrices(String query);

    public void addValue(String sql);
}
