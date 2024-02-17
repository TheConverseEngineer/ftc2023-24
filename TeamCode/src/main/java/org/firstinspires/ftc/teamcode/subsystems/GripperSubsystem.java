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

    private final ServoImplEx rightServo, leftServo;

    /** Controls the two "finger joints" at the end of the claw */
    public GripperSubsystem(HardwareMap hardwareMap) {
        rightServo = hardwareMap.get(ServoImplEx.class, "rightFinger");
        leftServo = hardwareMap.get(ServoImplEx.class, "leftFinger");
        leftServo.setPwmRange(new PwmControl.PwmRange(610, 2360));
        rightServo.setPwmRange(new PwmControl.PwmRange(610, 2360));
    }

    /** Opens both sides of the claw */
    public void openClaw() {
        rightServo.setPosition(RIGHT_SERVO_OPEN);
        leftServo.setPosition(LEFT_SERVO_OPEN);
    }

    /** Closes both sides of the claw */
    public void closeClaw(){
        rightServo.setPosition(RIGHT_SERVO_CLOSE);
        leftServo.setPosition(LEFT_SERVO_CLOSE);
    }

    /** Opens only the right claw */
    public void openRightClaw(){
        rightServo.setPosition(RIGHT_SERVO_OPEN);
    }

    /** Opens only the left claw */
    public void openLeftClaw(){
        leftServo.setPosition(LEFT_SERVO_OPEN);
    }

    /** Closes only the right claw */
    public void closeRightClaw(){
        rightServo.setPosition(RIGHT_SERVO_CLOSE);
    }

    /** Closes only the left claw */
    public void closeLeftClaw(){
        leftServo.setPosition(LEFT_SERVO_CLOSE);
    }

    @Override
    public void periodic() { }
}
