import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

public class LRUCacheTests {
    @Test
    void createEmptyCache() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new LRUCache<Integer, Integer>(0));
    }

    @Test
    void addOneElement() {
        LRUCache<Integer, Integer> cache = new LRUCache<>(1);
        cache.put(1, 1);
        Assertions.assertEquals(1, cache.get(1));
    }

    @Test
    void addSomeElementsUnderCapacity() {
        LRUCache<Integer, Integer> cache = new LRUCache<>(5);
        cache.put(1, 1);
        cache.put(2, 2);
        cache.put(3, 3);
        Assertions.assertAll(
                () -> Assertions.assertEquals(cache.get(1), 1),
                () -> Assertions.assertEquals(cache.get(2), 2),
                () -> Assertions.assertEquals(cache.get(3), 3)
        );
    }

    @Test
    void addMoreThanCapacity() {
        LRUCache<Integer, Integer> cache = new LRUCache<>(2);
        cache.put(1, 1);
        cache.put(2, 2);
        cache.put(3, 3);
        Assertions.assertAll(
                () -> Assertions.assertThrows(NoSuchElementException.class, () -> cache.get(1)),
                () -> Assertions.assertEquals(cache.get(2), 2),
                () -> Assertions.assertEquals(cache.get(3), 3)
        );
    }

    @Test
    void checkChangePriorityByGet() {
        LRUCache<Integer, Integer> cache = new LRUCache<>(2);
        cache.put(1, 1);
        cache.put(2, 2);
        cache.get(1);
        cache.put(3, 3);
        Assertions.assertAll(
                () -> Assertions.assertEquals(cache.get(1), 1),
                () -> Assertions.assertThrows(NoSuchElementException.class, () -> cache.get(2)),
                () -> Assertions.assertEquals(cache.get(3), 3)
        );
    }

    @Test
    void checkSizeWithDifferentElements() {
        LRUCache<Integer, Integer> cache = new LRUCache<>(2);
        Assertions.assertAll(
                () -> {
                    cache.put(1, 1);
                    Assertions.assertEquals(1, cache.getSize());
                },
                () -> {
                    cache.put(2, 2);
                    Assertions.assertEquals(2, cache.getSize());
                },
                () -> {
                    cache.put(3, 3);
                    Assertions.assertEquals(2, cache.getSize());
                }
        );
    }

    @Test
    void checkSizeWithSameKey() {
        LRUCache<Integer, Integer> cache = new LRUCache<>(2);
        Assertions.assertAll(
                () -> {
                    cache.put(1, 1);
                    Assertions.assertEquals(1, cache.getSize());
                },
                () -> {
                    cache.put(1, 2);
                    Assertions.assertEquals(1, cache.getSize());
                },
                () -> {
                    cache.put(1, 3);
                    Assertions.assertEquals(1, cache.getSize());
                }
        );
    }

    @Test
    void addElementWithSameKey() {
        LRUCache<Integer, Integer> cache = new LRUCache<>(2);
        cache.put(1, 1);
        cache.put(2, 2);
        cache.put(1, 3);
        Assertions.assertAll(
                () -> Assertions.assertEquals(cache.get(1), 3),
                () -> Assertions.assertEquals(cache.get(2), 2)
        );
    }

}
