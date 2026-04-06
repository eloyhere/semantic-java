package pers.eloyhere.semantic;

public interface IndexedAccumulator <A, E> {
    public A apply(A a, E e, Long index);
}
