package pers.eloyhere.semantic;

import java.math.BigDecimal;
import java.math.MathContext;
import java.security.InvalidAlgorithmParameterException;
import java.util.Comparator;

public class BigDecimalStatistics <E> extends Statistics<E, BigDecimal> {

    protected BigDecimalStatistics(Generator<E> generator) {
        super(generator);
    }

    protected BigDecimalStatistics(Generator<E> generator, long concurrent) {
        super(generator, concurrent);
    }

    protected BigDecimalStatistics(Generator<E> generator, Comparator<E> comparator) {
        super(generator, comparator);
    }

    protected BigDecimalStatistics(Generator<E> generator, Comparator<E> comparator, long concurrent) {
        super(generator, comparator, concurrent);
    }

    @Override
    public BigDecimal absolute(BigDecimal a) {
        return a.abs();
    }


    @Override
    public BigDecimal squareRoot(BigDecimal a) {
        return a.sqrt(MathContext.DECIMAL128);
    }

    @Override
    public int compare(BigDecimal a, BigDecimal b) {
        return a.compareTo(b);
    }

    @Override
    public BigDecimal zero() {
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal one() {
        return BigDecimal.ONE;
    }

    @Override
    public BigDecimal plus(BigDecimal a, BigDecimal b) {
        return a.add(b);
    }

    @Override
    public BigDecimal subtract(BigDecimal a, BigDecimal b) {
        return a.subtract(b);
    }

    @Override
    public BigDecimal multiply(BigDecimal a, BigDecimal b) {
        return a.multiply(b, MathContext.DECIMAL128);
    }

    @Override
    public BigDecimal divide(BigDecimal a, BigDecimal b) {
        return a.divide(b, MathContext.DECIMAL128);
    }

    @Override
    public BigDecimal divide(BigDecimal a, long b) {
        return a.divide(BigDecimal.valueOf(b), MathContext.DECIMAL128);
    }

    @Override
    public BigDecimal power(BigDecimal a, BigDecimal b) {
        return a.pow(b.intValue());
    }


}
