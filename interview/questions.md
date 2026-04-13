# Java 8 Interview Questions

## Lambda & Functional Interfaces

**Q1. What is a lambda expression?**
An anonymous function that implements a functional interface. Syntax: `(params) -> body`.

**Q2. What is a functional interface?**
An interface with exactly one abstract method. Annotated with `@FunctionalInterface` (optional but recommended).

**Q3. Can a functional interface have default/static methods?**
Yes. Only abstract methods count. Default and static methods are allowed.

**Q4. What is the difference between `Predicate`, `Function`, `Consumer`, and `Supplier`?**
- `Predicate<T>`: T → boolean (testing)
- `Function<T,R>`: T → R (transforming)
- `Consumer<T>`: T → void (side effects)
- `Supplier<T>`: () → T (producing)

**Q5. What does "effectively final" mean?**
A variable that is never reassigned after initialization. Lambdas can capture effectively final local variables.

**Q6. What is the difference between `andThen` and `compose` in Function?**
- `f.andThen(g)`: applies f first, then g → `g(f(x))`
- `f.compose(g)`: applies g first, then f → `f(g(x))`

---

## Stream API

**Q7. What is a Stream? Is it a data structure?**
No. A stream is a pipeline for processing data. It doesn't store data — it processes it from a source.

**Q8. What is the difference between intermediate and terminal operations?**
- Intermediate: lazy, return Stream, don't execute until terminal is called
- Terminal: eager, trigger execution, return a result or void

**Q9. Can a stream be reused?**
No. Once a terminal operation is called, the stream is consumed. Create a new stream to process again.

**Q10. What is the difference between `map` and `flatMap`?**
- `map`: one-to-one transformation → `Stream<Stream<T>>` if mapping to collections
- `flatMap`: one-to-many, flattens nested streams → `Stream<T>`

**Q11. What is the difference between `findFirst` and `findAny`?**
- `findFirst`: always returns the first element (deterministic)
- `findAny`: may return any element (faster in parallel streams)

**Q12. What is `reduce`?**
A terminal operation that combines stream elements into a single value using a `BinaryOperator`.
```java
Optional<Integer> sum = list.stream().reduce((a, b) -> a + b);
int sum = list.stream().reduce(0, Integer::sum); // with identity
```

**Q13. What is the difference between `forEach` and `forEachOrdered`?**
- `forEach`: no order guarantee (especially in parallel)
- `forEachOrdered`: maintains encounter order (slower in parallel)

**Q14. How does `collect` work?**
It's a mutable reduction — accumulates elements into a container using a `Collector`.

**Q15. What does `groupingBy` return?**
`Map<K, List<T>>` by default. Can be customized with a downstream collector.

**Q16. What is `Collectors.toMap` and when does it throw?**
Converts stream to Map. Throws `IllegalStateException` on duplicate keys — provide a merge function to handle them.

**Q17. What is `partitioningBy`?**
A special `groupingBy` that splits into two groups: `Map<Boolean, List<T>>`.

**Q18. What is `collectingAndThen`?**
Applies a finisher function after the downstream collector completes. Useful for wrapping in unmodifiable collections.

---

## Optional

**Q19. What is Optional and why was it introduced?**
A container for a value that may be absent. Introduced to reduce NPEs and make "no result" explicit in APIs.

**Q20. What is the difference between `orElse` and `orElseGet`?**
- `orElse(value)`: always evaluates the default value
- `orElseGet(supplier)`: lazily evaluates — only called if Optional is empty

**Q21. When should you NOT use Optional?**
- As a field type
- As a method parameter
- In collections (use empty collection instead)

**Q22. What is the difference between `map` and `flatMap` in Optional?**
- `map`: wraps result in Optional → `Optional<Optional<T>>` if function returns Optional
- `flatMap`: use when function already returns Optional → avoids double wrapping

---

## Date & Time

**Q23. Why was the new Date/Time API introduced?**
`java.util.Date` was mutable, not thread-safe, had 0-based months, and mixed date/time concerns.

**Q24. What is the difference between `LocalDate`, `LocalDateTime`, and `ZonedDateTime`?**
- `LocalDate`: date only, no time, no timezone
- `LocalDateTime`: date + time, no timezone
- `ZonedDateTime`: date + time + timezone

**Q25. What is `Instant`?**
A point on the timeline in UTC (epoch-based). Best for storing timestamps in databases.

**Q26. What is the difference between `Period` and `Duration`?**
- `Period`: date-based (years, months, days)
- `Duration`: time-based (hours, minutes, seconds, nanos)

---

## Parallel Streams

**Q27. How does a parallel stream work?**
Uses `ForkJoinPool.commonPool()` to split the stream into chunks, process in parallel, then merge results.

**Q28. When should you avoid parallel streams?**
Small datasets, I/O-bound operations, shared mutable state, order-sensitive operations.

**Q29. Is `collect` thread-safe in parallel streams?**
Yes — `Collectors` are designed to work correctly with parallel streams.

**Q30. How do you use a custom thread pool with parallel streams?**
```java
ForkJoinPool pool = new ForkJoinPool(4);
pool.submit(() -> list.parallelStream().filter(...).collect(...)).get();
```

---

## CompletableFuture

**Q31. What is the difference between `thenApply` and `thenCompose`?**
- `thenApply`: like `map` — function returns a plain value
- `thenCompose`: like `flatMap` — function returns a `CompletableFuture`

**Q32. What does `CompletableFuture.allOf` return?**
`CompletableFuture<Void>`. You need to call `.join()` on each individual future to get results.

**Q33. What is the difference between `exceptionally` and `handle`?**
- `exceptionally`: only called on failure, provides fallback value
- `handle`: called always (success or failure), receives both result and exception

**Q34. What is the difference between `get()` and `join()`?**
- `get()`: throws checked `ExecutionException` and `InterruptedException`
- `join()`: throws unchecked `CompletionException` — cleaner in stream pipelines

**Q35. Why should you use a custom executor for I/O in CompletableFuture?**
Default uses `ForkJoinPool.commonPool()` which is shared and sized for CPU tasks. I/O blocks threads, starving other tasks.
