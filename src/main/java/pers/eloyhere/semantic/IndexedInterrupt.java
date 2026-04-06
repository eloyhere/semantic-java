package pers.eloyhere.semantic;

public interface IndexedInterrupt<A, E> {
    public boolean test(A a, E e, Long index);
}
