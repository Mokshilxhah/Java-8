import java.util.*;
import java.util.function.*;

public class CustomFI {

    // Custom FI — meaningful domain name
    @FunctionalInterface
    interface Validator<T> {
        boolean validate(T value);

        default Validator<T> and(Validator<T> other) {
            return value -> this.validate(value) && other.validate(value);
        }

        default Validator<T> or(Validator<T> other) {
            return value -> this.validate(value) || other.validate(value);
        }
    }

    // Custom FI — checked exception support
    @FunctionalInterface
    interface ThrowingFunction<T, R> {
        R apply(T t) throws Exception;

        static <T, R> Function<T, R> wrap(ThrowingFunction<T, R> fn) {
            return t -> {
                try {
                    return fn.apply(t);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
        }
    }

    // Custom FI — tri-function (not in standard library)
    @FunctionalInterface
    interface TriFunction<A, B, C, R> {
        R apply(A a, B b, C c);
    }

    record User(String name, String email, int age) {}

    public static void main(String[] args) {

        // --- Validator composition ---
        Validator<User> hasName  = u -> u.name() != null && !u.name().isBlank();
        Validator<User> hasEmail = u -> u.email() != null && u.email().contains("@");
        Validator<User> isAdult  = u -> u.age() >= 18;

        Validator<User> fullValidation = hasName.and(hasEmail).and(isAdult);

        User valid   = new User("Alice", "alice@example.com", 25);
        User invalid = new User("", "not-an-email", 15);

        System.out.println(fullValidation.validate(valid));   // true
        System.out.println(fullValidation.validate(invalid)); // false

        System.out.println("---");

        // --- ThrowingFunction wrapping ---
        List<String> inputs = Arrays.asList("1", "2", "abc", "4");
        inputs.stream()
              .map(ThrowingFunction.wrap(Integer::parseInt))
              .forEach(System.out::println); // throws RuntimeException on "abc"

        System.out.println("---");

        // --- TriFunction ---
        TriFunction<String, String, Integer, User> createUser =
            (name, email, age) -> new User(name, email, age);

        User u = createUser.apply("Bob", "bob@example.com", 30);
        System.out.println(u);
    }
}
