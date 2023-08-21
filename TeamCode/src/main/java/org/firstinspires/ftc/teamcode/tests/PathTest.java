package org.firstinspires.ftc.teamcode.tests;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.blacksmithcore.CompoundPath;
import org.firstinspires.ftc.teamcode.blacksmithcore.DifferentiatedVector2d;
import org.firstinspires.ftc.teamcode.blacksmithcore.MecanumConfiguration;
import org.firstinspires.ftc.teamcode.blacksmithcore.Path;
import org.firstinspires.ftc.teamcode.blacksmithcore.PathSegment;
import org.firstinspires.ftc.teamcode.blacksmithcore.Quintic;
import org.firstinspires.ftc.teamcode.blacksmithcore.TimeParameter;
import org.firstinspires.ftc.teamcode.blacksmithcore.Trajectory;
import org.firstinspires.ftc.teamcode.blacksmithcore.TrajectoryState;
import org.firstinspires.ftc.teamcode.thundercoreV2.utils.Logger;
import org.firstinspires.ftc.teamcode.thundercoreV2.utils.Utils;


// It works!
@TeleOp
@Config
public class PathTest extends LinearOpMode {

    private final Logger logger = Logger.getInstance();

    public static double speed;
    public static double aMax;
    public static double Kt, Kc, Kl;

    @Override
    public void runOpMode() {
        Quintic xSpline1 = Quintic.Companion.generateFromControlPoints(0d, 50d, 75d, 75d, 0d, 0d);
        Quintic ySpline1 = Quintic.Companion.generateFromControlPoints(0d, 50d, 0d, 0d, 0d, 0d);
        Quintic xSpline2 = Quintic.Companion.generateFromControlPoints(50d, -20d, 75d, -75d, 0d, 0d);
        Quintic ySpline2 = Quintic.Companion.generateFromControlPoints(50d, -20d, 0d, 0d, 0d, 0d);
        Quintic xSpline3 = Quintic.Companion.generateFromControlPoints(-20d, -5, -75d, 0d, 0d, 0d);
        Quintic ySpline3 = Quintic.Companion.generateFromControlPoints(-20d, 50d, 0d, 75d, 0d, 0d);

        Path path = new CompoundPath(new Path[]{
                new PathSegment(xSpline1, ySpline1, 200d, 0d, Math.PI/2, 1000),
                new PathSegment(xSpline2, ySpline2, 200d, Math.PI/2, -Math.PI/2, 1000),
                new PathSegment(xSpline3, ySpline3, 200d, -Math.PI/2, 0d, 1000),

        });

        MecanumConfiguration configuration = new MecanumConfiguration(speed, 10d, aMax, -aMax, Kt, Kc, Kl);

        Trajectory trajectory = new Trajectory(path, configuration, 100, 0.1, 0d, 0d);

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
