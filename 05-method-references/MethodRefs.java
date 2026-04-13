import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class MethodRefs {

    static int doubleIt(int n) { return n * 2; }

    record Person(String name, int age) {
        String display() { return name + " (" + age + ")"; }
    }

    public static void main(String[] args) {

        // --- Static method reference ---
        Function<String, Integer> parse = Integer::parseInt;
        System.out.println(parse.apply("42")); // 42

        List<String> numStrs = Arrays.asList("3", "1", "4", "1", "5");
        numStrs.stream()
               .map(Integer::parseInt)
               .sorted()
               .forEach(System.out::println);

        System.out.println("---");

        // --- Unbound instance method reference ---
        List<String> words = Arrays.asList("hello", "world", "java");
        words.stream()
             .map(String::toUpperCase)
             .forEach(System.out::println);

        // BiFunction — first param is the receiver
        BiFunction<String, String, Boolean> startsWith = String::startsWith;
        System.out.println(startsWith.apply("Hello", "He")); // true

        System.out.println("---");

        // --- Bound instance method reference ---
        String prefix = "Java";
        Predicate<String> startsWithJava = prefix::startsWith; // bound to "Java"
        List<String> langs = Arrays.asList("Java", "JavaScript", "Python", "JavaFX");
        langs.stream()
             .filter(startsWithJava)
             .forEach(System.out::println);

        System.out.println("---");

        // --- Constructor reference ---
        Function<String, StringBuilder> sbFactory = StringBuilder::new;
        StringBuilder sb = sbFactory.apply("initial");
        System.out.println(sb); // initial

        // Collect to specific collection type
        List<String> names = Arrays.asList("Alice", "Bob", "Carol");
        List<Person> people = names.stream()
            .map(n -> new Person(n, 25))
            .collect(Collectors.toList());

        people.stream()
              .map(Person::display) // unbound instance
              .forEach(System.out::println);

        System.out.println("---");

        // --- Practical: Comparator with method reference ---
        people.stream()
              .sorted(Comparator.comparing(Person::name))
              .map(Person::display)
              .forEach(System.out::println);

        // --- Static method from this class ---
        List<Integer> nums = Arrays.asList(1, 2, 3, 4, 5);
        nums.stream()
            .map(MethodRefs::doubleIt)
            .forEach(System.out::println);
    }
}
