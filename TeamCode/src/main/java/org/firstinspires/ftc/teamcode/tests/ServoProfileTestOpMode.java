package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.common.command.CommandOpMode;
import org.firstinspires.ftc.teamcode.common.utils.ProfiledServo;

@TeleOp
public class ServoProfileTestOpMode extends CommandOpMode {

    ProfiledServo upperWrist;

    @Override
    public void initialize() {
        upperWrist = new ProfiledServo(hardwareMap, "upperWrist", 0.6);

        driver.add("up", driver.new DPadUpToggleButton() {
            @Override
            public void onPress(boolean value) {
                upperWrist.setPosition(0.6);
            }
        });

        driver.add("down", driver.new DPadDownToggleButton() {
            @Override
            public void onPress(boolean value) {
                upperWrist.setPosition(0.225);
            }
        });

        scheduler.registerSubsystem(upperWrist);
    }

    @Override
    public void run() {
        telemetry.addData("target", upperWrist.lastServoPos);
    }
}
