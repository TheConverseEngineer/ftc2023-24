package org.firstinspires.ftc.teamcode.thundercore.command;

import java.util.ArrayList;
import java.util.Collections;

public abstract class Command {
    private final ArrayList<Subsystem> requirements = new ArrayList<>();
    private final ArrayList<Object> data = new ArrayList<>();
    private boolean cancellable = true;

    /** The command scheduler will run this function once when this command is scheduled */
    public abstract void init();

    /** The command scheduler will repeatedly run this function while this command is active */
    public abstract void loop();

    /** The command scheduler will run this function once when the command ends
     *
     * @param cancelled     If true, then this command was cancelled prematurely.
     */
    public abstract void end(boolean cancelled);

    /** If this method returns true, the command scheduler will terminate this command */
    public abstract boolean isComplete();

    /* Everything beyond this point is a final method */

    /** Adds a subsystem to the list of required (blocking) subsystems
     * Only one command can used a blocking subsystem at a time.
     *
     * Note: Please do not repeatedly add the same subsystem.
     *
     * @param subsystems    The subsystem(s) to add
     */
    public final void addRequirement(Subsystem... subsystems) {
        Collections.addAll(requirements, subsystems);
    }

    /** Sets whether or not this command can be interrupted by another command
     * By default, a command can be interrupted
     *
     * @param cancellable   if true, another command will be able to interrupt this command
     */
    public final void setCancellable(boolean cancellable) {
        this.cancellable = cancellable;
    }

    /** Returns a list of the required subsystems for this command
     *
     * This command should only really be called by the CommandScheduler
     */
    public final Subsystem[] getRequirements() {
        return requirements.toArray(new Subsystem[0]);
    }

    /** Returns true if this command can be cancelled */
    public final boolean isCancellable() {
        return cancellable;
    }

    /** Adds something to this command's (local memory) */
    public final void addToMemory(Object obj) {
        this.data.add(obj);
    }

    /** Returns the number of objects stored in this command's (local) memory */
    public final int getMemorySize() {
        return this.data.size();
    }

    /** Returns an object stored in this class's memory at the defined position,
     * or null if the given index is out of bounds
     */
    public final Object getFromMemory(int index) {
        if (index < 0 || index > getMemorySize()) return null;
        return this.data.get(index);
    }
}
