package org.firstinspires.ftc.teamcode.auto;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.FLOAT;

import android.util.Size;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

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
import org.firstinspires.ftc.teamcode.common.vision.AprilTagOdometryProcessor;
import org.firstinspires.ftc.teamcode.subsystems.GripperSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.SlideSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.WristSubsystem;
import org.firstinspires.ftc.teamcode.vision.TeamElementDetectionPipeline;
import org.firstinspires.ftc.vision.VisionPortal;

@TeleOp
public class RedVisionAuto extends CommandOpMode {
    DriveSubsystem drive;
    SlideSubsystem actuator;
    WristSubsystem wrist;
    GripperSubsystem gripper;

    public static final Knot startKnot = new Knot(13.173, -64.541, -90, 90),
            spikeIntermediate = new Knot(34.5, -47, 180, 0),
            lrSpikeDeposit = new Knot(38.5, -37.5, 180, -90),
            centerSpikeDeposit = new Knot(42, -25.67, 180, -90);

    public static final double centerSpikeExtension = 9.5,
            leftSpikeExtension = 0,
            rightSpikeExtension = 23;

    public static final double depositX = 53.9,
            depositLY = -32,
            depositCY = -36.5,
            depositRY = -41;

    public static double spikeExtensionWait = 1;
    public static double depositExtension = 4.25;

    AprilTagOdometryProcessor localizer;
    TeamElementDetectionPipeline elementDetection;
    VisionPortal portal;

    Command left, right, center;

    @Override
    public void initialize() {
        drive = new DriveSubsystem(hardwareMap, startKnot.getPose());
        actuator = new SlideSubsystem(hardwareMap);
        gripper = new GripperSubsystem(hardwareMap);
        wrist = new WristSubsystem(hardwareMap);
        scheduler.registerSubsystem(drive, actuator, gripper, wrist);

        drive.getOdometry().setPoseEstimate(startKnot.getPose());
        localizer = AprilTagOdometryProcessor.generate(
                drive.getOdometry()::getPoseEstimate,
                drive.getOdometry()::applyOffset,
                0,0,0,0
        );

        elementDetection = new TeamElementDetectionPipeline(TeamElementDetectionPipeline.Alliance.RED);
        gripper.closeClaw();

        portal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(CameraName.class, "webcam 1"))
                .setCameraResolution(new Size(640, 360))
                .addProcessor(localizer)
                .addProcessor(elementDetection)
                .build();


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


        left = generateAuto(lrSpike, rightSpikeExtension, toRightVision);
        right = generateAuto(lrSpike, leftSpikeExtension, toLeftVision);
        center = generateAuto(cSpike, centerSpikeExtension, toCenterVision);

    }

    @Override
    public void begin() {

        switch (elementDetection.getDetection()) {
            case LEFT:
                scheduler.scheduleCommand(left);
                break;
            case RIGHT:
                scheduler.scheduleCommand(right);
                break;
            case CENTER:
                scheduler.scheduleCommand(center);
                break;
        }
        portal.setProcessorEnabled(elementDetection, false);
    }

    @Override
    protected void initLoop() {
        telemetry.addData("v", elementDetection.getDetection());
        telemetry.update();
    }

    @Override
    public void run() {
        telemetry.addData("v", elementDetection.getDetection());
        telemetry.addData("p", drive.getOdometry().getPoseEstimate());
        telemetry.update();
    }

    private Command generateAuto(Trajectory spike, double spikeExtension, Trajectory vision) {
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
                        drive.followTrajectory(vision),
                        new SequentialCommand(
                                new InstantCommand(() -> actuator.setSlideTarget(depositExtension)),
                                new WaitCommand(0.5),
                                new InstantCommand(() -> wrist.outtakePosition()),
                                new WaitUntilCommand(() -> Math.abs(actuator.slidePosition - depositExtension) < 0.3),
                                new InstantCommand(() -> actuator.setArmTarget(120)),
                                new WaitCommand(0.1),
                                new InstantCommand(wrist::outtakePosition),
                                new WaitUntilCommand(() -> Math.abs(actuator.armPosition - 2) < 0.03)
                        )
                ),
                new InstantCommand(gripper::openClaw)
        );
    }
}
