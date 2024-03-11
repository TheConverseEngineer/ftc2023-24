package org.firstinspires.ftc.teamcode.common.utils;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.function.DoubleConsumer;

public abstract class CachingSensor {

    private final long cacheDurationNs;
    private double lastStoredValue;
    private double lastAccessTimeNs;

    private @Nullable DoubleConsumer cacheClearCallback = null;

    /** Wraps a VoltageSensor object
     *
     * @param cacheDurationMs       How often the voltage should be re-read (in milliseconds)
     * */
    public CachingSensor(long cacheDurationMs, double initialValue) {
        this.cacheDurationNs = cacheDurationMs*1000000;
        lastAccessTimeNs = System.nanoTime();
        lastStoredValue = initialValue;
    }

    /** Reads the sensor and returns the measured value. This value will be cached automatically */
    protected abstract double querySensor();


    /** Returns the last-read voltage, updating the cache if necessary */
    public final double getValue() {
        long currentTime = System.nanoTime();
        if (currentTime >= lastAccessTimeNs + cacheDurationNs) {
            lastAccessTimeNs = currentTime;
            lastStoredValue = querySensor();
            if (cacheClearCallback != null) {
                cacheClearCallback.accept(lastStoredValue);
            }
        }

        return lastStoredValue;
    }

    /** Sets a method that should be called whenever new data is queried */
    public final void setCacheUpdateCallback(DoubleConsumer callback) {
        this.cacheClearCallback = callback;
    }
}
