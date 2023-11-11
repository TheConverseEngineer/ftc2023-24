package com.opensim.utils;

public class MathUtils {

    private MathUtils() {}

    public static double clamp(double x, double min, double max) {
        return Math.min(Math.max(x, min), max);
    }
}
