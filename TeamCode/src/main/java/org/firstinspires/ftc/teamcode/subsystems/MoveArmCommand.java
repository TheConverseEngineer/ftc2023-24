package org.firstinspires.ftc.teamcode.subsystems;

import com.arcrobotics.ftclib.command.CommandBase;
import com.arcrobotics.ftclib.trajectory.TrapezoidProfile;
import com.qualcomm.robotcore.util.ElapsedTime;

public class MoveArmCommand extends CommandBase {

    public static final double marginOfError = 25;

    private final ActuatorSubsystem arm;
    private final double target;

    private TrapezoidProfile profile;
    private final ElapsedTime timer = new ElapsedTime();

    public MoveArmCommand(ActuatorSubsystem arm, double target) {
        this.arm = arm;
        this.target = target;

        addRequirements(arm);
    }

    @Override
    public void initialize() {
        if (arm.getCurrentState() != ActuatorSubsystem.ActuatorState.ARM_MODE) return;

        profile = new TrapezoidProfile(
                ActuatorSubsystem.armConstraints,
                new TrapezoidProfile.State(target, 0),
                new TrapezoidProfile.State(arm.getArmPos(), 0)
        );

        arm.setArmProfile(this::getCurrentState);

        timer.reset();
    }

    private TrapezoidProfile.State getCurrentState() {
        return profile.calculate(timer.time());
    }

    @Override
    public boolean isFinished() {
        return arm.getCurrentState() != ActuatorSubsystem.ActuatorState.ARM_MODE ||
                Math.abs(arm.getArmPos() - target) < marginOfError;
    }
}
