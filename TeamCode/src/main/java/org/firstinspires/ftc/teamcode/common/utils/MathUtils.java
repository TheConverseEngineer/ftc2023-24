package org.firstinspires.ftc.teamcode.common.utils;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;

import org.firstinspires.ftc.robotcore.external.matrices.VectorF;

import java.sql.Array;

public class MathUtils {

    private MathUtils() { }

    /** Clamps a double value between two bounds */
    public static double clamp(double x, double min, double max) {
        return Math.max(Math.min(x, max), min);
    }

    /** Takes local changes in position and integrates them along a curve to get a global change in position
     *
     * @param dX                the change in the x-coordinate in local space
     * @param dY                the change in the y-coordinate in local space
     * @param dTheta            the change in the heading of the robot in radians
     * @param currentHeading    the current heading of the robot in radians
     *
     * @return          the change in the robot's pose in global coordinates.
     * */
    public static Pose2d runPoseExponential(double dX, double dY, double dTheta, double currentHeading) {
        // First handle the case where dTheta = 0 and we need to take the limit to avoid a divide-by-zero error
        double sin;
        double cos;
        if (Math.abs(dTheta) < 1E-9) {
            sin = 1.0 - 1.0 / 6.0 * dTheta * dTheta;
            cos = 0.5 * dTheta;
        } else {
            sin = Math.sin(dTheta) / dTheta;
            cos = (1 - Math.cos(dTheta)) / dTheta;
        }

        // Now integrate
        Pose2d integratedDelta = new Pose2d(
                sin*dX - cos*dY,
                cos*dX + sin*dY,
                dTheta
        );

        double sinG = Math.sin(currentHeading);
        double cosG = Math.cos(currentHeading);

        // And now rotate it into global space
        return new Pose2d(
                integratedDelta.getX()*cosG - integratedDelta.getY()*sinG,
                integratedDelta.getX()*sinG + integratedDelta.getY()*cosG,
                dTheta
        );
    }

    /** Converts a VectorF into a Vector2d */
    public static Vector2d toVector(VectorF vector) {
        return new Vector2d(vector.get(0), vector.get(1));
    }
}
