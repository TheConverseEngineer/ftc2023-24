package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.command.SubsystemBase;
import com.arcrobotics.ftclib.controller.PIDController;
import com.arcrobotics.ftclib.trajectory.TrapezoidProfile;

import org.firstinspires.ftc.teamcode.extensions.CachedMotor;

import java.util.function.Supplier;

@Config
@SuppressWarnings("unused")
public class ActuatorSubsystem extends SubsystemBase {

    public enum ActuatorState { ARM_MODE, SLIDE_MODE }

    /** The current state of this subsystem. In order to move the slide/arm, this subsystem must be in the correct mode. */
    private ActuatorState currentState = ActuatorState.SLIDE_MODE;

    private final PIDController armController = new PIDController(elevatorKp, elevatorKi, elevatorKd);
    private final PIDController slideController = new PIDController(armKp, armKi, armKd);

    private final CachedMotor armMotor, slideMotor1, slideMotor2;

    //************************ Constants ************************//
    public static final double radPerArmTick = 1;
    public static final double slideTickPerArmTick = 1;

    public static final double slideRetractPos = 100;
    public static final double armLowerPos = 0;
    public static final double armUpperPos = 1000;
    public static final double stateSwapThreshold = 20;

    public static double elevatorKp = 0, elevatorKi = 0, elevatorKd = 0;
    public static double armKp = 0, armKi = 0, armKd = 0;
    public static double armKg = 0, slideKg = 0;
    public static double armKv = 0, slideKv = 0;

    public static final TrapezoidProfile.Constraints slideConstraints = new TrapezoidProfile.Constraints(30, 30);
    public static final TrapezoidProfile.Constraints armConstraints = new TrapezoidProfile.Constraints(30, 30);

    /* Suppliers for each subcomponent's motion profiling */
    private Supplier<TrapezoidProfile.State> armState, slideState;

    /** Constructor for the ActuatorSubsystem class
     *
     * Assumes that the slides are fully retracted and that the arm is horizontal
     */
    public ActuatorSubsystem(CachedMotor armMotor, CachedMotor slideMotor1, CachedMotor slideMotor2) {
        this.armMotor = armMotor;
        this.slideMotor1 = slideMotor1;
        this.slideMotor2 = slideMotor2;
        armController.setIntegrationBounds(-.25, .25);
        slideController.setIntegrationBounds(-.25, .25);

        armState = () -> new TrapezoidProfile.State(armLowerPos, 0);
        slideState = () -> new TrapezoidProfile.State(slideRetractPos, 0);
    }

    /** Returns the current state of this subsystem */
    public ActuatorState getCurrentState() {
        return currentState;
    }

    /** Returns true if the arm is at one of its endpoints */
    public boolean canEnableSlideMode() {
        if (currentState == ActuatorState.SLIDE_MODE) return true;

        double armPos = this.armMotor.getCurrentPosition();
        return armPos <= armLowerPos + stateSwapThreshold || armPos >= armUpperPos - stateSwapThreshold;
    }

    /** Returns true if the slide is fully retracted */
    public boolean canEnableArmMode() {
        if (currentState == ActuatorState.ARM_MODE) return true;
        else return this.slideMotor1.getCurrentPosition() <= slideRetractPos + stateSwapThreshold;
    }

    /** Sets the mode to slide mode, and returns true if the mode was successfully changed */
    public boolean enableSlideMode() {
        if (currentState == ActuatorState.SLIDE_MODE) return true;
        else if (!canEnableSlideMode()) return false;

        // Cancel the arm's motion profile and set it's target position
        double armMidpoint = (armUpperPos + armLowerPos)/2;
        armState = ()->new TrapezoidProfile.State((this.armMotor.getCurrentPosition()>armMidpoint)?armUpperPos:armLowerPos, 0);

        currentState = ActuatorState.SLIDE_MODE;
        return true;
    }

    /** Sets the mode to arm mode, and returns true if the mode was successfully changed */
    public boolean enableArmMode() {
        if (currentState == ActuatorState.ARM_MODE) return true;
        else if (!canEnableArmMode()) return false;

        // Cancel the slide's motion profile and set it's target position
        slideState = ()->new TrapezoidProfile.State(slideRetractPos, 0);

        currentState = ActuatorState.ARM_MODE;
        return true;
    }

    /** Assigns a motion profile to the arm */
    public void setArmProfile(Supplier<TrapezoidProfile.State> profile) {
        if (currentState != ActuatorState.ARM_MODE) return;
        this.armState = profile;
    }

    /** Assigns a motion profile to the slide */
    public void setSlideProfile(Supplier<TrapezoidProfile.State> profile) {
        if (currentState != ActuatorState.SLIDE_MODE) return;
        this.slideState = profile;
    }

    @Override
    public void periodic() {
        // Comment out these lines when not tuning
        armController.setPID(armKp, armKi, armKd);
        slideController.setPID(elevatorKp, elevatorKi, elevatorKd);

        // Now calculate arm motor power
        double armPos = this.armMotor.getCurrentPosition();
        double armFeedforward = Math.cos(radPerArmTick * armPos)*armKg;
        TrapezoidProfile.State armTarget = armState.get();

        this.armMotor.set(armController.calculate(armPos, armTarget.position) + armFeedforward + getCurrentArmProfileFeedforward(armTarget));

        // Finally, calculate slide motor power
        double slidePos = this.slideMotor1.getCurrentPosition() - armPos*slideTickPerArmTick; // Adjusts for the fact that the slide pulley is coaxial
        double slideFeedforward = Math.cos(radPerArmTick * armPos)*slideKg;
        TrapezoidProfile.State slideTarget = slideState.get();

        double power = slideController.calculate(slidePos, slideTarget.position) + slideFeedforward + getCurrentSlideProfileFeedforward(slideTarget);

        this.slideMotor1.set(power);
        this.slideMotor2.set(power);
    }

    /** Returns the velocity/acceleration feedforward from the arm's current motion profile */
    private double getCurrentArmProfileFeedforward(TrapezoidProfile.State state) {
        return armKv*state.velocity;
    }

    /** Returns the velocity/acceleration feedforward from the slide's current motion profile */
    private double getCurrentSlideProfileFeedforward(TrapezoidProfile.State state) {
        return slideKv*state.velocity;
    }

    /** Returns the current position of the slide */
    public double getSlidePos() {
        return this.slideMotor1.getCurrentPosition();
    }

    /** Returns the current position of the arm */
    public double getArmPos() {
        return this.slideMotor1.getCurrentPosition();
    }
}
