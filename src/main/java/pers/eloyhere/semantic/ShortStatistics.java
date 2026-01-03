package pers.eloyhere.semantic;

import java.util.*;
import java.util.function.Function;

public class ShortStatistics<E> extends Statistics<E, Short> {

    public ShortStatistics(Generator<E> generator) {
        super(generator);
    }

    public ShortStatistics(Generator<E> generator, Comparator<E> comparator) {
        super(generator, comparator);
    }

    public ShortStatistics(Generator<E> generator, long concurrent) {
        super(generator, concurrent);
    }

    public ShortStatistics(Generator<E> generator, long concurrent, Comparator<E> comparator) {
        super(generator, concurrent, comparator);
    }

    @Override
    public Short range() {
        if (this.isEmpty()) {
            return 0;
        }
        Optional<E> maximum = this.maximum();
        Optional<E> minimum = this.minimum();
        if (maximum.isPresent() && minimum.isPresent()) {
            return (short) ((Short) maximum.get() - (Short) minimum.get());
        }
        return 0;
    }

    @Override
    public Short range(Function<E, Short> mapper) {
        if (this.isEmpty()) {
            return 0;
        }
        Optional<Short> maximum = this.maximum().map(mapper);
        Optional<Short> minimum = this.minimum().map(mapper);
        if (maximum.isPresent() && minimum.isPresent()) {
            return (short) (maximum.get() - minimum.get());
        }
        return 0;
    }

    @Override
    public Short variance() {
        if (this.count() < 2) {
            return 0;
        }
        double mean = this.mean();
        double sumSquaredDiffs = this.collect(
                () -> 0.0,
                (result, element, index) -> {
                    if (element instanceof Number) {
                        double value = ((Number) element).shortValue();
                        double diff = value - mean;
                        return result + (diff * diff);
                    }
                    return result;
                },
                Double::sum,
                result -> result
        );
        return (short) (sumSquaredDiffs / (this.count() - 1));
    }

    @Override
    public Short variance(Function<E, Short> mapper) {
        if (this.count() < 2) {
            return 0;
        }
        double mean = this.mean(mapper);
        double sumSquaredDiffs = this.collect(
                () -> 0.0,
                (result, element, index) -> {
                    short value = mapper.apply(element);
                    double diff = value - mean;
                    return result + (diff * diff);
                },
                Double::sum,
                result -> result
        );
        return (short) (sumSquaredDiffs / (this.count() - 1));
    }

    @Override
    public Short standardDeviation() {
        if (this.isEmpty()) {
            return 0;
        }
        Optional<E> maximum = this.maximum();
        Optional<E> minimum = this.minimum();
        if (maximum.isPresent() && minimum.isPresent()) {
            double mean = this.mean();
            double sumSquaredDiffs = this.collect(
                    () -> 0.0,
                    (result, element, index) -> {
                        if (element instanceof Number) {
                            double value = ((Number) element).shortValue();
                            double diff = value - mean;
                            return result + (diff * diff);
                        }
                        return result;
                    },
                    Double::sum,
                    result -> result
            );
            return (short) Math.sqrt(sumSquaredDiffs / (this.count() - 1));
        }
        return 0;
    }

    @Override
    public Short standardDeviation(Function<E, Short> mapper) {
        if (this.count() < 2) {
            return 0;
        }
        Optional<E> maximum = this.maximum();
        Optional<E> minimum = this.minimum();
        if (maximum.isPresent() && minimum.isPresent()) {
            double mean = this.mean(mapper);
            double sumSquaredDiffs = this.collect(
                    () -> 0.0,
                    (result, element, index) -> {
                        short value = mapper.apply(element);
                        double diff = value - mean;
                        return result + (diff * diff);
                    },
                    Double::sum,
                    result -> result
            );
            return (short) Math.sqrt(sumSquaredDiffs / (this.count() - 1));
        }
        return 0;
    }

    @Override
    public Short mean() {
        if (this.isEmpty()) {
            return 0;
        }
        Optional<E> maximum = this.maximum();
        Optional<E> minimum = this.minimum();
        if (maximum.isPresent() && minimum.isPresent()) {
            return this.collect(
                    () -> new double[]{0.0, 0.0},
                    (result, element, index) -> {
                        if (element instanceof Number) {
                            result[0] += ((Number) element).shortValue();
                            result[1] += 1.0;
                        }
                        return result;
                    },
                    (result1, result2) -> {
                        result1[0] += result2[0];
                        result1[1] += result2[1];
                        return result1;
                    },
                    result -> result[1] > 0 ? (short) (result[0] / result[1]) : 0
            );
        }
        return 0;
    }

    @Override
    public Short mean(Function<E, Short> mapper) {
        if (this.isEmpty()) return 0;
        Optional<E> maximum = this.maximum();
        Optional<E> minimum = this.minimum();
        if (maximum.isPresent() && minimum.isPresent()) {
            return this.collect(
                    () -> new double[]{0.0, 0.0},
                    (result, element, index) -> {
                        short value = mapper.apply(element);
                        result[0] += value;
                        result[1] += 1.0;
                        return result;
                    },
                    (result1, result2) -> {
                        result1[0] += result2[0];
                        result1[1] += result2[1];
                        return result1;
                    },
                    result -> result[1] > 0 ? (short) (result[0] / result[1]) : 0
            );
        }
        return 0;
    }

    @Override
    public Short median() {
        if (this.isEmpty()) return 0;
        Optional<E> maximum = this.maximum();
        Optional<E> minimum = this.minimum();
        if (maximum.isPresent() && minimum.isPresent()) {
            List<Short> values = new ArrayList<>();
            this.generator.accept((element, index) -> {
                if (element instanceof Number) {
                    values.add(((Number) element).shortValue());
                }
            }, (element) -> false);
            if (values.isEmpty()) return 0;
            Collections.sort(values);
            int size = values.size();
            if (size % 2 == 0) {
                return (short) ((values.get(size / 2 - 1) + values.get(size / 2)) / 2);
            } else {
                return values.get(size / 2);
            }
        }
        return 0;
    }

    @Override
    public Short median(Function<E, Short> mapper) {
        if (this.isEmpty()) {
            return 0;
        }
        Optional<E> maximum = this.maximum();
        Optional<E> minimum = this.minimum();
        if (maximum.isPresent() && minimum.isPresent()) {
            List<Short> values = new ArrayList<>();
            this.generator.accept((element, index) -> {
                values.add(mapper.apply(element));
            }, (element) -> false);
            if (values.isEmpty()) {
                return 0;
            }
            Collections.sort(values);
            int size = values.size();
            if (size % 2 == 0) {
                return (short) ((values.get(size / 2 - 1) + values.get(size / 2)) / 2);
            } else {
                return values.get(size / 2);
            }
        }
        return 0;
    }

    @Override
    public Short mode() {
        if (this.isEmpty()) {
            return 0;
        }
        HashMap<Short, Long> freq = this.frequency();
        if (freq.isEmpty()) {
            return 0;
        }
        return freq.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse((short) 0);
    }

    @Override
    public Short mode(Function<E, Short> mapper) {
        if (this.isEmpty()) {
            return 0;
        }
        HashMap<Short, Long> freq = this.frequency(mapper);
        if (freq.isEmpty()) {
            return 0;
        }
        return freq.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse((short) 0);
    }

    @Override
    public Short product() {
        if (this.isEmpty()) {
            return 1;
        }
        Optional<E> maximum = this.maximum();
        if (maximum.isPresent()) {
            return this.collect(
                    () -> 1,
                    (result, element, index) -> {
                        if (element instanceof Number) {
                            return ((result * ((Number) element).shortValue()));
                        }
                        return result;
                    },
                    (a, b) ->  (a * b),
                    result -> result
            ).shortValue();
        }
        return 1;
    }

    @Override
    public Short product(Function<E, Short> mapper) {
        if (this.isEmpty()) {
            return 1;
        }
        Optional<E> maximum = this.maximum();
        if (maximum.isPresent()) {
            return this.collect(
                    () -> 1,
                    (result, element, index) -> (result * mapper.apply(element)),
                    (a, b) -> (a * b),
                    result -> result
            ).shortValue();
        }
        return 1;
    }

    @Override
    public Short geometricMean() {
        if (this.isEmpty()) {
            return 0;
        }
        Optional<E> maximum = this.maximum();
        if (maximum.isPresent()) {
            List<Short> values = new ArrayList<>();
            this.generator.accept((element, index) -> {
                if (element instanceof Number) {
                    short value = ((Number) element).shortValue();
                    if (value > 0) {
                        values.add(value);
                    }
                }
            }, (element) -> false);
            if (values.isEmpty()) {
                return 0;
            }
            double product = 1.0;
            for (short value : values) {
                product *= value;
            }
            return (short) Math.pow(product, 1.0 / values.size());
        }
        return 0;
    }

    @Override
    public Short geometricMean(Function<E, Short> mapper) {
        if (this.isEmpty()) {
            return 0;
        }
        Optional<E> maximum = this.maximum();
        if (maximum.isPresent()) {
            List<Short> values = new ArrayList<>();
            this.generator.accept((element, index) -> {
                short value = mapper.apply(element);
                if (value > 0) {
                    values.add(value);
                }
            }, (element) -> false);
            if (values.isEmpty()) {
                return 0;
            }
            double product = 1.0;
            for (short value : values) {
                product *= value;
            }
            return (short) Math.pow(product, 1.0 / values.size());
        }
        return 0;
    }

    @Override
    public Short harmonicMean() {
        if (this.isEmpty()) {
            return 0;
        }
        Optional<E> maximum = this.maximum();
        if (maximum.isPresent()) {
            return this.collect(
                    () -> new double[]{0.0, 0.0},
                    (result, element, index) -> {
                        if (element instanceof Number) {
                            short value = ((Number) element).shortValue();
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
                    result -> result[1] > 0 ? (short) (result[1] / result[0]) : 0
            );
        }
        return 0;
    }

    @Override
    public Short harmonicMean(Function<E, Short> mapper) {
        if (this.isEmpty()) {
            return 0;
        }
        Optional<E> maximum = this.maximum();
        if (maximum.isPresent()) {
            return this.collect(
                    () -> new double[]{0.0, 0.0},
                    (result, element, index) -> {
                        short value = mapper.apply(element);
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
                    result -> result[1] > 0 ? (short) (result[1] / result[0]) : 0
            );
        }
        return 0;
    }

    @Override
    public Short medianAbsoluteDeviation() {
        if (this.isEmpty()) {
            return 0;
        }
        short median = this.median();
        List<Short> deviations = new ArrayList<>();
        this.generator.accept((element, index) -> {
            if (element instanceof Number) {
                short value = ((Number) element).shortValue();
                deviations.add((short) Math.abs(value - median));
            }
        }, (element) -> false);
        if (deviations.isEmpty()) {
            return 0;
        }
        Collections.sort(deviations);
        int size = deviations.size();
        if (size % 2 == 0) {
            return (short) ((deviations.get(size / 2 - 1) + deviations.get(size / 2)) / 2);
        } else {
            return deviations.get(size / 2);
        }
    }

    @Override
    public Short medianAbsoluteDeviation(Function<E, Short> mapper) {
        if (this.isEmpty()) {
            return 0;
        }
        short median = this.median(mapper);
        List<Short> deviations = new ArrayList<>();
        this.generator.accept((element, index) -> {
            short value = mapper.apply(element);
            deviations.add((short) Math.abs(value - median));
        }, (element) -> false);
        if (deviations.isEmpty()) {
            return 0;
        }
        Collections.sort(deviations);
        int size = deviations.size();
        if (size % 2 == 0) {
            return (short) ((deviations.get(size / 2 - 1) + deviations.get(size / 2)) / 2);
        } else {
            return deviations.get(size / 2);
        }
    }

    @Override
    public Short coefficientOfVariation() {
        if (this.isEmpty()) {
            return 0;
        }
        double mean = this.mean();
        double stdDev = this.standardDeviation();
        if (mean == 0) {
            return 0;
        }
        return (short) (stdDev / mean);
    }

    @Override
    public Short coefficientOfVariation(Function<E, Short> mapper) {
        if (this.isEmpty()) {
            return 0;
        }
        double mean = this.mean(mapper);
        double stdDev = this.standardDeviation(mapper);
        if (mean == 0) {
            return 0;
        }
        return (short) (stdDev / mean);
    }

    @Override
    public Short quartile1() {
        List<Short> quartiles = this.quartiles();
        return quartiles.isEmpty() ? 0 : quartiles.get(0);
    }

    @Override
    public Short quartile1(Function<E, Short> mapper) {
        List<Short> quartiles = this.quartiles(mapper);
        return quartiles.isEmpty() ? 0 : quartiles.get(0);
    }

    @Override
    public HashMap<Short, Long> frequency() {
        HashMap<Short, Long> freqMap = new HashMap<>();
        this.generator.accept((element, index) -> {
            if (element instanceof Number) {
                short value = ((Number) element).shortValue();
                freqMap.merge(value, 1L, Long::sum);
            }
        }, (element) -> false);
        return freqMap;
    }

    @Override
    public HashMap<Short, Long> frequency(Function<E, Short> mapper) {
        HashMap<Short, Long> freqMap = new HashMap<>();
        this.generator.accept((element, index) -> {
            short value = mapper.apply(element);
            freqMap.merge(value, 1L, Long::sum);
        }, (element) -> false);
        return freqMap;
    }

    @Override
    public Short summate() {
        if (this.isEmpty()) {
            return 0;
        }
        return this.collect(
                () -> 0,
                (result, element, index) -> {
                    if (element instanceof Number) {
                        return (Integer) (result + ((Number) element).shortValue());
                    }
                    return result;
                },
                Integer::sum,
                result -> result
        ).shortValue();
    }

    @Override
    public Short summate(Function<E, Short> mapper) {
        return this.collect(
                () -> 0,
                (result, element, index) -> {
                    short value = mapper.apply(element);
                    return (result + value);
                },
                Integer::sum,
                result -> result
        ).shortValue();
    }

    @Override
    public List<Short> quartiles() {
        if (this.isEmpty()) return List.of();
        List<Short> values = new ArrayList<>();
        this.generator.accept((element, index) -> {
            if (element instanceof Number) {
                values.add(((Number) element).shortValue());
            }
        }, (element) -> false);
        if (values.size() < 4) return List.of();
        Collections.sort(values);
        int size = values.size();
        short q1 = values.get(size / 4);
        short q2 = values.get(size / 2);
        short q3 = values.get(3 * size / 4);
        return List.of(q1, q2, q3);
    }

    @Override
    public List<Short> quartiles(Function<E, Short> mapper) {
        if (this.isEmpty()) return List.of();
        List<Short> values = new ArrayList<>();
        this.generator.accept((element, index) -> {
            values.add(mapper.apply(element));
        }, (element) -> false);
        if (values.size() < 4) return List.of();
        Collections.sort(values);
        int size = values.size();
        short q1 = values.get(size / 4);
        short q2 = values.get(size / 2);
        short q3 = values.get(3 * size / 4);
        return List.of(q1, q2, q3);
    }

    @Override
    public Short interquartileRange() {
        List<Short> quartiles = this.quartiles();
        if (quartiles.size() < 3) {
            return 0;
        }
        return (short) (quartiles.get(2) - quartiles.get(0));
    }

    @Override
    public Short interquartileRange(Function<E, Short> mapper) {
        List<Short> quartiles = this.quartiles(mapper);
        if (quartiles.size() < 3) {
            return 0;
        }
        return (short) (quartiles.get(2) - quartiles.get(0));
    }

    @Override
    public Short skewness() {
        if (this.count() < 3) {
            return 0;
        }
        double mean = this.mean();
        double stdDev = this.standardDeviation();
        if (stdDev == 0) {
            return 0;
        }
        double sumCubedDiffs = this.collect(
                () -> 0.0,
                (result, element, index) -> {
                    if (element instanceof Number) {
                        double value = ((Number) element).shortValue();
                        double diff = value - mean;
                        return result + (diff * diff * diff);
                    }
                    return result;
                },
                Double::sum,
                result -> result
        );
        return (short) ((sumCubedDiffs / this.count()) / Math.pow(stdDev, 3));
    }

    @Override
    public Short skewness(Function<E, Short> mapper) {
        if (this.count() < 3) {
            return 0;
        }
        double mean = this.mean(mapper);
        double stdDev = this.standardDeviation(mapper);
        if (stdDev == 0) {
            return 0;
        }
        double sumCubedDiffs = this.collect(
                () -> 0.0,
                (result, element, index) -> {
                    short value = mapper.apply(element);
                    double diff = value - mean;
                    return result + (diff * diff * diff);
                },
                Double::sum,
                result -> result
        );
        return (short) ((sumCubedDiffs / this.count()) / Math.pow(stdDev, 3));
    }

    @Override
    public boolean isEmpty() {
        return this.count() == 0;
    }
}