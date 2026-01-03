package pers.eloyhere.semantic;

import java.util.*;
import java.util.function.Function;

public class LongStatistics<E> extends Statistics<E, Long> {

    public LongStatistics(Generator<E> generator) {
        super(generator);
    }

    public LongStatistics(Generator<E> generator, Comparator<E> comparator) {
        super(generator, comparator);
    }

    public LongStatistics(Generator<E> generator, long concurrent) {
        super(generator, concurrent);
    }

    public LongStatistics(Generator<E> generator, long concurrent, Comparator<E> comparator) {
        super(generator, concurrent, comparator);
    }

    @Override
    public Long range() {
        if (this.isEmpty()) {
            return 0L;
        }
        Optional<E> maximum = this.maximum();
        Optional<E> minimum = this.minimum();
        if (maximum.isPresent() && minimum.isPresent()) {
            return (Long) maximum.get() - (Long) minimum.get();
        }
        return 0L;
    }

    @Override
    public Long range(Function<E, Long> mapper) {
        if (this.isEmpty()) {
            return 0L;
        }
        Optional<Long> maximum = this.maximum().map(mapper);
        Optional<Long> minimum = this.minimum().map(mapper);
        if (maximum.isPresent() && minimum.isPresent()) {
            return maximum.get() - minimum.get();
        }
        return 0L;
    }

    @Override
    public Long variance() {
        if (this.count() < 2) {
            return 0L;
        }
        double mean = this.mean();
        double sumSquaredDiffs = this.collect(
                () -> 0.0,
                (result, element, index) -> {
                    if (element instanceof Number) {
                        double value = ((Number) element).longValue();
                        double diff = value - mean;
                        return result + (diff * diff);
                    }
                    return result;
                },
                Double::sum,
                result -> result
        );
        return (long) (sumSquaredDiffs / (this.count() - 1));
    }

    @Override
    public Long variance(Function<E, Long> mapper) {
        if (this.count() < 2) {
            return 0L;
        }
        double mean = this.mean(mapper);
        double sumSquaredDiffs = this.collect(
                () -> 0.0,
                (result, element, index) -> {
                    long value = mapper.apply(element);
                    double diff = value - mean;
                    return result + (diff * diff);
                },
                Double::sum,
                result -> result
        );
        return (long) (sumSquaredDiffs / (this.count() - 1));
    }

    @Override
    public Long standardDeviation() {
        if (this.isEmpty()) {
            return 0L;
        }
        Optional<E> maximum = this.maximum();
        Optional<E> minimum = this.minimum();
        if (maximum.isPresent() && minimum.isPresent()) {
            double mean = this.mean();
            double sumSquaredDiffs = this.collect(
                    () -> 0.0,
                    (result, element, index) -> {
                        if (element instanceof Number) {
                            double value = ((Number) element).longValue();
                            double diff = value - mean;
                            return result + (diff * diff);
                        }
                        return result;
                    },
                    Double::sum,
                    result -> result
            );
            return (long) Math.sqrt(sumSquaredDiffs / (this.count() - 1));
        }
        return 0L;
    }

    @Override
    public Long standardDeviation(Function<E, Long> mapper) {
        if (this.count() < 2) {
            return 0L;
        }
        Optional<E> maximum = this.maximum();
        Optional<E> minimum = this.minimum();
        if (maximum.isPresent() && minimum.isPresent()) {
            double mean = this.mean(mapper);
            double sumSquaredDiffs = this.collect(
                    () -> 0.0,
                    (result, element, index) -> {
                        long value = mapper.apply(element);
                        double diff = value - mean;
                        return result + (diff * diff);
                    },
                    Double::sum,
                    result -> result
            );
            return (long) Math.sqrt(sumSquaredDiffs / (this.count() - 1));
        }
        return 0L;
    }

    @Override
    public Long mean() {
        if (this.isEmpty()) {
            return 0L;
        }
        Optional<E> maximum = this.maximum();
        Optional<E> minimum = this.minimum();
        if (maximum.isPresent() && minimum.isPresent()) {
            return this.collect(
                    () -> new double[]{0.0, 0.0},
                    (result, element, index) -> {
                        if (element instanceof Number) {
                            result[0] += ((Number) element).longValue();
                            result[1] += 1.0;
                        }
                        return result;
                    },
                    (result1, result2) -> {
                        result1[0] += result2[0];
                        result1[1] += result2[1];
                        return result1;
                    },
                    result -> result[1] > 0 ? (long) (result[0] / result[1]) : 0L
            );
        }
        return 0L;
    }

    @Override
    public Long mean(Function<E, Long> mapper) {
        if (this.isEmpty()) return 0L;
        Optional<E> maximum = this.maximum();
        Optional<E> minimum = this.minimum();
        if (maximum.isPresent() && minimum.isPresent()) {
            return this.collect(
                    () -> new double[]{0.0, 0.0},
                    (result, element, index) -> {
                        long value = mapper.apply(element);
                        result[0] += value;
                        result[1] += 1.0;
                        return result;
                    },
                    (result1, result2) -> {
                        result1[0] += result2[0];
                        result1[1] += result2[1];
                        return result1;
                    },
                    result -> result[1] > 0 ? (long) (result[0] / result[1]) : 0L
            );
        }
        return 0L;
    }

    @Override
    public Long median() {
        if (this.isEmpty()) return 0L;
        Optional<E> maximum = this.maximum();
        Optional<E> minimum = this.minimum();
        if (maximum.isPresent() && minimum.isPresent()) {
            List<Long> values = new ArrayList<>();
            this.generator.accept((element, index) -> {
                if (element instanceof Number) {
                    values.add(((Number) element).longValue());
                }
            }, (element) -> false);
            if (values.isEmpty()) return 0L;
            Collections.sort(values);
            int size = values.size();
            if (size % 2 == 0) {
                return (values.get(size / 2 - 1) + values.get(size / 2)) / 2;
            } else {
                return values.get(size / 2);
            }
        }
        return 0L;
    }

    @Override
    public Long median(Function<E, Long> mapper) {
        if (this.isEmpty()) {
            return 0L;
        }
        Optional<E> maximum = this.maximum();
        Optional<E> minimum = this.minimum();
        if (maximum.isPresent() && minimum.isPresent()) {
            List<Long> values = new ArrayList<>();
            this.generator.accept((element, index) -> {
                values.add(mapper.apply(element));
            }, (element) -> false);
            if (values.isEmpty()) {
                return 0L;
            }
            Collections.sort(values);
            int size = values.size();
            if (size % 2 == 0) {
                return (values.get(size / 2 - 1) + values.get(size / 2)) / 2;
            } else {
                return values.get(size / 2);
            }
        }
        return 0L;
    }

    @Override
    public Long mode() {
        if (this.isEmpty()) {
            return 0L;
        }
        HashMap<Long, Long> freq = this.frequency();
        if (freq.isEmpty()) {
            return 0L;
        }
        return freq.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(0L);
    }

    @Override
    public Long mode(Function<E, Long> mapper) {
        if (this.isEmpty()) {
            return 0L;
        }
        HashMap<Long, Long> freq = this.frequency(mapper);
        if (freq.isEmpty()) {
            return 0L;
        }
        return freq.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(0L);
    }

    @Override
    public Long product() {
        if (this.isEmpty()) {
            return 1L;
        }
        Optional<E> maximum = this.maximum();
        if (maximum.isPresent()) {
            return this.collect(
                    () -> 1L,
                    (result, element, index) -> {
                        if (element instanceof Number) {
                            return result * ((Number) element).longValue();
                        }
                        return result;
                    },
                    (a, b) -> a * b,
                    result -> result
            );
        }
        return 1L;
    }

    @Override
    public Long product(Function<E, Long> mapper) {
        if (this.isEmpty()) {
            return 1L;
        }
        Optional<E> maximum = this.maximum();
        if (maximum.isPresent()) {
            return this.collect(
                    () -> 1L,
                    (result, element, index) -> {
                        return result * mapper.apply(element);
                    },
                    (a, b) -> a * b,
                    result -> result
            );
        }
        return 1L;
    }

    @Override
    public Long geometricMean() {
        if (this.isEmpty()) {
            return 0L;
        }
        Optional<E> maximum = this.maximum();
        if (maximum.isPresent()) {
            List<Long> values = new ArrayList<>();
            this.generator.accept((element, index) -> {
                if (element instanceof Number) {
                    long value = ((Number) element).longValue();
                    if (value > 0) {
                        values.add(value);
                    }
                }
            }, (element) -> false);
            if (values.isEmpty()) {
                return 0L;
            }
            double product = 1.0;
            for (long value : values) {
                product *= value;
            }
            return (long) Math.pow(product, 1.0 / values.size());
        }
        return 0L;
    }

    @Override
    public Long geometricMean(Function<E, Long> mapper) {
        if (this.isEmpty()) {
            return 0L;
        }
        Optional<E> maximum = this.maximum();
        if (maximum.isPresent()) {
            List<Long> values = new ArrayList<>();
            this.generator.accept((element, index) -> {
                long value = mapper.apply(element);
                if (value > 0) {
                    values.add(value);
                }
            }, (element) -> false);
            if (values.isEmpty()) {
                return 0L;
            }
            double product = 1.0;
            for (long value : values) {
                product *= value;
            }
            return (long) Math.pow(product, 1.0 / values.size());
        }
        return 0L;
    }

    @Override
    public Long harmonicMean() {
        if (this.isEmpty()) {
            return 0L;
        }
        Optional<E> maximum = this.maximum();
        if (maximum.isPresent()) {
            return this.collect(
                    () -> new double[]{0.0, 0.0},
                    (result, element, index) -> {
                        if (element instanceof Number) {
                            long value = ((Number) element).longValue();
                            if (value != 0) {
                                result[0] += 1.0 / value;
                                result[1] += 1.0;
                            }
                        }
                        return result;
                    },
                    (result1, result2) -> {
                        result1[0] += result2[0];
                        result1[1] += result2[1];
                        return result1;
                    },
                    result -> result[1] > 0 ? (long) (result[1] / result[0]) : 0L
            );
        }
        return 0L;
    }

    @Override
    public Long harmonicMean(Function<E, Long> mapper) {
        if (this.isEmpty()) {
            return 0L;
        }
        Optional<E> maximum = this.maximum();
        if (maximum.isPresent()) {
            return this.collect(
                    () -> new double[]{0.0, 0.0},
                    (result, element, index) -> {
                        long value = mapper.apply(element);
                        if (value != 0) {
                            result[0] += 1.0 / value;
                            result[1] += 1.0;
                        }
                        return result;
                    },
                    (result1, result2) -> {
                        result1[0] += result2[0];
                        result1[1] += result2[1];
                        return result1;
                    },
                    result -> result[1] > 0 ? (long) (result[1] / result[0]) : 0L
            );
        }
        return 0L;
    }

    @Override
    public Long medianAbsoluteDeviation() {
        if (this.isEmpty()) {
            return 0L;
        }
        long median = this.median();
        List<Long> deviations = new ArrayList<>();
        this.generator.accept((element, index) -> {
            if (element instanceof Number) {
                long value = ((Number) element).longValue();
                deviations.add(Math.abs(value - median));
            }
        }, (element) -> false);
        if (deviations.isEmpty()) {
            return 0L;
        }
        Collections.sort(deviations);
        int size = deviations.size();
        if (size % 2 == 0) {
            return (deviations.get(size / 2 - 1) + deviations.get(size / 2)) / 2;
        } else {
            return deviations.get(size / 2);
        }
    }

    @Override
    public Long medianAbsoluteDeviation(Function<E, Long> mapper) {
        if (this.isEmpty()) {
            return 0L;
        }
        long median = this.median(mapper);
        List<Long> deviations = new ArrayList<>();
        this.generator.accept((element, index) -> {
            long value = mapper.apply(element);
            deviations.add(Math.abs(value - median));
        }, (element) -> false);
        if (deviations.isEmpty()) {
            return 0L;
        }
        Collections.sort(deviations);
        int size = deviations.size();
        if (size % 2 == 0) {
            return (deviations.get(size / 2 - 1) + deviations.get(size / 2)) / 2;
        } else {
            return deviations.get(size / 2);
        }
    }

    @Override
    public Long coefficientOfVariation() {
        if (this.isEmpty()) {
            return 0L;
        }
        double mean = this.mean();
        double stdDev = this.standardDeviation();
        if (mean == 0) {
            return 0L;
        }
        return (long) (stdDev / mean);
    }

    @Override
    public Long coefficientOfVariation(Function<E, Long> mapper) {
        if (this.isEmpty()) {
            return 0L;
        }
        double mean = this.mean(mapper);
        double stdDev = this.standardDeviation(mapper);
        if (mean == 0) {
            return 0L;
        }
        return (long) (stdDev / mean);
    }

    @Override
    public Long quartile1() {
        List<Long> quartiles = this.quartiles();
        return quartiles.isEmpty() ? 0L : quartiles.get(0);
    }

    @Override
    public Long quartile1(Function<E, Long> mapper) {
        List<Long> quartiles = this.quartiles(mapper);
        return quartiles.isEmpty() ? 0L : quartiles.get(0);
    }

    @Override
    public HashMap<Long, Long> frequency() {
        HashMap<Long, Long> freqMap = new HashMap<>();
        this.generator.accept((element, index) -> {
            if (element instanceof Number) {
                long value = ((Number) element).longValue();
                freqMap.merge(value, 1L, Long::sum);
            }
        }, (element) -> false);
        return freqMap;
    }

    @Override
    public HashMap<Long, Long> frequency(Function<E, Long> mapper) {
        HashMap<Long, Long> freqMap = new HashMap<>();
        this.generator.accept((element, index) -> {
            long value = mapper.apply(element);
            freqMap.merge(value, 1L, Long::sum);
        }, (element) -> false);
        return freqMap;
    }

    @Override
    public Long summate() {
        if (this.isEmpty()) {
            return 0L;
        }
        return this.collect(
                () -> 0L,
                (result, element, index) -> {
                    if (element instanceof Number) {
                        return result + ((Number) element).longValue();
                    }
                    return result;
                },
                Long::sum,
                result -> result
        );
    }

    @Override
    public Long summate(Function<E, Long> mapper) {
        return this.collect(
                () -> 0L,
                (result, element, index) -> {
                    long value = mapper.apply(element);
                    return result + value;
                },
                Long::sum,
                result -> result
        );
    }

    @Override
    public List<Long> quartiles() {
        if (this.isEmpty()) return List.of();
        List<Long> values = new ArrayList<>();
        this.generator.accept((element, index) -> {
            if (element instanceof Number) {
                values.add(((Number) element).longValue());
            }
        }, (element) -> false);
        if (values.size() < 4) return List.of();
        Collections.sort(values);
        int size = values.size();
        long q1 = values.get(size / 4);
        long q2 = values.get(size / 2);
        long q3 = values.get(3 * size / 4);
        return List.of(q1, q2, q3);
    }

    @Override
    public List<Long> quartiles(Function<E, Long> mapper) {
        if (this.isEmpty()) return List.of();
        List<Long> values = new ArrayList<>();
        this.generator.accept((element, index) -> {
            values.add(mapper.apply(element));
        }, (element) -> false);
        if (values.size() < 4) return List.of();
        Collections.sort(values);
        int size = values.size();
        long q1 = values.get(size / 4);
        long q2 = values.get(size / 2);
        long q3 = values.get(3 * size / 4);
        return List.of(q1, q2, q3);
    }

    @Override
    public Long interquartileRange() {
        List<Long> quartiles = this.quartiles();
        if (quartiles.size() < 3) {
            return 0L;
        }
        return quartiles.get(2) - quartiles.get(0);
    }

    @Override
    public Long interquartileRange(Function<E, Long> mapper) {
        List<Long> quartiles = this.quartiles(mapper);
        if (quartiles.size() < 3) {
            return 0L;
        }
        return quartiles.get(2) - quartiles.get(0);
    }

    @Override
    public Long skewness() {
        if (this.count() < 3) {
            return 0L;
        }
        double mean = this.mean();
        double stdDev = this.standardDeviation();
        if (stdDev == 0) {
            return 0L;
        }
        double sumCubedDiffs = this.collect(
                () -> 0.0,
                (result, element, index) -> {
                    if (element instanceof Number) {
                        double value = ((Number) element).longValue();
                        double diff = value - mean;
                        return result + (diff * diff * diff);
                    }
                    return result;
                },
                Double::sum,
                result -> result
        );
        return (long) ((sumCubedDiffs / this.count()) / Math.pow(stdDev, 3));
    }

    @Override
    public Long skewness(Function<E, Long> mapper) {
        if (this.count() < 3) {
            return 0L;
        }
        double mean = this.mean(mapper);
        double stdDev = this.standardDeviation(mapper);
        if (stdDev == 0) {
            return 0L;
        }
        double sumCubedDiffs = this.collect(
                () -> 0.0,
                (result, element, index) -> {
                    long value = mapper.apply(element);
                    double diff = value - mean;
                    return result + (diff * diff * diff);
                },
                Double::sum,
                result -> result
        );
        return (long) ((sumCubedDiffs / this.count()) / Math.pow(stdDev, 3));
    }

    @Override
    public boolean isEmpty() {
        return this.count() == 0;
    }
}