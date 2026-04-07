package pers.eloyhere.semantic;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;
import java.util.function.*;
import java.util.concurrent.atomic.*;

public class Semantic<E> {

    protected final Generator<E> generator;

    protected final Long concurrent;

    public Semantic(Generator<E> generator) {
        this.generator = generator;
        this.concurrent = 1L;
    }

    public Semantic(Generator<E> generator, Long concurrent) {
        this.generator = generator;
        this.concurrent = concurrent;
    }

    public Semantic<E> concatenate(final Semantic<E> other){
        Objects.requireNonNull(other, "Other could not be null.");
        return new Semantic<>((accept, interrupt) -> {
            AtomicLong count = new AtomicLong(0);
            AtomicBoolean stop = new AtomicBoolean(false);
            this.generator.accept(accept, (element, index) -> {
                stop.set(stop.get() || interrupt.test(element, count.getAndIncrement()));
                return stop.get();
            });
            other.source().accept(accept, (element, index) -> {
                stop.set(stop.get() || interrupt.test(element, count.getAndIncrement()));
                return stop.get();
            });
        }, this.concurrent);
    }

    public Semantic<E> concatenate(final Iterable<E> other){
        Objects.requireNonNull(other, "Other could not be null.");
        return new Semantic<>((accept, interrupt) -> {
            AtomicLong count = new AtomicLong(0);
            AtomicBoolean stop = new AtomicBoolean(false);
            this.generator.accept(accept, (element, index) -> {
                stop.set(stop.get() || interrupt.test(element, count.getAndIncrement()));
                return stop.get();
            });
            for(E element : other){
                if(interrupt.test(element, count.get())){
                    break;
                }
                accept.accept(element, count.getAndIncrement());
            }
        }, this.concurrent);
    }

    public Semantic<E> concatenate(final E[] other){
        Objects.requireNonNull(other, "Other could not be null.");
        return new Semantic<>((accept, interrupt) -> {
            AtomicLong count = new AtomicLong(0);
            AtomicBoolean stop = new AtomicBoolean(false);
            this.generator.accept(accept, (element, index) -> {
                stop.set(stop.get() || interrupt.test(element, count.getAndIncrement()));
                return stop.get();
            });
            for(E element : other){
                if(interrupt.test(element, count.get())){
                    break;
                }
                accept.accept(element, count.getAndIncrement());
            }
        }, this.concurrent);
    }

    public Semantic<E> distinct() {
        return new Semantic<>((accept, interrupt) -> {
            Set<E> seen = new HashSet<>();
            this.generator.accept((element, index) -> {
                if (!seen.contains(element)) {
                    seen.add(element);
                    accept.accept(element, (long) seen.size());
                }
            }, interrupt);
        }, this.concurrent);
    }

    public Semantic<E> distinct(final Comparator<E> comparator) {
        Objects.requireNonNull(comparator, "Comparator could not be null.");
        return new Semantic<>((accept, interrupt) -> {
            Set<E> seen = new TreeSet<>(comparator);
            this.generator.accept((element, index) -> {
                if (!seen.contains(element)) {
                    seen.add(element);
                    accept.accept(element, (long) seen.size());
                }
            }, interrupt);
        }, this.concurrent);
    }

    public Semantic<E> dropWhile(final Predicate<E> predicate) {
        Objects.requireNonNull(predicate, "Predicate could not be null.");
        return new Semantic<>((accept, interrupt) -> {
            AtomicLong count = new AtomicLong(-1L);
            this.generator.accept((element, index) -> {
                if(count.get() == -1L){
                    if(!predicate.test(element)){
                        count.getAndIncrement();
                    }
                }else{
                    accept.accept(element, count.getAndIncrement());
                }
            }, interrupt);
        }, this.concurrent);
    }

    public Semantic<E> dropWhile(final BiPredicate<E, Long> predicate) {
        Objects.requireNonNull(predicate, "Predicate could not be null.");
        return new Semantic<>((accept, interrupt) -> {
            AtomicLong count = new AtomicLong(-1L);
            this.generator.accept((element, index) -> {
                if(count.get() == -1L){
                    if(!predicate.test(element, index)){
                        count.getAndIncrement();
                    }
                }else{
                    accept.accept(element, count.getAndIncrement());
                }
            }, interrupt);
        }, this.concurrent);
    }

    public Semantic<E> filter(final Predicate<E> predicate) {
        Objects.requireNonNull(predicate, "Predicate could not be null.");
        return new Semantic<>((accept, interrupt) -> {
            this.generator.accept((element, index) -> {
                if (predicate.test(element)) {
                    accept.accept(element, index);
                }
            }, interrupt);
        }, this.concurrent);
    }

    public Semantic<E> filter(final BiPredicate<E, Long> predicate) {
        Objects.requireNonNull(predicate, "Predicate could not be null.");
        return new Semantic<>((accept, interrupt) -> {
            this.generator.accept((element, index) -> {
                if (predicate.test(element, index)) {
                    accept.accept(element, index);
                }
            }, interrupt);
        }, this.concurrent);
    }

    public <R> Semantic<R> map(final Function<E, R> mapper) {
        Objects.requireNonNull(mapper, "Mapper could not be null.");
        return new Semantic<>((accept, interrupt) -> {
            AtomicBoolean stop = new AtomicBoolean(false);
            this.generator.accept((element, index) -> {
                R mapped = mapper.apply(element);
                stop.set(stop.get() || interrupt.test(mapped, index));
                accept.accept(mapped, index);
            }, (element, index) -> stop.get());
        }, this.concurrent);
    }

    public Semantic<E> flat(final Function<E, Semantic<E>> mapper){
        Objects.requireNonNull(mapper, "Mapper could not be null.");
        return new Semantic<>((accept, interrupt) -> {
            AtomicLong count = new AtomicLong(0);
            AtomicBoolean stop = new AtomicBoolean(false);
            this.generator.accept((element, index) -> {
                Semantic<E> inner = mapper.apply(element);
                inner.source().accept((element1, index1) -> {
                    stop.set(stop.get() || interrupt.test(element1, count.get()));
                    accept.accept(element1, count.getAndIncrement());
                }, (element1, index1) -> stop.get());
            }, (element, index) -> stop.get());
        }, this.concurrent);
    }

    public Semantic<E> flat(final BiFunction<E, Long, Semantic<E>> mapper){
        Objects.requireNonNull(mapper, "Mapper could not be null.");
        return new Semantic<>((accept, interrupt) -> {
            AtomicLong count = new AtomicLong(0);
            AtomicBoolean stop = new AtomicBoolean(false);
            this.generator.accept((element, index) -> {
                Semantic<E> inner = mapper.apply(element, index);
                inner.source().accept((element1, index1) -> {
                    stop.set(stop.get() || interrupt.test(element1, count.get()));
                    accept.accept(element1, count.getAndIncrement());
                }, (element1, index1) -> stop.get());
            }, (element, index) -> stop.get());
        }, this.concurrent);
    }

    public <R> Semantic<R> flatMap(final Function<E, Semantic<R>> mapper){
        Objects.requireNonNull(mapper, "Mapper could not be null.");
        return new Semantic<>((accept, interrupt) -> {
            AtomicLong count = new AtomicLong(0);
            AtomicBoolean stop = new AtomicBoolean(false);
            this.generator.accept((element, index) -> {
                Semantic<R> inner = mapper.apply(element);
                inner.source().accept((element1, index1) -> {
                    stop.set(stop.get() || interrupt.test(element1, count.get()));
                    accept.accept(element1, count.getAndIncrement());
                }, (element1, index1) -> stop.get());
            }, (element, index) -> stop.get());
        }, this.concurrent);
    }

    public <R> Semantic<R> flatMap(final BiFunction<E, Long, Semantic<R>> mapper){
        Objects.requireNonNull(mapper, "Mapper could not be null.");
        return new Semantic<>((accept, interrupt) -> {
            AtomicLong count = new AtomicLong(0);
            AtomicBoolean stop = new AtomicBoolean(false);
            this.generator.accept((element, index) -> {
                Semantic<R> inner = mapper.apply(element, index);
                inner.source().accept((element1, index1) -> {
                    stop.set(stop.get() || interrupt.test(element1, count.get()));
                    accept.accept(element1, count.getAndIncrement());
                }, (element1, index1) -> stop.get());
            }, (element, index) -> stop.get());
        }, this.concurrent);
    }

    public Semantic<E> limit(final long n){
        if(n < 0){
            throw new IllegalArgumentException("Count could not less than 0.");
        }
        return new Semantic<>((accept, interrupt) -> {
            AtomicLong count = new AtomicLong(0);
            this.generator.accept((element, index) -> {
                if(count.get() < n){
                    accept.accept(element, count.getAndIncrement());
                }
            }, (element, index) -> count.get() >= n);
        }, this.concurrent);
    }

    public <R> Semantic<R> map(final BiFunction<E, Long, R> mapper) {
        Objects.requireNonNull(mapper, "Mapper could not be null.");
        return new Semantic<>((accept, interrupt) -> {
            AtomicBoolean stop = new AtomicBoolean(false);
            this.generator.accept((element, index) -> {
                R mapped = mapper.apply(element, index);
                stop.set(stop.get() || interrupt.test(mapped, index));
                accept.accept(mapped, index);
            }, (element, index) -> stop.get());
        }, this.concurrent);
    }

    public Semantic<E> parallel(){
        return new Semantic<>(this.generator, this.concurrent + 1L);
    }

    public Semantic<E> parallel(long concurrent){
        return new Semantic<>(this.generator, Math.max(concurrent, 1L));
    }

    public Semantic<E> peek(final Consumer<E> consumer) {
        Objects.requireNonNull(consumer, "Consumer could not be null.");
        return new Semantic<>((accept, interrupt) -> {
            this.generator.accept((element, index) -> {
                consumer.accept(element);
                accept.accept(element, index);
            }, interrupt);
        }, this.concurrent);
    }

    public Semantic<E> peek(final BiConsumer<E, Long> consumer) {
        Objects.requireNonNull(consumer, "Consumer could not be null.");
        return new Semantic<>((accept, interrupt) -> {
            this.generator.accept((element, index) -> {
                consumer.accept(element, index);
                accept.accept(element, index);
            }, interrupt);
        }, this.concurrent);
    }

    public Semantic<E> redirect(final BiFunction<E, Long, Long> redirector){
        Objects.requireNonNull(redirector, "Redirector could not be null.");
        return new Semantic<>((accept, interrupt) -> {
            AtomicBoolean stop = new AtomicBoolean(false);
            this.generator.accept((element, index) -> {
                Long redirected = redirector.apply(element, index);
                stop.set(stop.get() || interrupt.test(element, redirected));
                accept.accept(element, redirected);
            }, (element, index) -> {
                return stop.get();
            });
        }, this.concurrent);
    }

    public Semantic<E> reverse(){
        return new Semantic<>((accept, interrupt) -> {
            AtomicBoolean stop = new AtomicBoolean(false);
            this.generator.accept((element, index) -> {
                Long redirected = -index;
                stop.set(stop.get() || interrupt.test(element, redirected));
                accept.accept(element, redirected);
            }, (element, index) -> {
                return stop.get();
            });
        }, this.concurrent);
    }

    public Semantic<E> shuffle(){
        return new Semantic<>((accept, interrupt) -> {
            AtomicBoolean stop = new AtomicBoolean(false);
            Random random = new Random(System.currentTimeMillis());
            this.generator.accept((element, index) -> {
                Long redirected = random.nextLong();
                stop.set(stop.get() || interrupt.test(element, redirected));
                accept.accept(element, redirected);
            }, (element, index) -> stop.get());
        }, this.concurrent);
    }

    public Semantic<E> skip(final long n){
        if(n < 0){
            throw new IllegalArgumentException("Count could not less than 0.");
        }
        return new Semantic<>((accept, interrupt) -> {
            AtomicLong count = new AtomicLong(0);
            this.generator.accept((element, index) -> {
                if(count.get() > n){
                    accept.accept(element, count.get());
                }
                count.getAndIncrement();
            }, (element, index) -> count.get() >= n);
        }, this.concurrent);
    }

    public Semantic<E> sub(final long start, final long end){
        if(start * end < 0){
            throw new IllegalArgumentException("Start or end could not be less than 0.");
        }
        long minimum = Math.min(start, end);
        long maximum = Math.max(start, end);
        return new Semantic<>((accept, interrupt) -> {
            AtomicLong count = new AtomicLong(0);
            this.generator.accept((element, index) -> {
                if(count.get() > minimum && count.get() < maximum){
                    accept.accept(element, count.get());
                }
                count.getAndIncrement();
            }, (element, index) -> count.get() >= maximum);
        }, this.concurrent);
    }

    public Semantic<E> translate(final long translator){
        return new Semantic<>((accept, interrupt) -> {
            AtomicBoolean stop = new AtomicBoolean(false);
            this.generator.accept((element, index) -> {
                Long redirected = index + translator;
                stop.set(stop.get() || interrupt.test(element, redirected));
                accept.accept(element, redirected);
            }, (element, index) -> stop.get());
        }, this.concurrent);
    }

    public Semantic<E> translate(final BiFunction<E, Long, Long> translator){
        return new Semantic<>((accept, interrupt) -> {
            AtomicBoolean stop = new AtomicBoolean(false);
            this.generator.accept((element, index) -> {
                Long redirected = translator.apply(element, index);
                stop.set(stop.get() || interrupt.test(element, redirected));
                accept.accept(element, redirected);
            }, (element, index) -> stop.get());
        }, this.concurrent);
    }

    @SuppressWarnings("unchecked")
    public OrderedCollectable<E> sorted(){
        return new OrderedCollectable<>(this.source(), (a, b) -> ((Comparable<E>)a).compareTo(b), this.concurrent);
    }

    public OrderedCollectable<E> sorted(final Comparator<E> comparator){
        Objects.requireNonNull(comparator, "Comparator could not be null.");
        return new OrderedCollectable<>(this.source(), comparator, this.concurrent);
    }

    public Generator<E> source(){
        return this.generator;
    }

    public Semantic<E> takeWhile(final Predicate<E> predicate) {
        Objects.requireNonNull(predicate, "Predicate could not be null.");
        return new Semantic<>((accept, interrupt) -> {
            AtomicBoolean stop = new AtomicBoolean(false);
            this.generator.accept((element, index) -> {
                if(predicate.test(element) && !stop.get()){
                    accept.accept(element, index);
                }else{
                    stop.set(true);
                }
            }, (element, index) -> {
                stop.set(stop.get() || interrupt.test(element, index));
                return stop.get();
            });
        }, this.concurrent);
    }

    public Semantic<E> takeWhile(final BiPredicate<E, Long> predicate) {
        Objects.requireNonNull(predicate, "Predicate could not be null.");
        return new Semantic<>((accept, interrupt) -> {
            AtomicBoolean stop = new AtomicBoolean(false);
            this.generator.accept((element, index) -> {
                if(predicate.test(element, index) && !stop.get()){
                    accept.accept(element, index);
                }else{
                    stop.set(true);
                }
            }, (element, index) -> {
                stop.set(stop.get() || interrupt.test(element, index));
                return stop.get();
            });
        }, this.concurrent);
    }

    public ByteStatistics<E> toByteStatistics(){
        return new ByteStatistics<>(this.source(), this.concurrent);
    }

    public ShortStatistics<E> toShortStatistics(){
        return new ShortStatistics<>(this.source(), this.concurrent);
    }

    public FloatStatistics<E> toFloatStatistics(){
        return new FloatStatistics<>(this.source(), this.concurrent);
    }

    public IntStatistics<E> toIntStatistics(){
        return new IntStatistics<>(this.source(), this.concurrent);
    }

    public DoubleStatistics<E> toDoubleStatistics(){
        return new DoubleStatistics<>(this.source(), this.concurrent);
    }

    public LongStatistics<E> toLongStatistics(){
        return new LongStatistics<>(this.source(), this.concurrent);
    }

    public BigIntegerStatistics<E> toBigIntegerStatistics(){
        return new BigIntegerStatistics<>(this.source(), this.concurrent);
    }

    public BigDecimalStatistics<E> toBigDecimalStatistics(){
        return new BigDecimalStatistics<>(this.source(), this.concurrent);
    }

    public OrderedCollectable<E> toOrdered() {
        return new OrderedCollectable<>(this.source(), this.concurrent);
    }

    public WindowCollectable<E> toWindow(){
        return new WindowCollectable<>(this.source(), this.concurrent);
    }

    public UnorderedCollectable<E> toUnordered(){
        return new UnorderedCollectable<>(this.source(), this.concurrent);
    }

    public static Semantic<Long> useRange(long start, long end){
        long minimum = Math.min(start, end);
        long maximum = Math.max(start, end);
        return new Semantic<>((accept, interrupt) -> {
            for(long index = minimum; index < maximum; index++){
                if(interrupt.test(index, index)){
                    break;
                }
                accept.accept(index, index);
            }
        });
    }

    public static Semantic<Long> useRange(long start, long end, long step){
        long minimum = Math.min(start, end);
        long maximum = Math.max(start, end);
        long gap = Math.max(1L, Math.abs(step));
        return new Semantic<>((accept, interrupt) -> {
            for(long index = minimum; index < maximum; index+=gap){
                if(interrupt.test(index, index)){
                    break;
                }
                accept.accept(index, index);
            }
        });
    }

    public static <E> Semantic<E> useFrom(final Iterable<E> iterable){
        Objects.requireNonNull(iterable, "Iterable could not be null.");
        return new Semantic<>((accept, interrupt) -> {
            long index = 0;
            for(E element : iterable){
                if(interrupt.test(element, index)){
                    break;
                }
                accept.accept(element, index);
            }
        });
    }

    public static <E> Semantic<E> useFrom(final E[] elements){
        Objects.requireNonNull(elements, "Elements could not be null.");
        return new Semantic<>((accept, interrupt) -> {
            long index = 0;
            for(E element : elements){
                if(interrupt.test(element, index)){
                    break;
                }
                accept.accept(element, index);
            }
        });
    }

    public static Semantic<Byte> useBlob(final InputStream stream){
        Objects.requireNonNull(stream, "Stream could not be null.");
        return new Semantic<>((accept, interrupt) -> {
            long index = 0;
            try{
               for(byte b : stream.readAllBytes()){
                   if(interrupt.test(b, index)){
                       break;
                   }
                   accept.accept(b, index);
                   index++;
               }
            }catch (IOException exception){
                throw new RuntimeException(exception);
            }
        });
    }

    public static Semantic<Character> useBlob(final InputStream stream, final Charset charset){
        Objects.requireNonNull(stream, "Stream could not be null.");
        Objects.requireNonNull(charset, "Charset could not be null.");
        return new Semantic<>((accept, interrupt) -> {
            long index = 0;
            try(InputStreamReader reader = new InputStreamReader(stream, charset)){
                char[] buffer = new char[stream.available()];
                reader.read(buffer);
                for(Character character : buffer){
                    if(interrupt.test(character, index)){
                        break;
                    }
                    accept.accept(character, index);
                    index++;
                }
            }catch (IOException exception){
                throw new RuntimeException(exception);
            }
        });
    }

    public static Semantic<String> useCodePoint(final String text){
        Objects.requireNonNull(text, "Text could not be null.");
        return new Semantic<>((accept, interrupt) -> {
            for (int index = 0; index < text.codePointCount(0, text.length()); index++) {
                String point = Character.toString(text.codePointAt(index));
                if(interrupt.test(point, (long) index)){
                    break;
                }
                accept.accept(point, (long) index);
            }
        });
    }

    public static Semantic<String> useText(final String text){
        Objects.requireNonNull(text, "Text could not be null.");
        return new Semantic<>((accept, interrupt) -> {
            long index = 0;
            if(!interrupt.test(text, index)){
                accept.accept(text, index);
            }
        });
    }

    public static Semantic<String> useText(final String text, final String delimiter){
        Objects.requireNonNull(text, "Text could not be null.");
        Objects.requireNonNull(delimiter, "Delimiter could not be null.");
        return new Semantic<>((accept, interrupt) -> {
            if(!text.isEmpty() && !delimiter.isEmpty()){
                String[] pool = text.split(delimiter);
                for (int index = 0; index < pool.length; index++) {
                    if(interrupt.test(pool[index], (long) index)){
                        break;
                    }
                    accept.accept(pool[index], (long) index);
                }
            }
        });
    }
}
