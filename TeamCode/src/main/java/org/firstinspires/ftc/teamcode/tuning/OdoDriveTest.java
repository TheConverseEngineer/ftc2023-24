package org.firstinspires.ftc.teamcode.tuning;

import com.arcrobotics.ftclib.command.OdometrySubsystem;
import com.arcrobotics.ftclib.geometry.Pose2d;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.kinematics.HolonomicOdometry;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.DrivetrainConstants;
import org.firstinspires.ftc.teamcode.extensions.CachedMotor;
import org.firstinspires.ftc.teamcode.trajectory.command.MecanumDriveSubsystem;

import java.util.List;

@TeleOp
public class OdoDriveTest extends LinearOpMode {
    protected CachedMotor leftFrontDrive, rightFrontDrive, leftRearDrive, rightRearDrive;
    protected Motor.Encoder leftOdoPod, rightOdoPod, frontOdoPod;

    MecanumDriveSubsystem drive;
    OdometrySubsystem odo;

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

        drive = new MecanumDriveSubsystem(leftFrontDrive, rightFrontDrive, leftRearDrive, rightRearDrive);
        odo = new OdometrySubsystem(new HolonomicOdometry(
                leftOdoPod::getDistance, rightOdoPod::getDistance, frontOdoPod::getDistance,
                DrivetrainConstants.odoTrackWidth, DrivetrainConstants.odoForwardOffset
        ));

        List<LynxModule> modules = hardwareMap.getAll(LynxModule.class);
        modules.forEach(i -> i.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL));

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        double lastLoopTime = System.nanoTime()/1000000.0;

        while (opModeIsActive() && !isStopRequested()) {
            modules.forEach(LynxModule::clearBulkCache);
            drive.periodic();
            odo.periodic();

            double rx = gamepad1.right_stick_x;
            double ry = -gamepad1.right_stick_y;

            drive.driveWithGamepad(rx, ry, gamepad1.left_stick_x);

            double dTime = (System.nanoTime()/1000000.0) - lastLoopTime;
            telemetry.addData("lt", dTime);
            telemetry.addData("rx", rx);
            telemetry.addData("ry", ry);

            Pose2d pose = odo.getPose();

            telemetry.addData("x", pose.getX());
            telemetry.addData("y", pose.getY());
            telemetry.addData("t", Math.toDegrees(pose.getHeading()));

            telemetry.update();
        }
    }
}
