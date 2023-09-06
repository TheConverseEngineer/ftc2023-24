package com.example.opensimclient.hardware.actuators;

import com.example.opensimclient.MessageCodes;
import com.example.opensimclient.SocketClient;
import com.example.opensimclient.hardware.VirtualHardwareDevice;
import com.example.opensimclient.utils.Utils;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;

public class VirtualServo extends VirtualHardwareDevice implements Servo {

    private double currentPosition = 0.0;
    private Direction currentDirection = Direction.FORWARD;

    private double scaleMin = 0;
    private double scaleMax = 1;

    public VirtualServo(String deviceName, SocketClient messageReceiver) {
        super(deviceName, messageReceiver);
    }

    @Override public ServoController getController() { return null; }
    @Override public int getPortNumber() { return 0; }

    @Override public void setDirection(Direction direction) {
        this.currentDirection = direction;
    }
    @Override public Direction getDirection() { return currentDirection; }

    @Override public void setPosition(double position) {
        position = Utils.clamp(position, 0, 1);
        if (this.currentDirection == Direction.REVERSE) position = 1 - position;
        this.currentPosition = position*(scaleMax - scaleMin) + scaleMin;
        sendMessage(MessageCodes.SET_SERVO_POSITION, this.getByteName(), encode(currentPosition));
    }
    @Override public double getPosition() { return currentPosition; }

    @Override public void scaleRange(double min, double max) {
        scaleMin = Utils.clamp(min, 0, 1);
        scaleMax = Utils.clamp(max, min, 1);
    }

    @Override
    public void resetDeviceConfigurationForOpMode() {
        setDirection(Direction.FORWARD);
        scaleRange(0, 1);
        setPosition(0);
    }
}
