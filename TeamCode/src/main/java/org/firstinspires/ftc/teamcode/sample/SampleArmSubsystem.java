package org.firstinspires.ftc.teamcode.sample;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.general.blacksmithcore.controllers.PIDCoefficients;
import org.firstinspires.ftc.teamcode.general.blacksmithcore.controllers.PIDController;
import org.firstinspires.ftc.teamcode.general.thundercore.actions.Subsystem;

@Config // Using the @Config annotation allows us to tune the arm in real time
public class SampleArmSubsystem extends Subsystem {

    // Only static variables can be edited in real-time
    public static PIDCoefficients coefficients = new PIDCoefficients(0, 0, 0);

    // Create the PIDController and the arm motor
    private final PIDController controller = new PIDController(coefficients);
    private final DcMotor armMotor;

    // Store the arm motor
    public SampleArmSubsystem(DcMotor armMotor) {
        this.armMotor = armMotor;
    }

    // Method that allows one to change the target position
    public void setTargetPosition(double newTargetPos) {
        controller.setTargetPos(newTargetPos);
    }

    // Runs every loop iteration (updates the PID controller).
    @Override
    public void earlyPeriodic() {
        armMotor.setPower(
                controller.update(armMotor.getCurrentPosition())
        );
    }
}
