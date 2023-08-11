package org.firstinspires.ftc.teamcode.thundercore.command.templates;

import org.firstinspires.ftc.teamcode.thundercore.command.Command;
import org.firstinspires.ftc.teamcode.thundercore.command.Subsystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/** Template command that runs a group of commands concurrently.
 * Commands are terminated as they finish, and cancelling this command will cause
 * all unfinished commands to be ended
 *
 * Two commands cannot share a requirement
 * The required subsystems for this command are the combined required subsystems of all the commands
 * that make up this command.
 *
 * If at least one command that makes up this command cannot be cancelled, then this command cannot be cancelled either.
 */
public class ParallelCommand extends Command {
    private final ArrayList<Command> cmds;

    public ParallelCommand(Command... cmds) {
        this.cmds = new ArrayList<>(Arrays.asList(cmds));

        // Now make sure that two commands don't use the same subsystem
        HashSet<Subsystem> reqs = new HashSet<>();
        for (Command i : cmds) {
            for (Subsystem j : i.getRequirements()) {
                if (reqs.contains(j)) throw new RuntimeException("Two commands in a parallel command both required " + j.getClass().getSimpleName());
                reqs.add(j);
            }
            if (!i.isCancellable()) this.setCancellable(false);
        }
        for (Subsystem i : reqs) this.addRequirement(i);
    }

    @Override
    public void init() {
        for (Command i : cmds) i.init();
    }

    @Override
    public void loop() {
        for (int i = cmds.size()-1; i >= 0; i--) {
            if (cmds.get(i).isComplete()) cmds.remove(i).end(false);
            else cmds.get(i).loop();
        }
    }

    @Override
    public void end(boolean cancelled) {
        // If a command is still in this stack, than it is being cancelled
        // (because otherwise, it would have already been removed).
        for (Command i : cmds) i.end(true);
    }

    @Override
    public boolean isComplete() {
        return cmds.size() == 0;
    }
}
