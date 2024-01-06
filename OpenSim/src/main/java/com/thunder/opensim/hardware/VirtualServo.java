package com.thunder.opensim.hardware;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;
import com.thunder.opensim.MathUtils;

public class VirtualServo extends VirtualHardwareDevice implements Servo {

    private Direction direction = Direction.FORWARD;
    private double minRange = 0;
    private double maxRange = 1;

    protected VirtualHardwareBridge hardwareBridge = VirtualHardwareBridge.getInstance();

    public VirtualServo(String deviceName) {
        super(deviceName);
    }

    @Override public ServoController getController() { throwUnsupported("Servo.getController"); return null; }

    @Override public int getPortNumber() { throwUnsupported("Servo.getPortNumber"); return 0; }

    @Override public void setDirection(Direction direction) { this.direction = direction;}

    @Override public Direction getDirection() { return this.direction; }

    @Override
    public void setPosition(double position) {
        double scaledPos = (maxRange - minRange)*MathUtils.clamp(position, 0, 1) + minRange;
        hardwareBridge.setServoPositions(getDeviceName(), scaledPos);
    }

    @Override
    public double getPosition() {
        double scaledPos = hardwareBridge.getServoPosition(getDeviceName());
        return (scaledPos - minRange)/(maxRange - minRange);
    }

    @Override
    public void scaleRange(double min, double max) {
        min = MathUtils.clamp(min, 0, 1);
        max = MathUtils.clamp(max, 0, 1);
        if (max <= min) throw new IllegalArgumentException("Tried to use Servo.scaleRange, but the inputted minimum value is greater than the inputted maximum");
        this.minRange = min;
        this.maxRange = max;
    }
}
