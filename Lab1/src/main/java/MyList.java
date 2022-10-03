import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;

public class MyList<V> {
    private Node<V> head, tail;
    private int size = 0;

    public @NotNull Node<V> getHead() {
        if (head == null) {
            throw new NoSuchElementException("Empty list");
        }
        return head;
    }

    public @NotNull Node<V> getTail() {
        if (tail == null) {
            throw new NoSuchElementException("Empty list");
        }
        return tail;
    }

    public int getSize() {
        return size;
    }

    private boolean contains(@NotNull Node<V> node) {
        Node<V> curNode = head;
        while (curNode != null) {
            if (curNode == node) {
                return true;
            } else {
                curNode = curNode.getNext();
            }
        }
        return false;
    }

    public void rebaseNode(@NotNull Node<V> node) {
        int startSize = size;
        assert 0 < size;
        assert head != null && tail != null;
        assert contains(node);
        if (tail != node) {
            if (node == head) {
                head = node.getNext();
            }
            node.dropAndTie();
            size--;
            assert 0 <= size;
            updateTailWithNode(node);
        }
        assert head != null && tail == node;
        assert 0 < size && startSize == size;
    }

    public @NotNull Node<V> dropHead() {
        if (size == 0) {
            throw new NoSuchElementException("Empty list");
        }
        int startSize = size;
        assert 0 < size;
        assert head != null && tail != null;
        Node<V> prevHead = head;
        size--;
        if (size == 0) {
            head = null;
            tail = null;
        } else {
            head = head.getNext();
            assert head != prevHead;
        }
        assert 0 <= size && startSize - 1 == size;
        return prevHead;
    }

    public @NotNull Node<V> updateTail(@NotNull V value) {
        Node<V> node = new Node<>(value);
        updateTailWithNode(node);
        return node;
    }

    private void updateTailWithNode(@NotNull Node<V> node) {
        int startSize = size;
        assert 0 <= size;
        if (size == 0) {
            head = node;
        }
        if (tail != null) {
            Node.unit(tail, node);
            assert tail.getNext() == node && node.getPrev() == tail;
        }
        tail = node;
        size++;
        assert 0 < size && startSize + 1 == size;
        assert head != null && tail == node;
    }
}

class Node<V> {
    @NotNull public V value;
    private Node<V> next, prev;

    public Node(@NotNull V value) {
        this.value = value;
    }

    void dropAndTie() {
        if (prev != null) {
            assert prev.next == this;
            prev.setNext(next);
            assert prev.next != this;
        }
        if (next != null) {
            assert next.prev == this;
            next.setPrev(prev);
            assert next.prev != this;
        }
        prev = null;
        next = null;
    }

    public static <V> void unit(@NotNull Node<V> fst, @NotNull Node<V> snd) {
        assert fst != snd;
        assert fst.getNext() == null;
        assert snd.getPrev() == null;
        fst.setNext(snd);
        snd.setPrev(fst);
        assert fst.getNext().getPrev() == fst && snd.getPrev().getNext() == snd;
    }

    public Node<V> getPrev() {
        assert prev != this && (prev == null || prev.next == this);
        return prev;
    }

    public void setPrev(Node<V> prev) {
        assert prev != this;
        this.prev = prev;
    }

    public void setNext(Node<V> next) {
        assert next != this;
        this.next = next;
    }

    public Node<V> getNext() {
        assert next != this && (next == null || next.prev == this);
        return next;
    }
}