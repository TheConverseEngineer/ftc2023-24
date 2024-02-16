package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.common.command.CommandOpMode;
import org.firstinspires.ftc.teamcode.common.trajectory.DriveSubsystem;
import org.firstinspires.ftc.teamcode.common.trajectory.Knot;

import java.util.Scanner;


@Autonomous(preselectTeleOp = "StandardTeleOp")
@Config
public class BlueSideAuto extends CommandOpMode {

    DriveSubsystem drive;

    public static Knot[] initialApproachKnots = new Knot[]{
            new Knot(12, 63.28, 90, -90),
            new Knot(35, 48, 180, 0),
            new Knot(50, 36, 180, 0)
    };

    @Override
    public void initialize() {
        drive = new DriveSubsystem(hardwareMap);

        scheduler.registerSubsystem(drive);

        drive.getOdometry().setPoseEstimate(initialApproachKnots[0].getPose());

        Trajectory initialApproach = drive.buildTrajectory(initialApproachKnots[0])
                .splineToSplineHeading(initialApproachKnots[1])
                .splineToConstantHeading(initialApproachKnots[2])
                .build();

        scheduler.scheduleCommand(drive.followTrajectory(initialApproach));
    }
}
