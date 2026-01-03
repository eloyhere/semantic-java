package pers.eloyhere.semantic;

import java.util.*;
import java.util.function.Function;

public class FloatStatistics<E> extends Statistics<E, Float> {

    public FloatStatistics(Generator<E> generator) {
        super(generator);
    }

    public FloatStatistics(Generator<E> generator, Comparator<E> comparator) {
        super(generator, comparator);
    }

    public FloatStatistics(Generator<E> generator, long concurrent) {
        super(generator, concurrent);
    }

    public FloatStatistics(Generator<E> generator, long concurrent, Comparator<E> comparator) {
        super(generator, concurrent, comparator);
    }

    @Override
    public Float range() {
        if (this.isEmpty()) {
            return 0.0f;
        }
        Optional<E> maximum = this.maximum();
        Optional<E> minimum = this.minimum();
        if (maximum.isPresent() && minimum.isPresent()) {
            return ((Number) maximum.get()).floatValue() - ((Number) minimum.get()).floatValue();
        }
        return 0.0f;
    }

    @Override
    public Float range(Function<E, Float> mapper) {
        if (this.isEmpty()) {
            return 0.0f;
        }
        Optional<Float> maximum = this.maximum().map(mapper);
        Optional<Float> minimum = this.minimum().map(mapper);
        if (maximum.isPresent() && minimum.isPresent()) {
            return maximum.get() - minimum.get();
        }
        return 0.0f;
    }

    @Override
    public Float variance() {
        if (this.count() < 2) {
            return 0.0f;
        }
        float mean = this.mean();
        float sumSquaredDiffs = this.collect(
                () -> 0.0f,
                (result, element, index) -> {
                    if (element instanceof Number) {
                        float value = ((Number) element).floatValue();
                        float diff = value - mean;
                        return result + (diff * diff);
                    }
                    return result;
                },
                Float::sum,
                result -> result
        );
        return sumSquaredDiffs / (this.count() - 1);
    }

    @Override
    public Float variance(Function<E, Float> mapper) {
        if (this.count() < 2) {
            return 0.0f;
        }
        float mean = this.mean(mapper);
        float sumSquaredDiffs = this.collect(
                () -> 0.0f,
                (result, element, index) -> {
                    if (element instanceof Number) {
                        float value = ((Number) element).floatValue();
                        float diff = value - mean;
                        return result + (diff * diff);
                    }
                    return result;
                },
                Float::sum,
                result -> result
        );
        return sumSquaredDiffs / (this.count() - 1);
    }

    @Override
    public Float standardDeviation() {
        if (this.isEmpty()) {
            return 0.0f;
        }
        Optional<E> maximum = this.maximum();
        Optional<E> minimum = this.minimum();
        if (maximum.isPresent() && minimum.isPresent()) {
            float mean = this.mean();
            float sumSquaredDiffs = this.collect(
                    () -> 0.0f,
                    (result, element, index) -> {
                        if (element instanceof Number) {
                            float value = ((Number) element).floatValue();
                            float diff = value - mean;
                            return result + (diff * diff);
                        }
                        return result;
                    },
                    Float::sum,
                    result -> result
            );
            return (float) Math.sqrt(sumSquaredDiffs / (this.count() - 1));
        }
        return 0.0f;
    }

    @Override
    public Float standardDeviation(Function<E, Float> mapper) {
        if (this.count() < 2) {
            return 0.0f;
        }
        Optional<E> maximum = this.maximum();
        Optional<E> minimum = this.minimum();
        if (maximum.isPresent() && minimum.isPresent()) {
            float mean = this.mean(mapper);
            float sumSquaredDiffs = this.collect(
                    () -> 0.0f,
                    (result, element, index) -> {
                        float value = mapper.apply(element);
                        float diff = value - mean;
                        return result + (diff * diff);
                    },
                    Float::sum,
                    result -> result
            );
            return (float) Math.sqrt(sumSquaredDiffs / (this.count() - 1));
        }
        return 0.0f;
    }

    @Override
    public Float mean() {
        if (this.isEmpty()) {
            return 0.0f;
        }
        Optional<E> maximum = this.maximum();
        Optional<E> minimum = this.minimum();
        if (maximum.isPresent() && minimum.isPresent()) {
            return this.collect(
                    () -> new float[]{0.0f, 0.0f},
                    (result, element, index) -> {
                        if (element instanceof Number) {
                            result[0] += ((Number) element).floatValue();
                            result[1] += 1.0f;
                        }
                        return result;
                    },
                    (result1, result2) -> {
                        result1[0] += result2[0];
                        result1[1] += result2[1];
                        return result1;
                    },
                    result -> result[1] > 0 ? result[0] / result[1] : 0.0f
            );
        }
        return 0.0f;
    }

    @Override
    public Float mean(Function<E, Float> mapper) {
        if (this.isEmpty()) return 0.0f;
        Optional<E> maximum = this.maximum();
        Optional<E> minimum = this.minimum();
        if (maximum.isPresent() && minimum.isPresent()) {
            return this.collect(
                    () -> new float[]{0.0f, 0.0f},
                    (result, element, index) -> {
                        float value = mapper.apply(element);
                        result[0] += value;
                        result[1] += 1.0f;
                        return result;
                    },
                    (result1, result2) -> {
                        result1[0] += result2[0];
                        result1[1] += result2[1];
                        return result1;
                    },
                    result -> result[1] > 0 ? result[0] / result[1] : 0.0f
            );
        }
        return 0.0f;
    }

    @Override
    public Float median() {
        if (this.isEmpty()) return 0.0f;
        Optional<E> maximum = this.maximum();
        Optional<E> minimum = this.minimum();
        if (maximum.isPresent() && minimum.isPresent()) {
            List<Float> values = new ArrayList<>();
            this.generator.accept((element, index) -> {
                if (element instanceof Number) {
                    values.add(((Number) element).floatValue());
                }
            }, (element) -> false);
            if (values.isEmpty()) return 0.0f;
            Collections.sort(values);
            int size = values.size();
            if (size % 2 == 0) {
                return (values.get(size / 2 - 1) + values.get(size / 2)) / 2.0f;
            } else {
                return values.get(size / 2);
            }
        }
        return 0.0f;
    }

    @Override
    public Float median(Function<E, Float> mapper) {
        if (this.isEmpty()) {
            return 0.0f;
        }
        Optional<E> maximum = this.maximum();
        Optional<E> minimum = this.minimum();
        if (maximum.isPresent() && minimum.isPresent()) {
            List<Float> values = new ArrayList<>();
            this.generator.accept((element, index) -> {
                values.add(mapper.apply(element));
            }, (element) -> false);
            if (values.isEmpty()) {
                return 0.0f;
            }
            Collections.sort(values);
            int size = values.size();
            if (size % 2 == 0) {
                return (values.get(size / 2 - 1) + values.get(size / 2)) / 2.0f;
            } else {
                return values.get(size / 2);
            }
        }
        return 0.0f;
    }

    @Override
    public Float mode() {
        if (this.isEmpty()) {
            return 0.0f;
        }
        HashMap<Float, Long> freq = this.frequency();
        if (freq.isEmpty()) {
            return 0.0f;
        }
        return freq.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(0.0f);
    }

    @Override
    public Float mode(Function<E, Float> mapper) {
        if (this.isEmpty()) {
            return 0.0f;
        }
        HashMap<Float, Long> freq = this.frequency(mapper);
        if (freq.isEmpty()) {
            return 0.0f;
        }
        return freq.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(0.0f);
    }

    @Override
    public Float product() {
        if (this.isEmpty()) {
            return 0.0f;
        }
        Optional<E> maximum = this.maximum();
        if (maximum.isPresent()) {
            return this.collect(
                    () -> 1.0f,
                    (result, element, index) -> {
                        if (element instanceof Number) {
                            return result * ((Number) element).floatValue();
                        }
                        return result;
                    },
                    (a, b) -> a * b,
                    result -> result
            );
        }
        return 0.0f;
    }

    @Override
    public Float product(Function<E, Float> mapper) {
        if (this.isEmpty()) {
            return 0.0f;
        }
        Optional<E> maximum = this.maximum();
        if (maximum.isPresent()) {
            return this.collect(
                    () -> 1.0f,
                    (result, element, index) -> {
                        return result * mapper.apply(element);
                    },
                    (a, b) -> a * b,
                    result -> result
            );
        }
        return 0.0f;
    }

    @Override
    public Float geometricMean() {
        if (this.isEmpty()) {
            return 0.0f;
        }
        Optional<E> maximum = this.maximum();
        if (maximum.isPresent()) {
            List<Float> values = new ArrayList<>();
            this.generator.accept((element, index) -> {
                if (element instanceof Number) {
                    float value = ((Number) element).floatValue();
                    if (value > 0) {
                        values.add(value);
                    }
                }
            }, (element) -> false);
            if (values.isEmpty()) {
                return 0.0f;
            }
            float product = 1.0f;
            for (float value : values) {
                product *= value;
            }
            return (float) Math.pow(product, 1.0 / values.size());
        }
        return 0.0f;
    }

    @Override
    public Float geometricMean(Function<E, Float> mapper) {
        if (this.isEmpty()) {
            return 0.0f;
        }
        Optional<E> maximum = this.maximum();
        if (maximum.isPresent()) {
            List<Float> values = new ArrayList<>();
            this.generator.accept((element, index) -> {
                float value = mapper.apply(element);
                if (value > 0) {
                    values.add(value);
                }
            }, (element) -> false);
            if (values.isEmpty()) {
                return 0.0f;
            }
            float product = 1.0f;
            for (float value : values) {
                product *= value;
            }
            return (float) Math.pow(product, 1.0 / values.size());
        }
        return 0.0f;
    }

    @Override
    public Float harmonicMean() {
        if (this.isEmpty()) {
            return 0.0f;
        }
        Optional<E> maximum = this.maximum();
        if (maximum.isPresent()) {
            return this.collect(
                    () -> new float[]{0.0f, 0.0f},
                    (result, element, index) -> {
                        if (element instanceof Number) {
                            float value = ((Number) element).floatValue();
                            if (value != 0) {
                                result[0] += 1.0f / value;
                                result[1] += 1.0f;
                            }
                        }
                        return result;
                    },
                    (result1, result2) -> {
                        result1[0] += result2[0];
                        result1[1] += result2[1];
                        return result1;
                    },
                    result -> result[1] > 0 ? result[1] / result[0] : 0.0f
            );
        }
        return 0.0f;
    }

    @Override
    public Float harmonicMean(Function<E, Float> mapper) {
        if (this.isEmpty()) {
            return 0.0f;
        }
        Optional<E> maximum = this.maximum();
        if (maximum.isPresent()) {
            return this.collect(
                    () -> new float[]{0.0f, 0.0f},
                    (result, element, index) -> {
                        float value = mapper.apply(element);
                        if (value != 0) {
                            result[0] += 1.0f / value;
                            result[1] += 1.0f;
                        }
                        return result;
                    },
                    (result1, result2) -> {
                        result1[0] += result2[0];
                        result1[1] += result2[1];
                        return result1;
                    },
                    result -> result[1] > 0 ? result[1] / result[0] : 0.0f
            );
        }
        return 0.0f;
    }

    @Override
    public Float medianAbsoluteDeviation() {
        if (this.isEmpty()) {
            return 0.0f;
        }
        float median = this.median();
        List<Float> deviations = new ArrayList<>();
        this.generator.accept((element, index) -> {
            if (element instanceof Number) {
                float value = ((Number) element).floatValue();
                deviations.add(Math.abs(value - median));
            }
        }, (element) -> false);
        if (deviations.isEmpty()) {
            return 0.0f;
        }
        Collections.sort(deviations);
        int size = deviations.size();
        if (size % 2 == 0) {
            return (deviations.get(size / 2 - 1) + deviations.get(size / 2)) / 2.0f;
        } else {
            return deviations.get(size / 2);
        }
    }

    @Override
    public Float medianAbsoluteDeviation(Function<E, Float> mapper) {
        if (this.isEmpty()) {
            return 0.0f;
        }
        float median = this.median(mapper);
        List<Float> deviations = new ArrayList<>();
        this.generator.accept((element, index) -> {
            float value = mapper.apply(element);
            deviations.add(Math.abs(value - median));
        }, (element) -> false);
        if (deviations.isEmpty()) {
            return 0.0f;
        }
        Collections.sort(deviations);
        int size = deviations.size();
        if (size % 2 == 0) {
            return (deviations.get(size / 2 - 1) + deviations.get(size / 2)) / 2.0f;
        } else {
            return deviations.get(size / 2);
        }
    }

    @Override
    public Float coefficientOfVariation() {
        if (this.isEmpty()) {
            return 0.0f;
        }
        float mean = this.mean();
        float stdDev = this.standardDeviation();
        if (mean == 0) {
            return 0.0f;
        }
        return stdDev / mean;
    }

    @Override
    public Float coefficientOfVariation(Function<E, Float> mapper) {
        if (this.isEmpty()) {
            return 0.0f;
        }
        float mean = this.mean(mapper);
        float stdDev = this.standardDeviation(mapper);
        if (mean == 0) {
            return 0.0f;
        }
        return stdDev / mean;
    }

    @Override
    public Float quartile1() {
        List<Float> quartiles = this.quartiles();
        return quartiles.isEmpty() ? 0.0f : quartiles.get(0);
    }

    @Override
    public Float quartile1(Function<E, Float> mapper) {
        List<Float> quartiles = this.quartiles(mapper);
        return quartiles.isEmpty() ? 0.0f : quartiles.get(0);
    }

    @Override
    public HashMap<Float, Long> frequency() {
        HashMap<Float, Long> freqMap = new HashMap<>();
        this.generator.accept((element, index) -> {
            if (element instanceof Number) {
                float value = ((Number) element).floatValue();
                freqMap.merge(value, 1L, Long::sum);
            }
        }, (element) -> false);
        return freqMap;
    }

    @Override
    public HashMap<Float, Long> frequency(Function<E, Float> mapper) {
        HashMap<Float, Long> freqMap = new HashMap<>();
        this.generator.accept((element, index) -> {
            float value = mapper.apply(element);
            freqMap.merge(value, 1L, Long::sum);
        }, (element) -> false);
        return freqMap;
    }

    @Override
    public Float summate() {
        if (this.isEmpty()) {
            return 0.0f;
        }
        return this.collect(
                () -> 0.0f,
                (result, element, index) -> {
                    if (element instanceof Number) {
                        return result + ((Number) element).floatValue();
                    }
                    return result;
                },
                Float::sum,
                result -> result
        );
    }

    @Override
    public Float summate(Function<E, Float> mapper) {
        return this.collect(
                () -> 0.0f,
                (result, element, index) -> {
                    float value = mapper.apply(element);
                    return result + value;
                },
                Float::sum,
                result -> result
        );
    }

    @Override
    public List<Float> quartiles() {
        if (this.isEmpty()) return List.of();
        List<Float> values = new ArrayList<>();
        this.generator.accept((element, index) -> {
            if (element instanceof Number) {
                values.add(((Number) element).floatValue());
            }
        }, (element) -> false);
        if (values.size() < 4) return List.of();
        Collections.sort(values);
        int size = values.size();
        float q1 = values.get(size / 4);
        float q2 = values.get(size / 2);
        float q3 = values.get(3 * size / 4);
        return List.of(q1, q2, q3);
    }

    @Override
    public List<Float> quartiles(Function<E, Float> mapper) {
        if (this.isEmpty()) return List.of();
        List<Float> values = new ArrayList<>();
        this.generator.accept((element, index) -> {
            values.add(mapper.apply(element));
        }, (element) -> false);
        if (values.size() < 4) return List.of();
        Collections.sort(values);
        int size = values.size();
        float q1 = values.get(size / 4);
        float q2 = values.get(size / 2);
        float q3 = values.get(3 * size / 4);
        return List.of(q1, q2, q3);
    }

    @Override
    public Float interquartileRange() {
        List<Float> quartiles = this.quartiles();
        if (quartiles.size() < 3) {
            return 0.0f;
        }
        return quartiles.get(2) - quartiles.get(0);
    }

    @Override
    public Float interquartileRange(Function<E, Float> mapper) {
        List<Float> quartiles = this.quartiles(mapper);
        if (quartiles.size() < 3) {
            return 0.0f;
        }
        return quartiles.get(2) - quartiles.get(0);
    }

    @Override
    public Float skewness() {
        if (this.count() < 3) {
            return 0.0f;
        }
        float mean = this.mean();
        float stdDev = this.standardDeviation();
        if (stdDev == 0) {
            return 0.0f;
        }
        float sumCubedDiffs = this.collect(
                () -> 0.0f,
                (result, element, index) -> {
                    if (element instanceof Number) {
                        float value = ((Number) element).floatValue();
                        float diff = value - mean;
                        return result + (diff * diff * diff);
                    }
                    return result;
                },
                Float::sum,
                result -> result
        );
        return (sumCubedDiffs / this.count()) / (float) Math.pow(stdDev, 3);
    }

    @Override
    public Float skewness(Function<E, Float> mapper) {
        if (this.count() < 3) {
            return 0.0f;
        }
        float mean = this.mean(mapper);
        float stdDev = this.standardDeviation(mapper);
        if (stdDev == 0) {
            return 0.0f;
        }
        float sumCubedDiffs = this.collect(
                () -> 0.0f,
                (result, element, index) -> {
                    float value = mapper.apply(element);
                    float diff = value - mean;
                    return result + (diff * diff * diff);
                },
                Float::sum,
                result -> result
        );
        return (sumCubedDiffs / this.count()) / (float) Math.pow(stdDev, 3);
    }

    @Override
    public boolean isEmpty() {
        return this.count() == 0;
    }
}