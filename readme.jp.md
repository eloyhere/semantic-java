# semantic-java

`semantic-java`ライブラリのご利用を歓迎します！🚀 このライブラリは、Javaでのコレクション処理を簡素化するために設計されており、統計分析やストリーム処理などのシナリオに適した豊富な機能を提供しています。要素の統計解析が必要な場合でも、順序付けられたコレクションや無順序コレクションを扱う必要がある場合でも、カスタムジェネレータを定義する必要がある場合でも、`semantic-java`は必要なツールを提供します。

## 概要

`semantic-java`ライブラリは、高水準の抽象化と実用的なツールを提供することで、複雑なコレクション操作を簡素化します。関数型プログラミングの特性を組み合わせて、シンプルで表現力のあるAPIを提供しています。

## 主な特徴

- **統計解析**：平均値、中央値、最頻値、分散など、様々な統計計算をサポートしています。
- **順序付けられたコレクションと無順序コレクション**：特定の順序を持つコレクションや順序制約のないコレクションを処理します。
- **ストリーム処理**：Javaのストリームに似た流れるようなAPIを提供し、さらに多くの機能を備えています。
- **カスタムジェネレータ**：要素のシーケンスを生成するためのカスタムジェネレータを定義することができます。

## APIドキュメント

以下は、`semantic-java`ライブラリの主要なAPIの詳細な説明と使用例です。

### 1. Collectable<E>

`Collectable`クラスはライブラリの中核であり、異なる条件に基づいて要素を収集するためのメソッドを提供します。

#### メソッド：

- `anyMatch(Predicate<E> predicate)`：任意の要素が指定された条件に一致する場合は`true`を返します。
- `allMatch(Predicate<E> predicate)`：すべての要素が指定された条件に一致する場合は`true`を返します。
- `noneMatch(Predicate<E> predicate)`：どの要素も指定された条件に一致しない場合は`true`を返します。
- `count()`：要素の数を返します。
- `findFirst()`：最初の要素を含む`Optional`オブジェクトを返します。
- `findAny()`：任意の要素を含む`Optional`オブジェクトを返します。
- `forEach(BiConsumer<E, Long> consumer)`：各要素に対して操作を実行します。
- `groupBy(Function<E, K> keyExtractor)`：キー抽出器に基づいて要素をグループ化します。
- `join(String delimiter)`：指定された区切り文字を使用して要素を単一の文字列に連結します。
- `print(OutputStream stream)`：要素を指定された出力ストリームに印刷します。
- `reduce(BinaryOperator<E> operator)`：指定された演算子を使用して要素を縮小します。
- `toList()`：コレクションを`List`に変換します。
- `toMap(Function<E, K> keyExtractor, Function<E, V> valueExtractor)`：コレクションを`Map`に変換します。

#### 使用例：

```java
Collectable<Integer> collectable = new Collectable<>(generator);
boolean hasEven = collectable.anyMatch(n -> n % 2 == 0);
System.out.println("偶数が存在する: " + hasEven);
```

### 2. Collector<A, E, R>

`Collector`クラスは、要素を可変の結果コンテナに蓄積するために使用されます。

#### メソッド：

- `collect(Generator<E> generator)`：ジェネレータから要素を収集します。
- `collect(Iterable<E> iterable)`：イテレータブルオブジェクトから要素を収集します。
- `collect(E[] elements)`：配列から要素を収集します。

#### 使用例：

```java
Collector<Integer, Integer, Integer> collector = Collector.full(0, (acc, elem) -> acc + elem, Integer::sum);
int sum = collector.collect(generator);
System.out.println("合計: " + sum);
```

### 3. Statistics<E, D extends Number>

`Statistics`クラスは、統計測定を計算するためのメソッドを提供します。

#### メソッド：

- `mean()`：要素の平均値を計算します。
- `median()`：要素の中央値を計算します。
- `mode()`：要素の最頻値を計算します。
- `variance()`：要素の分散を計算します。
- `standardDeviation()`：要素の標準偏差を計算します。

#### 使用例：

```java
Statistics<Integer, Double> stats = new IntegerStatistics<>(generator);
double average = stats.mean();
System.out.println("平均: " + average);
```

### 4. OrderedCollectable<E>

`OrderedCollectable`クラスは`Collectable`を拡張し、順序付けられたコレクションを処理するためのメソッドを提供します。

#### メソッド：

- `sorted()`：要素をソートします。
- `sorted(Comparator<E> comparator)`：指定された比較子を使用して要素をソートします。

#### 使用例：

```java
OrderedCollectable<Integer> ordered = new OrderedCollectable<>(generator);
List<Integer> sortedList = ordered.sorted().toList();
System.out.println("ソート後のリスト: " + sortedList);
```

### 5. UnorderedCollectable<E>

`UnorderedCollectable`クラスは`Collectable`を拡張し、順序を考慮しないコレクションを処理するためのメソッドを提供します。

#### メソッド：

- `toUnordered()`：コレクションを無順序の表現に変換します。

#### 使用例：

```java
UnorderedCollectable<Integer> unordered = new UnorderedCollectable<>(generator);
unordered.toUnordered();
```

### 6. Window<E>

`Window`クラスは、スライディングウィンドウやタンブリングウィンドウを作成するためのメソッドを提供します。

#### メソッド：

- `slide(long size, long step)`：指定されたサイズとステップでスライディングウィンドウを作成します。
- `tumble(long size)`：指定されたサイズでタンブリングウィンドウを作成します。

#### 使用例：

```java
Window<Integer> window = new Window<>(generator);
Semantic<Semantic<Integer>> slidingWindow = window.slide(3, 1);
```

## Java Streamsとの比較

| 特徴                           | semantic-java                           | Java Streams                           |
|--------------------------------|-----------------------------------------|----------------------------------------|
| 統計解析                       | サポート                                | 限定的（外部ライブラリが必要）          |
| 順序付けられたコレクションと無順序コレクションの処理 | サポート                                | 限定的（Streamsはデフォルトで順序付けられている）|
| カスタムジェネレータ           | サポート                                | サポートされていない                    |
| ウィンドウ操作                 | サポート（スライディングウィンドウとタンブリングウィンドウ）| サポートされていない                    |
| パフォーマンスの最適化         | 特定のシナリオに最適化されている        | 一般的な用途に強いが、特定のタスクに最適化されていない場合がある|

## 特記事項

- **toUnordered**：このメソッドは要素の順序を考慮せず、パフォーマンスを最適化します。要素の順序が重要でないシナリオに適しています。
- **toXXX（例：toList、toMap）**：これらのメソッドは要素の順序を考慮し、シーケンスを保持する必要があるシナリオに適しています。
- **インデックス操作**：`redirect`、`reverse`、または`translate`を呼び出した後に`sorted`を呼び出すと、インデックス操作が無効になる可能性があります。なぜなら、ソートはこれらの操作を上書きするからです。

## 結論

`semantic-java`は、関数型プログラミングとパフォーマンスの最適化を組み合わせた強力なJavaコレクション処理ライブラリです。複雑な統計解析を行う必要がある場合でも、大規模なデータセットを効率的に処理する必要がある場合でも、コレクションに対するより多くの制御が必要な場合でも、`semantic-java`は必要なツールを提供します。

このライブラリがお役に立てれば幸いです。ご意見をお待ちしております！🌟

---

*この使用説明書は出発点であり、ライブラリが進化するにつれて、より詳細なドキュメントと例を提供します。*
