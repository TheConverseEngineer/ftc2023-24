package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.ServoImplEx;

import org.firstinspires.ftc.teamcode.common.command.Subsystem;

@Config
public class GripperSubsystem implements Subsystem {

    /* open and close ranges */
    public static double RIGHT_SERVO_CLOSE = .43;
    public static double LEFT_SERVO_CLOSE = .69;
    public static double RIGHT_SERVO_OPEN = .78;
    public static double LEFT_SERVO_OPEN = .30;

    private final ServoImplEx upperClaw, lowerClaw;

    private enum CLAW_STATE { OPEN, HALF_OPEN, CLOSED }
    private CLAW_STATE currentClawState = CLAW_STATE.OPEN;

    /** Controls the two "finger joints" at the end of the claw */
    public GripperSubsystem(HardwareMap hardwareMap) {
        lowerClaw = hardwareMap.get(ServoImplEx.class, "rightFinger");
        upperClaw = hardwareMap.get(ServoImplEx.class, "leftFinger");
        lowerClaw.setPwmRange(new PwmControl.PwmRange(610, 2360));
        upperClaw.setPwmRange(new PwmControl.PwmRange(610, 2360));
    }

    /** Opens both sides of the claw */
    public void openClaw() {
        currentClawState = CLAW_STATE.OPEN;
        lowerClaw.setPosition(.8);
        upperClaw.setPosition(.2);
    }

    /** Closes both sides of the claw */
    public void closeClaw(){
        currentClawState = CLAW_STATE.CLOSED;
        lowerClaw.setPosition(.55);
        upperClaw.setPosition(.5);
    }

    public void toggleIntake() {
        if (currentClawState == CLAW_STATE.OPEN) {
            closeClaw();
        } else openClaw();
    }

    public void toggleDeposit() {
        switch (currentClawState) {
            case OPEN:
                closeClaw(); break;
            case HALF_OPEN:
                openClaw(); break;
            case CLOSED:
                halfOpenClaw(); break;
        }
    }

    public void halfOpenClaw() {
        currentClawState = CLAW_STATE.HALF_OPEN;
        lowerClaw.setPosition(.55);
        upperClaw.setPosition(.2);
    }

    /** Opens only the right claw */
    public void openRightClaw(){

    }

    /** Opens only the left claw */
    public void openLeftClaw(){

    }

    @Override
    public void periodic() { }
}
