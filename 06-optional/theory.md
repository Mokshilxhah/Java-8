# Optional

## One-Line Definition
A container that may or may not hold a value — a type-safe alternative to returning null.

## Why It Exists
`NullPointerException` is the most common Java runtime error. Optional forces callers to handle the "no value" case explicitly.

## Creating Optional

```java
Optional.of(value)        // value must be non-null
Optional.ofNullable(val)  // val can be null
Optional.empty()          // explicitly empty
```

## Key Methods

| Method | Description |
|--------|-------------|
| `isPresent()` | true if value exists |
| `isEmpty()` | true if no value (Java 11+) |
| `get()` | returns value, throws if empty — avoid |
| `orElse(default)` | value or default |
| `orElseGet(Supplier)` | value or lazily computed default |
| `orElseThrow(Supplier)` | value or throw custom exception |
| `ifPresent(Consumer)` | run action if value exists |
| `map(Function)` | transform value if present |
| `flatMap(Function)` | transform when function returns Optional |
| `filter(Predicate)` | keep value only if condition met |

## Key Points
- Never use `Optional.get()` without `isPresent()` — defeats the purpose
- `orElseGet` is lazy — use it when the default is expensive to compute
- Don't use Optional as a field type or method parameter — it's designed for return types
- `map` vs `flatMap`: use `flatMap` when your mapping function already returns an Optional

## Anti-patterns
```java
// BAD — same as null check
if (opt.isPresent()) { opt.get().doSomething(); }

// GOOD
opt.ifPresent(v -> v.doSomething());

// BAD — always evaluates the default
opt.orElse(expensiveOperation());

// GOOD — lazy
opt.orElseGet(() -> expensiveOperation());
```

## When to Use / Avoid
- Use: return type when a method might not find a result
- Avoid: fields, parameters, collections (use empty collection instead)
