# Lambda Expressions

## One-Line Definition
A lambda is an anonymous function — behavior you can pass around like a variable.

## Why It Exists
Before lambdas, passing behavior required anonymous classes — 5 lines for something that should be 1.

## Syntax
```
(parameters) -> expression
(parameters) -> { statements; }
```

```java
// No params
() -> System.out.println("Hello")

// One param (parens optional)
x -> x * x

// Multiple params
(a, b) -> a + b

// Block body
(a, b) -> {
    int sum = a + b;
    return sum;
}
```

## Key Points
- A lambda implements a **functional interface** (interface with exactly one abstract method)
- Type inference — compiler figures out parameter types from context
- Lambdas can capture effectively final local variables
- `this` inside a lambda refers to the enclosing class (unlike anonymous classes)

## Variable Capture Rules
```java
int x = 10; // effectively final
Runnable r = () -> System.out.println(x); // OK

int y = 10;
y = 20; // now NOT effectively final
Runnable r2 = () -> System.out.println(y); // COMPILE ERROR
```

## When to Use
- Passing behavior to methods (callbacks, event handlers)
- Stream operations
- Replacing single-method anonymous classes

## When to Avoid
- Complex logic (extract to a named method instead)
- When readability suffers
