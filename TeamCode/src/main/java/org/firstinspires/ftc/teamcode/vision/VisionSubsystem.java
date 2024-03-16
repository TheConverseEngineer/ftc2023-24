package org.firstinspires.ftc.teamcode.vision;

import android.util.Size;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.common.command.Subsystem;
import org.firstinspires.ftc.vision.VisionPortal;

public class VisionSubsystem implements Subsystem {

    private final VisionPortal visionPortal;

    private final TeamElementDetectionPipeline detectionPipeline;

    public VisionSubsystem(HardwareMap hardwareMap, TeamElementDetectionPipeline.Alliance alliance) {
        detectionPipeline = new TeamElementDetectionPipeline(alliance);

        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .addProcessor(detectionPipeline)
                .setCameraResolution(new Size(640, 360))
                .enableLiveView(true)
                .setAutoStopLiveView(true)
                .setStreamFormat(VisionPortal.StreamFormat.YUY2)
                .build();
    }

    public void enableTeamDetection() {
        detectionPipeline.enable();
    }

    /** Closes the vision portal asynchronously.
     * Note that this will permanently stop the camera feed. It cannot be re-opened*/
    public void close() {
        visionPortal.close();
    }

    public int getFramesAnalyzed() {
        return detectionPipeline.getFramesAnalyzed();
    }

    public TeamElementDetectionPipeline.Detection getTeamElementLocation() {
        return detectionPipeline.getDetection();
    }

    @Override
    public void periodic() {

    }

    @NonNull
    @Override
    public String toString() {
        return detectionPipeline.toString();
    }
}
