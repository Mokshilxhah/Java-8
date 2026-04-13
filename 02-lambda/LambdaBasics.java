import java.util.*;
import java.util.function.*;

public class LambdaBasics {

    public static void main(String[] args) {

        // --- EASY 1: Runnable ---
        Runnable greet = () -> System.out.println("Hello from lambda");
        greet.run();

        // --- EASY 2: Comparator ---
        List<String> names = Arrays.asList("Charlie", "Alice", "Bob");
        names.sort((a, b) -> a.compareTo(b));
        System.out.println(names); // [Alice, Bob, Charlie]

        // --- MEDIUM 1: Predicate ---
        Predicate<String> isLong = s -> s.length() > 4;
        System.out.println(isLong.test("Hi"));      // false
        System.out.println(isLong.test("Hello"));   // true

        // --- MEDIUM 2: Function chaining ---
        Function<String, String> trim = String::trim;
        Function<String, String> upper = String::toUpperCase;
        Function<String, String> process = trim.andThen(upper);
        System.out.println(process.apply("  hello world  ")); // HELLO WORLD

        // --- HARD 1: BiFunction with custom logic ---
        BiFunction<List<Integer>, Integer, List<Integer>> filterAbove =
            (list, threshold) -> {
                List<Integer> result = new ArrayList<>();
                list.forEach(n -> { if (n > threshold) result.add(n); });
                return result;
            };
        List<Integer> numbers = Arrays.asList(1, 5, 3, 8, 2, 9);
        System.out.println(filterAbove.apply(numbers, 4)); // [5, 8, 9]

        // --- HARD 2: Lambda returning lambda (higher-order function) ---
        Function<Integer, Predicate<Integer>> greaterThan = threshold -> n -> n > threshold;
        Predicate<Integer> greaterThan5 = greaterThan.apply(5);
        numbers.stream()
               .filter(greaterThan5)
               .forEach(System.out::println); // 8, 9
    }
}
