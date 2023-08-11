package org.firstinspires.ftc.teamcode.thundercore.command.templates;

import org.firstinspires.ftc.teamcode.thundercore.command.Command;
import org.firstinspires.ftc.teamcode.thundercore.command.Subsystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

/** Template command that runs a list of commands one after the other
 *
 * The required subsystems for this command are the combined required subsystems of all the commands
 * that make up this command.
 *
 * If at least one command that makes up this command cannot be cancelled, then this command cannot be cancelled either.
 */
public class SequentialCommand extends Command {
    private final ArrayList<Command> cmds;
    private int currentCommand = 0;

    public SequentialCommand(Command... cmds) {
        this.cmds = new ArrayList<>(Arrays.asList(cmds));

        // Add all requirements
        HashSet<Subsystem> reqs = new HashSet<>();
        for (Command i : cmds) {
            Collections.addAll(reqs, i.getRequirements());
            if (!i.isCancellable()) this.setCancellable(false);
        }
        for (Subsystem i : reqs) this.addRequirement(i);
    }

    @Override
    public void init() {
        if (cmds.size() > 0) cmds.get(0).init();
    }

    @Override
    public void loop() {
        if (currentCommand < cmds.size()) {
            if (cmds.get(currentCommand).isComplete()) {
                cmds.get(currentCommand).end(false);
                currentCommand++;
                if (currentCommand < cmds.size()) cmds.get(currentCommand).init();
            } else cmds.get(currentCommand).loop();
        }
    }

    @Override
    public void end(boolean cancelled) {
        if (currentCommand < cmds.size()) cmds.get(currentCommand).end(true);
    }

    @Override
    public boolean isComplete() {
        return currentCommand >= cmds.size();
    }
}
