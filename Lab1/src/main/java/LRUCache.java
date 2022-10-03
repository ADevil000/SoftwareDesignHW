import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.NoSuchElementException;

interface Cache<K, T> {
    T get(K key);
    void put(K key, T value);
}

public class LRUCache<K, V> implements Cache<K, V> {
    private final int capacity;
    @NotNull private final HashMap<K, Node<Pair<K, V>>> nodes;
    @NotNull private final MyList<Pair<K, V>> list;

    public LRUCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be more than 0");
        }
        this.capacity = capacity;
        this.nodes = new HashMap<>(capacity);
        this.list = new MyList<>();
    }

    @Override
    public V get(@NotNull K key) {
        assert list.getSize() == getSize();
        Node<Pair<K, V>> node = nodes.get(key);
        if (node == null) {
            throw new NoSuchElementException("Element with key: " + key + " not found.");
        }
        list.rebaseNode(node);
        assert list.getSize() == getSize();
        assert list.getTail() == node;
        return node.value.value;
    }

    @Override
    public void put(@NotNull K key, @NotNull V value) {
        assert list.getSize() == getSize();
        Node<Pair<K, V>> node = nodes.get(key);
        if (node == null) {
            if (nodes.size() == capacity) {
                assert list.getSize() == getSize();
                Node<Pair<K, V>> prevHead = list.dropHead();
                boolean check = nodes.remove(prevHead.value.key, prevHead);
                assert check;
                assert list.getSize() == getSize() && nodes.size() < capacity;
            }
            node = list.updateTail(new Pair<>(key, value));
            nodes.put(key, node);
        } else {
            node.value.value = value;
            list.rebaseNode(node);
        }
        assert list.getSize() == getSize() && 0 < nodes.size();
        assert list.getTail() == node;
    }

    public int getSize() {
        assert nodes.size() <= capacity;
        return nodes.size();
    }

    private static class Pair<K, V> {
        private final K key;
        private V value;

        private Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}