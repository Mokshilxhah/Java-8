import java.util.*;
import java.util.stream.*;
import java.time.*;
import java.time.format.*;
import java.util.regex.*;

public class LogAnalysis {

    enum Level { DEBUG, INFO, WARN, ERROR }

    record LogEntry(LocalDateTime timestamp, Level level, String service, String message) {

        static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        static Optional<LogEntry> parse(String line) {
            // Format: 2024-01-15 10:23:45 ERROR UserService Failed to connect
            Pattern p = Pattern.compile(
                "(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}) (DEBUG|INFO|WARN|ERROR) (\\w+) (.+)"
            );
            Matcher m = p.matcher(line);
            if (!m.matches()) return Optional.empty();
            return Optional.of(new LogEntry(
                LocalDateTime.parse(m.group(1), FMT),
                Level.valueOf(m.group(2)),
                m.group(3),
                m.group(4)
            ));
        }
    }

    static List<String> rawLogs() {
        return Arrays.asList(
            "2024-01-15 10:00:01 INFO  AuthService User login successful",
            "2024-01-15 10:00:05 DEBUG OrderService Processing order O123",
            "2024-01-15 10:00:10 ERROR PaymentService Payment gateway timeout",
            "2024-01-15 10:00:15 WARN  InventoryService Low stock for product P45",
            "2024-01-15 10:00:20 ERROR AuthService Invalid token received",
            "2024-01-15 10:00:25 INFO  OrderService Order O123 confirmed",
            "2024-01-15 10:00:30 ERROR PaymentService Connection refused",
            "2024-01-15 10:00:35 INFO  AuthService User logout",
            "2024-01-15 10:00:40 WARN  OrderService Order O124 delayed",
            "2024-01-15 10:00:45 ERROR InventoryService Database connection failed",
            "2024-01-15 10:00:50 INFO  PaymentService Payment P456 processed",
            "2024-01-15 10:00:55 ERROR AuthService Brute force detected from 192.168.1.1",
            "2024-01-15 10:01:00 DEBUG InventoryService Cache refreshed",
            "2024-01-15 10:01:05 ERROR PaymentService Timeout after 30s"
        );
    }

    public static void main(String[] args) {

        List<LogEntry> logs = rawLogs().stream()
            .map(LogEntry::parse)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());

        // 1. Error count by service
        System.out.println("=== Errors by Service ===");
        logs.stream()
            .filter(e -> e.level() == Level.ERROR)
            .collect(Collectors.groupingBy(LogEntry::service, Collectors.counting()))
            .entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .forEach(e -> System.out.println("  " + e.getKey() + ": " + e.getValue()));

        // 2. Log level distribution
        System.out.println("\n=== Log Level Distribution ===");
        logs.stream()
            .collect(Collectors.groupingBy(LogEntry::level, Collectors.counting()))
            .forEach((level, cnt) -> System.out.println("  " + level + ": " + cnt));

        // 3. All ERROR messages
        System.out.println("\n=== ERROR Messages ===");
        logs.stream()
            .filter(e -> e.level() == Level.ERROR)
            .map(e -> String.format("  [%s] %s: %s",
                e.timestamp().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                e.service(), e.message()))
            .forEach(System.out::println);

        // 4. Services with multiple errors (potential incidents)
        System.out.println("\n=== Services with 2+ Errors (Incident Alert) ===");
        logs.stream()
            .filter(e -> e.level() == Level.ERROR)
            .collect(Collectors.groupingBy(LogEntry::service, Collectors.counting()))
            .entrySet().stream()
            .filter(e -> e.getValue() >= 2)
            .forEach(e -> System.out.println("  ⚠ " + e.getKey() + " (" + e.getValue() + " errors)"));

        // 5. Timeline — entries per minute
        System.out.println("\n=== Activity per Minute ===");
        logs.stream()
            .collect(Collectors.groupingBy(
                e -> e.timestamp().truncatedTo(java.time.temporal.ChronoUnit.MINUTES),
                Collectors.counting()
            ))
            .entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(e -> System.out.println("  " + e.getKey() + " → " + e.getValue() + " entries"));
    }
}
