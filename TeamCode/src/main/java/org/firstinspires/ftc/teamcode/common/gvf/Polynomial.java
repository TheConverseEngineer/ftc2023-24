package org.firstinspires.ftc.teamcode.common.gvf;

/** Represents a polynomial with coefficients of type T */
public class Polynomial {

    private final double[] coefficients;

    /** Creates a new polynomial with the inputted coefficients
     *
     * @param coefficients      array of coefficients, starting with the highest degree term*/
    public Polynomial(double[] coefficients) {
        this.coefficients = coefficients;
        if (coefficients.length == 0)
            throw new UnsupportedOperationException("Attempted to create a polynomial with 0 terms");
    }

    public double getCoefficient(int index) {
        return coefficients[index];
    }

    /** Evaluates this polynomial at a point x (see also: {@link Polynomial#eval(double)})
     *
     * @param <T>           The type of input
     * @param x             The value of the input
     * @param zeroValue     Whatever is considered to be zero according to type T
     */
    @SuppressWarnings("unchecked")
    public <T extends Unit> T eval(T x, T zeroValue) {
        Unit result = zeroValue.plus(coefficients[0]);
        for (int i = 1; i < coefficients.length; i++)
            result = (result.times(x)).plus(coefficients[i]);

        return (T) result;
    }

    /** Evaluates a polynomial at a point x*/
    public double eval(double x) {
        double result = coefficients[0];
        for (int i = 1; i < coefficients.length; i++)
            result = result*x + coefficients[i];

        return result;
    }

    /** Returns the degree of this polynomial */
    public int getDegree() {
        return coefficients.length-1;
    }

    /** Returns a new polynomial, which represents the derivative of this polynomial */
    public Polynomial getDerivative() {
        if (coefficients.length <= 1) return new Polynomial(new double[]{0});
        double[] newCoeffs = new double[coefficients.length-1];

        for (int i = 0; i < coefficients.length-1; i++) {
            newCoeffs[i] = coefficients[i] * (coefficients.length-1-i);
        }

        return new Polynomial(newCoeffs);
    }

    /** Returns this polynomial squared */
    public Polynomial getSquared() {
        double[] newCoeffs = new double[2*coefficients.length - 1];
        for (int i = 0; i < coefficients.length; i++) {
            for (int j = 0; j < coefficients.length; j++) {
                newCoeffs[i+j] += coefficients[i]*coefficients[j];
            }
        }

        return new Polynomial(newCoeffs);
    }

    /** Returns the sum of this polynomial and another polynomial */
    public Polynomial plus(Polynomial other) {
        double[] newCoeffs = new double[Math.max(other.getDegree(), getDegree()) + 1];

        for (int i = 0; i < newCoeffs.length; i++) {
            if (getDegree() >= i) newCoeffs[newCoeffs.length - i - 1] += coefficients[coefficients.length - i - 1];
            if (other.getDegree() >= i) newCoeffs[newCoeffs.length - i - 1] += other.getCoefficient(other.getDegree()-i);
        }

        return new Polynomial(newCoeffs);
    }

    public static double getMinimumX(Polynomial p) {
        return 0;
    }

}
