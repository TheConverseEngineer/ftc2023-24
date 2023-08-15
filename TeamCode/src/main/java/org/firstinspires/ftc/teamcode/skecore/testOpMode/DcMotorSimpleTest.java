package org.firstinspires.ftc.teamcode.skecore.testOpMode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class DcMotorSimpleTest extends LinearOpMode {
    // here is the exact opmode
    @Override
    public void runOpMode() {
        DcMotorSimple motor = hardwareMap.get(DcMotorSimple.class, "motor");
        DcMotorSimple arm = hardwareMap.get(DcMotorSimple.class, "armMotor");
        motor.setPower(0.8);
        arm.setPower(0.3);

        waitForStart();
        motor.setPower(0.7);
        arm.setPower(0.8);
        sleep(2000);
        motor.setPower(0);
        arm.setPower(0);
    }
}
