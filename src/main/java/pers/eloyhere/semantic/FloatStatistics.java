package pers.eloyhere.semantic;

import java.util.Comparator;

public class FloatStatistics <E> extends Statistics<E, Float>{

    protected FloatStatistics(Generator<E> generator) {
        super(generator);
    }

    protected FloatStatistics(Generator<E> generator, long concurrent) {
        super(generator, concurrent);
    }

    protected FloatStatistics(Generator<E> generator, Comparator<E> comparator) {
        super(generator, comparator);
    }

    protected FloatStatistics(Generator<E> generator, Comparator<E> comparator, long concurrent) {
        super(generator, comparator, concurrent);
    }

    @Override
    public Float absolute(Float a) {
        return Math.abs(a);
    }

    @Override
    public Float squareRoot(Float a) {
        return (float) Math.sqrt(a);
    }

    @Override
    public int compare(Float a, Float b) {
        return Float.compare(a, b);
    }

    @Override
    public Float zero() {
        return 0f;
    }

    @Override
    public Float one() {
        return 1f;
    }

    @Override
    public Float plus(Float a, Float b) {
        return a + b;
    }

    @Override
    public Float subtract(Float a, Float b) {
        return a - b;
    }

    @Override
    public Float multiply(Float a, Float b) {
        return a * b;
    }

    @Override
    public Float divide(Float a, Float b) {
        return a / b;
    }

    @Override
    public Float divide(Float a, long b) {
        return a / b;
    }

    @Override
    public Float power(Float a, Float b) {
        return (float) Math.pow(a, b);
    }
}
