import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

/**
 * Simulates a real-world scenario: fetch user profile, their orders,
 * and product recommendations in parallel, then assemble a dashboard response.
 */
public class AsyncCalls {

    record UserProfile(String id, String name, String tier) {}
    record Order(String id, double amount) {}
    record Recommendation(String productId, String name, double score) {}
    record Dashboard(UserProfile profile, List<Order> orders, List<Recommendation> recommendations) {}

    static ExecutorService io = Executors.newFixedThreadPool(8);

    // Simulated remote calls
    static UserProfile fetchProfile(String userId) {
        sleep(120);
        return new UserProfile(userId, "Alice", "PREMIUM");
    }

    static List<Order> fetchOrders(String userId) {
        sleep(200);
        return Arrays.asList(
            new Order("O1", 250.0),
            new Order("O2", 180.0),
            new Order("O3", 420.0)
        );
    }

    static List<Recommendation> fetchRecommendations(String userId, String tier) {
        sleep(150);
        return Arrays.asList(
            new Recommendation("P1", "Laptop Pro",  0.95),
            new Recommendation("P2", "Wireless Mouse", 0.87),
            new Recommendation("P3", "USB Hub",     0.72)
        );
    }

    static double fetchExchangeRate(String currency) {
        sleep(80);
        return currency.equals("EUR") ? 0.92 : 1.0;
    }

    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    public static void main(String[] args) throws Exception {

        String userId = "U123";

        // --- Pattern 1: Sequential (slow) ---
        long t1 = System.currentTimeMillis();
        UserProfile profile1 = fetchProfile(userId);
        List<Order> orders1  = fetchOrders(userId);
        List<Recommendation> recs1 = fetchRecommendations(userId, profile1.tier());
        System.out.println("Sequential time: " + (System.currentTimeMillis() - t1) + "ms");

        System.out.println("---");

        // --- Pattern 2: Parallel independent calls ---
        long t2 = System.currentTimeMillis();

        CompletableFuture<UserProfile> profileFuture = CompletableFuture
            .supplyAsync(() -> fetchProfile(userId), io);

        CompletableFuture<List<Order>> ordersFuture = CompletableFuture
            .supplyAsync(() -> fetchOrders(userId), io);

        // Recommendations depend on profile tier — chain with thenCompose
        CompletableFuture<List<Recommendation>> recsFuture = profileFuture
            .thenComposeAsync(p ->
                CompletableFuture.supplyAsync(() -> fetchRecommendations(userId, p.tier()), io), io);

        // Assemble dashboard when all are ready
        Dashboard dashboard = profileFuture
            .thenCombine(ordersFuture, (profile, orders) ->
                new Object[]{ profile, orders })
            .thenCombine(recsFuture, (pair, recs) ->
                new Dashboard((UserProfile) pair[0], (List<Order>) pair[1], recs))
            .get();

        System.out.println("Parallel time: " + (System.currentTimeMillis() - t2) + "ms");
        System.out.println("User: " + dashboard.profile().name() + " (" + dashboard.profile().tier() + ")");
        System.out.println("Orders: " + dashboard.orders().size());
        System.out.println("Recommendations: " + dashboard.recommendations().size());

        System.out.println("---");

        // --- Pattern 3: Fan-out — fetch multiple users in parallel ---
        List<String> userIds = Arrays.asList("U1", "U2", "U3", "U4", "U5");
        long t3 = System.currentTimeMillis();

        List<UserProfile> profiles = userIds.stream()
            .map(id -> CompletableFuture.supplyAsync(() -> fetchProfile(id), io))
            .collect(Collectors.toList())
            .stream()
            .map(CompletableFuture::join)
            .collect(Collectors.toList());

        System.out.println("Fan-out time: " + (System.currentTimeMillis() - t3) + "ms (vs ~600ms sequential)");
        System.out.println("Profiles fetched: " + profiles.size());

        System.out.println("---");

        // --- Pattern 4: anyOf — first response wins (race) ---
        CompletableFuture<Object> fastest = CompletableFuture.anyOf(
            CompletableFuture.supplyAsync(() -> { sleep(300); return "Server A"; }, io),
            CompletableFuture.supplyAsync(() -> { sleep(100); return "Server B"; }, io),
            CompletableFuture.supplyAsync(() -> { sleep(200); return "Server C"; }, io)
        );
        System.out.println("Fastest server: " + fastest.get());

        System.out.println("---");

        // --- Pattern 5: Error handling with fallback ---
        CompletableFuture<List<Recommendation>> safeRecs = CompletableFuture
            .supplyAsync(() -> {
                if (Math.random() > 0.5) throw new RuntimeException("Recommendation service down");
                return fetchRecommendations(userId, "STANDARD");
            }, io)
            .exceptionally(ex -> {
                System.out.println("Fallback: " + ex.getMessage());
                return Collections.emptyList();
            });

        System.out.println("Recs (with fallback): " + safeRecs.get().size());

        io.shutdown();
    }
}
