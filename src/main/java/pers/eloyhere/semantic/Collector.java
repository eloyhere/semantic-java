package pers.eloyhere.semantic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Collector <E, A, R>{

    private static final Runtime runtime = Runtime.getRuntime();
    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            runtime.availableProcessors(),
            runtime.availableProcessors() * 2,
            5L,
            TimeUnit.MINUTES,
            new LinkedBlockingDeque<>(),
            runnable -> {
                Thread thread = new Thread(runnable, "collector");
                thread.setDaemon(true);
                return thread;
            }
    );

    private final Supplier<A> identity;

    private final IndexedInterrupt<A, E> interrupt;

    private final IndexedAccumulator<A, E> accumulator;

    private final BiFunction<A, A, A> combiner;

    private final Function<A, R> finisher;

    private Collector(Supplier<A> identity, IndexedInterrupt<A, E> interrupt, IndexedAccumulator<A, E> accumulator, BiFunction<A, A, A> combiner, Function<A, R> finisher) {
        this.identity = identity;
        this.interrupt = interrupt;
        this.accumulator = accumulator;
        this.combiner = combiner;
        this.finisher = finisher;
    }

    public static <E, A, R> Collector<E, A, R> useFull(Supplier<A> identity, IndexedAccumulator<A, E> accumulator, BiFunction<A, A, A> combiner, Function<A, R> finisher){
        return new Collector<>(identity, (a, e, i)-> false, accumulator, combiner, finisher);
    }

    public static <E, A, R> Collector<E, A, R> useShortable(Supplier<A> identity, IndexedInterrupt<A, E> interrupt, IndexedAccumulator<A, E> accumulator, BiFunction<A, A, A> combiner, Function<A, R> finisher){
        return new Collector<>(identity, interrupt, accumulator, combiner, finisher);
    }

    public R collect(Generator<E> generator){
        return this.collect(generator, 1);
    }

    public R collect(Generator<E> generator, long concurrent){
        if(concurrent < 2){
            final AtomicReference<A> a = new AtomicReference<>(this.identity.get());
            generator.accept((element, index) -> {
                a.updateAndGet((previous) -> this.accumulator.apply(previous, element, index));
            }, (element, index) -> this.interrupt.test(a.get(), element, index));
            return this.finisher.apply(a.get());
        }
        List<Future<A>> futures = new ArrayList<>();
        for (AtomicLong thread = new AtomicLong(0); thread.get() < concurrent; thread.getAndIncrement()) {
            final long identity = thread.get();
            futures.add(executor.submit(()->{
                final AtomicReference<A> a = new AtomicReference<>(this.identity.get());
                generator.accept((element, index) -> {
                    if(index % concurrent == identity){
                        a.updateAndGet((previous) -> this.accumulator.apply(previous, element, index));
                    }
                }, (element, index) -> this.interrupt.test(a.get(), element, index));
                return a.get();
            }));
        }
        A a = this.identity.get();
        try{
            for(Future<A> future : futures){
                a = this.combiner.apply(a, future.get());
            }
        }catch (Exception exception){
            throw new RuntimeException(exception);
        }
        return this.finisher.apply(a);
    }

    public R collect(E[] elements){
        return this.collect(elements, 1);
    }

    public R collect(E[] elements, long concurrent){
        if(concurrent < 2){
            final AtomicReference<A> a = new AtomicReference<>(this.identity.get());
            AtomicLong index = new AtomicLong(0);
            for(E element : elements){
                if(this.interrupt.test(a.get(), element, index.get())){
                    break;
                }
                a.updateAndGet((previous) -> this.accumulator.apply(previous, element, index.getAndIncrement()));
            }
            return this.finisher.apply(a.get());
        }
        List<Future<A>> futures = new ArrayList<>();
        for (AtomicLong thread = new AtomicLong(0); thread.get() < concurrent; thread.getAndIncrement()) {
            final long identity = thread.get();
            futures.add(executor.submit(()->{
                final AtomicReference<A> a = new AtomicReference<>(this.identity.get());
                AtomicLong index = new AtomicLong(0);
                if(index.get() % concurrent == identity){
                    for(E element : elements){
                        if(this.interrupt.test(a.get(), element, index.get())){
                            break;
                        }
                        a.updateAndGet((previous) -> this.accumulator.apply(previous, element, index.getAndIncrement()));
                    }
                }
                return a.get();
            }));
        }
        A a = this.identity.get();
        try{
            for(Future<A> future : futures){
                a = this.combiner.apply(a, future.get());
            }
        }catch (Exception exception){
            throw new RuntimeException(exception);
        }
        return this.finisher.apply(a);
    }

    public R collect(Iterable<E> iterable){
        return this.collect(iterable, 1);
    }

    public R collect(Iterable<E> iterable, long concurrent){
        if(concurrent < 2){
            final AtomicReference<A> a = new AtomicReference<>(this.identity.get());
            AtomicLong index = new AtomicLong(0);
            for(E element : iterable){
                if(this.interrupt.test(a.get(), element, index.get())){
                    break;
                }
                a.updateAndGet((previous) -> this.accumulator.apply(previous, element, index.getAndIncrement()));
            }
            return this.finisher.apply(a.get());
        }
        List<Future<A>> futures = new ArrayList<>();
        for (AtomicLong thread = new AtomicLong(0); thread.get() < concurrent; thread.getAndIncrement()) {
            final long identity = thread.get();
            futures.add(executor.submit(()->{
                final AtomicReference<A> a = new AtomicReference<>(this.identity.get());
                AtomicLong index = new AtomicLong(0);
                if(index.get() % concurrent == identity){
                    for(E element : iterable){
                        if(this.interrupt.test(a.get(), element, index.get())){
                            break;
                        }
                        a.updateAndGet((previous) -> this.accumulator.apply(previous, element, index.getAndIncrement()));
                    }
                }
                return a.get();
            }));
        }
        A a = this.identity.get();
        try{
            for(Future<A> future : futures){
                a = this.combiner.apply(a, future.get());
            }
        }catch (Exception exception){
            throw new RuntimeException(exception);
        }
        return this.finisher.apply(a);
    }
}
