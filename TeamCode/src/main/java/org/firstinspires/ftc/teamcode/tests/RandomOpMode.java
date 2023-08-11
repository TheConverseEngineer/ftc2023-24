package org.firstinspires.ftc.teamcode.tests;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.blacksmithcore.Trajectory;
import org.firstinspires.ftc.teamcode.thundercore.command.Subsystem;
import org.firstinspires.ftc.teamcode.thundercore.command.ThunderOpMode;
import org.firstinspires.ftc.teamcode.thundercore.controllers.Controller;
import org.firstinspires.ftc.teamcode.thundercore.controllers.PIDController;

@TeleOp
@Config
public class RandomOpMode extends ThunderOpMode {
    Arm arm;

    Trajectory trajectory;

    @Override
    public void initializeHardware() {
        DcMotorEx motor = thunderMap.getMotor("motor1");
        Servo servo = thunderMap.getServo("motor2");

        arm = new Arm(motor);
        scheduler.registerSubsystem(arm);

        trajectory = Trajectory.builder(0, 0, 0, 0)
                .splineTo(10, 10, 0, 0)
                .splineTo(0, 0, 0, 0)
                .build(10, 10);
    }

    @Override
    public void initializeCommands() {
        driver.circle.onPress(this::updateDrive);
        driver.cross.onPress(() -> {arm.setTarget(500);});
    }

    private void updateDrive() {

    }

    static class Arm implements Subsystem {

        Controller controller;
        static PIDController.PIDCoefficients coefficients;

        public Arm(DcMotor motor) {
            controller = Controller.getBuilder()
                    .addPIDController(coefficients)
                    .addMotor(motor)
                    .build(0);

        }

        @Override
        public void periodic() {
           controller.update(0);
        }

        public void setTarget(double newTarget) {
            controller.setNewTarget(newTarget);
        }
    }
}
