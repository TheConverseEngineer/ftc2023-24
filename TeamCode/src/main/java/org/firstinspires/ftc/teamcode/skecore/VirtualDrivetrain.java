package org.firstinspires.ftc.teamcode.skecore;

import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.tests.DriveConstants;
import org.firstinspires.ftc.teamcode.blacksmithcore.kinematics;
import org.firstinspires.ftc.teamcode.blacksmithcore.MutablePose2D;
import org.firstinspires.ftc.teamcode.blacksmithcore.Pose2d;
import org.firstinspires.ftc.teamcode.blacksmithcore.WheelSpeeds;
import org.firstinspires.ftc.teamcode.skecore.hardware.DcMotorTypes;
import org.firstinspires.ftc.teamcode.skecore.hardware.VirtualDcMotorEx;

public class VirtualDrivetrain {
    public final DcMotorEx leftFront, leftRear, rightRear, rightFront;

    private final MutablePose2D pose = new MutablePose2D(0, 0, 0);

    double lastLF, lastLR, lastRR, lastRF;

    public VirtualDrivetrain(DcMotorTypes type) {
        leftFront = new VirtualDcMotorEx(type);
        leftRear = new VirtualDcMotorEx(type);
        rightRear = new VirtualDcMotorEx(type);
        rightFront = new VirtualDcMotorEx(type);
    }

    public void update() {
        double dLF = leftFront.getCurrentPosition() - lastLF;
        double dLR = leftRear.getCurrentPosition() - lastLR;
        double dRR = rightRear.getCurrentPosition() - lastRR;
        double dRF = rightFront.getCurrentPosition() - lastRF;

        pose.integrateGlobalTransform(kinematics.mecanumForwardKinematics(
                new WheelSpeeds(dLF, dLR, dRR, dRF),
                DriveConstants.TRACK_WIDTH, DriveConstants.WHEEL_BASE, DriveConstants.WHEEL_RADIUS
        ));

        lastLF += dLF;
        lastLR += dLR;
        lastRR += dRR;
        lastRF += dRF;
    }

    public void setPower(Pose2d velocity) {
        WheelSpeeds speeds = kinematics.mecanumInverseKinematics(velocity, DriveConstants.TRACK_WIDTH, DriveConstants.WHEEL_BASE, DriveConstants.WHEEL_RADIUS);
        leftFront.setPower(speeds.getLeftFront());
        rightFront.setPower(speeds.getRightFront());
        leftRear.setPower(speeds.getLeftRear());
        rightRear.setPower(speeds.getRightRear());
    }

    public MutablePose2D getPose() {
        return this.pose;
    }
}
