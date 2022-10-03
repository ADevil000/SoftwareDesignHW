import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            LRUCache<Integer, Integer> cache = new LRUCache<>(scanner.nextInt());
            while (true) {
                String type = scanner.next();
                switch (type) {
                    case "p":
                        cache.put(scanner.nextInt(), scanner.nextInt());
                        break;
                    case "g":
                        try {
                            System.out.println(cache.get(scanner.nextInt()));
                        } catch (NoSuchElementException e) {
                            System.out.println(e);
                        }
                        break;
                    default:
                        return;
                }
            }
        } catch (Exception ignored) {}
    }
}
