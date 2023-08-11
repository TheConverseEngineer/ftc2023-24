package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.blacksmithcore.Trajectory;
import org.firstinspires.ftc.teamcode.thundercore.command.Subsystem;
import org.firstinspires.ftc.teamcode.thundercore.command.ThunderOpMode;
import org.firstinspires.ftc.teamcode.thundercore.controllers.Controller;
import org.firstinspires.ftc.teamcode.thundercore.controllers.PIDController;

@TeleOp
public class SampleOpMode extends ThunderOpMode {

    Arm arm;

    @Override
    public void initializeHardware() {
        enableVirtualHardware();
        DcMotor motor1 = thunderMap.getMotor("motor1");
        arm = new Arm(motor1);
        scheduler.registerSubsystem(arm);

        Trajectory trajectory = Trajectory.builder(0, 0, 0, 0)
                .splineTo(0, 10, 0, 0)
                .splineTo(10, 10, 0, 0)
                .build(30, 5);
    }

    @Override
    public void initializeCommands() {
        driver.dpadUp.onPress(() -> arm.setPosition(500));
        driver.dpadDown.onPress(() -> arm.setPosition(100));
    }

    static class Arm implements Subsystem {

        DcMotor motor;
        Controller armController;
        PIDController.PIDCoefficients coefficients = new PIDController.PIDCoefficients(0, 0, 0);

        public Arm(DcMotor motor) {
            this.motor = motor;
            motor.setPower(1);
            motor.getCurrentPosition();

            armController = Controller.getBuilder()
                    .addPIDController(coefficients)
                    .addPIDController(coefficients)
                    .addMotor(motor)
                    .addMotor(motor)
                    .addPIDController(coefficients)
                    .build(0);
        }

        @Override
        public void periodic() {
            armController.update(motor.getCurrentPosition());
        }

        public void setPosition(double pos) {
            armController.setNewTarget(pos);
        }
    }
}
