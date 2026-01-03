package pers.eloyhere.semantic;

import java.util.Comparator;

public class UnorderedCollectable<E> extends Collectable<E> {

    public UnorderedCollectable(Generator<E> generator) {
        super(generator);
    }

    public UnorderedCollectable(Generator<E> generator, final long concurrent) {
        super(generator, concurrent);
    }
}
