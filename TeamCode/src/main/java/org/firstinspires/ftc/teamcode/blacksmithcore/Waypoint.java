package org.firstinspires.ftc.teamcode.blacksmithcore;

/** Ftc-Dashboard compatible way to declare a waypoint position. */
public class Waypoint {
    public double x, y, heading, splineHeading;

    /** The waypoint
     *
     * @param x         x position of the robot
     * @param y         y position of the robot
     * @param heading   the heading of the robot (in degrees)
     * @param splineHeading     the direction the robot should travel (in degrees)
     */
    public Waypoint(double x, double y, double heading, double splineHeading) {
        this.x = x;
        this.y = y;
        this.heading = heading;
        this.splineHeading = splineHeading;
    }
}
