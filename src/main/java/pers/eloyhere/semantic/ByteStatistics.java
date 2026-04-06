package pers.eloyhere.semantic;

import java.util.Comparator;

public class ByteStatistics<E> extends Statistics<E, Byte>{

    protected ByteStatistics(Generator<E> generator) {
        super(generator);
    }

    protected ByteStatistics(Generator<E> generator, long concurrent) {
        super(generator, concurrent);
    }

    protected ByteStatistics(Generator<E> generator, Comparator<E> comparator) {
        super(generator, comparator);
    }

    protected ByteStatistics(Generator<E> generator, Comparator<E> comparator, long concurrent) {
        super(generator, comparator, concurrent);
    }

    @Override
    public Byte absolute(Byte a) {
        return a < 0 ? (byte) -a : a;
    }

    @Override
    public Byte squareRoot(Byte a) {
        return (byte) Math.sqrt(a);
    }

    @Override
    public int compare(Byte a, Byte b) {
        return Integer.compare(a - b, 0);
    }

    @Override
    public Byte zero() {
        return 0;
    }

    @Override
    public Byte one() {
        return 1;
    }

    @Override
    public Byte plus(Byte a, Byte b) {
        return (byte) (a + b);
    }

    @Override
    public Byte subtract(Byte a, Byte b) {
        return (byte) (a - b);
    }

    @Override
    public Byte multiply(Byte a, Byte b) {
        return (byte) (a * b);
    }

    @Override
    public Byte divide(Byte a, Byte b) {
        return (byte) (a / b);
    }

    @Override
    public Byte divide(Byte a, long b) {
        return (byte)(a / b);
    }

    @Override
    public Byte power(Byte a, Byte b) {
        return (byte) Math.pow(a, b);
    }
}
