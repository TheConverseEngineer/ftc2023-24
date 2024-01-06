package com.thunder.opensim.hardware;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.thunder.opensim.MathUtils;

public class VirtualDcMotorSimple extends VirtualHardwareDevice implements DcMotorSimple {

    private Direction direction = Direction.FORWARD;
    protected VirtualHardwareBridge hardwareBridge = VirtualHardwareBridge.getInstance();

    private double power = 0;

    public VirtualDcMotorSimple(String deviceName) {
        super(deviceName);
    }

    @Override
    public void resetDeviceConfigurationForOpMode() {
        super.resetDeviceConfigurationForOpMode();
        this.power = 0;
        this.direction = Direction.FORWARD;
    }

    @Override public void setDirection(Direction direction) { this.direction = direction; }
    @Override public Direction getDirection() { return this.direction; }

    @Override
    public void setPower(double power) {
        this.power = power;
        hardwareBridge.setMotorPower(getDeviceName(), MathUtils.clamp(power, -1, 1));
    }

    @Override
    public double getPower() {
        return power;
    }
}
