# Stream API

## One-Line Definition
A pipeline for processing sequences of data — filter, transform, aggregate — without mutating the source.

## Why It Exists
For-loops are imperative and verbose. Streams let you express *what* you want, not *how* to iterate.

## Stream Pipeline
```
Source → Intermediate Operations → Terminal Operation
```
- Source: Collection, array, file, generator
- Intermediate: lazy, return a new Stream
- Terminal: eager, triggers execution, returns a result

## Intermediate Operations (lazy)

| Operation | Description |
|-----------|-------------|
| `filter(Predicate)` | Keep elements matching condition |
| `map(Function)` | Transform each element |
| `flatMap(Function)` | Flatten nested streams |
| `distinct()` | Remove duplicates |
| `sorted()` / `sorted(Comparator)` | Sort elements |
| `limit(n)` | Take first n elements |
| `skip(n)` | Skip first n elements |
| `peek(Consumer)` | Debug without consuming |

## Terminal Operations (eager)

| Operation | Description |
|-----------|-------------|
| `collect(Collector)` | Gather into collection/map/string |
| `forEach(Consumer)` | Side effect on each element |
| `reduce(identity, BinaryOperator)` | Fold to single value |
| `count()` | Number of elements |
| `findFirst()` / `findAny()` | Return Optional |
| `anyMatch` / `allMatch` / `noneMatch` | Boolean checks |
| `min` / `max` | Return Optional |
| `toArray()` | Convert to array |

## Key Points
- Streams are **not** data structures — they don't store data
- A stream can only be consumed **once**
- Intermediate ops are **lazy** — nothing runs until a terminal op is called
- Short-circuit ops (`findFirst`, `anyMatch`, `limit`) stop early
- `flatMap` is for when `map` would produce `Stream<Stream<T>>`

## flatMap vs map
```java
// map → Stream<List<String>>
list.stream().map(s -> Arrays.asList(s.split(",")))

// flatMap → Stream<String>
list.stream().flatMap(s -> Arrays.stream(s.split(",")))
```

## When to Use / Avoid
- Use: data transformation pipelines, filtering, aggregation
- Avoid: when you need index-based access, when mutating external state, for very simple single-iteration loops
