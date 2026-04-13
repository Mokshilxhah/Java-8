import java.util.*;
import java.util.stream.*;
import java.util.function.*;

public class StreamTerminal {

    record Order(String id, String customer, String status, double amount) {}

    public static void main(String[] args) {

        List<Order> orders = Arrays.asList(
            new Order("O1", "Alice",   "COMPLETED", 250.00),
            new Order("O2", "Bob",     "PENDING",   180.00),
            new Order("O3", "Alice",   "COMPLETED", 320.00),
            new Order("O4", "Charlie", "CANCELLED",  90.00),
            new Order("O5", "Bob",     "COMPLETED", 410.00),
            new Order("O6", "Charlie", "PENDING",   150.00),
            new Order("O7", "Alice",   "PENDING",   200.00)
        );

        // --- EASY 1: collect to list + count ---
        List<Order> completed = orders.stream()
            .filter(o -> o.status().equals("COMPLETED"))
            .collect(Collectors.toList());
        System.out.println("Completed orders: " + completed.size()); // 3

        // --- EASY 2: min / max ---
        orders.stream()
              .max(Comparator.comparingDouble(Order::amount))
              .ifPresent(o -> System.out.println("Highest order: " + o.id() + " $" + o.amount()));

        System.out.println("---");

        // --- MEDIUM 1: reduce for total revenue ---
        double totalRevenue = orders.stream()
            .filter(o -> o.status().equals("COMPLETED"))
            .mapToDouble(Order::amount)
            .sum();
        System.out.printf("Total revenue: $%.2f%n", totalRevenue);

        // --- MEDIUM 2: joining ---
        String customerList = orders.stream()
            .map(Order::customer)
            .distinct()
            .sorted()
            .collect(Collectors.joining(", ", "[", "]"));
        System.out.println("Customers: " + customerList);

        System.out.println("---");

        // --- HARD 1: groupingBy + counting ---
        Map<String, Long> ordersByStatus = orders.stream()
            .collect(Collectors.groupingBy(Order::status, Collectors.counting()));
        ordersByStatus.forEach((status, cnt) ->
            System.out.println(status + ": " + cnt));

        System.out.println("---");

        // --- HARD 2: groupingBy + summingDouble + toMap ---
        Map<String, Double> revenueByCustomer = orders.stream()
            .filter(o -> o.status().equals("COMPLETED"))
            .collect(Collectors.groupingBy(
                Order::customer,
                Collectors.summingDouble(Order::amount)
            ));

        revenueByCustomer.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .forEach(e -> System.out.printf("%s: $%.2f%n", e.getKey(), e.getValue()));

        System.out.println("---");

        // allMatch / anyMatch / noneMatch
        boolean allProcessed = orders.stream().noneMatch(o -> o.status().equals("FAILED"));
        System.out.println("No failed orders: " + allProcessed);

        boolean hasLargeOrder = orders.stream().anyMatch(o -> o.amount() > 400);
        System.out.println("Has order > $400: " + hasLargeOrder);
    }
}
