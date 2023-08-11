package org.firstinspires.ftc.teamcode.skecore.hardware;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ServoController;

public class VirtualCRServo implements CRServo {
    private double currentPower = 0.0;
    private Direction currentDirection = Direction.FORWARD;

    @Override public ServoController getController() { return null; }
    @Override public int getPortNumber() { return 0; }

    @Override public void setDirection(Direction direction) { this.currentDirection = direction; }
    @Override public Direction getDirection() { return this.currentDirection; }

    @Override public void setPower(double power) { this.currentPower = power; }
    @Override public double getPower() { return currentPower; }

    // Hardware device methods
    @Override public Manufacturer getManufacturer() { return Manufacturer.Other; }
    @Override public String getDeviceName() { return "Virtual Servo"; }
    @Override public String getConnectionInfo() { return "Connected Virtually"; }
    @Override public int getVersion() { return 0; }
    @Override public void resetDeviceConfigurationForOpMode() { }
    @Override public void close() { }
}
