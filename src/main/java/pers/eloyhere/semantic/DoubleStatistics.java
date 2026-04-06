package pers.eloyhere.semantic;

import java.util.Comparator;

public class DoubleStatistics <E> extends Statistics<E, Double>{

    protected DoubleStatistics(Generator<E> generator) {
        super(generator);
    }

    protected DoubleStatistics(Generator<E> generator, long concurrent) {
        super(generator, concurrent);
    }

    protected DoubleStatistics(Generator<E> generator, Comparator<E> comparator) {
        super(generator, comparator);
    }

    protected DoubleStatistics(Generator<E> generator, Comparator<E> comparator, long concurrent) {
        super(generator, comparator, concurrent);
    }

    @Override
    public Double absolute(Double a) {
        return Math.abs(a);
    }

    @Override
    public Double squareRoot(Double a) {
        return Math.sqrt(a);
    }

    @Override
    public int compare(Double a, Double b) {
        return Double.compare(a, b);
    }

    @Override
    public Double zero() {
        return 0.0;
    }

    @Override
    public Double one() {
        return 1.0;
    }

    @Override
    public Double plus(Double a, Double b) {
        return a + b;
    }

    @Override
    public Double subtract(Double a, Double b) {
        return a - b;
    }

    @Override
    public Double multiply(Double a, Double b) {
        return a * b;
    }

    @Override
    public Double divide(Double a, Double b) {
        return a / b;
    }

    @Override
    public Double divide(Double a, long b) {
        return a / b;
    }

    @Override
    public Double power(Double a, Double b) {
        return Math.pow(a, b);
    }
}
