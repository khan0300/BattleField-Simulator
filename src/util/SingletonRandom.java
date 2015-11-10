package util;

import java.util.Random;

import javafx.beans.property.SimpleDoubleProperty;

/**
 * Singleton class that provides a simplified interface to random number generation using a Gaussian (normal) distribution
 */
public class SingletonRandom {
  /** public final static gives immutable access to reference-to variable of the single object of type <i>SingletonRandom</i> */
  public final static SingletonRandom instance = new SingletonRandom();
  /** reference-to value for the java.util.Random object that will be created */
  private Random random;

  /** private constructor is a key element in <i>Singleton</i> design pattern. */
  private SingletonRandom() { random = new Random(); }

  /**
   * @param lowerLimit returned values will always be greater-than-or-equal-to this value
   * @param upperLimit returned values will always be less-than-or-equal-to this value
   * @param standardDeviationSpread influences the shape of the "normal" curve, example cases of values for standardDeviationSpread: <ul><li>1.0: distribution will be flatter, 68% of values naturally fall between
   * lowerLimit and upperLimit, 32% (100%-68%) are outside limits and have to be snapped back into range</li><li>2.0: distribution will be moderately peaked, 95% of values naturally fall between lowerLimit
   * and upperLimit, 5% (100%-95%) are outside limits and have to be snapped back into range</li><li>3.0: distribution will have sharp clustering around the mean, 99.7% of values naturally fall between
   * lowerLimit and upperLimit, 0.3% (100%-99.7%) are outside limits and have to be snapped back into range since standardDeviationSpread is double, it can contain fractional values, not just the integral values shown here.</li></ul>
   *
   * @return a random number that follows a normal (Gaussian) distribution within the specified range.
   */
  public double getNormalDistribution(double lowerLimit, double upperLimit, double standardDeviationSpread) {
    if (standardDeviationSpread < 1.0 || standardDeviationSpread > 5.0) // if standardDeviationSpread is < 1.0 there is a risk of excessive iterations of the range-checking do-while() loop
      throw new IllegalArgumentException();
    double range = upperLimit - lowerLimit + 1.0;
    double mean = (lowerLimit + upperLimit) / 2.0;
    double calculatedRandom;
    do {
      // next.Gaussian() returns a value where 1-standard deviation worth range between -1.0 and +1.0 (i.e. about 68% of values)
      calculatedRandom = (random.nextGaussian() * (range / 2.0) / standardDeviationSpread) + mean;
    } while (calculatedRandom < lowerLimit || calculatedRandom > upperLimit);
    return calculatedRandom;
  } // end getNormalDistribution()
} // end class SingletonRandom