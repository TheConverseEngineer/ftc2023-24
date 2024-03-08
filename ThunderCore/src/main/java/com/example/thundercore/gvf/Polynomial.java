package com.example.thundercore.gvf;

import com.example.thundercore.math.MathUtils;
import com.example.thundercore.math.dualnum.DualNumber;
import com.example.thundercore.math.dualnum.generics.Parameter;

import org.ejml.data.Complex64F;
import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.DecompositionFactory;
import org.ejml.interfaces.decomposition.EigenDecomposition;
import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;

/** Represents a polynomial with an arbitrary degree
 *
 * @param <P>   The parameterization of this polynomial
 */
public class Polynomial <P extends Parameter> {

    private final double[] coefficients;

    /** Creates a new polynomial with the inputted coefficients
     *
     * @param coefficients      array of coefficients, starting with the highest degree term
     */
    public Polynomial(double[] coefficients) {
        this.coefficients = coefficients;
        if (coefficients.length == 0)
            throw new UnsupportedOperationException("Attempted to create a polynomial with 0 terms");
    }

    public double getCoefficient(int index) {
        return coefficients[index];
    }

    /** Returns the degree of this polynomial */
    public int getDegree() {
        return coefficients.length-1;
    }

    /** Evaluates this polynomial at a point u */
    public DualNumber<P> get(double u) {
        double x = 0, dx = 0, ddx = 0;
        for (int i = 0; i < coefficients.length; i++) {
            x = x*u + coefficients[i];
            dx = dx*u + coefficients[i]*(getDegree() - i);
            ddx = ddx*u + coefficients[i]*(getDegree() - i)*(getDegree() - i - 1);
        }
        return new DualNumber<>(x, dx, ddx);
    }

    /** Evaluates the derivative of a polynomial at a point u */
    public DualNumber<P> getDerivative(double u) {
        double dx = 0, ddx = 0, dddx = 0;
        for (int i = 0; i < coefficients.length; i++) {
            dx = dx*u + coefficients[i]*(getDegree() - i);
            ddx = ddx*u + coefficients[i]*(getDegree() - i)*(getDegree() - i - 1);
            dddx = dddx*u + coefficients[i]*(getDegree() - i)*(getDegree() - i - 1)*(getDegree() - i - 2);
        }
        return new DualNumber<>(dx, ddx, dddx);
    }


    /** Returns a new polynomial, which represents the derivative of this polynomial */
    public Polynomial<P> derivative() {
        if (coefficients.length <= 1) return new Polynomial<>(new double[]{0});
        double[] newCoeffs = new double[coefficients.length-1];

        for (int i = 0; i < coefficients.length-1; i++) {
            newCoeffs[i] = coefficients[i] * (coefficients.length-1-i);
        }

        return new Polynomial<>(newCoeffs);
    }

    /** Returns this polynomial squared */
    public Polynomial<P> getSquared() {
        double[] newCoeffs = new double[2*coefficients.length - 1];
        for (int i = 0; i < coefficients.length; i++) {
            for (int j = 0; j < coefficients.length; j++) {
                newCoeffs[i+j] += coefficients[i]*coefficients[j];
            }
        }

        return new Polynomial<>(newCoeffs);
    }

    /** Returns the sum of this polynomial and another polynomial */
    public Polynomial<P> plus(Polynomial<P> other) {
        double[] newCoeffs = new double[Math.max(other.getDegree(), getDegree()) + 1];

        for (int i = 0; i < newCoeffs.length; i++) {
            if (getDegree() >= i) newCoeffs[newCoeffs.length - i - 1] += coefficients[coefficients.length - i - 1];
            if (other.getDegree() >= i) newCoeffs[newCoeffs.length - i - 1] += other.getCoefficient(other.getDegree()-i);
        }

        return new Polynomial<>(newCoeffs);
    }

    /** Returns the product of this polynomial and a scalar */
    public Polynomial<P> times(double scalar) {
        double[] newCoeffs = new double[coefficients.length];

        for (int i = 0; i < coefficients.length; i++) newCoeffs[i] = scalar*coefficients[i];

        return new Polynomial<>(newCoeffs);
    }

    /** Numerically estimates and returns all real zeroes of this polynomial be solving for the
     * eigenvalues of the companion matrix
     */
    public ArrayList<Double> getRealZeros() {
        int d = getDegree();

        DenseMatrix64F matrix = new DenseMatrix64F(d, d);
        double a = coefficients[0];
        for (int i = 0; i < d; i++) {
            matrix.set(i, d-1, -coefficients[d-i]/a);
            if (i >= 1) matrix.set(i, i-1, 1);
        }

        EigenDecomposition<DenseMatrix64F> evd = DecompositionFactory.eig(d, false);
        evd.decompose(matrix);

        ArrayList<Double> realZeroes = new ArrayList<>();

        for (int i = 0; i < d; i++) {
            Complex64F root = evd.getEigenvalue(i);
            if (Math.abs(root.getImaginary()) < 0.001)
                realZeroes.add(root.getReal());
        }

        return realZeroes;
    }
}
