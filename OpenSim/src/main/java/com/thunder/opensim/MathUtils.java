package com.thunder.opensim;

public class MathUtils {


    public static double clamp(double x, double min, double max) {
        return Math.min(Math.max(x, min), max);
    }

    /** Returns x unless it is null. If x is null, this method will return valIfNull */
    public static double ifNull(Double x, double valIfNull) {
        if (x == null) return valIfNull;
        else return x;
    }

    /** Returns x unless it is null. If x is null, this method will return valIfNull */
    public static int ifNull(Integer x, int valIfNull) {
        if (x == null) return valIfNull;
        else return x;
    }

    // Suppress instantiation
    private MathUtils() { }
}
