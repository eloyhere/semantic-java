# semantic-java

¡Bienvenido a la biblioteca `semantic-java`! 🚀 Esta biblioteca está diseñada para simplificar el manejo de colecciones en Java y ofrece una serie de herramientas potentes para análisis estadístico, procesamiento de flujos y otros escenarios. Ya sea que necesites realizar análisis estadísticos, trabajar con colecciones ordenadas o no ordenadas, o definir generadores personalizados, `semantic-java` tiene las herramientas que necesitas.

## Visión general

La biblioteca `semantic-java` simplifica operaciones complejas en colecciones proporcionando abstracciones de alto nivel y herramientas prácticas. Combina capacidades de programación funcional para ofrecer APIs concisas y expresivas.

## Características principales

- **Análisis estadístico**: soporta cálculos como promedio, mediana, moda, varianza, etc.
- **Colecciones ordenadas y no ordenadas**: maneja colecciones con un orden específico o sin restricciones de orden.
- **Procesamiento de flujos**: ofrece APIs para procesar flujos similares a los de Java Streams, pero con funcionalidades adicionales.
- **Generadores personalizados**: permite definir generadores personalizados para producir secuencias de elementos.

## Documentación de la API

A continuación se presenta una descripción detallada de las principales API de la biblioteca `semantic-java`, junto con ejemplos de uso.

### 1. Collectable<E>

La clase `Collectable` es el núcleo de la biblioteca y proporciona métodos para recopilar elementos según varios criterios.

#### Métodos:

- `anyMatch(Predicate<E> predicate)`: Devuelve `true` si algún elemento coincide con el predicado dado.
- `allMatch(Predicate<E> predicate)`: Devuelve `true` si todos los elementos coinciden con el predicado dado.
- `noneMatch(Predicate<E> predicate)`: Devuelve `true` si ningún elemento coincide con el predicado dado.
- `count()`: Devuelve el número de elementos.
- `findFirst()`: Devuelve un `Optional` que contiene el primer elemento.
- `findAny()`: Devuelve un `Optional` que contiene cualquier elemento.
- `forEach(BiConsumer<E, Long> consumer)`: Realiza una acción para cada elemento.
- `groupBy(Function<E, K> keyExtractor)`: Agrupa elementos por una clave extraída.
- `join(String delimiter)`: Une elementos en una sola cadena, separados por el delimitador dado.
- `print(OutputStream stream)`: Imprime elementos en el flujo de salida dado.
- `reduce(BinaryOperator<E> operator)`: Reduce los elementos usando el operador dado.
- `toList()`: Convierte la colección en una `List`.
- `toMap(Function<E, K> keyExtractor, Function<E, V> valueExtractor)`: Convierte la colección en un `Map`.

#### Ejemplo de uso:

```java
Collectable<Integer> collectable = new Collectable<>(generador);
boolean hasEven = collectable.anyMatch(n -> n % 2 == 0);
System.out.println("Existe un número par: " + hasEven);
```

### 2. Collector<A, E, R>

La clase `Collector` se utiliza para acumular elementos en un contenedor de resultados mutable.

#### Métodos:

- `collect(Generator<E> generator)`: Recopila elementos del generador.
- `collect(Iterable<E> iterable)`: Recopila elementos de la colección iterable.
- `collect(E[] elements)`: Recopila elementos del arreglo.

#### Ejemplo de uso:

```java
Collector<Integer, Integer, Integer> collector = Collector.full(0, (acc, elem) -> acc + elem, Integer::sum);
int sum = collector.collect(generador);
System.out.println("Suma: " + sum);
```

### 3. Statistics<E, D extends Number>

La clase `Statistics` proporciona métodos para calcular medidas estadísticas.

#### Métodos:

- `mean()`: Calcula la media de los elementos.
- `median()`: Calcula la mediana de los elementos.
- `mode()`: Calcula la moda de los elementos.
- `variance()`: Calcula la varianza de los elementos.
- `standardDeviation()`: Calcula la desviación estándar de los elementos.

#### Ejemplo de uso:

```java
Statistics<Integer, Double> stats = new IntegerStatistics<>(generador);
double average = stats.mean();
System.out.println("Promedio: " + average);
```

### 4. OrderedCollectable<E>

La clase `OrderedCollectable` extiende `Collectable` y proporciona métodos para manejar colecciones ordenadas.

#### Métodos:

- `sorted()`: Ordena los elementos.
- `sorted(Comparator<E> comparator)`: Ordena los elementos usando el comparador dado.

#### Ejemplo de uso:

```java
OrderedCollectable<Integer> ordered = new OrderedCollectable<>(generador);
List<Integer> sortedList = ordered.sorted().toList();
System.out.println("Lista ordenada: " + sortedList);
```

### 5. UnorderedCollectable<E>

La clase `UnorderedCollectable` extiende `Collectable` y proporciona métodos para manejar colecciones no ordenadas.

#### Métodos:

- `toUnordered()`: Convierte la colección en una representación no ordenada.

#### Ejemplo de uso:

```java
UnorderedCollectable<Integer> unordered = new UnorderedCollectable<>(generador);
unordered.toUnordered();
```

### 6. Window<E>

La clase `Window` proporciona métodos para crear ventanas deslizantes o tumbladoras sobre la colección.

#### Métodos:

- `slide(long size, long step)`: Crea una ventana deslizante del tamaño y paso dados.
- `tumble(long size)`: Crea una ventana tumbladora del tamaño dado.

#### Ejemplo de uso:

```java
Window<Integer> window = new Window<>(generador);
Semantic<Semantic<Integer>> slidingWindow = window.slide(3, 1);
```

## Comparación con Java Streams

| Característica                   | semantic-java                           | Java Streams                           |
|----------------------------------|-----------------------------------------|----------------------------------------|
| Análisis estadístico             | Soportado                               | Limitado (requiere bibliotecas externas)|
| Manejo de colecciones ordenadas y no ordenadas | Soportado                               | Limitado (los streams son ordenados por defecto)|
| Generadores personalizados       | Soportado                               | No soportado                            |
| Operaciones de ventana           | Soportado (ventanas deslizantes y tumbladoras) | No soportado                            |
| Optimización del rendimiento   | Optimizado para escenarios específicos  | Genérico, puede no estar optimizado para tareas específicas|

## Notas especiales

- **toUnordered**: Este método no considera el orden de los elementos y está optimizado para el rendimiento. Úselo cuando el orden de los elementos no sea importante.
- **toXXX (por ejemplo, toList, toMap)**: Estos métodos consideran el orden de los elementos y son útiles cuando la secuencia es importante.
- **Operaciones de índice**: Si llama a `redirect`, `reverse` o `translate` antes de llamar a `sorted`, las operaciones de índice pueden volverse inválidas, ya que el ordenamiento anula estas operaciones.

## Conclusión

`semantic-java` es una biblioteca poderosa para el procesamiento de colecciones en Java, combinando programación funcional con optimización del rendimiento. Ya sea que necesite realizar análisis estadísticos complejos, procesar eficientemente grandes conjuntos de datos o necesite más control sobre sus colecciones, `semantic-java` proporciona las herramientas necesarias.

Esperamos que esta biblioteca sea útil para usted y esperamos sus comentarios. ¡Estrellas! 🌟

---

*Esta guía de inicio es solo el comienzo y se expandirá con documentación más detallada y ejemplos a medida que la biblioteca evolucione.*
