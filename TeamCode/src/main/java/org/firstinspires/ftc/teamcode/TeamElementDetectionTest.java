package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.vision.TeamElementDetectionPipeline;
import org.firstinspires.ftc.teamcode.vision.VisionSubsystem;

@Autonomous
public class TeamElementDetectionTest extends LinearOpMode {

    VisionSubsystem visionSubsystem;

    @Override
    public void runOpMode() throws InterruptedException {
        visionSubsystem = new VisionSubsystem(hardwareMap, TeamElementDetectionPipeline.Alliance.RED);
        visionSubsystem.enableTeamDetection();
        waitForStart();

        while (opModeIsActive() && !isStopRequested()) {
            telemetry.addData("frames", visionSubsystem.getFramesAnalyzed());
            telemetry.addData("result", visionSubsystem.toString());
            telemetry.update();
        }
    }
}
