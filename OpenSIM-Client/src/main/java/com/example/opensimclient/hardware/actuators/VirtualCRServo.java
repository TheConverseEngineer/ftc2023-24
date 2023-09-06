package com.example.opensimclient.hardware.actuators;

import com.example.opensimclient.MessageCodes;
import com.example.opensimclient.SocketClient;
import com.example.opensimclient.hardware.VirtualHardwareDevice;
import com.example.opensimclient.utils.Utils;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.ServoController;

public class VirtualCRServo extends VirtualHardwareDevice implements CRServo {
    private DcMotorSimple.Direction direction = DcMotorSimple.Direction.FORWARD;
    private double power = 0;

    public VirtualCRServo(String deviceName, SocketClient messageReceiver) {
        super(deviceName, messageReceiver);
    }

    @Override
    public void setDirection(DcMotorSimple.Direction direction) {
        this.direction = direction;
        setPower(power);
    }

    @Override
    public DcMotorSimple.Direction getDirection() {
        return direction;
    }

    @Override
    public void setPower(double power) {
        power = Utils.clamp(power, -1, 1);
        this.power = power;
        sendMessage(MessageCodes.SET_MOTOR_POWER, this.getByteName(), encode((direction== DcMotorSimple.Direction.FORWARD?1:-1)*power));
    }

    @Override
    public double getPower() {
        return power;
    }

    @Override
    public void resetDeviceConfigurationForOpMode() {
        this.direction = DcMotorSimple.Direction.FORWARD;
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
