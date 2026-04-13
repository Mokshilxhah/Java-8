# Java 8 Cheat Sheet

## Lambda Syntax
```java
() -> expr                          // no params
x -> x * 2                         // one param
(x, y) -> x + y                    // two params
(x, y) -> { return x + y; }        // block body
```

## Functional Interfaces
```java
Predicate<T>          T -> boolean          .test()
Function<T,R>         T -> R                .apply()
Consumer<T>           T -> void             .accept()
Supplier<T>           () -> T               .get()
BiFunction<T,U,R>     T,U -> R              .apply()
UnaryOperator<T>      T -> T                .apply()
BinaryOperator<T>     T,T -> T              .apply()
```

## Method References
```java
Integer::parseInt           // static
String::toUpperCase         // unbound instance
obj::method                 // bound instance
ArrayList::new              // constructor
```

## Stream Pipeline
```java
list.stream()
    .filter(x -> x > 0)
    .map(x -> x * 2)
    .sorted()
    .distinct()
    .limit(10)
    .collect(Collectors.toList())
```

## Key Intermediate Ops
```java
filter(Predicate)           // keep matching
map(Function)               // transform
flatMap(Function)           // flatten nested streams
sorted() / sorted(Comp)     // sort
distinct()                  // remove duplicates
limit(n) / skip(n)          // slice
peek(Consumer)              // debug
```

## Key Terminal Ops
```java
collect(Collector)          // gather results
forEach(Consumer)           // side effects
reduce(identity, BinOp)     // fold to single value
count()                     // number of elements
findFirst() / findAny()     // Optional<T>
anyMatch / allMatch / noneMatch  // boolean
min(Comp) / max(Comp)       // Optional<T>
toArray()                   // T[]
```

## Collectors
```java
toList() / toSet()
toUnmodifiableList()
toMap(keyFn, valFn)
toMap(keyFn, valFn, mergeFn)        // handle duplicates
groupingBy(classifier)
groupingBy(classifier, downstream)
partitioningBy(predicate)
joining(delim, prefix, suffix)
counting()
summingDouble(fn)
averagingDouble(fn)
summarizingDouble(fn)               // DoubleSummaryStatistics
maxBy(comp) / minBy(comp)
collectingAndThen(downstream, fn)
mapping(mapper, downstream)
```

## Optional
```java
Optional.of(val)            // non-null
Optional.ofNullable(val)    // nullable
Optional.empty()

.isPresent() / .isEmpty()
.get()                      // avoid — throws if empty
.orElse(default)
.orElseGet(Supplier)        // lazy
.orElseThrow(Supplier)
.ifPresent(Consumer)
.map(Function)
.flatMap(Function)          // when fn returns Optional
.filter(Predicate)
```

## Date & Time
```java
LocalDate.now() / LocalDate.of(y, m, d)
LocalTime.now() / LocalTime.of(h, m, s)
LocalDateTime.now()
ZonedDateTime.now(ZoneId.of("America/New_York"))
Instant.now()

date.plusDays(n) / minusMonths(n)
Period.between(d1, d2)
Duration.between(t1, t2)

DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
date.format(fmt)
LocalDate.parse("15/01/2024", fmt)
```

## CompletableFuture
```java
CompletableFuture.supplyAsync(() -> value, executor)
CompletableFuture.runAsync(() -> doWork(), executor)

.thenApply(fn)              // map result
.thenApplyAsync(fn, exec)   // map on new thread
.thenCompose(fn)            // flatMap (fn returns CF)
.thenAccept(consumer)       // consume result
.thenRun(runnable)          // run after, no input

.thenCombine(other, fn)     // combine two CFs
CompletableFuture.allOf(cf1, cf2, cf3)   // wait for all
CompletableFuture.anyOf(cf1, cf2, cf3)   // first wins

.exceptionally(ex -> fallback)
.handle((result, ex) -> ...)             // always called
.whenComplete((result, ex) -> ...)       // side effect

.get()                      // blocking, checked exception
.join()                     // blocking, unchecked exception
.orTimeout(n, TimeUnit)     // Java 9+
```

## Key Differences

| | `map` | `flatMap` |
|--|-------|-----------|
| Stream | T → R | T → Stream<R> (flattened) |
| Optional | T → R | T → Optional<R> (unwrapped) |
| CompletableFuture | T → R | T → CF<R> (unwrapped) |

| | `orElse` | `orElseGet` |
|--|----------|-------------|
| Evaluation | Always | Lazy (only if empty) |
| Use when | Cheap default | Expensive default |

| | `thenApply` | `thenCompose` |
|--|-------------|---------------|
| Function returns | plain value | CompletableFuture |
| Equivalent to | map | flatMap |

## Parallel Stream Rules
```
✅ Use when: large data + CPU-heavy + no shared state
❌ Avoid when: small data, I/O, ordered ops, mutable state
```
