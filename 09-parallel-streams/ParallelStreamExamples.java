import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class ParallelStreamExamples {

    static boolean isPrime(long n) {
        if (n < 2) return false;
        for (long i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) return false;
        }
        return true;
    }

    public static void main(String[] args) throws Exception {

        // --- Example 1: Sequential vs Parallel for CPU-heavy work ---
        List<Long> numbers = LongStream.rangeClosed(1, 1_000_000)
            .boxed()
            .collect(Collectors.toList());

        long t1 = System.currentTimeMillis();
        long seqCount = numbers.stream().filter(ParallelStreamExamples::isPrime).count();
        long seqTime = System.currentTimeMillis() - t1;

        long t2 = System.currentTimeMillis();
        long parCount = numbers.parallelStream().filter(ParallelStreamExamples::isPrime).count();
        long parTime = System.currentTimeMillis() - t2;

        System.out.println("Primes found: " + seqCount + " (seq) / " + parCount + " (par)");
        System.out.println("Sequential: " + seqTime + "ms");
        System.out.println("Parallel:   " + parTime + "ms");

        System.out.println("---");

        // --- Example 2: Safe parallel collect ---
        List<Integer> data = IntStream.rangeClosed(1, 100).boxed().collect(Collectors.toList());

        List<Integer> evens = data.parallelStream()
            .filter(n -> n % 2 == 0)
            .collect(Collectors.toList()); // thread-safe
        System.out.println("Even count: " + evens.size()); // 50

        System.out.println("---");

        // --- Example 3: Parallel reduce ---
        double sum = data.parallelStream()
            .mapToDouble(Integer::doubleValue)
            .sum();
        System.out.println("Sum: " + sum); // 2550.0

        System.out.println("---");

        // --- Example 4: Custom ForkJoinPool (avoid blocking common pool) ---
        ForkJoinPool customPool = new ForkJoinPool(4);
        long result = customPool.submit(() ->
            numbers.parallelStream()
                   .filter(ParallelStreamExamples::isPrime)
                   .count()
        ).get();
        customPool.shutdown();
        System.out.println("Primes (custom pool): " + result);

        System.out.println("---");

        // --- Example 5: When NOT to use parallel (small data) ---
        List<Integer> small = Arrays.asList(1, 2, 3, 4, 5);

        long t3 = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            small.stream().mapToInt(Integer::intValue).sum();
        }
        System.out.println("Sequential small: " + (System.currentTimeMillis() - t3) + "ms");

        long t4 = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            small.parallelStream().mapToInt(Integer::intValue).sum();
        }
        System.out.println("Parallel small:   " + (System.currentTimeMillis() - t4) + "ms");
        System.out.println("(Parallel is slower for small data due to thread overhead)");
    }
}
