package com.example.thundercore.hardware;

import com.example.thundercore.math.MathUtils;
import com.qualcomm.robotcore.hardware.DcMotorEx;

/** Standard implementation of ThunderMotor */
public class ThunderMotorImpl implements ThunderMotor {

    private final DcMotorEx motor;

    private double lastSetPower = 0;
    private double lastRecordedBatteryVoltage = 12;


    private int velocityEstimateIdx;
    private double[] velocityEstimates;
    private int lastInternalPosition = 0;

    /** Wraps a DcMotorEx into a new ThunderMotor instance */
    public ThunderMotorImpl(DcMotorEx motor) {
        this.motor = motor;
    }

    @Override
    public void setPower(double power) {
        if (!MathUtils.epsEquals(power, lastSetPower, 0.001)) {
            motor.setPower(power);
            lastSetPower = power;
        }
    }

    @Override
    public void setVoltage(double voltage) {
        this.setPower(voltage/lastRecordedBatteryVoltage);
    }

    @Override
    public int getCurrentPosition() {
        return motor.getCurrentPosition();
    }

    @Override
    public double getRawVelocity() {
        return motor.getVelocity();
    }

    @Override
    public double getCorrectedVelocity() {
        double median = velocityEstimates[0] > velocityEstimates[1]
                ? Math.max(velocityEstimates[1], Math.min(velocityEstimates[0], velocityEstimates[2]))
                : Math.max(velocityEstimates[0], Math.min(velocityEstimates[1], velocityEstimates[2]));
        return inverseOverflow(getRawVelocity(), median);
    }

    @Override
    public void updateInternal(double dT, double voltage) {
        this.lastRecordedBatteryVoltage = voltage;

        // Update the estimate used for velocity correction
        int currentPosition = getCurrentPosition();
        velocityEstimates[velocityEstimateIdx] = (currentPosition - lastInternalPosition) / dT;
        velocityEstimateIdx = (velocityEstimateIdx + 1) % 3;
        lastInternalPosition = currentPosition;
    }

    private final static int CPS_STEP = 0x10000;

    /** Corrected for velocity overflow. Taken from the roadrunner quickstart */
    private static double inverseOverflow(double input, double estimate) {
        // convert to uint16
        int real = (int) input & 0xffff;
        // initial, modulo-based correction: it can recover the remainder of 5 of the upper 16 bits
        // because the velocity is always a multiple of 20 cps due to Expansion Hub's 50ms measurement window
        real += ((real % 20) / 4) * CPS_STEP;
        // estimate-based correction: it finds the nearest multiple of 5 to correct the upper bits by
        real += Math.round((estimate - real) / (5 * CPS_STEP)) * 5 * CPS_STEP;
        return real;
    }
}
