package pers.eloyhere.semantic;

import java.util.function.*;

public interface Generator<T> extends BiConsumer<BiConsumer<T, Long>, BiPredicate<T, Long>> {
}
