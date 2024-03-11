package org.firstinspires.ftc.teamcode.auto;

import android.util.Size;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.drive.MecanumDrive;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.teamcode.common.command.CommandOpMode;
import org.firstinspires.ftc.teamcode.common.command.prefabs.SequentialCommand;
import org.firstinspires.ftc.teamcode.common.command.prefabs.WaitCommand;
import org.firstinspires.ftc.teamcode.common.trajectory.DriveSubsystem;
import org.firstinspires.ftc.teamcode.common.utils.DashboardManager;
import org.firstinspires.ftc.teamcode.common.vision.AprilTagOdometryProcessor;
import org.firstinspires.ftc.teamcode.vision.TeamElementDetectionPipeline;
import org.firstinspires.ftc.teamcode.vision.VisionSubsystem;
import org.firstinspires.ftc.vision.VisionPortal;
import org.opencv.core.Mat;

@TeleOp(name = "Red 2+5") @Config
public class RedMaxCycle extends CommandOpMode {
    DriveSubsystem drive;
    VisionSubsystem camera;

    public static double DEPOSIT_X = 51.5;
    public static double DEPOSIT_CENTER_Y = -34.5;
    public static boolean enabled = true;

    //21.5in extension on cycle

    public static class PRESETS {
        public static final Pose2d startPose = new Pose2d(-26.512, -62.875, Math.toRadians(270));
        public static final Pose2d stackIntakePos = new Pose2d(-24, -15, Math.PI);
        public static final double[] spikeExtension = {10.2, 0, 0}; // B, C, A
        public static final Pose2d[] spikePoses = {new Pose2d(-37.5, -25, Math.toRadians(340)),
                                                    new Pose2d(-21.5, -12.5, Math.toRadians(226)),
                                                    new Pose2d(-40, -11.5, Math.toRadians(270))};
        public static final double[] spikeSplineHeadings = {0,0, Math.PI};

        public final Trajectory[] toSpike = new Trajectory[3];
        public final Trajectory[] spikeToStack = new Trajectory[3];
        public final Trajectory[] stackToBackdrop = new Trajectory[3];
        public final Trajectory[] backdropToStack = new Trajectory[3];

        public PRESETS(DriveSubsystem drive) {
            for (int i = 0; i < 3; i++) {
                toSpike[i] = drive.buildTrajectory(startPose, Math.PI/2)
                        .splineToLinearHeading(spikePoses[i], spikeSplineHeadings[i])
                        .build();

                spikeToStack[i] = drive.buildTrajectory(spikePoses[i],Math.PI/2)
                        .splineToLinearHeading(stackIntakePos, 0)
                        .build();

                stackToBackdrop[i] = drive.buildTrajectory(stackIntakePos, 0)
                        .splineToConstantHeading(new Vector2d(DEPOSIT_X, DEPOSIT_CENTER_Y + 6*(i-1)), 0)
                        .build();

                backdropToStack[i] = drive.buildTrajectory(new Pose2d(DEPOSIT_X, DEPOSIT_CENTER_Y + 6*(i-1), Math.PI), Math.PI)
                        .splineToConstantHeading(stackIntakePos.vec(), Math.PI)
                        .build();
            }
        }
    }

    public static int opt = 0;

    @Override
    public void initialize() {

        drive = new DriveSubsystem(hardwareMap, PRESETS.startPose);
        PRESETS paths = new PRESETS(drive);

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
        scheduler.scheduleCommand(new SequentialCommand(
                drive.followTrajectory(paths.toSpike[opt]),
                new WaitCommand(1),
                drive.followTrajectory(paths.spikeToStack[opt]),
                new WaitCommand(1),
                drive.followTrajectory(paths.stackToBackdrop[opt]),
                new WaitCommand(1),
                drive.followTrajectory(paths.backdropToStack[opt]),
                new WaitCommand(1),
                drive.followTrajectory(paths.stackToBackdrop[opt]),
                new WaitCommand(1),
                drive.followTrajectory(paths.backdropToStack[opt]),
                new WaitCommand(1),
                drive.followTrajectory(paths.stackToBackdrop[opt]),
                new WaitCommand(1),
                drive.followTrajectory(paths.backdropToStack[opt]),
                new WaitCommand(1),
                drive.followTrajectory(paths.stackToBackdrop[opt]),
                new WaitCommand(1),
                drive.followTrajectory(paths.backdropToStack[opt]),
                new WaitCommand(1),
                drive.followTrajectory(paths.stackToBackdrop[opt])
        ));
    }

    @Override
    public void run() {
        if (!enabled) drive.driveWithGamepad(gamepad1);

        TelemetryPacket packet = new TelemetryPacket();
        DashboardManager.drawRobot(packet, drive.getOdometry().getPoseEstimate());
        packet.put("pos", drive.getOdometry().getPoseEstimate().toString());
        FtcDashboard.getInstance().sendTelemetryPacket(packet);
    }
}
