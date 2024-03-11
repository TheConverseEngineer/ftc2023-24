package org.firstinspires.ftc.teamcode.auto;

import android.util.Size;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.teamcode.common.command.CommandOpMode;
import org.firstinspires.ftc.teamcode.common.trajectory.DriveSubsystem;
import org.firstinspires.ftc.teamcode.common.utils.DashboardManager;
import org.firstinspires.ftc.teamcode.common.vision.AprilTagOdometryProcessor;
import org.firstinspires.ftc.teamcode.vision.TeamElementDetectionPipeline;
import org.firstinspires.ftc.teamcode.vision.VisionSubsystem;
import org.firstinspires.ftc.vision.VisionPortal;

@TeleOp(name = "Red 2+5")
public class RedMaxCycle extends CommandOpMode {
    DriveSubsystem drive;
    VisionSubsystem camera;

    @Override
    public void initialize() {
        drive = new DriveSubsystem(hardwareMap, new Pose2d(0,0,Math.PI));
        //camera = new VisionSubsystem(hardwareMap, TeamElementDetectionPipeline.Alliance.RED);
        AprilTagOdometryProcessor localizer = AprilTagOdometryProcessor.generate(
                drive.getOdometry()::getPoseEstimate,
                drive.getOdometry()::applyOffset,
                0,0,0,0
        );

        VisionPortal portal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(CameraName.class, "webcam 1"))
                .setCameraResolution(new Size(640, 480))
                .addProcessor(localizer)
                .build();



        scheduler.registerSubsystem(drive);
    }

    @Override
    public void run() {
        drive.driveWithGamepad(gamepad1);

        TelemetryPacket packet = new TelemetryPacket();
        DashboardManager.drawRobot(packet, drive.getOdometry().getPoseEstimate());
        packet.put("pos", drive.getOdometry().getPoseEstimate().toString());
        FtcDashboard.getInstance().sendTelemetryPacket(packet);
    }
}
