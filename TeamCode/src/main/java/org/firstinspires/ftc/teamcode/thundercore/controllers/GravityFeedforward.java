package org.firstinspires.ftc.teamcode.thundercore.controllers;

public class GravityFeedforward implements FeedforwardController {
    private final GravityFeedforward.GravityFeedforwardCoefficients coefficients;

    /** Construct a gravity feedforward controller */
    public GravityFeedforward(GravityFeedforward.GravityFeedforwardCoefficients coefficients) {
        this.coefficients = coefficients;
    }

    @Override
    public double calculate(double position, double targetVelocity, double targetAcceleration) {
        return coefficients.gravityFeedforwardGain;
    }


    /* Contains all of the important tuning values for a GravityFeedforward Controller */
    public static class GravityFeedforwardCoefficients {
        public double gravityFeedforwardGain;

        /** Constructor for the GravityFeedforward class
         *
         * @param gravityFeedforwardGain    the gravity feedforward gain (constant power)
         */
        public GravityFeedforwardCoefficients(double gravityFeedforwardGain) {
            this.gravityFeedforwardGain = gravityFeedforwardGain;
        }
    }
}
