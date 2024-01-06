package org.firstinspires.ftc.teamcode.subsystems;


import com.arcrobotics.ftclib.command.SubsystemBase;
import com.qualcomm.robotcore.hardware.Servo;

public class GripperSubsystem extends SubsystemBase {

    private final Servo clawServo;

    private static final double CLAW_CLOSE_POS = 0;
    private static final double CLAW_OPEN_POS = 1;

    public GripperSubsystem(Servo clawServo) {
        this.clawServo = clawServo;
    }

    public void openClaw() {
        this.clawServo.setPosition(CLAW_OPEN_POS);
    }

    public void closeClaw() {
        this.clawServo.setPosition(CLAW_CLOSE_POS);
    }
}
