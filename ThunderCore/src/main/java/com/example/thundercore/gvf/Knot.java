package com.example.thundercore.gvf;

/** Simple mutable dataclass representing a control point along a path */
public class Knot {
    public double x, y, heading, splineHeading;

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getHeading() {
        return heading;
    }

    public double getSplineHeading() {
        return splineHeading;
    }
}
