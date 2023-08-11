package org.firstinspires.ftc.teamcode.skecore.hardware;

import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.skecore.MessageReceiver;

import java.nio.charset.StandardCharsets;

public class VirtualDcMotorSimple extends VirtualHardwareDevice implements DcMotorSimple {
    private Direction direction = Direction.FORWARD;
    private double power = 0;

    public VirtualDcMotorSimple(String deviceName, MessageReceiver messageReceiver) {
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
        sendMessage(this.getByteName(), encode((direction==Direction.FORWARD?1:-1)*power));
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
