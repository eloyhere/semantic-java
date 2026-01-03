package pers.eloyhere.semantic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class OrderedCollectable<E> extends Collectable<E> {

    protected final ArrayList<Pair<Long, E>> source = new ArrayList<>();

    @SuppressWarnings("unchecked")
    protected static <E> Comparator<E> defaultComparator(){
        return (a, b)->{
            if(a instanceof Comparable && b instanceof Comparable){
                Comparable<Object> c = (Comparable<Object>) a;
                Comparable<Object> d = (Comparable<Object>) b;
                return c.compareTo(d);
            }
            return Objects.hashCode(a) - Objects.hashCode(b);
        };
    }

    public OrderedCollectable(Generator<E> generator) {
        super(generator);
        generator.accept((element, index)-> {
            this.source.add(new Pair<>(index, element));
        }, (element)-> false);
        this.source.sort((a, b)-> defaultComparator().compare(a.a(), b.a()));
    }

    public OrderedCollectable(Generator<E> generator, Comparator<E> comparator) {
        super(generator);
        generator.accept((element, index)-> {
            this.source.add(new Pair<>(index, element));
        }, (element)-> false);
        this.source.sort((a, b)-> comparator.compare(a.b(), b.b()));
    }

    public OrderedCollectable(Generator<E> generator, long concurrent) {
        super(generator, concurrent);
        generator.accept((element, index)-> {
            this.source.add(new Pair<>(index, element));
        }, (element)-> false);
        this.source.sort((a, b)-> defaultComparator().compare(a.a(), b.a()));
    }

    public OrderedCollectable(Generator<E> generator, long concurrent, Comparator<E> comparator) {
        super(generator, concurrent);
        generator.accept((element, index)-> {
            this.source.add(new Pair<>(index, element));
        }, (element)-> false);
        this.source.sort((a, b)-> comparator.compare(a.b(), b.b()));
    }

    @Override
    public <R> R collect(Collector<R, E, R> collector) {
        if(Objects.isNull(collector)){
            throw new NullPointerException("Collector can not be null.");
        }
        return collector.collect(this.source.stream().map(Pair::b).toList(), this.concurrent);
    }

    @Override
    public <A, R> R collect(Supplier<A> identity, TriFunction<A, E, Long, A> accumulator, BiFunction<A, A, A> combiner, Function<A, R> finisher) {
        if(Objects.isNull(accumulator) || Objects.isNull(combiner) || Objects.isNull(finisher)){
            throw new NullPointerException("Accumulator, combiner and finisher can not be null.");
        }
        Collector<A, E, R> collector = Collector.full(identity, accumulator, combiner, finisher);
        return collector.collect(this.source.stream().map(Pair::b).toList(), this.concurrent);
    }

    @Override
    public <A, R> R collect(Supplier<A> identity, Predicate<E> interruptor, TriFunction<A, E, Long, A> accumulator, BiFunction<A, A, A> combiner, Function<A, R> finisher) {
        if(Objects.isNull(accumulator) || Objects.isNull(combiner) || Objects.isNull(finisher)){
            throw new NullPointerException("Accumulator, combiner and finisher can not be null.");
        }
        Collector<A, E, R> collector = Collector.shortable(identity, interruptor, accumulator, combiner, finisher);
        return collector.collect(this.source.stream().map(Pair::b).toList(), this.concurrent);
    }
}
