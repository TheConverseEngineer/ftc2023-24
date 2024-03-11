package org.firstinspires.ftc.teamcode.auto;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;

import org.firstinspires.ftc.teamcode.common.command.CommandOpMode;
import org.firstinspires.ftc.teamcode.common.trajectory.DriveSubsystem;
import org.firstinspires.ftc.teamcode.common.utils.DashboardManager;
import org.firstinspires.ftc.teamcode.vision.TeamElementDetectionPipeline;
import org.firstinspires.ftc.teamcode.vision.VisionSubsystem;

public class RedMaxCycle extends CommandOpMode {
    DriveSubsystem drive;
    VisionSubsystem camera;

    @Override
    public void initialize() {
        drive = new DriveSubsystem(hardwareMap);
        //camera = new VisionSubsystem(hardwareMap, TeamElementDetectionPipeline.Alliance.RED);

        scheduler.registerSubsystem(drive);
    }

    @Override
    public void run() {
        telemetry.addData("pos", drive.getOdometry().getPoseEstimate().toString());
        drive.driveWithGamepad(gamepad1);

        TelemetryPacket packet = new TelemetryPacket();
        DashboardManager.drawRobot(packet, drive.getOdometry().getPoseEstimate());
        FtcDashboard.getInstance().sendTelemetryPacket(packet);

    }
}
