package org.firstinspires.ftc.teamcode.tests;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.common.command.Command;
import org.firstinspires.ftc.teamcode.common.command.CommandOpMode;
import org.firstinspires.ftc.teamcode.common.command.prefabs.InstantCommand;
import org.firstinspires.ftc.teamcode.common.command.prefabs.SequentialCommand;
import org.firstinspires.ftc.teamcode.common.trajectory.DriveSubsystem;
import org.firstinspires.ftc.teamcode.common.trajectory.Knot;

@TeleOp
public class FeedbackTunerOpMode extends CommandOpMode {

    DriveSubsystem driveSubsystem;

    Trajectory forward, back;

    @Override
    public void initialize() {
        enableDashboard();
        driveSubsystem = new DriveSubsystem(hardwareMap);

        scheduler.registerSubsystem(driveSubsystem);

        forward = driveSubsystem
                    .buildTrajectory(new Knot(0, 0, 0, 0))
                    .splineToLinearHeading(new Knot(FeedforwardTunerOpMode.DISTANCE, 0, 0, 0))
                    .build();

        back = driveSubsystem
                .buildTrajectory(new Knot(FeedforwardTunerOpMode.DISTANCE, 0, 0, -180))
                .splineToLinearHeading(new Knot(0, 0, 0, -180))
                .build();


        scheduleBackAndForth();


    }

    private void scheduleBackAndForth() {
        scheduler.scheduleCommand(new SequentialCommand(
            driveSubsystem.followTrajectory(forward),
            driveSubsystem.followTrajectory(back),
            new InstantCommand(this::scheduleBackAndForth)
        ));
    }
}
