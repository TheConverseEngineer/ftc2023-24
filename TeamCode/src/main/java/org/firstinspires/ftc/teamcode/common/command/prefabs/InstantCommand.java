package org.firstinspires.ftc.teamcode.common.command.prefabs;

import org.firstinspires.ftc.teamcode.common.command.Command;

/** A pre-built command that will run the inputted runnable once when scheduled and will then terminate. */
public class InstantCommand implements Command {
    private final Runnable command;

    public InstantCommand(Runnable command) {
        this.command = command;
    }

    @Override
    public void init() {
        command.run();
    }

    @Override
    public void loop() {
    }

    @Override
    public void end() {
    }

    @Override
    public boolean isComplete() {
        return true;
    }
}