import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.NoSuchElementException;
import java.util.stream.IntStream;

public class MyListTests {
    private MyList<Integer> list;

    @BeforeEach
    void initList() {
        list = new MyList<>();
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    void addElement(int value) {
        list.updateTail(value);
        Assertions.assertEquals(value, list.getTail().value);
    }

    @Test
    void addSomeElements() {
        IntStream.range(0, 5).forEach(list::updateTail);
        Assertions.assertArrayEquals(IntStream.range(0, 5).toArray(), IntStream.range(0, 5).map(i -> list.dropHead().value).toArray());
    }

    @Test
    void removeFromEmptyList() {
        Assertions.assertThrows(NoSuchElementException.class, () -> list.dropHead());
    }

    @Test
    void testRebaseWithOneElement() {
        Node<Integer> node = list.updateTail(1);
        Assertions.assertAll(
                () -> Assertions.assertEquals(node, list.getHead()),
                () -> Assertions.assertEquals(node, list.getTail()),
                () -> {
                    list.rebaseNode(node);
                    Assertions.assertAll(
                            () -> Assertions.assertEquals(node, list.getHead()),
                            () -> Assertions.assertEquals(node, list.getTail())
                    );
                }
        );
    }

    @Test
    void testSomeRebases() {
        Node<Integer> node1 = list.updateTail(1);
        Node<Integer> node2 = list.updateTail(2);
        Node<Integer> node3 = list.updateTail(3);
        list.rebaseNode(node2);
        Assertions.assertAll(
                () -> {
                    list.rebaseNode(node2);
                    Assertions.assertEquals(node2.value, list.getTail().value);
                },
                () -> {
                    list.rebaseNode(node1);
                    Assertions.assertEquals(node1.value, list.getTail().value);
                },
                () -> {
                    list.rebaseNode(node3);
                    Assertions.assertEquals(node3.value, list.getTail().value);
                },
                () -> {
                    list.rebaseNode(node1);
                    Assertions.assertEquals(node1.value, list.getTail().value);
                },
                () -> {
                    list.rebaseNode(node1);
                    Assertions.assertEquals(node1.value, list.getTail().value);
                }
        );
    }

    @Test
    void checkSize() {
        Assertions.assertAll(
                () -> Assertions.assertEquals(0, list.getSize()),
                () -> {
                    list.updateTail(1);
                    Assertions.assertEquals(1, list.getSize());
                },
                () -> {
                    list.updateTail(2);
                    Assertions.assertEquals(2, list.getSize());
                },
                () -> {
                    list.updateTail(3);
                    Assertions.assertEquals(3, list.getSize());
                },
                () -> {
                    list.dropHead();
                    Assertions.assertEquals(2, list.getSize());
                },
                () -> {
                    list.dropHead();
                    Assertions.assertEquals(1, list.getSize());
                },
                () -> {
                    list.dropHead();
                    Assertions.assertEquals(0, list.getSize());
                }
        );
    }
}
