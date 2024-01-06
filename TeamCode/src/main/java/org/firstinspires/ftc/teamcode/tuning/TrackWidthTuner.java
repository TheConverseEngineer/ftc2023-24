package org.firstinspires.ftc.teamcode.tuning;

import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.DrivetrainConstants;
import org.firstinspires.ftc.teamcode.extensions.CachedMotor;

/**
 * Tunes the track width for the odo pods
 */
@TeleOp(group="tuning")
public class TrackWidthTuner extends LinearOpMode {
    protected CachedMotor leftFrontDrive, rightFrontDrive, leftRearDrive, rightRearDrive;
    protected Motor.Encoder leftOdoPod, rightOdoPod, frontOdoPod;

    @Override
    public void runOpMode() throws InterruptedException {
        leftFrontDrive = new CachedMotor(hardwareMap, "leftFrontDrive", Motor.GoBILDA.RPM_312).setFFParams(DrivetrainConstants.Kt, DrivetrainConstants.Kstatic);
        rightFrontDrive = new CachedMotor(hardwareMap, "rightFrontDrive", Motor.GoBILDA.RPM_312).setFFParams(DrivetrainConstants.Kt, DrivetrainConstants.Kstatic);
        leftRearDrive = new CachedMotor(hardwareMap, "leftRearDrive", Motor.GoBILDA.RPM_312).setFFParams(DrivetrainConstants.Kt, DrivetrainConstants.Kstatic);
        rightRearDrive = new CachedMotor(hardwareMap, "rightRearDrive", Motor.GoBILDA.RPM_312).setFFParams(DrivetrainConstants.Kt, DrivetrainConstants.Kstatic);

        leftFrontDrive.setInverted(true);
        leftRearDrive.setInverted(true);

        leftOdoPod = leftFrontDrive.encoder.setDistancePerPulse(DrivetrainConstants.distancePerTick);
        rightOdoPod = rightRearDrive.encoder.setDistancePerPulse(DrivetrainConstants.distancePerTick);
        frontOdoPod = rightFrontDrive.encoder.setDistancePerPulse(DrivetrainConstants.distancePerTick);

        leftFrontDrive.stopAndResetEncoder();
        rightFrontDrive.stopAndResetEncoder();
        leftRearDrive.stopAndResetEncoder();

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive() && !isStopRequested() && !gamepad1.a) {
            double power = gamepad1.right_stick_y;

            leftFrontDrive.set(power);
            leftRearDrive.set(power);
            rightFrontDrive.set(-power);
            rightRearDrive.set(-power);

            telemetry.addLine("Please complete 12 rotations");
            telemetry.addLine("Press a to continue");
            telemetry.addData("Tracked rotation", (leftOdoPod.getDistance() - rightOdoPod.getDistance())/2);
            telemetry.update();
        }

        telemetry.addData("track width",(leftOdoPod.getDistance() - rightOdoPod.getDistance())/(48*Math.PI));
        telemetry.addData("forward offset", frontOdoPod.getDistance()/(24*Math.PI));
        telemetry.update();

        while (opModeIsActive() && !isStopRequested()) {
            idle();
        }
    }
}
