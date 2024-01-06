package org.firstinspires.ftc.teamcode.trajectory.command;

import com.arcrobotics.ftclib.command.CommandBase;

import java.util.function.DoubleSupplier;

/** Drives the robot using the gamepad */
public class GamepadDriveCommand extends CommandBase {

    private final MecanumDriveSubsystem drivetrain;

    private final DoubleSupplier forwardInput, strafeInput, turnInput;

    public GamepadDriveCommand(MecanumDriveSubsystem drivetrain, DoubleSupplier forwardInput, DoubleSupplier strafeInput, DoubleSupplier turnInput) {
        this.drivetrain = drivetrain;
        this.forwardInput = forwardInput;
        this.strafeInput = strafeInput;
        this.turnInput = turnInput;

        addRequirements(drivetrain);
    }

    @Override
    public void execute() {
        drivetrain.driveWithGamepad(
                strafeInput.getAsDouble(),
                forwardInput.getAsDouble(),
                turnInput.getAsDouble()
        );
    }
}
