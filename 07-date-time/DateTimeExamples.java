import java.time.*;
import java.time.format.*;
import java.time.temporal.*;
import java.util.*;
import java.util.stream.*;

public class DateTimeExamples {

    public static void main(String[] args) {

        // --- EASY 1: Basic LocalDate operations ---
        LocalDate today = LocalDate.now();
        LocalDate birthday = LocalDate.of(1995, Month.MARCH, 15);
        Period age = Period.between(birthday, today);
        System.out.printf("Age: %d years, %d months, %d days%n",
            age.getYears(), age.getMonths(), age.getDays());

        LocalDate nextFriday = today.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
        System.out.println("Next Friday: " + nextFriday);

        // --- EASY 2: Formatting and parsing ---
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy");
        System.out.println("Formatted: " + today.format(fmt));

        LocalDate parsed = LocalDate.parse("25 Dec 2024", fmt);
        System.out.println("Parsed: " + parsed);

        System.out.println("---");

        // --- MEDIUM 1: Duration between two times ---
        LocalDateTime start = LocalDateTime.of(2024, 1, 15, 9, 0);
        LocalDateTime end   = LocalDateTime.of(2024, 1, 15, 17, 30);
        Duration workDay = Duration.between(start, end);
        System.out.printf("Work duration: %d hours %d minutes%n",
            workDay.toHours(), workDay.toMinutesPart());

        // --- MEDIUM 2: ZonedDateTime for global scheduling ---
        ZonedDateTime nycTime = ZonedDateTime.now(ZoneId.of("America/New_York"));
        ZonedDateTime londonTime = nycTime.withZoneSameInstant(ZoneId.of("Europe/London"));
        ZonedDateTime tokyoTime  = nycTime.withZoneSameInstant(ZoneId.of("Asia/Tokyo"));

        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm z");
        System.out.println("NYC:    " + nycTime.format(timeFmt));
        System.out.println("London: " + londonTime.format(timeFmt));
        System.out.println("Tokyo:  " + tokyoTime.format(timeFmt));

        System.out.println("---");

        // --- HARD 1: Business days calculation ---
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to   = LocalDate.of(2024, 1, 31);

        long businessDays = from.datesUntil(to)
            .filter(d -> d.getDayOfWeek() != DayOfWeek.SATURDAY
                      && d.getDayOfWeek() != DayOfWeek.SUNDAY)
            .count();
        System.out.println("Business days in Jan 2024: " + businessDays);

        // --- HARD 2: Subscription expiry system ---
        record Subscription(String user, LocalDate startDate, int durationMonths) {
            LocalDate expiryDate() { return startDate.plusMonths(durationMonths); }
            boolean isExpired()    { return expiryDate().isBefore(LocalDate.now()); }
            long daysUntilExpiry() { return ChronoUnit.DAYS.between(LocalDate.now(), expiryDate()); }
        }

        List<Subscription> subs = Arrays.asList(
            new Subscription("Alice", LocalDate.of(2024, 1, 1),  12),
            new Subscription("Bob",   LocalDate.of(2023, 6, 1),   6),
            new Subscription("Carol", LocalDate.of(2025, 3, 1),  24)
        );

        System.out.println("\nSubscription Status:");
        subs.forEach(s -> System.out.printf("%-6s expires: %s | expired: %-5s | days left: %d%n",
            s.user(), s.expiryDate(), s.isExpired(), s.daysUntilExpiry()));
    }
}
