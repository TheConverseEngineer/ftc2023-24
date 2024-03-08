package com.example.thundercore.filter;

import com.example.thundercore.math.MathUtils;

/** General-purpose SISO filter that removes sensor noise through a Least Squares regression model */
public class LeastSquaresFilter {

    private double[] previousValues;
    private double[] previousTimestamps;
    int currentStackPointer;

    private final double k;


    /** Creates a new LeastSquaresFilter
     *
     * @param k     The convergence gain. Should be between 0 and 1. Higher values places more trust on the
     *              sensor (low noise), while lower values favor the model (high noise).
     * @param n     The number of values to use when calculating the regression (3 seems to work well)
     */
    public LeastSquaresFilter(double k, int n) {
        previousValues = new double[n];
        previousTimestamps = new double[n];
        currentStackPointer = -n;

        this.k = k;
    }

    /** Filters a potentially noisy measurement. This method should be called frequently in order for it
     * to work as intended.
     */
    public double filter(double measurement) {
        if (currentStackPointer < 0) {
            // Still populating the regression
            previousValues[currentStackPointer + previousValues.length] = measurement;
            previousTimestamps[currentStackPointer + previousTimestamps.length] = System.nanoTime();
            advancePointer();
            return measurement;
        }

        double currentTime = System.nanoTime();

        double prediction = MathUtils.modelLeastSquares(previousValues, previousTimestamps, currentTime);

        double filtered = prediction + k*(measurement - prediction);

        previousValues[currentStackPointer] = filtered;
        previousTimestamps[currentStackPointer] = currentTime;

        return filtered;
    }

    private void advancePointer() {
        currentStackPointer++;
        while(currentStackPointer >= previousValues.length)
            currentStackPointer -= previousValues.length;
    }
}
