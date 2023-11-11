package com.opensim.hardware.devices;

import com.opensim.utils.MathUtils;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class VirtualDcMotorSimple extends VirtualHardwareDevice implements DcMotorSimple {

    protected final Object hardwareLock = new Object();

    private Direction direction = Direction.FORWARD;
    private double power = 0.0;

    public VirtualDcMotorSimple(String name) {
        super(name);
    }

    @Override
    public void setDirection(Direction direction) {
        synchronized (hardwareLock) {
            this.direction = direction;
        }
    }

    @Override
    public Direction getDirection() {
        synchronized (hardwareLock) {
            return this.direction;
        }
    }

    @Override
    public void setPower(double power) {
        synchronized (hardwareLock) {
            this.power = MathUtils.clamp(power, -1, 1);
        }
    }

    @Override
    public double getPower() {
        synchronized (hardwareLock) {
            return this.power;
        }
    }

    @Override
    public void resetDeviceConfigurationForOpMode() {
        setDirection(Direction.FORWARD);
    }
}
