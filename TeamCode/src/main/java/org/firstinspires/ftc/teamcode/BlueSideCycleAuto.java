package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.checkerframework.checker.units.qual.K;
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
import org.firstinspires.ftc.teamcode.vision.VisionSubsystem;

import java.util.Scanner;


@Autonomous(preselectTeleOp = "StandardTeleOp")
@Config
public class BlueSideCycleAuto extends CommandOpMode {

    DriveSubsystem drive;
    VisionSubsystem camera;
    SlideSubsystem actuator;
    WristSubsystem wrist;
    GripperSubsystem gripper;

    public static final Knot startKnot = new Knot(0, 0, 90, -90),
            spikeIntermediate = new Knot(21, -14, 180, 0),
            lrSpikeDeposit = new Knot(28, -21.725, 180, -90),
            centerSpikeDeposit = new Knot(31.4, -33.5, 180, -90);

    public static final double centerSpikeExtension = 9.5,
            leftSpikeExtension = 0,
            rightSpikeExtension = 23;

    public static final double depositX = 43.8,
            depositLY = -18.15,
            depositCY = -24.37,
            depositRY = -30.3;

    public static double spikeExtensionWait = 1;

    public static final Pose2d cycleIntermediate = new Pose2d(26.5, -43.5, Math.PI),
                                cycleIntakePos = new Pose2d(-37.5, -43.5, Math.PI),
                                cycleOuttakePos = new Pose2d(43.8, -13.75, Math.PI);

    public static final double cycleOneExtension = 21.6,
                               cycleTwoExtension = 22.5;

    @Override
    public void initialize() {
        drive = new DriveSubsystem(hardwareMap);
        camera = new VisionSubsystem(hardwareMap, TeamElementDetectionPipeline.Alliance.BLUE);
        actuator = new SlideSubsystem(hardwareMap);
        gripper = new GripperSubsystem(hardwareMap);
        wrist = new WristSubsystem(hardwareMap);
        scheduler.registerSubsystem(drive, camera, actuator, gripper, wrist);

        drive.getOdometry().setPoseEstimate(startKnot.getPose());

        Trajectory lrSpike = drive.buildTrajectory(startKnot)
                .splineToSplineHeading(spikeIntermediate)
                .splineToConstantHeading(lrSpikeDeposit)
                .build();

        Trajectory cSpike = drive.buildTrajectory(startKnot)
                .splineToSplineHeading(spikeIntermediate)
                .splineToConstantHeading(centerSpikeDeposit)
                .build();

        Trajectory toLeftVision = drive.buildTrajectory(new Knot(lrSpikeDeposit.x, lrSpikeDeposit.y, 180, 0))
                .splineToConstantHeading(new Knot(depositX, depositLY, 180, 0))
                .build();


        Trajectory toRightVision = drive.buildTrajectory(new Knot(lrSpikeDeposit.x, lrSpikeDeposit.y, 180, 0))
                .splineToConstantHeading(new Knot(depositX, depositRY, 180, 0))
                .build();


        Trajectory toCenterVision = drive.buildTrajectory(new Knot(centerSpikeDeposit.x, centerSpikeDeposit.y, 180, 0))
                .splineToConstantHeading(new Knot(depositX, depositCY, 180, 0))
                .build();

        Trajectory leftToStack = drive.buildTrajectory(new Knot(depositX, depositLY, 180, 180))
                    .splineToConstantHeading(new Knot(cycleIntermediate, 180))
                    .splineToConstantHeading(new Knot(cycleIntakePos, 180))
                    .build();

        gripper.closeClaw();

        scheduler.scheduleCommand(new SequentialCommand(
                new InstantCommand(camera::enableTeamDetection),
                new WaitUntilCommand(() -> camera.getFramesAnalyzed() > 10),
                new InstantCommand(() -> {
                    TeamElementDetectionPipeline.Detection detection = camera.getTeamElementLocation();
                    switch (detection) {
                        case LEFT:
                            scheduler.scheduleCommand(new SequentialCommand(
                                    new ParallelCommand(
                                            drive.followTrajectory(lrSpike),
                                            new SequentialCommand(
                                                    new WaitCommand(spikeExtensionWait),
                                                    new InstantCommand(() -> actuator.setSlideTarget(leftSpikeExtension)),
                                                    new InstantCommand(() -> wrist.spikeDropPosition()),
                                                    new WaitCommand(0.5)
                                            ),
                                            new WaitUntilCommand(() -> Math.abs(actuator.slidePosition - leftSpikeExtension) < 0.3)
                                    ),
                                    new InstantCommand(gripper::openLeftClaw),
                                    new WaitCommand(0.5),
                                    new ParallelCommand(
                                            drive.followTrajectory(toLeftVision),
                                            new SequentialCommand(
                                                    new InstantCommand(() -> actuator.setSlideTarget(0)),
                                                    new WaitCommand(0.5),
                                                    new InstantCommand(() -> wrist.outtakePosition()),
                                                    new WaitUntilCommand(() -> Math.abs(actuator.slidePosition) < 0.3),
                                                    new InstantCommand(() -> actuator.setArmTarget(120)),
                                                    new WaitCommand(0.1),
                                                    new InstantCommand(wrist::visionOuttakePosition),
                                                    new WaitUntilCommand(() -> Math.abs(actuator.armPosition - 2) < 0.03)
                                            )
                                    ),
                                    new InstantCommand(gripper::openRightClaw),
                                    new WaitCommand(2),
                                    new ParallelCommand(
                                            drive.followTrajectory(leftToStack),
                                            new SequentialCommand(
                                                    new InstantCommand(() -> actuator.setArmTarget(0)),
                                                    new WaitUntilCommand(() -> Math.abs(actuator.armPosition) < 0.03),
                                                    new InstantCommand(() -> actuator.setSlideTarget(cycleOneExtension)),
                                                    new InstantCommand(wrist::stackTopIntakePos)
                                            )
                                    )
                            ));
                            break;
                        case CENTER:
                            scheduler.scheduleCommand(new SequentialCommand(
                                    new ParallelCommand(
                                            drive.followTrajectory(cSpike),
                                            new SequentialCommand(
                                                    new WaitCommand(spikeExtensionWait),
                                                    new InstantCommand(() -> actuator.setSlideTarget(centerSpikeExtension)),
                                                    new InstantCommand(() -> wrist.spikeDropPosition()),
                                                    new WaitCommand(0.5)
                                            ),
                                            new WaitUntilCommand(() -> Math.abs(actuator.slidePosition - centerSpikeExtension) < 0.3)
                                    ),
                                    new InstantCommand(gripper::openLeftClaw),
                                    new WaitCommand(0.5),
                                    new ParallelCommand(
                                            drive.followTrajectory(toCenterVision),
                                            new SequentialCommand(
                                                    new InstantCommand(() -> actuator.setSlideTarget(0)),
                                                    new WaitCommand(0.5),
                                                    new InstantCommand(() -> wrist.outtakePosition()),
                                                    new WaitUntilCommand(() -> Math.abs(actuator.slidePosition) < 0.3),
                                                    new InstantCommand(() -> actuator.setArmTarget(120)),
                                                    new WaitCommand(0.1),
                                                    new InstantCommand(wrist::visionOuttakePosition),
                                                    new WaitUntilCommand(() -> Math.abs(actuator.armPosition - 2) < 0.03)
                                            )
                                    ),
                                    new InstantCommand(gripper::openRightClaw)
                            ));
                            break;
                        case RIGHT:
                            scheduler.scheduleCommand(new SequentialCommand(
                                    new ParallelCommand(
                                            drive.followTrajectory(lrSpike),
                                            new SequentialCommand(
                                                    new WaitCommand(spikeExtensionWait),
                                                    new InstantCommand(() -> actuator.setSlideTarget(rightSpikeExtension)),
                                                    new InstantCommand(() -> wrist.spikeDropPosition()),
                                                    new WaitCommand(0.5)
                                            ),
                                            new WaitUntilCommand(() -> Math.abs(actuator.slidePosition - rightSpikeExtension) < 0.3)
                                    ),
                                    new InstantCommand(gripper::openLeftClaw),
                                    new WaitCommand(0.5),
                                    new ParallelCommand(
                                            drive.followTrajectory(toRightVision),
                                            new SequentialCommand(
                                                    new InstantCommand(() -> actuator.setSlideTarget(0)),
                                                    new WaitCommand(0.5),
                                                    new InstantCommand(() -> wrist.outtakePosition()),
                                                    new WaitUntilCommand(() -> Math.abs(actuator.slidePosition) < 0.3),
                                                    new InstantCommand(() -> actuator.setArmTarget(120)),
                                                    new WaitCommand(0.1),
                                                    new InstantCommand(wrist::visionOuttakePosition),
                                                    new WaitUntilCommand(() -> Math.abs(actuator.armPosition - 2) < 0.03)
                                            )
                                    ),
                                    new InstantCommand(gripper::openRightClaw)
                            ));
                            break;
                    }
                })
        ));
    }


    @Override
    public void run() {
        telemetry.addData("frames", camera.getFramesAnalyzed());
        telemetry.addData("res", camera.toString());
    }
}
