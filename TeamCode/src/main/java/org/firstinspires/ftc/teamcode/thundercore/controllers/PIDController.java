package org.firstinspires.ftc.teamcode.thundercore.controllers;

import static org.firstinspires.ftc.teamcode.blacksmithcore.utils.clamp;
import static java.lang.Math.abs;

import org.firstinspires.ftc.teamcode.thundercore.utils.Logger;

public class PIDController implements FeedbackController {
    /* Tuning Value */
    private PIDCoefficients coefficients;

    /* Private variables */
    private boolean calledYet = false;
    private long lastEpoch = 0;

    private double previousError = 0.0;
    private double integralSum = 0.0;

    private double lastDerivativeEstimate = 0.0;

    private double lastTarget = 0;

    public PIDController(PIDCoefficients coefficients) {
        this.coefficients = coefficients;
        Logger.INSTANCE.logMessage("Created a PID Controller");
    }

    @Override
    public double calculate(double currentPosition, double targetPosition) {
        double dt = getDeltaTime();
        double error = targetPosition - currentPosition;

        // Put kd first so that ki can use the derivative in its calculation
        if (abs(targetPosition - lastTarget) > 0.0001) integralSum = 0;
        double output = error*coefficients.kP + calculateDerivative(error, dt)*coefficients.kD + clamp(integrate(error, dt)*coefficients.kI, -coefficients.intLimit, coefficients.intLimit);

        previousError = error;
        lastTarget = targetPosition;
        return output;
    }

    public double getDerivative() {
        return lastDerivativeEstimate;
    }

    /** Calculates the derivative (using a low-pass filter) */
    private double calculateDerivative(double error, double dt) {
        lastDerivativeEstimate = (coefficients.lowPassGain*lastDerivativeEstimate + (1-coefficients.lowPassGain)*(error-previousError)/dt);
        return lastDerivativeEstimate;
    }

    /** Calculates the integral (with a few added safety measures) */
    private double integrate(double error, double dt) {
        if (crossOverDetected(error, previousError)) integralSum = 0.0;
        else if (abs(lastDerivativeEstimate) > coefficients.stabilityLimit) return integralSum;
        integralSum += ((error+previousError)/2)*dt;
        return integralSum;
    }

    /** Returns the time in milliseconds since the last loop iteration */
    private double getDeltaTime() {
        if (!calledYet) {
            calledYet = true;
            lastEpoch = System.nanoTime();
            return 0.00001;
        }
        long deltaTime = System.nanoTime() - lastEpoch;
        lastEpoch += deltaTime;
        return deltaTime/1000000.0;
    }

    /** Returns true if the sign of the error has changed */
    private boolean crossOverDetected(double error, double prev) {
        return (error >= 0 && prev < 0) || (error <= 0 && prev > 0);
    }

    public static class PIDCoefficients {
        public double kP;
        public double kI;
        public double kD;
        public double intLimit;
        public double stabilityLimit;
        public double lowPassGain;

        /** Tuning constants for a PID Controller, with defaults used for advanced settings
         * <br>
         * Advanced settings:
         * <ul>
         *     <li>intLimit defaults to 0.35</li>
         *     <li>stabilityLimit defaults to 999999</li>
         *     <li>lowPassGain defaults to 0.15</li>
         * </ul>
         *
         * @param kP    the proportional term
         * @param kI    the integral term
         * @param kD    the derivative term
         */
        public PIDCoefficients(double kP, double kI, double kD) {
            this(kP, kI, kD, 0.35, 999999, 0.15);
        }

        /** Tuning constants for a PID Controller with overloads for advanced settings
         *
         * @param kP                the proportional term
         * @param kI                the integral term
         * @param kD                the derivative term
         * @param intLimit          the maximum output contribution from the derivative
         * @param stabilityLimit    the threshold for the derivative before the integral term activates
         * @param lowPassGain       the low pass filter value for derivative (should be between 0 and 1). Lower values
         *                              prioritize current data and higher values prioritize past data.
         */
        public PIDCoefficients(double kP, double kI, double kD, double intLimit, double stabilityLimit, double lowPassGain) {
            this.kP = kP;
            this.kI = kI;
            this.kD = kD;
            this.intLimit = intLimit;
            this.stabilityLimit = stabilityLimit;
            this.lowPassGain = lowPassGain;
        }
    }
}
