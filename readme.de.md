# semantic-java

Willkommen bei der `semantic-java` Bibliothek! 🚀 Diese Bibliothek ist darauf ausgelegt, die Arbeit mit Sammlungen in Java zu erleichtern und bietet eine Reihe leistungsstarker Werkzeuge für statistische Analysen, Stream-Verarbeitung und andere Szenarien. Ob Sie nun statistische Analysen durchführen, mit geordneten oder ungeordneten Sammlungen arbeiten oder benutzerdefinierte Erzeuger definieren müssen, `semantic-java` bietet Ihnen die benötigten Werkzeuge.

## Übersicht

Die `semantic-java` Bibliothek vereinfacht komplexe Sammlungsoperationen, indem sie hohe Abstraktionsebenen und praktische Werkzeuge bereitstellt. Sie kombiniert funktionale Programmierfähigkeiten, um prägnante und ausdrucksstarke APIs anzubieten.

## Hauptmerkmale

- **Statistische Analyse**: Unterstützt Berechnungen wie Durchschnitt, Median, Modus, Varianz und mehr.
- **Geordnete und ungeordnete Sammlungen**: Behandelt Sammlungen mit einer bestimmten Reihenfolge oder ohne jegliche Reihenfolgebeschränkungen.
- **Stream-Verarbeitung**: Bietet APIs zur Verarbeitung von Streams, ähnlich Java Streams, aber mit zusätzlichen Funktionen.
- **Benutzerdefinierte Erzeuger**: Ermöglicht das Definieren benutzerdefinierter Erzeuger zur Generierung von Elementsequenzen.

## API-Dokumentation

Im Folgenden finden Sie eine detaillierte Beschreibung der wichtigsten APIs der `semantic-java` Bibliothek zusammen mit Nutzungsbefehlen.

### 1. Collectable<E>

Die `Collectable` Klasse ist das Herzstück der Bibliothek und bietet Methoden zum Sammeln von Elementen basierend auf verschiedenen Kriterien.

#### Methoden:

- `anyMatch(Predicate<E> predicate)`: Gibt `true` zurück, wenn irgendein Element dem gegebenen Prädikat entspricht.
- `allMatch(Predicate<E> predicate)`: Gibt `true` zurück, wenn alle Elemente dem gegebenen Prädikat entsprechen.
- `noneMatch(Predicate<E> predicate)`: Gibt `true` zurück, wenn kein Element dem gegebenen Prädikat entspricht.
- `count()`: Gibt die Anzahl der Elemente zurück.
- `findFirst()`: Gibt ein `Optional`, das das erste Element enthält.
- `findAny()`: Gibt ein `Optional`, das ein beliebiges Element enthält.
- `forEach(BiConsumer<E, Long> consumer)`: Führt eine Aktion für jedes Element aus.
- `groupBy(Function<E, K> keyExtractor)`: Gruppiert Elemente nach einem Schlüssel.
- `join(String delimiter)`: Verbindet Elemente zu einem einzelnen String, getrennt durch den angegebenen Trennzeichen.
- `print(OutputStream stream)`: Druckt Elemente in den angegebenen Ausgabestream.
- `reduce(BinaryOperator<E> operator)`: Reduziert die Elemente mit dem gegebenen Operator.
- `toList()`: Konvertiert die Sammlung in eine `List`.
- `toMap(Function<E, K> keyExtractor, Function<E, V> valueExtractor)`: Konvertiert die Sammlung in eine `Map`.

#### Verwendungsbefehl:

```java
Collectable<Integer> collectable = new Collectable<>(generator);
boolean hasEven = collectable.anyMatch(n -> n % 2 == 0);
System.out.println("Gibt es gerade Zahlen: " + hasEven);
```

### 2. Collector<A, E, R>

Die `Collector` Klasse wird verwendet, um Elemente in einen veränderbaren Ergebnisbehälter zu sammeln.

#### Methoden:

- `collect(Generator<E> generator)`: Sammelt Elemente aus dem Generator.
- `collect(Iterable<E> iterable)`: Sammelt Elemente aus dem durchlaufbaren Objekt.
- `collect(E[] elements)`: Sammelt Elemente aus dem Array.

#### Verwendungsbefehl:

```java
Collector<Integer, Integer, Integer> collector = Collector.full(0, (acc, elem) -> acc + elem, Integer::sum);
int sum = collector.collect(generator);
System.out.println("Summe: " + sum);
```

### 3. Statistics<E, D extends Number>

Die `Statistics` Klasse bietet Methoden zum Berechnen statistischer Maße.

#### Methoden:

- `mean()`: Berechnet den Durchschnitt der Elemente.
- `median()`: Berechnet die Mediane der Elemente.
- `mode()`: Berechnet das Modus der Elemente.
- `variance()`: Berechnet die Varianz der Elemente.
- `standardDeviation()`: Berechnet die Standardabweichung der Elemente.

#### Verwendungsbefehl:

```java
Statistics<Integer, Double> stats = new IntegerStatistics<>(generator);
double average = stats.mean();
System.out.println("Durchschnitt: " + average);
```

### 4. OrderedCollectable<E>

Die `OrderedCollectable` Klasse erweitert `Collectable` und bietet Methoden zum Umgang mit geordneten Sammlungen.

#### Methoden:

- `sorted()`: Sortiert die Elemente.
- `sorted(Comparator<E> comparator)`: Sortiert die Elemente mit dem gegebenen Vergleichsoperator.

#### Verwendungsbefehl:

```java
OrderedCollectable<Integer> ordered = new OrderedCollectable<>(generator);
List<Integer> sortedList = ordered.sorted().toList();
System.out.println("Sortierte Liste: " + sortedList);
```

### 5. UnorderedCollectable<E>

Die `UnorderedCollectable` Klasse erweitert `Collectable` und bietet Methoden zum Umgang mit ungeordneten Sammlungen.

#### Methoden:

- `toUnordered()`: Konvertiert die Sammlung in eine ungeordnete Darstellung.

#### Verwendungsbefehl:

```java
UnorderedCollectable<Integer> unordered = new UnorderedCollectable<>(generator);
unordered.toUnordered();
```

### 6. Window<E>

Die `Window` Klasse bietet Methoden zum Erstellen von gleitenden oder rollierenden Fenstern über die Sammlung.

#### Methoden:

- `slide(long size, long step)`: Erstellt ein gleitendes Fenster der angegebenen Größe und Schrittweite.
- `tumble(long size)`: Erstellt ein rollierendes Fenster der angegebenen Größe.

#### Verwendungsbefehl:

```java
Window<Integer> window = new Window<>(generator);
Semantic<Semantic<Integer>> slidingWindow = window.slide(3, 1);
```

## Vergleich mit Java Streams

| Funktion                       | semantic-java                           | Java Streams                           |
|--------------------------------|-----------------------------------------|----------------------------------------|
| Statistische Analyse           | Unterstützt                             | Begrenzt (erfordert externe Bibliotheken)|
| Geordnete und ungeordnete Sammlungen behandeln | Unterstützt                             | Begrenzt (Streams sind standardmäßig geordnet)|
| Benutzerdefinierte Erzeuger    | Unterstützt                             | Nicht unterstützt                       |
| Fensteroperationen             | Unterstützt (gleitende und rollierende Fenster) | Nicht unterstützt                       |
| Leistungsoptimierung           | Für bestimmte Szenarien optimiert       | Allgemeine Zwecke stark, kann aber nicht für bestimmte Aufgaben optimiert sein|

## Besondere Hinweise

- **toUnordered**: Diese Methode berücksichtigt nicht die Reihenfolge der Elemente und ist für die Leistung optimiert. Geeignet für Szenarien, in denen die Reihenfolge der Elemente nicht wichtig ist.
- **toXXX (z.B. toList, toMap)**: Diese Methoden berücksichtigen die Reihenfolge der Elemente und sind für Szenarien geeignet, in denen die Sequenz erhalten bleiben muss.
- **Indexoperationen**: Wenn nach dem Aufruf von `redirect`, `reverse` oder `translate` `sorted` aufgerufen wird, können Indexoperationen ungültig werden, da die Sortierung diese Operationen überschreibt.

## Fazit

`semantic-java` ist eine leistungsstarke Java-Bibliothek für die Verarbeitung von Sammlungen, die funktionales Programmieren mit Leistungsoptimierungen kombiniert. Ob Sie jetzt komplexe statistische Analysen durchführen, große Datensätze effizient verarbeiten oder mehr Kontrolle über Ihre Sammlungen benötigen, `semantic-java` bietet die notwendigen Werkzeuge.

Wir hoffen, dass Ihnen diese Bibliothek hilfreich ist und freuen uns auf Ihr Feedback! 🌟

---

*Diese Anleitung ist ein Ausgangspunkt und wird mit detaillierteren Dokumentationen und Beispielen erweitert, sobald die Bibliothek weiterentwickelt wird.*
