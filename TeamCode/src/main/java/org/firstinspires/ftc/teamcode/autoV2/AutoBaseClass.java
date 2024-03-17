package org.firstinspires.ftc.teamcode.autoV2;

import android.util.Size;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;

import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.teamcode.common.command.Command;
import org.firstinspires.ftc.teamcode.common.command.CommandOpMode;
import org.firstinspires.ftc.teamcode.common.command.prefabs.InstantCommand;
import org.firstinspires.ftc.teamcode.common.command.prefabs.ParallelCommand;
import org.firstinspires.ftc.teamcode.common.command.prefabs.SequentialCommand;
import org.firstinspires.ftc.teamcode.common.command.prefabs.WaitCommand;
import org.firstinspires.ftc.teamcode.common.command.prefabs.WaitUntilCommand;
import org.firstinspires.ftc.teamcode.common.trajectory.DriveSubsystem;
import org.firstinspires.ftc.teamcode.common.trajectory.Knot;
import org.firstinspires.ftc.teamcode.subsystems.GripperSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.SlideSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.WristSubsystem;
import org.firstinspires.ftc.teamcode.vision.TeamElementDetectionPipeline;
import org.firstinspires.ftc.vision.VisionPortal;

public abstract class AutoBaseClass extends CommandOpMode {
    DriveSubsystem drive;
    SlideSubsystem actuator;
    WristSubsystem wrist;
    GripperSubsystem gripper;

    TeamElementDetectionPipeline elementDetection;
    VisionPortal portal;

    private static final double spikeExtensionWait = 1;
    private static final double depositExtension = 4.25;

    @Override
    public void initialize() {
        // Subsystems
        drive = new DriveSubsystem(hardwareMap, new Pose2d(0, 0, Math.toRadians(initialHeading())));
        actuator = new SlideSubsystem(hardwareMap);
        gripper = new GripperSubsystem(hardwareMap); gripper.closeClaw();
        wrist = new WristSubsystem(hardwareMap);
        scheduler.registerSubsystem(drive, actuator, gripper, wrist);

        // Vision
        elementDetection = new TeamElementDetectionPipeline(getAlliance());

        portal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(CameraName.class, "webcam 1"))
                .setCameraResolution(new Size(640, 360))
                .addProcessor(elementDetection)
                .build();
    }

    protected Command generateAutoPath(double initialSplineHeading, double spikeExtension, Vector2d spikeDepositPoint, Vector2d visionDeposit) {
        Trajectory spike = drive.buildTrajectory(new Pose2d(0, 0, Math.toRadians(initialHeading())), initialSplineHeading)
                .splineToSplineHeading(intermediateSpikePoint())
                .splineToConstantHeading(spikeDepositPoint, 0)
                .build();

        Trajectory toVision = drive.buildTrajectory(new Pose2d(spikeDepositPoint.getX(), spikeDepositPoint.getY(), 180), 0)
                .splineToConstantHeading(visionDeposit, 0)
                .build();

        return new SequentialCommand(
                new ParallelCommand(
                        drive.followTrajectory(spike),
                        new SequentialCommand(
                                new WaitCommand(spikeExtensionWait),
                                new InstantCommand(() -> actuator.setSlideTarget(spikeExtension)),
                                new InstantCommand(() -> wrist.spikeDropPosition()),
                                new WaitCommand(0.5)
                        ),
                        new WaitUntilCommand(() -> Math.abs(actuator.slidePosition - spikeExtension) < 0.3)
                ),
                new InstantCommand(gripper::halfOpenClaw),
                new WaitCommand(0.5),
                new ParallelCommand(
                        drive.followTrajectory(toVision),
                        new SequentialCommand(
                                new WaitCommand(0.5),
                                new InstantCommand(() -> actuator.setSlideTarget(depositExtension)),
                                new InstantCommand(() -> wrist.outtakePosition()),
                                new WaitUntilCommand(() -> Math.abs(actuator.slidePosition - depositExtension) < 0.3),
                                new InstantCommand(() -> actuator.setArmTarget(120)),
                                new WaitCommand(0.1),
                                new WaitUntilCommand(() -> Math.abs(actuator.armPosition - 2) < 0.03)
                        )
                ),
                new InstantCommand(gripper::openClaw),
                new WaitCommand(.75),
                new ParallelCommand(
                        //TODO: PARK
                )
        );
    }

    @Override
    public void begin() {
        onStart();
    }

    protected abstract Knot intermediateSpikePoint();
    protected abstract TeamElementDetectionPipeline.Alliance getAlliance();
    /** In degrees */
    protected abstract double initialHeading();

    protected abstract void onInit();
    protected abstract void onStart();

    protected final TeamElementDetectionPipeline.Detection getVisionDetection() {
        return elementDetection.getDetection();
    }

    protected final void disableWebcam() {
        portal.setProcessorEnabled(elementDetection, false);
    }
}