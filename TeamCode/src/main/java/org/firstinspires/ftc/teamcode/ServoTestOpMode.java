package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class ServoTestOpMode extends OpMode {
    CRServo leftServo, rightServo;


    @Override
    public void init() {
        leftServo = hardwareMap.get(CRServo.class, "leftClimb");
        rightServo = hardwareMap.get(CRServo.class, "rightClimb");
    }

    @Override
    public void loop() {
        leftServo.setPower(gamepad1.left_stick_x);
        rightServo.setPower(gamepad1.right_stick_x);
    }
}
