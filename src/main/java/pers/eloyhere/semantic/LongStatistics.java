package pers.eloyhere.semantic;

import java.util.Comparator;

public class LongStatistics <E> extends Statistics<E, Long>{

    protected LongStatistics(Generator<E> generator) {
        super(generator);
    }

    protected LongStatistics(Generator<E> generator, long concurrent) {
        super(generator, concurrent);
    }

    protected LongStatistics(Generator<E> generator, Comparator<E> comparator) {
        super(generator, comparator);
    }

    protected LongStatistics(Generator<E> generator, Comparator<E> comparator, long concurrent) {
        super(generator, comparator, concurrent);
    }

    @Override
    public Long absolute(Long a) {
        return Math.abs(a);
    }

    @Override
    public Long squareRoot(Long a) {
        return (long) Math.sqrt(a);
    }

    @Override
    public int compare(Long a, Long b) {
        return Long.compare(a, b);
    }

    @Override
    public Long zero() {
        return 0L;
    }

    @Override
    public Long one() {
        return 1L;
    }

    @Override
    public Long plus(Long a, Long b) {
        return a + b;
    }

    @Override
    public Long subtract(Long a, Long b) {
        return a - b;
    }

    @Override
    public Long multiply(Long a, Long b) {
        return a * b;
    }

    @Override
    public Long divide(Long a, Long b) {
        return a / b;
    }

    @Override
    public Long divide(Long a, long b) {
        return a / b;
    }

    @Override
    public Long power(Long a, Long b) {
        return (long) Math.pow(a, b);
    }
}
