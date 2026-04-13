# CompletableFuture

## One-Line Definition
A composable async computation — run tasks in the background, chain them, combine them, handle errors.

## Why It Exists
`Future` (Java 5) was limited: you could submit a task and get a result, but couldn't chain, combine, or handle errors without blocking. `CompletableFuture` fixes all of that.

## Creating

```java
// Run async, no return value
CompletableFuture<Void> cf = CompletableFuture.runAsync(() -> doWork());

// Run async, returns value
CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> fetchData());

// With custom executor (don't block common pool with I/O)
CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> fetchData(), executor);
```

## Chaining (non-blocking)

| Method | Input | Output | Thread |
|--------|-------|--------|--------|
| `thenApply(fn)` | result | new result | same thread |
| `thenApplyAsync(fn)` | result | new result | new thread |
| `thenAccept(consumer)` | result | void | same thread |
| `thenRun(runnable)` | nothing | void | same thread |
| `thenCompose(fn)` | result | unwrapped CF | — |

## Combining

```java
// Both must complete
CompletableFuture.allOf(cf1, cf2, cf3)

// First to complete wins
CompletableFuture.anyOf(cf1, cf2, cf3)

// Combine two results
cf1.thenCombine(cf2, (r1, r2) -> r1 + r2)
```

## Error Handling

```java
cf.exceptionally(ex -> fallbackValue)
cf.handle((result, ex) -> ex != null ? fallback : result)
cf.whenComplete((result, ex) -> log(result, ex))
```

## Key Points
- `thenCompose` is `flatMap` for CompletableFuture — use when your function returns a CF
- `thenApply` is `map` — use when your function returns a plain value
- Always use a custom `ExecutorService` for I/O tasks — don't block `ForkJoinPool.commonPool()`
- `allOf` returns `CompletableFuture<Void>` — you need to call `.get()` on each CF to extract results
- `join()` is like `get()` but throws unchecked exceptions

## When to Use
- Parallel API calls
- Non-blocking I/O pipelines
- Fan-out/fan-in patterns
- Timeout handling on external calls
