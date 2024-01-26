package org.firstinspires.ftc.teamcode.common.command.prefabs;

import org.firstinspires.ftc.teamcode.common.command.Command;

import java.util.ArrayList;
import java.util.Arrays;

/** A pre-built class that will run all commands simultaneously and will end once all commands have ended. */
public class ParallelCommand implements Command {

    private final ArrayList<Command> commands;

    public ParallelCommand(Command... commands) {
        this.commands = new ArrayList<>(Arrays.asList(commands));
    }

    @Override
    public void init() {
        for (Command i : commands) i.init();
    }

    @Override
    public void loop() {
        for (int i = commands.size()-1; i >= 0; i--) {
            if (commands.get(i).isComplete()) commands.remove(i).end();
            else commands.get(i).loop();
        }
    }

    @Override
    public void end() {
        for (Command i : commands) i.end();
    }

    @Override
    public boolean isComplete() {
        return commands.size() == 0;
    }
}