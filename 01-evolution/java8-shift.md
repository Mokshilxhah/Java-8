# Java 8 — The Shift

## One-Line Definition
Java 8 brought functional programming into Java, making code shorter, cleaner, and more expressive.

## Why It Happened
Before Java 8, writing even simple operations required verbose boilerplate:

```java
// Pre Java 8 — sort a list of names
Collections.sort(names, new Comparator<String>() {
    public int compare(String a, String b) {
        return a.compareTo(b);
    }
});

// Java 8
names.sort(Comparator.naturalOrder());
```

The industry had moved toward functional languages (Scala, Kotlin, Python). Java had to evolve.

## What Changed

| Feature | Problem It Solved |
|---------|------------------|
| Lambda Expressions | Eliminated anonymous class boilerplate |
| Stream API | Replaced verbose for-loops for data processing |
| Optional | Reduced NullPointerExceptions |
| Functional Interfaces | Enabled passing behavior as data |
| Method References | Made lambdas even cleaner |
| New Date/Time API | Replaced broken `Date` and `Calendar` |
| Default Methods | Added methods to interfaces without breaking existing code |
| CompletableFuture | Made async programming composable |

## The Mental Shift

**Before:** Tell Java *how* to do something (imperative)
```java
List<String> result = new ArrayList<>();
for (String s : list) {
    if (s.startsWith("A")) {
        result.add(s.toUpperCase());
    }
}
```

**After:** Tell Java *what* you want (declarative)
```java
List<String> result = list.stream()
    .filter(s -> s.startsWith("A"))
    .map(String::toUpperCase)
    .collect(Collectors.toList());
```

## Key Insight
Java 8 didn't replace OOP — it added functional tools *on top* of it. The best Java 8 code combines both.
