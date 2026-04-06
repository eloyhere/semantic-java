package pers.eloyhere.semantic;

import java.util.*;
import java.util.function.*;

public abstract class Collectable <E> {

    private final long concurrent;

    protected Collectable(long concurrent) {
        this.concurrent = concurrent;
    }

    public boolean anyMatch(final Predicate<E> predicate) {
        Collector<E, Boolean, Boolean> collector = Collectors.useAnyMatch(predicate);
        return collector.collect(this.source(), this.concurrent);
    }

    public boolean anyMatch(final BiPredicate<E, Long> predicate) {
        Collector<E, Boolean, Boolean> collector = Collectors.useAnyMatch(predicate);
        return collector.collect(this.source(), this.concurrent);
    }

    public boolean allMatch(final Predicate<E> predicate) {
        Collector<E, Boolean, Boolean> collector = Collectors.useAllMatch(predicate);
        return collector.collect(this.source(), this.concurrent);
    }

    public boolean allMatch(final BiPredicate<E, Long> predicate) {
        Collector<E, Boolean, Boolean> collector = Collectors.useAllMatch(predicate);
        return collector.collect(this.source(), this.concurrent);
    }

    public boolean noneMatch(final Predicate<E> predicate) {
        Collector<E, Boolean, Boolean> collector = Collectors.useNoneMatch(predicate);
        return collector.collect(this.source(), this.concurrent);
    }

    public boolean noneMatch(final BiPredicate<E, Long> predicate) {
        Collector<E, Boolean, Boolean> collector = Collectors.useNoneMatch(predicate);
        return collector.collect(this.source(), this.concurrent);
    }

    public Optional<E> findFirst() {
        Collector<E, Optional<E>, Optional<E>> collector = Collectors.useFindFirst();
        return collector.collect(this.source(), this.concurrent);
    }

    public Optional<E> findLast() {
        Collector<E, Optional<E>, Optional<E>> collector = Collectors.useFindLast();
        return collector.collect(this.source(), this.concurrent);
    }

    public Optional<E> findAny() {
        Collector<E, Optional<E>, Optional<E>> collector = Collectors.useFindAny();
        return collector.collect(this.source(), this.concurrent);
    }

    public Optional<E> findAt(long index) {
        if (index < 0) {
            Collector<E, List<E>, Optional<E>> collector = Collectors.useFindNegativeAt(index);
            return collector.collect(this.source(), this.concurrent);
        } else {
            Collector<E, Optional<E>, Optional<E>> collector = Collectors.useFindAt(index);
            return collector.collect(this.source(), this.concurrent);
        }
    }

    public Optional<E> findMaximum() {
        Collector<E, Optional<E>, Optional<E>> collector = Collectors.useFindMaximum();
        return collector.collect(this.source(), this.concurrent);
    }

    public Optional<E> findMaximum(Comparator<E> comparator) {
        Collector<E, Optional<E>, Optional<E>> collector = Collectors.useFindMaximum(comparator);
        return collector.collect(this.source(), this.concurrent);
    }

    public Optional<E> findMinimum() {
        Collector<E, Optional<E>, Optional<E>> collector = Collectors.useFindMinimum();
        return collector.collect(this.source(), this.concurrent);
    }

    public Optional<E> findMinimum(Comparator<E> comparator) {
        Collector<E, Optional<E>, Optional<E>> collector = Collectors.useFindMinimum(comparator);
        return collector.collect(this.source(), this.concurrent);
    }

    public Optional<E> reduce(BiFunction<E, E, E> operator) {
        Collector<E, Optional<E>, Optional<E>> collector = Collectors.useReduce(operator);
        return collector.collect(this.source(), this.concurrent);
    }

    public E reduce(E identity, final BiFunction<E, E, E> operator) {
        Collector<E, E, E> collector = Collectors.useReduce(identity, operator);
        return collector.collect(this.source(), this.concurrent);
    }

    public <R> Optional<R> reduce(R identity, final BiFunction<R, E, R> operator, final BiFunction<R, R, R> combiner) {
        Collector<E, R, R> collector = Collectors.useReduce(identity, operator, combiner);
        return Optional.ofNullable(collector.collect(this.source(), this.concurrent));
    }

    public long count() {
        Collector<E, Long, Long> collector = Collectors.useCount();
        return collector.collect(this.source(), this.concurrent);
    }

    public long forEach(Consumer<E> consumer) {
        Collector<E, Long, Long> collector = Collectors.useForEach(consumer);
        return collector.collect(this.source(), this.concurrent);
    }

    public long forEach(BiConsumer<E, Long> consumer) {
        Collector<E, Long, Long> collector = Collectors.useForEach(consumer);
        return collector.collect(this.source(), this.concurrent);
    }

    public List<E> toList() {
        Collector<E, List<E>, List<E>> collector = Collectors.toList();
        return collector.collect(this.source(), this.concurrent);
    }

    public Set<E> toHashSet() {
        Collector<E, Set<E>, Set<E>> collector = Collectors.toHashSet();
        return collector.collect(this.source(), this.concurrent);
    }

    public Set<E> toTreeSet() {
        Collector<E, Set<E>, Set<E>> collector = Collectors.useToTreeSet();
        return collector.collect(this.source(), this.concurrent);
    }

    public <K> Map<K, E> toHashMap(Function<E, K> keyExtractor) {
        Collector<E, Map<K, E>, Map<K, E>> collector = Collectors.useToHashMap(keyExtractor);
        return collector.collect(this.source(), this.concurrent);
    }

    public <K, V> Map<K, V> toHashMap(Function<E, K> keyExtractor, Function<E, V> valueExtractor) {
        Collector<E, Map<K, V>, Map<K, V>> collector = Collectors.useToHashMap(keyExtractor, valueExtractor);
        return collector.collect(this.source(), this.concurrent);
    }

    public <K extends Comparable<?>> Map<K, E> toTreeMap(Function<E, K> keyExtractor) {
        Collector<E, Map<K, E>, Map<K, E>> collector = Collectors.useToTreeMap(keyExtractor);
        return collector.collect(this.source(), this.concurrent);
    }

    public <K extends Comparable<?>, V> Map<K, V> toTreeMap(Function<E, K> keyExtractor, Function<E, V> valueExtractor) {
        Collector<E, Map<K, V>, Map<K, V>> collector = Collectors.useToTreeMap(keyExtractor, valueExtractor);
        return collector.collect(this.source(), this.concurrent);
    }

    public String join() {
        Collector<E, StringBuilder, String> collector = Collectors.useJoin();
        return collector.collect(this.source(), this.concurrent);
    }

    public String join(String delimiter) {
        Collector<E, StringBuilder, String> collector = Collectors.useJoin(delimiter);
        return collector.collect(this.source(), this.concurrent);
    }

    public String join(String prefix, String delimiter, String suffix) {
        Collector<E, StringBuilder, String> collector = Collectors.useJoin(prefix, delimiter, suffix);
        return collector.collect(this.source(), this.concurrent);
    }

    public <K> Map<K, List<E>> group(Function<E, K> keyExtractor) {
        Collector<E, Map<K, List<E>>, Map<K, List<E>>> collector = Collectors.useGroup(keyExtractor);
        return collector.collect(this.source(), this.concurrent);
    }

    public <K> Map<K, List<E>> group(BiFunction<E, Long, K> keyExtractor) {
        Collector<E, Map<K, List<E>>, Map<K, List<E>>> collector = Collectors.useGroup(keyExtractor);
        return collector.collect(this.source(), this.concurrent);
    }

    public <K, V> Map<K, List<V>> groupBy(Function<E, K> keyExtractor, Function<E, V> valueExtractor) {
        Collector<E, Map<K, List<V>>, Map<K, List<V>>> collector = Collectors.useGroupBy(keyExtractor, valueExtractor);
        return collector.collect(this.source(), this.concurrent);
    }

    public <K, V> Map<K, List<V>> groupBy(BiFunction<E, Long, K> keyExtractor, BiFunction<E, Long, V> valueExtractor) {
        Collector<E, Map<K, List<V>>, Map<K, List<V>>> collector = Collectors.useGroupBy(keyExtractor, valueExtractor);
        return collector.collect(this.source(), this.concurrent);
    }

    public List<List<E>> partition(long count) {
        Collector<E, Map<Long, List<E>>, List<List<E>>> collector = Collectors.usePartition(count);
        return collector.collect(this.source(), this.concurrent);
    }

    public List<List<E>> partitionBy(Function<E, Long> keyExtractor) {
        Collector<E, Map<Long, List<E>>, List<List<E>>> collector = Collectors.usePartitionBy(keyExtractor);
        return collector.collect(this.source(), this.concurrent);
    }

    public List<List<E>> partitionBy(BiFunction<E, Long, Long> keyExtractor) {
        Collector<E, Map<Long, List<E>>, List<List<E>>> collector = Collectors.usePartitionBy(keyExtractor);
        return collector.collect(this.source(), this.concurrent);
    }

    public abstract Generator<E> source();
}