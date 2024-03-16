package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.common.command.CommandOpMode;

@TeleOp
public class ServoTuner extends CommandOpMode {

    Servo drone;
    double position = 0.5;

    @Override
    public void initialize() {
        drone = hardwareMap.get(Servo.class, "drone");
        driver.add("a", driver.new DPadUpToggleButton() {
            @Override
            public void onPress(boolean value) {
                position += 0.01;
            }
        });

        driver.add("b", driver.new DPadDownToggleButton() {
            @Override
            public void onPress(boolean value) {
                position -= 0.01;
            }
        });
    }

    @Override
    public void run() {
        telemetry.addData("pos", position);
        drone.setPosition(position);
    }
}
