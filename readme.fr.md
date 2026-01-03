# semantic-java

Bienvenue dans la bibliothèque `semantic-java` ! 🚀 Cette bibliothèque est conçue pour simplifier le traitement des collections en Java et offre un ensemble d'outils puissants pour l'analyse statistique, le traitement de flux et d'autres scénarios. Que vous ayez besoin d'effectuer des analyses statistiques, de travailler avec des collections ordonnées ou non ordonnées, ou de définir des générateurs personnalisés, `semantic-java` fournit les outils nécessaires.

## Vue d'ensemble

La bibliothèque `semantic-java` simplifie les opérations complexes sur les collections en fournissant des abstractions de haut niveau et des outils pratiques. Elle combine les capacités de programmation fonctionnelle pour offrir des API concises et expressives.

## Principales caractéristiques

- **Analyse statistique** : prend en charge le calcul de moyennes, médianes, modes, variances, etc.
- **Collections ordonnées et non ordonnées** : gère les collections avec un ordre spécifique ou sans aucune contrainte d'ordre.
- **Traitement de flux** : offre des API de traitement de flux similaires à celles de Java Streams, mais avec des fonctionnalités supplémentaires.
- **Générateurs personnalisés** : permet de définir des générateurs personnalisés pour produire des séquences d'éléments.

## Documentation de l'API

Voici une description détaillée des principales API de la bibliothèque `semantic-java`, accompagnée d'exemples d'utilisation.

### 1. Collectable<E>

La classe `Collectable` est au cœur de la bibliothèque et fournit des méthodes pour collecter des éléments en fonction de divers critères.

#### Méthodes :

- `anyMatch(Predicate<E> predicate)`: Retourne `true` si au moins un élément correspond au prédicat donné.
- `allMatch(Predicate<E> predicate)`: Retourne `true` si tous les éléments correspondent au prédicat donné.
- `noneMatch(Predicate<E> predicate)`: Retourne `true` si aucun élément ne correspond au prédicat donné.
- `count()`: Retourne le nombre d'éléments.
- `findFirst()`: Retourne un `Optional` contenant le premier élément.
- `findAny()`: Retourne un `Optional` contenant n'importe quel élément.
- `forEach(BiConsumer<E, Long> consumer)`: Exécute une action pour chaque élément.
- `groupBy(Function<E, K> keyExtractor)`: Regroupe les éléments par une clé extraite.
- `join(String delimiter)`: Joint les éléments en une seule chaîne, séparés par le délimiteur donné.
- `print(OutputStream stream)`: Imprime les éléments dans le flux de sortie donné.
- `reduce(BinaryOperator<E> operator)`: Réduit les éléments en utilisant l'opérateur donné.
- `toList()`: Convertit la collection en `List`.
- `toMap(Function<E, K> keyExtractor, Function<E, V> valueExtractor)`: Convertit la collection en `Map`.

#### Exemple d'utilisation :

```java
Collectable<Integer> collectable = new Collectable<>(generateur);
boolean hasEven = collectable.anyMatch(n -> n % 2 == 0);
System.out.println("Existe-t-il un nombre pair : " + hasEven);
```

### 2. Collector<A, E, R>

La classe `Collector` est utilisée pour accumuler des éléments dans un conteneur de résultat mutable.

#### Méthodes :

- `collect(Generator<E> generator)`: Collecte des éléments à partir du générateur.
- `collect(Iterable<E> iterable)`: Collecte des éléments à partir de l'itérable.
- `collect(E[] elements)`: Collecte des éléments à partir du tableau.

#### Exemple d'utilisation :

```java
Collector<Integer, Integer, Integer> collector = Collector.full(0, (acc, elem) -> acc + elem, Integer::sum);
int sum = collector.collect(generateur);
System.out.println("Somme : " + sum);
```

### 3. Statistics<E, D extends Number>

La classe `Statistics` fournit des méthodes pour calculer des mesures statistiques.

#### Méthodes :

- `mean()`: Calcule la moyenne des éléments.
- `median()`: Calcule la médiane des éléments.
- `mode()`: Calcule le mode des éléments.
- `variance()`: Calcule la variance des éléments.
- `standardDeviation()`: Calcule l'écart type des éléments.

#### Exemple d'utilisation :

```java
Statistics<Integer, Double> stats = new IntegerStatistics<>(generateur);
double average = stats.mean();
System.out.println("Moyenne : " + average);
```

### 4. OrderedCollectable<E>

La classe `OrderedCollectable` étend `Collectable` et fournit des méthodes pour traiter des collections ordonnées.

#### Méthodes :

- `sorted()`: Trie les éléments.
- `sorted(Comparator<E> comparator)`: Trie les éléments en utilisant le comparateur donné.

#### Exemple d'utilisation :

```java
OrderedCollectable<Integer> ordered = new OrderedCollectable<>(generateur);
List<Integer> sortedList = ordered.sorted().toList();
System.out.println("Liste triée : " + sortedList);
```

### 5. UnorderedCollectable<E>

La classe `UnorderedCollectable` étend `Collectable` et fournit des méthodes pour traiter des collections non ordonnées.

#### Méthodes :

- `toUnordered()`: Convertit la collection en une représentation non ordonnée.

#### Exemple d'utilisation :

```java
UnorderedCollectable<Integer> unordered = new UnorderedCollectable<>(generateur);
unordered.toUnordered();
```

### 6. Window<E>

La classe `Window` fournit des méthodes pour créer des fenêtres glissantes ou des fenêtres basculantes sur la collection.

#### Méthodes :

- `slide(long size, long step)`: Crée une fenêtre glissante de la taille et de l'étape données.
- `tumble(long size)`: Crée une fenêtre basculante de la taille donnée.

#### Exemple d'utilisation :

```java
Window<Integer> window = new Window<>(generateur);
Semantic<Semantic<Integer>> slidingWindow = window.slide(3, 1);
```

## Comparaison avec Java Streams

| Fonctionnalité                   | semantic-java                           | Java Streams                           |
|----------------------------------|-----------------------------------------|----------------------------------------|
| Analyse statistique              | Pris en charge                          | Limité (nécessite des bibliothèques externes)|
| Traitement de collections ordonnées et non ordonnées | Pris en charge                          | Limité (les flux sont par défaut ordonnés)|
| Générateurs personnalisés        | Pris en charge                          | Non pris en charge                      |
| Opérations sur les fenêtres      | Pris en charge (fenêtres glissantes et basculantes) | Non pris en charge                      |
| Optimisation des performances    | Optimisé pour des scénarios spécifiques | Polyvalent, mais peut ne pas être optimisé pour des tâches spécifiques|

## Remarques particulières

- **toUnordered** : Cette méthode ne prend pas en compte l'ordre des éléments et est optimisée pour les performances. Utilisez-la lorsque l'ordre des éléments n'est pas important.
- **toXXX (par exemple, toList, toMap)** : Ces méthodes prennent en compte l'ordre des éléments et sont utiles lorsque la séquence est importante.
- **Opérations d'indexation** : Si vous appelez `redirect`, `reverse` ou `translate` avant de trier, les opérations d'indexation peuvent devenir invalides, car le tri remplace ces opérations.

## Conclusion

`semantic-java` est une bibliothèque puissante pour le traitement des collections en Java, combinant la programmation fonctionnelle et l'optimisation des performances. Que vous deviez effectuer des analyses statistiques complexes, traiter efficacement de grands ensembles de données ou avoir plus de contrôle sur vos collections, `semantic-java` offre les outils nécessaires.

Nous espérons que cette bibliothèque vous sera utile et attendons vos commentaires ! 🌟

---

*Cette notice d'utilisation est un point de départ et sera complétée avec des documents et des exemples plus détaillés à mesure que la bibliothèque évoluera.*
