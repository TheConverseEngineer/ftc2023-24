package org.firstinspires.ftc.teamcode.common.vision;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.common.trajectory.OdometrySubsystem;
import org.firstinspires.ftc.teamcode.common.utils.MathUtils;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase;
import org.firstinspires.ftc.vision.apriltag.AprilTagLibrary;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessorImpl;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Config
public class AprilTagOdometryProcessor extends AprilTagProcessorImpl {

    private final OdometrySubsystem odometry;

    public static double covarianceGain = 0.2;
    public static double offset = 11;

    public AprilTagOdometryProcessor(OdometrySubsystem odometry, double fx, double fy, double cx, double cy, DistanceUnit outputUnitsLength, AngleUnit outputUnitsAngle, AprilTagLibrary tagLibrary, boolean drawAxes, boolean drawCube, boolean drawOutline, boolean drawTagID, TagFamily tagFamily, int threads) {
        super(fx, fy, cx, cy, outputUnitsLength, outputUnitsAngle, tagLibrary, drawAxes, drawCube, drawOutline, drawTagID, tagFamily, threads);

        this.odometry = odometry;
    }

    private final AtomicInteger detectedItems = new AtomicInteger(0);
    private double cumulativeHeadingError = 0;
    private final Object cumulativeHeadingMutex = new Object();

    @SuppressWarnings("unchecked")
    @Override
    public Object processFrame(Mat input, long captureTimeNanos) {
        Pose2d currentPoseEstimate = odometry.getPoseEstimate(); // Do this before finding tags

        ArrayList<AprilTagDetection> detections =
                (ArrayList<AprilTagDetection>) super.processFrame(input, captureTimeNanos);


        for (AprilTagDetection i : detections) {
            if (i.metadata == null) continue; // Unknown tag
            detectedItems.getAndIncrement();

            Vector2d tagPosition = MathUtils.toVector(i.metadata.fieldPosition);
            Vector2d robotOffset = new Vector2d(i.ftcPose.y, i.ftcPose.x).rotated(i.ftcPose.yaw);

            Vector2d visionPoseEstimate = tagPosition.plus(new Vector2d(robotOffset.getX(), -robotOffset.getY()));
            visionPoseEstimate = visionPoseEstimate.plus(currentPoseEstimate.headingVec().times(offset));

            synchronized (cumulativeHeadingMutex) {
                cumulativeHeadingError += Math.abs(currentPoseEstimate.getHeading() - i.ftcPose.yaw);
            }

            // TODO: only correct distance from back wall.
            Pose2d deltaPose = new Pose2d(visionPoseEstimate.minus(currentPoseEstimate.vec()), 0).times(covarianceGain);

            odometry.addDeltaPose(deltaPose);
            currentPoseEstimate = currentPoseEstimate.plus(deltaPose);
        }

        return detections;
    }

    public int getDetectedItems() {
        return detectedItems.get();
    }

    public double getAverageHeadingError() {
        double average;
        synchronized (cumulativeHeadingMutex) {
            average = cumulativeHeadingError;
        }
        return average / detectedItems.get();
    }

    /** Creates a new instance using all of the default settings*/
    public static AprilTagOdometryProcessor generate(OdometrySubsystem odometry, double fx, double fy, double cx, double cy) {
        AprilTagOdometryProcessor instance = new AprilTagOdometryProcessor(
                odometry, fx, fy, cx, cy, DistanceUnit.INCH, AngleUnit.RADIANS,
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
