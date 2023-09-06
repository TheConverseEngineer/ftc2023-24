package org.firstinspires.ftc.teamcode.general.tests;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.general.blacksmithcore.Path;
import org.firstinspires.ftc.teamcode.general.blacksmithcore.Trajectory;
import org.firstinspires.ftc.teamcode.general.blacksmithcore.TrajectoryBuilder;
import org.firstinspires.ftc.teamcode.general.blacksmithcore.TrajectoryState;
import org.firstinspires.ftc.teamcode.general.thundercore.utils.Logger;
import org.firstinspires.ftc.teamcode.general.thundercore.utils.Utils;


// It works!
@TeleOp
@Config
public class PathTest extends LinearOpMode {

    private final Logger logger = Logger.getInstance();

    @Override
    public void runOpMode() {
        Trajectory trajectory = TrajectoryBuilder.buildTrajectory(0d, 0d, 0d, 0d)
                        .goTo(50, 50, 180d, 0d, 40d)
                        .goTo(35, 35, 180d, 90d, 40d)
                        .goTo(20, 20, -200d, 90d, 40d)
                        .build();

        Path path = trajectory.getPath();

        logger.put("length", path.getLength());
        logger.put("duration", trajectory.getDuration());
        logger.drawPath(path, "green");
        logger.update();

        waitForStart();

        double startTime = Utils.getMsTime();
        while (!isStopRequested()) {
            double time = (Utils.getMsTime() - startTime)/1000;
            TrajectoryState target = trajectory.getGlobalRobotTargetAtTime(time);
            logger.drawRobot(target.getPosition(), target.getTargetHeading(), "blue", 8d);
            logger.drawPath(path, "green");
            logger.drawVector(target.getMovementVector(), target.getPosition(), "blue");
            logger.put("time", time);
            logger.put("angular velocity", target.getHeadingVelocity());
            logger.put("velocity", target.getMovementVector().magnitude());
            logger.update();
        }
    }
}
