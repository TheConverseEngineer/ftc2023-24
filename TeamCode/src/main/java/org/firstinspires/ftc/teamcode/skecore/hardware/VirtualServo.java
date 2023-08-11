package org.firstinspires.ftc.teamcode.skecore.hardware;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;

public class VirtualServo implements Servo {

    private double currentPosition = 0.0;
    private Direction currentDirection = Direction.FORWARD;

    @Override public ServoController getController() { return null; }
    @Override public int getPortNumber() { return 0; }

    @Override public void setDirection(Direction direction) { this.currentDirection = direction; }
    @Override public Direction getDirection() { return currentDirection; }

    @Override public void setPosition(double position) { this.currentPosition = position; }
    @Override public double getPosition() { return currentPosition; }

    @Override public void scaleRange(double min, double max) { }

    // Hardware device methods
    @Override public Manufacturer getManufacturer() { return Manufacturer.Other; }
    @Override public String getDeviceName() { return "Virtual Servo"; }
    @Override public String getConnectionInfo() { return "Connected Virtually"; }
    @Override public int getVersion() { return 0; }
    @Override public void resetDeviceConfigurationForOpMode() { }
    @Override public void close() { }
}
