import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class BuiltIn {

    public static void main(String[] args) {

        // --- Predicate ---
        Predicate<Integer> isEven = n -> n % 2 == 0;
        Predicate<Integer> isPositive = n -> n > 0;
        Predicate<Integer> isPositiveEven = isEven.and(isPositive);

        List<Integer> nums = Arrays.asList(-4, -1, 0, 3, 6, 8);
        nums.stream().filter(isPositiveEven).forEach(System.out::println); // 6, 8

        System.out.println("---");

        // --- Function ---
        Function<String, Integer> strLen = String::length;
        Function<Integer, String> intToStr = n -> "Length: " + n;
        Function<String, String> pipeline = strLen.andThen(intToStr);

        System.out.println(pipeline.apply("Hello")); // Length: 5

        System.out.println("---");

        // --- Consumer ---
        Consumer<String> print = System.out::println;
        Consumer<String> printUpper = s -> System.out.println(s.toUpperCase());
        Consumer<String> both = print.andThen(printUpper);

        both.accept("java"); // java \n JAVA

        System.out.println("---");

        // --- Supplier ---
        Supplier<List<String>> listFactory = ArrayList::new;
        List<String> list1 = listFactory.get();
        List<String> list2 = listFactory.get();
        list1.add("a");
        System.out.println(list1); // [a]
        System.out.println(list2); // []  — separate instances

        System.out.println("---");

        // --- BiFunction ---
        BiFunction<String, Integer, String> repeat = (s, n) -> s.repeat(n);
        System.out.println(repeat.apply("ab", 3)); // ababab

        System.out.println("---");

        // --- UnaryOperator ---
        UnaryOperator<String> trim = String::trim;
        UnaryOperator<String> upper = String::toUpperCase;
        UnaryOperator<String> normalize = trim.andThen(upper)::apply;
        System.out.println(normalize.apply("  hello  ")); // HELLO

        System.out.println("---");

        // --- BinaryOperator ---
        BinaryOperator<Integer> max = (a, b) -> a > b ? a : b;
        System.out.println(max.apply(10, 25)); // 25

        // Reduce with BinaryOperator
        List<Integer> values = Arrays.asList(3, 7, 2, 9, 1);
        int maxVal = values.stream().reduce(Integer.MIN_VALUE, max);
        System.out.println(maxVal); // 9
    }
}
