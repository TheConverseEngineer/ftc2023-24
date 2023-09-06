package org.firstinspires.ftc.teamcode.general.skecore.hardware;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;

import org.firstinspires.ftc.teamcode.general.skecore.MessageCodes;
import org.firstinspires.ftc.teamcode.general.skecore.MessageReceiver;
import org.firstinspires.ftc.teamcode.general.thundercore.utils.Utils;

public class VirtualServo extends VirtualHardwareDevice implements Servo {

    private double currentPosition = 0.0;
    private Direction currentDirection = Direction.FORWARD;

    private double scaleMin = 0;
    private double scaleMax = 1;

    public VirtualServo(String deviceName, MessageReceiver messageReceiver) {
        super(deviceName, messageReceiver);
    }

    @Override public ServoController getController() { return null; }
    @Override public int getPortNumber() { return 0; }

    @Override public void setDirection(Direction direction) {
        this.currentDirection = direction;
        sendMessage(MessageCodes.SET_SERVO_DIRECTION, this.getByteName(), new byte[]{(byte)((currentDirection==Direction.FORWARD)?1:0)});
    }
    @Override public Direction getDirection() { return currentDirection; }

    @Override public void setPosition(double position) {
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
