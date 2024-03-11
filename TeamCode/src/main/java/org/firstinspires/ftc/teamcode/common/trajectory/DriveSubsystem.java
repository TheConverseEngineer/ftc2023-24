package org.firstinspires.ftc.teamcode.common.trajectory;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.control.PIDCoefficients;
import com.acmerobotics.roadrunner.drive.DriveSignal;
import com.acmerobotics.roadrunner.drive.MecanumDrive;
import com.acmerobotics.roadrunner.followers.HolonomicPIDVAFollower;
import com.acmerobotics.roadrunner.followers.TrajectoryFollower;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.constraints.AngularVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.MecanumVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.MinVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.ProfileAccelerationConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.TrajectoryAccelerationConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.TrajectoryVelocityConstraint;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.teamcode.common.command.Command;
import org.firstinspires.ftc.teamcode.common.command.Subsystem;
import org.firstinspires.ftc.teamcode.common.command.gamepad.GamepadEx;
import org.firstinspires.ftc.teamcode.common.utils.DashboardManager;

import java.util.Arrays;
import java.util.List;

@Config
public class DriveSubsystem extends MecanumDrive implements Subsystem {

    // .00104963, .00106459, .00105193   vs    0.00114746364 (theoretical--wrong dia)
    public static double[] ODO_IN_PER_TICK = {-0.00105691, 0.00105691, 0.00105691};
    public static double ODO_TRACK_WIDTH = 10.1636;
    public static double ODO_FRONT_OFFSET = 9;

    // Please note that these values are for the drivetrain, not for odo and should probably not be edited
    public static final double DRIVETRAIN_TRACK_WIDTH = 5.244095*2;
    public static final double DRIVETRAIN_WHEEL_BASE = 6.435*2;
    public static final double LATERAL_MULTIPLIER = 1.11;

    public static final double MAX_VEL = 30, MAX_ACCEL = 30, MAX_ANG_VEL = Math.PI, MAX_ANG_ACCEL = Math.PI;

    private static final TrajectoryVelocityConstraint VEL_CONSTRAINT = getVelocityConstraint(MAX_VEL, MAX_ANG_VEL, DRIVETRAIN_TRACK_WIDTH);
    private static final TrajectoryAccelerationConstraint ACCEL_CONSTRAINT = getAccelerationConstraint(MAX_ACCEL);

    public static PIDCoefficients TRANSLATIONAL_PID = new PIDCoefficients(6, 0, 0);
    public static PIDCoefficients ROTATIONAL_PID = new PIDCoefficients(5, 0, 0);

    public static double kV = .015, kA = .002, kStatic = .07;

    private final TrajectoryFollower follower;

    public final DcMotorEx leftFront, leftRear, rightFront, rightRear;

    private final VoltageSensor voltageSensor;

    private boolean setFirstTrajectory = false;

    private final FusedOdoSubsystem odometry;

    public DriveSubsystem(HardwareMap hardwareMap) {
        this(hardwareMap, new Pose2d());
    }

    public DriveSubsystem(HardwareMap hardwareMap, Pose2d initialPose) {
        super(kV, kA, kStatic, DRIVETRAIN_TRACK_WIDTH, DRIVETRAIN_WHEEL_BASE, LATERAL_MULTIPLIER);

        // Note that under these constraints, the follower will basically never naturally exit the control mode
        // Instead, we code the exit condition into the command; this way, the robot will continue to make micro-adjustments,
        // even after the trajectory has "concluded"
        follower = new HolonomicPIDVAFollower(TRANSLATIONAL_PID, TRANSLATIONAL_PID, ROTATIONAL_PID,
                new Pose2d(0, 0, Math.toRadians(0)), 20);

        // Create the motors
        leftFront = hardwareMap.get(DcMotorEx.class, "leftFrontDrive");
        leftRear = hardwareMap.get(DcMotorEx.class, "leftRearDrive");
        rightRear = hardwareMap.get(DcMotorEx.class, "rightRearDrive");
        rightFront = hardwareMap.get(DcMotorEx.class, "rightFrontDrive");

        // Reset encoders and reverse directions as needed.
        configureMotors();

        // Get the voltage sensor
        voltageSensor = hardwareMap.voltageSensor.iterator().next();

        // Set up odo
        IMU internal = hardwareMap.get(IMU.class, "emu");

        IMU.Parameters parameters = new IMU.Parameters(
                new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.RIGHT,
                        RevHubOrientationOnRobot.UsbFacingDirection.UP
                )
        );

        internal.initialize(parameters);
        odometry = new FusedOdoSubsystem(internal, leftFront, leftRear, rightFront, initialPose);
    }

    @Override
    public void earlyPeriodic() {
        odometry.update();
    }

    public Pose2d getPosition() {
        return odometry.getPoseEstimate();
    }

    public void setPosition(Pose2d pose) {
        odometry.setPoseEstimate(pose);
    }

    @Override
    public void setMotorPowers(double v, double v1, double v2, double v3) {
        leftFront.setPower(v);
        leftRear.setPower(v1);
        rightRear.setPower(v2);
        rightFront.setPower(v3);
    }

    private void configureMotors() {
        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftRear.setDirection(DcMotorSimple.Direction.REVERSE);


        leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftRear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftRear.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

    }

    @Override
    protected double getRawExternalHeading() {
        throw new UnsupportedOperationException("Attempted to use the built-in mecanum localizer, which is unsupported.");
    }

    @NonNull
    @Override
    public List<Double> getWheelPositions() {
        throw new UnsupportedOperationException("Attempted to use the built-in mecanum localizer, which is unsupported.");
    }

    public Command followTrajectory(Trajectory trajectory) {
        return new Command() {
            @Override
            public void init() {
                setTrajectory(trajectory);

            }

            // Loop handled by subsystem periodic
            @Override public void loop() { }

            @Override public void end() { }

            @Override
            public boolean isComplete() {
                return follower.elapsedTime() >= trajectory.duration();
            }
        };
    }

    public TrajectoryBuilderEx buildTrajectory(Knot startPose) {
        return new TrajectoryBuilderEx(startPose, VEL_CONSTRAINT, ACCEL_CONSTRAINT);
    }

    private void setTrajectory(Trajectory trajectory) {
        setFirstTrajectory = true;
        follower.followTrajectory(trajectory);
    }

    public FusedOdoSubsystem getOdometry() {
        return odometry;
    }

    @Override
    public void periodic() {
        odometry.update();

        if (!setFirstTrajectory) return; // Wait until the first trajectory has been scheduled.

        // This calculates both feedforward and feedback
        DriveSignal driveSignal = follower.update(odometry.getPoseEstimate());

        // Compensate for voltage
        double voltageRatio = 12.0/voltageSensor.getVoltage();
        driveSignal = new DriveSignal(
                driveSignal.getVel().times(voltageRatio),
                driveSignal.getAccel().times(voltageRatio)
        );

        // Now apply the drive signal
        setDriveSignal(driveSignal);

        // Draw robot desired pose
        Pose2d realPose = odometry.getPoseEstimate();
        Pose2d desired = realPose.plus(follower.getLastError());
        DashboardManager.getInstance().drawRobot(realPose);
        DashboardManager.getInstance().drawRobot(desired);
        DashboardManager.getInstance().put("y", follower.getLastError().getY());
        DashboardManager.getInstance().put("x", follower.getLastError().getX());
    }

    /** Call this method every loop iteration in order to drive during teleop
     * <br>
     * This method will drive the robot in a field-centric manner.
     * */
    public void driveWithGamepad(Gamepad gamepad) {
        setFirstTrajectory = false;

        double x = gamepad.left_stick_x;
        double y = -gamepad.left_stick_y;
        double rw = gamepad.right_stick_x;

        double speedTrigger = gamepad.right_trigger;
        double brakeTrigger = gamepad.left_trigger;

        double theta = Math.atan2(y, x*1.1) - Math.PI/4  - odometry.getPoseEstimate().getHeading();
        double rho = (x*x + y*y)*(0.6 + 0.4*speedTrigger)/(Math.max(Math.abs(Math.cos(theta)), Math.abs(Math.sin(theta))))*(1-brakeTrigger*0.5);
        double w = rw*(0.4+0.6*speedTrigger)*(1-brakeTrigger*0.5);
        if (rho+Math.abs(rw) > 1) {
            w = rw/(rho + Math.abs(rw));
            rho /= (rho + Math.abs(rw));
        }
        leftFront.setPower(rho*Math.cos(theta)+w);
        rightFront.setPower(rho*Math.sin(theta)-w);
        rightRear.setPower(rho*Math.cos(theta)-w);
        leftRear.setPower(rho*Math.sin(theta)+w);
    }

    public static TrajectoryVelocityConstraint getVelocityConstraint(double maxVel, double maxAngularVel, double trackWidth) {
        return new MinVelocityConstraint(Arrays.asList(
                new AngularVelocityConstraint(maxAngularVel),
                new MecanumVelocityConstraint(maxVel, trackWidth)
        ));
    }

    public static TrajectoryAccelerationConstraint getAccelerationConstraint(double maxAccel) {
        return new ProfileAccelerationConstraint(maxAccel);
    }
}
