package org.firstinspires.ftc.teamcode.common.utils;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.ServoImplEx;

import org.firstinspires.ftc.teamcode.common.command.Subsystem;

public class ProfiledServo implements Subsystem {

    private final ServoImplEx servo;
    private final ProfiledSystemController controller;
    public double lastServoPos;

    public ProfiledServo(HardwareMap hardwareMap, String name, double startPosition) {
        this.servo = hardwareMap.get(ServoImplEx.class, name);
        this.servo.setPwmRange(new PwmControl.PwmRange(500, 2500));

        this.controller = new ProfiledSystemController(startPosition, new ProfiledSystemController.ProfileConstants(
                .8, 3, 0, 0
        ));

        this.lastServoPos = startPosition;
        servo.setPosition(startPosition);
    }

    public void setPosition(double newTarget) {
        this.controller.setNewTarget(newTarget);
    }

    @Override
    public void periodic() {
        double newTarget = this.controller.getTargetPosition();
        if (Math.abs(newTarget - this.lastServoPos) > 0.0000001) {
            this.servo.setPosition(newTarget);
            this.lastServoPos = newTarget;
        }
    }
}
