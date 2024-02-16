package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp
@Disabled
public class TrackWidthTuner extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        DcMotor leftFront = hardwareMap.get(DcMotorEx.class, "leftFrontDrive");
        DcMotor leftRear = hardwareMap.get(DcMotorEx.class, "leftRearDrive");
        DcMotor rightRear = hardwareMap.get(DcMotorEx.class, "rightRearDrive");
        DcMotor rightFront = hardwareMap.get(DcMotorEx.class, "rightFrontDrive");


        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftRear.setDirection(DcMotorSimple.Direction.REVERSE);

        leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftRear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftRear.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);


        if (leftFront == null) {
            telemetry.addData("null", "true");
        } else telemetry.addData("null", "false");
        telemetry.update();

        waitForStart();

        if (leftFront == null) {
            telemetry.addData("null", "true");
        } else telemetry.addData("null", "false");
        telemetry.update();

        while (opModeIsActive() && !isStopRequested()) {
            if (gamepad1.a) {
                leftFront.setPower(0.3);
                rightFront.setPower(-0.3);
                leftRear.setPower(0.3);
                rightRear.setPower(-0.3);
            } else {
                leftFront.setPower(0);
                rightFront.setPower(0);
                leftRear.setPower(0);
                rightRear.setPower(0);
            }

            telemetry.addData("left", leftRear.getCurrentPosition());
            telemetry.addData("right", leftFront.getCurrentPosition());
            telemetry.addData("track width", (leftFront.getCurrentPosition() + leftRear.getCurrentPosition())/(20*Math.PI));
            telemetry.update();
        }
    }
}
