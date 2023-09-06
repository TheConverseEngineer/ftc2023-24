package com.example.opensimclient.hardware.actuators;

import com.example.opensimclient.MessageCodes;
import com.example.opensimclient.SocketClient;
import com.example.opensimclient.hardware.VirtualHardwareDevice;
import com.example.opensimclient.utils.Utils;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class VirtualDcMotorSimple extends VirtualHardwareDevice implements DcMotorSimple {
    private DcMotorSimple.Direction direction = Direction.FORWARD;
    private double power = 0;

    public VirtualDcMotorSimple(String deviceName, SocketClient messageReceiver) {
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
        this.power = Utils.clamp(power, -1, 1);
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
}
