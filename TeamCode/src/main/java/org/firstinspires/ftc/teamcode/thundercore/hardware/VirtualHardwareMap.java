package org.firstinspires.ftc.teamcode.thundercore.hardware;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.skecore.hardware.DcMotorTypes;
import org.firstinspires.ftc.teamcode.skecore.hardware.VirtualCRServo;
import org.firstinspires.ftc.teamcode.skecore.hardware.VirtualDcMotorEx;
import org.firstinspires.ftc.teamcode.skecore.hardware.VirtualServo;

import java.util.HashSet;
import java.util.Set;

@Deprecated
public class VirtualHardwareMap implements ThunderHardwareMap{

    private final Set<String> names = new HashSet<>();

    @Override
    public DcMotorEx getMotor(String name, boolean reversed) {
        checkUsage(name);
        DcMotorEx motor = new VirtualDcMotorEx(DcMotorTypes.GOBILDA_435);
        if (reversed) motor.setDirection(DcMotorSimple.Direction.REVERSE);
        else motor.setDirection(DcMotorSimple.Direction.FORWARD);
        return motor;
    }

    @Override
    public DcMotorEx getMotor(String name) {
        return getMotor(name, false);
    }

    @Override
    public Servo getServo(String name) {
        checkUsage(name);
        return new VirtualServo(name, null);
    }

    @Override
    public CRServo getCRServo(String name) {
        checkUsage(name);
        return new VirtualCRServo(name, null);
    }

    private void checkUsage(String name) {
        if (names.contains(name)) throw new RuntimeException("A hardware device named " + name + " was created twice.");
        names.add(name);
    }
}
