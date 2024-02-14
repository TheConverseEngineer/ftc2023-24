package org.firstinspires.ftc.teamcode.sandbox;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.common.command.CommandOpMode;
import org.firstinspires.ftc.teamcode.common.trajectory.DriveSubsystem;

@TeleOp
@Disabled
public class DrivetrainSimTestOpMode extends CommandOpMode {
    DcMotor leftFront, leftBack, rightFront, rightBack;
    private double heading = 0;

    @Override
    public void initialize() {
        enableSimMode(new Pose2d());
        enableDashboard();

        leftFront = hardwareMap.get(DcMotorEx.class, "leftFrontDrive");
        leftBack = hardwareMap.get(DcMotorEx.class, "leftRearDrive");
        rightBack = hardwareMap.get(DcMotorEx.class, "rightRearDrive");
        rightFront = hardwareMap.get(DcMotorEx.class, "rightFrontDrive");

        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // TODO: reverse motor directions if needed
        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftBack.setDirection(DcMotorSimple.Direction.REVERSE);
        rightBack.setDirection(DcMotorSimple.Direction.REVERSE);

        leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    @Override
    public void run() {
        heading = AngleUnit.normalizeRadians((leftFront.getCurrentPosition() - rightBack.getCurrentPosition())* DriveSubsystem.ODO_IN_PER_TICK[2] / DriveSubsystem.ODO_TRACK_WIDTH);

        telemetry.addData("head", heading);

        driveWithGamepad(gamepad1.left_stick_x, -gamepad1.left_stick_y, gamepad1.right_stick_x, gamepad1.right_trigger, gamepad1.left_trigger);
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
