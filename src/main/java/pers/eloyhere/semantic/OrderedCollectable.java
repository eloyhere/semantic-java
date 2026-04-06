package pers.eloyhere.semantic;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class OrderedCollectable<E> extends Collectable<E>{

    private final TreeMap<Long, E> buffer;

    protected OrderedCollectable(Generator<E> generator) {
        super(1L);
        this.buffer = new TreeMap<>();
        final TreeMap<Long, E> temporary = new TreeMap<>();
        generator.accept((element, index) -> {
            temporary.put(index, element);
        }, (element, index) -> false);
        temporary.forEach((index, element) -> {
            buffer.put(((index % temporary.size()) + temporary.size()) % temporary.size(), element);
        });
    }

    protected OrderedCollectable(Generator<E> generator, long concurrent) {
        super(concurrent);
        this.buffer = new TreeMap<>();
        final TreeMap<Long, E> temporary = new TreeMap<>();
        generator.accept((element, index) -> {
            temporary.put(index, element);
        }, (element, index) -> false);
        temporary.forEach((index, element) -> {
            buffer.put(((index % temporary.size()) + temporary.size()) % temporary.size(), element);
        });
    }

    protected OrderedCollectable(Generator<E> generator, final Comparator<E> comparator) {
        super(1L);
        final TreeMap<Long, E> temporary = new TreeMap<>();
        this.buffer = new TreeMap<>((a, b) -> comparator.compare(temporary.get(a), temporary.get(b)));
        generator.accept((element, index) -> {
            temporary.put(index, element);
        }, (element, index) -> false);
        temporary.forEach((index, element) -> {
            buffer.put(((index % temporary.size()) + temporary.size()) % temporary.size(), element);
        });
    }

    protected OrderedCollectable(Generator<E> generator, final Comparator<E> comparator, final long concurrent) {
        super(concurrent);
        final TreeMap<Long, E> temporary = new TreeMap<>();
        this.buffer = new TreeMap<>((a, b) -> comparator.compare(temporary.get(a), temporary.get(b)));
        generator.accept((element, index) -> {
            temporary.put(index, element);
        }, (element, index) -> false);
        temporary.forEach((index, element) -> {
            buffer.put(((index % temporary.size()) + temporary.size()) % temporary.size(), element);
        });
    }

    @Override
    public long count() {
        return this.buffer.size();
    }

    @Override
    public Generator<E> source() {
        return (accept, interrupt)->{
            for(Map.Entry<Long, E> entry : this.buffer.entrySet()){
                if(interrupt.test(entry.getValue(), entry.getKey())){
                    break;
                }
                accept.accept(entry.getValue(), entry.getKey());
            }
        };
    }
}
