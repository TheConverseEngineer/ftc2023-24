package org.firstinspires.ftc.teamcode.thundercore.controllers;

public class ArmFeedforward implements FeedforwardController {

    private final ArmFeedforwardCoefficients coefficients;

    /** Construct an arm feedforward controller */
    public ArmFeedforward(ArmFeedforwardCoefficients coefficients) {
        this.coefficients = coefficients;
    }

    @Override
    public double calculate(double position, double targetVelocity, double targetAcceleration) {
        return coefficients.armFeedforwardGain * Math.sin(position);
    }


    /* Contains all of the important tuning values for an ArmFeedforward Controller */
    public static class ArmFeedforwardCoefficients {
        public double armFeedforwardGain;

        /** Constructor for the ArmFeedforward class
         *
         * @param armFeedforwardGain    the arm feedforward gain (proportional to position)
         */
        public ArmFeedforwardCoefficients(double armFeedforwardGain) {
            this.armFeedforwardGain = armFeedforwardGain;
        }
    }
}
