package org.firstinspires.ftc.teamcode.thundercore.hardware;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.CRServoImplEx;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;

import java.util.List;

public class PhysicalHardwareMap implements ThunderHardwareMap {

    private final HardwareMap hardwareMap;
    private final List<LynxModule> hubs;

    public PhysicalHardwareMap(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;
        this.hubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : this.hubs) hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
    }

    @Override
    public DcMotorEx getMotor(String name, boolean reversed) {
        DcMotorEx motor = this.hardwareMap.get(DcMotorEx.class, name);
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
        ServoImplEx servo = this.hardwareMap.get(ServoImplEx.class, name);
        servo.setPwmRange(new PwmControl.PwmRange(500, 2500));
        return servo;
    }

    @Override
    public CRServo getCRServo(String name) {
        CRServoImplEx servo = this.hardwareMap.get(CRServoImplEx.class, name);
        servo.setPwmRange(new PwmControl.PwmRange(500, 2500));
        return servo;
    }

    @Override
    public void updateCache() {
        for (LynxModule hub : hubs) hub.clearBulkCache();
    }
}
