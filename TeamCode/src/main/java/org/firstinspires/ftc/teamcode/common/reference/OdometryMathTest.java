package org.firstinspires.ftc.teamcode.common.reference;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;

import org.firstinspires.ftc.teamcode.common.simulation.VirtualDummyMotorEx;
import org.firstinspires.ftc.teamcode.common.trajectory.DriveSubsystem;
import org.firstinspires.ftc.teamcode.common.trajectory.OdometrySubsystem;

import java.util.ArrayList;

public class OdometryMathTest {

    private static double maxDeltaPerLoop = ((40.0/1000)*8)/.00106459;

    private static void testOdo(int numIterations) {
        VirtualDummyMotorEx left = new VirtualDummyMotorEx(),
                            right = new VirtualDummyMotorEx(),
                            front = new VirtualDummyMotorEx();

        OdometrySubsystem realOdo = new OdometrySubsystem(left, right, front);
        TrackingWheelLocalizerReference referenceOdo =
                new TrackingWheelLocalizerReference(left, right, front,
                        new ArrayList<>(), new ArrayList<>()
                );

        printDiff(realOdo.getPoseEstimate(), referenceOdo.getPoseEstimate());
        realOdo.updateTrackingWheels();
        referenceOdo.update();
        printDiff(realOdo.getPoseEstimate(), referenceOdo.getPoseEstimate());

        double averagePoseError = 0;
        double averageHeadingError = 0;

        for (int iter = 0; iter < numIterations; iter++) {
            double rf = maxDeltaPerLoop*(2*Math.random() - 1);
            applyOffset(left, maxDeltaPerLoop*(2*Math.random() - 1));
            applyOffset(right, maxDeltaPerLoop*(2*Math.random() - 1));
            applyOffset(front, maxDeltaPerLoop*(2*Math.random() - 1));

            realOdo.updateTrackingWheels();
            referenceOdo.update();

            printDiff(realOdo.getPoseEstimate(), referenceOdo.getPoseEstimate());
            averagePoseError += getPoseError(realOdo.getPoseEstimate(), referenceOdo.getPoseEstimate());
            averageHeadingError += getHeadingError(realOdo.getPoseEstimate(), referenceOdo.getPoseEstimate());
        }

        System.out.println("Average error: " + (averagePoseError/numIterations));
        System.out.println("Average heading error: " + (averageHeadingError/numIterations));
    }

    private static void printDiff(Pose2d a, Pose2d b) {
        System.out.println("Error: " + (a.minus(b)) + "(" + a + " and " + b + "), " + getHeadingError(a, b));
    }

    private static double getPoseError(Pose2d a, Pose2d b) {
        return a.vec().distTo(b.vec());
    }

    private static double getHeadingError(Pose2d a, Pose2d b) {
        double error = Math.toDegrees(a.getHeading() - b.getHeading());
        while (error > 180) error -= 360;
        while (error < -180) error += 360;

        return Math.abs(error);
    }

    private static void applyOffset(VirtualDummyMotorEx motor, double offset) {
        motor.internalSetPosition(motor.internalGetExactPosition() + offset);
    }

    /*
    public static void main(String[] args) {
        System.out.println("Testing");
        testOdo(1000);
    } */
}
