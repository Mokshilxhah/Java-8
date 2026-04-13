import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class LambdaUseCases {

    record Employee(String name, String dept, double salary) {}

    public static void main(String[] args) {

        List<Employee> employees = Arrays.asList(
            new Employee("Alice", "Engineering", 95000),
            new Employee("Bob",   "Marketing",   60000),
            new Employee("Carol", "Engineering", 110000),
            new Employee("Dave",  "HR",          55000),
            new Employee("Eve",   "Engineering", 88000)
        );

        // Use case 1: Filter by department
        Predicate<Employee> isEngineer = e -> e.dept().equals("Engineering");
        employees.stream()
                 .filter(isEngineer)
                 .map(Employee::name)
                 .forEach(System.out::println);

        System.out.println("---");

        // Use case 2: Compose predicates — Engineering AND salary > 90k
        Predicate<Employee> highEarner = e -> e.salary() > 90000;
        employees.stream()
                 .filter(isEngineer.and(highEarner))
                 .map(e -> e.name() + " - $" + e.salary())
                 .forEach(System.out::println);

        System.out.println("---");

        // Use case 3: Sort by salary descending, then name ascending
        Comparator<Employee> bySalaryDesc = Comparator.comparingDouble(Employee::salary).reversed();
        Comparator<Employee> byName       = Comparator.comparing(Employee::name);
        employees.stream()
                 .sorted(bySalaryDesc.thenComparing(byName))
                 .map(e -> String.format("%-10s $%.0f", e.name(), e.salary()))
                 .forEach(System.out::println);

        System.out.println("---");

        // Use case 4: Transform to map of name -> salary
        Map<String, Double> salaryMap = employees.stream()
            .collect(Collectors.toMap(Employee::name, Employee::salary));
        salaryMap.forEach((name, sal) -> System.out.printf("%s: $%.0f%n", name, sal));

        System.out.println("---");

        // Use case 5: Reusable lambda as a strategy
        Function<Double, Double> applyBonus = salary -> salary * 1.10;
        employees.stream()
                 .filter(isEngineer)
                 .map(e -> new Employee(e.name(), e.dept(), applyBonus.apply(e.salary())))
                 .forEach(e -> System.out.printf("%s new salary: $%.0f%n", e.name(), e.salary()));
    }
}
