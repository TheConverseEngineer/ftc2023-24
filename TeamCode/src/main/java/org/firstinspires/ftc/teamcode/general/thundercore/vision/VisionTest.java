package org.firstinspires.ftc.teamcode.general.thundercore.vision;

import android.util.Size;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagLibrary;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.ArrayList;

public class VisionTest extends LinearOpMode {

    VisionPortal portal;
    AprilTagProcessor aprilTagProcessor;
    AprilTagLibrary tagLibrary;

    @Override
    public void runOpMode() throws InterruptedException {
        tagLibrary = new AprilTagLibrary.Builder()
                .addTag(0, "Tag 0", 3, DistanceUnit.INCH)
                .addTag(1, "Tag 1", 3, DistanceUnit.INCH)
                .build();

        aprilTagProcessor = new AprilTagProcessor.Builder()
                .setTagLibrary(tagLibrary)
                .setDrawTagID(true)
                .setDrawTagOutline(true)
                .setDrawAxes(true)
                .setDrawCubeProjection(true)
                .build();

        portal = new VisionPortal.Builder()
                .setCamera(BuiltinCameraDirection.BACK)
                .setCameraResolution(new Size(640, 480))
                .setStreamFormat(VisionPortal.StreamFormat.YUY2)
                .addProcessor(aprilTagProcessor)
                .enableCameraMonitoring(true)
                .setAutoStopLiveView(true)
                .build();

        waitForStart();

        while (isStarted() && !isStopRequested()) {

            // Get any newly-detected tags
            ArrayList<AprilTagDetection> detections = aprilTagProcessor.getFreshDetections();
            if (detections == null) continue;

            for (AprilTagDetection detection : detections) {

            }
        }
    }
}
