package org.firstinspires.ftc.teamcode.common.trajectory;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.common.utils.MathUtils;

import java.util.concurrent.atomic.AtomicReference;

/** A thread-safe class that handles position tracking during autonomous.
 * <br>
 * Note that despite the name, this class does not implement {@link org.firstinspires.ftc.teamcode.common.command.Subsystem}
 * and is used internally by {@link DriveSubsystem}*/
public class OdometrySubsystem {

    // Using an atomic reference here so that the vision and hardware threads can edit this simultaneously.
    private final AtomicReference<Pose2d> robotPose = new AtomicReference<>();

    // left, right, front
    private final double[] encoderValues = new double[]{0.0, 0.0, 0.0};
    private final DcMotor[] pods;

    /** Creates a three wheel odometry system */
    public OdometrySubsystem(DcMotor left, DcMotor right, DcMotor front) {
        this.pods = new DcMotor[]{left, right, front};
        robotPose.set(new Pose2d());
    }

    public String getEncoderRaw() {
        return String.format("%6.1f %6.1f %6.1f", encoderValues[0], encoderValues[1], encoderValues[2]);
    }

    /** Updates the pose estimate from the tracking wheels
     * <br>
     * This method should ideally be called as often as possible (once per loop iteration)
     * */
    public void updateTrackingWheels() {
        double[] encoderDeltaInches = new double[3]; // left, right, front

        for (int i = 0; i < 3; i++) {
            encoderDeltaInches[i] = pods[i].getCurrentPosition()-encoderValues[i];
            encoderValues[i] += encoderDeltaInches[i];
            encoderDeltaInches[i] *= DriveSubsystem.ODO_IN_PER_TICK[i];
        }

        double dTheta = (encoderDeltaInches[1] - encoderDeltaInches[0])/ DriveSubsystem.ODO_TRACK_WIDTH;

        integrateDeltaPose(
               (encoderDeltaInches[0] + encoderDeltaInches[1])/2,
               encoderDeltaInches[2] - dTheta*DriveSubsystem.ODO_FRONT_OFFSET,
               dTheta
        );
    }

    /** Returns the last-recorded pose estimate for this robot. */
    public Pose2d getPoseEstimate() {
        return robotPose.get();
    }

    /** Sets the current pose estimate that should be used by the odometry
     * <br>
     * Note that this operation involves flushing the CPU cache store of the atomic reference to
     * the current pose. As such, it is <u>not</u> recommended to call this method frequently.
     * The intended use case is to call this method <u>once</u> at the start of an OpMode.
     * */
    public void setPoseEstimate(Pose2d newPose) {
        robotPose.set(newPose);
    }

    /** Adds a pose delta to the currently stored pose.
     * <br>
     * Note that a pose exponential is <u>not</u> used here, and that the delta pose
     * is directly added to the current pose.
     *
     * @param delta     the pose to add
     * */
    public void addDeltaPose(Pose2d delta) {
        Pose2d currentPose, newPose;
        do {
            currentPose = robotPose.get();
            newPose = currentPose.plus(delta);
        } while(!robotPose.compareAndSet(currentPose, newPose));
    }

    /** Integrates a local pose delta before adding it the currently stored pose.
     * <br>
     * Note that a pose exponential is used here, and that the delta pose should be in
     * local coordinates.
     *
     * @param dX                the change in the x-coordinate in local space
     * @param dY                the change in the y-coordinate in local space
     * @param dTheta            the change in the heading of the robot in radians
     * */
    private void integrateDeltaPose(double dX, double dY, double dTheta) {
        Pose2d currentPose, newPose;
        do {
            currentPose = robotPose.get();
            newPose = currentPose.plus(MathUtils.runPoseExponential(
                    dX, dY, dTheta, currentPose.getHeading()
            ));
        } while(!robotPose.compareAndSet(currentPose, newPose));
    }

}
