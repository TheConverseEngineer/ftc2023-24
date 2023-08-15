package org.firstinspires.ftc.teamcode.skecore.hardware;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ServoController;

import org.firstinspires.ftc.teamcode.skecore.MessageCodes;
import org.firstinspires.ftc.teamcode.skecore.MessageReceiver;

public class VirtualCRServo extends VirtualHardwareDevice implements CRServo {
    private Direction direction = Direction.FORWARD;
    private double power = 0;

    public VirtualCRServo(String deviceName, MessageReceiver messageReceiver) {
        super(deviceName, messageReceiver);
    }

    @Override
    public void setDirection(Direction direction) {
        this.direction = direction;
        setPower(power);
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public void setPower(double power) {
        this.power = power;
        sendMessage(MessageCodes.SET_MOTOR_POWER, this.getByteName(), encode((direction==Direction.FORWARD?1:-1)*power));
    }

    @Override
    public double getPower() {
        return power;
    }

    @Override
    public void resetDeviceConfigurationForOpMode() {
        this.direction = Direction.FORWARD;
        setPower(0);
    }

    @Override
    public ServoController getController() {
        return null;
    }

    @Override
    public int getPortNumber() {
        return 0;
    }
}
