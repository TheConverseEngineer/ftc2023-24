package com.opensim.physics;

import com.opensim.hardware.VirtualEncoder;

public class FreeMotorSystem extends SystemBase implements VirtualEncoder {

    private final double resistance;

    /** Angular velocity constant (rad/sV)*/
    private final double Kv;

    /** Torque constant (Nm/amp)*/
    private final double Kt;

    /** Current angular velocity of the system */
    private double omega = 0;

    public FreeMotorSystem(double angularFreeSpeed, double freeCurrent, double stallCurrent, double stallTorque, double voltage) {
        this.resistance = voltage/stallCurrent;
        this.Kv = angularFreeSpeed / (voltage - freeCurrent*resistance);
        this.Kt = stallTorque / stallCurrent;
    }


    @Override
    public int getEncoderCounts() {
        return 0;
    }

    @Override
    public double getEncoderVelocity() {
        return 0;
    }

    @Override
    public void resetEncoder() {

    }

    @Override
    public double getEncoderCPR() {
        return 0;
    }

    @Override
    public void update(double deltaTime) {
        
       // double angularAcceleration = (voltage - omega/Kv)*Kt/resistance;
    }

    @Override
    public double getCurrentDraw() {
        return 0;
    }

    @Override
    public double getCurrentDrawPerMotor() {
        return 0;
    }
}
