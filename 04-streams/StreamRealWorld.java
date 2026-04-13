import java.util.*;
import java.util.stream.*;
import java.util.function.*;

public class StreamRealWorld {

    record Employee(String name, String dept, String city, double salary, int yearsExp) {}
    record Department(String name, long headcount, double avgSalary, double totalPayroll) {}

    static List<Employee> employees() {
        return Arrays.asList(
            new Employee("Alice",   "Engineering", "NYC",  95000, 5),
            new Employee("Bob",     "Engineering", "SF",  110000, 8),
            new Employee("Carol",   "Marketing",   "NYC",  70000, 3),
            new Employee("Dave",    "HR",          "Chicago", 60000, 2),
            new Employee("Eve",     "Engineering", "SF",   88000, 4),
            new Employee("Frank",   "Marketing",   "NYC",  75000, 6),
            new Employee("Grace",   "HR",          "SF",   65000, 3),
            new Employee("Heidi",   "Engineering", "NYC", 105000, 7),
            new Employee("Ivan",    "Marketing",   "Chicago", 68000, 4),
            new Employee("Judy",    "Engineering", "Chicago", 92000, 5)
        );
    }

    public static void main(String[] args) {

        List<Employee> emps = employees();

        // --- Pipeline 1: Department summary report ---
        System.out.println("=== Department Report ===");
        Map<String, Department> deptReport = emps.stream()
            .collect(Collectors.groupingBy(
                Employee::dept,
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    list -> new Department(
                        list.get(0).dept(),
                        list.size(),
                        list.stream().mapToDouble(Employee::salary).average().orElse(0),
                        list.stream().mapToDouble(Employee::salary).sum()
                    )
                )
            ));

        deptReport.values().stream()
            .sorted(Comparator.comparingDouble(Department::totalPayroll).reversed())
            .forEach(d -> System.out.printf("%-15s headcount=%-3d avgSalary=$%-8.0f payroll=$%.0f%n",
                d.name(), d.headcount(), d.avgSalary(), d.totalPayroll()));

        System.out.println();

        // --- Pipeline 2: Top earner per department ---
        System.out.println("=== Top Earner Per Department ===");
        emps.stream()
            .collect(Collectors.groupingBy(
                Employee::dept,
                Collectors.maxBy(Comparator.comparingDouble(Employee::salary))
            ))
            .forEach((dept, emp) ->
                emp.ifPresent(e -> System.out.printf("%-15s → %s ($%.0f)%n", dept, e.name(), e.salary())));

        System.out.println();

        // --- Pipeline 3: Salary band distribution ---
        System.out.println("=== Salary Bands ===");
        Map<String, Long> bands = emps.stream()
            .collect(Collectors.groupingBy(
                e -> {
                    if (e.salary() < 70000)  return "< 70k";
                    if (e.salary() < 90000)  return "70k-90k";
                    if (e.salary() < 110000) return "90k-110k";
                    return "> 110k";
                },
                Collectors.counting()
            ));
        bands.forEach((band, cnt) -> System.out.println(band + ": " + cnt));

        System.out.println();

        // --- Pipeline 4: City-wise avg salary for Engineering ---
        System.out.println("=== Engineering Avg Salary by City ===");
        emps.stream()
            .filter(e -> e.dept().equals("Engineering"))
            .collect(Collectors.groupingBy(
                Employee::city,
                Collectors.averagingDouble(Employee::salary)
            ))
            .entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .forEach(e -> System.out.printf("%-10s $%.0f%n", e.getKey(), e.getValue()));

        System.out.println();

        // --- Pipeline 5: Employees eligible for promotion (5+ years, salary < 100k) ---
        System.out.println("=== Promotion Candidates ===");
        emps.stream()
            .filter(e -> e.yearsExp() >= 5 && e.salary() < 100000)
            .sorted(Comparator.comparingInt(Employee::yearsExp).reversed())
            .map(e -> String.format("%s (%s, %d yrs, $%.0f)", e.name(), e.dept(), e.yearsExp(), e.salary()))
            .forEach(System.out::println);
    }
}
