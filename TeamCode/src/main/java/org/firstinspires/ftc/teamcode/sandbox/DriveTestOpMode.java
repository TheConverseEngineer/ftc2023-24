package org.firstinspires.ftc.teamcode.sandbox;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp
@Disabled
public class DriveTestOpMode extends LinearOpMode {

    DcMotor leftFront, leftBack, rightFront, rightBack;
    public double inPerTick = 0.001056876526771654;
    public double lateralInPerTick = inPerTick;
    public double trackWidthTicks = 4802.36777868;

    double target = 0;

    double heading = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        leftFront = hardwareMap.get(DcMotorEx.class, "leftFrontDrive");
        leftBack = hardwareMap.get(DcMotorEx.class, "leftRearDrive");
        rightBack = hardwareMap.get(DcMotorEx.class, "rightRearDrive");
        rightFront = hardwareMap.get(DcMotorEx.class, "rightFrontDrive");

        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftBack.setDirection(DcMotorSimple.Direction.REVERSE);
        rightBack.setDirection(DcMotorSimple.Direction.REVERSE);

        leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        waitForStart();

        long lastLoop = System.nanoTime();

        while (opModeIsActive() && !isStopRequested()) {
            driveWithGamepad(gamepad1.left_stick_x, -gamepad1.left_stick_y, gamepad1.right_stick_x, gamepad1.right_trigger, gamepad1.left_trigger);

            heading = AngleUnit.normalizeRadians((leftFront.getCurrentPosition() - rightBack.getCurrentPosition())/(2*trackWidthTicks));
            telemetry.addData("head", heading);
            telemetry.update();
        }
    }

    public void driveWithGamepad(double x, double y, double rw, double speedTrigger, double brakeTrigger) {
        double theta = Math.atan2(y, x) - Math.PI/4 - heading;
        double rho = (x*x + y*y)*(0.6 + 0.4*speedTrigger)/(Math.max(Math.abs(Math.cos(theta)), Math.abs(Math.sin(theta))))*(1-brakeTrigger*0.5);
        double w = rw*(0.4+0.6*speedTrigger)*(1-brakeTrigger*0.5);
        if (rho+Math.abs(rw) > 1) {
            w = rw/(rho + Math.abs(rw));
            rho /= (rho + Math.abs(rw));
        }
        leftFront.setPower(rho*Math.cos(theta)+w);
        rightFront.setPower(rho*Math.sin(theta)-w);
        rightBack.setPower(rho*Math.cos(theta)-w);
        leftBack.setPower(rho*Math.sin(theta)+w);
    }
}
