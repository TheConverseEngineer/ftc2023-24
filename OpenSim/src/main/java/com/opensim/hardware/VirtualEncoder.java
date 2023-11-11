package com.opensim.hardware;

public interface VirtualEncoder {

    /** Returns the current value of this encoder */
    int getEncoderCounts();

    /** Returns the current velocity of this encoder in ticks/sec */
    double getEncoderVelocity();

    /** Resets the encoder, so that the current value is 0 */
    void resetEncoder();

    /** Returns the number of encoder counts in one rotation */
    double getEncoderCPR();
}
