package org.firstinspires.ftc.teamcode.sandbox;

import android.util.Size;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.example.thundercore.localization.AprilTagLocalizer;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

import java.util.ArrayList;

@TeleOp
public class AprilTagIPPETest extends LinearOpMode {

    VisionPortal portal;

    @Override
    public void runOpMode() throws InterruptedException {
        AprilTagLocalizer localizer = AprilTagLocalizer.generate(822.317,822.317, 319.495, 236.834);

        telemetry = FtcDashboard.getInstance().getTelemetry();

        portal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(CameraName.class, "webcam 1"))
                .setCameraResolution(new Size(640, 480))
                .addProcessor(localizer)
                .build();

        waitForStart();

        while (opModeIsActive() && !isStopRequested()) {
            TelemetryPacket packet = new TelemetryPacket();
            //packet.put("delay", localizer.getAvg());
            FtcDashboard.getInstance().sendTelemetryPacket(packet);
            /*ArrayList<AprilTagDetection> detections = localizer.getDetections();

            if (detections == null) {
                telemetry.update();
                continue;
            }

            for (AprilTagDetection detection : detections) {
                if (detection.metadata != null) {
                    telemetry.addLine(String.format("\n==== (ID %d) %s", detection.id, detection.metadata.name));
                    telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f", detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z));
                } else {
                    telemetry.addLine(String.format("\n==== (ID %d) Unknown", detection.id));
                    telemetry.addLine(String.format("Center %6.0f %6.0f", detection.center.x, detection.center.y));
                }
            }

            telemetry.update();
            */

            sleep(8);
        }
    }
}
