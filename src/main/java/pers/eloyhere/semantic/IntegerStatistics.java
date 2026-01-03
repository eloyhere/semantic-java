package pers.eloyhere.semantic;

import java.util.*;
import java.util.function.Function;

public class IntegerStatistics<E> extends Statistics<E, Integer> {

    public IntegerStatistics(Generator<E> generator) {
        super(generator);
    }

    public IntegerStatistics(Generator<E> generator, Comparator<E> comparator) {
        super(generator, comparator);
    }

    public IntegerStatistics(Generator<E> generator, long concurrent) {
        super(generator, concurrent);
    }

    public IntegerStatistics(Generator<E> generator, long concurrent, Comparator<E> comparator) {
        super(generator, concurrent, comparator);
    }

    @Override
    public Integer range() {
        if (this.isEmpty()) {
            return 0;
        }
        Optional<E> maximum = this.maximum();
        Optional<E> minimum = this.minimum();
        if (maximum.isPresent() && minimum.isPresent()) {
            return (Integer) maximum.get() - (Integer) minimum.get();
        }
        return 0;
    }

    @Override
    public Integer range(Function<E, Integer> mapper) {
        if (this.isEmpty()) {
            return 0;
        }
        Optional<Integer> maximum = this.maximum().map(mapper);
        Optional<Integer> minimum = this.minimum().map(mapper);
        if (maximum.isPresent() && minimum.isPresent()) {
            return maximum.get() - minimum.get();
        }
        return 0;
    }

    @Override
    public Integer variance() {
        if (this.count() < 2) {
            return 0;
        }
        double mean = this.mean();
        double sumSquaredDiffs = this.collect(
                () -> 0.0,
                (result, element, index) -> {
                    if (element instanceof Number) {
                        double value = ((Number) element).intValue();
                        double diff = value - mean;
                        return result + (diff * diff);
                    }
                    return result;
                },
                Double::sum,
                result -> result
        );
        return (int) (sumSquaredDiffs / (this.count() - 1));
    }

    @Override
    public Integer variance(Function<E, Integer> mapper) {
        if (this.count() < 2) {
            return 0;
        }
        double mean = this.mean(mapper);
        double sumSquaredDiffs = this.collect(
                () -> 0.0,
                (result, element, index) -> {
                    int value = mapper.apply(element);
                    double diff = value - mean;
                    return result + (diff * diff);
                },
                Double::sum,
                result -> result
        );
        return (int) (sumSquaredDiffs / (this.count() - 1));
    }

    @Override
    public Integer standardDeviation() {
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
                            double value = ((Number) element).intValue();
                            double diff = value - mean;
                            return result + (diff * diff);
                        }
                        return result;
                    },
                    Double::sum,
                    result -> result
            );
            return (int) Math.sqrt(sumSquaredDiffs / (this.count() - 1));
        }
        return 0;
    }

    @Override
    public Integer standardDeviation(Function<E, Integer> mapper) {
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
                        int value = mapper.apply(element);
                        double diff = value - mean;
                        return result + (diff * diff);
                    },
                    Double::sum,
                    result -> result
            );
            return (int) Math.sqrt(sumSquaredDiffs / (this.count() - 1));
        }
        return 0;
    }

    @Override
    public Integer mean() {
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
                            result[0] += ((Number) element).intValue();
                            result[1] += 1.0;
                        }
                        return result;
                    },
                    (result1, result2) -> {
                        result1[0] += result2[0];
                        result1[1] += result2[1];
                        return result1;
                    },
                    result -> result[1] > 0 ? (int) (result[0] / result[1]) : 0
            );
        }
        return 0;
    }

    @Override
    public Integer mean(Function<E, Integer> mapper) {
        if (this.isEmpty()) return 0;
        Optional<E> maximum = this.maximum();
        Optional<E> minimum = this.minimum();
        if (maximum.isPresent() && minimum.isPresent()) {
            return this.collect(
                    () -> new double[]{0.0, 0.0},
                    (result, element, index) -> {
                        int value = mapper.apply(element);
                        result[0] += value;
                        result[1] += 1.0;
                        return result;
                    },
                    (result1, result2) -> {
                        result1[0] += result2[0];
                        result1[1] += result2[1];
                        return result1;
                    },
                    result -> result[1] > 0 ? (int) (result[0] / result[1]) : 0
            );
        }
        return 0;
    }

    @Override
    public Integer median() {
        if (this.isEmpty()) return 0;
        Optional<E> maximum = this.maximum();
        Optional<E> minimum = this.minimum();
        if (maximum.isPresent() && minimum.isPresent()) {
            List<Integer> values = new ArrayList<>();
            this.generator.accept((element, index) -> {
                if (element instanceof Number) {
                    values.add(((Number) element).intValue());
                }
            }, (element) -> false);
            if (values.isEmpty()) return 0;
            Collections.sort(values);
            int size = values.size();
            if (size % 2 == 0) {
                return (values.get(size / 2 - 1) + values.get(size / 2)) / 2;
            } else {
                return values.get(size / 2);
            }
        }
        return 0;
    }

    @Override
    public Integer median(Function<E, Integer> mapper) {
        if (this.isEmpty()) {
            return 0;
        }
        Optional<E> maximum = this.maximum();
        Optional<E> minimum = this.minimum();
        if (maximum.isPresent() && minimum.isPresent()) {
            List<Integer> values = new ArrayList<>();
            this.generator.accept((element, index) -> {
                values.add(mapper.apply(element));
            }, (element) -> false);
            if (values.isEmpty()) {
                return 0;
            }
            Collections.sort(values);
            int size = values.size();
            if (size % 2 == 0) {
                return (values.get(size / 2 - 1) + values.get(size / 2)) / 2;
            } else {
                return values.get(size / 2);
            }
        }
        return 0;
    }

    @Override
    public Integer mode() {
        if (this.isEmpty()) {
            return 0;
        }
        HashMap<Integer, Long> freq = this.frequency();
        if (freq.isEmpty()) {
            return 0;
        }
        return freq.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(0);
    }

    @Override
    public Integer mode(Function<E, Integer> mapper) {
        if (this.isEmpty()) {
            return 0;
        }
        HashMap<Integer, Long> freq = this.frequency(mapper);
        if (freq.isEmpty()) {
            return 0;
        }
        return freq.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(0);
    }

    @Override
    public Integer product() {
        if (this.isEmpty()) {
            return 1;
        }
        Optional<E> maximum = this.maximum();
        if (maximum.isPresent()) {
            return this.collect(
                    () -> 1,
                    (result, element, index) -> {
                        if (element instanceof Number) {
                            return result * ((Number) element).intValue();
                        }
                        return result;
                    },
                    (a, b) -> a * b,
                    result -> result
            );
        }
        return 1;
    }

    @Override
    public Integer product(Function<E, Integer> mapper) {
        if (this.isEmpty()) {
            return 1;
        }
        Optional<E> maximum = this.maximum();
        if (maximum.isPresent()) {
            return this.collect(
                    () -> 1,
                    (result, element, index) -> {
                        return result * mapper.apply(element);
                    },
                    (a, b) -> a * b,
                    result -> result
            );
        }
        return 1;
    }

    @Override
    public Integer geometricMean() {
        if (this.isEmpty()) {
            return 0;
        }
        Optional<E> maximum = this.maximum();
        if (maximum.isPresent()) {
            List<Integer> values = new ArrayList<>();
            this.generator.accept((element, index) -> {
                if (element instanceof Number) {
                    int value = ((Number) element).intValue();
                    if (value > 0) {
                        values.add(value);
                    }
                }
            }, (element) -> false);
            if (values.isEmpty()) {
                return 0;
            }
            double product = 1.0;
            for (int value : values) {
                product *= value;
            }
            return (int) Math.pow(product, 1.0 / values.size());
        }
        return 0;
    }

    @Override
    public Integer geometricMean(Function<E, Integer> mapper) {
        if (this.isEmpty()) {
            return 0;
        }
        Optional<E> maximum = this.maximum();
        if (maximum.isPresent()) {
            List<Integer> values = new ArrayList<>();
            this.generator.accept((element, index) -> {
                int value = mapper.apply(element);
                if (value > 0) {
                    values.add(value);
                }
            }, (element) -> false);
            if (values.isEmpty()) {
                return 0;
            }
            double product = 1.0;
            for (int value : values) {
                product *= value;
            }
            return (int) Math.pow(product, 1.0 / values.size());
        }
        return 0;
    }

    @Override
    public Integer harmonicMean() {
        if (this.isEmpty()) {
            return 0;
        }
        Optional<E> maximum = this.maximum();
        if (maximum.isPresent()) {
            return this.collect(
                    () -> new double[]{0.0, 0.0},
                    (result, element, index) -> {
                        if (element instanceof Number) {
                            int value = ((Number) element).intValue();
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
                    result -> result[1] > 0 ? (int) (result[1] / result[0]) : 0
            );
        }
        return 0;
    }

    @Override
    public Integer harmonicMean(Function<E, Integer> mapper) {
        if (this.isEmpty()) {
            return 0;
        }
        Optional<E> maximum = this.maximum();
        if (maximum.isPresent()) {
            return this.collect(
                    () -> new double[]{0.0, 0.0},
                    (result, element, index) -> {
                        int value = mapper.apply(element);
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
                    result -> result[1] > 0 ? (int) (result[1] / result[0]) : 0
            );
        }
        return 0;
    }

    @Override
    public Integer medianAbsoluteDeviation() {
        if (this.isEmpty()) {
            return 0;
        }
        int median = this.median();
        List<Integer> deviations = new ArrayList<>();
        this.generator.accept((element, index) -> {
            if (element instanceof Number) {
                int value = ((Number) element).intValue();
                deviations.add(Math.abs(value - median));
            }
        }, (element) -> false);
        if (deviations.isEmpty()) {
            return 0;
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
    public Integer medianAbsoluteDeviation(Function<E, Integer> mapper) {
        if (this.isEmpty()) {
            return 0;
        }
        int median = this.median(mapper);
        List<Integer> deviations = new ArrayList<>();
        this.generator.accept((element, index) -> {
            int value = mapper.apply(element);
            deviations.add(Math.abs(value - median));
        }, (element) -> false);
        if (deviations.isEmpty()) {
            return 0;
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
    public Integer coefficientOfVariation() {
        if (this.isEmpty()) {
            return 0;
        }
        double mean = this.mean();
        double stdDev = this.standardDeviation();
        if (mean == 0) {
            return 0;
        }
        return (int) (stdDev / mean);
    }

    @Override
    public Integer coefficientOfVariation(Function<E, Integer> mapper) {
        if (this.isEmpty()) {
            return 0;
        }
        double mean = this.mean(mapper);
        double stdDev = this.standardDeviation(mapper);
        if (mean == 0) {
            return 0;
        }
        return (int) (stdDev / mean);
    }

    @Override
    public Integer quartile1() {
        List<Integer> quartiles = this.quartiles();
        return quartiles.isEmpty() ? 0 : quartiles.get(0);
    }

    @Override
    public Integer quartile1(Function<E, Integer> mapper) {
        List<Integer> quartiles = this.quartiles(mapper);
        return quartiles.isEmpty() ? 0 : quartiles.get(0);
    }

    @Override
    public HashMap<Integer, Long> frequency() {
        HashMap<Integer, Long> freqMap = new HashMap<>();
        this.generator.accept((element, index) -> {
            if (element instanceof Number) {
                int value = ((Number) element).intValue();
                freqMap.merge(value, 1L, Long::sum);
            }
        }, (element) -> false);
        return freqMap;
    }

    @Override
    public HashMap<Integer, Long> frequency(Function<E, Integer> mapper) {
        HashMap<Integer, Long> freqMap = new HashMap<>();
        this.generator.accept((element, index) -> {
            int value = mapper.apply(element);
            freqMap.merge(value, 1L, Long::sum);
        }, (element) -> false);
        return freqMap;
    }

    @Override
    public Integer summate() {
        if (this.isEmpty()) {
            return 0;
        }
        return this.collect(
                () -> 0,
                (result, element, index) -> {
                    if (element instanceof Number) {
                        return result + ((Number) element).intValue();
                    }
                    return result;
                },
                Integer::sum,
                result -> result
        );
    }

    @Override
    public Integer summate(Function<E, Integer> mapper) {
        return this.collect(
                () -> 0,
                (result, element, index) -> {
                    int value = mapper.apply(element);
                    return result + value;
                },
                Integer::sum,
                result -> result
        );
    }

    @Override
    public List<Integer> quartiles() {
        if (this.isEmpty()) return List.of();
        List<Integer> values = new ArrayList<>();
        this.generator.accept((element, index) -> {
            if (element instanceof Number) {
                values.add(((Number) element).intValue());
            }
        }, (element) -> false);
        if (values.size() < 4) return List.of();
        Collections.sort(values);
        int size = values.size();
        int q1 = values.get(size / 4);
        int q2 = values.get(size / 2);
        int q3 = values.get(3 * size / 4);
        return List.of(q1, q2, q3);
    }

    @Override
    public List<Integer> quartiles(Function<E, Integer> mapper) {
        if (this.isEmpty()) return List.of();
        List<Integer> values = new ArrayList<>();
        this.generator.accept((element, index) -> {
            values.add(mapper.apply(element));
        }, (element) -> false);
        if (values.size() < 4) return List.of();
        Collections.sort(values);
        int size = values.size();
        int q1 = values.get(size / 4);
        int q2 = values.get(size / 2);
        int q3 = values.get(3 * size / 4);
        return List.of(q1, q2, q3);
    }

    @Override
    public Integer interquartileRange() {
        List<Integer> quartiles = this.quartiles();
        if (quartiles.size() < 3) {
            return 0;
        }
        return quartiles.get(2) - quartiles.get(0);
    }

    @Override
    public Integer interquartileRange(Function<E, Integer> mapper) {
        List<Integer> quartiles = this.quartiles(mapper);
        if (quartiles.size() < 3) {
            return 0;
        }
        return quartiles.get(2) - quartiles.get(0);
    }

    @Override
    public Integer skewness() {
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
                        double value = ((Number) element).intValue();
                        double diff = value - mean;
                        return result + (diff * diff * diff);
                    }
                    return result;
                },
                Double::sum,
                result -> result
        );
        return (int) ((sumCubedDiffs / this.count()) / Math.pow(stdDev, 3));
    }

    @Override
    public Integer skewness(Function<E, Integer> mapper) {
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
                    int value = mapper.apply(element);
                    double diff = value - mean;
                    return result + (diff * diff * diff);
                },
                Double::sum,
                result -> result
        );
        return (int) ((sumCubedDiffs / this.count()) / Math.pow(stdDev, 3));
    }

    @Override
    public boolean isEmpty() {
        return this.count() == 0;
    }
}