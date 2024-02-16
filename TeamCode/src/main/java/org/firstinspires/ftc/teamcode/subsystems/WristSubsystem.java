package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.ServoImplEx;

import org.firstinspires.ftc.teamcode.common.command.Subsystem;
import org.firstinspires.ftc.teamcode.common.utils.ProfiledServo;

@Config
public class WristSubsystem implements Subsystem {
    public static double LOWER_WRIST_INTAKE = 0.365;
    public static double LOWER_WRIST_OUTTAKE = 0.17;

    public static double UPPER_WRIST_INTAKE = 0.225;
    public static double UPPER_WRIST_OUTTAKE = 0.57;

    private ServoImplEx lowerWrist;
    private ProfiledServo upperWrist;

    public WristSubsystem(HardwareMap hardwareMap) {
        lowerWrist = hardwareMap.get(ServoImplEx.class, "lowerWrist");
        upperWrist = new ProfiledServo(hardwareMap, "upperWrist", .71);
        lowerWrist.setPosition(.67);
    }

    public void intakePosition() {
        lowerWrist.setPosition(LOWER_WRIST_INTAKE);
        upperWrist.setPosition(UPPER_WRIST_INTAKE);
    }


    public void outtakePosition() {
        lowerWrist.setPosition(LOWER_WRIST_OUTTAKE);
        upperWrist.setPosition(UPPER_WRIST_OUTTAKE);
    }

    public void transferPosition() {
        upperWrist.setPosition(UPPER_WRIST_OUTTAKE);
        lowerWrist.setPosition(0.3);
    }

    @Override
    public void earlyPeriodic() {
        upperWrist.earlyPeriodic();
    }

    @Override
    public void periodic() {
        upperWrist.periodic();
    }
}
