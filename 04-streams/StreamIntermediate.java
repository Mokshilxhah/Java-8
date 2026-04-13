import java.util.*;
import java.util.stream.*;

public class StreamIntermediate {

    record Product(String name, String category, double price, int stock) {}

    public static void main(String[] args) {

        List<Product> products = Arrays.asList(
            new Product("Laptop",    "Electronics", 999.99, 50),
            new Product("Phone",     "Electronics", 699.99, 200),
            new Product("Desk",      "Furniture",   299.99, 30),
            new Product("Chair",     "Furniture",   199.99, 75),
            new Product("Headphones","Electronics", 149.99, 100),
            new Product("Lamp",      "Furniture",    49.99, 0),
            new Product("Tablet",    "Electronics", 499.99, 15)
        );

        // --- EASY 1: filter + map ---
        System.out.println("In-stock Electronics:");
        products.stream()
                .filter(p -> p.category().equals("Electronics"))
                .filter(p -> p.stock() > 0)
                .map(p -> p.name() + " ($" + p.price() + ")")
                .forEach(System.out::println);

        System.out.println("---");

        // --- EASY 2: sorted by price ---
        System.out.println("Products by price (asc):");
        products.stream()
                .sorted(Comparator.comparingDouble(Product::price))
                .map(p -> String.format("%-15s $%.2f", p.name(), p.price()))
                .forEach(System.out::println);

        System.out.println("---");

        // --- MEDIUM 1: peek for debugging ---
        System.out.println("Expensive products pipeline:");
        List<String> expensive = products.stream()
            .filter(p -> p.price() > 400)
            .peek(p -> System.out.println("  After filter: " + p.name()))
            .map(Product::name)
            .peek(n -> System.out.println("  After map: " + n))
            .sorted()
            .collect(Collectors.toList());
        System.out.println("Result: " + expensive);

        System.out.println("---");

        // --- MEDIUM 2: flatMap on tags ---
        List<String> sentences = Arrays.asList(
            "laptop phone tablet",
            "desk chair lamp",
            "headphones speaker"
        );

        List<String> words = sentences.stream()
            .flatMap(s -> Arrays.stream(s.split(" ")))
            .distinct()
            .sorted()
            .collect(Collectors.toList());

        System.out.println("All words: " + words);

        System.out.println("---");

        // --- HARD 1: multi-level sort ---
        System.out.println("Sorted by category, then price desc:");
        products.stream()
                .sorted(Comparator.comparing(Product::category)
                                  .thenComparing(Comparator.comparingDouble(Product::price).reversed()))
                .map(p -> String.format("%-15s %-12s $%.2f", p.name(), p.category(), p.price()))
                .forEach(System.out::println);

        System.out.println("---");

        // --- HARD 2: mapToDouble for stats ---
        DoubleSummaryStatistics stats = products.stream()
            .filter(p -> p.stock() > 0)
            .mapToDouble(Product::price)
            .summaryStatistics();

        System.out.printf("Count: %d, Min: $%.2f, Max: $%.2f, Avg: $%.2f, Sum: $%.2f%n",
            stats.getCount(), stats.getMin(), stats.getMax(), stats.getAverage(), stats.getSum());
    }
}
