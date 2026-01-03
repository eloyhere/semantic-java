package pers.eloyhere.semantic;

import java.util.*;
import java.util.function.Function;

public class DoubleStatistics<E> extends Statistics<E, Double> {

    public DoubleStatistics(Generator<E> generator) {
        super(generator);
    }

    public DoubleStatistics(Generator<E> generator, Comparator<E> comparator) {
        super(generator, comparator);
    }

    public DoubleStatistics(Generator<E> generator, long concurrent) {
        super(generator, concurrent);
    }

    public DoubleStatistics(Generator<E> generator, long concurrent, Comparator<E> comparator) {
        super(generator, concurrent, comparator);
    }

    @Override
    public Double range() {
        if (this.isEmpty()) {
            return 0.0;
        }
        Optional<E> maximum = this.maximum();
        Optional<E> minimum = this.minimum();
        if (maximum.isPresent() && minimum.isPresent()) {
            return ((Number) maximum.get()).doubleValue() - ((Number) minimum.get()).doubleValue();
        }
        return 0.0;
    }

    @Override
    public Double range(Function<E, Double> mapper) {
        if (this.isEmpty()) {
            return 0.0;
        }
        Optional<Double> maximum = this.maximum().map(mapper);
        Optional<Double> minimum = this.minimum().map(mapper);
        if (maximum.isPresent() && minimum.isPresent()) {
            return maximum.get() - minimum.get();
        }
        return 0.0;
    }

    @Override
    public Double variance() {
        if (this.count() < 2) {
            return 0.0;
        }
        double mean = this.mean();
        double sumSquaredDiffs = this.collect(
                () -> 0.0,
                (result, element, index) -> {
                    if (element instanceof Number) {
                        double value = ((Number) element).doubleValue();
                        double diff = value - mean;
                        return result + (diff * diff);
                    }
                    return result;
                },
                Double::sum,
                result -> result
        );
        return sumSquaredDiffs / (this.count() - 1);
    }

    @Override
    public Double variance(Function<E, Double> mapper) {
        if (this.count() < 2) {
            return 0.0;
        }
        double mean = this.mean(mapper);
        double sumSquaredDiffs = this.collect(
                () -> 0.0,
                (result, element, index) -> {
                    if (element instanceof Number) {
                        double value = ((Number) element).doubleValue();
                        double diff = value - mean;
                        return result + (diff * diff);
                    }
                    return result;
                },
                Double::sum,
                result -> result
        );
        return sumSquaredDiffs / (this.count() - 1);
    }

    @Override
    public Double standardDeviation() {
        if (this.isEmpty()) {
            return 0.0;
        }
        Optional<E> maximum = this.maximum();
        Optional<E> minimum = this.minimum();
        if (maximum.isPresent() && minimum.isPresent()) {
            double mean = this.mean();
            double sumSquaredDiffs = this.collect(
                    () -> 0.0,
                    (result, element, index) -> {
                        if (element instanceof Number) {
                            double value = ((Number) element).doubleValue();
                            double diff = value - mean;
                            return result + (diff * diff);
                        }
                        return result;
                    },
                    Double::sum,
                    result -> result
            );
            return Math.sqrt(sumSquaredDiffs / (this.count() - 1));
        }
        return 0.0;
    }

    @Override
    public Double standardDeviation(Function<E, Double> mapper) {
        if (this.count() < 2) {
            return 0.0;
        }
        Optional<E> maximum = this.maximum();
        Optional<E> minimum = this.minimum();
        if (maximum.isPresent() && minimum.isPresent()) {
            double mean = this.mean(mapper);
            double sumSquaredDiffs = this.collect(
                    () -> 0.0,
                    (result, element, index) -> {
                        double value = mapper.apply(element);
                        double diff = value - mean;
                        return result + (diff * diff);
                    },
                    Double::sum,
                    result -> result
            );
            return Math.sqrt(sumSquaredDiffs / (this.count() - 1));
        }
        return 0.0;
    }

    @Override
    public Double mean() {
        if (this.isEmpty()) {
            return 0.0;
        }
        Optional<E> maximum = this.maximum();
        Optional<E> minimum = this.minimum();
        if (maximum.isPresent() && minimum.isPresent()) {
            return this.collect(
                    () -> new double[]{0.0, 0.0},
                    (result, element, index) -> {
                        if (element instanceof Number) {
                            result[0] += ((Number) element).doubleValue();
                            result[1] += 1.0;
                        }
                        return result;
                    },
                    (result1, result2) -> {
                        result1[0] += result2[0];
                        result1[1] += result2[1];
                        return result1;
                    },
                    result -> result[1] > 0 ? result[0] / result[1] : 0.0
            );
        }
        return 0.0;
    }

    @Override
    public Double mean(Function<E, Double> mapper) {
        if (this.isEmpty()) return 0.0;
        Optional<E> maximum = this.maximum();
        Optional<E> minimum = this.minimum();
        if (maximum.isPresent() && minimum.isPresent()) {
            return this.collect(
                    () -> new double[]{0.0, 0.0},
                    (result, element, index) -> {
                        double value = mapper.apply(element);
                        result[0] += value;
                        result[1] += 1.0;
                        return result;
                    },
                    (result1, result2) -> {
                        result1[0] += result2[0];
                        result1[1] += result2[1];
                        return result1;
                    },
                    result -> result[1] > 0 ? result[0] / result[1] : 0.0
            );
        }
        return 0.0;
    }

    @Override
    public Double median() {
        if (this.isEmpty()) return 0.0;
        Optional<E> maximum = this.maximum();
        Optional<E> minimum = this.minimum();
        if (maximum.isPresent() && minimum.isPresent()) {
            List<Double> values = new ArrayList<>();
            this.generator.accept((element, index) -> {
                if (element instanceof Number) {
                    values.add(((Number) element).doubleValue());
                }
            }, (element) -> false);
            if (values.isEmpty()) return 0.0;
            Collections.sort(values);
            int size = values.size();
            if (size % 2 == 0) {
                return (values.get(size / 2 - 1) + values.get(size / 2)) / 2.0;
            } else {
                return values.get(size / 2);
            }
        }
        return 0.0;
    }

    @Override
    public Double median(Function<E, Double> mapper) {
        if (this.isEmpty()) {
            return 0.0;
        }
        Optional<E> maximum = this.maximum();
        Optional<E> minimum = this.minimum();
        if (maximum.isPresent() && minimum.isPresent()) {
            List<Double> values = new ArrayList<>();
            this.generator.accept((element, index) -> {
                values.add(mapper.apply(element));
            }, (element) -> false);
            if (values.isEmpty()) {
                return 0.0;
            }
            Collections.sort(values);
            int size = values.size();
            if (size % 2 == 0) {
                return (values.get(size / 2 - 1) + values.get(size / 2)) / 2.0;
            } else {
                return values.get(size / 2);
            }
        }
        return 0.0;
    }

    @Override
    public Double mode() {
        if (this.isEmpty()) {
            return 0.0;
        }
        HashMap<Double, Long> freq = this.frequency();
        if (freq.isEmpty()) {
            return 0.0;
        }
        return freq.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(0.0);
    }

    @Override
    public Double mode(Function<E, Double> mapper) {
        if (this.isEmpty()) {
            return 0.0;
        }
        HashMap<Double, Long> freq = this.frequency(mapper);
        if (freq.isEmpty()) {
            return 0.0;
        }
        return freq.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(0.0);
    }

    @Override
    public Double product() {
        if (this.isEmpty()) {
            return 0.0;
        }
        Optional<E> maximum = this.maximum();
        if (maximum.isPresent()) {
            return this.collect(
                    () -> 1.0,
                    (result, element, index) -> {
                        if (element instanceof Number) {
                            return result * ((Number) element).doubleValue();
                        }
                        return result;
                    },
                    (a, b) -> a * b,
                    result -> result
            );
        }
        return 0.0;
    }

    @Override
    public Double product(Function<E, Double> mapper) {
        if (this.isEmpty()) {
            return 0.0;
        }
        Optional<E> maximum = this.maximum();
        if (maximum.isPresent()) {
            return this.collect(
                    () -> 1.0,
                    (result, element, index) -> {
                        return result * mapper.apply(element);
                    },
                    (a, b) -> a * b,
                    result -> result
            );
        }
        return 0.0;
    }

    @Override
    public Double geometricMean() {
        if (this.isEmpty()) {
            return 0.0;
        }
        Optional<E> maximum = this.maximum();
        if (maximum.isPresent()) {
            List<Double> values = new ArrayList<>();
            this.generator.accept((element, index) -> {
                if (element instanceof Number) {
                    double value = ((Number) element).doubleValue();
                    if (value > 0) {
                        values.add(value);
                    }
                }
            }, (element) -> false);
            if (values.isEmpty()) {
                return 0.0;
            }
            double product = 1.0;
            for (double value : values) {
                product *= value;
            }
            return Math.pow(product, 1.0 / values.size());
        }
        return 0.0;
    }

    @Override
    public Double geometricMean(Function<E, Double> mapper) {
        if (this.isEmpty()) {
            return 0.0;
        }
        Optional<E> maximum = this.maximum();
        if (maximum.isPresent()) {
            List<Double> values = new ArrayList<>();
            this.generator.accept((element, index) -> {
                double value = mapper.apply(element);
                if (value > 0) {
                    values.add(value);
                }
            }, (element) -> false);
            if (values.isEmpty()) {
                return 0.0;
            }
            double product = 1.0;
            for (double value : values) {
                product *= value;
            }
            return Math.pow(product, 1.0 / values.size());
        }
        return 0.0;
    }

    @Override
    public Double harmonicMean() {
        if (this.isEmpty()) {
            return 0.0;
        }
        Optional<E> maximum = this.maximum();
        if (maximum.isPresent()) {
            return this.collect(
                    () -> new double[]{0.0, 0.0},
                    (result, element, index) -> {
                        if (element instanceof Number) {
                            double value = ((Number) element).doubleValue();
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
                    result -> result[1] > 0 ? result[1] / result[0] : 0.0
            );
        }
        return 0.0;
    }

    @Override
    public Double harmonicMean(Function<E, Double> mapper) {
        if (this.isEmpty()) {
            return 0.0;
        }
        Optional<E> maximum = this.maximum();
        if (maximum.isPresent()) {
            return this.collect(
                    () -> new double[]{0.0, 0.0},
                    (result, element, index) -> {
                        double value = mapper.apply(element);
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
                    result -> result[1] > 0 ? result[1] / result[0] : 0.0
            );
        }
        return 0.0;
    }

    @Override
    public Double medianAbsoluteDeviation() {
        if (this.isEmpty()) {
            return 0.0;
        }
        double median = this.median();
        List<Double> deviations = new ArrayList<>();
        this.generator.accept((element, index) -> {
            if (element instanceof Number) {
                double value = ((Number) element).doubleValue();
                deviations.add(Math.abs(value - median));
            }
        }, (element) -> false);
        if (deviations.isEmpty()) {
            return 0.0;
        }
        Collections.sort(deviations);
        int size = deviations.size();
        if (size % 2 == 0) {
            return (deviations.get(size / 2 - 1) + deviations.get(size / 2)) / 2.0;
        } else {
            return deviations.get(size / 2);
        }
    }

    @Override
    public Double medianAbsoluteDeviation(Function<E, Double> mapper) {
        if (this.isEmpty()) {
            return 0.0;
        }
        double median = this.median(mapper);
        List<Double> deviations = new ArrayList<>();
        this.generator.accept((element, index) -> {
            double value = mapper.apply(element);
            deviations.add(Math.abs(value - median));
        }, (element) -> false);
        if (deviations.isEmpty()) {
            return 0.0;
        }
        Collections.sort(deviations);
        int size = deviations.size();
        if (size % 2 == 0) {
            return (deviations.get(size / 2 - 1) + deviations.get(size / 2)) / 2.0;
        } else {
            return deviations.get(size / 2);
        }
    }

    @Override
    public Double coefficientOfVariation() {
        if (this.isEmpty()) {
            return 0.0;
        }
        double mean = this.mean();
        double stdDev = this.standardDeviation();
        if (mean == 0) {
            return 0.0;
        }
        return stdDev / mean;
    }

    @Override
    public Double coefficientOfVariation(Function<E, Double> mapper) {
        if (this.isEmpty()) {
            return 0.0;
        }
        double mean = this.mean(mapper);
        double stdDev = this.standardDeviation(mapper);
        if (mean == 0) {
            return 0.0;
        }
        return stdDev / mean;
    }

    @Override
    public Double quartile1() {
        List<Double> quartiles = this.quartiles();
        return quartiles.isEmpty() ? 0.0 : quartiles.get(0);
    }

    @Override
    public Double quartile1(Function<E, Double> mapper) {
        List<Double> quartiles = this.quartiles(mapper);
        return quartiles.isEmpty() ? 0.0 : quartiles.get(0);
    }

    @Override
    public HashMap<Double, Long> frequency() {
        HashMap<Double, Long> freqMap = new HashMap<>();
        this.generator.accept((element, index) -> {
            if (element instanceof Number) {
                double value = ((Number) element).doubleValue();
                freqMap.merge(value, 1L, Long::sum);
            }
        }, (element) -> false);
        return freqMap;
    }

    @Override
    public HashMap<Double, Long> frequency(Function<E, Double> mapper) {
        HashMap<Double, Long> freqMap = new HashMap<>();
        this.generator.accept((element, index) -> {
            double value = mapper.apply(element);
            freqMap.merge(value, 1L, Long::sum);
        }, (element) -> false);
        return freqMap;
    }

    @Override
    public Double summate() {
        if (this.isEmpty()) {
            return 0.0;
        }
        return this.collect(
                () -> 0.0,
                (result, element, index) -> {
                    if (element instanceof Number) {
                        return result + ((Number) element).doubleValue();
                    }
                    return result;
                },
                Double::sum,
                result -> result
        );
    }

    @Override
    public Double summate(Function<E, Double> mapper) {
        return this.collect(
                () -> 0.0,
                (result, element, index) -> {
                    double value = mapper.apply(element);
                    return result + value;
                },
                Double::sum,
                result -> result
        );
    }

    @Override
    public List<Double> quartiles() {
        if (this.isEmpty()) return List.of();
        List<Double> values = new ArrayList<>();
        this.generator.accept((element, index) -> {
            if (element instanceof Number) {
                values.add(((Number) element).doubleValue());
            }
        }, (element) -> false);
        if (values.size() < 4) return List.of();
        Collections.sort(values);
        int size = values.size();
        double q1 = values.get(size / 4);
        double q2 = values.get(size / 2);
        double q3 = values.get(3 * size / 4);
        return List.of(q1, q2, q3);
    }

    @Override
    public List<Double> quartiles(Function<E, Double> mapper) {
        if (this.isEmpty()) return List.of();
        List<Double> values = new ArrayList<>();
        this.generator.accept((element, index) -> {
            values.add(mapper.apply(element));
        }, (element) -> false);
        if (values.size() < 4) return List.of();
        Collections.sort(values);
        int size = values.size();
        double q1 = values.get(size / 4);
        double q2 = values.get(size / 2);
        double q3 = values.get(3 * size / 4);
        return List.of(q1, q2, q3);
    }

    @Override
    public Double interquartileRange() {
        List<Double> quartiles = this.quartiles();
        if (quartiles.size() < 3) {
            return 0.0;
        }
        return quartiles.get(2) - quartiles.get(0);
    }

    @Override
    public Double interquartileRange(Function<E, Double> mapper) {
        List<Double> quartiles = this.quartiles(mapper);
        if (quartiles.size() < 3) {
            return 0.0;
        }
        return quartiles.get(2) - quartiles.get(0);
    }

    @Override
    public Double skewness() {
        if (this.count() < 3) {
            return 0.0;
        }
        double mean = this.mean();
        double stdDev = this.standardDeviation();
        if (stdDev == 0) {
            return 0.0;
        }
        double sumCubedDiffs = this.collect(
                () -> 0.0,
                (result, element, index) -> {
                    if (element instanceof Number) {
                        double value = ((Number) element).doubleValue();
                        double diff = value - mean;
                        return result + (diff * diff * diff);
                    }
                    return result;
                },
                Double::sum,
                result -> result
        );
        return (sumCubedDiffs / this.count()) / Math.pow(stdDev, 3);
    }

    @Override
    public Double skewness(Function<E, Double> mapper) {
        if (this.count() < 3) {
            return 0.0;
        }
        double mean = this.mean(mapper);
        double stdDev = this.standardDeviation(mapper);
        if (stdDev == 0) {
            return 0.0;
        }
        double sumCubedDiffs = this.collect(
                () -> 0.0,
                (result, element, index) -> {
                    double value = mapper.apply(element);
                    double diff = value - mean;
                    return result + (diff * diff * diff);
                },
                Double::sum,
                result -> result
        );
        return (sumCubedDiffs / this.count()) / Math.pow(stdDev, 3);
    }

    @Override
    public boolean isEmpty() {
        return this.count() == 0;
    }
}