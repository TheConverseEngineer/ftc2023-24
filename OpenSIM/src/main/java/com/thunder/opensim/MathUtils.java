package com.thunder.opensim;

public class MathUtils {


    public static double clamp(double x, double min, double max) {
        return Math.min(Math.max(x, min), max);
    }

    // Suppress instantiation
    private MathUtils() { }
}
