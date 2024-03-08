package com.example.thundercore.math.dualnum;

import com.example.thundercore.math.dualnum.generics.Parameter;

/** Representation of an dual number, allowing for forward auto-differentiation.
 *
 * <p> When doing operations with a dual number, the first 3 derivatives of this operation are also stored,
 * in addition to the operation itself.
 * */
public class DualNumber<T extends Parameter> {
    private final double x, dx, ddx;

    /** Creates a new DualNumber using a user-inputted array of derivatives
     *
     * @param x      the stored values
     * @param dx     the first derivative
     * @param ddx    the second derivative
     */
    public DualNumber(double x, double dx, double ddx) {
        this.x = x;
        this.dx = dx;
        this.ddx = ddx;
    }

    /** Returns the value of this dual num */
    public double get() {
        return x;
    }

    /** Returns the derivative of this dual num */
    public double getDerivative() {
        return dx;
    }

    /** Returns the second derivative of this dual num */
    public double getSecondDerivative() {
        return ddx;
    }

    /** Returns the sum of two dual numbers */
    public DualNumber<T> plus(DualNumber<T> other) {
        return new DualNumber<>(
                x + other.get(),
                dx + other.getDerivative(),
                ddx + other.getSecondDerivative()
        );
    }

    /** Returns the difference of two dual numbers */
    public DualNumber<T> minus(DualNumber<T> other) {
        return new DualNumber<>(
                x - other.get(),
                dx - other.getDerivative(),
                ddx - other.getSecondDerivative()
        );
    }

    /** Returns the product of two dual numbers */
    public DualNumber<T> times(DualNumber<T> other) {
        return new DualNumber<>(
                x * other.get(),
                x*other.getDerivative() + dx*other.get(),
                x *other.getSecondDerivative() + ddx*other.get() + 2*dx*other.getDerivative()
        );
    }

    /** Returns the quotient of two dual numbers */
    public DualNumber<T> div(DualNumber<T> other) {
        return new DualNumber<>(
                x / other.get(),
                (other.get()*dx - x*other.getDerivative()) / (other.get()*other.get()),
                (other.get()*other.get()*ddx - other.get()*(2*dx*other.getDerivative()+
                        x*other.getSecondDerivative()) + 2*x*other.getDerivative()*other.getDerivative())/
                            (other.get()*other.get()*other.get())
        );
    }

    /** Returns the square of this dual number */
    public DualNumber<T> square() {
        return new DualNumber<>(
                x*x, 2*x*dx, 2*dx*dx + 2*x*ddx
        );
    }

    /** Returns the square root of this dual number */
    public DualNumber<T> sqrt() {
        double s = Math.sqrt(x);
        return new DualNumber<>(
                s,
                dx/(2*s),
                (2*x*ddx - dx*dx)/(4*x*s)
        );
    }

    /** Returns the "derivative" as a new DualNumber.
     *
     * <p> Note that higher-order derivatives are not tracked beyond acceleration and will be meaningless.
     */
    public DualNumber<T> differentiate() {
        return new DualNumber<>(dx, ddx, 0);
    }


}
