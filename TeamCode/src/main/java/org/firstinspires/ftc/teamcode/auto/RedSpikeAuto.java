package org.firstinspires.ftc.teamcode.auto;

import android.util.Size;

import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.teamcode.common.command.CommandOpMode;
import org.firstinspires.ftc.teamcode.common.command.prefabs.InstantCommand;
import org.firstinspires.ftc.teamcode.common.command.prefabs.SequentialCommand;
import org.firstinspires.ftc.teamcode.common.command.prefabs.WaitCommand;
import org.firstinspires.ftc.teamcode.common.trajectory.DriveSubsystem;
import org.firstinspires.ftc.teamcode.common.trajectory.Knot;
import org.firstinspires.ftc.teamcode.common.vision.AprilTagOdometryProcessor;
import org.firstinspires.ftc.teamcode.subsystems.GripperSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.SlideSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.WristSubsystem;
import org.firstinspires.ftc.teamcode.vision.TeamElementDetectionPipeline;
import org.firstinspires.ftc.vision.VisionPortal;

@Autonomous
public class RedSpikeAuto extends CommandOpMode {
    DriveSubsystem drive;
    SlideSubsystem actuator;
    WristSubsystem wrist;
    GripperSubsystem gripper;

    public static final Knot startKnot = new Knot(0, 0, 180, 0);

    TeamElementDetectionPipeline elementDetection;
    VisionPortal portal;

    Trajectory left, center, right;

    @Override
    public void initialize() {
        drive = new DriveSubsystem(hardwareMap, startKnot.getPose());
        actuator = new SlideSubsystem(hardwareMap);
        gripper = new GripperSubsystem(hardwareMap);
        wrist = new WristSubsystem(hardwareMap);
        scheduler.registerSubsystem(drive, actuator, gripper, wrist);

        drive.getOdometry().setPoseEstimate(startKnot.getPose());

        elementDetection = new TeamElementDetectionPipeline(TeamElementDetectionPipeline.Alliance.RED);
        gripper.closeClaw();

        portal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(CameraName.class, "webcam 1"))
                .setCameraResolution(new Size(640, 360))
                //.addProcessor(localizer)
                .addProcessor(elementDetection)
                .build();

         center = drive.buildTrajectory(startKnot)
                .splineToConstantHeading(new Vector2d(0, 27), 0)
                .build();

         right = drive.buildTrajectory(startKnot)
                .splineToConstantHeading(new Vector2d(24, 12), 90)
                .build();


    }

    @Override
    public void begin() {
        if (elementDetection.getDetection() == TeamElementDetectionPipeline.Detection.LEFT) {
            scheduler.scheduleCommand(new SequentialCommand(
                    drive.followTrajectory(right),
                    new InstantCommand(wrist::visionOuttakePosition),
                    new WaitCommand(3),
                    new InstantCommand(gripper::openClaw)
            ));
        } else
            scheduler.scheduleCommand(new SequentialCommand(
                drive.followTrajectory(center),
                new InstantCommand(wrist::visionOuttakePosition),
                new WaitCommand(3),
                new InstantCommand(gripper::openClaw)
        ));
    }

    @Override
    protected void initLoop() {
        telemetry.addData("v", elementDetection.getDetection());
        telemetry.update();
    }
}
