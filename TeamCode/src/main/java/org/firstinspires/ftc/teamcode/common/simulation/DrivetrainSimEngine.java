package org.firstinspires.ftc.teamcode.common.simulation;

import com.acmerobotics.roadrunner.geometry.Pose2d;

import org.firstinspires.ftc.teamcode.common.trajectory.DriveSubsystem;

public class DrivetrainSimEngine {
    VirtualDummyMotorEx leftFront, rightFront, leftRear, rightRear;

    // Both of the following are inches from the center
    public static final double TRACK_WIDTH = 5.244095;
    public static final double WHEEL_BASE = 6.435;

    public static final double R = 3.77953/2;

    public static final double mu = 1 / (TRACK_WIDTH + WHEEL_BASE);

    public static final double MAX_RAD_PER_SEC = (312.0/60)*2*(24.0/20)*Math.PI;

    public static final Matrix forwardKinematics = new Matrix(
            new double[][]{{1, 1, 1, 1}, {-1, 1, -1, 1},
            {-mu, -mu, mu, mu}}
    ).times(R/4);

    private Matrix currentPose;

    public Matrix getCurrentPose() {
        return currentPose;
    }

    public void setCurrentPose(Pose2d pose) {
        this.currentPose = new Matrix(new double[][]{{pose.getX()}, {pose.getY()}, {pose.getHeading()}});
    }

    public DrivetrainSimEngine(VirtualDummyMotorEx leftFront, VirtualDummyMotorEx rightFront, VirtualDummyMotorEx leftRear, VirtualDummyMotorEx rightRear) {
        this.leftFront = leftFront;
        this.rightFront = rightFront;
        this.leftRear = leftRear;
        this.rightRear = rightRear;

        currentPose = new Matrix(new double[][]{{0}, {0}, {0}});
    }

    public void update(double deltaTime) {
        Matrix localDelta =  forwardKinematics.times(new double[][]{
                        {leftFront.getPower()}, {leftRear.getPower()}, {rightRear.getPower()}, {rightFront.getPower()}})
                .times(MAX_RAD_PER_SEC*deltaTime);

        Matrix delta = Matrix.toGlobal(
                deltaTime, forwardKinematics.times(new double[][]{
                        {leftFront.getPower()}, {leftRear.getPower()}, {rightRear.getPower()}, {rightFront.getPower()}})
                        .times(MAX_RAD_PER_SEC),
                currentPose
        );

        currentPose = currentPose.plus(delta);

        double leftDelta = 0.5*(2*localDelta.get(0, 0) - DriveSubsystem.ODO_TRACK_WIDTH*localDelta.get(2, 0));
        double rightDelta = 0.5*(2*localDelta.get(0, 0) + DriveSubsystem.ODO_TRACK_WIDTH*localDelta.get(2, 0));
        double forwardDelta = DriveSubsystem.ODO_FRONT_OFFSET*localDelta.get(2, 0) + localDelta.get(1, 0);

        leftFront.internalSetPosition(leftFront.internalGetExactPosition() + leftDelta/ DriveSubsystem.ODO_IN_PER_TICK);
        rightRear.internalSetPosition(rightRear.internalGetExactPosition() + rightDelta/ DriveSubsystem.ODO_IN_PER_TICK);
        leftRear.internalSetPosition(leftRear.internalGetExactPosition() + forwardDelta/ DriveSubsystem.ODO_IN_PER_TICK);

        leftFront.internalSetVelocity(leftDelta/(DriveSubsystem.ODO_IN_PER_TICK*deltaTime));
        rightRear.internalSetVelocity(rightDelta/(DriveSubsystem.ODO_IN_PER_TICK*deltaTime));
        leftRear.internalSetVelocity(forwardDelta/(DriveSubsystem.ODO_IN_PER_TICK*deltaTime));
    }
}
