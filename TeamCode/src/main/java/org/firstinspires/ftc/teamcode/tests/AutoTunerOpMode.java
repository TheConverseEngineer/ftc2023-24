package org.firstinspires.ftc.teamcode.tests;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;
import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.FLOAT;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.ServoImplEx;

import org.firstinspires.ftc.teamcode.common.trajectory.DriveSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.SlideSubsystem;

import java.util.List;

@Disabled
public class AutoTunerOpMode extends LinearOpMode {

    ServoImplEx servoU, servoL;

    DcMotorEx armMotor, slideMotor;

    DriveSubsystem drive;

    @Override
    public void runOpMode() throws InterruptedException {
        servoU = hardwareMap.get(ServoImplEx.class, "upperWrist");
        servoL = hardwareMap.get(ServoImplEx.class, "lowerWrist");

        drive = new DriveSubsystem(hardwareMap);
        drive.getOdometry().setPoseEstimate(new Pose2d(0, 0, Math.PI/2));

        servoU.setPwmRange(new PwmControl.PwmRange(500, 2500));

        armMotor = hardwareMap.get(DcMotorEx.class, "armMotor");
        slideMotor = hardwareMap.get(DcMotorEx.class, "slideMotor1");

        armMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        armMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        slideMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        armMotor.setZeroPowerBehavior(FLOAT);
        slideMotor.setZeroPowerBehavior(FLOAT);

        List<LynxModule> hubs = hardwareMap.getAll(LynxModule.class);
        hubs.forEach(module -> module.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL));

        waitForStart();

        int targetU = 50, targetL = 50;
        boolean lastR = false, lastL = false;
        boolean lastR1 = false, lastL1 = false;
        while (opModeIsActive() && !isStopRequested()) {
            // bulk cache
            hubs.forEach(LynxModule::clearBulkCache);

            // Arm and slides
            telemetry.addData("slide", slideMotor.getCurrentPosition() / SlideSubsystem.SLIDE_TICKS_PER_INCH);
            telemetry.addData("arm", Math.toDegrees(armMotor.getCurrentPosition() / SlideSubsystem.ARM_TICKS_PER_RAD));

            // Drivetrain
            drive.earlyPeriodic();
            drive.periodic();
            drive.driveWithGamepad(gamepad2);
            telemetry.addData("pos", drive.getOdometry().getPoseEstimate().toString());

            // Wrist Servos
            if (gamepad1.left_bumper) {
                if (!lastL) {
                    lastL = true;
                    targetU--;
                }
            } else lastL = false;

            if (gamepad1.right_bumper) {
                if (!lastR) {
                    lastR = true;
                    targetU++;
                }
            } else lastR = false;

            if (gamepad1.y) servoU.setPosition(targetU/100.0);
            telemetry.addData("upperPos", targetU);

            if (gamepad1.left_trigger > 0.7) {
                if (!lastL1) {
                    lastL1 = true;
                    targetL--;
                }
            } else lastL1 = false;

            if (gamepad1.right_trigger > 0.7) {
                if (!lastR1) {
                    lastR1 = true;
                    targetL++;
                }
            } else lastR1 = false;

            if (gamepad1.b) servoL.setPosition(targetL/100.0);
            telemetry.addData("lowerPos", targetL);
            telemetry.update();
        }
    }
}
