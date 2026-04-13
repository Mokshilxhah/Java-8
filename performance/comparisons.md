# Performance: Streams vs Loops vs Parallel

## Streams vs For-Loops

### The Reality
Streams have a small overhead (lambda invocation, pipeline setup). For tiny datasets, a plain loop is faster. For real-world data sizes, the difference is negligible.

| Scenario | Winner | Why |
|----------|--------|-----|
| < 1000 elements, simple op | Loop | No pipeline overhead |
| 10k+ elements, complex pipeline | Stream | Readability + comparable speed |
| CPU-heavy, 100k+ elements | Parallel Stream | Multi-core utilization |
| I/O-bound operations | Neither (use async) | Threads block regardless |

### Benchmark Example (approximate)
```
Operation: filter + map + collect on 1M integers

For-loop:         ~15ms
Sequential stream: ~18ms   (+20% overhead, negligible)
Parallel stream:   ~6ms    (3x faster on 4-core machine)
```

## When Parallel Streams Win
```java
// CPU-heavy + large data = parallel wins
long count = LongStream.rangeClosed(1, 10_000_000)
    .parallel()
    .filter(n -> isPrime(n))
    .count();
```

## When Parallel Streams Lose

### 1. Small data
```java
// Parallel is SLOWER here — thread overhead > work
List<Integer> small = Arrays.asList(1, 2, 3, 4, 5);
small.parallelStream().mapToInt(i -> i).sum(); // slower than sequential
```

### 2. I/O-bound work
```java
// All threads block on I/O — no CPU gain
list.parallelStream().map(id -> httpClient.get(id)).collect(toList());
// Use CompletableFuture with a dedicated thread pool instead
```

### 3. Ordered operations
```java
// forEachOrdered kills parallelism benefit
list.parallelStream().forEachOrdered(System.out::println); // sequential speed
```

### 4. Shared mutable state
```java
// Race condition — NEVER do this
List<Integer> result = new ArrayList<>();
list.parallelStream().forEach(result::add); // BROKEN
```

## Decision Guide

```
Data size < 10k?
  → Use sequential stream or loop

CPU-heavy computation?
  → Try parallel stream, benchmark first

I/O operations?
  → Use CompletableFuture with custom executor

Need order guaranteed?
  → Sequential stream

Shared mutable state?
  → Sequential stream or redesign
```

## Stream vs Loop: Readability Trade-off

```java
// Loop — explicit, easy to debug
int total = 0;
for (Order o : orders) {
    if (o.status() == COMPLETED) {
        total += o.amount();
    }
}

// Stream — declarative, composable, harder to step through in debugger
int total = orders.stream()
    .filter(o -> o.status() == COMPLETED)
    .mapToInt(Order::amount)
    .sum();
```

**Rule:** Use streams for pipelines with 2+ operations. Use loops when you need index access, early mutation, or complex branching.

## Primitive Streams — Always Use for Numbers
```java
// BAD — boxing overhead
Stream<Integer> s = list.stream().map(n -> n * 2);

// GOOD — no boxing
IntStream s = list.stream().mapToInt(n -> n * 2);
```

`IntStream`, `LongStream`, `DoubleStream` avoid autoboxing and have extra methods: `sum()`, `average()`, `summaryStatistics()`.
