package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.ServoImplEx;

import org.firstinspires.ftc.teamcode.common.command.Subsystem;
import org.firstinspires.ftc.teamcode.common.utils.ProfiledServo;

@Config
public class WristSubsystem implements Subsystem {
    public double LOWER_WRIST_INTAKE = 0.365;
    public double LOWER_WRIST_OUTTAKE = 0.17;

    public double UPPER_WRIST_INTAKE = 0.225;
    public double UPPER_WRIST_OUTTAKE = 0.57;

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

    public void spikeDropPosition() {
        upperWrist.setPosition(.26);
        lowerWrist.setPosition(.41);
    }

    public void stackTopIntakePos() {
        upperWrist.setPosition(0.28);
        lowerWrist.setPosition(0.43);
    }

    public void stackBottomIntakePos() {
        upperWrist.setPosition(0.25);
        lowerWrist.setPosition(0.40);
    }

    public void idlePosition() {
        lowerWrist.setPosition(LOWER_WRIST_OUTTAKE);
        upperWrist.setPosition(UPPER_WRIST_OUTTAKE);
    }

    public void outtakePosition() {
        lowerWrist.setPosition(.20);
        upperWrist.setPosition(.59);
    }

    public void transferPosition() {
        upperWrist.setPosition(UPPER_WRIST_OUTTAKE);
        lowerWrist.setPosition(0.3);
    }

    public void visionOuttakePosition() {
        upperWrist.setPosition(.65);
        lowerWrist.setPosition(.31);
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
