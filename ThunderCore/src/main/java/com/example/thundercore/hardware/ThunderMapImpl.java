package com.example.thundercore.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class ThunderMapImpl implements ThunderMap {

    private final HardwareMap hardwareMap;

    /** Creates a new ThunderMapImpl instance */
    public ThunderMapImpl(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;
    }

    @Override
    public ThunderMotor getMotor(String name, boolean reversed, boolean shouldBrake) {
        DcMotorEx motor = hardwareMap.get(DcMotorEx.class, name);

        if (reversed) motor.setDirection(DcMotorSimple.Direction.REVERSE);
        else motor.setDirection(DcMotorSimple.Direction.FORWARD);

        if (shouldBrake) motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        else motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        return new ThunderMotorImpl(motor);
    }
}
