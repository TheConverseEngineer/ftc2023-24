package org.firstinspires.ftc.teamcode.common.trajectory;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;

/** Represents a point in a spline-based trajectory */
public class Knot {
    public double x;
    public double y;
    public double heading;
    public double splineHeading;

    /** Note that heading and splineHeading should be in degrees */
    public Knot(double x, double y, double heading, double splineHeading) {
        this.x = x;
        this.y = y;
        this.heading = Math.toRadians(heading);
        this.splineHeading = Math.toRadians(splineHeading);
    }

    public Knot(Pose2d pose, double splineHeading) {
        this(pose.getX(), pose.getY(), Math.toDegrees(pose.getHeading()), splineHeading);
    }

    public Knot() {
        this(0.0, 0.0, 0.0, 0.0);
    }

    public Vector2d getPoint() {
        return new Vector2d(x, y);
    }

    public Pose2d getPose() {
        return new Pose2d(x, y, heading);
    }
}
