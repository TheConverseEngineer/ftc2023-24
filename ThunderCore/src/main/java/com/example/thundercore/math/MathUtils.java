package com.example.thundercore.math;

import com.example.thundercore.math.matrix.Matrix;
import com.example.thundercore.math.matrix.Num;

public class MathUtils {

    /** Returns true if a and b are within eps of each other */
    public static boolean epsEquals(double a, double b, double eps) {
        return Math.abs(a-b) < eps;
    }

    /** Calculates the sum of an array */
    public static double sum(double[] vals) {
        double s = 0;
        for (double i : vals) s += i;
        return s;
    }

    /** Given a set of values in a series, predicts the next value using linear regression
     * The arrays x and y should be the same length
     */
    public static double modelLeastSquares(double[] y, double[] x, double x1) {
        double xAvg = sum(x)/x.length;
        double yAvg = sum(y)/y.length;

        double mNumerator = 0;
        double mDenominator = 0;

        for (int i = 0; i < x.length; i++) {
            mNumerator += (x[i]-xAvg)*(y[i]-yAvg);
            mDenominator += (x[i]-xAvg)*(x[i]-xAvg);
        }

        double m = mNumerator/mDenominator;
        double b = yAvg - m*xAvg;

        return x1*m + b;
    }


    /** Only for instantiation suppression */
    private MathUtils() { }
}
