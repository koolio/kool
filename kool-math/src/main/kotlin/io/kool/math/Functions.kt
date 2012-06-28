package io.kool.math

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics

/**
 * Creates a DescriptiveStatistics for the collection of numbers so that the various
 * calculations can be performed
 */
inline fun java.lang.Iterable<Double>.descriptiveStatistics(): DescriptiveStatistics {
    val answer = DescriptiveStatistics()
    for (n in this) {
        if (n != null) {
            answer.addValue(n.toDouble())
        }
    }
    return answer
}

/**
 * Creates a DescriptiveStatistics for the collection of values using the given function on each
 * value to create the number value
 */
inline fun <T> java.lang.Iterable<T>.descriptiveStatistics(fn: (T) -> Double?): DescriptiveStatistics {
    val answer = DescriptiveStatistics()
    for (e in this) {
        val d = (fn)(e)
        if (d != null) {
            answer.addValue(d)
        }
    }
    return answer
}


/**
 * Returns the maximum value of the collection
 */
inline fun java.lang.Iterable<Double>.max(): Double = descriptiveStatistics().getMax()

/**
 * Returns the minimum value of the collection
 */
inline fun java.lang.Iterable<Double>.min(): Double = descriptiveStatistics().getMin()

/**
 * Returns the mean value of the collection
 */
inline fun java.lang.Iterable<Double>.mean(): Double = descriptiveStatistics().getMean()


/**
 * Returns the geometric mean of the collection
 */
inline fun java.lang.Iterable<Double>.geometricMean(): Double = descriptiveStatistics().getGeometricMean()


/**
 * Returns the Kurtosis of the collection
 */
inline fun java.lang.Iterable<Double>.kurtosis(): Double = descriptiveStatistics().getKurtosis()


/**
 * Returns the pth percentile of the collection
 */
inline fun java.lang.Iterable<Double>.percentile(p: Double): Double = descriptiveStatistics().getPercentile(p)


/**
 * Returns the skewness of the collection
 */
inline fun java.lang.Iterable<Double>.skewness(): Double = descriptiveStatistics().getSkewness()


/**
 * Returns the sum of the collection
 */
inline fun java.lang.Iterable<Double>.sum(): Double = descriptiveStatistics().getSum()

/**
 * Returns the standard deviation of the collection
 */
inline fun java.lang.Iterable<Double>.standardDeviation(): Double = descriptiveStatistics().getStandardDeviation()

/**
 * Returns the sum of the squares of the collection
 */
inline fun java.lang.Iterable<Double>.sumSquares(): Double = descriptiveStatistics().getSumsq()

/**
 * Returns the variance of the collection
 */
inline fun java.lang.Iterable<Double>.variance(): Double = descriptiveStatistics().getVariance()





/**
 * Returns the maximum value of the collection
 */
inline fun <T> java.lang.Iterable<T>.max(fn: (T) -> Double?): Double = descriptiveStatistics(fn).getMax()

/**
 * Returns the minimum value of the collection
 */
inline fun  <T> java.lang.Iterable<T>.min(fn: (T) -> Double?): Double = descriptiveStatistics(fn).getMin()

/**
 * Returns the mean value of the collection
 */
inline fun  <T> java.lang.Iterable<T>.mean(fn: (T) -> Double?): Double = descriptiveStatistics(fn).getMean()


/**
 * Returns the geometric mean of the collection
 */
inline fun  <T> java.lang.Iterable<T>.geometricMean(fn: (T) -> Double?): Double = descriptiveStatistics(fn).getGeometricMean()


/**
 * Returns the Kurtosis of the collection
 */
inline fun  <T> java.lang.Iterable<T>.kurtosis(fn: (T) -> Double?): Double = descriptiveStatistics(fn).getKurtosis()


/**
 * Returns the pth percentile of the collection
 */
inline fun  <T> java.lang.Iterable<T>.percentile(p: Double, fn: (T) -> Double?): Double = descriptiveStatistics(fn).getPercentile(p)

/**
 * Returns the skewness of the collection
 */
inline fun  <T> java.lang.Iterable<T>.skewness(fn: (T) -> Double?): Double = descriptiveStatistics(fn).getSkewness()


/**
 * Returns the sum of the collection
 */
inline fun  <T> java.lang.Iterable<T>.sum(fn: (T) -> Double?): Double = descriptiveStatistics(fn).getSum()

/**
 * Returns the standard deviation of the collection
 */
inline fun  <T> java.lang.Iterable<T>.standardDeviation(fn: (T) -> Double?): Double = descriptiveStatistics(fn).getStandardDeviation()

/**
 * Returns the sum of the squares of the collection
 */
inline fun <T> java.lang.Iterable<T>.sumSquares(fn: (T) -> Double?): Double = descriptiveStatistics(fn).getSumsq()

/**
 * Returns the variance of the collection
 */
inline fun <T> java.lang.Iterable<T>.variance(fn: (T) -> Double?): Double = descriptiveStatistics(fn).getVariance()
