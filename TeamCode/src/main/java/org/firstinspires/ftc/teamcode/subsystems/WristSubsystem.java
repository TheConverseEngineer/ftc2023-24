package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.ServoImplEx;

@Config
public class WristSubsystem {
    public static double LOWER_WRIST_INTAKE = 0.17;
    public static double LOWER_WRIST_OUTTAKE = 0.01;

    private static double UPPER_WRIST_INTAKE = 0.81;
    private static double UPPER_WRIST_OUTTAKE = 0.30;

    private ServoImplEx upperWrist, lowerWrist;

    public WristSubsystem(HardwareMap hardwareMap) {
        lowerWrist = hardwareMap.get(ServoImplEx.class, "lowerWrist");
        upperWrist = hardwareMap.get(ServoImplEx.class, "upperWrist");
    }

    public void goToIntakePosition() {
        lowerWrist.setPosition(LOWER_WRIST_INTAKE);
        upperWrist.setPosition(UPPER_WRIST_INTAKE);
    }


    public void goToOuttakePosition() {
        lowerWrist.setPosition(LOWER_WRIST_OUTTAKE);
        upperWrist.setPosition(UPPER_WRIST_OUTTAKE);
    }
}
