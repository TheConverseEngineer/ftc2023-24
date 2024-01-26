package org.firstinspires.ftc.teamcode.common.command.prefabs;

import org.firstinspires.ftc.teamcode.common.command.Command;
import org.firstinspires.ftc.teamcode.common.command.CommandScheduler;

/** A pre-built command that will run multiple commands in the order they are given.
 * <br>
 *
 * <p>
 *     Implementation note: Commands that end instantly (ie: {@link InstantCommand}) will be ran
 *     and terminated simultaneously. Therefore, a command that follows an InstantCommand will
 *     be scheduled on the same loop iteration as the InstantCommand. Note that the InstantCommand
 *     will still run before the command it precedes.
 * </p>
 *
 * */
public class SequentialCommand implements Command {
    private final Command[] commands;
    private int current;

    public SequentialCommand(Command... commands) {
        this.commands = commands;
        assert commands.length >= 1; // Please don't give empty command groups
        current = 0;
    }


    @Override
    public void init() {
        commands[0].init();
    }

    @Override
    public void loop() {
        if (current >= commands.length) return;
        if (commands[current].isComplete()) {
            do { // This way we can go through multiple InstantCommands
                commands[current].end();
                current++;
                if (current >= commands.length) return;
                commands[current].init();
            } while (commands[current].isComplete());
        } else commands[current].loop();
    }

    @Override
    public void end() {
        if (current < commands.length) commands[current].end();
    }

    @Override
    public boolean isComplete() {
        return current >= commands.length;
    }

    /** Convenience methods which allows one to schedule a command inside a SequentialCommand,
     * but proceed to the next command without waiting for it to finish*/
    public static Command runAsync(Command command) {
        return new InstantCommand(() -> CommandScheduler.getInstance().scheduleCommand(command));
    }
}