package org.firstinspires.ftc.teamcode.tests;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp
public class ActuatorDirectionTest extends LinearOpMode {

    private DcMotorEx armMotor, slideMotor1, slideMotor2;
    @Override
    public void runOpMode() {
        armMotor = hardwareMap.get(DcMotorEx.class, "armMotor");
        slideMotor1 = hardwareMap.get(DcMotorEx.class, "slideMotor1");
        slideMotor2 = hardwareMap.get(DcMotorEx.class, "slideMotor2");

        waitForStart();

        while (opModeIsActive() && !isStopRequested()) {
            if (gamepad1.y) armMotor.setPower(0.2);
            else armMotor.setPower(0);

            if (gamepad1.b) slideMotor1.setPower(0.2);
            else slideMotor1.setPower(0);

            if (gamepad1.a) slideMotor2.setPower(0.2);
            else slideMotor2.setPower(0);
        }
    }
}
