import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class CompletableFutureExamples {

    static ExecutorService executor = Executors.newFixedThreadPool(4);

    static String fetchUser(int id) {
        sleep(100);
        return "User-" + id;
    }

    static String fetchOrders(String user) {
        sleep(150);
        return user + "-Orders[O1,O2,O3]";
    }

    static double fetchPrice(String productId) {
        sleep(80);
        return Math.random() * 100 + 10;
    }

    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    public static void main(String[] args) throws Exception {

        // --- EASY 1: Basic async task ---
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> fetchUser(1), executor);
        System.out.println("Doing other work while fetching...");
        System.out.println("Result: " + future.get());

        System.out.println("---");

        // --- EASY 2: thenApply (map) ---
        CompletableFuture<String> pipeline = CompletableFuture
            .supplyAsync(() -> fetchUser(2), executor)
            .thenApply(String::toUpperCase)
            .thenApply(s -> "Processed: " + s);

        System.out.println(pipeline.get());

        System.out.println("---");

        // --- MEDIUM 1: thenCompose (flatMap) — chained dependent calls ---
        long start = System.currentTimeMillis();
        CompletableFuture<String> userOrders = CompletableFuture
            .supplyAsync(() -> fetchUser(3), executor)
            .thenCompose(user -> CompletableFuture.supplyAsync(() -> fetchOrders(user), executor));

        System.out.println(userOrders.get());
        System.out.println("Time: " + (System.currentTimeMillis() - start) + "ms");

        System.out.println("---");

        // --- MEDIUM 2: thenCombine — two independent tasks merged ---
        CompletableFuture<String> userFuture  = CompletableFuture.supplyAsync(() -> fetchUser(4), executor);
        CompletableFuture<Double> priceFuture = CompletableFuture.supplyAsync(() -> fetchPrice("P1"), executor);

        String combined = userFuture.thenCombine(priceFuture,
            (user, price) -> user + " paid $" + String.format("%.2f", price)
        ).get();
        System.out.println(combined);

        System.out.println("---");

        // --- HARD 1: allOf — fan-out parallel calls ---
        List<Integer> userIds = Arrays.asList(1, 2, 3, 4, 5);
        long t1 = System.currentTimeMillis();

        List<CompletableFuture<String>> futures = userIds.stream()
            .map(id -> CompletableFuture.supplyAsync(() -> fetchUser(id), executor))
            .collect(Collectors.toList());

        CompletableFuture<Void> allDone = CompletableFuture.allOf(
            futures.toArray(new CompletableFuture[0])
        );

        List<String> users = allDone.thenApply(v ->
            futures.stream().map(CompletableFuture::join).collect(Collectors.toList())
        ).get();

        System.out.println("All users: " + users);
        System.out.println("Parallel time: " + (System.currentTimeMillis() - t1) + "ms (vs ~500ms sequential)");

        System.out.println("---");

        // --- HARD 2: Exception handling ---
        CompletableFuture<String> risky = CompletableFuture
            .supplyAsync(() -> {
                if (Math.random() > 0.5) throw new RuntimeException("Service unavailable");
                return "Success";
            }, executor)
            .exceptionally(ex -> "Fallback: " + ex.getMessage())
            .thenApply(result -> "Final: " + result);

        System.out.println(risky.get());

        // handle() — access both result and exception
        CompletableFuture<String> handled = CompletableFuture
            .supplyAsync(() -> { throw new RuntimeException("DB error"); }, executor)
            .handle((result, ex) -> {
                if (ex != null) return "Recovered from: " + ex.getMessage();
                return result.toString();
            });

        System.out.println(handled.get());

        System.out.println("---");

        // --- HARD 3: Timeout (Java 9+) ---
        CompletableFuture<String> withTimeout = CompletableFuture
            .supplyAsync(() -> { sleep(500); return "slow result"; }, executor)
            .orTimeout(200, TimeUnit.MILLISECONDS)
            .exceptionally(ex -> "Timed out");

        System.out.println(withTimeout.get());

        executor.shutdown();
    }
}
