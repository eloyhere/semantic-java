package pers.eloyhere.semantic;

import java.util.Comparator;

public class IntStatistics <E> extends Statistics<E, Integer>{

    protected IntStatistics(Generator<E> generator) {
        super(generator);
    }

    protected IntStatistics(Generator<E> generator, long concurrent) {
        super(generator, concurrent);
    }

    protected IntStatistics(Generator<E> generator, Comparator<E> comparator) {
        super(generator, comparator);
    }

    protected IntStatistics(Generator<E> generator, Comparator<E> comparator, long concurrent) {
        super(generator, comparator, concurrent);
    }

    @Override
    public Integer absolute(Integer a) {
        return Math.abs(a);
    }

    @Override
    public Integer squareRoot(Integer a) {
        return (int) Math.sqrt(a);
    }

    @Override
    public int compare(Integer a, Integer b) {
        return Integer.compare(a, b);
    }

    @Override
    public Integer zero() {
        return 0;
    }

    @Override
    public Integer one() {
        return 1;
    }

    @Override
    public Integer plus(Integer a, Integer b) {
        return a + b;
    }

    @Override
    public Integer subtract(Integer a, Integer b) {
        return a - b;
    }

    @Override
    public Integer multiply(Integer a, Integer b) {
        return a * b;
    }

    @Override
    public Integer divide(Integer a, Integer b) {
        return a / b;
    }

    @Override
    public Integer divide(Integer a, long b) {
        return Math.toIntExact(a / b);
    }

    @Override
    public Integer power(Integer a, Integer b) {
        return (int) Math.pow(a, b);
    }


}
