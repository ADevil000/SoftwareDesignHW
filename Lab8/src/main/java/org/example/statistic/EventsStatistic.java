package org.example.statistic;

import java.util.List;
import java.util.Map;

public interface EventsStatistic {
    void incEvent(String name);
    List<Integer> getEventStatisticByName(String name);
    Map<String, List<Integer>> getAllEventStatistic();
    void printStatistic();
}
