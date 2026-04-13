import java.util.*;
import java.util.stream.*;

public class OptionalUsage {

    record User(String name, Address address) {}
    record Address(String city, String zip) {}

    static Map<Integer, User> userDb = Map.of(
        1, new User("Alice", new Address("NYC", "10001")),
        2, new User("Bob",   null),
        3, new User("Carol", new Address("SF",  "94102"))
    );

    static Optional<User> findUser(int id) {
        return Optional.ofNullable(userDb.get(id));
    }

    static Optional<Address> getAddress(User user) {
        return Optional.ofNullable(user.address());
    }

    public static void main(String[] args) {

        // --- EASY 1: orElse / orElseGet ---
        String city1 = findUser(1)
            .flatMap(OptionalUsage::getAddress)
            .map(Address::city)
            .orElse("Unknown");
        System.out.println("User 1 city: " + city1); // NYC

        String city2 = findUser(2)
            .flatMap(OptionalUsage::getAddress)
            .map(Address::city)
            .orElse("Unknown");
        System.out.println("User 2 city: " + city2); // Unknown

        // --- EASY 2: ifPresent ---
        findUser(3).ifPresent(u -> System.out.println("Found: " + u.name()));

        System.out.println("---");

        // --- MEDIUM 1: filter ---
        findUser(1)
            .filter(u -> u.name().startsWith("A"))
            .map(User::name)
            .ifPresent(n -> System.out.println("Name starts with A: " + n));

        // --- MEDIUM 2: orElseThrow ---
        try {
            User user = findUser(99)
                .orElseThrow(() -> new RuntimeException("User not found: 99"));
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("---");

        // --- HARD 1: Optional in stream pipeline ---
        List<Integer> ids = Arrays.asList(1, 2, 3, 99, 4);
        List<String> cities = ids.stream()
            .map(OptionalUsage::findUser)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(OptionalUsage::getAddress)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(Address::city)
            .collect(Collectors.toList());
        System.out.println("Cities: " + cities);

        // Cleaner with flatMap (Java 9+)
        List<String> cities2 = ids.stream()
            .map(OptionalUsage::findUser)
            .flatMap(Optional::stream)
            .map(OptionalUsage::getAddress)
            .flatMap(Optional::stream)
            .map(Address::city)
            .collect(Collectors.toList());
        System.out.println("Cities (flatMap): " + cities2);

        System.out.println("---");

        // --- HARD 2: chained Optional transformations ---
        String result = findUser(1)
            .flatMap(OptionalUsage::getAddress)
            .map(a -> a.city() + "-" + a.zip())
            .map(String::toUpperCase)
            .filter(s -> s.contains("NYC"))
            .orElseGet(() -> "No NYC address found");
        System.out.println(result); // NYC-10001
    }
}
