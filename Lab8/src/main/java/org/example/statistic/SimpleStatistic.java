package org.example.statistic;

import org.example.clock.Clock;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class SimpleStatistic implements EventsStatistic {
    private Clock clock;
    Map<String, LinkedList<Instant>> eventsToTime = new HashMap<>();

    public SimpleStatistic(Clock clock) {
        this.clock = clock;
    }

    @Override
    public void incEvent(String name) {
        Instant now = clock.now();
        LinkedList<Instant> times = new LinkedList<>(List.of(now));
        eventsToTime.merge(name, times, SimpleStatistic::addNewEventByList);
    }

    private static LinkedList<Instant> addNewEventByList(List<Instant> oldValues, List<Instant> newValueList) {
        Instant newValue = newValueList.get(0);
        int lastHourStartIndex = 0;
        for (Instant oldValue : oldValues) {
            Instant timePlusHour = oldValue.plus(1, ChronoUnit.HOURS);
            if (!timePlusHour.isBefore(newValue)) {
                break;
            }
            lastHourStartIndex++;
        }
        LinkedList<Instant> result = new LinkedList<>(oldValues.subList(lastHourStartIndex, oldValues.size()));
        result.add(newValue);
        return result;
    }


    @Override
    public List<Integer> getEventStatisticByName(String name) {
        Instant endTime = clock.now();
        return timesToPerMinuteStat(eventsToTime.getOrDefault(name, new LinkedList<>()), endTime);
    }

    private List<Integer> timesToPerMinuteStat(LinkedList<Instant> times, Instant endTime) {
        Instant startTime = endTime.minus(1, ChronoUnit.HOURS);
        while (!times.isEmpty()) {
            Instant eventTime = times.getFirst();
            Instant eventTimePlusHour = eventTime.plus(1, ChronoUnit.HOURS);
            if (eventTimePlusHour.isBefore(endTime)) {
                times.removeFirst();
            } else {
                break;
            }
        }
        int[] eventsPerMinute = new int[60];
        for (Instant time : times) {
            if (time.equals(endTime)) {
                eventsPerMinute[eventsPerMinute.length - 1]++;
                continue;
            }
            int ind = (int) startTime.until(time, ChronoUnit.MINUTES);
            eventsPerMinute[ind]++;
        }
        return Arrays.stream(eventsPerMinute).boxed().collect(Collectors.toList());
    }

    @Override
    public Map<String, List<Integer>> getAllEventStatistic() {
        HashMap<String, List<Integer>> eventToStatistic = new HashMap<>();
        allEventsAction((key, value) -> {
            eventToStatistic.put(key, value);
            return null;
        });
        return eventToStatistic;
    }

    @Override
    public void printStatistic() {
        allEventsAction((name, statistic) -> {
            System.out.println(name + " " + statistic);
            return null;
        });
    }

    private void allEventsAction(BiFunction<String, List<Integer>, Void> action) {
        Instant endTime = clock.now();
        for (Map.Entry<String, LinkedList<Instant>> entry : eventsToTime.entrySet()) {
            String name = entry.getKey();
            LinkedList<Instant> events = entry.getValue();
            action.apply(name, timesToPerMinuteStat(events, endTime));
        }
    }
}
