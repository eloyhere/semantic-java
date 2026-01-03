package pers.eloyhere.semantic;

import java.util.*;
import java.util.function.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class Collector<A, E, R> {

    protected final Supplier<A> identity;

    protected final Predicate<E> interruptor;

    protected final TriFunction<A, E, Long, A> accumulator;

    protected final BiFunction<A, A, A> combiner;

    protected final Function<A, R> finisher;

    protected static Runtime runtime = Runtime.getRuntime();

    protected static ThreadPoolExecutor executor = new ThreadPoolExecutor(runtime.availableProcessors(), runtime.availableProcessors()*2, 10, TimeUnit.MINUTES, new ArrayBlockingQueue<>(runtime.availableProcessors()*2));

    protected Collector(final Supplier<A> identity, final Predicate<E> interruptor, final TriFunction<A, E, Long, A> accumulator, final BiFunction<A, A, A> combiner, final Function<A, R> finisher) {
        this.identity = identity;
        this.interruptor = interruptor;
        this.accumulator = accumulator;
        this.combiner = combiner;
        this.finisher = finisher;
    }

    public R collect(final Generator<E> generator){
        return collect(generator, 1);
    }

    public R collect(final Iterable<E> iterable){
        return collect(iterable, 1);
    }

    public R collect(final E[] elements){
        return collect(elements, 1);
    }

    public R collect(final Generator<E> generator, final long threadCount) {
        if (threadCount <= 1) {
            AtomicReference<A> containerRef = new AtomicReference<>(identity.get());
            AtomicLong index = new AtomicLong(0L);
            generator.accept((element, idx) -> {
                if (interruptor.test(element)) {
                    return;
                }
                containerRef.updateAndGet(current ->
                        accumulator.apply(current, element, index.getAndIncrement())
                );
            }, interruptor);
            return finisher.apply(containerRef.get());
        } else {
            List<E> collectedElements = new ArrayList<>();
            generator.accept((element, idx) -> {
                if (interruptor.test(element)) {
                    return;
                }
                collectedElements.add(element);
            }, interruptor);

            int size = collectedElements.size();
            int chunkSize = (int) Math.ceil((double) size / threadCount);
            List<Future<A>> futures = new ArrayList<>();

            for (int i = 0; i < threadCount; i++) {
                final int start = i * chunkSize;
                final int end = Math.min(start + chunkSize, size);
                final AtomicLong localIndex = new AtomicLong(start);

                futures.add(executor.submit(() -> {
                    AtomicReference<A> containerRef = new AtomicReference<>(identity.get());
                    for (int j = start; j < end; j++) {
                        E element = collectedElements.get(j);
                        if (interruptor.test(element)) {
                            break;
                        }
                        containerRef.updateAndGet(current ->
                                accumulator.apply(current, element, localIndex.getAndIncrement())
                        );
                    }
                    return containerRef.get();
                }));
            }

            A combined = identity.get();
            for (Future<A> future : futures) {
                try {
                    combined = combiner.apply(combined, future.get());
                } catch (InterruptedException | ExecutionException e) {
                    Thread.currentThread().interrupt();
                }
            }
            return finisher.apply(combined);
        }
    }

    public R collect(final Iterable<E> iterable, final long threadCount) {
        if (threadCount <= 1) {
            AtomicReference<A> containerRef = new AtomicReference<>(identity.get());
            AtomicLong index = new AtomicLong(0L);
            for (E element : iterable) {
                if (interruptor.test(element)) {
                    break;
                }
                containerRef.updateAndGet(current ->
                        accumulator.apply(current, element, index.getAndIncrement())
                );
            }
            return finisher.apply(containerRef.get());
        } else {
            List<E> elements = new ArrayList<>();
            Iterator<E> iterator = iterable.iterator();
            AtomicLong globalIndex = new AtomicLong(0L);

            while (iterator.hasNext()) {
                E element = iterator.next();
                if (interruptor.test(element)) {
                    break;
                }
                elements.add(element);
            }

            int size = elements.size();
            int chunkSize = (int) Math.ceil((double) size / threadCount);
            List<Future<A>> futures = new ArrayList<>();

            for (int i = 0; i < threadCount; i++) {
                final int start = i * chunkSize;
                final int end = Math.min(start + chunkSize, size);
                final AtomicLong threadLocalIndex = new AtomicLong(start);

                futures.add(executor.submit(() -> {
                    AtomicReference<A> containerRef = new AtomicReference<>(identity.get());
                    for (int j = start; j < end; j++) {
                        E element = elements.get(j);
                        if (interruptor.test(element)) {
                            break;
                        }
                        containerRef.updateAndGet(current ->
                                accumulator.apply(current, element, threadLocalIndex.getAndIncrement())
                        );
                    }
                    return containerRef.get();
                }));
            }

            A combined = identity.get();
            for (Future<A> future : futures) {
                try {
                    A partial = future.get();
                    combined = combiner.apply(combined, partial);
                } catch (InterruptedException | ExecutionException e) {
                    Thread.currentThread().interrupt();
                }
            }

            return finisher.apply(combined);
        }
    }

    public R collect(final E[] elements, final long threadCount) {
        if (threadCount <= 1) {
            AtomicReference<A> containerRef = new AtomicReference<>(identity.get());
            AtomicLong index = new AtomicLong(0L);
            for (E element : elements) {
                if (interruptor.test(element)) {
                    break;
                }
                containerRef.updateAndGet(current ->
                        accumulator.apply(current, element, index.getAndIncrement())
                );
            }
            return finisher.apply(containerRef.get());
        } else {
            int length = elements.length;
            int chunkSize = (int) Math.ceil((double) length / threadCount);
            List<Future<A>> futures = new ArrayList<>();

            AtomicLong globalIndex = new AtomicLong(0L);

            for (int i = 0; i < threadCount; i++) {
                final int start = i * chunkSize;
                final int end = Math.min(start + chunkSize, length);

                futures.add(executor.submit(() -> {
                    AtomicReference<A> containerRef = new AtomicReference<>(identity.get());
                    for (int j = start; j < end; j++) {
                        E element = elements[j];
                        if (interruptor.test(element)) {
                            break;
                        }
                        containerRef.updateAndGet(current ->
                                accumulator.apply(current, element, globalIndex.getAndIncrement())
                        );
                    }
                    return containerRef.get();
                }));
            }

            A combined = identity.get();
            for (Future<A> future : futures) {
                try {
                    A partial = future.get();
                    combined = combiner.apply(combined, partial);
                } catch (InterruptedException | ExecutionException e) {
                    Thread.currentThread().interrupt();
                }
            }

            return finisher.apply(combined);
        }
    }

    public static <A, E, R> Collector<A, E, R> full(final Supplier<A> identity, final TriFunction<A, E, Long, A> accumulator, final BiFunction<A, A, A> combiner, final Function<A, R> finisher){
        return new Collector<>(identity, (element)-> false, accumulator, combiner, finisher);
    }

    public static <A, E, R> Collector<A, E, R> shortable(final Supplier<A> identity, final Predicate<E> interruptor, final TriFunction<A, E, Long, A> accumulator, final BiFunction<A, A, A> combiner, final Function<A, R> finisher){
        return new Collector<>(identity, interruptor, accumulator, combiner, finisher);
    }
}
