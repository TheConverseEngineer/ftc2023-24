package com.example.thundercore.math.geometry;

import org.firstinspires.ftc.robotcore.external.matrices.VectorF;

/** Immutable alternative to {@link Vector2d} that uses doubles instead of {@link com.example.thundercore.math.dualnum.DualNumber} */
public class SimpleVector2d {
    private final double x, y;

    public SimpleVector2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public SimpleVector2d plus(SimpleVector2d other) {
        return new SimpleVector2d(x + other.x, y + other.y);
    }

    public SimpleVector2d minus(SimpleVector2d other) {
        return new SimpleVector2d(x - other.x, y - other.y);
    }

    public SimpleVector2d times(double other) {
        return new SimpleVector2d(x * other, y * other);
    }

    public SimpleVector2d rotateBy(double angle) {
        double newX = x * Math.cos(angle) - y * Math.sin(angle);
        double newY = x * Math.sin(angle) + y * Math.cos(angle);
        return new SimpleVector2d(newX, newY);
    }

    public static SimpleVector2d fromVectorF(VectorF vector) {
        return new SimpleVector2d(vector.get(0), vector.get(1));
    }
}
