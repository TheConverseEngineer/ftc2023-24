package org.firstinspires.ftc.teamcode.common.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/** Singleton class responsible for managing the execution of commands */
public final class CommandScheduler {

    private static final CommandScheduler instance = new CommandScheduler();

    private final Stack<Command> queuedCommands = new Stack<>();
    private final List<Command> runningCommands = new ArrayList<>();
    private final List<Subsystem> subsystems = new ArrayList<>();

    /** Resets the command scheduler. It is recommended to run this before/after every opmode. */
    public void reset() {
        runningCommands.clear();
        queuedCommands.clear();
        subsystems.clear();
    }

    /** Schedules commands to run on the next loop iteration */
    public void scheduleCommand(Command... cmds) {
        for (Command i : cmds) queuedCommands.push(i);
    }

    /** Adds subsystems to the list of subsystems.
     * Subsystems which are not registered will not have their periodic methods ran. */
    public void registerSubsystem(Subsystem... sys) {
        subsystems.addAll(Arrays.asList(sys));
    }

    /** Updates the CommandScheduler. This method must be called every loop iteration */
    public void run() {
        for (Subsystem i : subsystems) i.earlyPeriodic();

        for (int i = runningCommands.size() - 1; i >= 0; i--) {
            if (runningCommands.get(i).isComplete()) {
                runningCommands.remove(i).end();
            } else runningCommands.get(i).loop();
        }

        while (!queuedCommands.empty()) {
            queuedCommands.peek().init();
            runningCommands.add(queuedCommands.pop());
        }

        for (Subsystem subsystem : subsystems) subsystem.periodic();

    }

    public static CommandScheduler getInstance() {
        return instance;
    }

    private CommandScheduler() { }
}
