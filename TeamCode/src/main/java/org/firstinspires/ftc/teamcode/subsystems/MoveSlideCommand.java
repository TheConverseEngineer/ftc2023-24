package org.firstinspires.ftc.teamcode.subsystems;

import com.arcrobotics.ftclib.command.CommandBase;
import com.arcrobotics.ftclib.trajectory.TrapezoidProfile;
import com.qualcomm.robotcore.util.ElapsedTime;

public class MoveSlideCommand extends CommandBase {

    public static final double marginOfError = 25;

    private final ActuatorSubsystem slide;
    private final double target;

    private TrapezoidProfile profile;
    private final ElapsedTime timer = new ElapsedTime();

    public MoveSlideCommand(ActuatorSubsystem slide, double target) {
        this.slide = slide;
        this.target = target;

        addRequirements(slide);
    }

    @Override
    public void initialize() {
        if (slide.getCurrentState() != ActuatorSubsystem.ActuatorState.SLIDE_MODE) return;

        profile = new TrapezoidProfile(
                ActuatorSubsystem.slideConstraints,
                new TrapezoidProfile.State(target, 0),
                new TrapezoidProfile.State(slide.getSlidePos(), 0)
        );

        slide.setSlideProfile(this::getCurrentState);

        timer.reset();
    }

    private TrapezoidProfile.State getCurrentState() {
        return profile.calculate(timer.time());
    }

    @Override
    public boolean isFinished() {
        return slide.getCurrentState() != ActuatorSubsystem.ActuatorState.SLIDE_MODE ||
                    Math.abs(slide.getSlidePos() - target) < marginOfError;
    }
}
