# Interview Coding Problems

## Problem 1: Find duplicates in a list
```java
List<Integer> nums = Arrays.asList(1, 2, 3, 2, 4, 3, 5);

Set<Integer> seen = new HashSet<>();
List<Integer> duplicates = nums.stream()
    .filter(n -> !seen.add(n))
    .distinct()
    .collect(Collectors.toList());
// [2, 3]
```

## Problem 2: Group anagrams
```java
List<String> words = Arrays.asList("eat", "tea", "tan", "ate", "nat", "bat");

Map<String, List<String>> anagrams = words.stream()
    .collect(Collectors.groupingBy(w -> {
        char[] chars = w.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }));
// {aet=[eat, tea, ate], ant=[tan, nat], abt=[bat]}
```

## Problem 3: Flatten and count word frequency
```java
List<String> sentences = Arrays.asList("hello world", "hello java", "world of java");

Map<String, Long> freq = sentences.stream()
    .flatMap(s -> Arrays.stream(s.split(" ")))
    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
// {hello=2, world=2, java=2, of=1}
```

## Problem 4: Second highest salary
```java
List<Integer> salaries = Arrays.asList(50000, 90000, 75000, 90000, 60000);

Optional<Integer> second = salaries.stream()
    .distinct()
    .sorted(Comparator.reverseOrder())
    .skip(1)
    .findFirst();
// 75000
```

## Problem 5: Partition employees by salary threshold
```java
Map<Boolean, List<Employee>> partitioned = employees.stream()
    .collect(Collectors.partitioningBy(e -> e.salary() > 80000));

List<Employee> highEarners = partitioned.get(true);
List<Employee> others      = partitioned.get(false);
```

## Problem 6: Convert list to map (id → object)
```java
Map<String, Employee> empMap = employees.stream()
    .collect(Collectors.toMap(Employee::id, Function.identity()));
```

## Problem 7: Find employees with same name (duplicates by field)
```java
Map<String, Long> nameCounts = employees.stream()
    .collect(Collectors.groupingBy(Employee::name, Collectors.counting()));

List<String> duplicateNames = nameCounts.entrySet().stream()
    .filter(e -> e.getValue() > 1)
    .map(Map.Entry::getKey)
    .collect(Collectors.toList());
```

## Problem 8: Running total (prefix sum)
```java
List<Integer> values = Arrays.asList(1, 2, 3, 4, 5);

// Using iterate
List<Integer> running = IntStream.range(0, values.size())
    .mapToObj(i -> values.subList(0, i + 1).stream().mapToInt(Integer::intValue).sum())
    .collect(Collectors.toList());
// [1, 3, 6, 10, 15]
```

## Problem 9: Most frequent element
```java
List<String> items = Arrays.asList("a", "b", "a", "c", "b", "a");

String mostFrequent = items.stream()
    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
    .entrySet().stream()
    .max(Map.Entry.comparingByValue())
    .map(Map.Entry::getKey)
    .orElseThrow();
// "a"
```

## Problem 10: Merge two maps, summing values on conflict
```java
Map<String, Integer> map1 = Map.of("a", 1, "b", 2, "c", 3);
Map<String, Integer> map2 = Map.of("b", 10, "c", 20, "d", 4);

Map<String, Integer> merged = Stream.of(map1, map2)
    .flatMap(m -> m.entrySet().stream())
    .collect(Collectors.toMap(
        Map.Entry::getKey,
        Map.Entry::getValue,
        Integer::sum
    ));
// {a=1, b=12, c=23, d=4}
```

## Problem 11: Longest string in list
```java
List<String> words = Arrays.asList("apple", "banana", "kiwi", "strawberry");

String longest = words.stream()
    .max(Comparator.comparingInt(String::length))
    .orElseThrow();
// "strawberry"
```

## Problem 12: Check if all elements satisfy a condition
```java
List<Integer> ages = Arrays.asList(18, 22, 25, 30);
boolean allAdults = ages.stream().allMatch(age -> age >= 18); // true
```

## Problem 13: Zip two lists into a map
```java
List<String> keys   = Arrays.asList("a", "b", "c");
List<Integer> values = Arrays.asList(1, 2, 3);

Map<String, Integer> zipped = IntStream.range(0, keys.size())
    .boxed()
    .collect(Collectors.toMap(keys::get, values::get));
// {a=1, b=2, c=3}
```

## Problem 14: CompletableFuture — parallel API calls with timeout
```java
ExecutorService exec = Executors.newFixedThreadPool(4);
List<String> ids = Arrays.asList("U1", "U2", "U3");

List<String> results = ids.stream()
    .map(id -> CompletableFuture
        .supplyAsync(() -> callApi(id), exec)
        .orTimeout(500, TimeUnit.MILLISECONDS)
        .exceptionally(ex -> "fallback-" + id))
    .collect(Collectors.toList())
    .stream()
    .map(CompletableFuture::join)
    .collect(Collectors.toList());
```

## Problem 15: Custom collector — concatenate with separator
```java
// Using built-in
String result = Stream.of("a", "b", "c")
    .collect(Collectors.joining(" | ", "START[", "]END"));
// START[a | b | c]END
```
