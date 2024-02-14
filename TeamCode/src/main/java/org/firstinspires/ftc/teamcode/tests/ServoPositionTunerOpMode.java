package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.ServoImplEx;

@TeleOp
public class ServoPositionTunerOpMode extends LinearOpMode {

    ServoImplEx servo;

    @Override
    public void runOpMode() throws InterruptedException {
        servo = hardwareMap.get(ServoImplEx.class, "servo");


        while (!gamepad1.a && !gamepad1.b) {

        }

        if (gamepad1.a) {
            telemetry.addLine("full range");
            telemetry.update();
            servo.setPwmRange(new PwmControl.PwmRange(500, 2500));
        } else {
            telemetry.addLine("partial range");
            telemetry.update();
            servo.setPwmRange(new PwmControl.PwmRange(610, 2360));
        }

        waitForStart();

        int target = 50;
        boolean lastR = false, lastL = false;
        while (opModeIsActive() && !isStopRequested()) {
            if (gamepad1.left_bumper) {
                if (!lastL) {
                    lastL = true;
                    target--;
                }
            } else lastL = false;

            if (gamepad1.right_bumper) {
                if (!lastR) {
                    lastR = true;
                    target++;
                }
            } else lastR = false;

            if (gamepad1.y) servo.setPosition(target/100.0);
            telemetry.addData("pos", target);
            telemetry.update();
        }
    }
}
