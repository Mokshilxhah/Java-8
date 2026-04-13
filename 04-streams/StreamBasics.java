import java.util.*;
import java.util.stream.*;

public class StreamBasics {

    public static void main(String[] args) {

        // --- EASY 1: filter + map + collect ---
        List<String> names = Arrays.asList("Alice", "Bob", "Anna", "Charlie", "Amy");

        List<String> aNames = names.stream()
            .filter(n -> n.startsWith("A"))
            .map(String::toUpperCase)
            .sorted()
            .collect(Collectors.toList());

        System.out.println(aNames); // [ALICE, AMY, ANNA]

        // --- EASY 2: count + anyMatch ---
        long count = names.stream().filter(n -> n.length() > 3).count();
        System.out.println("Names longer than 3: " + count); // 3

        boolean hasShort = names.stream().anyMatch(n -> n.length() <= 3);
        System.out.println("Has short name: " + hasShort); // true

        System.out.println("---");

        // --- MEDIUM 1: flatMap ---
        List<List<Integer>> nested = Arrays.asList(
            Arrays.asList(1, 2, 3),
            Arrays.asList(4, 5),
            Arrays.asList(6, 7, 8, 9)
        );

        List<Integer> flat = nested.stream()
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

        System.out.println(flat); // [1, 2, 3, 4, 5, 6, 7, 8, 9]

        // --- MEDIUM 2: reduce ---
        int sum = flat.stream().reduce(0, Integer::sum);
        System.out.println("Sum: " + sum); // 45

        Optional<Integer> product = flat.stream().reduce((a, b) -> a * b);
        product.ifPresent(p -> System.out.println("Product: " + p));

        System.out.println("---");

        // --- HARD 1: distinct + sorted + limit + skip ---
        List<Integer> nums = Arrays.asList(5, 3, 1, 4, 1, 5, 9, 2, 6, 5, 3);

        List<Integer> processed = nums.stream()
            .distinct()
            .sorted()
            .skip(2)
            .limit(4)
            .collect(Collectors.toList());

        System.out.println(processed); // [3, 4, 5, 6]

        // --- HARD 2: Stream.generate + limit (infinite stream) ---
        List<Integer> fibonacci = Stream.iterate(new int[]{0, 1}, f -> new int[]{f[1], f[0] + f[1]})
            .limit(10)
            .map(f -> f[0])
            .collect(Collectors.toList());

        System.out.println(fibonacci); // [0, 1, 1, 2, 3, 5, 8, 13, 21, 34]
    }
}
