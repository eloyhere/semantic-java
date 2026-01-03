# semantic-java

Welcome to the `semantic-java` library! 🚀 This library provides a rich set of tools for processing collections in Java, offering both functional and performance-oriented features. Whether you're dealing with data analysis, stream processing, or just need some handy collection utilities, `semantic-java` has got you covered.

## Overview

The `semantic-java` library is designed to simplify complex collection operations by providing high-level abstractions and utilities. It includes classes for statistical analysis, ordered and unordered collections, and more. The library leverages Java's functional programming capabilities to offer concise and expressive APIs.

## Key Features

- **Statistical Analysis**: Perform various statistical computations like mean, median, mode, variance, etc.
- **Ordered and Unordered Collections**: Handle collections with specific ordering or without any order constraints.
- **Stream Processing**: Process collections using fluent APIs similar to Java Streams but with additional functionalities.
- **Custom Generators**: Define custom generators to produce sequences of elements.

## API Documentation

Below is a detailed overview of the key APIs provided by `semantic-java`, along with usage examples.

### 1. Collectable<E>

The `Collectable` class is the core of the library, providing methods to collect elements based on various criteria.

#### Methods:

- `anyMatch(Predicate<E> predicate)`: Returns `true` if any element matches the given predicate.
- `allMatch(Predicate<E> predicate)`: Returns `true` if all elements match the given predicate.
- `noneMatch(Predicate<E> predicate)`: Returns `true` if no elements match the given predicate.
- `count()`: Returns the number of elements.
- `findFirst()`: Returns an `Optional` containing the first element.
- `findAny()`: Returns an `Optional` containing any element.
- `forEach(BiConsumer<E, Long> consumer)`: Performs an action on each element.
- `groupBy(Function<E, K> keyExtractor)`: Groups elements by a key.
- `join(String delimiter)`: Joins elements into a single string separated by the given delimiter.
- `print(OutputStream stream)`: Prints elements to the given output stream.
- `reduce(BinaryOperator<E> operator)`: Reduces the elements using the given operator.
- `toList()`: Converts the collection to a `List`.
- `toMap(Function<E, K> keyExtractor, Function<E, V> valueExtractor)`: Converts the collection to a `Map`.

#### Example Usage:

```java
Collectable<Integer> collectable = new Collectable<>(generator);
boolean hasEven = collectable.anyMatch(n -> n % 2 == 0);
System.out.println("Has even number: " + hasEven);
```

### 2. Collector<A, E, R>

The `Collector` class is used to accumulate elements into a mutable result container.

#### Methods:

- `collect(Generator<E> generator)`: Collects elements from the generator.
- `collect(Iterable<E> iterable)`: Collects elements from the iterable.
- `collect(E[] elements)`: Collects elements from the array.

#### Example Usage:

```java
Collector<Integer, Integer, Integer> collector = Collector.full(0, (acc, elem) -> acc + elem, Integer::sum);
int sum = collector.collect(generator);
System.out.println("Sum: " + sum);
```

### 3. Statistics<E, D extends Number>

The `Statistics` class provides methods to compute statistical measures.

#### Methods:

- `mean()`: Computes the mean of the elements.
- `median()`: Computes the median of the elements.
- `mode()`: Computes the mode of the elements.
- `variance()`: Computes the variance of the elements.
- `standardDeviation()`: Computes the standard deviation of the elements.

#### Example Usage:

```java
Statistics<Integer, Double> stats = new IntegerStatistics<>(generator);
double average = stats.mean();
System.out.println("Average: " + average);
```

### 4. OrderedCollectable<E>

The `OrderedCollectable` class extends `Collectable` and provides methods to handle ordered collections.

#### Methods:

- `sorted()`: Sorts the elements.
- `sorted(Comparator<E> comparator)`: Sorts the elements using the given comparator.

#### Example Usage:

```java
OrderedCollectable<Integer> ordered = new OrderedCollectable<>(generator);
List<Integer> sortedList = ordered.sorted().toList();
System.out.println("Sorted List: " + sortedList);
```

### 5. UnorderedCollectable<E>

The `UnorderedCollectable` class extends `Collectable` and provides methods to handle unordered collections.

#### Methods:

- `toUnordered()`: Converts the collection to an unordered representation.

#### Example Usage:

```java
UnorderedCollectable<Integer> unordered = new UnorderedCollectable<>(generator);
unordered.toUnordered();
```

### 6. Window<E>

The `Window` class provides methods to create sliding or tumbling windows over the collection.

#### Methods:

- `slide(long size, long step)`: Creates a sliding window of the given size and step.
- `tumble(long size)`: Creates a tumbling window of the given size.

#### Example Usage:

```java
Window<Integer> window = new Window<>(generator);
Semantic<Semantic<Integer>> slidingWindow = window.slide(3, 1);
```

## Comparison with Java Streams

| Feature                          | semantic-java                           | Java Streams                           |
|----------------------------------|-----------------------------------------|----------------------------------------|
| Statistical Analysis             | Yes                                     | Limited (requires external libraries)  |
| Ordered and Unordered Handling   | Yes                                     | Limited (Streams are inherently ordered)|
| Custom Generators                | Yes                                     | No                                     |
| Window Operations                | Yes (Sliding and Tumbling Windows)      | No                                     |
| Performance                      | Optimized for specific use cases        | General-purpose, may not be optimized for specific tasks |

## Special Notes

- **toUnordered**: This method does not consider the order of elements and is optimized for performance. Use this when the order of elements is not important.
- **toXXX (e.g., toList, toMap)**: These methods consider the order of elements and are useful when the sequence matters.
- **Index Operations**: If you perform redirection, reversing, or translation before sorting, index operations may become invalid as sorting overrides these operations.

## Conclusion

`semantic-java` is a powerful library for handling collections in Java, offering a blend of functional programming and performance optimizations. Whether you need to perform complex statistical analyses, handle large datasets efficiently, or simply require more control over your collections, `semantic-java` provides the tools you need.

We hope you find this library useful and look forward to your feedback! 🌟

---

*This README is a starting point and will be expanded with more detailed documentation and examples as the library evolves.*
