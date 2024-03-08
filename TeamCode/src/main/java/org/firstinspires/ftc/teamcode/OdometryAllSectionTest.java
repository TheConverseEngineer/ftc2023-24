package org.firstinspires.ftc.teamcode;

import android.util.Size;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.example.thundercore.math.geometry.SimpleVector2d;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.teamcode.common.command.CommandOpMode;
import org.firstinspires.ftc.teamcode.vision.AprilTagLocalizer;
import org.firstinspires.ftc.vision.VisionPortal;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

@TeleOp
public class OdometryAllSectionTest extends CommandOpMode {

    TrackingWheelReference ref;
    public DcMotorEx leftFront, leftRear, rightFront, rightRear;

    VisionPortal portal;
    AprilTagLocalizer localizer;

    public AtomicReference<Double> lastRecordedHeading = new AtomicReference<>(0.0);


    @Override
    public void initialize() {
        ref = new TrackingWheelReference(hardwareMap, new ArrayList<>(), new ArrayList<>());

        leftFront = hardwareMap.get(DcMotorEx.class, "leftFrontDrive");
        leftRear = hardwareMap.get(DcMotorEx.class, "leftRearDrive");
        rightRear = hardwareMap.get(DcMotorEx.class, "rightRearDrive");
        rightFront = hardwareMap.get(DcMotorEx.class, "rightFrontDrive");


        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftRear.setDirection(DcMotorSimple.Direction.REVERSE);


        leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftRear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftRear.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        localizer = AprilTagLocalizer.generate(() -> lastRecordedHeading.get(),0,0,0,0);

        portal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(CameraName.class, "webcam 1"))
                .setCameraResolution(new Size(640, 480))
                .addProcessor(localizer)
                .build();


    }

    @Override
    public void run() {
        ref.update();
        TelemetryPacket packet = new TelemetryPacket();
        Pose2d pose = ref.getPoseEstimate();
        lastRecordedHeading.set(pose.getHeading());

        telemetry.addData("h", Math.toDegrees(pose.getHeading()));

        SimpleVector2d vision = localizer.getRecent();

        telemetry.addData("v", vision.getX() + " " + vision.getY());

        packet.fieldOverlay()
                .setStroke("green")
                .strokeCircle(vision.getX(), vision.getY(), 5)
                .strokeLine(vision.getX(), vision.getY(), vision.getX() + 5*Math.cos(pose.getHeading()), vision.getY() + 5*Math.sin(pose.getHeading()));


        driveWithGamepad(gamepad1);

        FtcDashboard.getInstance().sendTelemetryPacket(packet);
    }

    public void driveWithGamepad(Gamepad gamepad) {
        double x = gamepad.left_stick_x;
        double y = -gamepad.left_stick_y;
        double rw = gamepad.right_stick_x;

        double speedTrigger = gamepad.right_trigger;
        double brakeTrigger = gamepad.left_trigger;

        double theta = Math.atan2(y, x*1.1) - Math.PI/4  - ref.getPoseEstimate().getHeading();
        double rho = (x*x + y*y)*(0.6 + 0.4*speedTrigger)/(Math.max(Math.abs(Math.cos(theta)), Math.abs(Math.sin(theta))))*(1-brakeTrigger*0.5);
        double w = rw*(0.4+0.6*speedTrigger)*(1-brakeTrigger*0.5);
        if (rho+Math.abs(rw) > 1) {
            w = rw/(rho + Math.abs(rw));
            rho /= (rho + Math.abs(rw));
        }
        leftFront.setPower(rho*Math.cos(theta)+w);
        rightFront.setPower(rho*Math.sin(theta)-w);
        rightRear.setPower(rho*Math.cos(theta)-w);
        leftRear.setPower(rho*Math.sin(theta)+w);
    }
}
