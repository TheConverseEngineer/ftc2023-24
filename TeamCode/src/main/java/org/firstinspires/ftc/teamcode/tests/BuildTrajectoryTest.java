package org.firstinspires.ftc.teamcode.tests;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.common.command.CommandOpMode;
import org.firstinspires.ftc.teamcode.common.command.prefabs.SequentialCommand;
import org.firstinspires.ftc.teamcode.common.command.prefabs.WaitCommand;
import org.firstinspires.ftc.teamcode.common.trajectory.DriveSubsystem;
import org.firstinspires.ftc.teamcode.common.trajectory.Knot;
import org.firstinspires.ftc.teamcode.common.utils.DashboardManager;

@TeleOp
@Disabled
public class BuildTrajectoryTest extends CommandOpMode {

    private Trajectory trajectory1, trajectory2;
    private DriveSubsystem driveSubsystem;

    public static Knot[] scorePreload = {
            new Knot(12, -68, -90, 90),
            new Knot(48, -40, -179.999999, 0),
            new Knot(65, -40, -180, 0)
    };

    public static Knot[] moveToStack = {
            new Knot(65, -40, -180, 0),
            new Knot(30, -12, -180, -180),
            new Knot(-50, -12, -180, -180)
    };

    double startTime;

    @Override
    public void initialize() {
        enableSimMode(scorePreload[0].getPose());
        enableDashboard();

        driveSubsystem = new DriveSubsystem(hardwareMap);

        trajectory1 = driveSubsystem.buildTrajectory(scorePreload[0])
                .splineToSplineHeading(scorePreload[1])
                .splineToConstantHeading(scorePreload[2])
                .build();

        trajectory2 = driveSubsystem.buildTrajectory(moveToStack[0])
                    .splineToConstantHeading(moveToStack[1])
                    .splineToConstantHeading(moveToStack[2])
                    .build();

        scheduler.registerSubsystem(driveSubsystem);
        scheduler.scheduleCommand(new SequentialCommand(
                driveSubsystem.followTrajectory(trajectory1),
                new WaitCommand(1),
                driveSubsystem.followTrajectory(trajectory2)
        ));
    }

    @Override
    public void begin() {
        startTime = System.nanoTime()/1e9;
        driveSubsystem.setPosition(scorePreload[0].getPose());
    }

    @Override
    public void run() {
        DashboardManager.getInstance().drawTrajectory(trajectory1);
        DashboardManager.getInstance().drawTrajectory(trajectory2);
        DashboardManager.getInstance().drawRobot(driveSubsystem.getPosition());
    }
}
