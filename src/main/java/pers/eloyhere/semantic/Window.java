package pers.eloyhere.semantic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Window<E> extends OrderedCollectable<E> {

    public Window(Generator<E> generator) {
        super(generator);
    }

    public Window(Generator<E> generator, Comparator<E> comparator) {
        super(generator, comparator);
    }

    public Window(Generator<E> generator, long concurrent) {
        super(generator, concurrent);
    }

    public Window(Generator<E> generator, long concurrent, Comparator<E> comparator) {
        super(generator, concurrent, comparator);
    }

    public Semantic<Semantic<E>> slide(long size, long step){
        List<E> list = this.source.stream().map(Pair::b).toList();
        return new Semantic<>((consumer1, interruptor1) -> {
            long index = 0;
            while (index < list.size()) {
                final long currentIndex = index;
                Semantic<E> window = new Semantic<>((consumer2, interruptor2) -> {
                    for (long i = currentIndex; i < Math.min(currentIndex + size, list.size()); i++) {
                        E element = list.get((int) i);
                        if(interruptor2.test(element)){
                            break;
                        }
                        consumer2.accept(element, i);
                    }
                });
                if(interruptor1.test(window)){
                    break;
                }
                consumer1.accept(window, index);
                index += step;
            }
        });
    }

    public Semantic<Semantic<E>> tumble(long size){
        return this.slide(size, size);
    }
}
