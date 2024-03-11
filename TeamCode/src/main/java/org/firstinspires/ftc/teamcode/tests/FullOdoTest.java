package org.firstinspires.ftc.teamcode.tests;

import android.util.Size;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.common.command.CommandOpMode;
import org.firstinspires.ftc.teamcode.common.simulation.VirtualDummyMotorEx;
import org.firstinspires.ftc.teamcode.common.trajectory.DriveSubsystem;
import org.firstinspires.ftc.teamcode.common.trajectory.FusedOdoSubsystem;
import org.firstinspires.ftc.teamcode.common.trajectory.OdometrySubsystem;
import org.firstinspires.ftc.teamcode.common.utils.DashboardManager;
import org.firstinspires.ftc.teamcode.common.vision.AprilTagOdometryProcessor;
import org.firstinspires.ftc.vision.VisionPortal;


@TeleOp
@Disabled
public class FullOdoTest extends CommandOpMode {

    VisionPortal visionPortal;
    AprilTagOdometryProcessor aprilTagProcessor;


    FusedOdoSubsystem odometrySubsystem;

    DriveSubsystem drive;

    @Override
    public void initialize() {

        enableDashboard();

        drive = new DriveSubsystem(hardwareMap);
        odometrySubsystem = drive.getOdometry();

        //aprilTagProcessor = AprilTagOdometryProcessor.generate(
          //       odometrySubsystem,757.2307669, 751.5267343, 484.4751617, 337.4213896
        //);

        /*visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .setCameraResolution(new Size(1024, 576))
                .enableLiveView(true)
                .addProcessor(aprilTagProcessor)
                .build();
*/
        scheduler.registerSubsystem(drive);
    }

    @Override
    public void run() {
        drive.driveWithGamepad(gamepad1);
        //telemetry.addData("detected", aprilTagProcessor.getDetectedItems());
        //telemetry.addData("heading error", aprilTagProcessor.getAverageHeadingError());
        telemetry.addLine(odometrySubsystem.getPoseEstimate().toString());

        DashboardManager.getInstance().drawRobot(odometrySubsystem.getPoseEstimate());
    }
}
