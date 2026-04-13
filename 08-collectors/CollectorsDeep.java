import java.util.*;
import java.util.stream.*;
import java.util.function.*;

public class CollectorsDeep {

    record Transaction(String id, String type, String region, double amount, boolean successful) {}

    static List<Transaction> transactions() {
        return Arrays.asList(
            new Transaction("T1", "PURCHASE", "NORTH", 250.0,  true),
            new Transaction("T2", "REFUND",   "SOUTH", 80.0,   true),
            new Transaction("T3", "PURCHASE", "NORTH", 420.0,  false),
            new Transaction("T4", "PURCHASE", "EAST",  310.0,  true),
            new Transaction("T5", "REFUND",   "NORTH", 50.0,   true),
            new Transaction("T6", "PURCHASE", "SOUTH", 190.0,  true),
            new Transaction("T7", "PURCHASE", "EAST",  560.0,  false),
            new Transaction("T8", "PURCHASE", "WEST",  130.0,  true)
        );
    }

    public static void main(String[] args) {

        List<Transaction> txns = transactions();

        // --- groupingBy + counting ---
        System.out.println("=== Count by Type ===");
        txns.stream()
            .collect(Collectors.groupingBy(Transaction::type, Collectors.counting()))
            .forEach((type, cnt) -> System.out.println(type + ": " + cnt));

        System.out.println();

        // --- groupingBy + summingDouble ---
        System.out.println("=== Revenue by Region (successful only) ===");
        txns.stream()
            .filter(Transaction::successful)
            .collect(Collectors.groupingBy(
                Transaction::region,
                Collectors.summingDouble(Transaction::amount)
            ))
            .entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .forEach(e -> System.out.printf("%-8s $%.2f%n", e.getKey(), e.getValue()));

        System.out.println();

        // --- partitioningBy ---
        System.out.println("=== Partition by Success ===");
        Map<Boolean, List<Transaction>> partitioned = txns.stream()
            .collect(Collectors.partitioningBy(Transaction::successful));
        System.out.println("Successful: " + partitioned.get(true).size());
        System.out.println("Failed:     " + partitioned.get(false).size());

        System.out.println();

        // --- joining ---
        System.out.println("=== Transaction IDs ===");
        String ids = txns.stream()
            .map(Transaction::id)
            .collect(Collectors.joining(", ", "[", "]"));
        System.out.println(ids);

        System.out.println();

        // --- toMap with merge function ---
        System.out.println("=== Total Amount by Region (toMap) ===");
        Map<String, Double> regionTotals = txns.stream()
            .collect(Collectors.toMap(
                Transaction::region,
                Transaction::amount,
                Double::sum
            ));
        regionTotals.forEach((r, a) -> System.out.printf("%-8s $%.2f%n", r, a));

        System.out.println();

        // --- collectingAndThen ---
        System.out.println("=== Unmodifiable list of successful IDs ===");
        List<String> successIds = txns.stream()
            .filter(Transaction::successful)
            .map(Transaction::id)
            .collect(Collectors.collectingAndThen(
                Collectors.toList(),
                Collections::unmodifiableList
            ));
        System.out.println(successIds);

        System.out.println();

        // --- nested groupingBy ---
        System.out.println("=== Nested: Region → Type → Count ===");
        txns.stream()
            .collect(Collectors.groupingBy(
                Transaction::region,
                Collectors.groupingBy(Transaction::type, Collectors.counting())
            ))
            .forEach((region, typeMap) -> {
                System.out.println(region + ":");
                typeMap.forEach((type, cnt) -> System.out.println("  " + type + ": " + cnt));
            });

        System.out.println();

        // --- summarizingDouble ---
        System.out.println("=== Amount Statistics ===");
        DoubleSummaryStatistics stats = txns.stream()
            .collect(Collectors.summarizingDouble(Transaction::amount));
        System.out.printf("Count=%d, Sum=%.2f, Avg=%.2f, Min=%.2f, Max=%.2f%n",
            stats.getCount(), stats.getSum(), stats.getAverage(), stats.getMin(), stats.getMax());
    }
}
