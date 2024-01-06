package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;

@Config
public class DrivetrainConstants {

    // Drivetrain sizes
    public static final double maxWheelRPM = 325;
    public static final double wheelDiameter = 3.7795;  // In inches
    public static final double trackWidth = 10;         // Distance from one side of the drivetrain to the other (THIS IS NOT FOR ODO!)
    public static final double trackHeight = 17;        // Distance between the center of the front wheel and the back wheel
    public static final double drivetrainMu = (trackHeight + trackHeight)/2;

    // Movement preferences
    public static final double maxSafeAutoSpeed = 0.9;  // The percentage of maximum speed that the robot should travel at (out of 1)
    public static final double maxAcceleration = 40;    // Positive number, the maximum acceleration of the robot in in/s^2
    public static final double minAcceleration = -20;   // Negative number, the minimum acceleration of the robot in in/s^2

    // Tuning values! (not marked final so that they can be FTC-dash compatible)
    public static double Kt = 1;          // Standard feed-forward velocity gain (1/(in/s))
    public static final double Kc = 0;          // Centripetal acceleration feed-forward gain
    public static final double Kl = 0.9;        // Percentage of maximum speed that the robot can strafe at (0.9 is probably good)
    public static double Kstatic = 0.05;  // Standard static feed-forward gain

    public static double Kp = 1;
    public static double headingKp = 1;
    public static double maxCorrectionSpeed = 10; // The maximum speed at which the drivetrain should make corrections (in in/sec).
    public static double maxCorrectionHeadingSpeed = Math.PI/2;   // The maximum speed at which the drivetrain should make heading corrections (in rad/sec).

    // Odometry Settings - NOT YET TUNED
    public static double distancePerTick = 0.001056876526771654; // Distance traveled in inches per encoder tick
    public static double odoTrackWidth = 0.01;
    public static double odoForwardOffset = 10;

    // Ignore this, it is just for instantiation suppression
    private DrivetrainConstants() { }
}
