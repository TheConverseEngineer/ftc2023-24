package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.ServoImplEx;

import org.firstinspires.ftc.teamcode.common.utils.ProfiledServo;

@Config @TeleOp @Disabled
public class ClawTuner extends OpMode {

    public static double L_V = 0.5, U_V = 0.5;
    ServoImplEx lowerClaw;
    ProfiledServo upperClaw;

    @Override
    public void init() {
        lowerClaw = hardwareMap.get(ServoImplEx.class, "lowerWrist");
        upperClaw = new ProfiledServo(hardwareMap, "upperWrist", .71);
    }

    @Override
    public void loop() {
        lowerClaw.setPosition(L_V);
        upperClaw.setPosition(U_V);
        upperClaw.periodic();
    }
}

