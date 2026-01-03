package pers.eloyhere.semantic;

import java.util.function.*;

public interface Generator <E> extends BiConsumer<BiConsumer<E, Long>, Predicate<E>> {

}
