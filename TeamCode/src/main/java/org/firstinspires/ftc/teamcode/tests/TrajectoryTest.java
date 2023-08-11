package org.firstinspires.ftc.teamcode.tests;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.blacksmithcore.Pose2d;
import org.firstinspires.ftc.teamcode.blacksmithcore.Trajectory;
import org.firstinspires.ftc.teamcode.blacksmithcore.Vector2dDual;
import org.firstinspires.ftc.teamcode.blacksmithcore.Waypoint;
import org.firstinspires.ftc.teamcode.thundercore.utils.Logger;
import org.firstinspires.ftc.teamcode.thundercore.utils.LoggerConfiguration;

@Config
@TeleOp
/* UPDATE: It works (except for the degenerate motion profile case)!
* Also, I still need to add support for rotation */
public class TrajectoryTest extends LinearOpMode {
    Logger logger = Logger.INSTANCE;

    public static Waypoint waypoint1 = new Waypoint(0, 0, 0, 0);
    public static Waypoint waypoint2 = new Waypoint(0, 10, 0, 0);
    public static Waypoint waypoint3 = new Waypoint(10, 0, 0, 0);
    public static Waypoint waypoint4 = new Waypoint(10, 10, 0, 0);

    public static double vmx, amx;

    @Override
    public void runOpMode() {
        logger.setTelemetry(telemetry);
        logger.setConfiguration(LoggerConfiguration.DASHBOARD_AND_TELEMETRY);

        Trajectory trajectory = Trajectory.builder(waypoint1)
                .splineTo(waypoint2)
                .splineTo(waypoint3)
                .splineTo(waypoint4).build(vmx, amx);

        logger.drawTrajectory(trajectory, 100);
        logger.put("length", trajectory.getDistance());
        logger.put("duration", trajectory.getLength());
        logger.update();

        waitForStart();

        long epoch = System.nanoTime();
        while (!isStopRequested()) {
            double time = (System.nanoTime()-epoch)/1e9;
            Vector2dDual target = trajectory.invoke(time);
            logger.drawTrajectory(trajectory, 100);
            logger.drawRobot(new Pose2d(target.getX().getX(), target.getY().getX(), 0), 8, 8);
            logger.drawForceVectors(target);
            logger.put("time", time);

            logger.update();
        }

    }
}
