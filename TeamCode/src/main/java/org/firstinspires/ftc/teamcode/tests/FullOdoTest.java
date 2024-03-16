package org.firstinspires.ftc.teamcode.tests;

import android.util.Size;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.common.command.CommandOpMode;
import org.firstinspires.ftc.teamcode.common.simulation.VirtualDummyMotorEx;
import org.firstinspires.ftc.teamcode.common.trajectory.DriveSubsystem;
import org.firstinspires.ftc.teamcode.common.trajectory.FusedOdoSubsystem;
import org.firstinspires.ftc.teamcode.common.trajectory.Knot;
import org.firstinspires.ftc.teamcode.common.trajectory.OdometrySubsystem;
import org.firstinspires.ftc.teamcode.common.utils.DashboardManager;
import org.firstinspires.ftc.teamcode.common.vision.AprilTagOdometryProcessor;
import org.firstinspires.ftc.teamcode.vision.TeamElementDetectionPipeline;
import org.firstinspires.ftc.vision.VisionPortal;


@TeleOp
public class FullOdoTest extends CommandOpMode {

    VisionPortal visionPortal;
    AprilTagOdometryProcessor aprilTagProcessor;
    public static final Knot startKnot = new Knot(13.173, -64.541, -90, 90);


    FusedOdoSubsystem odometrySubsystem;

    DriveSubsystem drive;
    AprilTagOdometryProcessor localizer;
    TeamElementDetectionPipeline elementDetection;
    VisionPortal portal;

    @Override
    public void initialize() {

        enableDashboard();

        drive = new DriveSubsystem(hardwareMap);
        odometrySubsystem = drive.getOdometry();

        localizer = AprilTagOdometryProcessor.generate(
                drive.getOdometry()::getPoseEstimate,
                drive.getOdometry()::applyOffset,
                0,0,0,0
        );

        elementDetection = new TeamElementDetectionPipeline(TeamElementDetectionPipeline.Alliance.RED);

        portal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(CameraName.class, "webcam 1"))
                .setCameraResolution(new Size(640, 360))
                //.addProcessor(localizer)
                .addProcessor(elementDetection)
                .build();

        scheduler.registerSubsystem(drive);
    }

    @Override
    public void run() {
        drive.driveWithGamepad(gamepad1);
        //telemetry.addData("detected", aprilTagProcessor.getDetectedItems());
        //telemetry.addData("heading error", aprilTagProcessor.getAverageHeadingError());
        telemetry.addLine(odometrySubsystem.getPoseEstimate().toString());
        telemetry.update();

        DashboardManager.getInstance().drawRobot(odometrySubsystem.getPoseEstimate());
    }
}
