package org.firstinspires.ftc.teamcode.thundercore.controllers;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.blacksmithcore.utils;

import java.util.ArrayList;

public class Controller {

    private final FeedbackController[] feedbacks;
    private final FeedforwardController[] feedforwards;
    private final DcMotor[] motors;

    private double targetPos;
    private double lastOutput = 0;

    private final double minPower;
    private final double zeroThreshold;

    private Controller(DcMotor[] motors, FeedbackController[] feedbacks, FeedforwardController[] feedforwards, double targetPos, double minPower, double zeroThreshold) {
        this.feedbacks = feedbacks;
        this.feedforwards = feedforwards;
        this.motors = motors;
        this.targetPos = targetPos;
        this.minPower = minPower;
        this.zeroThreshold = zeroThreshold;
    }

    public void update(double currentPosition) {
        lastOutput = 0.0;
        for (FeedbackController i : feedbacks) lastOutput += i.calculate(currentPosition, targetPos);
        for (FeedforwardController i : feedforwards) lastOutput += i.calculate(currentPosition, 0, 0);

        lastOutput = utils.clamp(lastOutput, -1, 1);
        if (Math.abs(lastOutput) < minPower && Math.abs(lastOutput) > zeroThreshold ) lastOutput = minPower * lastOutput / Math.abs(lastOutput);

        for (DcMotor motor : motors) motor.setPower(lastOutput);
    }

    public double getLastOutput () {
        return lastOutput;
    }

    public void setNewTarget(double newTarget) {
        this.targetPos = newTarget;
    }

    public double getCurrentTarget() {
        return this.targetPos;
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    public static class Builder {

        ArrayList<FeedbackController> feedbackControllers = new ArrayList<>();
        ArrayList<FeedforwardController> feedforwardControllers = new ArrayList<>();
        ArrayList<DcMotor> motors = new ArrayList<>();

        /** Adds a PID controller to this controller
         *
         * @param coefficients  the PID coefficients
         */
        public Builder addPIDController(PIDController.PIDCoefficients coefficients) {
            feedbackControllers.add(new PIDController(coefficients));
            return this;
        }

        /** Adds an arm feedforward to this controller. Note that in order for this feedforward to
         * work, position must be inputted in radians, with 0 radians being vertical. 
         *
         * @param coefficients  the arm feedforward coefficients
         */
        public Builder addArmFeedforward(ArmFeedforward.ArmFeedforwardCoefficients coefficients) {
            feedforwardControllers.add(new ArmFeedforward(coefficients));
            return this;
        }

        /** Adds a gravity feedforward to this controller
         *
         * @param coefficients  the gravity feedforward coefficients
         */
        public Builder addGravityFeedforward(GravityFeedforward.GravityFeedforwardCoefficients coefficients) {
            feedforwardControllers.add(new GravityFeedforward(coefficients));
            return this;
        }

        public Builder addMotor(DcMotor motor) {
            motors.add(motor);
            return this;
        }

        public Controller build(double targetPos, double minPower, double zeroThreshold) {
            return new Controller(
                motors.toArray(new DcMotor[0]),
                feedbackControllers.toArray(new FeedbackController[0]),
                feedforwardControllers.toArray(new FeedforwardController[0]),
                targetPos, minPower, zeroThreshold
            );
        }

        public Controller build(double targetPos) {
            return this.build(targetPos, 0.05, 0.01);
        }
    }
}
