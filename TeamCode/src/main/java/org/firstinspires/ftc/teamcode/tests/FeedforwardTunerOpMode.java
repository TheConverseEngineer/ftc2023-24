package org.firstinspires.ftc.teamcode.tests;

import static org.firstinspires.ftc.teamcode.common.trajectory.DriveSubsystem.MAX_ACCEL;
import static org.firstinspires.ftc.teamcode.common.trajectory.DriveSubsystem.MAX_VEL;
import static org.firstinspires.ftc.teamcode.common.trajectory.DriveSubsystem.ODO_IN_PER_TICK;
import static org.firstinspires.ftc.teamcode.common.trajectory.DriveSubsystem.kA;
import static org.firstinspires.ftc.teamcode.common.trajectory.DriveSubsystem.kStatic;
import static org.firstinspires.ftc.teamcode.common.trajectory.DriveSubsystem.kV;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.kinematics.Kinematics;
import com.acmerobotics.roadrunner.profile.MotionProfile;
import com.acmerobotics.roadrunner.profile.MotionProfileGenerator;
import com.acmerobotics.roadrunner.profile.MotionState;
import com.acmerobotics.roadrunner.util.NanoClock;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.common.trajectory.DriveSubsystem;
import org.firstinspires.ftc.teamcode.common.utils.Encoder;

import java.util.Objects;

@Disabled
@TeleOp
public class FeedforwardTunerOpMode extends LinearOpMode {
    public static double DISTANCE = 60; // in

    private FtcDashboard dashboard = FtcDashboard.getInstance();

    private DriveSubsystem drive;

    enum Mode {
        DRIVER_MODE,
        TUNING_MODE
    }

    private Mode mode;

    private static MotionProfile generateProfile(boolean movingForward) {
        MotionState start = new MotionState(movingForward ? 0 : DISTANCE, 0, 0, 0);
        MotionState goal = new MotionState(movingForward ? DISTANCE : 0, 0, 0, 0);
        return MotionProfileGenerator.generateSimpleMotionProfile(start, goal, MAX_VEL, MAX_ACCEL);
    }

    @Override
    public void runOpMode() {
        telemetry = new MultipleTelemetry(this.telemetry, dashboard.getTelemetry());

        drive = new DriveSubsystem(hardwareMap);

        final VoltageSensor voltageSensor = hardwareMap.voltageSensor.iterator().next();

        mode = Mode.TUNING_MODE;

        NanoClock clock = NanoClock.system();

        Encoder left = new Encoder(drive.leftFront);
        Encoder right = new Encoder(drive.leftRear);
        left.setDirection(Encoder.Direction.REVERSE);

        telemetry.addLine("Ready!");
        telemetry.update();
        telemetry.clearAll();

        waitForStart();

        if (isStopRequested()) return;

        boolean movingForwards = true;
        MotionProfile activeProfile = generateProfile(true);
        double profileStart = clock.seconds();


        while (!isStopRequested()) {
            telemetry.addData("mode", mode);

            switch (mode) {
                case TUNING_MODE:
                    if (gamepad1.y) {
                        mode = Mode.DRIVER_MODE;
                    }

                    // calculate and set the motor power
                    double profileTime = clock.seconds() - profileStart;

                    if (profileTime > activeProfile.duration()) {
                        // generate a new profile
                        movingForwards = !movingForwards;
                        activeProfile = generateProfile(movingForwards);
                        profileStart = clock.seconds();
                    }

                    MotionState motionState = activeProfile.get(profileTime);
                    double targetPower = Kinematics.calculateMotorFeedforward(motionState.getV(), motionState.getA(), kV, kA, kStatic);

                    final double NOMINAL_VOLTAGE = 12.0;
                    final double voltage = voltageSensor.getVoltage();
                    drive.setDrivePower(new Pose2d(NOMINAL_VOLTAGE / voltage * targetPower, 0, 0));

                    double currentVelo = -(right.getCorrectedVelocity() + left.getCorrectedVelocity())*ODO_IN_PER_TICK[1]/2;

                    // update telemetry
                    telemetry.addData("targetVelocity", motionState.getV());
                    telemetry.addData("measuredVelocity", currentVelo);
                    telemetry.addData("error", motionState.getV() - currentVelo);
                    break;
                case DRIVER_MODE:
                    if (gamepad1.b) {
                        mode = Mode.TUNING_MODE;
                        movingForwards = true;
                        activeProfile = generateProfile(movingForwards);
                        profileStart = clock.seconds();
                    }

                    drive.driveWithGamepad(gamepad1);
                    break;
            }

            telemetry.update();
        }
    }
}
