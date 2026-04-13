# Date & Time API (java.time)

## One-Line Definition
An immutable, thread-safe date/time API that finally replaces the broken `Date` and `Calendar` classes.

## Why It Exists
`java.util.Date` was mutable, not thread-safe, had confusing month indexing (0-based), and mixed date + time concerns. `Calendar` was even worse.

## Core Classes

| Class | Represents |
|-------|-----------|
| `LocalDate` | Date only (2024-01-15) |
| `LocalTime` | Time only (14:30:00) |
| `LocalDateTime` | Date + time, no timezone |
| `ZonedDateTime` | Date + time + timezone |
| `Instant` | Machine timestamp (epoch millis) |
| `Duration` | Time-based amount (hours, minutes, seconds) |
| `Period` | Date-based amount (years, months, days) |
| `DateTimeFormatter` | Formatting and parsing |

## Key Points
- All classes are **immutable** — operations return new instances
- Month is **1-based** (January = 1, not 0)
- `LocalDate`/`LocalTime`/`LocalDateTime` have no timezone — use for business logic
- `ZonedDateTime` / `Instant` for cross-timezone or storage
- `Instant` is the best type for storing timestamps in databases

## Common Operations
```java
LocalDate today = LocalDate.now();
LocalDate tomorrow = today.plusDays(1);
LocalDate nextMonth = today.plusMonths(1);

boolean isBefore = today.isBefore(tomorrow); // true

Period age = Period.between(birthDate, today);
Duration elapsed = Duration.between(start, end);

// Formatting
DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
String formatted = today.format(fmt);
LocalDate parsed = LocalDate.parse("15/01/2024", fmt);
```

## When to Use
- `LocalDate` — birthdays, deadlines, business dates
- `LocalDateTime` — event scheduling without timezone
- `ZonedDateTime` — calendar apps, global scheduling
- `Instant` — audit logs, database timestamps
- `Duration` — measuring elapsed time
- `Period` — age calculation, subscription periods
