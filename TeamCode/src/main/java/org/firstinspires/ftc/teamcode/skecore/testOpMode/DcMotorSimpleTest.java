package org.firstinspires.ftc.teamcode.skecore.testOpMode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class DcMotorSimpleTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        DcMotorSimple motor = hardwareMap.get(DcMotorSimple.class, "motor");

        waitForStart();
        motor.setPower(1);
        while (isStarted() && !isStopRequested()) {}
        motor.setPower(0);
    }
}
