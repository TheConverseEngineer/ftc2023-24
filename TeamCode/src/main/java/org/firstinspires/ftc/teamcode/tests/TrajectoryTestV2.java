package org.firstinspires.ftc.teamcode.tests;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.blacksmithcore.DualNum;
import org.firstinspires.ftc.teamcode.blacksmithcore.Path;
import org.firstinspires.ftc.teamcode.blacksmithcore.QuinticPolynomial;
import org.firstinspires.ftc.teamcode.blacksmithcore.QuinticSpline;
import org.firstinspires.ftc.teamcode.tests.DriveConstants;
import org.firstinspires.ftc.teamcode.thundercore.utils.Logger;

@TeleOp
@Config
public class TrajectoryTestV2 extends LinearOpMode {

    Path path;
    Logger logger = Logger.INSTANCE;

    public static double speed = 2;

    @Override
    public void runOpMode() {
        path = new Path(new QuinticSpline[]{
                new QuinticSpline(
                        QuinticPolynomial.fitPolynomial(0, 30, 30, 30, 0, 0),
                        QuinticPolynomial.fitPolynomial(0, 30, 0, 0, 0, 0), 100
                ),
                new QuinticSpline(
                        QuinticPolynomial.fitPolynomial(30, -10, 0, 0, 0, 0),
                        QuinticPolynomial.fitPolynomial(30, 30, 40, -40, 0, 0), 100
                )
        }, new Double[]{
                0d, Math.PI/2, -Math.PI/2
        });


        logger.put("distance", path.length());
        logger.update();
        waitForStart();

        long start = System.nanoTime();
        while (!isStopRequested()) {
            double currentTime = (System.nanoTime()-start) * speed * 0.000000001;
            logger.drawRobot(
                    path.invoke(new DualNum(currentTime, speed, 0.0)).toPose(),
                    DriveConstants.TRACK_WIDTH, DriveConstants.WHEEL_BASE
            );
            logger.put("travel", currentTime);
            logger.update();
        }
    }
}
