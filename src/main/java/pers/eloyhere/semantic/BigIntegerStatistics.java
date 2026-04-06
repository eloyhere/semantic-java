package pers.eloyhere.semantic;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.util.Comparator;

public class BigIntegerStatistics <E> extends Statistics<E, BigInteger> {

    protected BigIntegerStatistics(Generator<E> generator) {
        super(generator);
    }

    protected BigIntegerStatistics(Generator<E> generator, long concurrent) {
        super(generator, concurrent);
    }

    protected BigIntegerStatistics(Generator<E> generator, Comparator<E> comparator) {
        super(generator, comparator);
    }

    protected BigIntegerStatistics(Generator<E> generator, Comparator<E> comparator, long concurrent) {
        super(generator, comparator, concurrent);
    }

    @Override
    public BigInteger absolute(BigInteger a) {
        return a.abs();
    }

    @Override
    public BigInteger squareRoot(BigInteger a) {
        return a.sqrt();
    }

    @Override
    public int compare(BigInteger a, BigInteger b) {
        return a.compareTo(b);
    }

    @Override
    public BigInteger zero() {
        return BigInteger.ZERO;
    }

    @Override
    public BigInteger one() {
        return BigInteger.ONE;
    }

    @Override
    public BigInteger plus(BigInteger a, BigInteger b) {
        return a.add(b);
    }

    @Override
    public BigInteger subtract(BigInteger a, BigInteger b) {
        return a.subtract(b);
    }

    @Override
    public BigInteger multiply(BigInteger a, BigInteger b) {
        return a.multiply(b);
    }

    @Override
    public BigInteger divide(BigInteger a, BigInteger b) {
        return a.divide(b);
    }

    @Override
    public BigInteger divide(BigInteger a, long b) {
        return a.divide(BigInteger.valueOf(b));
    }

    @Override
    public BigInteger power(BigInteger a, BigInteger b) {
        return a.pow(b.intValue());
    }
}
