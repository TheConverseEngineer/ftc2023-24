package org.firstinspires.ftc.teamcode.tests;

import android.util.Size;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.ftccommon.internal.manualcontrol.parameters.ImuParameters;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.common.command.CommandOpMode;
import org.firstinspires.ftc.teamcode.common.simulation.VirtualDummyMotorEx;
import org.firstinspires.ftc.teamcode.common.trajectory.DriveSubsystem;
import org.firstinspires.ftc.teamcode.common.trajectory.OdometrySubsystem;
import org.firstinspires.ftc.teamcode.common.utils.DashboardManager;
import org.firstinspires.ftc.teamcode.common.vision.AprilTagOdometryProcessor;
import org.firstinspires.ftc.vision.VisionPortal;


@TeleOp
@Config
public class FullOdoTest extends CommandOpMode {

    VisionPortal visionPortal;
    AprilTagOdometryProcessor aprilTagProcessor;


    OdometrySubsystem odometrySubsystem;

    DriveSubsystem drive;
    IMU imu;

    int counter = 0;
    public static double Hz = 10;

    @Override
    public void initialize() {

        enableDashboard();

        drive = new DriveSubsystem(hardwareMap);
        odometrySubsystem = drive.getOdometry();


        imu = hardwareMap.get(IMU.class, "eimu");
        IMU.Parameters params = new IMU.Parameters(
                new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.RIGHT,
                        RevHubOrientationOnRobot.UsbFacingDirection.UP
                )
        );
        imu.initialize(params);
        imu.resetYaw();



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
        telemetry.addLine(odometrySubsystem.getEncoderRaw());


        if (counter >= Hz) {
            counter = 0;
            double angle = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
            Pose2d currentPose = odometrySubsystem.getPoseEstimate();
            odometrySubsystem.setPoseEstimate(new Pose2d(currentPose.getX(), currentPose.getY(), angle));
        }

        DashboardManager.getInstance().drawRobot(odometrySubsystem.getPoseEstimate());
    }
}
