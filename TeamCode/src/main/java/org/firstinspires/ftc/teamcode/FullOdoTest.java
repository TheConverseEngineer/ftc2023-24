package org.firstinspires.ftc.teamcode;

import android.util.Size;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.common.command.CommandOpMode;
import org.firstinspires.ftc.teamcode.common.simulation.VirtualDummyMotorEx;
import org.firstinspires.ftc.teamcode.common.trajectory.OdometrySubsystem;
import org.firstinspires.ftc.teamcode.common.utils.DashboardManager;
import org.firstinspires.ftc.teamcode.common.vision.AprilTagOdometryProcessor;
import org.firstinspires.ftc.vision.VisionPortal;


@TeleOp
public class FullOdoTest extends CommandOpMode {

    VisionPortal visionPortal;
    AprilTagOdometryProcessor aprilTagProcessor;

    DcMotor front = new VirtualDummyMotorEx(),
            right = new VirtualDummyMotorEx(),
            left = new VirtualDummyMotorEx();

    OdometrySubsystem odometrySubsystem;

    @Override
    public void initialize() {

        enableDashboard();

        odometrySubsystem = new OdometrySubsystem(left, right, front);

        aprilTagProcessor = AprilTagOdometryProcessor.generate(
                 odometrySubsystem,757.2307669, 751.5267343, 484.4751617, 337.4213896
        );

        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .setCameraResolution(new Size(1024, 576))
                .enableLiveView(true)
                .addProcessor(aprilTagProcessor)
                .build();
    }

    @Override
    public void run() {
        telemetry.addData("detected", aprilTagProcessor.getDetectedItems());
        telemetry.addData("heading error", aprilTagProcessor.getAverageHeadingError());
        telemetry.addLine(odometrySubsystem.getPoseEstimate().toString());

        DashboardManager.getInstance().drawRobot(odometrySubsystem.getPoseEstimate());
    }
}
