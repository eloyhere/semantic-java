package pers.eloyhere.semantic;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.function.*;
import java.util.concurrent.atomic.*;

public class Collectable<E> {

    protected final Generator<E> generator;

    protected final Long concurrent;

    public Collectable(final Generator<E> generator){
        this.generator = generator;
        this.concurrent = 1L;
    }

    public Collectable(final Generator<E> generator, final long concurrent){
        this.generator = generator;
        this.concurrent = concurrent;
    }

    public boolean anyMatch(final Predicate<E> predicate){
        AtomicBoolean found = new AtomicBoolean(false);
        return this.collect(
                ()-> false,
                (element)-> found.get(),
                (result, element, index)-> {
                    if(predicate.test(element)){
                        found.set(true);
                        return true;
                    }
                    return false;
                },
                (r1, r2)-> {
                    return r1 || r2;
                },
                (r1)->{
                    return r1;
                }
        );
    }

    public boolean allMatch(final Predicate<E> predicate){
        if(Objects.isNull(predicate)){
            throw new NullPointerException("Predicate is null.");
        }
        AtomicBoolean found = new AtomicBoolean(true);
        return this.collect(
                ()-> true,
                (element)-> !found.get(),
                (result, element, index)-> {
                    if(!predicate.test(element)){
                        found.set(false);
                        return true;
                    }
                    return false;
                },
                (r1, r2)-> {
                    return r1 && r2;
                },
                (r1)->{
                    return r1;
                }
        );
    }

    public <R> R collect(final Collector<R, E, R> collector){
        if(Objects.isNull(collector)){
            throw new NullPointerException("Collector is null.");
        }
        return collector.collect(this.generator, this.concurrent);
    }

    public <A, R> R collect(final Supplier<A> identity, final TriFunction<A, E, Long, A> accumulator, final BiFunction<A, A, A> combiner, final Function<A, R> finisher){
        if(Objects.isNull(identity) || Objects.isNull(accumulator) || Objects.isNull(combiner) || Objects.isNull(finisher)){
            throw new NullPointerException("Parameters are null.");
        }
        Collector<A, E, R> collector = Collector.full(identity, accumulator, combiner, finisher);
        return collector.collect(this.generator, this.concurrent);
    }

    public <A, R> R collect(final Supplier<A> identity, final Predicate<E> interruptor, final TriFunction<A, E, Long, A> accumulator, final BiFunction<A, A, A> combiner, final Function<A, R> finisher){
        if(Objects.isNull(identity) || Objects.isNull(interruptor) || Objects.isNull(accumulator) || Objects.isNull(combiner) || Objects.isNull(finisher)){
            throw new NullPointerException("Parameters are null.");
        }
        Collector<A, E, R> collector = Collector.shortable(identity, interruptor, accumulator, combiner, finisher);
        return collector.collect(this.generator, this.concurrent);
    }

    long count(){
        return this.collect(
                ()-> 0L,
                (count, element, index)-> count + 1,
                Long::sum,
                (count)-> count
        );
    }

    @SuppressWarnings("unchecked")
    Optional<E> findFirst(){
        AtomicBoolean found = new AtomicBoolean(false);
        return this.collect(
                ()-> (Optional<E>) Optional.empty(),
                (element)-> {
                    if(found.get()){
                        return false;
                    }
                    found.set(true);
                    return true;
                },
                (result, element, index)-> {
                    if(found.get()){
                        return Optional.of(element);
                    }
                    return Optional.empty();
                },
                (r1, r2)-> {
                    if(r1.isPresent()){
                        return r1;
                    }
                    return r2;
                },
                (r1)->{
                    return r1;
                }
        );
    }

    @SuppressWarnings("unchecked")
    Optional<E> findAny(){
        AtomicBoolean found = new AtomicBoolean(false);
        Random random = new Random();
        return this.collect(
                ()-> (Optional<E>) Optional.empty(),
                (element)-> {
                    if(found.get()){
                        return false;
                    }
                    found.set(true);
                    return true;
                },
                (result, element, index)-> {
                    if(found.get() && random.nextBoolean()){
                        return Optional.of(element);
                    }
                    return Optional.empty();
                },
                (r1, r2)-> {
                    if(r1.isPresent()){
                        return r1;
                    }
                    return r2;
                },
                (r1)->{
                    return r1;
                }
        );
    }

    void forEach(final BiConsumer<E, Long> consumer){
        if(Objects.isNull(consumer)){
            throw new NullPointerException("Consumer is null.");
        }
        this.collect(
                ()-> 0L,
                (result, element, index)-> {
                    consumer.accept(element, index);
                    return result+1L;
                },
                Long::sum,
                (count)-> count
        );
    }

    public <K> HashMap<K, ArrayList<E>> group(final Function<E, K> keyExtractor){
        if(Objects.isNull(keyExtractor)){
            throw new NullPointerException("KeyExtractor is null.");
        }
        return this.collect(
                ()-> new HashMap<K, ArrayList<E>>(),
                (map, element, index)-> {
                    K key = keyExtractor.apply(element);
                    ArrayList<E> list = (ArrayList<E>) map.getOrDefault(key, new ArrayList<>());
                    list.add(element);
                    map.put(key, list);
                    return map;
                },
                (map1, map2)-> {
                    map1.putAll(map2);
                    return map1;
                },
                (map)->{
                    return map;
                });
    }

    public <K, V> HashMap<K, ArrayList<V>>  groupBy(final Function<E, K> keyExtractor, final Function<E, V> valueExtractor){
        if(Objects.isNull(keyExtractor) || Objects.isNull(valueExtractor)){
            throw new NullPointerException("KeyExtractor or ValueExtractor is null.");
        }
        return this.collect(
                ()-> new HashMap<K, ArrayList<V>>(),
                (map, element, index)-> {
                    K key = keyExtractor.apply(element);
                    ArrayList<V> list = (ArrayList<V>) map.getOrDefault(key, new ArrayList<>());
                    list.add(valueExtractor.apply(element));
                    map.put(key, list);
                    return map;
                },
                (map1, map2)-> {
                    map1.putAll(map2);
                    return map1;
                },
                (map)->{
                    return map;
                });
    }

    public String join(){
        return this.join(",");
    }

    public String join(final String delimiter){
        return this.join("[", delimiter, "]");
    }

    public String join(final BiFunction<E, StringBuilder, StringBuilder> accumulator){
        return this.collect(
                StringBuilder::new,
                (builder, element, index)-> {
                    if(index > 0){
                        builder.append(",");
                    }
                    accumulator.apply(element, builder);
                    return builder;
                },
                (builder1, builder2)-> {
                    builder1.append(builder2);
                    return builder1;
                },
                StringBuilder::toString);
    }

    public String join(final String prefix, final String delimiter, final String suffix){
        return this.collect(
                StringBuilder::new,
                (builder, element, index)-> {
                    if(index > 0){
                        builder.append(delimiter);
                    }
                    builder.append(element);
                    return builder;
                },
                (builder1, builder2)-> {
                    builder1.append(builder2);
                    return builder1;
                },
                StringBuilder::toString);
    }

    boolean noneMatch(final Predicate<E> predicate) {
        if (Objects.isNull(predicate)) {
            throw new NullPointerException("Predicate is null.");
        }
        AtomicBoolean found = new AtomicBoolean(false);
        return this.collect(
                () -> true,
                (element) -> found.get(),
                (result, element, index) -> {
                    if (predicate.test(element)) {
                        found.set(true);
                        return true;
                    }
                    return false;
                },
                (r1, r2) -> {
                    return r1 && r2;
                },
                (r1) -> {
                    return !r1;
                }
        );
    }

    public ArrayList<ArrayList<E>> partition(final long count){
        if(count <= 0){
            throw new IllegalArgumentException("Count must be greater than 0.");
        }
        return this.collect(
                ()-> new ArrayList<ArrayList<E>>(),
                (list, element, index)-> {
                    int indexInList = (int) (index % count);
                    while(list.size() <= indexInList){
                        list.add(new ArrayList<>());
                    }
                    list.get(indexInList).add(element);
                    return list;
                },
                (list1, list2)-> {
                    for(int i=0; i<list2.size(); i++){
                        if(list1.size() <= i){
                            list1.add(list2.get(i));
                        }else{
                            list1.get(i).addAll(list2.get(i));
                        }
                    }
                    return list1;
                },
                (list)->{
                    return list;
                });
    }

    public ArrayList<ArrayList<E>> partitionBy(final Function<E, Long> classifier){
        if(Objects.isNull(classifier)){
            throw new NullPointerException("Classifier is null.");
        }
        return this.collect(
                ()-> new ArrayList<ArrayList<E>>(),
                (list, element, index)-> {
                    int indexInList = (int) (classifier.apply(element) % list.size());
                    while(list.size() <= indexInList){
                        list.add(new ArrayList<>());
                    }
                    list.get(indexInList).add(element);
                    return list;
                },
                (list1, list2)-> {
                    for(int i=0; i<list2.size(); i++){
                        if(list1.size() <= i){
                            list1.add(list2.get(i));
                        }else{
                            list1.get(i).addAll(list2.get(i));
                        }
                    }
                    return list1;
                },
                (list)->{
                    return list;
                });
    }

    public void print(){
        System.out.println(this.join());
    }

    public void print(final BiFunction<E, OutputStream, OutputStream> accumulator){
        ByteArrayOutputStream stream = this.collect(
                ByteArrayOutputStream::new,
                (result, element, index)-> {
                    return (ByteArrayOutputStream) accumulator.apply(element, result);
                },
                (stream1, stream2)-> {
                    try {
                        stream1.write(stream2.toByteArray());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return stream1;
                },
                (result)-> result);
        System.out.print(stream.toString());
    }

    public void print(final OutputStream stream){
        String text = this.join();
        try {
            stream.write(text.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void print(final OutputStream stream, final BiFunction<E, OutputStream, OutputStream> accumulator){
        if(Objects.isNull(stream) || Objects.isNull(accumulator)){
            throw new NullPointerException("Parameters are null.");
        }
        ByteArrayOutputStream result = this.collect(
                ByteArrayOutputStream::new,
                (resultStream, element, index)-> {
                    return (ByteArrayOutputStream) accumulator.apply(element, resultStream);
                },
                (stream1, stream2)-> {
                    try {
                        stream1.write(stream2.toByteArray());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return stream1;
                },
                (resultStream)-> resultStream);
        try {
            stream.write(result.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void print(final OutputStream stream, final String delimiter){
        if(Objects.isNull(stream) || Objects.isNull(delimiter)){
            throw new NullPointerException("Parameters are null.");
        }
        try {
            stream.write(this.join(delimiter).getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void print(final OutputStream stream, final String prefix, final String delimiter, final String suffix) {
        if(Objects.isNull(stream) || Objects.isNull(prefix) || Objects.isNull(delimiter) || Objects.isNull(suffix)){
            throw new NullPointerException("Parameters are null.");
        }
        try {
            stream.write(prefix.getBytes());
            stream.write(this.join(delimiter).getBytes());
            stream.write(suffix.getBytes());
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public Optional<E> reduce(final BinaryOperator<E> operator){
        if(Objects.isNull(operator)){
            throw new NullPointerException("Operator is null.");
        }
        return this.collect(
                ()-> (Optional<E>) Optional.empty(),
                (result, element, index)-> {
                    if(result.isPresent()){
                        return Optional.of(operator.apply(result.get(), element));
                    }
                    return Optional.of(element);
                },
                (r1, r2)-> {
                    if(r1.isPresent() && r2.isPresent()){
                        return Optional.of(operator.apply(r1.get(), r2.get()));
                    }
                    if(r1.isPresent()){
                        return r1;
                    }
                    return r2;
                },
                (r1)->{
                    return r1;
                }
        );
    }

    public E reduce(final E identity, final BinaryOperator<E> accumulator){
        if(Objects.isNull(accumulator)){
            throw new NullPointerException("Accumulator is null.");
        }
        return this.collect(
                ()-> identity,
                (result, element, index)-> accumulator.apply(result, element),
                accumulator,
                (r1)-> r1
        );
    }

    public <R> R reduce(final R identity, final BiFunction<R, E, R> accumulator, final BinaryOperator<R> combiner) {
        if (Objects.isNull(accumulator) || Objects.isNull(combiner)) {
            throw new NullPointerException("Accumulator or Combiner is null.");
        }
        return this.collect(
                () -> identity,
                (result, element, index) -> accumulator.apply(result, element),
                combiner,
                (r1) -> r1
        );
    }

    public Semantic<E> semantic(){
        return new Semantic<>(this.generator, this.concurrent);
    }

    public List<E> toList(){
        return this.collect(
                ()-> new ArrayList<E>(),
                (list, element, index)-> {
                    list.add(element);
                    return list;
                },
                (list1, list2)-> {
                    list1.addAll(list2);
                    return list1;
                },
                (list)->{
                    return list;
                });
    }

    public <K, V> HashMap<K, V> toMap(final Function<E, K> keyExtractor, final Function<E, V> valueExtractor){
        if(Objects.isNull(keyExtractor) || Objects.isNull(valueExtractor)){
            throw new NullPointerException("KeyExtractor or ValueExtractor is null.");
        }
        return this.collect(
                ()-> new HashMap<K, V>(),
                (map, element, index)-> {
                    map.put(keyExtractor.apply(element), valueExtractor.apply(element));
                    return map;
                },
                (map1, map2)-> {
                    map1.putAll(map2);
                    return map1;
                },
                (map)->{
                    return map;
                });
    }

    public Set<E> toOrderedSet(final Comparator<E> comparator){
        return this.collect(
                ()-> new TreeSet<E>(comparator),
                (set, element, index)-> {
                    set.add(element);
                    return set;
                },
                (set1, set2)-> {
                    set1.addAll(set2);
                    return set1;
                },
                (set)->{
                    return set;
                });
    }

    public Set<E> toUnorderedSet(){
        return this.collect(
                ()-> new HashSet<E>(),
                (set, element, index)-> {
                    set.add(element);
                    return set;
                },
                (set1, set2)-> {
                    set1.addAll(set2);
                    return set1;
                },
                (set)->{
                    return set;
                });
    }

    public Vector<E> toVector(){
        return this.collect(
                ()-> new Vector<E>(),
                (vector, element, index)-> {
                    vector.add(element);
                    return vector;
                },
                (vector1, vector2)-> {
                    vector1.addAll(vector2);
                    return vector1;
                },
                (vector)->{
                    return vector;
                });
    }

    public String toString(){
        return this.join();
    }
}
