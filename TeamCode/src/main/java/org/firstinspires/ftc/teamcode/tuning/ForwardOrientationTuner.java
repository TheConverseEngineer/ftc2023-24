package org.firstinspires.ftc.teamcode.tuning;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.DrivetrainConstants;
import org.firstinspires.ftc.teamcode.extensions.CachedMotor;

/**
 * Ensures that the orientation of each drive motor is correct
 */
@TeleOp(group="tuning")
public class ForwardOrientationTuner extends LinearOpMode {

    CachedMotor leftFrontDrive, rightFrontDrive, leftRearDrive, rightRearDrive;

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        leftFrontDrive = new CachedMotor(hardwareMap, "leftFrontDrive", Motor.GoBILDA.RPM_312).setFFParams(DrivetrainConstants.Kt, DrivetrainConstants.Kstatic);
        rightFrontDrive = new CachedMotor(hardwareMap, "rightFrontDrive", Motor.GoBILDA.RPM_312).setFFParams(DrivetrainConstants.Kt, DrivetrainConstants.Kstatic);
        leftRearDrive = new CachedMotor(hardwareMap, "leftRearDrive", Motor.GoBILDA.RPM_312).setFFParams(DrivetrainConstants.Kt, DrivetrainConstants.Kstatic);
        rightRearDrive = new CachedMotor(hardwareMap, "rightRearDrive", Motor.GoBILDA.RPM_312).setFFParams(DrivetrainConstants.Kt, DrivetrainConstants.Kstatic);

        leftFrontDrive.setInverted(true);
        leftRearDrive.setInverted(true);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        telemetry.addLine("Press a to drive forward");
        telemetry.update();

        while (opModeIsActive() && !isStopRequested()) {
            if (gamepad1.a) {
                leftFrontDrive.set(0.5);
                rightFrontDrive.set(0.5);
                leftRearDrive.set(0.5);
                rightRearDrive.set(0.5);
            } else {
                leftFrontDrive.set(0);
                rightFrontDrive.set(0);
                leftRearDrive.set(0);
                rightRearDrive.set(0);
            }

        }
    }
}
