package pers.eloyhere.semantic;

import java.util.Comparator;

public class ShortStatistics<E> extends Statistics<E, Short>{

    protected ShortStatistics(Generator<E> generator) {
        super(generator);
    }

    protected ShortStatistics(Generator<E> generator, Comparator<E> comparator, long concurrent) {
        super(generator, comparator, concurrent);
    }

    protected ShortStatistics(Generator<E> generator, Comparator<E> comparator) {
        super(generator, comparator);
    }

    protected ShortStatistics(Generator<E> generator, long concurrent) {
        super(generator, concurrent);
    }

    @Override
    public Short absolute(Short a) {
        return (short) Math.abs(a);
    }

    @Override
    public Short squareRoot(Short a) {
        return (short) Math.sqrt(a);
    }

    @Override
    public int compare(Short a, Short b) {
        return Short.compare(a, b);
    }

    @Override
    public Short zero() {
        return 0;
    }

    @Override
    public Short one() {
        return 1;
    }



    @Override
    public Short plus(Short a, Short b) {
        return (short) (a + b);
    }

    @Override
    public Short subtract(Short a, Short b) {
        return (short) (a - b);
    }

    @Override
    public Short multiply(Short a, Short b) {
        return (short) (a * b);
    }

    @Override
    public Short divide(Short a, Short b) {
        return (short) (a / b);
    }

    @Override
    public Short divide(Short a, long b) {
        return (short) (a / b);
    }

    @Override
    public Short power(Short a, Short b) {
        return (short) Math.pow(a, b);
    }


}
