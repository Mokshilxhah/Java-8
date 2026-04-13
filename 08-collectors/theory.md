# Collectors

## One-Line Definition
Pre-built reduction strategies for `Stream.collect()` — turn a stream into a collection, map, string, or summary.

## Why It Exists
`reduce()` is powerful but low-level. Collectors provide high-level, readable aggregation patterns.

## Most Important Collectors

```java
// Basic collection
Collectors.toList()
Collectors.toSet()
Collectors.toUnmodifiableList()

// Map
Collectors.toMap(keyFn, valueFn)
Collectors.toMap(keyFn, valueFn, mergeFunction)  // handle duplicate keys

// Grouping
Collectors.groupingBy(classifier)
Collectors.groupingBy(classifier, downstream)

// Partitioning (split into true/false)
Collectors.partitioningBy(predicate)
Collectors.partitioningBy(predicate, downstream)

// String joining
Collectors.joining()
Collectors.joining(delimiter)
Collectors.joining(delimiter, prefix, suffix)

// Numeric summaries
Collectors.counting()
Collectors.summingInt/Long/Double(fn)
Collectors.averagingInt/Long/Double(fn)
Collectors.summarizingInt/Long/Double(fn)

// Min/Max
Collectors.minBy(comparator)
Collectors.maxBy(comparator)

// Advanced
Collectors.collectingAndThen(downstream, finisher)
Collectors.mapping(mapper, downstream)
Collectors.teeing(downstream1, downstream2, merger)  // Java 12+
```

## Key Points
- `groupingBy` is the most powerful — combine with downstream collectors for complex aggregations
- `partitioningBy` is a special case of `groupingBy` with a boolean key
- `collectingAndThen` lets you post-process the result (e.g., wrap in unmodifiable list)
- `toMap` throws on duplicate keys by default — always provide a merge function for real data

## groupingBy Pattern
```java
// Count per group
groupingBy(Employee::dept, counting())

// List per group (default)
groupingBy(Employee::dept)

// Sum per group
groupingBy(Employee::dept, summingDouble(Employee::salary))

// Max per group
groupingBy(Employee::dept, maxBy(comparingDouble(Employee::salary)))

// Nested grouping
groupingBy(Employee::dept, groupingBy(Employee::city))
```
