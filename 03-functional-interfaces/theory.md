# Functional Interfaces

## One-Line Definition
An interface with exactly one abstract method — the target type for any lambda.

## Why It Exists
Lambdas need a type. Functional interfaces provide that type, enabling lambdas to be assigned, passed, and returned.

## The `@FunctionalInterface` Annotation
Optional but recommended — causes a compile error if you accidentally add a second abstract method.

## Core Built-in Functional Interfaces

| Interface | Signature | Use Case |
|-----------|-----------|----------|
| `Predicate<T>` | `T → boolean` | Filtering, testing conditions |
| `Function<T,R>` | `T → R` | Transforming values |
| `Consumer<T>` | `T → void` | Side effects (print, save) |
| `Supplier<T>` | `() → T` | Lazy value creation |
| `BiFunction<T,U,R>` | `T,U → R` | Two-input transformation |
| `BiPredicate<T,U>` | `T,U → boolean` | Two-input test |
| `UnaryOperator<T>` | `T → T` | Transform same type |
| `BinaryOperator<T>` | `T,T → T` | Combine two same-type values |

## Composition Methods

```java
// Predicate
p1.and(p2)    // both must be true
p1.or(p2)     // either must be true
p1.negate()   // flip result

// Function
f1.andThen(f2)  // f1 first, then f2
f1.compose(f2)  // f2 first, then f1
```

## Key Points
- Default methods (like `andThen`, `and`) don't count as abstract methods
- `java.util.function` package has 43 built-in functional interfaces
- Primitive specializations exist: `IntPredicate`, `LongFunction`, `ToIntFunction`, etc. — use them to avoid boxing overhead

## When to Use Custom Functional Interfaces
- When built-in ones don't fit your signature
- When you want a meaningful name for domain clarity
- When you need checked exceptions (built-ins don't declare them)
