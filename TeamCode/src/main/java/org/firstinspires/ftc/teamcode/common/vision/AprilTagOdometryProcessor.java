package org.firstinspires.ftc.teamcode.common.vision;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;

import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.common.trajectory.OdometrySubsystem;
import org.firstinspires.ftc.teamcode.common.utils.MathUtils;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase;
import org.firstinspires.ftc.vision.apriltag.AprilTagLibrary;
import org.firstinspires.ftc.vision.apriltag.AprilTagPoseFtc;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessorImpl;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

@Config
public class AprilTagOdometryProcessor extends AprilTagProcessorImpl {

    public AprilTagOdometryProcessor(Supplier<Pose2d> currentPose, Consumer<Vector2d> updateCallback, double fx, double fy, double cx, double cy, DistanceUnit outputUnitsLength, AngleUnit outputUnitsAngle, AprilTagLibrary tagLibrary, boolean drawAxes, boolean drawCube, boolean drawOutline, boolean drawTagID, TagFamily tagFamily, int threads) {
        super(fx, fy, cx, cy, outputUnitsLength, outputUnitsAngle, tagLibrary, drawAxes, drawCube, drawOutline, drawTagID, tagFamily, threads, false);
        this.currentPose = currentPose;
        this.updateCallback = updateCallback;
    }

    public static double CAMERA_ANGLE = 23.5;
    public static double Y_OFFSET = 4.875;
    public static double X_OFFSET = 5;

    private final Supplier<Pose2d> currentPose;
    private final Consumer<Vector2d> updateCallback;

    private static final double K = 0.8;

    @Override
    @SuppressWarnings("unchecked")
    public Object processFrame(Mat input, long captureTimeNanos) {
        Pose2d pose = currentPose.get();

        ArrayList<AprilTagDetection> detections =
                (ArrayList<AprilTagDetection>) super.processFrame(input, captureTimeNanos);

        for (AprilTagDetection i : detections) {
            if (i.metadata == null) continue; // Unknown tag (how did this even enter the camera view?)
            Vector2d tagPosition = fromVectorF(i.metadata.fieldPosition);
            Vector2d cameraPosition = tagPosition .plus (vectorOffset(i.ftcPose));

            Vector2d robotPosition = cameraPosition .plus (new Vector2d(X_OFFSET, Y_OFFSET).rotated(pose.getHeading()));

            if (robotPosition.distTo(pose.vec()) < 7)
                updateCallback.accept(robotPosition.minus(pose.vec()).times(K));
        }

        return null;
    }

    private Vector2d fromVectorF(VectorF v) {
        return new Vector2d(v.get(0), v.get(1));
    }


    /** Converts an AprilTagPoseFtc object to a SimpleVector2d offset.
     *
     * <p>Add this value to the actual position of the april tag in order to get the camera position.
     */
    public Vector2d vectorOffset(AprilTagPoseFtc pose) {
        // Yes, the x and y should be swapped, as the tags are parallel to the y axis
        double a = pose.z/Math.tan(Math.PI/2 - Math.toRadians(CAMERA_ANGLE));

        return new Vector2d(-(pose.y - a)*Math.cos(Math.toRadians(CAMERA_ANGLE)), pose.x).rotated(-pose.yaw);
    }

    /** Generates a new AprilTagLocalizer instance with the inputted camera calibration.
     *
     * <p> Setting all parameters to zero will default to use the preset configuration
     */
    public static AprilTagOdometryProcessor generate(Supplier<Pose2d> currentPose, Consumer<Vector2d> updateCallback, double fx, double fy, double cx, double cy) {
        AprilTagOdometryProcessor instance = new AprilTagOdometryProcessor(currentPose, updateCallback,
                fx, fy, cx, cy, DistanceUnit.INCH, AngleUnit.RADIANS,
                AprilTagGameDatabase.getCenterStageTagLibrary(),
                false, false, true, true,
                TagFamily.TAG_36h11, 3
        );

        // This is theoretically the best algorithm, but we should probably make sure that it
        // is fast and has good precision.
        instance.setPoseSolver(PoseSolver.OPENCV_IPPE_SQUARE);

        return instance;
    }

}
