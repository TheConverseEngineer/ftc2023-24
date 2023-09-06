package org.firstinspires.ftc.teamcode.general.skecore.testOpMode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

// This class runs directly in the simulator!
public class DcMotorSimpleTest extends LinearOpMode {

    @Override
    public void runOpMode() {
        DcMotor arm = hardwareMap.get(DcMotor.class, "armMotor");
        DcMotorEx motor = hardwareMap.get(DcMotorEx.class, "elevator");

        motor.setPower(0.8);
        arm.setPower(0.3);

        telemetry.addData("Waiting for", "start"); telemetry.update();
        waitForStart();

        motor.setPower(0.7);
        arm.setPower(0.8);

        sleep(2000);

        motor.setPower(0);
        arm.setPower(0);

        telemetry.addData("Program", "complete!"); telemetry.update();
    }
}
