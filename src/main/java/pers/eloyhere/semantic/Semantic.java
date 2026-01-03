package pers.eloyhere.semantic;
import java.io.*;
import java.util.*;
import java.util.Objects;
import java.util.Comparator;
import java.util.function.*;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.*;

public class Semantic<E> {

    protected final Generator<E> generator;

    protected final long concurrent;

    protected Semantic(final Generator<E> generator){
        this.generator = generator;
        this.concurrent = 1L;
    }

    protected Semantic(final Generator<E> generator, long concurrent){
        this.generator = generator;
        this.concurrent = concurrent;
    }

    public final Semantic<E> concat(final Semantic<E> other){
        return new Semantic<>((acceptor, interruptor)->{
            AtomicLong count = new AtomicLong(0L);
            this.generator.accept((element, index)-> {
                acceptor.accept(element, count.getAndIncrement());
            }, interruptor);
            other.generator.accept((element, index)-> {
                acceptor.accept(element, count.getAndIncrement());
            }, interruptor);
        });
    }

    public final Semantic<E> distinct(){
        return new Semantic<>((acceptor, interruptor) -> {
            AtomicLong index = new AtomicLong(0L);
            HashSet<E> seen = new HashSet<>();
            this.generator.accept((element, idx) -> {
                if (!seen.contains(element)) {
                    seen.add(element);
                    if (!interruptor.test(element)) {
                        acceptor.accept(element, index.getAndIncrement());
                    }
                }
            }, interruptor);
        });
    }

    public final Semantic<E> distinct(final Comparator<E> comparator){
        if(Objects.isNull(comparator)){
            throw new NullPointerException("Comparator could not be null.");
        }
        return new Semantic<>((acceptor, interruptor) -> {
            AtomicLong index = new AtomicLong(0L);
            ArrayList<E> seen = new ArrayList<>();
            this.generator.accept((element, idx) -> {
                boolean isDuplicate = false;
                for (E seenElement : seen) {
                    if (comparator.compare(element, seenElement) == 0) {
                        isDuplicate = true;
                        break;
                    }
                }
                if (!isDuplicate) {
                    seen.add(element);
                    if (!interruptor.test(element)) {
                        acceptor.accept(element, index.getAndIncrement());
                    }
                }
            }, interruptor);
        });
    }

    public final Semantic<E> dropWhile(final Predicate<E> predicate){
        if (Objects.isNull(predicate)) {
            throw new NullPointerException("Predicate must not be null.");
        }
        return new Semantic<>((acceptor, interruptor) -> {
            AtomicLong index = new AtomicLong(0L);
            AtomicBoolean dropping = new AtomicBoolean(true);
            this.generator.accept((element, idx) -> {
                if (dropping.get()) {
                    if (!predicate.test(element)) {
                        dropping.set(false);
                        if (!interruptor.test(element)) {
                            acceptor.accept(element, index.getAndIncrement());
                        }
                    }
                } else {
                    if (!interruptor.test(element)) {
                        acceptor.accept(element, index.getAndIncrement());
                    }
                }
            }, interruptor);
        });
    }

    public final Semantic<E> filter(final Predicate<E> predicate){
        if (Objects.isNull(predicate)) {
            throw new NullPointerException("Predicate must not be null.");
        }
        return new Semantic<>((acceptor, interruptor) -> {
            AtomicLong index = new AtomicLong(0L);
            this.generator.accept((element, idx) -> {
                if (predicate.test(element) && !interruptor.test(element)) {
                    acceptor.accept(element, index.getAndIncrement());
                }
            }, interruptor);
        });
    }

    public final Semantic<E> flat(final Function<E, Semantic<E>> mapper){
        if (Objects.isNull(mapper)) {
            throw new NullPointerException("Mapper must not be null.");
        }
        return new Semantic<>((acceptor, interruptor) -> {
            AtomicLong index = new AtomicLong(0L);
            this.generator.accept((element, idx) -> {
                Semantic<E> mapped = mapper.apply(element);
                if (Objects.nonNull(mapped)) {
                    mapped.generator.accept((mappedElement, mappedIdx) -> {
                        if (interruptor.test(mappedElement)) {
                            return;
                        }
                        acceptor.accept(mappedElement, index.getAndIncrement());
                    }, interruptor);
                }
            }, interruptor);
        });
    }

    public final  <R> Semantic<R> flatMap(final Function<E, Semantic<R>> mapper){
        if (Objects.isNull(mapper)) {
            throw new NullPointerException("Mapper must not be null.");
        }
        return new Semantic<>((acceptor, interruptor) -> {
            AtomicLong index = new AtomicLong(0L);
            AtomicBoolean stop = new AtomicBoolean(false);
            this.generator.accept((element, idx) -> {
                Semantic<R> mapped = mapper.apply(element);
                if (mapped != null) {
                    mapped.generator.accept((mappedElement, mappedIdx) -> {
                        stop.set(stop.get() || interruptor.test(mappedElement));
                        acceptor.accept(mappedElement, index.getAndIncrement());
                    }, interruptor);
                }
            }, (element)-> stop.get());
        });
    }

    public final Semantic<E> limit(long n){
        if (n < 0L) {
            throw new IllegalArgumentException("Limit must be non-negative.");
        }
        return new Semantic<>((acceptor, interruptor) -> {
            AtomicLong count = new AtomicLong(0L);
            this.generator.accept((element, index) -> {
                if (count.get() < n) {
                    acceptor.accept(element, index);
                    count.incrementAndGet();
                }
            }, interruptor);
        });
    }

    public final <R> Semantic<R> map(final Function<E, R> mapper){
        if (Objects.isNull(mapper)) {
            throw new NullPointerException("Mapper must not be null.");
        }
        return new Semantic<>((acceptor, interruptor) -> {
            AtomicBoolean stop = new AtomicBoolean(false);
            this.generator.accept((element, index) -> {
                R mapped = mapper.apply(element);
                if (mapped != null) {
                    acceptor.accept(mapped, index);
                }
                stop.set(stop.get() || interruptor.test(mapped));
            }, (element) -> stop.get());
        });
    }

    public final Semantic<E> parallel(){
        Runtime runtime = Runtime.getRuntime();
        return new Semantic<>(this.generator, (long) runtime.availableProcessors());
    }

    public final Semantic<E> parallel(final long threadCount){
        return new Semantic<>(this.generator, threadCount);
    }

    public final Semantic<E> peek(final BiConsumer<E, Long> action){
        if (Objects.isNull(action)) {
            throw new NullPointerException("Action must not be null.");
        }
        return new Semantic<>((acceptor, interruptor) -> {
            this.generator.accept((element, index) -> {
                action.accept(element, index);
                acceptor.accept(element, index);
            }, interruptor);
        });
    }

    public final Semantic<E> redirect(final BiFunction<E, Long, Long> redirector){
        if (Objects.isNull(redirector)) {
            throw new NullPointerException("Redirector must not be null.");
        }
        return new Semantic<>((acceptor, interruptor) -> {
            this.generator.accept((element, index) -> {
                acceptor.accept(element, redirector.apply(element, index));
            }, interruptor);
        });
    }

    public final Semantic<E> reverse(){
        return new Semantic<>((acceptor, interruptor) -> {
            this.generator.accept((element, index) -> {
                acceptor.accept(element, -index);
            }, interruptor);
        });
    }

    public final Semantic<E> shuffle(){
        return new Semantic<>((acceptor, interruptor) -> {
            this.generator.accept((element, index) -> {
                acceptor.accept(element, (long) Objects.hash(element, index));
            }, interruptor);
        });
    }

    public final Semantic<E> shuffle(final BiFunction<E, Long, Long> redirector){
        return new Semantic<>((acceptor, interruptor) -> {
            this.generator.accept((element, index) -> {
                acceptor.accept(element, redirector.apply(element, index));
            }, interruptor);
        });
    }

    public final Semantic<E> skip(long n){
        if (n < 0L) {
            throw new IllegalArgumentException("Limit must be non-negative.");
        }
        return new Semantic<>((acceptor, interruptor) -> {
            AtomicLong count = new AtomicLong(0L);
            this.generator.accept((element, index) -> {
                if (count.get() < n) {
                    count.getAndIncrement();
                }else{
                    acceptor.accept(element, index - count.getAndIncrement());
                }
            }, interruptor);
        });
    }

    @SuppressWarnings("unchecked")
    OrderedCollectable<E> sorted(){
        return new OrderedCollectable<>((acceptor, interruptor) -> {
            List<Map.Entry<Long, E>> entries = new ArrayList<>();
            this.generator.accept((element, index) -> {
                entries.add(new AbstractMap.SimpleEntry<>(index, element));
            }, (element) -> false);
            entries.sort((a, b)-> {
                if(a.getValue() instanceof Comparable && b.getValue() instanceof Comparable){
                    Comparable<Object> c = (Comparable<Object>) a.getValue();
                    Comparable<Object> d = (Comparable<Object>) b.getValue();
                    return c.compareTo(d);
                }
                return Objects.hashCode(a) - Objects.hashCode(b);
            });
            AtomicLong newIndex = new AtomicLong(0L);
            for (Map.Entry<Long, E> entry : entries) {
                E element = entry.getValue();
                if (interruptor.test(element)) {
                    break;
                }
                acceptor.accept(element, newIndex.getAndIncrement());
            }
        }, this.concurrent);
    }

    OrderedCollectable<E> sorted(final Comparator<E> comparator){
        return new OrderedCollectable<>((acceptor, interruptor) -> {
            List<Map.Entry<Long, E>> entries = new ArrayList<>();
            this.generator.accept((element, index) -> {
                entries.add(new AbstractMap.SimpleEntry<>(index, element));
            }, (element) -> false);
            entries.sort((a, b)-> comparator.compare(a.getValue(), b.getValue()));
            AtomicLong newIndex = new AtomicLong(0L);
            for (Map.Entry<Long, E> entry : entries) {
                E element = entry.getValue();
                if (interruptor.test(element)) {
                    break;
                }
                acceptor.accept(element, newIndex.getAndIncrement());
            }
        }, this.concurrent);
    }

    public final Semantic<E> sub(long start, long end){
        if (start < 0L || end < 0L) {
            throw new IllegalArgumentException("Start and end must all be positive.");
        }
        return new Semantic<>((acceptor, interruptor) -> {
            long maximum = Math.max(start, end);
            long minimum = Math.min(start, end);
            AtomicLong count = new AtomicLong(0L);
            this.generator.accept((element, index) -> {
                if(minimum < count.get() && count.get() < end){
                    acceptor.accept(element, index - count.get());
                }
                count.incrementAndGet();
            }, interruptor);
        });
    }

    public Semantic<E> takeWhile(final Predicate<E> predicate){
        if (predicate == null) {
            throw new NullPointerException("Predicate must not be null.");
        }
        return new Semantic<>((acceptor, interruptor) -> {
            AtomicLong index = new AtomicLong(0L);
            this.generator.accept((element, idx) -> {
                if (predicate.test(element)) {
                    acceptor.accept(element, index.getAndIncrement());
                }
            }, interruptor);
        });
    }

    public OrderedCollectable<E> toOrdered(){
        return new OrderedCollectable<>(this.generator, this.concurrent);
    }

    public Statistics<E, Float> toFloatStatistics(){
        return new FloatStatistics<>(this.generator, this.concurrent);
    }

    public Statistics<E, Short> toShortStatistics(){
        return new ShortStatistics<>(this.generator, this.concurrent);
    }

    public Statistics<E, Double> toDoubleStatistics(){
        return new DoubleStatistics<>(this.generator, this.concurrent);
    }

    public Statistics<E, Integer> toIntegerStatistics(){
        return new IntegerStatistics<>(this.generator, this.concurrent);
    }

    public Window<E> toWindow(){
        return new Window<>(this.generator, this.concurrent);
    }

    public Statistics<E, Long> toLongStatistics(){
        return new LongStatistics<>(this.generator, this.concurrent);
    }

    public UnorderedCollectable<E> toUnordered(){
        return new UnorderedCollectable<>(this.generator, this.concurrent);
    }

    public static Semantic<Byte> bytes(final InputStream stream) {
        if(Objects.nonNull(stream)){
            return new Semantic<>((acceptor, interrupt) -> {
                try {
                    int byteValue;
                    long index = 0L;
                    while ((byteValue = stream.read()) != -1) {
                        Byte b = (byte) byteValue;
                        if (interrupt.test(b)) {
                            break;
                        }
                        acceptor.accept(b, index);
                        index++;
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Error reading from InputStream", e);
                }
            });
        }
        throw new NullPointerException("Input stream is null.");
    }

    public static Semantic<Byte> chunks(final InputStream stream, final Long size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Chunk size must be greater than zero.");
        }
        if(Objects.nonNull(stream)){
            return new Semantic<>((acceptor, interrupt) -> {
                try {
                    byte[] buffer = new byte[size.intValue()];
                    int bytesRead;
                    long index = 0L;
                    while ((bytesRead = stream.read(buffer)) != -1) {
                        for (int i = 0; i < bytesRead; i++) {
                            Byte b = buffer[i];
                            if (interrupt.test(b)) {
                                break;
                            }
                            acceptor.accept(b, index);
                            index++;
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Error reading chunks from InputStream", e);
                }
            });
        }
        throw new NullPointerException("Input stream is null.");
    }

    public static <E> Semantic<E> empty(){
        return new Semantic<>((acceptor, interrupt)->{

        });
    }

    @SafeVarargs
    public static <E> Semantic<E> of(final E... elements){
        if(Objects.isNull(elements)){
            throw new NullPointerException("Elements are null.");
        }
        return new Semantic<>((acceptor, interruptor)->{
            Long index = 0L;
            for (E element: elements){
                if(interruptor.test(element)){
                    break;
                }
                acceptor.accept(element, index);
                index++;
            }
        });
    }

    public static <E> Semantic<E> fill(final E element, final long count){
        if(count < 1L){
            throw new IllegalArgumentException("Count must be positive.");
        }
        if(Objects.isNull(element)){
            throw new NullPointerException("Supplier is null.");
        }
        return new Semantic<>((acceptor, interrupt)->{
            for (long i = 0L; i < count; i++) {
                if(interrupt.test(element)){
                    break;
                }
                acceptor.accept(element, i);
            }
        });
    }

    public static <E> Semantic<E> fill(final Supplier<E> supplier, final long count){
        if(count < 1L){
            throw new IllegalArgumentException("Count must be positive.");
        }
        if(Objects.isNull(supplier)){
            throw new NullPointerException("Supplier is null.");
        }
        return new Semantic<>((acceptor, interrupt) -> {
            for (long i = 0L; i < count; i++) {
                E element = supplier.get();
                if (interrupt.test(element)) {
                    break;
                }
                acceptor.accept(element, i);
            }
        });
    }

    public static <E> Semantic<E> from(final Iterable<E> iterable){
        if(Objects.nonNull(iterable)){
            return new Semantic<>((acceptor, interruptor)->{
                Long index = 0L;
                for (E element: iterable){
                    if(interruptor.test(element)){
                        break;
                    }
                    acceptor.accept(element, index);
                    index++;
                }
            });
        }
        throw new NullPointerException("Iterable is null.");
    }

    public static <E> Semantic<E> iterate(final Generator<E> generator){
        if(Objects.isNull(generator)){
            throw new NullPointerException("Generator is null.");
        }
        return new Semantic<>(generator);
    }

    public static Semantic<Character> lines(final InputStream stream){
        if(Objects.isNull(stream)){
            throw new NullPointerException("Input stream is null.");
        }
        return new Semantic<>((acceptor, interrupt) -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                String line;
                long lineIndex = 0L;
                while ((line = reader.readLine()) != null) {
                    for (char c : line.toCharArray()) {
                        if (interrupt.test(c)) {
                            break;
                        }
                        acceptor.accept(c, lineIndex);
                        lineIndex++;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Error reading lines from InputStream", e);
            }
        });
    }

    public static Semantic<Long> range(long start, long end){
        if(start != end){
            long minimum = Math.min(start, end);
            long maximum = Math.max(start, end);
            return new Semantic<>((acceptor, interrupt)->{
                for (long i = minimum; i < maximum; i++) {
                    if(interrupt.test(i)){
                        break;
                    }
                    acceptor.accept(i, i);
                }
            });
        }
        throw new IllegalArgumentException("Start must not equal end.");
    }

    public static Semantic<Long> range(long start, long end, long step){
        if(start != end && step != 0L){
            return new Semantic<>((acceptor, interrupt) -> {
                if (step > 0) {
                    for (long i = start; i <= end; i += step) {
                        if (interrupt.test(i)) {
                            break;
                        }
                        acceptor.accept(i, i);
                    }
                } else {
                    for (long i = start; i >= end; i += step) {
                        if (interrupt.test(i)) {
                            break;
                        }
                        acceptor.accept(i, i);
                    }
                }
            });
        }
        throw new IllegalArgumentException("Start must not equal end and step must not equal zero.");
    }

    public static Semantic<Character> split(final InputStream stream, final Character delimiter){
        if(Objects.isNull(stream) || Objects.isNull(delimiter)){
            throw new NullPointerException("Input stream and delimiter could not be null.");
        }
        return new Semantic<>((acceptor, interrupt) -> {
            try {
                int byteValue;
                long index = 0L;
                boolean inToken = false;
                while ((byteValue = stream.read()) != -1) {
                    char c = (char) byteValue;
                    if (byteValue == delimiter) {
                        inToken = false;
                    } else {
                        if (!inToken) {
                            inToken = true;
                        }
                        if (interrupt.test(c)) {
                            break;
                        }
                        acceptor.accept(c, index);
                        index++;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Error splitting InputStream by delimiter", e);
            }
        });
    }

    public static Semantic<String> text(final InputStream stream){
        if(Objects.isNull(stream)){
            throw new NullPointerException("Input stream is null.");
        }
        return text(stream, Charset.defaultCharset());
    }

    public static Semantic<String> text(final InputStream stream, Charset charset){
        if(Objects.isNull(stream) || Objects.isNull(charset)){
            throw new NullPointerException("Input stream and charset could not be null.");
        }
        return new Semantic<>((acceptor, interrupt) -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, charset))) {
                String line;
                long lineIndex = 0L;
                while ((line = reader.readLine()) != null) {
                    if (interrupt.test(line)) {
                        break;
                    }
                    acceptor.accept(line, lineIndex);
                    lineIndex++;
                }
            } catch (IOException e) {
                throw new RuntimeException("Error reading text from InputStream", e);
            }
        });
    }
}
