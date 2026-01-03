package pers.eloyhere.semantic;

import java.util.*;
import java.util.function.Function;

public abstract class Statistics<E, D extends Number> extends OrderedCollectable<E>{

    public Statistics(Generator<E> generator) {
        super(generator);
    }

    public Statistics(Generator<E> generator, Comparator<E> comparator) {
        super(generator, comparator);
    }

    public Statistics(Generator<E> generator, long concurrent) {
        super(generator, concurrent);
    }

    public Statistics(Generator<E> generator, long concurrent, Comparator<E> comparator) {
        super(generator, concurrent, comparator);
    }

    public long count() {
        return super.count();
    }

    Optional<E> maximum(){
        return this.maximum((a, b)->{
            if(a instanceof Comparable && b instanceof Comparable){
                Comparable<Object> c = (Comparable<Object>) a;
                Comparable<Object> d = (Comparable<Object>) b;
                return c.compareTo(d);
            }
            return Objects.hashCode(a) - Objects.hashCode(b);
        });
    }

    Optional<E> maximum(Comparator<E> comparator){
        return this.source.stream().map(Pair::b).max(comparator);
    }

    Optional<E> minimum(){
        return this.minimum((a, b)->{
            if(a instanceof Comparable && b instanceof Comparable){
                Comparable<Object> c = (Comparable<Object>) a;
                Comparable<Object> d = (Comparable<Object>) b;
                return c.compareTo(d);
            }
            return Objects.hashCode(a) - Objects.hashCode(b);
        });
    }

    Optional<E> minimum(Comparator<E> comparator){
        return this.source.stream().map(Pair::b).min(comparator);
    }

    public abstract D range();
    public abstract D range(final Function<E, D> mapper);

    public abstract D variance();
    public abstract D variance(final Function<E, D> mapper);

    public abstract D standardDeviation();
    public abstract D standardDeviation(final Function<E, D> mapper);

    public abstract D mean();
    public abstract D mean(final Function<E, D> mapper);

    public abstract D median();
    public abstract D median(final Function<E, D> mapper);

    public abstract D mode();
    public abstract D mode(final Function<E, D> mapper);

    public abstract D product();
    public abstract D product(final Function<E, D> mapper);

    public abstract D geometricMean();
    public abstract D geometricMean(final Function<E, D> mapper);

    public abstract D harmonicMean();
    public abstract D harmonicMean(final Function<E, D> mapper);

    public abstract D medianAbsoluteDeviation();
    public abstract D medianAbsoluteDeviation(final Function<E, D> mapper);

    public abstract D coefficientOfVariation();
    public abstract D coefficientOfVariation(final Function<E, D> mapper);

    public abstract D quartile1();
    public abstract D quartile1(final Function<E, D> mapper);

    public abstract HashMap<D, Long> frequency();
    public abstract HashMap<D, Long> frequency(final Function<E, D> mapper);

    public abstract D summate();
    public abstract D summate(final Function<E, D> mapper);

    public abstract List<D> quartiles();
    public abstract List<D> quartiles(final Function<E, D> mapper);

    public abstract D interquartileRange();
    public abstract D interquartileRange(final Function<E, D> mapper);

    public abstract D skewness();
    public abstract D skewness(final Function<E, D> mapper);

    public abstract boolean isEmpty();
}
