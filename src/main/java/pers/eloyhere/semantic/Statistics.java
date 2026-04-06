package pers.eloyhere.semantic;

import java.util.*;
import java.util.function.Function;

public abstract class Statistics<E, D extends Number> extends OrderedCollectable<E>{

    protected Statistics(Generator<E> generator) {
        super(generator);
    }

    protected Statistics(Generator<E> generator, long concurrent) {
        super(generator, concurrent);
    }

    protected Statistics(Generator<E> generator, Comparator<E> comparator) {
        super(generator, comparator);
    }

    protected Statistics(Generator<E> generator, Comparator<E> comparator, long concurrent) {
        super(generator, comparator, concurrent);
    }

    public abstract D absolute(D a);
    public abstract D squareRoot(D a);
    public abstract int compare(D a, D b);
    public abstract D zero();
    public abstract D one();
    public abstract D plus(D a, D b);
    public abstract D subtract(D a, D b);
    public abstract D multiply(D a, D b);
    public abstract D divide(D a, D b);
    public abstract D divide(D a, long b);
    public abstract D power(D a, D b);

    @SuppressWarnings("unchecked")
    public D average(){
        return this.average((element) -> (D) element);
    }

    public D average(final Function<E, D> mapper){
        Collector<E, D, D> collector = Collector.useFull(
                this::zero,
                (accumulator, element, index) -> this.plus(accumulator, mapper.apply(element)),
                this::plus,
                (a) -> this.divide(a, this.count())
        );
        return collector.collect(this.source());
    }

    @SuppressWarnings("unchecked")
    public D summate(){
        return this.summate((element) -> (D) element);
    }

    public D summate(final Function<E, D> mapper){
        Collector<E, D, D> collector = Collector.useFull(
                this::zero,
                (accumulator, element, index) -> this.plus(accumulator, mapper.apply(element)),
                this::plus,
                (a) -> a
        );
        return collector.collect(this.source());
    }

    Map<E, Long> frequency(){
        Collector<E, Map<E, Long>, Map<E, Long>> collector = Collectors.useFrequency();
        return collector.collect(this.source());
    }

    Map<D, Long> frequency(final Function<E, D> mapper){
        Collector<E, Map<D, Long>, Map<D, Long>> collector = Collectors.useFrequency(mapper);
        return collector.collect(this.source());
    }

    @SuppressWarnings("unchecked")
    public D median() {
        return this.median((element) -> (D) element);
    }

    public D median(final Function<E, D> mapper) {
        List<D> values = new ArrayList<>();
        this.forEach(element -> values.add(mapper.apply(element)));
        values.sort(this::compare);

        int size = values.size();
        if (size == 0) {
            return this.zero();
        }
        if (size % 2 == 1) {
            return values.get(size / 2);
        } else {
            D lower = values.get(size / 2 - 1);
            D upper = values.get(size / 2);
            return this.divide(this.plus(lower, upper), 2L);
        }
    }

    public Optional<E> mode(){
        Collector<E, TreeMap<E, Long>, Optional<E>> collector = Collectors.useMode();
        return collector.collect(this.source());
    }

    public Optional<D> mode(final Function<E, D> mapper) {
        Collector<E, TreeMap<D, Long>, Optional<D>> collector = Collectors.useMode(mapper);
        return collector.collect(this.source());
    }

    @SuppressWarnings("unchecked")
    public D variance() {
        return this.variance((element) -> (D) element);
    }

    public D variance(final Function<E, D> mapper) {
        D mean = this.average(mapper);
        long count = this.count();

        if (count <= 1) {
            return this.zero();
        }

        Collector<E, D, D> collector = Collector.useFull(
                this::zero,
                (accumulator, element, index) -> {
                    D value = mapper.apply(element);
                    D diff = this.subtract(value, mean);
                    D squared = this.multiply(diff, diff);
                    return this.plus(accumulator, squared);
                },
                this::plus,
                a -> this.divide(a, count - 1)
        );
        return collector.collect(this.source());
    }

    @SuppressWarnings("unchecked")
    public D standardDeviation() {
        return this.standardDeviation((element) -> (D) element);
    }

    public D standardDeviation(final Function<E, D> mapper) {
        D variance = this.variance(mapper);
        return this.squareRoot(variance);
    }
}
