package pers.eloyhere.semantic;

import java.util.Map;
import java.util.HashMap;

public class UnorderedCollectable <E> extends Collectable<E>{

    private final HashMap<Long, E> buffer = new HashMap<>();

    protected UnorderedCollectable(Generator<E> generator, long concurrent) {
        super(concurrent);
        generator.accept((element, index) -> buffer.put(index, element), (element, index) -> false);
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
