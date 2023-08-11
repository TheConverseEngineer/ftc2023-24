package org.firstinspires.ftc.teamcode.thundercore.command.templates;

import org.firstinspires.ftc.teamcode.thundercore.command.Command;
import org.firstinspires.ftc.teamcode.thundercore.command.Subsystem;

import java.util.HashSet;

/** Similar to the race command, but, instead of ending the command when all commands are complete,
 * the race command terminates as soon as any one command is complete.
 *
 * Any other commands are then terminated.
 */
public class ParallelRaceCommand extends Command {

    private final Command[] cmds;
    boolean terminated = false;

    public ParallelRaceCommand(Command... cmds) {
        this.cmds = cmds;

        // Now make sure that two commands don't use the same subsystem
        HashSet<Subsystem> reqs = new HashSet<>();
        for (Command i : cmds) {
            for (Subsystem j : i.getRequirements()) {
                if (reqs.contains(j)) throw new RuntimeException("Two commands in a parallel race command both required " + j.getClass().getSimpleName());
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
        int tInd = -1;
        if (terminated) return;
        for (int i = 0; i < cmds.length; i++) {
            if (cmds[i].isComplete()) {
                terminated = true;
                tInd = i;
                break;
            } else cmds[i].loop();
        }
        if (terminated) {
            for (int i = 0; i < cmds.length; i++) cmds[i].end(i != tInd);
        }
    }

    @Override
    public void end(boolean cancelled) {
        if (!terminated) {
            for (Command i : cmds) i.end(true);
        }
    }

    @Override
    public boolean isComplete() {
        return terminated;
    }
}
