package pers.eloyhere.semantic;

import java.util.ArrayList;
import java.util.Comparator;

public class WindowCollectable<E> extends OrderedCollectable<E> {

    public WindowCollectable(Generator<E> generator) {
        super(generator);
    }

    public WindowCollectable(Generator<E> generator, long concurrent) {
        super(generator, concurrent);
    }

    public WindowCollectable(Generator<E> generator, Comparator<E> comparator) {
        super(generator, comparator);
    }

    public WindowCollectable(Generator<E> generator, Comparator<E> comparator, long concurrent) {
        super(generator, comparator, concurrent);
    }

    public Semantic<Semantic<E>> slide(long size, long step){
        return new Semantic<>((accept, interrupt)->{
            long total = this.buffer.size();
            long index = 0L;
            boolean stop = false;
            for (long start = 0; start < total && !stop; start += step) {
                long end = Math.min(start + size, total);
                if(start < end){
                    ArrayList<E> window = new ArrayList<>();
                    for (long i = start; i < end; i++) {
                        window.add(this.buffer.get(i));
                    }
                    Semantic<E> semantic = Semantic.useFrom(window);
                    if(interrupt.test(semantic, index)){
                        break;
                    }
                    accept.accept(semantic, index);
                    index++;
                }
            }
        }, this.concurrent);
    }

    public Semantic<Semantic<E>> tumble(long size){
        return this.slide(size, size);
    }
}
