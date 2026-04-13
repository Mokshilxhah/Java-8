import java.util.*;
import java.util.stream.*;
import java.time.*;

public class OrderProcessing {

    enum Status { PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED }

    record Product(String id, String name, String category, double price) {}
    record OrderItem(Product product, int quantity) {
        double subtotal() { return product.price() * quantity; }
    }
    record Order(String id, String customerId, Status status, LocalDate date, List<OrderItem> items) {
        double total() { return items.stream().mapToDouble(OrderItem::subtotal).sum(); }
    }

    static List<Order> sampleOrders() {
        Product laptop  = new Product("P1", "Laptop",  "Electronics", 999.99);
        Product phone   = new Product("P2", "Phone",   "Electronics", 699.99);
        Product desk    = new Product("P3", "Desk",    "Furniture",   299.99);
        Product chair   = new Product("P4", "Chair",   "Furniture",   199.99);
        Product monitor = new Product("P5", "Monitor", "Electronics", 399.99);

        return Arrays.asList(
            new Order("O1", "C1", Status.DELIVERED, LocalDate.of(2024, 1, 5),
                Arrays.asList(new OrderItem(laptop, 1), new OrderItem(monitor, 2))),
            new Order("O2", "C2", Status.SHIPPED,   LocalDate.of(2024, 1, 10),
                Arrays.asList(new OrderItem(phone, 2))),
            new Order("O3", "C1", Status.CONFIRMED, LocalDate.of(2024, 1, 15),
                Arrays.asList(new OrderItem(desk, 1), new OrderItem(chair, 2))),
            new Order("O4", "C3", Status.PENDING,   LocalDate.of(2024, 1, 18),
                Arrays.asList(new OrderItem(laptop, 2))),
            new Order("O5", "C2", Status.CANCELLED, LocalDate.of(2024, 1, 20),
                Arrays.asList(new OrderItem(monitor, 1))),
            new Order("O6", "C3", Status.DELIVERED, LocalDate.of(2024, 1, 22),
                Arrays.asList(new OrderItem(phone, 1), new OrderItem(chair, 1)))
        );
    }

    public static void main(String[] args) {

        List<Order> orders = sampleOrders();

        // 1. Revenue from delivered orders
        double revenue = orders.stream()
            .filter(o -> o.status() == Status.DELIVERED)
            .mapToDouble(Order::total)
            .sum();
        System.out.printf("Total Revenue: $%.2f%n", revenue);

        // 2. Orders per customer
        System.out.println("\nOrders per customer:");
        orders.stream()
              .collect(Collectors.groupingBy(Order::customerId, Collectors.counting()))
              .forEach((c, cnt) -> System.out.println("  " + c + ": " + cnt));

        // 3. Top spending customers
        System.out.println("\nTop customers by spend:");
        orders.stream()
              .filter(o -> o.status() != Status.CANCELLED)
              .collect(Collectors.groupingBy(Order::customerId, Collectors.summingDouble(Order::total)))
              .entrySet().stream()
              .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
              .forEach(e -> System.out.printf("  %s: $%.2f%n", e.getKey(), e.getValue()));

        // 4. Most ordered products
        System.out.println("\nMost ordered products:");
        orders.stream()
              .filter(o -> o.status() != Status.CANCELLED)
              .flatMap(o -> o.items().stream())
              .collect(Collectors.groupingBy(
                  item -> item.product().name(),
                  Collectors.summingInt(OrderItem::quantity)
              ))
              .entrySet().stream()
              .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
              .forEach(e -> System.out.println("  " + e.getKey() + ": " + e.getValue() + " units"));

        // 5. Revenue by category
        System.out.println("\nRevenue by category:");
        orders.stream()
              .filter(o -> o.status() == Status.DELIVERED)
              .flatMap(o -> o.items().stream())
              .collect(Collectors.groupingBy(
                  item -> item.product().category(),
                  Collectors.summingDouble(OrderItem::subtotal)
              ))
              .forEach((cat, rev) -> System.out.printf("  %s: $%.2f%n", cat, rev));

        // 6. Orders in date range
        LocalDate from = LocalDate.of(2024, 1, 10);
        LocalDate to   = LocalDate.of(2024, 1, 20);
        System.out.println("\nOrders between " + from + " and " + to + ":");
        orders.stream()
              .filter(o -> !o.date().isBefore(from) && !o.date().isAfter(to))
              .map(o -> String.format("  %s | %s | $%.2f", o.id(), o.status(), o.total()))
              .forEach(System.out::println);
    }
}
