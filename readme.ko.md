# semantic-java

`semantic-java` 라이브러리를 환영합니다! 🚀 이 라이브러리는 Java에서 컬렉션을 처리하기 위해 설계되었으며, 통계 분석, 스트림 처리 등의 시나리오에 적합한 다양한 기능을 제공합니다. 요소의 통계 분석이 필요하든, 순서가 있는 컬렉션 또는 순서가 없는 컬렉션을 처리해야 하든, 사용자 정의 생성기가 필요하든, `semantic-java`는 필요한 도구를 제공합니다.

## 개요

`semantic-java` 라이브러리는 고수준의 추상화와 실용적인 도구를 제공함으로써 복잡한 컬렉션 작업을 단순화합니다. 함수형 프로그래밍의 특성을 결합하여 간결하고 표현력 있는 API를 제공합니다.

## 주요 기능

- **통계 분석** : 평균, 중앙값, 최빈값, 분산 등 다양한 통계 계산을 지원합니다.
- **순서가 있는 컬렉션과 순서가 없는 컬렉션** : 특정 순서를 가진 컬렉션 또는 순서 제약이 없는 컬렉션을 처리합니다.
- **스트림 처리** : Java의 스트림과 유사한 유동적인 API를 제공하며, 추가 기능을 갖추고 있습니다.
- **사용자 정의 생성기** : 요소 시퀀스를 생성하기 위한 사용자 정의 생성기를 정의할 수 있습니다.

## API 문서

다음은 `semantic-java` 라이브러리의 주요 API에 대한 자세한 설명과 사용 예입니다.

### 1. Collectable<E>

`Collectable` 클래스는 라이브러리의 핵심으로, 다양한 조건에 따라 요소를 수집하는 메서드를 제공합니다.

#### 메서드:

- `anyMatch(Predicate<E> predicate)`: 임의의 요소가 주어진 조건에 일치하면 `true`를 반환합니다.
- `allMatch(Predicate<E> predicate)`: 모든 요소가 주어진 조건에 일치하면 `true`를 반환합니다.
- `noneMatch(Predicate<E> predicate)`: 어떤 요소도 주어진 조건에 일치하지 않으면 `true`를 반환합니다.
- `count()`: 요소의 수를 반환합니다.
- `findFirst()`: 첫 번째 요소를 포함하는 `Optional` 객체를 반환합니다.
- `findAny()`: 임의의 요소를 포함하는 `Optional` 객체를 반환합니다.
- `forEach(BiConsumer<E, Long> consumer)`: 각 요소에 대해 작업을 수행합니다.
- `groupBy(Function<E, K> keyExtractor)`: 키 추출기에 따라 요소를 그룹화합니다.
- `join(String delimiter)`: 지정된 구분자를 사용하여 요소를 단일 문자열로 연결합니다.
- `print(OutputStream stream)`: 요소를 지정된 출력 스트림에 인쇄합니다.
- `reduce(BinaryOperator<E> operator)`: 지정된 연산자를 사용하여 요소를 축소합니다.
- `toList()`: 컬렉션을 `List`로 변환합니다.
- `toMap(Function<E, K> keyExtractor, Function<E, V> valueExtractor)`: 컬렉션을 `Map`로 변환합니다.

#### 사용 예:

```java
Collectable<Integer> collectable = new Collectable<>(generator);
boolean hasEven = collectable.anyMatch(n -> n % 2 == 0);
System.out.println("짝수가 존재함: " + hasEven);
```

### 2. Collector<A, E, R>

`Collector` 클래스는 요소를 가변 결과 컨테이너에 축적하는 데 사용됩니다.

#### 메서드:

- `collect(Generator<E> generator)`: 생성기에서 요소를 수집합니다.
- `collect(Iterable<E> iterable)`: 반복 가능한 객체에서 요소를 수집합니다.
- `collect(E[] elements)`: 배열에서 요소를 수집합니다.

#### 사용 예:

```java
Collector<Integer, Integer, Integer> collector = Collector.full(0, (acc, elem) -> acc + elem, Integer::sum);
int sum = collector.collect(generator);
System.out.println("합계: " + sum);
```

### 3. Statistics<E, D extends Number>

`Statistics` 클래스는 통계 측정을 계산하는 메서드를 제공합니다.

#### 메서드:

- `mean()`: 요소의 평균을 계산합니다.
- `median()`: 요소의 중앙값을 계산합니다.
- `mode()`: 요소의 최빈값을 계산합니다.
- `variance()`: 요소의 분산을 계산합니다.
- `standardDeviation()`: 요소의 표준 편차를 계산합니다.

#### 사용 예:

```java
Statistics<Integer, Double> stats = new IntegerStatistics<>(generator);
double average = stats.mean();
System.out.println("평균: " + average);
```

### 4. OrderedCollectable<E>

`OrderedCollectable` 클래스는 `Collectable`을 확장하고 순서가 있는 컬렉션을 처리하기 위한 메서드를 제공합니다.

#### 메서드:

- `sorted()`: 요소를 정렬합니다.
- `sorted(Comparator<E> comparator)`: 지정된 비교자를 사용하여 요소를 정렬합니다.

#### 사용 예:

```java
OrderedCollectable<Integer> ordered = new OrderedCollectable<>(generator);
List<Integer> sortedList = ordered.sorted().toList();
System.out.println("정렬된 리스트: " + sortedList);
```

### 5. UnorderedCollectable<E>

`UnorderedCollectable` 클래스는 `Collectable`을 확장하고 순서를 고려하지 않는 컬렉션을 처리하기 위한 메서드를 제공합니다.

#### 메서드:

- `toUnordered()`: 컬렉션을 무순서 표현으로 변환합니다.

#### 사용 예:

```java
UnorderedCollectable<Integer> unordered = new UnorderedCollectable<>(generator);
unordered.toUnordered();
```

### 6. Window<E>

`Window` 클래스는 슬라이딩 창이나 터닝 창을 만들기 위한 메서드를 제공합니다.

#### 메서드:

- `slide(long size, long step)`: 지정된 크기와 단계로 슬라이딩 창을 만듭니다.
- `tumble(long size)`: 지정된 크기로 터닝 창을 만듭니다.

#### 사용 예:

```java
Window<Integer> window = new Window<>(generator);
Semantic<Semantic<Integer>> slidingWindow = window.slide(3, 1);
```

## Java 스트림과의 비교

| 특징                           | semantic-java                           | Java 스트림                           |
|--------------------------------|-----------------------------------------|----------------------------------------|
| 통계 분석                       | 지원                                    | 제한적(외부 라이브러리 필요)            |
| 순서가 있는 컬렉션과 순서가 없는 컬렉션 처리 | 지원                                    | 제한적(스트림은 기본적으로 순서가 있음)  |
| 사용자 정의 생성기               | 지원                                    | 지원되지 않음                           |
| 창 작업                         | 지원(슬라이딩 창 및 터닝 창)            | 지원되지 않음                           |
| 성능 최적화                     | 특정 시나리오에 최적화됨                | 일반적인 용도에 강하지만 특정 작업에 최적화되지 않을 수 있음 |

## 특별한 설명

- **toUnordered** : 이 메서드는 요소의 순서를 고려하지 않고 성능을 최적화합니다. 요소의 순서가 중요하지 않은 시나리오에 적합합니다.
- **toXXX(예: toList, toMap)** : 이러한 메서드는 요소의 순서를 고려하며 순서를 유지해야 하는 시나리오에 적합합니다.
- **인덱스 작업** : `redirect`, `reverse` 또는 `translate`를 호출한 후에 `sorted`를 호출하면 인덱스 작업이 무효화될 수 있습니다. 왜냐하면 정렬은 이러한 작업을 덮어쓰기 때문입니다.

## 결론

`semantic-java`는 함수형 프로그래밍과 성능 최적화를 결합한 강력한 Java 컬렉션 처리 라이브러리입니다. 복잡한 통계 분석을 수행해야 하거나 대규모 데이터 세트를 효율적으로 처리해야 하거나 컬렉션에 대한 더 많은 제어가 필요한 경우, `semantic-java`는 필요한 도구를 제공합니다.

이 라이브러리가 도움이 되기를 바랍니다. 귀하의 피드백을 기다리고 있습니다! 🌟

---

*이 사용 설명서는 시작점이며, 라이브러리가 발전함에 따라 더 자세한 문서와 예제를 제공할 것입니다.*
