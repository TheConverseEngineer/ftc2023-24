package com.example.opensimclient.utils;

public class Utils {

    private Utils() { }

    /** Clamps the inputted double into a range */
    public static double clamp(double x, double min, double max) {
        return Math.min(max, Math.max(x, min));
    }
}
