# Parallel Streams

## One-Line Definition
A stream that splits work across multiple CPU cores using the ForkJoinPool — automatic parallelism.

## How It Works
```
Source → split into chunks → process in parallel → merge results
```
Uses `ForkJoinPool.commonPool()` by default (threads = CPU cores - 1).

## Creating Parallel Streams
```java
list.parallelStream()
list.stream().parallel()
```

## When Parallel Streams Help
- Large datasets (100k+ elements)
- CPU-intensive operations (complex calculations)
- Independent operations (no shared state)
- Operations where order doesn't matter

## When Parallel Streams Hurt
- Small datasets — thread overhead > benefit
- I/O-bound operations — threads block, no CPU gain
- Operations with shared mutable state — race conditions
- Order-sensitive operations — extra overhead to maintain order
- Short-circuit operations (`findFirst`) — may be slower than sequential

## Thread Safety Rules
```java
// WRONG — race condition
List<Integer> result = new ArrayList<>();
list.parallelStream().forEach(result::add); // concurrent modification!

// CORRECT — use thread-safe collector
List<Integer> result = list.parallelStream().collect(Collectors.toList());
```

## Performance Reality
```
Sequential: simple, predictable, low overhead
Parallel:   faster for CPU-heavy + large data, but adds:
            - Thread creation/management overhead
            - Data splitting overhead
            - Result merging overhead
```

## Key Points
- `reduce` and `collect` are safe with parallel streams (designed for it)
- Avoid stateful lambdas in parallel streams
- `forEachOrdered` maintains order but kills parallelism benefit
- Custom thread pool: wrap in `ForkJoinPool` to avoid blocking common pool

## Rule of Thumb
> Benchmark before using parallel streams. They're not always faster.
