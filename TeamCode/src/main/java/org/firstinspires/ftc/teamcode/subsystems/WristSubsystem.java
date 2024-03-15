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
        lowerWrist.setPosition(.5);
    }

    public void intakePosition() {
        lowerWrist.setPosition(0.5);
        upperWrist.setPosition(0.245);
    }

    public void spikeDropPosition() {
        upperWrist.setPosition(.26);
        lowerWrist.setPosition(.41);
    }

    public void stackTopIntakePos() {
        upperWrist.setPosition(0.28);
        lowerWrist.setPosition(0.43);
    }

    public void idlePosition() {
        lowerWrist.setPosition(0.5);
        upperWrist.setPosition(0.72);
    }

    public void outtakePosition() {
        lowerWrist.setPosition(0.5);
        upperWrist.setPosition(0.73);
    }

    public void transferPosition() {
        lowerWrist.setPosition(0.5);
        upperWrist.setPosition(0.72);
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
