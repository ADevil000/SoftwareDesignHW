import org.example.clock.SettableClock;
import org.example.statistic.SimpleStatistic;
import org.junit.jupiter.api.*;

import java.io.PrintStream;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.mockito.Mockito.*;

public class SimpleStatisticTests {
    private static final Instant START_TIME = Instant.parse("2022-01-01T00:00:00.00Z");
    SettableClock clock;
    ArrayList<String> output;
    PrintStream stream;

    @BeforeEach
    public void setUp() {
        clock = new SettableClock(START_TIME);

        stream = mock(PrintStream.class);
        output = new ArrayList<>();
        doAnswer(invocation -> {
            String s = invocation.getArgument(0, String.class);
            output.add(s);
            return null;
        }).when(stream).println(anyString());
    }

    @Test
    public void incrementOnceTest() {
        String tag = "test";
        SimpleStatistic statistic = new SimpleStatistic(clock);
        statistic.incEvent(tag);
        Map<String, LinkedList<Instant>> result = statistic.getEventsToTime();
        Map<String, List<Instant>> expected = Map.of(
                tag, List.of(clock.now())
        );
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void incrementManyInMinuteTest() {
        String tag = "test";
        SimpleStatistic statistic = new SimpleStatistic(clock);
        List<Instant> expectedList = new LinkedList<>();
        expectedList.add(clock.now());
        statistic.incEvent(tag);
        clock.setNow(clock.now().plus(1, ChronoUnit.MINUTES));
        expectedList.add(clock.now());
        statistic.incEvent(tag);
        clock.setNow(clock.now().plus(1, ChronoUnit.MINUTES));
        expectedList.add(clock.now());
        statistic.incEvent(tag);
        Map<String, LinkedList<Instant>> result = statistic.getEventsToTime();
        Map<String, List<Instant>> expected = Map.of(
                tag, expectedList
        );
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void incrementDifferentTest() {
        String tag1 = "test";
        String tag2 = "test2";
        String tag3 = "test3";
        SimpleStatistic statistic = new SimpleStatistic(clock);
        statistic.incEvent(tag1);
        statistic.incEvent(tag2);
        statistic.incEvent(tag3);
        Map<String, LinkedList<Instant>> result = statistic.getEventsToTime();
        Map<String, List<Instant>> expected = Map.of(
                tag1, List.of(clock.now()),
                tag2, List.of(clock.now()),
                tag3, List.of(clock.now())
        );
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void getEventStatisticByNameAtStartTest() {
        String tag = "test";
        SimpleStatistic statistic = new SimpleStatistic(clock);
        statistic.incEvent(tag);
        clock.setNow(clock.now().plus(1, ChronoUnit.HOURS));
        List<Integer> result = statistic.getEventStatisticByName(tag);
        List<Integer> expected = new ArrayList<>(60);
        expected.add(1);
        for (int i = 1; i < 60; i++) {
            expected.add(0);
        }
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void getEventStatisticByNameAtEndTest() {
        String tag = "test";
        SimpleStatistic statistic = new SimpleStatistic(clock);
        statistic.incEvent(tag);
        List<Integer> result = statistic.getEventStatisticByName(tag);
        List<Integer> expected = new ArrayList<>(60);
        for (int i = 0; i < 59; i++) {
            expected.add(0);
        }
        expected.add(1);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void getEventStatisticByNameInEachMinuteTest() {
        String tag = "test";
        SimpleStatistic statistic = new SimpleStatistic(clock);
        List<Integer> expected = new ArrayList<>(60);
        for (int i = 0; i < 60; i++) {
            statistic.incEvent(tag);
            clock.setNow(clock.now().plus(1, ChronoUnit.MINUTES));
            expected.add(1);
        }
        List<Integer> result = statistic.getEventStatisticByName(tag);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void getAllEventsStatisticOneTagTest() {
        String tag = "test";
        SimpleStatistic statistic = new SimpleStatistic(clock);
        statistic.incEvent(tag);
        Map<String, List<Integer>> expected = Map.of(
                tag, IntStream.range(0, 60).map(i -> i == 59 ? 1 : 0).boxed().toList()
        );
        Map<String, List<Integer>> result = statistic.getAllEventStatistic();
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void getAllEventsStatisticDifferentTagsTest() {
        String tag1 = "test";
        String tag2 = "test2";
        SimpleStatistic statistic = new SimpleStatistic(clock);
        statistic.incEvent(tag1);
        statistic.incEvent(tag2);
        Map<String, List<Integer>> expected = Map.of(
                tag1, IntStream.range(0, 60).map(i -> i == 59 ? 1 : 0).boxed().toList(),
                tag2, IntStream.range(0, 60).map(i -> i == 59 ? 1 : 0).boxed().toList()
        );
        Map<String, List<Integer>> result = statistic.getAllEventStatistic();
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void getAllEventsStatisticOneTagInDifferentTimeTest() {
        String tag = "test";
        SimpleStatistic statistic = new SimpleStatistic(clock);
        statistic.incEvent(tag);
        clock.setNow(clock.now().plus(1, ChronoUnit.MINUTES));
        statistic.incEvent(tag);
        Map<String, List<Integer>> expected = Map.of(
                tag, IntStream.range(0, 60).map(i -> i <= 1 ? 1 : 0).boxed().toList()
        );
        clock.setNow(clock.now().plus(59, ChronoUnit.MINUTES));
        Map<String, List<Integer>> result = statistic.getAllEventStatistic();
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void printStatisticOneTagTest() {
        String tag = "test";
        SimpleStatistic statistic = new SimpleStatistic(clock, stream);
        statistic.incEvent(tag);
        List<String> expected = List.of(
                tag + " " + IntStream.range(0, 60).map(i -> i == 59 ? 1 : 0).boxed().toList()
        );
        statistic.printStatistic();
        Assertions.assertEquals(expected, output);
    }

    @Test
    public void printStatisticDifferentTagsTest() {
        String tag1 = "test";
        String tag2 = "test2";
        SimpleStatistic statistic = new SimpleStatistic(clock, stream);
        statistic.incEvent(tag1);
        statistic.incEvent(tag2);
        List<String> expected = List.of(
                tag1 + " " + IntStream.range(0, 60).map(i -> i == 59 ? 1 : 0).boxed().toList(),
                tag2 + " " + IntStream.range(0, 60).map(i -> i == 59 ? 1 : 0).boxed().toList()
        );
        statistic.printStatistic();
        output.sort(String::compareTo);
        Assertions.assertEquals(expected, output);
    }

    @Test
    public void printStatisticOneTagInDifferentTimeTest() {
        String tag = "test";
        SimpleStatistic statistic = new SimpleStatistic(clock, stream);
        statistic.incEvent(tag);
        clock.setNow(clock.now().plus(1, ChronoUnit.MINUTES));
        statistic.incEvent(tag);
        List<String> expected = List.of(
                tag + " " + IntStream.range(0, 60).map(i -> i <= 1 ? 1 : 0).boxed().toList()
        );
        clock.setNow(clock.now().plus(59, ChronoUnit.MINUTES));
        statistic.printStatistic();
        Assertions.assertEquals(expected, output);
    }

}
