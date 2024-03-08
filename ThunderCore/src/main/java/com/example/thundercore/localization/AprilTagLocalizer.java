package com.example.thundercore.localization;

import com.example.thundercore.math.geometry.SimpleVector2d;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase;
import org.firstinspires.ftc.vision.apriltag.AprilTagLibrary;
import org.firstinspires.ftc.vision.apriltag.AprilTagPoseFtc;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessorImpl;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class AprilTagLocalizer extends AprilTagProcessorImpl {
    public AprilTagLocalizer(double fx, double fy, double cx, double cy, DistanceUnit outputUnitsLength, AngleUnit outputUnitsAngle, AprilTagLibrary tagLibrary, boolean drawAxes, boolean drawCube, boolean drawOutline, boolean drawTagID, TagFamily tagFamily, int threads) {
        super(fx, fy, cx, cy, outputUnitsLength, outputUnitsAngle, tagLibrary, drawAxes, drawCube, drawOutline, drawTagID, tagFamily, threads);
    }

    AtomicReference<SimpleVector2d> pose = new AtomicReference<>(new SimpleVector2d(0,0));

    public static double CAMERA_ANGLE = 0;

    @Override
    @SuppressWarnings("unchecked")
    public Object processFrame(Mat input, long captureTimeNanos) {
        ArrayList<AprilTagDetection> detections =
                (ArrayList<AprilTagDetection>) super.processFrame(input, captureTimeNanos);

        for (AprilTagDetection i : detections) {
            if (i.metadata == null) continue; // Unknown tag (how did this even enter the camera view?)
            SimpleVector2d tagPosition = SimpleVector2d.fromVectorF(i.metadata.fieldPosition);
            SimpleVector2d cameraPosition = tagPosition .plus (vectorOffset(i.ftcPose));

            pose.set(cameraPosition);
        }

        return null;
    }

    public SimpleVector2d getRecent() {
        return pose.get();
    }


    /** Converts an AprilTagPoseFtc object to a SimpleVector2d offset.
     *
     * <p>Add this value to the actual position of the april tag in order to get the camera position.
     */
    public SimpleVector2d vectorOffset(AprilTagPoseFtc pose) {
        // Yes, the x and y should be swapped, as the tags are parallel to the y axis
        double a = pose.z/Math.tan(Math.PI/2 - Math.toRadians(CAMERA_ANGLE));

        return new SimpleVector2d(-(pose.y - a)*Math.cos(Math.toRadians(CAMERA_ANGLE)), pose.x).rotateBy(pose.yaw);
    }

    /** Generates a new AprilTagLocalizer instance with the inputted camera calibration.
     *
     * <p> Setting all parameters to zero will default to use the preset configuration
     */
    public static AprilTagLocalizer generate(double fx, double fy, double cx, double cy) {
        AprilTagLocalizer instance = new AprilTagLocalizer(
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
