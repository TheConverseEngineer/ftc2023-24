package org.firstinspires.ftc.teamcode.common.vision;

import android.graphics.Canvas;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;

import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Quaternion;
import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.firstinspires.ftc.teamcode.common.trajectory.OdometrySubsystem;
import org.firstinspires.ftc.teamcode.common.utils.DashboardManager;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase;
import org.firstinspires.ftc.vision.apriltag.AprilTagLibrary;
import org.firstinspires.ftc.vision.apriltag.AprilTagPoseFtc;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.opencv.core.Mat;

/** Experimental vision processor that uses april tags to re-localize odometry */
@Config
@Deprecated // This does not work, check out AprilTagOdometryProcessor instead
public class AprilTagLocalizationProcessor implements VisionProcessor {

    private final AprilTagProcessor aprilTagProcessor;
    private final OdometrySubsystem odometry;

    public static double convergenceGain = 0.2;

    public final AprilTagLibrary centerStageLibrary = new AprilTagLibrary.Builder()
            .addTag(9, "small",2, new VectorF(-72, 0, 4), DistanceUnit.INCH, Quaternion.identityQuaternion())
            .addTag(10, "big",5, new VectorF(-72, 5.5f, 5.5f), DistanceUnit.INCH, Quaternion.identityQuaternion())
            .build();


    public AprilTagLocalizationProcessor(OdometrySubsystem odometry) {
        this.odometry = odometry;

        // TODO: Use custom lens intrinsics
        aprilTagProcessor = new AprilTagProcessor.Builder()
                .setOutputUnits(DistanceUnit.INCH, AngleUnit.RADIANS)
                .setTagLibrary(centerStageLibrary)
                .setDrawTagOutline(true)
                .setDrawTagID(true)
                //.setLensIntrinsics(578.272, 578.272, 402.145, 221.506)
                .build();

        aprilTagProcessor.setDecimation(2); // Allows us to have a good chance to detect both tags
    }

    @Override
    public void init(int width, int height, CameraCalibration calibration) {
        aprilTagProcessor.init(width, height, calibration);

    }

    @Override
    public Object processFrame(Mat frame, long captureTimeNanos) {
        // By fetching the pose estimate before we start the april tag algorithm ensures that our data is as close
        // as possible to the time when the image was collected.
        Pose2d currentPose = odometry.getPoseEstimate();

        // Now handle the april tag detection
        Object result = aprilTagProcessor.processFrame(frame, captureTimeNanos);

        // Now calculate the robot pose based only on the detected april tags
        int counter = 0;
        Vector2d measuredRobotPose = new Vector2d();
        for (AprilTagDetection detection : aprilTagProcessor.getDetections()) {
            //if (detection.id <= 6) continue; // These are the backdrop tags and are not as reliable

            // Rather conveniently, the SDK already separates the position of the center of the tag from it's
            // rotation. Since our camera should be horizontal, this simplifies the math here immensely.
            // We just need to rotate the offset vector to point in the direction that the robot is pointing
            Vector2d tagPosition = toVector(detection.metadata.fieldPosition);
            measuredRobotPose = measuredRobotPose.plus(tagPosition.plus(toVector(detection.ftcPose).rotated(currentPose.getHeading() + Math.PI)));
            counter++;

            // TODO: account for the fact that the camera is not at the center of the robot
        }
        measuredRobotPose = measuredRobotPose.div(counter); // Calculate average result
        DashboardManager.getInstance().put("count", counter);
        DashboardManager.getInstance().put("mx", measuredRobotPose.getX());
        DashboardManager.getInstance().put("my", measuredRobotPose.getY());

        // Apply feedback and send updated pose
        Vector2d feedbackResult = measuredRobotPose.minus(currentPose.vec()).times(convergenceGain);
        odometry.addDeltaPose(new Pose2d(feedbackResult, 0.0));
        
        return result;
    }

    private static Vector2d toVector(VectorF vector) {
        return new Vector2d(vector.get(0), vector.get(1));
    }

    private static Vector2d toVector(AprilTagPoseFtc vector) {
        return new Vector2d(vector.x, vector.y);
    }


    @Override
    public void onDrawFrame(Canvas canvas, int onscreenWidth, int onscreenHeight, float scaleBmpPxToCanvasPx, float scaleCanvasDensity, Object userContext) {
        aprilTagProcessor.onDrawFrame(canvas, onscreenWidth, onscreenHeight, scaleBmpPxToCanvasPx, scaleCanvasDensity, userContext);
    }
}
