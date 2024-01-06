package org.firstinspires.ftc.teamcode.trajectory.controllers;

public class PIDCoefficients {
    public double kP, kI, kD, intLimit, stabilityLimit, lowPassFilter;

    /** Initialize a PIDCoefficients class and set the kP, kI, and kD terms */
    public PIDCoefficients(double kP, double kI, double kD) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
    }

    /** Initialize a PIDCoefficients class and set all tuning terms
     *
     * @param intLimit          the maximum power contribution from the integral term. Defaults to 0.25.
     * @param stabilityLimit    the maximum velocity at which steady-state error should be considered. Defaults to 550 (~20% motor power)
     * @param lowPassFilter     the lowPassFilter tuning value (closer to zero is noisier, closer to 1 is slower). Defaults to 0.2
     * */
    public PIDCoefficients(double kP, double kI, double kD, double intLimit, double stabilityLimit, double lowPassFilter) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        this.intLimit = intLimit;
        this.stabilityLimit = stabilityLimit;
        this.lowPassFilter = lowPassFilter;
    }
}
