package com.example.thundercore.gvf;

import android.net.wifi.p2p.WifiP2pManager;

import com.example.thundercore.math.dualnum.generics.Parameter;
import com.example.thundercore.math.geometry.Vector2d;
import com.example.thundercore.math.geometry.generics.Displacement;
import com.example.thundercore.math.geometry.generics.Global;
import com.example.thundercore.math.geometry.generics.Position;
import com.example.thundercore.math.geometry.generics.Unit;

import java.util.ArrayList;

public class CubicSpline {
    private final Polynomial<Parameter> x, y;
    private final Polynomial<Parameter> squareSum;

    /** Creates a new cubic spline between two knots */
    public CubicSpline(Knot start, Knot end) {
        double dist = Math.sqrt((end.getX()-start.getX())*(end.getX()-start.getX()) +
                (end.getY()-start.getY())*(end.getY()-start.getY()));
        x = constructPolynomial(start.x, end.x, Math.cos(start.getSplineHeading())*dist, Math.cos(end.getSplineHeading())*dist);
        y = constructPolynomial(start.y, end.y, Math.sin(start.getSplineHeading())*dist, Math.sin(end.getSplineHeading())*dist);
        squareSum = x.getSquared() .plus (y.getSquared());
    }

    /** Create a polynomial given the initial and final values and first derivatives */
    private static Polynomial<Parameter> constructPolynomial(double xi, double xf, double vi, double vf) {
        return new Polynomial<>(new double[]{
                vf - 2*xf + vi + 2*xi,
                3*xf - 2*vi - 3*xi - vf,
                vi, xi
        });
    }

    /** Evaluates this spline at a point u */
    public Vector2d<Parameter, Position, Global> get(double u) {
        return new Vector2d<>(
                x.get(u), y.get(u)
        );
    }

    /** Evaluates the unit tangent vector at a point u
     *
     * <p>Note that by definition, the derivative of this vector will be the (potentially non-unit) normal vector
     */
    public Vector2d<Parameter, Unit, Global> getTangent(double u) {
        return Vector2d.toUnitVector(new Vector2d<>(x.getDerivative(u), y.getDerivative(u)));
    }

    /** Numerically approximates the closest point on the spline to the inputted point p.
     *
     * @return  the parameter corresponding to the closest point (between 0 and 1)
     */
    public double getClosestPoint(Vector2d<Parameter, Position, Global> p) {
        // Define the derivative of the distance squared function
        Polynomial<Parameter> distSquared = squareSum .plus (x.times(-2*p.getX().get())) .plus (y.times(-2*p.getY().get()))
                .plus(new Polynomial<>(new double[]{p.getX().get()*p.getX().get() + p.getY().get()*p.getY().get()}));
        Polynomial<Parameter> dDistSquared = distSquared.derivative();

        // Find zeroes (these are the critical points)
        ArrayList<Double> zeroes = dDistSquared.getRealZeros();
        zeroes.add(1.0); // Consider the end point

        // Loop through critical points to find the best one
        double best = 0.0; // Consider the start point
        for (double i : zeroes) {
            if (i < 0 || i > 1) continue;
            if (p.distTo(get(best)) > p.distTo(get(i))) best = i;
        }

        return best;
    }
}
