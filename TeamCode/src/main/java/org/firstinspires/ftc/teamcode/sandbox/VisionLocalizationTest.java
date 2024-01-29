package org.firstinspires.ftc.teamcode.sandbox;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Quaternion;
import org.firstinspires.ftc.teamcode.common.command.CommandOpMode;
import org.firstinspires.ftc.teamcode.common.simulation.VirtualDummyMotorEx;
import org.firstinspires.ftc.teamcode.common.trajectory.OdometrySubsystem;
import org.firstinspires.ftc.teamcode.common.utils.DashboardManager;
import org.firstinspires.ftc.teamcode.common.vision.AprilTagLocalizationProcessor;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagLibrary;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

@TeleOp
public class VisionLocalizationTest extends CommandOpMode {

    DcMotor front = new VirtualDummyMotorEx(),
            right = new VirtualDummyMotorEx(),
            left = new VirtualDummyMotorEx();

    OdometrySubsystem odometrySubsystem;

    VisionPortal visionPortal;

    public final AprilTagLibrary centerStageLibrary = new AprilTagLibrary.Builder()
            .addTag(9, "small",2, new VectorF(-72, 0, 4), DistanceUnit.INCH, Quaternion.identityQuaternion())
            .addTag(10, "big",5, new VectorF(-72, 5.5f, 5.5f), DistanceUnit.INCH, Quaternion.identityQuaternion())
            .build();

    @Override
    public void initialize() {
        enableDashboard();

        odometrySubsystem = new OdometrySubsystem(left, right, front);

        AprilTagProcessor aprilTagProcessor = new AprilTagProcessor.Builder()
                .setOutputUnits(DistanceUnit.INCH, AngleUnit.RADIANS)
                .setTagLibrary(centerStageLibrary)
                .setDrawTagOutline(true)
                .setDrawTagID(true)
                .setLensIntrinsics(757.2307669, 751.5267343, 484.4751617, 337.4213896)
                .build();

        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .enableLiveView(true)
                .addProcessor(aprilTagProcessor)
                .build();

    }

    @Override
    public void run() {
        odometrySubsystem.updateTrackingWheels();

        DashboardManager.getInstance().put("in loop", true);

        DashboardManager.getInstance().drawRobot(odometrySubsystem.getPoseEstimate());

        sleep(2);
    }
}
