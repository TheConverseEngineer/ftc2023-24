package org.firstinspires.ftc.teamcode.common.gvf;

/** Represents a 2 dimensional, parametrically-defined cubic spline */
public class CubicSpline {
    private final Polynomial x, y;

    /** Creates a new cubic spline between two knots */
    public CubicSpline(Knot start, Knot end) {
        double dist = Math.sqrt((end.getX()-start.getX())*(end.getX()-start.getX()) +
                (end.getY()-start.getY())*(end.getY()-start.getY()));
        x = constructPolynomial(start.x, end.x, Math.cos(start.getSplineHeading())*dist, Math.cos(end.getSplineHeading())*dist);
        y = constructPolynomial(start.y, end.y, Math.sin(start.getSplineHeading())*dist, Math.sin(end.getSplineHeading())*dist);
    }

    /** Create a polynomial given the initial and final values and first derivatives */
    private static Polynomial constructPolynomial(double xi, double xf, double vi, double vf) {
        return new Polynomial(new double[]{
                vf - 2*xf + vi + 2*xi,
                3*xf - 2*vi - 3*xi - vf,
                vi, xi
        });
    }
}
