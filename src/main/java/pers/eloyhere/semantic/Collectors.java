package pers.eloyhere.semantic;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.*;

public class Collectors {

    public static <E> Collector<E, Boolean, Boolean> useAnyMatch(Predicate<E> predicate){
        Objects.requireNonNull(predicate, "Predicate could not be null.");
        return Collector.useShortable(
                ()-> false,
                (accumulator, element, index) -> accumulator,
                (accumulator, element, index) -> accumulator || predicate.test(element),
                (a, b) -> a || b,
                (accumulator) -> accumulator
        );
    }

    public static <E> Collector<E, Boolean, Boolean> useAnyMatch(BiPredicate<E, Long> predicate){
        Objects.requireNonNull(predicate, "Predicate could not be null.");
        return Collector.useShortable(
                ()-> false,
                (accumulator, element, index) -> accumulator,
                (accumulator, element, index) -> accumulator || predicate.test(element, index),
                (a, b) -> a || b,
                (accumulator) -> accumulator
        );
    }

    public static <E> Collector<E, Boolean, Boolean> useAllMatch(Predicate<E> predicate) {
        Objects.requireNonNull(predicate, "Predicate could not be null.");
        return Collector.useShortable(
                () -> true,
                (accumulator, element, index) -> !accumulator,
                (accumulator, element, index) -> accumulator && predicate.test(element),
                (a, b) -> a && b,
                (accumulator) -> accumulator
        );
    }

    public static <E> Collector<E, Boolean, Boolean> useAllMatch(BiPredicate<E, Long> predicate) {
        Objects.requireNonNull(predicate, "Predicate could not be null.");
        return Collector.useShortable(
                () -> true,
                (accumulator, element, index) -> !accumulator,
                (accumulator, element, index) -> accumulator && predicate.test(element, index),
                (a, b) -> a && b,
                (accumulator) -> accumulator
        );
    }

    public static <E> Collector<E, Boolean, Boolean> useNoneMatch(Predicate<E> predicate) {
        Objects.requireNonNull(predicate, "Predicate could not be null.");
        return Collector.useShortable(
                () -> true,
                (accumulator, element, index) -> !accumulator,
                (accumulator, element, index) -> accumulator && !predicate.test(element),
                (a, b) -> a && b,
                (accumulator) -> accumulator
        );
    }

    public static <E> Collector<E, Boolean, Boolean> useNoneMatch(BiPredicate<E, Long> predicate) {
        Objects.requireNonNull(predicate, "Predicate could not be null.");
        return Collector.useShortable(
                () -> true,
                (accumulator, element, index) -> !accumulator,
                (accumulator, element, index) -> accumulator && !predicate.test(element, index),
                (a, b) -> a && b,
                (accumulator) -> accumulator
        );
    }

    public static <E> Collector<E, Optional<E>, Optional<E>> useFindFirst() {
        return Collector.useShortable(
                Optional::empty,
                (accumulator, element, index) -> accumulator.isPresent(),
                (accumulator, element, index) -> Optional.of(element),
                (a, b) -> {
                    if(a.isPresent()){
                        return a;
                    }
                    return b;
                },
                (accumulator) -> accumulator
        );
    }

    public static <E> Collector<E, Optional<E>, Optional<E>> useFindLast() {
        return Collector.useFull(
                Optional::empty,
                (accumulator, element, index) -> Optional.of(element),
                (a, b) -> {
                    if(a.isPresent()){
                        return a;
                    }
                    return b;
                },
                (accumulator) -> accumulator
        );
    }

    public static <E> Collector<E, Optional<E>, Optional<E>> useFindAny() {
        return Collector.useShortable(
                Optional::empty,
                (accumulator, element, index) -> accumulator.isPresent(),
                (accumulator, element, index) -> {
                    Random random = new Random();
                    if(random.nextBoolean()){
                        return Optional.of(element);
                    }
                    return accumulator;
                },
                (a, b) -> {
                    if(a.isPresent()){
                        return a;
                    }
                    return b;
                },
                (accumulator) -> accumulator
        );
    }

    public static <E> Collector<E, Optional<E>, Optional<E>> useFindAt(long target) {
        if(target < 0){
            throw new IllegalArgumentException("Use function \"useFindNegativeAt\" instead.");
        }
        return Collector.useShortable(
                Optional::empty,
                (accumulator, element, index) -> accumulator.isPresent(),
                (accumulator, element, index) -> {
                    if(index == target){
                        return Optional.of(element);
                    }
                    return accumulator;
                },
                (a, b) -> {
                    if(a.isPresent()){
                        return a;
                    }
                    return b;
                },
                (accumulator) -> accumulator
        );
    }

    public static <E> Collector<E, List<E>, Optional<E>> useFindNegativeAt(long target) {
        if(target > -1){
            throw new IllegalArgumentException("Use function \"useFindAt\" instead.");
        }
        return Collector.useFull(
                ArrayList::new,
                (accumulator, element, index) -> {
                    accumulator.add(element);
                    return accumulator;
                },
                (a, b) -> {
                    a.addAll(b);
                    return a;
                },
                (accumulator) -> {
                    if(accumulator.isEmpty()){
                        return Optional.empty();
                    }
                    int index = ((int)(target % accumulator.size()) + accumulator.size()) % accumulator.size();
                    return Optional.of(accumulator.get(index));
                }
        );
    }

    @SuppressWarnings("unchecked")
    public static <E> Collector<E, Optional<E>, Optional<E>> useFindMaximum() {
        return useFindMaximum((e1, e2) -> ((Comparable<E>) e1).compareTo(e2));
    }

    public static <E> Collector<E, Optional<E>, Optional<E>> useFindMaximum(Comparator<E> comparator) {
        Objects.requireNonNull(comparator, "Comparator could not be null.");
        return Collector.useShortable(
                Optional::empty,
                (accumulator, element, index) -> false,
                (accumulator, element, index) -> {
                    if (accumulator.isEmpty() || comparator.compare(element, accumulator.get()) > 0) {
                        return Optional.of(element);
                    }
                    return accumulator;
                },
                (a, b) -> {
                    if (a.isEmpty()) return b;
                    if (b.isEmpty()) return a;
                    return comparator.compare(a.get(), b.get()) > 0 ? a : b;
                },
                accumulator -> accumulator
        );
    }

    @SuppressWarnings("unchecked")
    public static <E> Collector<E, Optional<E>, Optional<E>> useFindMinimum() {
        return useFindMinimum((e1, e2) -> ((Comparable<E>) e1).compareTo(e2));
    }

    public static <E> Collector<E, Optional<E>, Optional<E>> useFindMinimum(Comparator<E> comparator) {
        Objects.requireNonNull(comparator, "Comparator could not be null.");
        return Collector.useShortable(
                Optional::empty,
                (accumulator, element, index) -> false,
                (accumulator, element, index) -> {
                    if (accumulator.isEmpty() || comparator.compare(element, accumulator.get()) < 0) {
                        return Optional.of(element);
                    }
                    return accumulator;
                },
                (a, b) -> {
                    if (a.isEmpty()) return b;
                    if (b.isEmpty()) return a;
                    return comparator.compare(a.get(), b.get()) < 0 ? a : b;
                },
                accumulator -> accumulator
        );
    }

    public static <E, K> Collector<E, Map<K, List<E>>, Map<K, List<E>>> useGroup(final Function<E, K> keyExtractor) {
        Objects.requireNonNull(keyExtractor, "Key extractor could not be null.");
        return Collector.useFull(
                HashMap::new,
                (map, element, index) -> {
                    K key = keyExtractor.apply(element);
                    map.computeIfAbsent(key, k -> new ArrayList<>()).add(element);
                    return map;
                },
                (map1, map2) -> {
                    map2.forEach((key, list) -> map1.computeIfAbsent(key, k -> new ArrayList<>()).addAll(list));
                    return map1;
                },
                map -> map
        );
    }

    public static <E, K> Collector<E, Map<K, List<E>>, Map<K, List<E>>> useGroup(final BiFunction<E, Long, K> keyExtractor) {
        Objects.requireNonNull(keyExtractor, "Key extractor could not be null.");
        return Collector.useFull(
                HashMap::new,
                (map, element, index) -> {
                    K key = keyExtractor.apply(element, index);
                    map.computeIfAbsent(key, k -> new ArrayList<>()).add(element);
                    return map;
                },
                (map1, map2) -> {
                    map2.forEach((key, list) -> map1.computeIfAbsent(key, k -> new ArrayList<>()).addAll(list));
                    return map1;
                },
                map -> map
        );
    }

    public static <E, K, V> Collector<E, Map<K, List<V>>, Map<K, List<V>>> useGroupBy(final Function<E, K> keyExtractor, final Function<E, V> valueExtractor) {
        Objects.requireNonNull(keyExtractor, "Key extractor could not be null.");
        Objects.requireNonNull(valueExtractor, "Value extractor could not be null.");
        return Collector.useFull(
                HashMap::new,
                (map, element, index) -> {
                    K key = keyExtractor.apply(element);
                    V value = valueExtractor.apply(element);
                    map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
                    return map;
                },
                (map1, map2) -> {
                    map2.forEach((key, list) -> map1.computeIfAbsent(key, k -> new ArrayList<>()).addAll(list));
                    return map1;
                },
                map -> map
        );
    }

    public static <E, K, V> Collector<E, Map<K, List<V>>, Map<K, List<V>>> useGroupBy(final BiFunction<E, Long, K> keyExtractor, final BiFunction<E, Long, V> valueExtractor) {
        Objects.requireNonNull(keyExtractor, "Key extractor could not be null.");
        Objects.requireNonNull(valueExtractor, "Value extractor could not be null.");
        return Collector.useFull(
                HashMap::new,
                (map, element, index) -> {
                    K key = keyExtractor.apply(element, index);
                    V value = valueExtractor.apply(element, index);
                    map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
                    return map;
                },
                (map1, map2) -> {
                    map2.forEach((key, list) -> map1.computeIfAbsent(key, k -> new ArrayList<>()).addAll(list));
                    return map1;
                },
                map -> map
        );
    }

    public static <E> Collector<E, StringBuilder, String> useJoin() {
        return Collector.useFull(
                StringBuilder::new,
                (builder, element, index) -> {
                    if (!builder.isEmpty()) {
                        builder.append(", ");
                    }
                    builder.append(element);
                    return builder;
                },
                (builder1, builder2) -> {
                    if (!builder2.isEmpty()) {
                        if (!builder1.isEmpty()) {
                            builder1.append(", ");
                        }
                        builder1.append(builder2);
                    }
                    return builder1;
                },
                (a) -> {
                    a.insert(0, '[');
                    a.append(']');
                    return a.toString();
                }
        );
    }

    public static <E> Collector<E, StringBuilder, String> useJoin(final String delimiter) {
        Objects.requireNonNull(delimiter, "Delimiter could not be null.");
        return Collector.useFull(
                StringBuilder::new,
                (builder, element, index) -> {
                    if (!builder.isEmpty()) {
                        builder.append(delimiter);
                    }
                    builder.append(element);
                    return builder;
                },
                (builder1, builder2) -> {
                    if (!builder2.isEmpty()) {
                        if (!builder1.isEmpty()) {
                            builder1.append(delimiter);
                        }
                        builder1.append(builder2);
                    }
                    return builder1;
                },
                (a) -> {
                    a.insert(0, '[');
                    a.append(']');
                    return a.toString();
                }
        );
    }

    public static <E> Collector<E, StringBuilder, String> useJoin(final String prefix, final String delimiter, final String suffix) {
        Objects.requireNonNull(prefix, "Prefix could not be null.");
        Objects.requireNonNull(delimiter, "Delimiter could not be null.");
        Objects.requireNonNull(suffix, "Suffix could not be null.");
        return Collector.useFull(
                StringBuilder::new,
                (builder, element, index) -> {
                    if (!builder.isEmpty()) {
                        builder.append(delimiter);
                    }
                    builder.append(element);
                    return builder;
                },
                (builder1, builder2) -> {
                    if (!builder2.isEmpty()) {
                        if (!builder1.isEmpty()) {
                            builder1.append(delimiter);
                        }
                        builder1.append(builder2);
                    }
                    return builder1;
                },
                builder -> prefix + builder.toString() + suffix
        );
    }

    public static <E> Collector<E, StringBuilder, String> useJoin(final String prefix, final BiFunction<E, Long, String> serializer, final String suffix) {
        Objects.requireNonNull(prefix, "Prefix could not be null.");
        Objects.requireNonNull(serializer, "Serializer could not be null.");
        Objects.requireNonNull(suffix, "Suffix could not be null.");
        return Collector.useFull(
                StringBuilder::new,
                (builder, element, index) -> {
                    if (!builder.isEmpty()) {
                        builder.append(", ");
                    }
                    builder.append(serializer.apply(element, index));
                    return builder;
                },
                (builder1, builder2) -> {
                    if (!builder2.isEmpty()) {
                        if (!builder1.isEmpty()) {
                            builder1.append(", ");
                        }
                        builder1.append(builder2);
                    }
                    return builder1;
                },
                builder -> prefix + builder.toString() + suffix
        );
    }

    public static <E> Collector<E, StringBuilder, String> useJoin(final String prefix, final IndexedAccumulator<String, E> serializer, final String suffix) {
        Objects.requireNonNull(prefix, "Prefix could not be null.");
        Objects.requireNonNull(serializer, "Serializer could not be null.");
        Objects.requireNonNull(suffix, "Suffix could not be null.");
        return Collector.useFull(
                StringBuilder::new,
                (builder, element, index) -> {
                    if (!builder.isEmpty()) {
                        builder.append(", ");
                    }
                    builder.append(serializer.apply("", element, index));
                    return builder;
                },
                (builder1, builder2) -> {
                    if (!builder2.isEmpty()) {
                        if (!builder1.isEmpty()) {
                            builder1.append(", ");
                        }
                        builder1.append(builder2);
                    }
                    return builder1;
                },
                builder -> prefix + builder.toString() + suffix
        );
    }

    public static <E> Collector<E, Map<Long, List<E>>, List<List<E>>> usePartition(final long count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Partition count must be positive.");
        }
        return Collector.useFull(
                () -> {
                    Map<Long, List<E>> map = new HashMap<>();
                    for (long i = 0; i < count; i++) {
                        map.put(i, new ArrayList<>());
                    }
                    return map;
                },
                (map, element, index) -> {
                    long partitionKey = index % count;
                    map.get(partitionKey).add(element);
                    return map;
                },
                (map1, map2) -> {
                    for (long i = 0; i < count; i++) {
                        map1.get(i).addAll(map2.get(i));
                    }
                    return map1;
                },
                map -> {
                    List<List<E>> result = new ArrayList<>();
                    for (long i = 0; i < count; i++) {
                        result.add(map.get(i));
                    }
                    return result;
                }
        );
    }

    public static <E> Collector<E, Map<Long, List<E>>, List<List<E>>> usePartitionBy(final Function<E, Long> keyExtractor) {
        Objects.requireNonNull(keyExtractor, "Key extractor could not be null.");
        return Collector.useFull(
                HashMap::new,
                (map, element, index) -> {
                    long partitionKey = keyExtractor.apply(element);
                    map.computeIfAbsent(partitionKey, k -> new ArrayList<>()).add(element);
                    return map;
                },
                (map1, map2) -> {
                    map2.forEach((key, list) -> map1.computeIfAbsent(key, k -> new ArrayList<>()).addAll(list));
                    return map1;
                },
                map -> {
                    List<Long> sortedKeys = new ArrayList<>(map.keySet());
                    Collections.sort(sortedKeys);
                    List<List<E>> result = new ArrayList<>();
                    for (Long key : sortedKeys) {
                        result.add(map.get(key));
                    }
                    return result;
                }
        );
    }

    public static <E> Collector<E, Map<Long, List<E>>, List<List<E>>> usePartitionBy(final BiFunction<E, Long, Long> keyExtractor) {
        Objects.requireNonNull(keyExtractor, "Key extractor could not be null.");
        return Collector.useFull(
                HashMap::new,
                (map, element, index) -> {
                    long partitionKey = keyExtractor.apply(element, index);
                    map.computeIfAbsent(partitionKey, k -> new ArrayList<>()).add(element);
                    return map;
                },
                (map1, map2) -> {
                    map2.forEach((key, list) -> map1.computeIfAbsent(key, k -> new ArrayList<>()).addAll(list));
                    return map1;
                },
                map -> {
                    List<Long> sortedKeys = new ArrayList<>(map.keySet());
                    Collections.sort(sortedKeys);
                    List<List<E>> result = new ArrayList<>();
                    for (Long key : sortedKeys) {
                        result.add(map.get(key));
                    }
                    return result;
                }
        );
    }

    public static <E> Collector<E, Optional<E>, Optional<E>> useReduce(final BiFunction<E, E, E> operator){
        Objects.requireNonNull(operator, "Operator could not be null.");
        return Collector.useFull(
                Optional::empty,
                (accumulator, element, index) -> accumulator.map((value) -> operator.apply(value, element)),
                (a, b)->{
                    if(a.isPresent()){
                        return a;
                    }
                    return b;
                },
                (accumulator) -> accumulator
        );
    }

    public static <E> Collector<E, E, E> useReduce(E identity, final BiFunction<E, E, E> operator){
        Objects.requireNonNull(operator, "Operator could not be null.");
        return Collector.useFull(
                () -> identity,
                (accumulator, element, index) -> operator.apply(accumulator, element),
                operator,
                (accumulator) -> accumulator
        );
    }

    public static <E, R> Collector<E, R, R> useReduce(R identity, final BiFunction<R, E, R> operator, final BiFunction<R, R, R> combiner){
        Objects.requireNonNull(identity, "identity could not be null.");
        Objects.requireNonNull(operator, "Operator could not be null.");
        Objects.requireNonNull(combiner, "Combiner could not be null.");
        return Collector.useFull(
                () -> identity,
                (accumulator, element, index) -> operator.apply(accumulator, element),
                combiner,
                (a) -> a
        );
    }

    public static <E> Collector<E, Long, Long> useCount(){
        return Collector.useFull(
                ()-> 0L,
                (accumulator, element, index) -> index + 1L,
                Long::sum,
                (a) -> a
        );
    }

    public static <E> Collector<E, Long, Long> useForEach(final Consumer<E> consumer){
        return Collector.useFull(
                ()-> 0L,
                (accumulator, element, index) -> {
                    consumer.accept(element);
                    return accumulator + 1L;
                },
                Long::sum,
                (a) -> a
        );
    }

    public static <E> Collector<E, Long, Long> useForEach(final BiConsumer<E, Long> consumer){
        return Collector.useFull(
                ()-> 0L,
                (accumulator, element, index) -> {
                    consumer.accept(element, index);
                    return accumulator + 1L;
                },
                Long::sum,
                (a) -> a
        );
    }

    public static <E> Collector<E, List<E>, List<E>> toList(){
        return Collector.useFull(
                ArrayList::new,
                (accumulator, element, index) -> {
                    accumulator.add(element);
                    return accumulator;
                },
                (a, b) -> {
                    a.addAll(b);
                    return a;
                },
                (a) -> a
        );
    }

    public static <E> Collector<E, Set<E>, Set<E>> toHashSet(){
        return Collector.useFull(
                HashSet::new,
                (accumulator, element, index) -> {
                    accumulator.add(element);
                    return accumulator;
                },
                (a, b) -> {
                    a.addAll(b);
                    return a;
                },
                (a) -> a
        );
    }

    public static <E> Collector<E, Set<E>, Set<E>> useToTreeSet(){
        return Collector.useFull(
                TreeSet::new,
                (accumulator, element, index) -> {
                    accumulator.add(element);
                    return accumulator;
                },
                (a, b) -> {
                    a.addAll(b);
                    return a;
                },
                (a) -> a
        );
    }

    public static <E, K> Collector<E, Map<K, E>, Map<K, E>> useToHashMap(final Function<E, K> keyExtractor){
        Objects.requireNonNull(keyExtractor, "Key extractor could not be null.");
        return Collector.useFull(
                HashMap::new,
                (map, element, index) -> {
                    K key = keyExtractor.apply(element);
                    map.put(key, element);
                    return map;
                },
                (map1, map2) -> {
                    map1.putAll(map2);
                    return map1;
                },
                map -> map
        );
    }

    public static <E, K, V> Collector<E, Map<K, V>, Map<K, V>> useToHashMap(final Function<E, K> keyExtractor, final Function<E, V> valueExtractor){
        Objects.requireNonNull(keyExtractor, "Key extractor could not be null.");
        Objects.requireNonNull(valueExtractor, "Value extractor could not be null.");
        return Collector.useFull(
                HashMap::new,
                (map, element, index) -> {
                    K key = keyExtractor.apply(element);
                    V value = valueExtractor.apply(element);
                    map.put(key, value);
                    return map;
                },
                (map1, map2) -> {
                    map1.putAll(map2);
                    return map1;
                },
                map -> map
        );
    }

    public static <E, K> Collector<E, Map<K, E>, Map<K, E>> useToTreeMap(final Function<E, K> keyExtractor){
        Objects.requireNonNull(keyExtractor, "Key extractor could not be null.");
        return Collector.useFull(
                TreeMap::new,
                (map, element, index) -> {
                    K key = keyExtractor.apply(element);
                    map.put(key, element);
                    return map;
                },
                (map1, map2) -> {
                    map1.putAll(map2);
                    return map1;
                },
                map -> map
        );
    }

    public static <E, K, V> Collector<E, Map<K, V>, Map<K, V>> useToTreeMap(final Function<E, K> keyExtractor, final Function<E, V> valueExtractor){
        Objects.requireNonNull(keyExtractor, "Key extractor could not be null.");
        Objects.requireNonNull(valueExtractor, "Value extractor could not be null.");
        return Collector.useFull(
                TreeMap::new,
                (map, element, index) -> {
                    K key = keyExtractor.apply(element);
                    V value = valueExtractor.apply(element);
                    map.put(key, value);
                    return map;
                },
                (map1, map2) -> {
                    map1.putAll(map2);
                    return map1;
                },
                map -> map
        );
    }

    public static <E> Collector<E, Map<E, Long>, Map<E, Long>> useFrequency(){
        return Collector.useFull(
                TreeMap::new,
                (accumulator, element, index) -> {
                    accumulator.compute(element, (key, value) -> {
                        if(Objects.nonNull(value)){
                            return value + 1;
                        }
                        return 0L;
                    });
                    return accumulator;
                },
                (a, b) -> {
                    b.forEach((k, v) -> {
                        a.compute(k, (key, value) -> {
                            if(Objects.nonNull(value)){
                                return value + 1;
                            }
                            return 0L;
                        });
                    });
                    return a;
                },
                (a) -> a
        );
    }

    public static <E, D> Collector<E, Map<D, Long>, Map<D, Long>> useFrequency(final Function<E, D> mapper){
        Objects.requireNonNull(mapper, "Mapper could not be null.");
        return Collector.useFull(
                TreeMap::new,
                (accumulator, element, index) -> {
                    accumulator.compute(mapper.apply(element), (key, value) -> {
                        if(Objects.nonNull(value)){
                            return value + 1;
                        }
                        return 0L;
                    });
                    return accumulator;
                },
                (a, b) -> {
                    b.forEach((k, v) -> {
                        a.compute(k, (key, value) -> {
                            if(Objects.nonNull(value)){
                                return value + 1;
                            }
                            return 0L;
                        });
                    });
                    return a;
                },
                (a) -> a
        );
    }

    public static <E> Collector<E, TreeMap<E, Long>, Optional<E>> useMode(){
        return Collector.useFull(
                TreeMap::new,
                (accumulator, element, index) -> {
                    accumulator.compute(element, (key, value) -> {
                        if(Objects.nonNull(value)){
                            return value + 1;
                        }
                        return 0L;
                    });
                    return accumulator;
                },
                (a, b) -> {
                    b.forEach((k, v) -> {
                        a.compute(k, (key, value) -> {
                            if(Objects.nonNull(value)){
                                return value + 1;
                            }
                            return 0L;
                        });
                    });
                    return a;
                },
                (a) -> {
                    if(a.isEmpty()){
                        return Optional.empty();
                    }
                    return Optional.of(((TreeMap<E, Long>)a).lastKey());
                }
        );
    }

    public static <E, D> Collector<E, TreeMap<D, Long>, Optional<D>> useMode(final Function<E, D> mapper){
        Objects.requireNonNull(mapper, "Mapper could not be null.");
        return Collector.useFull(
                TreeMap::new,
                (accumulator, element, index) -> {
                    accumulator.compute(mapper.apply(element), (key, value) -> {
                        if(Objects.nonNull(value)){
                            return value + 1;
                        }
                        return 0L;
                    });
                    return accumulator;
                },
                (a, b) -> {
                    b.forEach((k, v) -> {
                        a.compute(k, (key, value) -> {
                            if(Objects.nonNull(value)){
                                return value + 1;
                            }
                            return 0L;
                        });
                    });
                    return a;
                },
                (a) -> {
                    if(a.isEmpty()){
                        return Optional.empty();
                    }
                    return Optional.of(a.lastKey());
                }
        );
    }
}
