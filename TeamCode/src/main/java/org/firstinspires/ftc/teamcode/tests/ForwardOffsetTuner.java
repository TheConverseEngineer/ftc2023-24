package org.firstinspires.ftc.teamcode.tests;

import androidx.annotation.NonNull;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.common.trajectory.DriveSubsystem;

@TeleOp
@Disabled
public class ForwardOffsetTuner extends LinearOpMode {
    private DcMotor leftFront, leftRear, rightFront, rightRear, rightEncoder;
    private final double[] encoderValues = {0, 0, 0};

    @Override
    public void runOpMode() throws InterruptedException {
        leftFront = hardwareMap.get(DcMotorEx.class, "leftFrontDrive");
        leftRear = hardwareMap.get(DcMotorEx.class, "leftRearDrive");
        rightRear = hardwareMap.get(DcMotorEx.class, "rightRearDrive");
        rightFront = hardwareMap.get(DcMotorEx.class, "rightFrontDrive");
        rightEncoder = hardwareMap.get(DcMotorEx.class, "rightEncoder");


        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftRear.setDirection(DcMotorSimple.Direction.REVERSE);

        rightEncoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftRear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        rightEncoder.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftRear.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);


        waitForStart();

        PoseVariable currentPose = new PoseVariable(new Variable(0, 0), new Variable(0, 0), 0);

        DcMotor[] pods = {leftRear, rightEncoder, rightFront};

        while (opModeIsActive() && !isStopRequested()) {

            if (gamepad1.a) {
                leftFront.setPower(0.3);
                rightFront.setPower(-0.3);
                leftRear.setPower(0.3);
                rightRear.setPower(-0.3);
            } else {
                leftFront.setPower(0);
                rightFront.setPower(0);
                leftRear.setPower(0);
                rightRear.setPower(0);
            }

            double[] encoderDeltaInches = new double[3];

            for (int i = 0; i < 3; i++) {
                encoderDeltaInches[i] = pods[i].getCurrentPosition()-encoderValues[i];
                encoderValues[i] += encoderDeltaInches[i];
                encoderDeltaInches[i] *= DriveSubsystem.ODO_IN_PER_TICK[i];
            }

            double dTheta = (encoderDeltaInches[1] - encoderDeltaInches[0])/DriveSubsystem.ODO_TRACK_WIDTH;
            PoseVariable delta = runPoseExponential(
                    (encoderDeltaInches[0] + encoderDeltaInches[1])/2,
                    new Variable(encoderDeltaInches[2], -dTheta),
                    dTheta,
                    currentPose.heading
            );

            currentPose = currentPose.plus(delta);

            telemetry.addData("pose", currentPose.toString());
            telemetry.update();
        }
    }

    private static class Variable {
        private final double constant, coefficient;

        public Variable(double constant, double coefficient) {
            this.constant = constant;
            this.coefficient = coefficient;
        }

        public Variable plus(Variable other) {
            return new Variable(constant + other.constant, coefficient + other.coefficient);
        }

        public Variable minus(Variable other) {
            return new Variable(constant - other.constant, coefficient - other.coefficient);
        }

        public Variable times(double other) {
            return new Variable(constant*other, coefficient*other);
        }

        @NonNull
        @Override
        public String toString() {
            return coefficient + "x + " + constant;
        }
    }

    private static class PoseVariable {
        public final Variable x, y;
        public final double heading;

        public PoseVariable(Variable x, Variable y, double heading) {
            this.x = x;
            this.y = y;
            this.heading = heading;
        }

        public PoseVariable plus(PoseVariable other) {
            return new PoseVariable(
                    x.plus(other.x),
                    y.plus(other.y),
                    heading + other.heading
            );
        }

        @NonNull
        @Override
        public String toString() {
            return "{" +
                      x +
                    ", " + y +
                    ", " + Math.toDegrees(heading) +
                    '}';
        }
    }

    public static PoseVariable runPoseExponential(double dX, Variable dY, double dTheta, double currentHeading) {
        // First handle the case where dTheta = 0 and we need to take the limit to avoid a divide-by-zero error
        double sin;
        double cos;
        if (Math.abs(dTheta) < 1E-9) {
            sin = 1.0 - 1.0 / 6.0 * dTheta * dTheta;
            cos = 0.5 * dTheta;
        } else {
            sin = Math.sin(dTheta) / dTheta;
            cos = (1 - Math.cos(dTheta)) / dTheta;
        }

        // Now integrate
        PoseVariable integratedDelta = new PoseVariable(
                new Variable(sin*dX, 0).minus(dY.times(cos)),
                new Variable(cos*dX, 0).plus(dY.times(sin)),
                dTheta
        );

        double sinG = Math.sin(currentHeading);
        double cosG = Math.cos(currentHeading);

        // And now rotate it into global space
        return new PoseVariable(
                integratedDelta.x.times(cosG).minus(integratedDelta.y.times(sinG)),
                integratedDelta.x.times(sinG).plus(integratedDelta.y.times(cosG)),
                dTheta
        );
    }
}
