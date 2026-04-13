# Method References

## One-Line Definition
A shorthand for a lambda that does nothing but call an existing method.

## Syntax
```
ClassName::methodName
```

## Four Types

| Type | Syntax | Lambda Equivalent |
|------|--------|-------------------|
| Static method | `Integer::parseInt` | `s -> Integer.parseInt(s)` |
| Instance method (unbound) | `String::toUpperCase` | `s -> s.toUpperCase()` |
| Instance method (bound) | `myObj::method` | `() -> myObj.method()` |
| Constructor | `ArrayList::new` | `() -> new ArrayList<>()` |

## Examples

```java
// Static
Function<String, Integer> parse = Integer::parseInt;

// Unbound instance — first param becomes the receiver
Function<String, String> upper = String::toUpperCase;
BiFunction<String, String, Boolean> startsWith = String::startsWith;

// Bound instance
String prefix = "Hello";
Predicate<String> startsWithHello = prefix::startsWith; // bound to "Hello"

// Constructor
Supplier<List<String>> listMaker = ArrayList::new;
Function<Integer, int[]> arrayMaker = int[]::new;
```

## Key Points
- Method references are just syntactic sugar — they compile to the same bytecode as lambdas
- Use them when the lambda body is *only* a method call with no extra logic
- Unbound vs bound: unbound takes the instance as first argument; bound has the instance already fixed

## When to Use / Avoid
- Use: when the lambda is a direct method delegation — improves readability
- Avoid: when you need to transform arguments before passing, or when the method name doesn't make the intent clear
